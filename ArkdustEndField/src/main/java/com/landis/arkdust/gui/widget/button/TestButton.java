package com.landis.arkdust.gui.widget.button;

import com.landis.arkdust.gui.AbstractArkdustInfoUI;
import icyllis.modernui.core.Context;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.Button;

public class TestButton extends Button {
    public TestButton(Context context, ViewGroup viewGroup) {
        super(context);
        setBackground(AbstractArkdustInfoUI.withBorder());

    }
}
