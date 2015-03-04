package me.no_ip.glfernando.inteledisoncontroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Navigatino Drawer ListView menu custom Adapter
 *
 */
public class DrawerItemAdapter extends BaseAdapter {
    private Context mContext;
    String []items;
    int []icons;

    public DrawerItemAdapter(Context context) {
        this.mContext = context;
        items = context.getResources().getStringArray(R.array.drawer_items);
        icons = new int[]{R.mipmap.ic_home, R.mipmap.ic_gpio, R.mipmap.ic_pwm, R.mipmap.ic_i2c, R.mipmap.ic_spi};
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_item, parent, false);
        }
        TextView text = (TextView) convertView.findViewById(R.id.text_view_item);
        text.setText(items[position]);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view_item);
        imageView.setImageResource(icons[position]);

        return convertView;
    }
}
