package com.landis.arkdust.mui.mouse;

import icyllis.modernui.view.MotionEvent;
import icyllis.modernui.view.View;

public class BaseMouseInfo  {

//    {
//        //---[Mouse Event Handle鼠标事件处理]---
//
//        protected BaseMouseInfo mouseInfo = new BaseMouseInfo();
//        private boolean mouseInfoRedirected = false;
//        public boolean isMouseInfoRedirected() {
//        return mouseInfoRedirected;
//    }
//
//        public void redirectMouseInfo(BaseMouseInfo info){
//        this.mouseInfo = info;
//        if(!mouseInfoRedirected){
//            setOnTouchListener(null);
//        }
//        this.mouseInfoRedirected = true;
//    }
//
//        public final View.OnTouchListener mouseListener = (v, m)->{
//            this.mouseInfo.updateMouseInfo(m);
//            return false;
//        };
//    }



    // 鼠标指针的当前位置
    private float x;
    private float y;

    // 鼠标按钮状态，使用位字段来表示不同的按钮
    private boolean leftButtonPressed;
    private boolean rightButtonPressed;
    private boolean middleButtonPressed;
    private boolean isPressed;

    // 构造函数
    public BaseMouseInfo() {
        // 初始化鼠标状态
        this.x = 0;
        this.y = 0;
        this.leftButtonPressed = false;
        this.rightButtonPressed = false;
        this.middleButtonPressed = false;
        this.isPressed = false;
    }

    // 获取鼠标指针的当前X坐标
    public float getX() {
        return x;
    }

    // 设置鼠标指针的当前X坐标
    public void setX(float x) {
        this.x = x;
    }

    // 获取鼠标指针的当前Y坐标
    public float getY() {
        return y;
    }

    // 设置鼠标指针的当前Y坐标
    public void setY(float y) {
        this.y = y;
    }

    public boolean isPressed() {
        return isPressed;
    }

    // 检查鼠标左键是否被按下
    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    // 设置鼠标左键状态
    public void setLeftButtonPressed(boolean pressed) {
        this.leftButtonPressed = pressed;
    }

    // 检查鼠标右键是否被按下
    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    // 设置鼠标右键状态
    public void setRightButtonPressed(boolean pressed) {
        this.rightButtonPressed = pressed;
    }

    // 检查鼠标中键是否被按下
    public boolean isMiddleButtonPressed() {
        return middleButtonPressed;
    }

    // 设置鼠标中键状态
    public void setMiddleButtonPressed(boolean pressed) {
        this.middleButtonPressed = pressed;
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }


    public void updateMouseInfo(MotionEvent event){
        updateMouseInfo(event.getX(),event.getY(),event.isButtonPressed(1),event.isButtonPressed(2),event.isButtonPressed(4));
    }

    // 更新鼠标状态，通常由鼠标事件触发
    public void updateMouseInfo(float x, float y, boolean leftButton, boolean rightButton, boolean middleButton) {
        setX(x);
        setY(y);
        setLeftButtonPressed(leftButton);
        setRightButtonPressed(rightButton);
        setMiddleButtonPressed(middleButton);
        setPressed((leftButton || rightButton || middleButton));
    }

    // 用于调试的toString方法，打印当前鼠标状态
    @Override
    public String toString() {
        return "MouseInfo{" +
                "x=" + x +
                ", y=" + y +
                ", leftButtonPressed=" + leftButtonPressed +
                ", rightButtonPressed=" + rightButtonPressed +
                ", middleButtonPressed=" + middleButtonPressed +
                '}';
    }
}