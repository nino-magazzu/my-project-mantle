package com.project.mantle_v1;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.project.mantle_v1.fileNavigator.MantleFile;
import com.project.mantle_v1.notification_home.Notifica;
import com.project.mantle_v1.notification_home.NotificaAdapter;
import com.project.mantle_v1.notification_home.NotificationListActivity;
import com.project.mantle_v1.parser.MantleMessage;

public class MyHandler extends Handler {
	private Context context;
	private String link;
	private String email;
	private String FILE_HISTORY_NAME;

	public static List<Notifica> ITEMS = new ArrayList<Notifica>();
	public static Map<String, Notifica> NOTIFICA_MAP = new HashMap<String, Notifica>();
	public static Map<String, MantleFile> FILE_MAP = new HashMap<String, MantleFile>();

	private final String TAG = this.getClass().getSimpleName();

	public static NotificaAdapter adapter;

	public MyHandler(Context context) {
		super();

		// *** scelta del file storia *** //
		// History his = new History();

		// FILE_HISTORY_NAME = his.getLastFile();

		this.context = context;

		if (ITEMS.isEmpty())
			addItem(new Notifica(
					new Date(System.currentTimeMillis()).toString(),
					"Benvenuto in Mantle", MantleMessage.SYSTEM));
	}

	@Override
	public void handleMessage(Message msg) {

		Log.d(TAG, "Nuovo messaggio");

		Bundle bundle = msg.getData();

		if (bundle.containsKey("body")) {
			link = bundle.getString("body");
			email = bundle.getString("email");
			Log.d(TAG, "Email amico: " + email);

			MantleMessage mess = new MantleMessage(link, context, email,
					FILE_HISTORY_NAME);
			Notifica not = mess.getNotifica();

			if (not != null) {
				createNotification(not.getTitle());
				addItem(not);
				Log.v(TAG, "AdapterB: " + adapter.toString());
				adapter.notifyDataSetChanged();

			}
		}

		if (bundle.containsKey("adapter")) {
			adapter = (NotificaAdapter) bundle.get("adapter");
			Log.d(TAG, "Adapter: " + adapter.toString());

		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void createNotification(String title) {
		// Prepare intent which is triggered if the
		// notification is selected
		Intent intent = new Intent(context, NotificationListActivity.class);
		PendingIntent pIntent = PendingIntent
				.getActivity(context, 0, intent, 0);

		// Build notification
		// Actions are just fake
		Notification noti = new Notification.Builder(context)
				.setContentTitle("Mantle").setContentText(title)
				.setSmallIcon(R.drawable.ic_action_share)
				.setContentIntent(pIntent)
				// .addAction(R.drawable.icon, "More", pIntent)
				// .addAction(R.drawable.icon, "Call", pIntent)
				// .addAction(R.drawable.icon, "And more", pIntent)
				.build();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, noti);
	}

	private static void addItem(Notifica item) {
		NOTIFICA_MAP.put(String.valueOf(NOTIFICA_MAP.size() + 1), item);
		ITEMS.add(item);
	}

	public static void addFile(MantleFile item) {
		FILE_MAP.put(item.getIdFile(), item);
	}
}