package com.project.mantle_v1;

//commento di prova

import android.os.Environment;
import android.provider.Settings.Global;

public class Configuratore {
	
	public static String getHome() {
		return "/Mantle";
	}
		
	public static String getHomeDir() {
		return Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Mantle";
	}
	
	public static String getEmail() {
		return Global.USE_GOOGLE_MAIL;
	}
	
	public static String getMagicNumber() {
		return "Mantle001 ";
	}
	
}
