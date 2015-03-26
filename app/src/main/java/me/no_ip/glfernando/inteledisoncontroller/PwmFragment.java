package me.no_ip.glfernando.inteledisoncontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernando on 3/24/15.
 */
public class PwmFragment extends Fragment implements PwmRecyclerAdapter.ProgressChangedListener {
    private static final String TAG = "PwmFragment";
    private static final String PWM_LIST = "me.noip.glfernando.pwm.list";
    private static final int REQUEST_PWM = 1;
    private PwmRecyclerAdapter mAdapter;
    private IntelEdisonComm mComm;
    private List<Pwm> mPwmList;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mComm = (IntelEdisonComm)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pwm, container, false);

        //setup RecyclerView
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.pwm_recyclerview);
        rv.setHasFixedSize(true);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(lm);

        mAdapter = new PwmRecyclerAdapter();
        rv.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mPwmList = (List<Pwm>) savedInstanceState.getSerializable(PWM_LIST);
        } else {
            mPwmList = new ArrayList<>();
        }

        mAdapter.updateList(mPwmList);
        //mAdapter.addItem(new Pwm(14, 20000, (float) 0.5));
        mAdapter.setProgressChangedListener(this);

        //Attach FAB to RecyclerView
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.attachToRecyclerView(rv);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPwm();
            }
        });

        return v;
    }

    private void addNewPwm() {
        AddPwmFragment add = new AddPwmFragment();
        add.setTargetFragment(PwmFragment.this, REQUEST_PWM);
        add.show(getFragmentManager(), "add_pwm");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_PWM) {
            int pwm = data.getIntExtra(AddPwmFragment.EXTRA_PWM_NUM, 0);
            int period = data.getIntExtra(AddPwmFragment.EXTRA_PWM_PERIOD, 20000);
            boolean servo = data.getBooleanExtra(AddPwmFragment.EXTRA_PWM_SERVO, false);
            mAdapter.addItem(new Pwm(pwm, period, servo, (float) 0.0));
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(PWM_LIST, (java.io.Serializable) mAdapter.getList());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, boolean fromUser, Pwm pwm) {
        float v;

        if (pwm.servo == true) {
            v = (float) (pwm.value / 20 + 0.05);
        } else {
            v = pwm.value;
        }
        Log.d(TAG, "{\"type\":\"pwm\", \"num\":" + pwm.pin + ",\"period\":" + pwm.period + ", \"value\":" + v + "}\n");
        mComm.sendData("{\"type\":\"pwm\", \"num\":" + pwm.pin + ",\"period\":" + pwm.period + ", \"value\":" + v + "}\n");
    }
}
