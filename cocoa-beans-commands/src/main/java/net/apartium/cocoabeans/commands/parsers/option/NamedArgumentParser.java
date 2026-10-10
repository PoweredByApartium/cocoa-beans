package net.apartium.cocoabeans.commands.parsers.option;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.IndexedCommandProcessingContext;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.option.*;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.UnionCappableParser;
import net.apartium.cocoabeans.commands.parsers.exception.InvalidNamedArgumentResponse;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.requirements.RequirementEvaluationContext;
import net.apartium.cocoabeans.commands.requirements.RequirementResult;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.AvailableSince("0.0.54")
public class NamedArgumentParser extends ArgumentParser<NamedArgumentResult> implements UnionCappableParser<NamedArgumentResult> {

    public static final String DEFAULT_KEYWORD = "named-argument";
    public static final int DEFAULT_PRIORITY = 0;

    private final Set<NamedArgument> namedArguments;
    private final Map<String, NamedArgument> namedArgumentMap = new HashMap<>();

    public NamedArgumentParser(String keyword, int priority, Collection<NamedArgument> namedArguments) {
        super(keyword, NamedArgumentResult.class, priority, true);

        this.namedArguments = Set.copyOf(namedArguments);

        for (NamedArgument namedArgument : namedArguments) {
            NamedArgument old = namedArgumentMap.put(namedArgument.form().toLowerCase(), namedArgument);
            if (old != null)
                throw new IllegalArgumentException("Named argument '" + namedArgument.form() + "' is already registered");

            for (String form : namedArgument.forms()) {
                old = namedArgumentMap.put(form.toLowerCase(), namedArgument);
                if (old != null)
                    throw new IllegalArgumentException("Named argument '" + form + "' is already registered");
            }
        }

    }

    public NamedArgumentParser(String keyword, Collection<NamedArgument> namedArguments) {
        this(keyword, DEFAULT_PRIORITY, namedArguments);
    }

    public NamedArgumentParser(int priority, Collection<NamedArgument> namedArguments) {
        this(DEFAULT_KEYWORD, priority, namedArguments);
    }

    @Override
    public Optional<ParseResult<NamedArgumentResult>> parse(CommandProcessingContext context) {
        try {
            Reader reader = new Reader(context, false);
            reader.read();
            return Optional.of(new ParseResult<>(
                    reader.build(),
                    reader.index
            ));
        } catch (BadFlag badFlag) {
            context.report(
                    this,
                    new InvalidNamedArgumentResponse(
                            new IndexedCommandProcessingContext(context, badFlag.lastIndex),
                            this,
                            badFlag.getMessage(),
                            badFlag.error
                    )
            );
            return Optional.empty();
        }
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext context) {
        try {
            Reader reader = new Reader(context, true);
            reader.read();
            return OptionalInt.of(reader.index);
        } catch (BadFlag badFlag) {
            context.report(
                    this,
                    new InvalidNamedArgumentResponse(
                            new IndexedCommandProcessingContext(context, badFlag.lastIndex),
                            this,
                            badFlag.getMessage(),
                            badFlag.error
                    )
            );
            return OptionalInt.empty();
        }
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        return Optional.empty();
    }

    @Override
    public ArgumentParser<NamedArgumentResult> union(ArgumentParser<?>... parsers) {
        Set<NamedArgument> arguments = new HashSet<>(this.namedArguments);

        for (ArgumentParser<?> parser : parsers) {
            if (!(parser instanceof NamedArgumentParser namedArgumentParser))
                throw new IllegalArgumentException("Parser " + parser.getClass().getName() + " is not a named argument parser");

            arguments.addAll(namedArgumentParser.namedArguments);
        }

        return new NamedArgumentParser(getKeyword(), getPriority(), arguments);
    }

    private final class Reader {

        private final Set<FlagArgument> flagArguments = Collections.newSetFromMap(new IdentityHashMap<>());
        private final Map<OptionArgument<?>, OptionArgumentValue<?>> optionArguments = new ConcurrentHashMap<>();
        private final CommandProcessingContext context;
        private final List<String> args;
        private final boolean tryParse;
        private String[] argsAsArray = null;
        private int index;

        public Reader(CommandProcessingContext context, boolean tryParse) {
            this.context = context;
            this.args = context.args();
            this.index = context.index();
            this.tryParse = tryParse;
        }

        private void read() throws BadFlag {
            for (; this.index < args.size(); this.index++) {
                String token = args.get(this.index);
                if (!token.startsWith("-"))
                    return;

                if (token.length() == 1)
                    throw new BadFlag("Invalid usage of named arguments", this.index, null);

                token = token.substring(1);
                boolean doubleDash = token.startsWith("-");
                if (doubleDash)
                    token = token.substring(1);

                if (token.isEmpty())
                    throw new BadFlag("Invalid usage of named arguments", this.index, null);

                index = readArg(token, doubleDash);
            }
        }

