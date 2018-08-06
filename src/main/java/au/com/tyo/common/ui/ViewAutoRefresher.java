package au.com.tyo.common.ui;

import android.os.Handler;
import android.util.Log;

public class ViewAutoRefresher {

    private static final String LOG_TAG = "ViewAutoRefresher";

    /**
     * index of current showing image
     */
    protected int current = -1;

    /**
     *
     */
    protected int timeout = -1;

    protected Handler handler;

    private OnImageRefreshStateListener listener;

    /**
     *
     */
    private int refreshTimes;

    private boolean pause;

    public ViewAutoRefresher() {
        handler = null;
        listener = null;
        pause = false;
        timeout = 0;
        refreshTimes = 0;
    }

    public synchronized boolean isPause() {
        return pause;
    }

    public synchronized void setPause(boolean pause) {
        this.pause = pause;
    }

    public int getRefreshTimes() {
        return refreshTimes;
    }

    public void setRefreshTimes(int refreshTimes) {
        this.refreshTimes = refreshTimes;
    }

    public interface OnImageRefreshStateListener {
        void onEachRoundFinished(long timeout);
        void update(int current);
    }

    public int getTimeout() {
        return timeout;
    }

    protected void update() {
        try {
            updateImage(current);
        }
        catch (Exception ex) { Log.e(LOG_TAG, "error in updating the image", ex); }
    }

    /**
     *
     * @param timeout, in millisecond
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ++current;
            current %= getRefreshTimes();
            try {
                if (null != handler) {
                    updateImage(current);

                    refresh(timeout);
                }
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Unrecoverable error:", ex);
            }
        }
    };

    protected void updateImage(int current) throws Exception {
        if (null != listener)
            listener.update(current);
    }

    public void setOnImageRefreshStateListener(OnImageRefreshStateListener listener) {
        this.listener = listener;
    }

    public OnImageRefreshStateListener getOnImageRefreshStateListner() {
        return listener;
    }

    protected void onRefreshRoundFinished() {
        onRefreshRoundFinished(0);
    }

    protected void onRefreshRoundFinished(long timeout) {
        if (null != listener)
            listener.onEachRoundFinished(timeout);
    }

    public void updateImage() {
        if (getRefreshTimes() == 0)
            return;

        if (current < -1 && current >= getRefreshTimes())
            current = 0;

        update();
    }

    protected void refresh(long to) {
        if (!pause && null != handler)
            handler.postDelayed(runnable, to);
    }

    public void start() {
        if (getRefreshTimes() > 0) {
            current = 0;

            if (getRefreshTimes() > 1) {
                handler = new Handler();
            }

            try {
                updateImage(current);
            } catch (Exception e) {
                Log.e(LOG_TAG, "auto refresher failed to update view", e);
            }
        }
        else {
            onRefreshRoundFinished();
        }
    }

    public void pause() {
        setPause(true);
    }
}
