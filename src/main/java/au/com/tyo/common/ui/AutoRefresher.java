package au.com.tyo.common.ui;

import android.os.Handler;
import android.util.Log;

public class AutoRefresher {

    private static final String LOG_TAG = "AutoRefresher";

    /**
     * index of current showing image
     */
    protected int current = -1;

    /**
     *
     */
    protected int timeout = -1;

    /**
     *
     */
    protected Handler handler;

    /**
     *
     */
    private OnRefreshStateListener listener;

    /**
     * The times of being refreshed in the current round
     */
    private int refreshTimes;

    /**
     * The total round number
     */
    private int round;

    /**
     *
     */
    private boolean pause;

    public AutoRefresher() {
        this(0);
    }

    public AutoRefresher(int timeout) {
        this.timeout = timeout;

        handler = null;

        listener = null;
        pause = false;
        refreshTimes = 0;
        round = 0;
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

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public interface OnRefreshStateListener {
        void onEachRoundFinished(long timeout);
        void update(int current);
    }

    public int getTimeout() {
        return timeout;
    }

    protected void update() {
        try {
            updateTarget(current);
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
            // set the current refresh times index
            ++current;
            if (refreshTimes > 0)
                current %= getRefreshTimes();

            try {
                onTimeout();

                if (timeout > 0)
                    refresh(timeout);
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Unrecoverable error:", ex);
            }
        }
    };

    public void onTimeout() throws Exception {
        updateTarget(current);
    }

    protected void updateTarget(int current) throws Exception {
        if (null != listener)
            listener.update(current);
    }

    public void setOnViewRefreshStateListener(OnRefreshStateListener listener) {
        this.listener = listener;
    }

    public OnRefreshStateListener getOnImageRefreshStateListner() {
        return listener;
    }

    protected void onRefreshRoundFinished() {
        onRefreshRoundFinished(0);
    }

    protected void onRefreshRoundFinished(long timeout) {
        if (null != listener)
            listener.onEachRoundFinished(timeout);
    }

    public void updateTarget() {
        if (getRefreshTimes() == 0)
            return;

        if (current < -1 && current >= getRefreshTimes())
            current = 0;

        update();
    }

    public void start() {
        refresh(timeout);
    }

    protected void refresh(long to) {
        if (!pause && null != handler)
            handler.postDelayed(runnable, to);
    }

    public void startRefreshing() {
        if (getRefreshTimes() > 0) {
            current = 0;

            try {
                updateTarget(current);
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
