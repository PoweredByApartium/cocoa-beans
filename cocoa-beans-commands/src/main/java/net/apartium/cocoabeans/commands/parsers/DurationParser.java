package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@ApiStatus.AvailableSince("0.0.47")
public class DurationParser extends ArgumentParser<Duration> {

    public static final String DEFAULT_KEYWORD = "duration";

    public static final Map<String, Duration> DEFAULT_UNITS = Map.of(
            "y", Duration.ofDays(365),
            "w", Duration.ofDays(7),
            "d", Duration.ofDays(1),
            "h", Duration.ofHours(1),
            "m", Duration.ofMinutes(1),
            "s", Duration.ofSeconds(1)
    );

    private final Map<String, Duration> units;
    private final int minUnitLength;
    private final int maxUnitLength;

    /**
     * Constructs a new parser
     *
     * @param keyword  keyword of the parser
     * @param priority priority
     * @param units units of time
     */
    protected DurationParser(String keyword, int priority, Map<String, Duration> units) {
        super(keyword, Duration.class, priority, true);

        if (units == null || units.isEmpty())
            throw new IllegalArgumentException("units cannot be null or empty");

        this.units = units;

        this.minUnitLength = units.keySet()
                .stream()
                .map(String::length)
                .min(Integer::compareTo)
                .orElse(0);
        this.maxUnitLength = units.keySet()
                .stream()
                .map(String::length)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public DurationParser(String keyword, int priority) {
        this(keyword, priority, DEFAULT_UNITS);
    }

    public DurationParser(int priority) {
        this(DEFAULT_KEYWORD, priority);
    }

    public DurationParser() {
        this(0);
    }

    private record ArgResult(long value, Duration unit) { }

    @Override
    public Optional<ParseResult<Duration>> parse(CommandProcessingContext context) {
        List<String> args = context.args();
        int startIndex = context.index();

        if (startIndex >= args.size())
            return Optional.empty();


        Duration total = Duration.ZERO;
        for (int index = startIndex; index < args.size(); index++) {
            ArgResult argResult = parseArg(args.get(index));
            if (argResult == null) {
                if (total.isZero()) {
                    return Optional.empty();
                }

                return Optional.of(new ParseResult<>(
                        total,
                        index
                ));
            }

            long value = argResult.value();
            Duration base = argResult.unit();

            Duration duration = base.multipliedBy(value);
            total = total.plus(duration);
        }

        return Optional.of(new ParseResult<>(
                total,
                args.size()
        ));
    }

    private ArgResult parseArg(String arg) {
        int splitIndex = -1;
        long num = 0;
        long numByTen;

        for (int i = 0; i < arg.length(); i++) {
            char c = arg.charAt(i);
            if (!Character.isDigit(c)) {
                splitIndex = i;
                break;
            }

            num = num * 10 + (c - '0');
            numByTen = num * 10;
            if (num > numByTen)
                return null;
        }

        if (splitIndex == -1 || splitIndex == 0)
            return null;


        if (arg.length() - splitIndex > maxUnitLength || arg.length() - splitIndex < minUnitLength)
            return null;

        Duration duration = units.getOrDefault(arg.substring(splitIndex), null);

        if (duration == null)
            return null;

        return new ArgResult(num, duration);
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext context) {
        return parse(context)
                .map(result -> OptionalInt.of(result.newIndex()))
                .orElse(OptionalInt.empty());
    }

    private Optional<TabCompletionResult> numberTabCompletion(String arg, int index, List<String> args, long asLong, int lastArgIndex) {
        Set<String> result = new HashSet<>();
        for (int i = asLong == 0 ? 1 : 0; i < 10; i++) {
            long num = (asLong * 10 + i);
            if (asLong > num)
                break;

            result.add(args.get(index) + i);
        }

        if (arg.length() == lastArgIndex) {
            final long finalAsLong = asLong;
            result.addAll(this.units.keySet()
                    .stream()
                    .map(unit -> finalAsLong + unit)
                    .collect(Collectors.toSet())
            );
        }

        if (result.isEmpty())
            return Optional.empty();

        return Optional.of(new TabCompletionResult(
                result,
                index + 1
        ));
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (args.size() <= index || args.get(index).isEmpty())
            return Optional.of(new TabCompletionResult(
                    Set.of("1", "2", "3", "4", "5", "6", "7", "8", "9"),
                    index + 1
            ));

        index = tryParse(processingContext).orElse(index);
        if (index >= args.size())
            index -= 1;

        long asLong = 0;
        String arg = args.get(index);
        int lastArgIndex = 0;
        boolean numberComplete = true;
        while (lastArgIndex < arg.length() && numberComplete) {
            char c = arg.charAt(lastArgIndex);
            if (Character.isDigit(c)) {
                if (asLong > asLong * 10)
                    return Optional.empty();

                asLong = asLong * 10 + (c - '0');
                lastArgIndex++;
                continue;
            }

            numberComplete = false;
        }

        if (numberComplete)
            return numberTabCompletion(arg, index, args, asLong, lastArgIndex);

        String attemptUnit = arg.substring(lastArgIndex);
        final long finalAsLong = asLong;
        Set<String> result = this.units.keySet()
                .stream()
                .filter(unit -> unit.startsWith(attemptUnit))
                .map(unit -> finalAsLong + unit)
                .collect(Collectors.toSet());

        if (result.isEmpty())
            return Optional.empty();

        return Optional.of(new TabCompletionResult(
                result,
                index + 1
        ));
    }
}
