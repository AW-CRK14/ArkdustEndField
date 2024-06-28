package com.landis.arkdust.mui.abs;

import com.landis.breakdowncore.module.blockentity.gmui.IMenuChangeNotify;

public interface IMenu2ScreenNotifiable {
    void bingFragment(IMenuChangeNotify fragment);

    void notifySlotChanged(int index);

    void notifyDataChanged(int index, int content);
    void notifyTankChanged(int index);

}