        private int readArg(String token, boolean doubleDash) throws BadFlag {
            List<NamedArgument> namedArguments = new ArrayList<>();
            if (doubleDash) {
                namedArguments.add(Optional.ofNullable(namedArgumentMap.getOrDefault(token, null))
                        .orElseThrow(() -> new BadFlag("There is no --" + token + " named argument", index, null)));
            } else {
                for (char c : token.toCharArray()) {
                    NamedArgument namedArgument = Optional.ofNullable(namedArgumentMap.getOrDefault(String.valueOf(c), null))
                            .orElseThrow(() -> new BadFlag("There is no -" + c + " named argument", index, null));

                    if (namedArgument instanceof OptionArgument<?> && !namedArguments.isEmpty())
                        throw new BadFlag("Unexpected variation of named arguments", index, null);

                    namedArguments.add(namedArgument);
                }
            }

            for (NamedArgument namedArgument : namedArguments) {
                if (namedArgument instanceof FlagArgument flagArgument) {
                    add(flagArgument, index);
                    continue;
                }

                if (namedArgument instanceof OptionArgument<?> optionArgument)
                    return readOption(optionArgument) - 1;

                throw new BadFlag("Unexpected variation of named arguments", index, null);
            }

            return index;

        }

        private <T> int readOption(OptionArgument<T> optionArgument) throws BadFlag {
            int startIndex = index + 1;
            if (startIndex >= args.size())
                throw new BadFlag("Missing value for " + optionArgument.form(), index, null);

            IndexedCommandProcessingContext processingContext = new IndexedCommandProcessingContext(
                    context,
                    startIndex,
                    true
            );

            if (tryParse) {
                int newIndex = optionArgument.parser().tryParse(processingContext)
                        .orElseThrow(() -> orElseThrowParseFailure(processingContext, optionArgument));

                add(optionArgument, newIndex, new OptionArgumentValue<>(optionArgument, null, "-tryParse-"));
                return newIndex;
            }

            ParseResult<T> parseResult = optionArgument.parser().parse(processingContext)
                    .orElseThrow(() -> orElseThrowParseFailure(processingContext, optionArgument));

            T result = parseResult.result();
            int newIndex = parseResult.newIndex();

            add(
                    optionArgument,
                    newIndex,
                    new OptionArgumentValue<>(
                            optionArgument,
                            result,
                            String.join(" ", args.subList(index, newIndex))
                    )
            );
            return newIndex;
        }

        private BadFlag orElseThrowParseFailure(IndexedCommandProcessingContext processingContext, OptionArgument<?> optionArgument) {
            BadCommandResponse badCommandResponse = Optional.ofNullable(processingContext.errors())
                    .flatMap(m -> m.values()
                            .stream()
                            .findFirst()
                    )
                    .orElse(null);

            if (badCommandResponse == null)
                return new BadFlag("Missing value for " + optionArgument.form(), index, null);

            return new BadFlag(badCommandResponse.getMessage(), index, badCommandResponse);
        }

        private void add(FlagArgument argument, int index) throws BadFlag {
            requireNew(argument, index);
            meetRequirement(argument);

            flagArguments.add(argument);
        }

        private <T> void add(OptionArgument<T> argument, int index, OptionArgumentValue<T> value) {
            requireNew(argument, index);
            meetRequirement(argument);

            optionArguments.put(argument, value);
        }

        private void requireNew(NamedArgument argument, int index) {
            if (has(argument))
                throw new BadFlag(argument.form() + " was given twice.", index, null);
        }

        private void meetRequirement(NamedArgument argument) {
            Requirement requirement = argument.requirement();
            if (requirement == null)
                return;

            if (argsAsArray == null)
                argsAsArray = args.toArray(new String[0]);

            RequirementResult requirementResult = requirement.meetsRequirement(new RequirementEvaluationContext(
                    context.sender(),
                    context.label(),
                    argsAsArray,
                    index
            ));

            if (requirementResult.hasError())
                throw new BadFlag(requirementResult.getError().getMessage(), index, requirementResult.getError());
        }

        public boolean has(NamedArgument argument) {
            if (argument instanceof FlagArgument flagArgument)
                return flagArguments.contains(flagArgument);
            else if (argument instanceof OptionArgument<?> optionArgument)
                return optionArguments.containsKey(optionArgument);
            else return false;
        }

        private NamedArgumentResult build() {
            return new NamedArgumentResult(
                    flagArguments,
                    optionArguments
            );
        }
    }

    private static final class BadFlag extends RuntimeException {

        private final int lastIndex;
        private final BadCommandResponse error;

        BadFlag(String message, int lastIndex, BadCommandResponse error) {
            super(message, null, false, false);

            this.lastIndex = lastIndex;
            this.error = error;
        }

    }

}
