package me.no_ip.glfernando.inteledisoncontroller;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static me.no_ip.glfernando.inteledisoncontroller.AddGpioFragment.*;

/**
 * Created by fernando on 3/12/15.
 * Gpio Fragment
 */
public class GpioFragment extends Fragment implements GpioRecyclerAdapter.TouchListener {
    private static final String TAG = "IntelEdisonController";
    private static final int REQUEST_GPIO = 0;
    MediaPlayer mPlayer;
    GpioRecyclerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gpio, container, false);

        //setup RecyclerView
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.gpio_recyclerview);
        rv.setHasFixedSize(true);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(lm);

        mAdapter = new GpioRecyclerAdapter();
        rv.setAdapter(mAdapter);
        mAdapter.setTouchListener(this);

        // init gpio list TODO: is it really needed? revisit
        List<Gpio> list = new ArrayList<>();
        mAdapter.updateList(list);

        //Setup push button sound
        mPlayer = MediaPlayer.create(getActivity(), R.raw.push_button_down);

        //Attach FAB to RecyclerView
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.attachToRecyclerView(rv);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "FAB clicked", Toast.LENGTH_SHORT).show();
                addNewGpio();
            }
        });

        return v;
    }

    private void addNewGpio() {
        AddGpioFragment add = new AddGpioFragment();
        add.setTargetFragment(GpioFragment.this, REQUEST_GPIO);
        add.show(getActivity().getSupportFragmentManager(), "add_gpio");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event, Gpio gpio) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "action down gpio " + gpio.gpio);
                mPlayer.start();
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "action up gpio " + gpio.gpio);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_GPIO) {
            int gpio = data.getIntExtra(EXTRA_GPIO_NUM, 0);
            mAdapter.addItem(new Gpio(gpio, gpio, true, 0));
        }


    }
}
