package org.joshy.gfx.event;

public class ChangedEvent extends Event {
    public static final EventType StringChanged = new EventType("StringChanged");
    public static final EventType DoubleChanged = new EventType("DoubleChanged");
    public static final EventType ColorChanged = new EventType("ColorChanged");
    public static final EventType IntegerChanged = new EventType("IntegerChanged");
    public static final EventType BooleanChanged = new EventType("BooleanChanged");
    public static final EventType ObjectChanged = new EventType("ObjectChanged");
    public static final EventType FinalChange = new EventType("FinalChange");
    private Object value;
    private boolean adjusting = false;

    public ChangedEvent(EventType type, Object value, Object source) {
        super(type);
        this.value = value;
        this.source = source;
    }

    public ChangedEvent(EventType type, Object value, Object source, boolean adjusting) {
        super(type);
        this.value = value;
        this.source = source;
        this.adjusting = adjusting;
    }

    public Object getValue() {
        return value;
    }

    public boolean getBooleanValue() {
        return (Boolean)value;
    }

    public boolean isAdjusting() {
        return this.adjusting;
    }
}
