package com.landis.breakdowncore.system.storage.element;

import java.util.HashMap;

public interface IStorageElementsAble {
    StorageElements elements = new StorageElements(new HashMap<>());
    default StorageElements getStorage(){
        return elements;
    }

}
