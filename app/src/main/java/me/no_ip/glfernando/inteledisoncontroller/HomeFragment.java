package me.no_ip.glfernando.inteledisoncontroller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by fernando on 3/9/15.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private static final String CONNECTED = "me.noip.glfernando.connected";
    private static final String ADDR = "me.noip.glfernando.addr";
    private static final String PORT = "me.noip.glfernando.port";
    private static final String HIDDEN = "me.noip.glfernando.home.hidden";
    private Spinner mSpinner;
    private Connection mConn;
    private Button mButton;
    private Socket mSocket;
    private boolean mConnected;
    private String mAddr;
    private int mPort;
    private boolean mHidden;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        //Check if we should restore the connection
        if (savedInstanceState != null) {
            if (mSocket == null) {
                mConnected = savedInstanceState.getBoolean(CONNECTED);
                mAddr = savedInstanceState.getString(ADDR);
                mPort = savedInstanceState.getInt(PORT);

                if (mConnected == true) {
                    if (connect(mAddr, mPort) == true) {
                        mConn.connect(mSocket);
                    } else {
                        mConnected = false;
                    }
                }
            }

            //check if fragment was hidden
            mHidden = savedInstanceState.getBoolean(HIDDEN);
            if (mHidden)
                getFragmentManager().beginTransaction().hide(this).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        mSpinner = (Spinner) v.findViewById(R.id.spinner_conn_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_connection, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mButton = (Button) v.findViewById(R.id.button_connect);
        mButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
        mConn = (Connection)activity;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(CONNECTED, mConnected);
        outState.putString(ADDR, mAddr);
        outState.putInt(PORT, mPort);
        outState.putBoolean(HIDDEN, mHidden);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (mConnected == true) {
                mButton.setText("disconnect");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if (mConnected == true)
            disconnect();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mHidden = hidden;
    }

    @Override
    public void onClick(View v) {
        // if TCPIP
        if (mSpinner.getSelectedItemPosition() == 0) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
            EditText addr = (EditText) getView().findViewById(R.id.editText_ip_addr);
            EditText port = (EditText) getView().findViewById(R.id.editText_port);

            //remove focus from edit texts
            addr.clearFocus();
            port.clearFocus();

            //Hide keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            Log.d(TAG, "Button Text " + mButton.getText().toString());
            if (mConnected == false) {
                if (connect(addr.getText().toString(), Integer.valueOf(port.getText().toString())) == true) {
                    mConn.connect(mSocket);
                    mButton.setText("disconnect");
                }
            } else {
                mButton.setText("connect");
                disconnect();
                mConn.disconnect();
                mButton.setText("connect");
            }

        }
    }

    private boolean connect(String addr, int port) {
        try {
            InetAddress server_addr = InetAddress.getByName(addr);
            mSocket = new Socket(server_addr, port);
            mConnected = true;
            mAddr = addr;
            mPort = port;

            return true;
        } catch (UnknownHostException e) {
            Toast.makeText(getActivity(), "Unknown Host " + addr + ":" + port, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    private boolean disconnect() {
        try {
            mSocket.close();
            mConnected = false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface Connection {
        public boolean connect(Socket s);

        public void disconnect();

    }
}
