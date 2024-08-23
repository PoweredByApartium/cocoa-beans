package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.Dispensers;
import net.apartium.cocoabeans.commands.CommandNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @hidden
 */
@ApiStatus.Internal
public class SourceParserFactory implements ParserFactory {

    @Override
    public @NotNull Collection<ParserResult> getArgumentParser(CommandNode commandNode, Annotation annotation, GenericDeclaration obj) {
        if (!(annotation instanceof SourceParser sourceParser))
            return Collections.emptyList();

        if (!(obj instanceof Method method))
            return Collections.emptyList();

        if (!method.getReturnType().equals(Map.class))
            throw new RuntimeException("Wrong return type: " + method.getReturnType());

        try {
            return List.of(new ParserResult(new SourceParserImpl<>(
                    commandNode,
                    sourceParser.keyword(),
                    sourceParser.clazz(),
                    sourceParser.priority(),
                    MethodHandles.publicLookup().unreflect(method),
                    sourceParser.resultMaxAgeInMills(),
                    sourceParser.ignoreCase(),
                    sourceParser.lax()
            ), Scope.CLASS));
        } catch (IllegalAccessException e) {
            Dispensers.dispense(e);
            return Collections.emptyList();
        }
    }

    private static class SourceParserImpl<T> extends MapBasedParser<T> {

        private final CommandNode node;

        private final MethodHandle handle;
        private final long resultMaxAgeInMills;

        private Instant nextCall = Instant.now();
        private Map<String, T> result = null;

        public SourceParserImpl(CommandNode node, String keyword, Class<T> clazz, int priority, MethodHandle handle, long resultMaxAgeInMills, boolean ignoreCase, boolean lax) {
            super(keyword, clazz, priority, ignoreCase, lax);

            this.node = node;

            this.handle = handle;
            this.resultMaxAgeInMills = resultMaxAgeInMills;
        }

        @Override
        public Map<String, T> getMap() {
            if (this.resultMaxAgeInMills == 0)
                return getResult();

            if (this.result == null)
                this.result = getResult();

            if (this.resultMaxAgeInMills == -1)
                return this.result;

            if (Instant.now().isAfter(this.nextCall)) {
                this.nextCall = Instant.now().plusMillis(this.resultMaxAgeInMills);
                this.result = getResult();
            }

            return this.result;
        }

        private Map<String, T> getResult() {
            try {
                return (Map<String, T>) handle.invoke(node);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
