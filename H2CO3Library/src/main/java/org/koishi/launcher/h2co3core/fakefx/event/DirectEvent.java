package org.koishi.launcher.h2co3core.fakefx.event;

/**
 * Used as a wrapper to protect an {@code Event} from being redirected by
 * {@code EventRedirector}. The redirector only unwraps such event and sends
 * it to the rest of the event chain.
 */
public class DirectEvent extends Event {
    public static final EventType<DirectEvent> DIRECT =
            new EventType<DirectEvent>(Event.ANY, "DIRECT");
    private static final long serialVersionUID = 20121107L;
    private final Event originalEvent;

    public DirectEvent(final Event originalEvent) {
        this(originalEvent, null, null);
    }

    public DirectEvent(final Event originalEvent,
                       final Object source,
                       final EventTarget target) {
        super(source, target, DIRECT);
        this.originalEvent = originalEvent;
    }

    public Event getOriginalEvent() {
        return originalEvent;
    }
}
