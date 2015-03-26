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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by fernando on 3/24/15.
 */
public class AddPwmFragment extends DialogFragment {
    public static final String EXTRA_PWM_NUM = "me.noip.glfernando.pwm.extra.num";
    public static final String EXTRA_PWM_SERVO = "me.noip.glfernando.pwm.extra.servo";
    public static final String EXTRA_PWM_PERIOD = "me.noip.glfernando.pwm.extra.period";
    EditText mNum;
    EditText mPeriod;
    CheckBox mCheck;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_pwm, null);

        mNum = (EditText) v.findViewById(R.id.dialog_pwm_num_editText);
        mPeriod = (EditText) v.findViewById(R.id.dialog_pwm_period_editText);
        mCheck = (CheckBox) v.findViewById(R.id.dialog_pwm_servo_checkBox);
        mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    mPeriod.setText("20000");
                    mPeriod.setEnabled(false);
                } else {
                    mPeriod.setEnabled(true);
                }
            }
        });

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.choose_pwm).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(Activity.RESULT_OK, Integer.parseInt(mNum.getText().toString()),
                        Integer.parseInt(mPeriod.getText().toString()), mCheck.isChecked());
            }
        }).create();
    }

    private void sendResult(int result, int pwm, int p, boolean servo) {
        if (getTargetFragment() == null)
            return;

        Intent i = new Intent();
        i.putExtra(EXTRA_PWM_NUM, pwm);
        i.putExtra(EXTRA_PWM_SERVO, servo);
        i.putExtra(EXTRA_PWM_PERIOD, p);

        getTargetFragment().onActivityResult(getTargetRequestCode(), result, i);
    }
}
