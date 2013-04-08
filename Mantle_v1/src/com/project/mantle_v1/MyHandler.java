package com.project.mantle_v1;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.gmail.Mail;
import com.project.mantle_v1.notification_home.Note;
import com.project.mantle_v1.notification_home.Notifica;
import com.project.mantle_v1.notification_home.NotificaAdapter;
import com.project.mantle_v1.notification_home.NotificationListActivity;
import com.project.mantle_v1.parser.MantleMessage;
import com.project.mantle_v1.parser.Media;
import com.project.mantle_v1.parser.ParseJSON;
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
	private Context context;
	private String link;
	
	public static List<Notifica> ITEMS = new ArrayList<Notifica>();
	public static Map<String,Notifica> ITEM_MAP = new HashMap<String, Notifica>();
	
	private final String TAG = "MyHandler";
	
	private NotificaAdapter adapter;
	
	
	public MyHandler(Context context) {
		super();
		this.context = context;
		if(ITEMS.isEmpty())
			addItem(new Notifica(new Date(System.currentTimeMillis()).toString(), "Benvenuto in Mantle", MantleMessage.SYSTEM));
		
	}

	@Override
    public void handleMessage(Message msg) {
      
	 Log.d(TAG,"Nuovo messaggio");	
		
	  Bundle bundle = msg.getData();
	  
      if(bundle.containsKey("body")) {
    	  link = bundle.getString("body");
    	  
    	  MantleMessage mess = new MantleMessage(link, context);
    	  Notifica not = mess.getNotifica();
    	  createNotification(not.getTitle());
    	  addItem(not);
    	  Log.d(TAG, "ITEMS: " + String.valueOf(ITEMS.size()));
    	  Log.d(TAG, "AdapterB: " + adapter.toString());
    	  adapter.notifyDataSetChanged();
    	  
      }
     
      if(bundle.containsKey("adapter")) {
    	  this.adapter = (NotificaAdapter) bundle.get("adapter");
    	  Log.d(TAG, "Adapter: " + adapter.toString());
    	  
      }
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void createNotification(String title) {
	    // Prepare intent which is triggered if the
	    // notification is selected
	    Intent intent = new Intent(context, NotificationListActivity.class);
	    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

	    // Build notification
	    // Actions are just fake
	    Notification noti = new Notification.Builder(context)
	        .setContentTitle("Nuova condivisione")
	        .setContentText(title).setSmallIcon(R.drawable.ic_launcher)
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
		
	private static void addItem(Notifica item) {
		ITEM_MAP.put(String.valueOf(ITEM_MAP.size() + 1), item);
		ITEMS.add(item);
	}
}