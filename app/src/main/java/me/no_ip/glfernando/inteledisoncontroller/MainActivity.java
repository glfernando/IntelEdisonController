package me.no_ip.glfernando.inteledisoncontroller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,HomeFragment.Connection {
    private ListView mListView;
    private DrawerLayout mDrawerLayout;
    private String []mItems;
    private boolean mConnected = false;
    private OutputStream mOut;
    private TextView mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up app bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Shows if the application is connected to the server running in the Edison Board
        mStatus= (TextView) findViewById(R.id.status_textView);
        mStatus.setText("Disconnected");
        mStatus.setBackgroundColor(Color.RED);

        //Initialize Fragment with Home Fragment
        Fragment f = new HomeFragment();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.add(R.id.fragment_container, f, "home").commit();

        //Put Intel Edison Image on top of the Navigation Drawer
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.intel_edison);
        ImageView imageView = (ImageView) findViewById(R.id.logo_image_view);
        imageView.setImageBitmap(getCircleBitmap(bm));


        mItems = getResources().getStringArray(R.array.drawer_items);
        if (mItems == null)
            finish();

        /* replace home with app name */
        mItems[0] = getResources().getString(R.string.app_name);

        // Create Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        // Add Drawer Toggle
        mDrawerLayout.setDrawerListener(drawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        //Create List elements for the Navigation Drawer
        mListView = (ListView) findViewById(R.id.drawer_list_view);
        mListView.setAdapter(new DrawerItemAdapter(this));
        mListView.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        FragmentManager fm = getSupportFragmentManager();
        Fragment f;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }

        switch (position) {
            case 0: //Home
                //f = new HomeFragment();
                return;
            case 1: //GPIO
                f = new GpioFragment();
                break;
            default:
                Toast.makeText(this, "Option not supported", Toast.LENGTH_SHORT).show();
                return;
        }

        mListView.setItemChecked(position, true);
        getSupportActionBar().setTitle(mItems[position]);

        // Replace with new fragment and add it to the back stack
        ft.replace(R.id.fragment_container, f).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    @Override
    public boolean connect(Socket s) {
        try {
            mOut = s.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        mConnected = true;
        mStatus.setText("Connected");
        mStatus.setBackgroundResource(R.color.primary);

        return true;
    }

    @Override
    public void disconnect() {
        mConnected = false;
        mStatus.setText("Disconnected");
        mStatus.setBackgroundColor(Color.RED);
    }
}
