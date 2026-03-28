package com.maitre.mousetouch;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

public class MouseToTouchService extends AccessibilityService {

    private WindowManager windowManager;
    private View overlayView;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createOverlay();
    }

    private void createOverlay() {
        overlayView = new View(this) {
            @Override
            public boolean onGenericMotionEvent(MotionEvent event) {
                if (event.isFromSource(InputDevice.SOURCE_MOUSE)) {
                    if (event.getActionMasked() == MotionEvent.ACTION_BUTTON_PRESS
                            && event.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                        dispatchTap(event.getX(), event.getY());
                    }
                    return true;
                }
                return super.onGenericMotionEvent(event);
            }
        };

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSPARENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        windowManager.addView(overlayView, params);
    }

    private void dispatchTap(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription gesture = new GestureDescription.Builder()
                .addStroke(new GestureDescription.StrokeDescription(path, 0, 100))
                .build();
        dispatchGesture(gesture, null, null);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (windowManager != null && overlayView != null) {
            windowManager.removeView(overlayView);
        }
    }
}
