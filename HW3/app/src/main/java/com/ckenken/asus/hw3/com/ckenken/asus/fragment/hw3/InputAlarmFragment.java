package com.ckenken.asus.hw3.com.ckenken.asus.fragment.hw3;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.ckenken.asus.hw3.R;
import com.ckenken.asus.hw3.com.ckenken.asus.main.hw3.AlarmService;
import com.ckenken.asus.hw3.com.ckenken.asus.main.hw3.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InputAlarmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InputAlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputAlarmFragment extends Fragment {

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

    private Button ok;

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
     * @return A new instance of fragment InputAlarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InputAlarmFragment newInstance(String param1, String param2) {
        InputAlarmFragment fragment = new InputAlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InputAlarmFragment() {
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
        View view = inflater.inflate(R.layout.fragment_input_alarm, container, false);

        mTime = (EditText) view.findViewById(R.id.editText);
        mRepeat = (EditText) view.findViewById(R.id.editText2);
        mRingtone = (EditText) view.findViewById(R.id.editText3);

        mTime.setKeyListener(null);
        mRepeat.setKeyListener(null);
        mRingtone.setKeyListener(null);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Intent startIntent = new Intent(getActivity(), AlarmService.class);
        Bundle b = new Bundle();
        b.putInt("mission_id", AlarmService.MISSION_SETUPDATE);
        startIntent.putExtras(b);
        getActivity().startService(startIntent);
    }

    @Override
    public void onStart() {
        super.onStart();



        ok = (Button) getActivity().findViewById(R.id.buttonInputOK);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().findViewById(R.id.frameLayout2) != null) {
                    FragmentTransaction ft;
                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayout2, new SetAlarmFragment(), "SetAlarm");
                    ft.addToBackStack(null);
                    ft.commit();
                } else if (getActivity().findViewById(R.id.frameLayout4) != null) {
                    FragmentTransaction ft;
                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayout4, new SetAlarmFragment(), "SetAlarm");
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });


//        Bundle b = getActivity().getIntent().getExtras();
//
//        final int alarm_id = b.getInt("alarm_id");
        final int alarm_id = MainActivity.selectedId;
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

        ad = new AlertDialog.Builder(getActivity());
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

        final Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), AlarmService.alarms.get(alarm_id).ringtone);
        mRingtone.setText(ringtone.getTitle(getActivity()).toString());

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == getActivity().RESULT_OK) {
            final Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            final Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
            // Get your title here `ringtone.getTitle(this)`
            AlarmService.alarms.get(alarmId).setRingtone(uri);

            mRingtone.setText(RingtoneManager.getRingtone(getActivity(), AlarmService.alarms.get(alarmId).ringtone).getTitle(getActivity()).toString());
        }
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

    public void showTimePickerDialog(final int alarm_id) {
        // 設定初始時間
        Calendar c = Calendar.getInstance();
        mHour = AlarmService.alarms.get(alarm_id).hour;
        mMin = AlarmService.alarms.get(alarm_id).min;

        // 跳出時間選擇器
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
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

}
