package edu.tongji.andriylin;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * 与ringtone相关的设置
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
	 * 根据uri获取其title
	 * @param uri
	 * @return
	 */
	public String getTitleByUri(Uri uri) {
		Ringtone ringtone = RingtoneManager.getRingtone(this.context, uri);
		return ringtone.getTitle(this.context);
	}

	/**
	 * 设置一首歌为当前铃声
	 */
	public void setRingtone(Uri uri) {
		RingtoneManager.setActualDefaultRingtoneUri(this.context, RingtoneManager.TYPE_RINGTONE, uri);			
	}
	
	/**
	 * 设置为随机切换铃声
	 */
	public void registerListener() {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(RRPhoneStateListener.get(handler), PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	/**
	 * 取消随机切换铃声
	 */
	public void unregisterListener() {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(RRPhoneStateListener.get(handler), PhoneStateListener.LISTEN_NONE);
	}
	
}
