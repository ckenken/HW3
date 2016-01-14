package com.ckenken.asus.hw3.com.ckenken.asus.fragment.hw3;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import com.ckenken.asus.hw3.com.ckenken.asus.main.hw3.Alarm;
import com.ckenken.asus.hw3.com.ckenken.asus.main.hw3.AlarmService;
import com.ckenken.asus.hw3.com.ckenken.asus.main.hw3.MainActivity;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetAlarmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SetAlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetAlarmFragment extends Fragment {

    private ListView mListView;

    private Button mNewAlarm;

    private AlarmAdapter adapter;

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
     * @return A new instance of fragment SetAlarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetAlarmFragment newInstance(String param1, String param2) {
        SetAlarmFragment fragment = new SetAlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SetAlarmFragment() {
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
        return inflater.inflate(R.layout.fragment_set_alarm, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter = new AlarmAdapter(getActivity(), AlarmService.alarms);

        mListView = (ListView)getActivity().findViewById(R.id.listViewSetAlarmFragment);
        mListView.setAdapter(adapter);
        mListView.setTextFilterEnabled(true);

        mNewAlarm = (Button)getActivity().findViewById(R.id.buttonNewAlarmFrag);

        mNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Alarm newAlarm = new Alarm(AlarmService.alarms.size(), MainActivity.ALARM_OFF, 0, 0, MainActivity.AM);
                AlarmService.alarms.add(newAlarm);
                AlarmService.alarmManagers.add((AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE));

                Log.d("newAlarm size:", Integer.toString(AlarmService.alarms.size()));
                Log.d("counter:", Integer.toString(mListView.getCount()));
                //
                adapter.notifyDataSetChanged();
                mListView.invalidateViews();

//                try {
//                    saveAlarms(AlarmService.getSaveAlarmsString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });



    }
//    @Override
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


    @Override
    public void onResume() {
        super.onResume();
        if (AlarmService.alarms.size() == 0) {
            AlarmService.alarms.add(new Alarm(0, MainActivity.ALARM_OFF, 11, 21, MainActivity.AM));
            AlarmService.alarmManagers.add((AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE));
        }
        mListView.invalidateViews();

        Intent startIntent = new Intent(getActivity(), AlarmService.class);
        Bundle b = new Bundle();
        b.putInt("mission_id", AlarmService.MISSION_SETUPDATE);
        startIntent.putExtras(b);
        getActivity().startService(startIntent);
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
            if (position < AlarmService.alarms.size()) {
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
            }

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
//                Intent startIntent = new Intent(mContext, InputAlarmTimeActivity.class);
//                Bundle b = new Bundle();
//                b.putInt("alarm_id", id);
//                startIntent.putExtras(b);
//                mContext.startActivity(startIntent);

                if (getActivity().findViewById(R.id.frameLayout2) != null) {
                    FragmentTransaction ft;
                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayout2, new InputAlarmFragment(), "Input");
                    ft.addToBackStack(null);
                    ft.commit();
                }
                else if (getActivity().findViewById(R.id.frameLayout4) != null) {
                    FragmentTransaction ft;
                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayout4, new InputAlarmFragment(), "Input");
                    ft.addToBackStack(null);
                    ft.commit();
                }

                MainActivity.selectedId = id;
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
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            //editor.remove(getString(R.string.old_alarms_data));

            Log.d("In_SaveAlarms1, jaStr:", jaString);

            editor.putString(mContext.getString(R.string.old_alarms_data), jaString);
            editor.commit();
        }
    }
    //////////  AlarmAdapter  /////////////

}
