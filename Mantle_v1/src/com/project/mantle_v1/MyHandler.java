package com.project.mantle_v1;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyHandler extends Handler {
	private String not;
	private Context context;
	private String link;
	
	public MyHandler(Context context) {
		super();
		this.context = context;
	}

	@Override
    public void handleMessage(Message msg) {
      Bundle bundle = msg.getData();
      if(bundle.containsKey("notification")) {
    	  not = bundle.getString("notification"); 	  
    	  link = bundle.getString("link");
    	  createNotification();
    	  Log.d("EMAIL",not);
    	  Log.d("EMAIL",link);
      }
	}
	
	public String getNotification(){
		return not;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void createNotification() {
	    // Prepare intent which is triggered if the
	    // notification is selected
	    Intent intent = new Intent(context, Home.class);
	    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

	    // Build notification
	    // Actions are just fake
	    Notification noti = new Notification.Builder(context)
	        .setContentTitle("Nuova condivisione")
	        .setContentText(link).setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent)
	        //.addAction(R.drawable.icon, "More", pIntent)
	        //.addAction(R.drawable.icon, "Call", pIntent)
	        //.addAction(R.drawable.icon, "And more", pIntent)
	        .build();
	    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	    // Hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;
	    notificationManager.notify(0, noti);
	  }
}