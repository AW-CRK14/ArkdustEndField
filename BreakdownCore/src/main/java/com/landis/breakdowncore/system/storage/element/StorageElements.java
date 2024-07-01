package com.landis.breakdowncore.system.storage.element;

import java.util.Iterator;
import java.util.Map;

public class StorageElements {
    private final Map<MagicElement,Float> elements;

    private float receiveSpeed;

    public StorageElements(Map<MagicElement,Float> elements) {
        this.elements = elements;
    }


    public float getElementValue(MagicElement element) {
        return elements.get(element);
    }

    public Iterable<MagicElement> getElements() {
        return elements.keySet();
    }


    public boolean containsElement(MagicElement element) {
        return elements.containsKey(element);
    }


    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }


    public void clear() {
        elements.clear();
    }

    public void addElement(MagicElement element, float value) {
        if(this.containsElement(element)) {
            elements.put(element, elements.get(element) + value);
        } else {
            elements.put(element, value);
        }
    }

    public void removeElement(MagicElement elementName, float value, boolean deleteIfZero) {
        if (this.containsElement(elementName)) {
            float currentValue = elements.get(elementName);
            if (currentValue - value >= 0) {
                elements.put(elementName, currentValue - value);
            } else {
                if (deleteIfZero) {
                    this.deleteElement(elementName);
                } else {
                    elements.put(elementName, 0f);
                }
            }
        }
    }

    public void deleteElement(MagicElement elementName) {
        if (this.containsElement(elementName)) {
            elements.remove(elementName);
        }
    }

    public void setReceiveSpeed(float receiveSpeed) {
        this.receiveSpeed = receiveSpeed;
    }


    public void receiveElement(StorageElements other) {
        if (other == null) {
            return;
        }

        Iterator<MagicElement> iterator = other.getElements().iterator();

        if (!iterator.hasNext()) {
            return;
        }

        MagicElement element;
        do {
            element = iterator.next();
        } while (iterator.hasNext() && other.getElementValue(element) == 0);

        if (this.containsElement(element)) {
            float remainingValue = other.getElementValue(element);
            float amountToReceive = Math.min(remainingValue, this.receiveSpeed);

            this.addElement(element, amountToReceive);
            other.removeElement(element, amountToReceive, false);
        }
    }


    public String toString() {
        StringBuilder text = new StringBuilder("Storage Elements:\n");
        for (MagicElement element : this.getElements()) {
            text.append(element.getName()).append(" : ").append(this.getElementValue(element)).append("\n");
        }
        return text.toString();
    }

}
