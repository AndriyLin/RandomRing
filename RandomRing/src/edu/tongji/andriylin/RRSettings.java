package edu.tongji.andriylin;

import android.content.SharedPreferences;

/**
 * 包括程序参数的设置
 * @author Andriy
 */
public class RRSettings {

	private final SharedPreferences preferences;
	
	public RRSettings(SharedPreferences pref) {
		this.preferences = pref;
	}
	
	
}
