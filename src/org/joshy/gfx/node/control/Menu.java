package org.joshy.gfx.node.control;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.joshy.gfx.event.*;
import org.joshy.gfx.util.OSUtil;

public class Menu {
    private CharSequence title;
    private List actions;
    private JMenu jMenu;

    public Menu() {
        actions = new ArrayList();
    }

    public Menu setTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    public Menu addItem(CharSequence title, AminoAction action) {
        return this.addItem(title,null,action);
    }

    public Menu addItem(CharSequence title, String key, AminoAction action) {
        if(action instanceof ToggleAction) {
            ToggleActionAdapter act = new ToggleActionAdapter(title,key, (ToggleAction) action);
            actions.add(act);
            if(jMenu != null) {
                jMenu.add(new JCheckBoxMenuItem(act));
            }
        } else {
            ActionAdapter act = new ActionAdapter(title,key,action);
            actions.add(act);
            if(jMenu != null) {
                jMenu.add(act);
            }
        }
        return this;
    }

    public Menu removeAll() {
        actions.clear();
        if(jMenu != null) {
            jMenu.removeAll();
        }
        return this;
    }

    public Menu separator() {
        actions.add(new JSeparator());
        return this;
    }

    public Menu addMenu(Menu menu) {
        actions.add(menu);
        return this;
    }

    JMenu createJMenu() {
        jMenu = new JMenu(title.toString());
        for(Object action : actions) {
            if(action instanceof JSeparator) {
                jMenu.add((JSeparator)action);
                continue;
            }
            if(action instanceof ToggleActionAdapter) {
                jMenu.add(new JCheckBoxMenuItem((ToggleActionAdapter)action));
                continue;
            }
            if(action instanceof ActionAdapter) {
                jMenu.add((ActionAdapter)action);
                continue;
            }
            if(action instanceof Menu) {
                jMenu.add(((Menu)action).createJMenu());
                continue;
            }
        }
        return jMenu;
    }

    public JMenu getNativeMenu() {
        return this.jMenu;
    }


    private static class ActionAdapter extends AbstractAction {
        private CharSequence name;
        private AminoAction action;
        private String acceleratorKey;

        public ActionAdapter(CharSequence name, String acceleratorKey, AminoAction action) {
            this.name = name;
            this.action = action;
            this.acceleratorKey = acceleratorKey;
            EventBus.getSystem().addListener(action, ChangedEvent.BooleanChanged, new Callback<ChangedEvent>() {
                public void call(ChangedEvent changedEvent) throws Exception {
                    setEnabled(changedEvent.getBooleanValue());
                }
            });
        }

        @Override
        public Object getValue(String key) {
            if(Action.NAME.equals(key)) {
                if(name != null) return name.toString();
                return action.getDisplayName().toString();
            }
            if(Action.ACCELERATOR_KEY.equals(key)) {
                if(acceleratorKey != null) {
                    if(OSUtil.isMac()) {
                        return KeyStroke.getKeyStroke("meta " + acceleratorKey);
                    } else {
                        return KeyStroke.getKeyStroke("control " + acceleratorKey);
                    }
                }
                return null;
            }
            return super.getValue(key);
        }

        public boolean isEnabled() {
            return action.isEnabled();
        }
        public void actionPerformed(java.awt.event.ActionEvent e) {
            try {
                action.execute();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private class ToggleActionAdapter extends ActionAdapter {
        private ToggleAction toggleAction;

        public ToggleActionAdapter(CharSequence title, String key, ToggleAction action) {
            super(title,key,action);
            toggleAction = action;
        }

        @Override
        public void putValue(String key, Object newValue) {
            if(Action.SELECTED_KEY.equals(key)) {
                toggleAction.setToggleState((Boolean)newValue);
            }
            super.putValue(key, newValue);
        }

        @Override
        public Object getValue(String key) {
            if(Action.SELECTED_KEY.equals(key)) {
                return toggleAction.getToggleState();
            }
            return super.getValue(key);
        }
    }
}
