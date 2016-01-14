package com.ckenken.asus.hw3.com.ckenken.asus.main.hw3;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ckenken.asus.hw3.R;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OpeningActivity extends AppCompatActivity {

    public static SimpleDateFormat sdf = new SimpleDateFormat("EEE, MM/dd hh:mm:ss aaa");

    private static TextView mLabel;

    private Button mSetAlarmButton;

    private Button mRemoveButton;

    private TimeThread mRun1 = new TimeThread();

    private Handler mhandler = new Handler();

    class TimeThread implements Runnable {

        private boolean isRunning = true;

        @Override
        public void run() {
            Calendar c = Calendar.getInstance();
            Date d = c.getTime();
            mLabel.setText(sdf.format(d));
            mhandler.postDelayed(this, 500);
        }

        public void stopThread() {
            this.isRunning = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
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

        mSetAlarmButton = (Button)findViewById(R.id.buttonOpenSetAlarm);
        mRemoveButton = (Button)findViewById(R.id.buttonOpenRemove);

        mLabel = (TextView)findViewById(R.id.textViewOpen);

        mSetAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(OpeningActivity.this, SetAlarmActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPref = OpeningActivity.this.getPreferences(Context.MODE_PRIVATE);
        String output = sharedPref.getString(getString(R.string.old_alarms_data), MainActivity.NO_OLD_DATA);

        Log.d("OnCreate, old data:", output);

        if (!output.equals(MainActivity.NO_OLD_DATA) && AlarmService.alarms.size() == 0) {
            try {
                AlarmService.restoreAlarms(output);
                for(int i = 0; i<AlarmService.alarms.size(); i++) {
                    AlarmService.alarmManagers.add((AlarmManager) getSystemService(ALARM_SERVICE));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPref = OpeningActivity.this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(getString(R.string.old_alarms_data));
                editor.commit();
                AlarmService.alarms = new ArrayList<Alarm>();
                System.gc();

            }
        });

        Intent startIntent = new Intent(OpeningActivity.this, AlarmService.class);
        Bundle b = new Bundle();
        b.putInt("mission_id", AlarmService.MISSION_MAINSTART);
        startIntent.putExtras(b);
        startService(startIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mhandler.removeCallbacks(mRun1);

        try {
            saveAlarms(AlarmService.getSaveAlarmsString());
            Log.d("in_Check, saveString:", AlarmService.getSaveAlarmsString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mhandler.postDelayed(mRun1, 500);
    }


    public void saveAlarms(String jaString)
    {
        SharedPreferences sharedPref = OpeningActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.remove(getString(R.string.old_alarms_data));

        Log.d("Open_SaveAlarms2,jaStr:", jaString);

        editor.putString(getString(R.string.old_alarms_data), jaString);
        editor.commit();
    }

}
