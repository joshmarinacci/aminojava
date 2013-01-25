package org.joshy.gfx.node.control;

import java.util.ArrayList;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;

public class Togglegroup {
    private final ArrayList<Button> buttons;

    public Togglegroup() {
        this.buttons = new ArrayList<Button>();
    }

    public void add(final Button tg) {
        this.buttons.add(tg);
        tg.onClicked(new Callback<ActionEvent>() {
            @Override
            public void call(ActionEvent event) throws Exception {
                if(tg.isSelected()) {
                    for(Button bt : buttons) {
                        if(bt != tg) {
                            bt.setSelected(false);
                        }
                    }
                }
            }
        });
    }
}
