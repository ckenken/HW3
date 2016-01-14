package com.ckenken.asus.hw3.com.ckenken.asus.main.hw3;

import android.app.PendingIntent;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by ckenken on 2016/1/6.
 */
public class Alarm {
    public int id;
    public boolean a_on;
    public int hour;
    public int min;
    //int ampm;
//    ArrayList<Integer> repeat;
    public boolean [] repeat;

    public Uri ringtone;

    public PendingIntent pi;

    private SimpleDateFormat mWeekdateformat = new SimpleDateFormat("EEE");

    public Alarm()
    {
        ringtone = Uri.parse("content:\\/\\/media\\/internal\\/audio\\/media\\/34");
    }

    public Alarm(int inputId, boolean inputA_ON, int inputHour, int inputMin, int inputAMPM)
    {
        id = inputId;
        a_on = inputA_ON;
        hour = inputHour;
        min = inputMin;
   //     ampm = inputAMPM;
        repeat = new boolean[7];
        ringtone = Uri.parse("content://media/internal/audio/media/46");
    }

    public void setPendingPi(PendingIntent inputPi)
    {
        this.pi = inputPi;
    }

    public Alarm copy()
    {
        Alarm temp = new Alarm();

        temp.a_on = this.a_on;
        temp.hour = this.hour;
        temp.min = this.min;
     //   temp.ampm = this.ampm;

        temp.repeat = new boolean[7];

        for(int i = 0; i<7; i++) {
            temp.repeat[i] = this.repeat[i];
        }

        return temp;
    }

    public void setRingtone(Uri uri)
    {
        this.ringtone = uri;
    }

    public String displayRepeat()
    {

        if (Locale.getDefault().getDisplayLanguage().equals("中文")) {

        }
        StringBuilder SB = new StringBuilder();

        int counter = 0;

        Calendar c = Calendar.getInstance();

        for(int i =0; i<this.repeat.length; i++) {
            if (this.repeat[i]) {
                c.set(2016, 0, i+4, 0, 0, 0);
                if (counter == 0) {
                    SB.append(mWeekdateformat.format(c.getTime()).toString());
                }
                else {
                    SB.append(", " + mWeekdateformat.format(c.getTime()).toString());
                }
                counter++;
            }
        }
        if (counter == 0) {
            if (Locale.getDefault().getDisplayLanguage().equals("中文")) {
                return "不重複播放";
            }
            else {
                return "No Repeat";
            }
        }
        else if (counter >= 7) {
            if (Locale.getDefault().getDisplayLanguage().equals("中文")) {
                return "每天";
            }
            else {
                return "everyday";
            }
        }
        else if (this.repeat[5] && this.repeat[6] && counter == 2) {
            if (Locale.getDefault().getDisplayLanguage().equals("中文")) {
                return "週末";
            }
            else {
                return "Weekend";
            }
        }
        else if (this.repeat[0] && this.repeat[1] && this.repeat[2] && this.repeat[3] && this.repeat[4] && counter == 5) {
            if (Locale.getDefault().getDisplayLanguage().equals("中文")) {
                return "工作天";
            }
            else {
                return "Week day";
            }
        }
        else {
            return SB.toString();
        }
    }

}
