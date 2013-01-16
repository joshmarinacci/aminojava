package org.joshy.gfx.event;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 1/16/13
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AminoAction {
    private boolean enabled = true;

    public CharSequence getDisplayName() {
        return "unknown saction";
    }
    public abstract void execute() throws Exception;
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        boolean old = isEnabled();
        this.enabled = enabled;
        if(this.enabled != old) {
            fireChange(old,this.enabled);
        }
    }

    private void fireChange(boolean old, boolean enabled) {
        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.BooleanChanged,enabled,this));
    }
}
