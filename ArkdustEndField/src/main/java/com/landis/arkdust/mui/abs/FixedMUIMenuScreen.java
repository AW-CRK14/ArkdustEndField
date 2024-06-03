package com.landis.arkdust.mui.abs;

import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.mc.MuiScreen;
import icyllis.modernui.mc.ScreenCallback;
import icyllis.modernui.mc.UIManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**对 {@link icyllis.modernui.mc.neoforge.MenuScreen MenuScreen}的镜像处理，添加了部分内容以满足menu的需求 <br>
 * 可能会在mui的版本更新中废弃。目前mui的MenuScreen系统尚且存在较大的功能缺陷。
 * */
public class FixedMUIMenuScreen<T extends AbstractContainerMenu>
        extends AbstractContainerScreen<T>
        implements MuiScreen {

    private final UIManager mHost;
    private final Fragment mFragment;
    private final ScreenCallback mCallback;

    public FixedMUIMenuScreen(UIManager host, Fragment fragment, T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.mHost = host;
        this.mFragment = fragment;
        ScreenCallback var10001;
        if (fragment instanceof ScreenCallback callback) {
            var10001 = callback;
        } else {
            var10001 = null;
        }

        this.mCallback = var10001;
    }

    protected void init() {
        super.init();
        this.mHost.initScreen(this);
    }

    public void resize(@Nonnull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
    }

    public void render(@Nonnull GuiGraphics gr, int mouseX, int mouseY, float deltaTick) {
        ScreenCallback callback = this.getCallback();
        if (callback == null || callback.hasDefaultBackground()) {
            this.renderBackground(gr, mouseX, mouseY, deltaTick);
        }

        this.mHost.render();
    }

    protected void renderBg(@Nonnull GuiGraphics gr, float deltaTick, int x, int y) {
    }

    public void removed() {
        super.removed();
        this.mHost.removed();
    }

    @Nonnull
    public Fragment getFragment() {
        return this.mFragment;
    }

    @Nullable
    public ScreenCallback getCallback() {
        return this.mCallback;
    }

    public boolean isMenuScreen() {
        return true;
    }

    public void mouseMoved(double mouseX, double mouseY) {
        this.mHost.onHoverMove(true);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
//        this.mHost.
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        this.mHost.onScroll(deltaX, deltaY);
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.mHost.onKeyPress(keyCode, scanCode, modifiers);
        return false;
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.mHost.onKeyRelease(keyCode, scanCode, modifiers);
        return false;
    }

    public boolean charTyped(char ch, int modifiers) {
        return this.mHost.onCharTyped(ch);
    }
}
