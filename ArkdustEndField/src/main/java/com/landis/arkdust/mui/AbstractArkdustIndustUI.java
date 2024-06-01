package com.landis.arkdust.mui;

import com.landis.arkdust.Arkdust;
import com.landis.arkdust.mui.widget.viewgroup.IndsGroup;
import icyllis.modernui.ModernUI;
import icyllis.modernui.audio.AudioManager;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.RelativeLayout;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import javax.annotation.Nonnull;

public class AbstractArkdustIndustUI extends Fragment {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        Configurator.setRootLevel(Level.DEBUG);

        try (ModernUI app = new ModernUI()) {
            app.run(new AbstractArkdustIndustUI(false));
        }
        AudioManager.getInstance().close();
        System.gc();
    }

    public final boolean addPlayerSlots;

    public AbstractArkdustIndustUI(boolean addPlayerSlots) {
        this.addPlayerSlots = addPlayerSlots;
    }

    @Override
    @Nonnull
    public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
        ViewGroup base = new RelativeLayout(getContext());
        base.addView(new IndsGroup(getContext(),new ResourceLocation(Arkdust.MODID,"test"),2,true),new ViewGroup.LayoutParams(base.dp(1000),base.dp(320)));
        return base;
    }
}
