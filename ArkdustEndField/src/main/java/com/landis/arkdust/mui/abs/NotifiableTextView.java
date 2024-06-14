package com.landis.arkdust.mui.abs;

import icyllis.modernui.core.Context;
import icyllis.modernui.widget.TextView;

import java.util.function.Consumer;

public class NotifiableTextView extends TextView {
    public final Consumer<NotifiableTextView> notify;

    public NotifiableTextView(Context context, Consumer<NotifiableTextView> notify) {
        super(context);
        this.notify = notify;
    }

    public void notifyText() {
        notify.accept(this);
    }
}
