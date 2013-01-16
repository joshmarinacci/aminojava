package org.joshy.gfx.event;

/**
* Created by IntelliJ IDEA.
* User: josh
* Date: Jan 20, 2010
* Time: 10:14:43 AM
* To change this template use File | Settings | File Templates.
*/
public class Event {
    protected EventType type;
    public boolean defer;

    public Event(EventType type) {
        this.type = type;
    }

    public Event(EventType type, Object source) {
        this.type = type;
        this.source = source;
    }

    public EventType getType() {
        return type;
    }
    
    protected Object source;

    public Object getSource() {
        return source;
    }

    protected void finished() {

    }


    public static class EventType {
        private String type;

        public EventType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }

        public boolean matches(EventType type) {
            if(this.type.equals(type.type)) {
                return true;
            }
            return false;
        }
    }

}
