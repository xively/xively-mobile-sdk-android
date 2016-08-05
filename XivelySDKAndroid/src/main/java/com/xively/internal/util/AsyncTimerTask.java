package com.xively.internal.util;

import android.os.AsyncTask;
import android.os.Looper;

public class AsyncTimerTask {

    private Runnable postExecuteRunnable;
    private long timerMillis;
    private TimerTask timerTask;

    public AsyncTimerTask(final long timerMillis, final Runnable onPostExecute)
            throws IllegalThreadStateException {

        if (Looper.myLooper() != Looper.getMainLooper()){
            throw new IllegalThreadStateException("Must be run on ui thread!");
        }

        this.postExecuteRunnable = onPostExecute;
        this.timerMillis = timerMillis;

        timerTask = new TimerTask();
    }

    public void execute(){
        timerTask.execute(timerMillis);
    }

    public void cancel(){
        if (timerTask != null) {
            timerTask.cancel(true);
        }
    }

    private void runPostExecuteRunnable(){
        if (postExecuteRunnable != null){
            postExecuteRunnable.run();
        }
    }

    private class TimerTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... params) {
            try {
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isCancelled()){
                runPostExecuteRunnable();
            }
        }
    }

}
