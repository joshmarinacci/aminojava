package org.joshy.gfx.event;

public abstract class ToggleAction extends AminoAction {

    public abstract boolean getToggleState();
    public abstract void setToggleState(boolean toggleState);

    /**
     * Subclasses should override get/setToggleState instead of execute
     */
    @Override
    public void execute() {
        //no op
    }
}
