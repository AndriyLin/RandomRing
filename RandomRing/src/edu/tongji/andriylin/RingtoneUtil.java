package edu.tongji.andriylin;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * ��ringtone��ص�����
 * @author Andriy
 */
public class RingtoneUtil {
	
	private final Context context;
	public RingtoneUtil(Context aContext) {
		this.context = aContext;
	}
	
	public String getDefaultRingtoneTitle() {
		Uri uri = RingtoneManager.getActualDefaultRingtoneUri(this.context, RingtoneManager.TYPE_RINGTONE);
		return this.getTitleByUri(uri);
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
}
