package edu.tongji.andriylin;

import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class RRPhoneStateListener extends PhoneStateListener {

	private RRPhoneStateListener() {}
	private static RRPhoneStateListener instance = null;
	public static synchronized RRPhoneStateListener get(Handler aHandler) {
		if (instance == null) {
			instance = new RRPhoneStateListener();
		}
		instance.handler = aHandler;
		return instance;
	}

	private Handler handler;
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
		
		if (state == TelephonyManager.CALL_STATE_RINGING) {
			Message msg = Message.obtain(handler, RandomRingActivity.RINGTONE_SHOULD_CHANGE);
			msg.sendToTarget();
		}
	}

}
