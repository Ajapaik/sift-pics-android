package pics.sift.app.widget.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnSwipeTouchListener implements View.OnTouchListener {
    private static final float SWIPE_DISTANCE_THRESHOLD = 100.0F;
    private static final float SWIPE_VELOCITY_THRESHOLD = 100.0F;

    private final GestureDetector m_gestureDetector;

    public OnSwipeTouchListener(Context context) {
        this(context, SWIPE_DISTANCE_THRESHOLD, SWIPE_VELOCITY_THRESHOLD);
    }

    public OnSwipeTouchListener(Context context, float distanceThreshold, float velocityThreshold) {
        m_gestureDetector = new GestureDetector(context, new GestureListener(distanceThreshold, velocityThreshold));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return m_gestureDetector.onTouchEvent(event);
    }

    public abstract void onSwipeLeft();
    public abstract void onSwipeRight();
    public void onSingleTap() { }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private float m_distanceThreshold;
        private float m_velocityThreshold;

        public GestureListener(float distanceThreshold, float velocityThreshold) {
            m_distanceThreshold = distanceThreshold;
            m_velocityThreshold = velocityThreshold;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();

            if(Math.abs(distanceX) > Math.abs(distanceY) &&
               Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD &&
               Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if(distanceX > 0.0F) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }

                return true;
            }

            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onSingleTap();

            return super.onSingleTapUp(e);
        }
    }
}
