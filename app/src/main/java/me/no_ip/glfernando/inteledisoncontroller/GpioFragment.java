package me.no_ip.glfernando.inteledisoncontroller;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernando on 3/12/15.
 * Gpio Fragment
 */
public class GpioFragment extends Fragment implements GpioRecyclerAdapter.TouchListener {
    private static final String TAG = "IntelEdisonController";
    MediaPlayer mPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gpio, container, false);

        //setup RecyclerView
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.gpio_recyclerview);
        rv.setHasFixedSize(true);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(lm);

        GpioRecyclerAdapter a = new GpioRecyclerAdapter();
        rv.setAdapter(a);
        a.setTouchListener(this);

        // init gpio list TODO: is it really needed? revisit
        List<Gpio> list = new ArrayList<>();

        list.add(new Gpio(100, 1, true, 1));
        list.add(new Gpio(101, 2, true, 1));

        a.updateList(list);

        //Setup push button sound
        mPlayer = MediaPlayer.create(getActivity(), R.raw.push_button_down);
        return v;
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
}
