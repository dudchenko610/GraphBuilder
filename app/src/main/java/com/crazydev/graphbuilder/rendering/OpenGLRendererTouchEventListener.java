package com.crazydev.graphbuilder.rendering;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.crazydev.graphbuilder.framework.Input.TouchEvent;
import com.crazydev.graphbuilder.framework.Pool;
import com.crazydev.graphbuilder.framework.Pool.PoolObjectFactory;


import java.util.ArrayList;
import java.util.List;

public class OpenGLRendererTouchEventListener implements OnTouchListener {

    private Pool<TouchEvent> touchEventPool;
    private List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
    private List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();

    private static final int MAX_TOUCHPOINTS = 10;

    public OpenGLRendererTouchEventListener() {

        PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {

                return new TouchEvent();
            }
        };

        touchEventPool = new Pool<TouchEvent> (factory, 100);

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        synchronized (this) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
            int pointerCount = event.getPointerCount();
            TouchEvent touchEvent;

            for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
                if (i >= pointerCount) {
                    continue;
                }

                int pointerId = event.getPointerId(i);
                if (event.getAction() != MotionEvent.ACTION_MOVE && i != pointerIndex) {
                    continue;
                }

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        touchEvent = touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_DOWN;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = (int) event.getX(i);
                        touchEvent.y = (int) event.getY(i);

                        touchEventsBuffer.add(touchEvent);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:
                        touchEvent = touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_UP;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = (int) event.getX(i);
                        touchEvent.y = (int) event.getY(i);

                        touchEventsBuffer.add(touchEvent);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touchEvent = touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = (int) event.getX(i);
                        touchEvent.y = (int) event.getY(i);

                        touchEventsBuffer.add(touchEvent);
                        break;
                }
            }

            return true;
        }
    }

    public List<TouchEvent> getTouchEvents() {
        synchronized(this) {
            int lenght = touchEvents.size();

            for (int i = 0; i < lenght; i++) {
                touchEventPool.free(touchEvents.get(i));
            }

            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }


}
