package edu.tongji.andriylin;

import android.content.SharedPreferences;

public class RRSettings {

	private final SharedPreferences preferences;
	public RRSettings(SharedPreferences aPreferences) {
		this.preferences = aPreferences;
	}

	/*
	 * ����л������Ƿ���
	 */
	private static final String RANDOM_ON = "setting_random_on";
	public boolean isRandomOn() {
		return this.preferences.getBoolean(RANDOM_ON, false);
	}
	public synchronized void setRandomOn(boolean on) {
		this.preferences.edit().putBoolean(RANDOM_ON, on).commit();
	}
}
