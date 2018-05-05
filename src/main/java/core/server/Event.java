package core.server;

/**
 * A functional interface used for event callbacks.
 *
 * @author Niklas Johansen
 */
@FunctionalInterface
public interface Event
{
    void call();
}
