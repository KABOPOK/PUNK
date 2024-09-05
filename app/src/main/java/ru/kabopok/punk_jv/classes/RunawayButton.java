package ru.kabopok.punk_jv.classes;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class RunawayButton extends AppCompatButton {

    private int xVelocity;
    private int yVelocity;

    public RunawayButton(Context context) {
        super(context);
        init();
    }

    public RunawayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RunawayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        xVelocity = 0;
        yVelocity = 0;
    }

    public void setVelocity(int xVelocity, int yVelocity) {
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int newLeft = getLeft() + xVelocity;
        int newTop = getTop() + yVelocity;

        // Ограничьте движение кнопки пределами экрана
        if (newLeft < 0) {
            newLeft = 0;
        } else if (newLeft + getWidth() > getRootView().getWidth()) {
            newLeft = getRootView().getWidth() - getWidth();
        }

        if (newTop < 0) {
            newTop = 0;
        } else if (newTop + getHeight() > getRootView().getHeight()) {
            newTop = getRootView().getHeight() - getHeight();
        }

        // Переместите кнопку
        layout(newLeft, newTop, newLeft + getWidth(), newTop + getHeight());
    }
}
