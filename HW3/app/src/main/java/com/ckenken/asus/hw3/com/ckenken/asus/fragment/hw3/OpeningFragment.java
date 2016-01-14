package com.ckenken.asus.hw3.com.ckenken.asus.fragment.hw3;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ckenken.asus.hw3.R;
import com.ckenken.asus.hw3.com.ckenken.asus.main.hw3.Alarm;
import com.ckenken.asus.hw3.com.ckenken.asus.main.hw3.AlarmService;
import com.ckenken.asus.hw3.com.ckenken.asus.main.hw3.MainActivity;
import com.ckenken.asus.hw3.com.ckenken.asus.main.hw3.SetAlarmActivity;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OpeningFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OpeningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpeningFragment extends Fragment {

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OpeningFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OpeningFragment newInstance(String param1, String param2) {
        OpeningFragment fragment = new OpeningFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public OpeningFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opening, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override

    @Override
    public void onStart() {
        super.onStart();

        mSetAlarmButton = (Button)getActivity(). findViewById(R.id.buttonGoToSetAlarm);

        mLabel = (TextView)getActivity().findViewById(R.id.textViewFragmentOpen);

        mSetAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SetAlarmActivity.class);
                startActivity(intent);
            }
        });

        mSetAlarmButton.setVisibility(View.INVISIBLE);
        mRemoveButton = (Button)getActivity().findViewById(R.id.buttonRemoveAlarms);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String output = sharedPref.getString(getString(R.string.old_alarms_data), MainActivity.NO_OLD_DATA);

        Log.d("OnCreate, old data:", output);

        if (!output.equals(MainActivity.NO_OLD_DATA) && AlarmService.alarms.size() == 0) {
            try {
                AlarmService.restoreAlarms(output);
                for(int i = 0; i<AlarmService.alarms.size(); i++) {
                    AlarmService.alarmManagers.add((AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(getString(R.string.old_alarms_data));
                editor.commit();
                AlarmService.alarms = new ArrayList<Alarm>();
                System.gc();

                FragmentTransaction ft;
                ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.frameLayout4, new SetAlarmFragment(), "SetAlarm");
                ft.detach(getFragmentManager().findFragmentByTag("SetAlarm"));
                ft.attach(getFragmentManager().findFragmentByTag("SetAlarm"));
                //ft.addToBackStack(null);
                ft.commit();
            }
        });

        Intent startIntent = new Intent(getActivity(), AlarmService.class);
        Bundle b = new Bundle();
        b.putInt("mission_id", AlarmService.MISSION_MAINSTART);
        startIntent.putExtras(b);
        getActivity().startService(startIntent);

    }

    @Override
    public void onResume() {
        super.onResume();
        mhandler.postDelayed(mRun1, 500);
    }

//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
