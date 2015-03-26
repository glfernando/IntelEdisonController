package me.no_ip.glfernando.inteledisoncontroller;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by fernando on 3/24/15.
 */
public class PwmRecyclerAdapter extends RecyclerView.Adapter<PwmRecyclerAdapter.ViewHolder> {
    private static final String TAG = "PwmRecyclerAdapter";
    private List<Pwm> mPwmList = Collections.emptyList();
    private ProgressChangedListener mListener;

    public interface ProgressChangedListener {
        public void onProgressChanged(SeekBar seekBar, boolean fromUser, Pwm pwm);
    }

    public void setProgressChangedListener(ProgressChangedListener l) {
        mListener = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_pwm, parent, false);

        return new ViewHolder(v, mPwmList);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pwm pwm = mPwmList.get(position);
        holder.text.setText("PIN" + pwm.pin);
        holder.seek.setProgress((int) (mPwmList.get(position).value * 100));
        holder.setProgressChangedListener(new ViewHolder.ProgressChangedListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser, int position) {
                if (mListener != null) {
                    Pwm pwm = mPwmList.get(position);
                    pwm.value = (float)progress/100;
                    mListener.onProgressChanged(seekBar, fromUser, pwm);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPwmList.size();
    }

    public void updateList(List<Pwm> pwms) {
        mPwmList = pwms;
        notifyDataSetChanged();
    }

    public List<Pwm> getList() {
        return mPwmList;
    }

    public void addItem(int p, Pwm pwm) {
        mPwmList.add(p, pwm);
        notifyItemInserted(p);
    }

    public void addItem(Pwm pwm) {
        addItem(mPwmList.size(), pwm);
    }

    public void removeItem(int p) {
        mPwmList.remove(p);
        notifyItemRemoved(p);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements SeekBar.OnSeekBarChangeListener {
        public TextView text;
        public SeekBar seek;
        private ProgressChangedListener mListener;

        public ViewHolder(View itemView, List<Pwm> l) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.pwm_textView);
            seek = (SeekBar) itemView.findViewById(R.id.pwm_seekBar);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mListener.onProgressChanged(seekBar, progress, fromUser, getPosition());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        public interface ProgressChangedListener {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser, int position);
        }

        public void setProgressChangedListener(ProgressChangedListener l) {
            mListener = l;
            seek.setOnSeekBarChangeListener(this);
        }
    }
}
