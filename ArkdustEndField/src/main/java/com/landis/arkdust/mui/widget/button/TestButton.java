package com.landis.arkdust.mui.widget.button;

import com.landis.arkdust.helper.MUIHelper;
import com.landis.arkdust.mui.AbstractArkdustInfoUI;
import icyllis.modernui.core.Context;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.Button;

public class TestButton extends Button {
    public TestButton(Context context, ViewGroup viewGroup) {
        super(context);
        setBackground(MUIHelper.withBorder());

    }
}
