package me.no_ip.glfernando.inteledisoncontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

/**
 * Created by fernando on 3/15/15.
 */
public class AddGpioFragment extends DialogFragment {
    public static final String EXTRA_GPIO_NUM = "me.noip.glfernando.gpio_num";
    private EditText mEditText;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_gpio, null);

        mEditText = (EditText) v.findViewById(R.id.dialog_gpio_editText);

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.choose_gpio).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(Activity.RESULT_OK, Integer.parseInt(mEditText.getText().toString()));
            }
        }).create();
    }

    private void sendResult(int result, int gpio) {
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_GPIO_NUM, gpio);

        getTargetFragment().onActivityResult(getTargetRequestCode(), result, i);
    }
}
