package com.ckenken.asus.hw3.com.ckenken.asus.main.hw3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class AlarmService extends Service {

    final public static int MISSION_SETUPDATE = 400;
    final public static int MISSION_MAINSTART = 300;
    final public static int MISSION_TURNON = 100;
    final public static int MISSION_TURNOFF = 200;

    final public static int NOTIFICATION_REGULAR = 1;
    final public static int NOTIFICATION_TEN = 2;

    public static ArrayList<Alarm> alarms = new ArrayList<Alarm>();
    public static ArrayList<AlarmManager> alarmManagers= new ArrayList<AlarmManager>();

    public AlarmService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Bundle b = intent.getExtras();

        int id = -1;
        switch (b.getInt("mission_id")) {
            case MISSION_MAINSTART:
                for(int i = 0; i<alarms.size(); i++) {
                    if (alarms.get(i).a_on) {
                        turnOnAlarm(i);
                    }
                }
                break;
            case MISSION_SETUPDATE:
                for(int i = 0; i<alarms.size(); i++) {
                    Calendar c = Calendar.getInstance();
                    boolean flag = false;

                    for(int j = 0; j<alarms.size(); j++) {
                        if(alarms.get(j).repeat[j]) {
                            flag = true;
                            break;
                        }
                    }

                    if (alarms.get(i).a_on && ((alarms.get(i).repeat[c.get(Calendar.DAY_OF_WEEK)]) || !flag)) {
                        turnOffAlarm(i);
                        turnOnAlarm(i);
                    }
                }
                break;
            case MISSION_TURNON:
                id = b.getInt("alarm_id");
                if (alarms.size() > 0 && alarms.get(id).a_on) {
                    turnOnAlarm(id);
                }
                break;
            case MISSION_TURNOFF:
                id = b.getInt("alarm_id");
                if (alarms.size() > 0) {
                    turnOffAlarm(id);
                }
                break;
            default:
                break;
        }


        return START_REDELIVER_INTENT;
    }

    public void turnOnAlarm(int id)
    {
        Intent intent = new Intent(AlarmService.this, NotificationActivity.class);

        Bundle b = new Bundle();
        b.putInt("alarm_id", id);
        b.putInt("hour", alarms.get(id).hour);
        b.putInt("min", alarms.get(id).min);
        b.putString("ringtone", alarms.get(id).ringtone.toString());
        b.putInt("type", AlarmService.NOTIFICATION_REGULAR);
        intent.putExtras(b);

        Calendar c = Calendar.getInstance();

        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), alarms.get(id).hour, alarms.get(id).min, 0);

        if (checkBefore(c)) {
            c.add(Calendar.DATE, 1);
        }

        Bundle testB = intent.getExtras();

        Log.d("intent_putHour:", Integer.toString(testB.getInt("hour")));
        Log.d("intent_putMin", Integer.toString(testB.getInt("min")));

      //  final int _id = (int) System.currentTimeMillis();
        final int _id = id;

        Log.d("turn_on, time:", Integer.toString(alarms.get(id).hour) + ":" + Integer.toString(alarms.get(id).min));
        PendingIntent pi = PendingIntent.getActivity(AlarmService.this, _id, intent, 0);

        alarms.get(id).setPendingPi(pi);

        alarmManagers.get(id).set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
    }

    public boolean checkBefore(Calendar c)
    {
        if (c.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void turnOffAlarm(int id) {
        alarmManagers.get(id).cancel(alarms.get(id).pi);
    }

    public static void restoreAlarms(String restoreData) throws JSONException {

        Log.d("restoreData: ", restoreData);

        JSONArray ja = new JSONArray(restoreData);

        for(int i = 0; i<ja.length(); i++) {
            JSONObject j = ja.getJSONObject(i);

            Alarm al = new Alarm(j.getInt("id"), j.getBoolean("a_on"), j.getInt("hour"), j.getInt("min"), MainActivity.AM);

            al.ringtone = Uri.parse(j.getString("ringtone"));

            for(int k = 0; k<j.getJSONArray("repeat").length(); k++) {
                al.repeat[k] = j.getJSONArray("repeat").getJSONArray(0).getBoolean(k);
            }

            alarms.add(al);
        }
    }

    public static String getSaveAlarmsString() throws JSONException {
        JSONArray ja = new JSONArray();

        for(int i = 0; i<alarms.size(); i++) {
            Alarm al = alarms.get(i);

            JSONObject j = new JSONObject();
            j.put("id", i);
            j.put("a_on", al.a_on);
            j.put("hour", al.hour);
            j.put("min", al.min);
            j.put("ringtone", al.ringtone.toString());
            j.put("repeat", new JSONArray(Arrays.asList(al.repeat)));
      //      j.put("repeat", Arrays.asList(al.repeat));

            ja.put(j);
        }
        Log.d("ja.toString:", ja.toString());
        return ja.toString();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
