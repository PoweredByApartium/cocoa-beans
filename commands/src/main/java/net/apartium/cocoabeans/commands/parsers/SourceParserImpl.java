package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandNode;

import java.lang.invoke.MethodHandle;
import java.time.Instant;
import java.util.Map;

public class SourceParserImpl<T> extends MapBasedParser<T> {

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
