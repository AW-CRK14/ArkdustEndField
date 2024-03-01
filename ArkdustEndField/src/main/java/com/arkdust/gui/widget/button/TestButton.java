package com.arkdust.gui.widget.button;

import com.arkdust.gui.AbstractArkdustInfoUI;
import icyllis.modernui.core.Context;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.Button;

public class TestButton extends Button {
    public TestButton(Context context, ViewGroup viewGroup) {
        super(context);
        setBackground(AbstractArkdustInfoUI.withBorder());

    }
}
