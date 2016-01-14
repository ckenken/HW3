package com.ckenken.asus.hw3.com.ckenken.asus.main.hw3;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ckenken.asus.hw3.R;
import com.ckenken.asus.hw3.com.ckenken.asus.fragment.hw3.InputAlarmFragment;
import com.ckenken.asus.hw3.com.ckenken.asus.fragment.hw3.OpeningFragment;
import com.ckenken.asus.hw3.com.ckenken.asus.fragment.hw3.SetAlarmFragment;

public class MainActivity extends AppCompatActivity {

    public static final int REFRESH = 1;
    public static final int AM = 0;
    public static final int PM = 1;
    public static boolean ALARM_ON = true;
    public static boolean ALARM_OFF = false;
    public static final String NO_OLD_DATA= "__NO_SAVE";
    public static final String OLD_ALARMS_DATA = "com.ckenken.old.alarms.data";

    public static int selectedId;

    InputAlarmFragment inputAlarmFragment;
    SetAlarmFragment setAlarmFragment;
    OpeningFragment openingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        if (findViewById(R.id.frameLayout0) != null) {  // small
            Intent intent = new Intent(MainActivity.this, OpeningActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
        else if (findViewById(R.id.frameLayout1) != null) {  // big land
            openingFragment = new OpeningFragment();
            setAlarmFragment = new SetAlarmFragment();

            FragmentTransaction ft;
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout1, openingFragment, "OP");
            ft.addToBackStack(null);
            ft.commit();

            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout2, setAlarmFragment, "SetAlarm");
            ft.addToBackStack(null);
            ft.commit();

        }
        else if (findViewById(R.id.frameLayout3) != null) {  // big port
            openingFragment = new OpeningFragment();
            setAlarmFragment = new SetAlarmFragment();

            FragmentTransaction ft;
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout3, openingFragment, "OP");
            ft.addToBackStack(null);
            ft.commit();

            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout4, setAlarmFragment, "SetAlarm");
            ft.addToBackStack(null);
            ft.commit();
        }
        else {
            Log.e("Error:", "Startining error!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
