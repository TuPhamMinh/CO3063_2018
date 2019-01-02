package com.joaquimley.smsparsing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A broadcast receiver who listens for incoming SMS
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsBody += smsMessage.getMessageBody();
            }

            if (smsBody.startsWith(SmsHelper.SMS_CONDITION)) {
                Log.d(TAG, "Sms with condition detected");
                Toast.makeText(context, "Đã nhận tin nhắn: " + smsBody, Toast.LENGTH_LONG).show();
                if(smsBody.contains("FON")){
                    Toast.makeText(context, "Đã nhận tin nhắn: " + smsBody, Toast.LENGTH_LONG).show();
                    MainActivity.flashOn();
                }else if(smsBody.contains("FOFF")){
                    MainActivity.flashOff();
                }else if(smsBody.contains("ON")){
                    MainActivity.ledOn();
                }else if(smsBody.contains("OFF")){
                    Toast.makeText(context, "Đã nhận tin nhắn: " + smsBody, Toast.LENGTH_LONG).show();
                    MainActivity.ledOff();
                }else if(smsBody.contains("FP")){
                    Toast.makeText(context, "Đã nhận tin nhắn: " + smsBody, Toast.LENGTH_LONG).show();
                    MainActivity.percentUpdate(100);
                }

            }
            Log.d(TAG, "SMS detected: From " + smsSender + " With text " + smsBody);
        }
    }
}
