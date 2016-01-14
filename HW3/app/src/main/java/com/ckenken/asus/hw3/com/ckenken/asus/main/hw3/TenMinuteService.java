package com.ckenken.asus.hw3.com.ckenken.asus.main.hw3;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class TenMinuteService extends Service {

    public static int RING = 1;

    private int alarmId;
    private String mShowTime;

    public TenThread tenthread = new TenThread();
    public Thread t1;

    public TenMinuteService() {
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == RING) {
                tenthread.stopThread();
                if (t1.isAlive()) {
                    t1.interrupt();
                }
                Intent intent = new Intent(TenMinuteService.this, NotificationActivity.class);
                Bundle b = new Bundle();

                b.putInt("type", AlarmService.NOTIFICATION_TEN);
                b.putInt("alarm_id", alarmId);
                b.putString("showTime", mShowTime);

                intent.putExtras(b);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }

        }
    };

    class TenThread implements Runnable {

        private boolean isRunning = true;

        @Override
        public void run() {
            int counter = 0;
            while(isRunning) {
                counter++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (counter >= 600) { // 600
                    isRunning = false;
                    handler.obtainMessage(RING).sendToTarget();
                    break;
                }
            }
        }
        public void stopThread() {
            this.isRunning = false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Bundle b = intent.getExtras();

        alarmId = b.getInt("alarm_id");
        mShowTime = b.getString("showTime");

        t1 = new Thread(tenthread);
        t1.start();

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
