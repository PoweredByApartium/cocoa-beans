package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Function;

@ApiStatus.Internal
public class CompoundRecords {

    private static final List<Function<List<?>, CompoundRecord>> recordConstructions = List.of(
            ignored -> new EmptyRecord(),
            RecordOf1::new,
            RecordOf2::new,
            RecordOf3::new,
            RecordOf4::new,
            RecordOf5::new,
            RecordOf6::new,
            RecordOf7::new,
            RecordOf8::new,
            RecordOf9::new,
            RecordOf10::new
    );

    @SuppressWarnings("unchecked")
    static <T extends CompoundRecord> Observable<T> compound(Observable<?>... params) {
        return (Observable<T>) new ObservableCompound<>(CompoundRecords::constructDynamic, List.of(params));
    }

    public static CompoundRecord constructDynamic(List<?> content) {
        if (content.size() > recordConstructions.size())
            throw new IndexOutOfBoundsException("Too many arguments");

        return recordConstructions.get(content.size()).apply(content);
    }

    public sealed interface CompoundRecord permits EmptyRecord, RecordOf1, RecordOf2, RecordOf3, RecordOf4, RecordOf5, RecordOf6, RecordOf7, RecordOf8, RecordOf9, RecordOf10 {

    }

    public record EmptyRecord() implements CompoundRecord {}

    public record RecordOf1<A>(A arg0) implements CompoundRecord {
        public RecordOf1(List<?> values) {
            this((A) values.get(0));
        }
    }

    public record RecordOf2<ARG0, ARG1>(ARG0 arg0, ARG1 arg1) implements CompoundRecord {
        public RecordOf2(List<?> values) {
            this((ARG0) values.get(0), (ARG1) values.get(1));
        }
    }

    public record RecordOf3<ARG0, ARG1, ARG2>(ARG0 arg0, ARG1 arg1, ARG2 arg2) implements CompoundRecord {
        public RecordOf3(List<?> values) {
            this((ARG0) values.get(0), (ARG1) values.get(1), (ARG2) values.get(2));
        }
    }

    public record RecordOf4<ARG0, ARG1, ARG2, ARG3>(ARG0 arg0, ARG1 arg1, ARG2 arg2, ARG3 arg3) implements CompoundRecord {
        public RecordOf4(List<?> values) {
            this((ARG0) values.get(0), (ARG1) values.get(1), (ARG2) values.get(2), (ARG3) values.get(3));
        }
    }

    public record RecordOf5<ARG0, ARG1, ARG2, ARG3, ARG4>(ARG0 arg0, ARG1 arg1, ARG2 arg2, ARG3 arg3, ARG4 arg4) implements CompoundRecord {
        public RecordOf5(List<?> values) {
            this((ARG0) values.get(0), (ARG1) values.get(1), (ARG2) values.get(2), (ARG3) values.get(3), (ARG4) values.get(4));
        }
    }

    public record RecordOf6<ARG0, ARG1, ARG2, ARG3, ARG4, ARG5>(ARG0 arg0, ARG1 arg1, ARG2 arg2, ARG3 arg3, ARG4 arg4, ARG5 arg5) implements CompoundRecord {
        public RecordOf6(List<?> values) {
            this((ARG0) values.get(0), (ARG1) values.get(1), (ARG2) values.get(2), (ARG3) values.get(3), (ARG4) values.get(4), (ARG5) values.get(5));
        }
    }

    public record RecordOf7<ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6>(ARG0 arg0, ARG1 arg1, ARG2 arg2, ARG3 arg3, ARG4 arg4, ARG5 arg5, ARG6 arg6) implements CompoundRecord {
        public RecordOf7(List<?> values) {
            this((ARG0) values.get(0), (ARG1) values.get(1), (ARG2) values.get(2), (ARG3) values.get(3), (ARG4) values.get(4), (ARG5) values.get(5), (ARG6) values.get(6));
        }
    }

    public record RecordOf8<ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6, ARG7>(ARG0 arg0, ARG1 arg1, ARG2 arg2, ARG3 arg3, ARG4 arg4, ARG5 arg5, ARG6 arg6, ARG7 arg7) implements CompoundRecord {
        public RecordOf8(List<?> values) {
            this((ARG0) values.get(0), (ARG1) values.get(1), (ARG2) values.get(2), (ARG3) values.get(3), (ARG4) values.get(4), (ARG5) values.get(5), (ARG6) values.get(6), (ARG7) values.get(7));
        }
    }

    public record RecordOf9<ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6, ARG7, ARG8>(ARG0 arg0, ARG1 arg1, ARG2 arg2, ARG3 arg3, ARG4 arg4, ARG5 arg5, ARG6 arg6, ARG7 arg7, ARG8 arg8) implements CompoundRecord {
        public RecordOf9(List<?> values) {
            this((ARG0) values.get(0), (ARG1) values.get(1), (ARG2) values.get(2), (ARG3) values.get(3), (ARG4) values.get(4), (ARG5) values.get(5), (ARG6) values.get(6), (ARG7) values.get(7), (ARG8) values.get(8));
        }
    }

    public record RecordOf10<ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6, ARG7, ARG8, ARG9>(ARG0 arg0, ARG1 arg1, ARG2 arg2, ARG3 arg3, ARG4 arg4, ARG5 arg5, ARG6 arg6, ARG7 arg7, ARG8 arg8, ARG9 arg9) implements CompoundRecord {
        public RecordOf10(List<?> values) {
            this((ARG0) values.get(0), (ARG1) values.get(1), (ARG2) values.get(2), (ARG3) values.get(3), (ARG4) values.get(4), (ARG5) values.get(5), (ARG6) values.get(6), (ARG7) values.get(7), (ARG8) values.get(8), (ARG9) values.get(9));
        }
    }
    
}
