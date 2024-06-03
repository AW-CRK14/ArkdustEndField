package com.landis.arkdust.mui;

import com.landis.arkdust.mui.abs.ItemWidget;
import com.landis.arkdust.mui.widget.item.ArkdustContainerItemSlot;
import com.landis.breakdowncore.module.blockentity.container.ExpandedContainerMenu;
import com.landis.breakdowncore.module.blockentity.container.ISlotTypeExpansion;
import com.landis.breakdowncore.module.blockentity.container.SlotType;
import com.landis.breakdowncore.module.blockentity.gmui.ISlotChangeNotify;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.RelativeLayout;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractArkdustIndustContainerUI extends AbstractArkdustIndustUI implements ISlotChangeNotify {
    public final AbstractContainerMenu menu;
    protected final List<ItemWidget> widgets;
    public final boolean autoAddSlots;

    protected ViewGroup itemsGroup;
    public AbstractArkdustIndustContainerUI(boolean addPlayerSlots, AbstractContainerMenu menu){
        this(addPlayerSlots,menu,true);
    }
    public AbstractArkdustIndustContainerUI(boolean addPlayerSlots, AbstractContainerMenu menu, boolean autoAddSlots) {
        super(addPlayerSlots);
        this.autoAddSlots = autoAddSlots;
        this.menu = menu;
        if(menu instanceof ExpandedContainerMenu e){
            e.setBelonging(this);
        }
        widgets = new ArrayList<>(menu.slots.size());
        for(int i = 0 ; i < menu.slots.size() ; i++){
            widgets.add(null);
        }
    }

    @Override
    public @NotNull View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
        ViewGroup g = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        genItemViews(g);
        return g;
    }

    protected void genItemViews(ViewGroup g) {
        if(autoAddSlots){
            ViewGroup items = new RelativeLayout(getContext());
            for(int i = 0 ; i < menu.slots.size() ; i++) {
                Pair<ItemWidget, RelativeLayout.LayoutParams> pair = createWidget(i,menu.getSlot(i),menu instanceof ISlotTypeExpansion e ? e.getForType(i) : null);
                widgets.set(i,pair.getKey());
                if(pair.getRight() != null){
                    items.addView(pair.getLeft(),pair.getRight());
                }else {
                    items.addView(pair.getLeft());
                }
            }
            g.addView(items);
            this.itemsGroup = items;
        }
    }

    @Override
    public void notify(int index) {
        if(widgets.get(index) != null){
            widgets.get(index).refresh();
        }
    }

    public ItemWidget createItemWidgetCType(int index, float los){
        ItemWidget widget;
        if(los > 0){
            widget = new ArkdustContainerItemSlot(getContext(), menu.getSlot(index), los, menu);
        }else {
            widget = new ArkdustContainerItemSlot(getContext(), menu.getSlot(index), menu);
        }
        widgets.set(index, widget);
        return widget;
    }



    protected abstract Pair<ItemWidget,RelativeLayout.LayoutParams> createWidget(int index, Slot slot, @Nullable SlotType type);
}
