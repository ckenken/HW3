package com.ckenken.asus.hw3.com.ckenken.asus.main.hw3;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ckenken.asus.hw3.R;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SetAlarmActivity extends AppCompatActivity {

    private ListView mListView;

    private Button mNewAlarm;

    private AlarmAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
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

        adapter = new AlarmAdapter(SetAlarmActivity.this, AlarmService.alarms);

        mListView = (ListView)findViewById(R.id.listViewSetAlarm);
        mListView.setAdapter(adapter);
        mListView.setTextFilterEnabled(true);

        mNewAlarm = (Button)findViewById(R.id.button);

        mNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Alarm newAlarm = new Alarm(AlarmService.alarms.size(), MainActivity.ALARM_OFF, 0, 0, MainActivity.AM);
                AlarmService.alarms.add(newAlarm);
                AlarmService.alarmManagers.add((AlarmManager) getSystemService(ALARM_SERVICE));

                Log.d("newAlarm size:", Integer.toString(AlarmService.alarms.size()));
                Log.d("counter:", Integer.toString(mListView.getCount()));
 //
                adapter.notifyDataSetChanged();
                mListView.invalidateViews();

                try {
                    saveAlarms(AlarmService.getSaveAlarmsString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void saveAlarms(String jaString)
    {
        SharedPreferences sharedPref = SetAlarmActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.remove(getString(R.string.old_alarms_data));
        editor.putString(getString(R.string.old_alarms_data), jaString);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AlarmService.alarms.size() == 0) {
            AlarmService.alarms.add(new Alarm(0, MainActivity.ALARM_OFF, 11, 21, MainActivity.AM));
            AlarmService.alarmManagers.add((AlarmManager) getSystemService(ALARM_SERVICE));
        }
        mListView.invalidateViews();

        Intent startIntent = new Intent(SetAlarmActivity.this, AlarmService.class);
        Bundle b = new Bundle();
        b.putInt("mission_id", AlarmService.MISSION_SETUPDATE);
        startIntent.putExtras(b);
        startService(startIntent);
    }

    ///////////////////////////////  AlarmAdapter  //////////////////////////////////

    class AlarmAdapter extends BaseAdapter {

        private Context mContext;

        private LayoutInflater inflater = null;

        ArrayList<Alarm> alarms = new ArrayList<Alarm>();

        private SimpleDateFormat adf = new SimpleDateFormat("hh:mm aaa");

        public AlarmAdapter(Context context, ArrayList<Alarm> data) {
            // TODO Auto-generated constructor stub
            inflater = LayoutInflater.from(context);

            mContext = context;

            alarms = data;   // when alarms update in SetAlarmActivity, this alarm arrayList will update too.

//        for(int i = 0; i<data.size(); i++) {
//            alarms.add(data.get(i).copy());
//        }

        }

        @Override
        public int getCount() {
            return alarms.size();
        }

        @Override
        public Object getItem(int position) {
            return alarms.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = inflater.inflate(R.layout.alarm_list, null);

            CheckBox a_on = (CheckBox) v.findViewById(R.id.checkBox);
            TextView time = (TextView) v.findViewById(R.id.textView2);
            TextView repeat = (TextView) v.findViewById(R.id.textView4);

            a_on.setChecked(AlarmService.alarms.get(position).a_on);
            a_on.setOnCheckedChangeListener(new CheckOnChangeListener(position));

            Calendar c = Calendar.getInstance();
            c.set(2016, 0, 6, alarms.get(position).hour, alarms.get(position).min);
//        Log.d("alarms.time:", Integer.toString(alarms.get(position).hour) + "," + Integer.toString(alarms.get(position).min));

            time.setOnClickListener(new SetTimeOnClickListener(position));
            repeat.setOnClickListener(new SetTimeOnClickListener(position));

            Date da = c.getTime();
            time.setText(adf.format(da));
            repeat.setText(alarms.get(position).displayRepeat());

            return v;
        }

        class SetTimeOnClickListener implements View.OnClickListener {
            private int id;

            public SetTimeOnClickListener(int inputId)
            {
                id = inputId;
            }

            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(mContext, InputAlarmTimeActivity.class);
                Bundle b = new Bundle();
                b.putInt("alarm_id", id);
                startIntent.putExtras(b);
                mContext.startActivity(startIntent);
            }
        }

        class CheckOnChangeListener implements CompoundButton.OnCheckedChangeListener {
            private int id;
            public  CheckOnChangeListener(int inputId)
            {
                id = inputId;
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Log.d("on:", Integer.toString(id));
                    AlarmService.alarms.get(id).a_on = MainActivity.ALARM_ON;
                    Intent startIntent = new Intent(mContext, AlarmService.class);
                    Bundle b = new Bundle();
                    b.putInt("mission_id", AlarmService.MISSION_TURNON);
                    b.putInt("alarm_id", id);
                    startIntent.putExtras(b);
                    mContext.startService(startIntent);

                }
                else {

                    Log.d("off:", Integer.toString(id));
                    AlarmService.alarms.get(id).a_on = MainActivity.ALARM_OFF;
                    Intent startIntent = new Intent(mContext, AlarmService.class);
                    Bundle b = new Bundle();
                    b.putInt("mission_id", AlarmService.MISSION_TURNOFF);
                    b.putInt("alarm_id", id);
                    startIntent.putExtras(b);
                    mContext.startService(startIntent);

                }

                try {
                    saveAlarms(AlarmService.getSaveAlarmsString());
                    Log.d("in_Check, saveString:", AlarmService.getSaveAlarmsString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public void saveAlarms(String jaString)
        {
            SharedPreferences sharedPref = SetAlarmActivity.this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            //editor.remove(getString(R.string.old_alarms_data));

            Log.d("In_SaveAlarms1, jaStr:", jaString);

            editor.putString(mContext.getString(R.string.old_alarms_data), jaString);
            editor.commit();
        }
    }
    //////////  AlarmAdapter  /////////////

}
