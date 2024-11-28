package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.Dispensers;
import net.apartium.cocoabeans.commands.ArgumentMapper;
import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.RegisteredCommandVariant;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.exception.UnknownTokenException;
import net.apartium.cocoabeans.commands.lexer.ArgumentParserToken;
import net.apartium.cocoabeans.commands.lexer.CommandLexer;
import net.apartium.cocoabeans.commands.lexer.CommandToken;
import net.apartium.cocoabeans.commands.lexer.KeywordToken;
import net.apartium.cocoabeans.commands.requirements.RequirementSet;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.*;

@ApiStatus.AvailableSince("0.0.37")
public class  CompoundParser<T> extends ArgumentParser<T> {

    private final CompoundParserBranchProcessor<T> compoundParserBranchProcessor;
    private final Class<?> self;
    private final Map<String, ArgumentParser<?>> argumentTypeHandlerMap = new HashMap<>();

    private final ArgumentMapper argumentMapper;
    private final CommandLexer commandLexer;


    /**
     * Constructs a
     *
     * @param self
     * @param keyword
     * @param clazz
     * @param priority
     */
    protected CompoundParser(Class<? extends CompoundParser<T>> self, String keyword, Class<T> clazz, int priority, ArgumentMapper argumentMapper, CommandLexer commandLexer) {
        super(keyword, clazz, priority);

        this.self = self;
        this.argumentMapper = argumentMapper;

        this.commandLexer = commandLexer;
        this.compoundParserBranchProcessor = new CompoundParserBranchProcessor<>();

        createBranch();
    }

    private void createBranch() {
        for (Method method : self.getMethods()) {
            ParserVariant[] parserVariants = method.getAnnotationsByType(ParserVariant.class);
            if (parserVariants == null || parserVariants.length == 0)
                continue;

            // TODO Continue here
            for (ParserVariant parserVariant : parserVariants) {
                handleParserVariants(parserVariant);
            }
        }
    }

    private void handleParserVariants(ParserVariant parserVariant) {
        List<CommandToken> tokens = commandLexer.tokenize(parserVariant.value());

        if (tokens.isEmpty())
            throw new IllegalArgumentException("Parser variant cannot be empty");

        CompoundParserBranchProcessor<?> current = compoundParserBranchProcessor;
        for (int i = 0; i < tokens.size(); i++) {
            CommandToken token = tokens.get(i);

            RequirementSet requirements = i == 0 ? methodRequirements : new RequirementSet();

            if (token instanceof KeywordToken keywordToken) {
                current = createKeywordOption(currentCommandOption, context.subCommand, keywordToken, requirements, requirementsResult);
                continue;
            }

            if (token instanceof ArgumentParserToken argumentParserToken) {
                current = createArgumentOption(currentCommandOption, argumentParserToken, methodArgumentTypeHandlerMap, requirements, parsersResult, requirementsResult);
                continue;
            }

            throw new UnknownTokenException(token);
        }
    }

    private CompoundParserBranchProcessor

    protected void addParser(ArgumentParser<?> parser) {
        argumentTypeHandlerMap.put(parser.getKeyword(), parser);
    }

    @Override
    public Optional<ParseResult<T>> parse(CommandProcessingContext processingContext) {
        Optional<ParserResult> parse = compoundParserBranchProcessor.parse(processingContext);

        if (parse.isEmpty())
            return Optional.empty();

        List<Object> parameters = mapParameters(parse.get(), processingContext.sender(), processingContext);


        Object output;
        try {
            output = parse.get().methodHandle().invokeWithArguments(parameters);
        } catch (Throwable e) {
            Dispensers.dispense(e);
            return Optional.empty(); // never going to reach this place
        }

        if (output == null)
            return Optional.empty();

        return Optional.of((ParseResult<T>) new ParseResult<>(
                output,
                parse.get().newIndex
        ));
    }

    private List<Object> mapParameters(ParserResult result, Sender sender, CommandProcessingContext processingContext) {
        if (result.parameters.length == 0)
            return List.of(self.cast(this));

        List<Object> parameters = new ArrayList<>(result.parameters.length + 1);

        parameters.add(this);

        boolean senderHasBeenUsed = false;
        boolean processingContextHasBeenUsed = false;

        for (int i = 0; i < result.parameters.length; i++) {
            RegisteredCommandVariant.Parameter parameter = result.parameters[i];

            if (!senderHasBeenUsed) {
                if (parameter.type().isInstance(sender)) {
                    senderHasBeenUsed = true;
                    parameters.add(sender);
                    continue;
                }

                if (parameter.type().isInstance(sender.getSender())) {
                    senderHasBeenUsed = true;
                    parameters.add(sender.getSender());
                    continue;
                }
            }


            if (!processingContextHasBeenUsed && parameter.type().isInstance(processingContext)) {
                parameters.add(processingContext);
                processingContextHasBeenUsed = true;
                continue;
            }

            List<Object> objects = result.mappedByClass.get(parameter.type());
            if (objects == null)
                throw new RuntimeException("Shouldn't be null");

            parameters.add(objects.remove(0));
        }

        return parameters;
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return compoundParserBranchProcessor.tryParse(processingContext);
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        return compoundParserBranchProcessor.tabCompletion(processingContext);
    }


    /* package-private */ record ParserResult (
            MethodHandle methodHandle,
            RegisteredCommandVariant.Parameter[] parameters,
            int newIndex,
            Map<Class<?>, List<Object>> mappedByClass
    ) {

    }

}
