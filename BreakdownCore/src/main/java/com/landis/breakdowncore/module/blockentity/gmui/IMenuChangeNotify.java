package com.landis.breakdowncore.module.blockentity.gmui;

public interface IMenuChangeNotify {
    default void notifyItem(int index) {
    }

    default void notifyFluid(int index) {
    }

    default void notifyData(int index, int content) {
    }

}
