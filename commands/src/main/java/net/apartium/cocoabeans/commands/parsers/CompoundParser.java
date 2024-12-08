package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.Dispensers;
import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.exception.UnknownTokenException;
import net.apartium.cocoabeans.commands.lexer.ArgumentParserToken;
import net.apartium.cocoabeans.commands.lexer.CommandLexer;
import net.apartium.cocoabeans.commands.lexer.CommandToken;
import net.apartium.cocoabeans.commands.lexer.KeywordToken;
import net.apartium.cocoabeans.commands.requirements.*;
import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;

import static net.apartium.cocoabeans.commands.RegisteredVariant.REGISTERED_VARIANT_COMPARATOR;

@ApiStatus.AvailableSince("0.0.37")
public class CompoundParser<T> extends ArgumentParser<T> implements GenericNode {

    private final Map<Class<? extends RequirementFactory>, RequirementFactory> requirementFactories = new HashMap<>();
    private final Map<Class<? extends ParserFactory>, ParserFactory> parserFactories = new HashMap<>();
    private final Map<Class<? extends ArgumentRequirementFactory>, ArgumentRequirementFactory> argumentRequirementFactories = new HashMap<>();

    private final CompoundParserBranchProcessor<T> compoundParserBranchProcessor;

    private final ArgumentMapper argumentMapper;
    private final CommandLexer commandLexer;


    /**
     * Constructs a new parser
     * 
     * @param keyword keyword of the parser
     * @param clazz output class
     * @param priority priority
     */
    protected CompoundParser(String keyword, Class<T> clazz, int priority, ArgumentMapper argumentMapper, CommandLexer commandLexer) {
        super(keyword, clazz, priority);

        this.argumentMapper = argumentMapper;

        this.commandLexer = commandLexer;
        this.compoundParserBranchProcessor = new CompoundParserBranchProcessor<>();

        try {
            createBranch();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to create branch", e);
        }

        // clear cache
        this.requirementFactories.clear();
        this.parserFactories.clear();
        this.argumentRequirementFactories.clear();
    }

    private void createBranch() throws IllegalAccessException {
        RequirementSet requirementsResult = RequirementFactory.createRequirementSet(this, this.getClass().getAnnotations(), requirementFactories);
        Map<String, ArgumentParser<?>> argumentParser = new HashMap<>();

        MethodHandles.Lookup lookup = MethodHandles.publicLookup();

        CollectionHelpers.mergeInto(
                argumentParser,
                ParserFactory.findClassParsers(this, this.getClass(), parserFactories)
        );

        for (Method method : this.getClass().getMethods()) {
            ParserVariant[] parserVariants = method.getAnnotationsByType(ParserVariant.class);
            if (parserVariants.length == 0)
                continue;

            RequirementSet methodRequirements = new RequirementSet(
                    RequirementFactory.createRequirementSet(this, method.getAnnotations(), requirementFactories),
                    requirementsResult
            );

            Map<String, ArgumentParser<?>> methodArgumentTypeHandlerMap = new HashMap<>(argumentParser);
            methodArgumentTypeHandlerMap.putAll(ParserFactory.getArgumentParsers(this, method.getAnnotations(), method, false, parserFactories));

            RegisteredVariant.Parameter[] parameters = RegisteredVariant.Parameter.of(this, method.getParameters(), argumentRequirementFactories);

            for (ParserVariant parserVariant : parserVariants) {
                handleParserVariants(lookup.unreflect(method), parserVariant, methodRequirements, methodArgumentTypeHandlerMap, parameters, new ArrayList<>(), new ArrayList<>(methodRequirements));
            }
        }
    }

    private void handleParserVariants(MethodHandle method, ParserVariant parserVariant, RequirementSet requirementSet, Map<String, ArgumentParser<?>> argumentParserMap, RegisteredVariant.Parameter[] parameters, List<RegisterArgumentParser<?>> parsersResult, List<Requirement> requirementsResult) {
        List<CommandToken> tokens = commandLexer.tokenize(parserVariant.value());

        if (tokens.isEmpty())
            throw new IllegalArgumentException("Parser variant cannot be empty");

        CompoundParserOption<T> currentOption = findOrCreateOption(this.compoundParserBranchProcessor, requirementSet, new ArrayList<>());


        for (int i = 0; i < tokens.size(); i++) {
            CommandToken token = tokens.get(i);

            RequirementSet requirements = i == 0 ? requirementSet : new RequirementSet();


            if (token instanceof KeywordToken)
                throw new UnsupportedOperationException("Keyword tokens are not supported");


            if (token instanceof ArgumentParserToken argumentParserToken) {
                currentOption = createArgumentOption(currentOption, argumentParserToken, argumentParserMap, requirements, parsersResult, requirementsResult);
                continue;
            }

            throw new UnknownTokenException(token);
        }


        CollectionHelpers.addElementSorted(
                currentOption.getRegisteredVariants(),
                new RegisteredVariant(
                        method,
                        parameters,
                        this,
                        argumentMapper.mapIndices(parameters, parsersResult, requirementsResult),
                        parserVariant.priority()
                ),
                REGISTERED_VARIANT_COMPARATOR
        );
    }

