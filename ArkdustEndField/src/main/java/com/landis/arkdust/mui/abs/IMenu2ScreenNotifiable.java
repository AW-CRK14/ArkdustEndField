package com.landis.arkdust.mui.abs;

import com.landis.arkdust.mui.AbstractArkdustIndustContainerUI;
import com.landis.breakdowncore.module.blockentity.gmui.IMenuChangeNotify;
import icyllis.modernui.fragment.Fragment;

public interface IMenu2ScreenNotifiable {
    void bingFragment(IMenuChangeNotify fragment);

    void notifySlotChanged(int index);

    void notifyDataChanged(int index, int content);

}
