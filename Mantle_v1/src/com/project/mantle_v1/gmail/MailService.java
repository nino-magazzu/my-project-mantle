package com.project.mantle_v1.gmail;

import com.project.mantle_v1.MyHandler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.IBinder;
import android.util.Log;

public class MailService extends Service{
	private final String TAG = "MAIL_SERVICE" ;
	private State state;
	private MyHandler handler;
	private String account;
	private String pswd;
	
	public MailService(MyHandler handler, String email, String pswd) {
		this.handler = handler;
		this.pswd = pswd;
		this.account = email.substring(0, email.indexOf("@"));
	}
	
	@Override
    public void onCreate() {
    	super.onCreate();
    	Log.v(TAG , "***** MailService *****: onCreate");
    }
	
	@Override
    public void onStart(Intent intent, int startId) {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        state = State.DISCONNECTED;
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null)
            {
                state = netInfo.getState();
            
                if (state == State.CONNECTED)
                {   
                    Log.i(TAG, "Currently connected to a network");
                    Reader read = new Reader(account, pswd, handler);
        			try {
						read.detectMail();
						read.detectNewMail();
					} catch (Exception e) {
						Log.e(TAG, e.getMessage());
					}
                }
                else
                {
                    Log.i(TAG, "Current network state = " + state);
                }
            }
        }
	}

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
