package com.ckenken.asus.hw3.com.ckenken.asus.main.hw3;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ckenken.asus.hw3.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InputAlarmTimeActivity extends AppCompatActivity {

    private TextView mTimeText;
    private TextView mRepeatText;
    private TextView mRingtoneText;

    private EditText mTime;
    private EditText mRepeat;
    private EditText mRingtone;

    private SimpleDateFormat mAdf = new SimpleDateFormat("hh:mm aaa");
    private SimpleDateFormat mWeekdateformat = new SimpleDateFormat("EEE");

    private int mHour;
    private int mMin;

    private AlertDialog.Builder ad;
    private String [] mWeek;
    private boolean [] mTempRepeatChoice = new boolean[7];
    private boolean [] mOriginalRepeatChoice = new boolean[7];

    private int alarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_alarm_time);
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

        Log.d("language:" , Locale.getDefault().getDisplayLanguage());

        if (Locale.getDefault().getDisplayLanguage().toString().equals("中文")) {
            setTitle("設定時間");
        }
        else {
            setTitle("Set Time");
        }

        mTimeText = (TextView)findViewById(R.id.textView6);
        mRepeatText = (TextView)findViewById(R.id.textView7);
        mRingtoneText = (TextView)findViewById(R.id.textView8);

        mTime = (EditText) findViewById(R.id.editText4);
        mRepeat = (EditText) findViewById(R.id.editText5);
        mRingtone = (EditText) findViewById(R.id.editText6);

        mTime.setKeyListener(null);
        mRepeat.setKeyListener(null);
        mRingtone.setKeyListener(null);

        Bundle b = getIntent().getExtras();

        final int alarm_id = b.getInt("alarm_id");
        alarmId = alarm_id;

        int hour = AlarmService.alarms.get(alarm_id).hour;
        int min = AlarmService.alarms.get(alarm_id).min;

        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hour, min, 0);

        Date da = c.getTime();

        mTime.setText(mAdf.format(da).toString());

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(alarm_id);
            }
        });

        mRepeat.setText(AlarmService.alarms.get(alarm_id).displayRepeat());

        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.show();
            }
        });

        ad = new AlertDialog.Builder(this);
        ad.setTitle("Repeat");

        for(int i = 0; i<7; i++) {
            mTempRepeatChoice[i] = AlarmService.alarms.get(alarm_id).repeat[i];
            mOriginalRepeatChoice[i] = AlarmService.alarms.get(alarm_id).repeat[i];
        }

        mWeek = initMultiWeek();

        ad.setMultiChoiceItems(mWeek, AlarmService.alarms.get(alarm_id).repeat, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if (isChecked) {
                    mTempRepeatChoice[which] = true;
                }
                else {
                    mTempRepeatChoice[which] = false;
                }
            }
        });

        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < 7; i++) {
                    AlarmService.alarms.get(alarm_id).repeat[i] = mTempRepeatChoice[i];
                    mOriginalRepeatChoice[i] = mTempRepeatChoice[i];
                }
                mRepeat.setText(AlarmService.alarms.get(alarm_id).displayRepeat());
            }
        });

        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < 7; i++) {
                    AlarmService.alarms.get(alarm_id).repeat[i] = mOriginalRepeatChoice[i];
                }
                mRepeat.setText(AlarmService.alarms.get(alarm_id).displayRepeat());
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ad.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    for (int i = 0; i < 7; i++) {
                        AlarmService.alarms.get(alarm_id).repeat[i] = mOriginalRepeatChoice[i];
                    }

                }
            });
        }

        final Ringtone ringtone = RingtoneManager.getRingtone(this, AlarmService.alarms.get(alarm_id).ringtone);
        mRingtone.setText(ringtone.getTitle(this).toString());

        mRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent ringtone = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                startActivityForResult(ringtone, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            final Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            final Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            // Get your title here `ringtone.getTitle(this)`
            AlarmService.alarms.get(alarmId).setRingtone(uri);

            mRingtone.setText(RingtoneManager.getRingtone(this, AlarmService.alarms.get(alarmId).ringtone).getTitle(this).toString());
        }
    }

    public void showTimePickerDialog(final int alarm_id) {
        // 設定初始時間
        Calendar c = Calendar.getInstance();
        mHour = AlarmService.alarms.get(alarm_id).hour;
        mMin = AlarmService.alarms.get(alarm_id).min;

        // 跳出時間選擇器
        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                // 完成選擇，顯示時間
                //tvTime.setText(hourOfDay + ":" + minute);

                AlarmService.alarms.get(alarm_id).hour = hourOfDay;
                AlarmService.alarms.get(alarm_id).min = minute;

                Calendar cAfter = Calendar.getInstance();
                cAfter.set(cAfter.get(Calendar.YEAR), cAfter.get(Calendar.MONTH), cAfter.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);

                mTime.setText(mAdf.format(cAfter.getTime()).toString());
            }
        }, mHour, mMin, false);
        tpd.show();
    }

    public String [] initMultiWeek()
    {
        String [] temp = new String [7];

        Calendar c = Calendar.getInstance();
        c.set(2016, 0, 4, 12, 33, 0);
        Date d = c.getTime();
        temp[0] = mWeekdateformat.format(d).toString();
        c.set(2016, 0, 5, 12, 33, 0);
        d = c.getTime();
        temp[1] = mWeekdateformat.format(d).toString();
        c.set(2016, 0, 6, 12, 33, 0);
        d = c.getTime();
        temp[2] = mWeekdateformat.format(d).toString();
        c.set(2016, 0, 7, 12, 33, 0);
        d = c.getTime();
        temp[3] = mWeekdateformat.format(d).toString();
        c.set(2016, 0, 8, 12, 33, 0);
        d = c.getTime();
        temp[4] = mWeekdateformat.format(d).toString();
        c.set(2016, 0, 9, 12, 33, 0);
        d = c.getTime();
        temp[5] = mWeekdateformat.format(d).toString();
        c.set(2016, 0, 10, 12, 33, 0);
        d = c.getTime();
        temp[6] = mWeekdateformat.format(d).toString();

        return temp;
    }

    public boolean [] initBool()
    {
        boolean [] f = new boolean[7];
        for(int i = 0; i<7; i++) {
            f[i] = false;
        }
        return f;
    }

}
