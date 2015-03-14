package me.no_ip.glfernando.inteledisoncontroller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by fernando on 3/13/15.
 * Gpio Recycler Adapter
 */
public class GpioRecyclerAdapter extends RecyclerView.Adapter<GpioRecyclerAdapter.ViewHolder> {
    private List<Gpio> mGpio = Collections.emptyList();
    private TouchListener mTouchListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        public TextView text;
        public ImageButton button;
        private TouchListener mTouchListener;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.gpio_textView);
            button = (ImageButton) itemView.findViewById(R.id.gpio_imageButton);


        }

        public interface TouchListener {
            public boolean onTouch(View v, MotionEvent event, int position);
        }

        public void setTouchLister(TouchListener touchLister) {
            mTouchListener = touchLister;
            button.setOnTouchListener(this);

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mTouchListener.onTouch(v, event, getPosition());
        }
    }

    public void updateList(List<Gpio> gpio) {
        mGpio = gpio;
        notifyDataSetChanged();
    }

    public void addItem(int p, Gpio g) {
        mGpio.add(p, g);
        notifyItemInserted(p);
    }

    public void addItem(Gpio g) {
        addItem(mGpio.size() - 1, g);
    }

    public void removeItem(int p) {
        mGpio.remove(p);
        notifyItemRemoved(p);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_gpio, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Gpio g = mGpio.get(position);
        holder.text.setText("PIN" + g.pin + "(gpio" + g.gpio + ")");
        holder.button.setImageResource(R.mipmap.ic_push_button);
        holder.setTouchLister(new ViewHolder.TouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event, int position) {
                return (mTouchListener != null) &&
                        mTouchListener.onTouch(v, event, mGpio.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGpio.size();
    }

    public interface TouchListener {
        public boolean onTouch(View v, MotionEvent event, Gpio gpio);
    }

    public void setTouchListener(TouchListener touchListener) {
        mTouchListener = touchListener;
    }


}
