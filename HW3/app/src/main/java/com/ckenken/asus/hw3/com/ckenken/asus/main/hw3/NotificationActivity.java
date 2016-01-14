package com.ckenken.asus.hw3.com.ckenken.asus.main.hw3;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ckenken.asus.hw3.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private Button mCloseButton;
    private Button mLaterButton;

    private TextView mTv;

    private int mAlarm_id;
    private int mHour;
    private int mMin;
    private Uri mRingtone;
    private String mShowTime;

    private SimpleDateFormat mAdf = new SimpleDateFormat("hh:mm aaa");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Bundle b = getIntent().getExtras();

        mAlarm_id = b.getInt("alarm_id");
        int type = b.getInt("type");
        mHour = b.getInt("hour");
        mMin = b.getInt("min");
        mRingtone = Uri.parse(b.getString("ringtone"));

        Log.d("m_Alarm_id:", Integer.toString(mAlarm_id));
        Log.d("type:", Integer.toString(type));
        Log.d("mHour:", String.valueOf(mHour));
        Log.d("mMin:", String.valueOf(mMin));

        mCloseButton = (Button)findViewById(R.id.button2);
        mLaterButton = (Button)findViewById(R.id.button4);
        mTv = (TextView) findViewById(R.id.textView5);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                for(int i = 0; i < AlarmService.alarms.get(mAlarm_id).repeat.length; i++) {
                    if (AlarmService.alarms.get(mAlarm_id).repeat[i]){
                        flag = true;
                        break;
                    }
                }
                AlarmService.alarms.get(mAlarm_id).a_on = flag;

                mp.stop();
                NotificationActivity.this.finish();
            }
        });

        mLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyServiceRunning(TenMinuteService.class)) {
                    stopService(new Intent(NotificationActivity.this, TenMinuteService.class));
                }

                mp.stop();

                Intent startIntent = new Intent(NotificationActivity.this, TenMinuteService.class);

                Bundle sendB = new Bundle();

                sendB.putInt("alarm_id", mAlarm_id);

                Calendar c = Calendar.getInstance();

                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), AlarmService.alarms.get(mAlarm_id).hour, AlarmService.alarms.get(mAlarm_id).min, 0);
                c.add(Calendar.MINUTE, 10);

                sendB.putString("showTime", mAdf.format(c.getTime()).toString());

                startIntent.putExtras(sendB);

                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startService(startIntent);
                NotificationActivity.this.finish();
            }
        });

        switch (type)
        {
            case AlarmService.NOTIFICATION_REGULAR:
//                mAlarm_id = b.getInt("alarm_id");
                Calendar c = Calendar.getInstance();

                if (AlarmService.alarms != null && AlarmService.alarms.size() > 0) {
                    c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), AlarmService.alarms.get(mAlarm_id).hour, AlarmService.alarms.get(mAlarm_id).min, 0);
                    //           c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), mHour, mMin, 0);
                }
                mTv.setText(mAdf.format(c.getTime()).toString());

                break;
            case AlarmService.NOTIFICATION_TEN:
//                mAlarm_id = b.getInt("alarm_id");
                mShowTime = b.getString("showTime");
                mTv.setText(mShowTime);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri notification = AlarmService.alarms.get(mAlarm_id).ringtone;

     //   Uri notification = mRingtone;

      //  Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        // r.play();
        mp = new MediaPlayer();

        try {
            mp.setDataSource(NotificationActivity.this, notification);
            mp.setAudioStreamType(AudioManager.STREAM_ALARM);
            mp.prepare();
            mp.setLooping(true);
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
