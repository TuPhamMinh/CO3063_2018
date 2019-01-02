package com.joaquimley.smsparsing;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final String PREF_USER_MOBILE_PHONE = "pref_user_mobile_phone";
    private static final int SMS_PERMISSION_CODE = 0;

    private EditText mNumberEditText;
    private EditText message;
    private EditText delay;
    private static EditText percent;
    private String mUserMobilePhone;
    private SharedPreferences mSharedPreferences;
    private static Button btn_on_off;
    private static Button btn_blink;
    private static int ledStatus = 0;
    private static int flashStatus = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasReadSmsPermission()) {
            showRequestPermissionsInfoAlertDialog();
        }

        initViews();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserMobilePhone = mSharedPreferences.getString(PREF_USER_MOBILE_PHONE, "");
        if (!TextUtils.isEmpty(mUserMobilePhone)) {
            mNumberEditText.setText(mUserMobilePhone);
        }
    }

    private void initViews() {
        mNumberEditText = findViewById(R.id.et_number);
        message = findViewById(R.id.et_message);
        delay = findViewById(R.id.et_delay);
        percent = findViewById(R.id.et_percent);
        btn_on_off = findViewById(R.id.btn_on_off);
        btn_blink = findViewById(R.id.btn_blink);
        findViewById(R.id.btn_send).setOnClickListener(this);
        findViewById(R.id.btn_on_off).setOnClickListener(this);
        findViewById(R.id.btn_blink).setOnClickListener(this);
        findViewById(R.id.btn_up).setOnClickListener(this);
        findViewById(R.id.btn_down).setOnClickListener(this);
        findViewById(R.id.btn_min).setOnClickListener(this);
        findViewById(R.id.btn_max).setOnClickListener(this);
        findViewById(R.id.btn_set).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (!hasValidPreConditions()) return;
                checkAndUpdateUserPrefNumber();
                if(ledStatus == 1){
                    SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), String.valueOf(message.getText()));
                    Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                    break;
                }else if(ledStatus == 0){
                    SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + "ON");
                    Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.btn_on_off:
                if (!hasValidPreConditions()) return;
                checkAndUpdateUserPrefNumber();
                if(ledStatus == 1){
                    SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + "OFF");
                    Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                    break;
                }else if(ledStatus == 0){
                    SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + "ON");
                    Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.btn_blink:
                if (!hasValidPreConditions()) return;
                checkAndUpdateUserPrefNumber();
                if(flashStatus == 1){
                    SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + "FOFF");
                    Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                    break;
                }else if(flashStatus == 0){
                    SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + "FON " + String.valueOf(delay.getText()));
                    Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.btn_up:
                if (!hasValidPreConditions()) return;
                checkAndUpdateUserPrefNumber();
                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + "PUP");
                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_down:
                if (!hasValidPreConditions()) return;
                checkAndUpdateUserPrefNumber();
                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + "PDOWN");
                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_min:
                if (!hasValidPreConditions()) return;
                checkAndUpdateUserPrefNumber();
                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + "PMIN");
                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_max:
                if (!hasValidPreConditions()) return;
                checkAndUpdateUserPrefNumber();
                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + "PMAX");
                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_set:
                if (!hasValidPreConditions()) return;
                checkAndUpdateUserPrefNumber();
                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + "PSET " + String.valueOf(percent.getText()));
                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                break;

        }
    }

    /**
     * Checks if stored SharedPreferences value needs updating and updates \o/
     */
    private void checkAndUpdateUserPrefNumber() {
        if (TextUtils.isEmpty(mUserMobilePhone) && !mUserMobilePhone.equals(mNumberEditText.getText().toString())) {
            mSharedPreferences
                    .edit()
                    .putString(PREF_USER_MOBILE_PHONE, mNumberEditText.getText().toString())
                    .apply();
        }
    }


    /**
     * Validates if the app has readSmsPermissions and the mobile phone is valid
     *
     * @return boolean validation value
     */
    private boolean hasValidPreConditions() {
        if (!hasReadSmsPermission()) {
            requestReadAndSendSmsPermission();
            return false;
        }

        if (!SmsHelper.isValidPhoneNumber(mNumberEditText.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.error_invalid_phone_number, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Optional informative alert dialog to explain the user why the app needs the Read/Send SMS permission
     */
    private void showRequestPermissionsInfoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_alert_dialog_title);
        builder.setMessage(R.string.permission_dialog_message);
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestReadAndSendSmsPermission();
            }
        });
        builder.show();
    }

    /**
     * Runtime permission shenanigans
     */
    private boolean hasReadSmsPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_SMS)) {
            Log.d(TAG, "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                SMS_PERMISSION_CODE);
    }
    public static void ledOn(){
        ledStatus = 1;
        btn_on_off.setText("Tắt đèn");
    }
    public static void ledOff(){
        ledStatus = 0;
        btn_on_off.setText("Bật đèn");
    }
    public static void flashOn(){
        flashStatus = 1;
        btn_blink.setText("Dừng");
    }
    public static void flashOff(){
        flashStatus = 0;
        btn_blink.setText("Nháy");
    }
    public static void percentUpdate(int per){
        percent.setText(String.valueOf(per));

    }
}