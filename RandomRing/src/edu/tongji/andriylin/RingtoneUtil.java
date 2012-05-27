package edu.tongji.andriylin;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * ��ringtone��ص�����
 * @author Andriy
 */
public class RingtoneUtil {
	
	private final Context context;
	private final Handler handler;
	public RingtoneUtil(Context aContext, Handler aHandler) {
		this.context = aContext;
		this.handler = aHandler;
	}
	
	/**
	 * ����uri��ȡ��title
	 * @param uri
	 * @return
	 */
	public String getTitleByUri(Uri uri) {
		Ringtone ringtone = RingtoneManager.getRingtone(this.context, uri);
		return ringtone.getTitle(this.context);
	}

	/**
	 * ����һ�׸�Ϊ��ǰ����
	 */
	public void setRingtone(Uri uri) {
		RingtoneManager.setActualDefaultRingtoneUri(this.context, RingtoneManager.TYPE_RINGTONE, uri);			
	}
	
	/**
	 * ����Ϊ����л�����
	 */
	public void registerListener() {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(RRPhoneStateListener.get(handler), PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	/**
	 * ȡ������л�����
	 */
	public void unregisterListener() {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(RRPhoneStateListener.get(handler), PhoneStateListener.LISTEN_NONE);
	}
	
}
