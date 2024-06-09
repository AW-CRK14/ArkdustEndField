package com.landis.breakdowncore.module.blockentity.gmui;

public interface IMenuChangeNotify {
    default void notify(int index){}

    default void notifyData(int index, int content){}

}
