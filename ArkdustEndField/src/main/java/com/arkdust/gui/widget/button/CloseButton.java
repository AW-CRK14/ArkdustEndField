package com.arkdust.gui.widget.button;

import com.arkdust.Arkdust;
import icyllis.modernui.core.Context;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.drawable.ImageDrawable;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.CompoundButton;
import net.minecraft.client.Minecraft;

public class CloseButton extends CompoundButton {
//    private final TextView text;
//    private int ticker;
//    private boolean flag = false;
//
//    private Runnable runnable;

    public CloseButton(Context context, ViewGroup base, Fragment fragment) {
        super(context);

        setBackground(new ImageDrawable(Arkdust.MODID,"gui/element/close.png"));

//        Pair<TextView, RelativeLayout.LayoutParams> textPair = MUIHelper.drawStringAt(context,base,I18n.get("gui.arkdust.element.no_close"), Color.WHITE,12, Gravity.CENTER | Gravity.TOP,0,base.dp(3),0,0);
//        text = textPair.left();
//        textPair.right().addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        textPair.right().addRule(RelativeLayout.CENTER_HORIZONTAL);
//        text.setAlpha(0);
//
//        runnable = ()-> {
//            ticker += flag ? 1 : -1;
//            ticker = Math.clamp(0, 20, ticker);
//            text.setAlpha(ticker / 20F);
//            postDelayed(runnable,50);
//        };
//
//        postDelayed(runnable,50);

//        setOnHoverListener(((view, motionEvent) -> {
//            if(motionEvent.getAction() == MotionEvent.ACTION_HOVER_ENTER){
//                flag = true;
//            }else if(motionEvent.getAction() == MotionEvent.ACTION_HOVER_EXIT){
//                flag = false;
//            }
//            return false;
//        }));



        setOnCheckedChangeListener((button,id)-> {
            Minecraft.getInstance().execute(()->Minecraft.getInstance().setScreen(null));
        });
    }
}
