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
import android.nfc.Tag;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,HomeFragment.Connection, IntelEdisonComm {
    private static final String TAG = "MainActivity";
    private static final String CURRENT_FRAG = "me.noip.glfernando.current_fragment";
    private ListView mListView;
    private DrawerLayout mDrawerLayout;
    private String []mItems;
    private boolean mConnected = false;
    private OutputStream mOut;
    private TextView mStatus;
    private int mCurrentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Setting up app bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Shows if the application is connected to the server running in the Edison Board
        mStatus= (TextView) findViewById(R.id.status_textView);
        if (mConnected) {
            mStatus.setText("Connected");
            mStatus.setBackgroundResource(R.color.primary);
        } else {
            mStatus.setText("Disconnected");
            mStatus.setBackgroundColor(Color.RED);
        }

        // if orientation change no need to create fragments
        if (savedInstanceState == null) {
            Log.d(TAG, "New Activity");
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            //Add all fragments
            Fragment[] frags = new Fragment[] {new HomeFragment(), new GpioFragment()};

            for (Fragment f: frags)
                t.add(R.id.fragment_container, f, f.getClass().getName());
            t.commit();

        } else {
            mCurrentPosition = savedInstanceState.getInt(CURRENT_FRAG);
        }

        //Put Intel Edison Image on top of the Navigation Drawer
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.intel_edison);
        ImageView imageView = (ImageView) findViewById(R.id.logo_image_view);
        imageView.setImageBitmap(getCircleBitmap(bm));


        mItems = getResources().getStringArray(R.array.drawer_items);
        if (mItems == null)
            finish();

        /* replace home with app name */
        mItems[0] = getResources().getString(R.string.app_name);

        // Update title
        getSupportActionBar().setTitle(mItems[mCurrentPosition]);

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

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
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
        mListView.setItemChecked(mCurrentPosition, true);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putInt(CURRENT_FRAG, mCurrentPosition);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
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

        //return if the position is already selected
        if (position == mCurrentPosition) {
            return;
        }

        String ftag;
        switch (position) {
            case 0:
                ftag = HomeFragment.class.getName();
                break;
            case 1:
                ftag = GpioFragment.class.getName();
                break;
            default:
                Toast.makeText(this, "Option " + mItems[position] + " not supported", Toast.LENGTH_SHORT)
                        .show();
                return;

        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        List<Fragment> frags = fm.getFragments();

        ft.hide(frags.get(mCurrentPosition))
                .show(fm.findFragmentByTag(ftag))
                .commit();

        mListView.setItemChecked(position, true);
        mCurrentPosition = position;
        getSupportActionBar().setTitle(mItems[position]);
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
        // if View created update text view
        if (mStatus != null) {
            mStatus.setText("Connected");
            mStatus.setBackgroundResource(R.color.primary);
        }

        return true;
    }

    @Override
    public void disconnect() {
        try {
            mOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mConnected = false;
        // if View Created update text view
        if (mStatus != null) {
            mStatus.setText("Disconnected");
            mStatus.setBackgroundColor(Color.RED);
        }
    }

    @Override
    public void sendData(String data) {
        if (mConnected == false) {
            Toast.makeText(this, "Not connected to Edison Board", Toast.LENGTH_SHORT).show();
            return;
        }

        byte []msg = data.getBytes();

        try {
            mOut.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
