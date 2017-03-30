package com.example.kaouther.phonetophonecall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

public class PhoneStateReceiver extends BroadcastReceiver {
    public PhoneStateReceiver() {
    }

    public static String TAG="PhoneStateReceiver ";
    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            final String[] state = {intent.getStringExtra(TelephonyManager.EXTRA_STATE)};

            Log.d(TAG,"Call State= " + state[0]);

            if (state[0].equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Log.d(TAG,"IDLE");
            } else if (state[0].equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d(TAG,"Incoming Call From: " +  incomingNumber);

               /* if (!killCall(context)) { // Using the method defined earlier
                    Log.d(TAG,"PhoneStateReceiver **Unable to kill incoming call");
                }*/

            } else if (state[0].equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Log.d(TAG,"OFFHOOK");

            }
        } else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            // Outgoing call
            String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG,"Outgoing Call To: " + outgoingNumber);

        } else {
            Log.d(TAG,"Unexpected intent.action=" + intent.getAction());
        }
    }



    public boolean killCall(Context context) {


        try {

            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony

            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);


        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d(TAG,"PhoneStateReceiver **" + ex.toString());
            return false;
        }
        return true;
    }

}