    private CompoundParserOption<T> createArgumentOption(CompoundParserOption<T> option, ArgumentParserToken argumentParserToken, Map<String, ArgumentParser<?>> argumentTypeHandlerMap, RequirementSet requirements, List<RegisterArgumentParser<?>> parsersResult, List<Requirement> requirementsResult) {
        RegisterArgumentParser<?> parser = argumentParserToken.getParser(argumentTypeHandlerMap);
        if (parser == null)
            throw new IllegalArgumentException("Parser not found: " + argumentParserToken.getParserName());

        Entry<RegisterArgumentParser<?>, CompoundParserBranchProcessor<T>> entryArgument = option.argumentTypeHandlerMap.stream()
                .filter(entry -> entry.key().equals(parser))
                .findAny()
                .orElse(null);

        CompoundParserBranchProcessor<T> branchProcessor = entryArgument == null
                ? null
                : entryArgument.value();

        parsersResult.add(entryArgument == null ? parser : entryArgument.key());

        if (branchProcessor == null) {
            branchProcessor = new CompoundParserBranchProcessor<>();

            CollectionHelpers.addElementSorted(
                    option.argumentTypeHandlerMap,
                    new Entry<>(
                            parser,
                            branchProcessor
                    ),
                    (a, b) -> b.key().compareTo(a.key())
            );

        }

        return findOrCreateOption(branchProcessor, requirements, requirementsResult);
    }

    @ApiStatus.Internal
    private CompoundParserOption<T> findOrCreateOption(CompoundParserBranchProcessor<T> branchProcessor, RequirementSet requirements, List<Requirement> requirementsResult) {
        for (Entry<RequirementSet, CompoundParserOption<T>> entry : branchProcessor.objectMap) {
            if (entry.key().equals(requirements))
                return entry.value();
        }

        CompoundParserOption<T> option = new CompoundParserOption<>();
        branchProcessor.objectMap.add(new Entry<>(
                requirements,
                option
        ));

        requirementsResult.addAll(requirements);

        return option;
    }

    @ApiStatus.Internal
    private List<Object> getParameters(RegisteredVariant registeredVariant, Sender sender, ArgumentContext context) {
        List<Object> parameters = new ArrayList<>(registeredVariant.argumentIndexList().stream()
                .<Object>map((argumentIndex -> argumentIndex.get(context)))
                .toList());

        parameters.add(0, registeredVariant.node());

        for (int i = 0; i < registeredVariant.parameters().length; i++) {
            Object obj = parameters.get(i + 1); // first element is class instance
            for (ArgumentRequirement argumentRequirement : registeredVariant.parameters()[i].argumentRequirements()) {
                if (!argumentRequirement.meetsRequirement(sender, null, obj))
                    return null;
            }
        }

        return parameters;
    }


    @Override
    public Optional<ParseResult<T>> parse(CommandProcessingContext processingContext) {
        Optional<ParserResult> parse = compoundParserBranchProcessor.parse(processingContext);

        if (parse.isEmpty()) {
            processingContext.report(this, new BadCommandResponse(processingContext.label(), processingContext.args().toArray(new String[0]), processingContext.index(), "No variant found"));
            return Optional.empty();
        }

        for (RegisteredVariant registeredVariant : parse.get().registeredVariant()) {
            List<Object> parameters = getParameters(
                    registeredVariant,
                    processingContext.sender(),
                    new ArgumentContext(processingContext.label(), processingContext.args().toArray(new String[0]), processingContext.sender(), parse.get().mappedByClass)
            );

            if (parameters == null)
                continue;

            T output;
            try {
                output = (T) registeredVariant.method().invokeWithArguments(parameters);
            } catch (Throwable e) {
                Dispensers.dispense(e);
                return Optional.empty(); // never going to reach this place
            }

            if (output == null)
                continue;

            return Optional.of(new ParseResult<>(
                    output,
                    parse.get().newIndex
            ));

        }
        processingContext.report(this, new BadCommandResponse(processingContext.label(), processingContext.args().toArray(new String[0]), processingContext.index(), "No variant found"));
        return Optional.empty();
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return parse(processingContext)
                .map(ParseResult::newIndex)
                .map(OptionalInt::of)
                .orElse(OptionalInt.empty());
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        return compoundParserBranchProcessor.tabCompletion(processingContext);
    }


    /* package-private */ record ParserResult (
            List<RegisteredVariant> registeredVariant,
            int newIndex,
            Map<Class<?>, List<Object>> mappedByClass
    ) {

    }

}
