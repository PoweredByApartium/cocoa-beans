package net.apartium.cocoabeans.commands.parameter;

import net.apartium.cocoabeans.commands.Sender;

public class PlayerSender implements Sender {

    private final String name;

    public PlayerSender(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the underlying platform-specific sender object.
     * For Spigot, this would be the CommandSender instance
     *
     * @return the underlying sender object
     */
    @Override
    public Object getSender() {
        return null;
    }

    /**
     * Sends a message to the sender
     *
     * @param text message as text
     */
    @Override
    public void sendMessage(String text) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Sends a message to the sender
     *
     * @param args messages as text
     */
    @Override
    public void sendMessage(String... args) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
