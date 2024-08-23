package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.Dispensers;
import net.apartium.cocoabeans.commands.ArgumentMapper;
import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.RegisteredCommandVariant;
import net.apartium.cocoabeans.commands.Sender;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.*;

@ApiStatus.AvailableSince("0.0.30")
public class  CompoundParser<T> extends ArgumentParser<T> {

    private final CompoundParserBranchProcessor<T> compoundParserBranchProcessor;
    private final Class<?> self;

    /**
     * Constructs a
     *
     * @param keyword
     * @param clazz
     * @param priority
     */
    protected CompoundParser(Class<? extends CompoundParser<T>> self, String keyword, Class<T> clazz, int priority) {
        super(keyword, clazz, priority);

        this.self = self;
        this.compoundParserBranchProcessor = new CompoundParserBranchProcessor<>();

        createBranch();
    }

    private void createBranch() {
        for (Method method : self.getMethods()) {
            ParserVariant[] parserVariants = method.getAnnotationsByType(ParserVariant.class);
            if (parserVariants == null || parserVariants.length == 0)
                continue;

            // TODO Continue here
        }
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
