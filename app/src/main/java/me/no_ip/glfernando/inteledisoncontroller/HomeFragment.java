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
    private static final String TAG = "IntelEdisonController";
    private Spinner mSpinner;
    private Connection mConn;
    private Button mButton;
    private Socket mSocket;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        mSpinner = (Spinner) v.findViewById(R.id.spinner_conn_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_connection, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mButton = (Button) v.findViewById(R.id.button_connect);
        mButton.setText("connect");
        mButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mConn = (Connection)activity;
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
            if (mButton.getText().toString() == "connect") {
                try {
                    InetAddress server_addr = InetAddress.getByName(addr.getText().toString());
                    mSocket = new Socket(server_addr, Integer.valueOf(port.getText().toString()));
                    if (mConn.connect(mSocket) == true) {
                        mButton.setText("disconnect");
                    }
                } catch (UnknownHostException e) {
                    Toast.makeText(getActivity(), "Unknown Host " + addr.getText() + ":" + port.getText(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                mConn.disconnect();
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mButton.setText("connect");
            }

        }
    }

    public interface Connection {
        public boolean connect(Socket s);
        public void disconnect();

    }
}
