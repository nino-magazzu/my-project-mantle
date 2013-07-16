package com.project.mantle_v1.notification_home;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

import com.project.mantle_v1.R;
import com.project.mantle_v1.fileNavigator.MantleFile;
import com.project.mantle_v1.parser.MantleMessage;

/**
 * Si occupa del ciclo di vita delle notifiche all'interno dell'applicazione.
 * Gestisce la creazione delle stesse e la corretta visualizzazione all'interno
 * della GUI e della loro eliminazione a seguito dell'interazione dell'utente.
 * Questo processo è basato sullo scambio di messaggi fra le varie classi a
 * seguito di eventi di maggior rilievo.
 * 
 * ITEMS e NOTIFICA_MAP sono due strutture dati atte a contenere le notifiche
 * provenienti dalle mail. ITEMS è una semplice lista in cui verranno inserite
 * le notifiche che bisogna ancora leggere ed eliminate quelle lette
 * NOTIFICA_MAP invece è una Map che mi consente di accedere alla singola
 * notifica da qualunque classe conoscendone semplicemente la chiave
 * attribuitagli nella map
 * 
 * FILE_MAP raccoglie invece i dati relativi ai file presenti nel db, anch'essa
 * implementata per una maggiore velocità di accesso alle informazioni
 * 
 * @author nino
 * 
 */
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

	public final static String CLICKED_POS = "clickedpos";

	private static int index = 1;

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

	/**
	 * Metodo attivato a seguito della ricezione di un messaggio
	 */
	@Override
	public void handleMessage(Message msg) {

		Log.d(TAG, "Nuovo messaggio");

		Bundle bundle = msg.getData();

		/*
		 * Condizione verificata nel momento in cui arriva una nuova mail da un
		 * altro utente
		 */
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

		/*
		 * comunicazione dell'adapter per la gestione delle notifiche
		 * all'interno della home
		 */
		if (bundle.containsKey("adapter")) {
			adapter = (NotificaAdapter) bundle.get("adapter");
			Log.d(TAG, "Adapter: " + adapter.toString());

		}

		/*
		 * eliminazione di una notifica dopo che è stata visionata
		 */
		if (bundle.containsKey(CLICKED_POS)) {
			int clickedpos = bundle.getInt(CLICKED_POS);
			removeItem(clickedpos);
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
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.build();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, noti);
	}

	private static void addItem(Notifica item) {
		Log.w("MY HANDLER", "Index: " + index);
		item.setPositionMap(String.valueOf(index++));
		NOTIFICA_MAP.put(item.getPositionMap(), item);
		ITEMS.add(item);
	}

	synchronized private static void removeItem(int clickedpos) {
		String pos = String.valueOf(clickedpos);
		NOTIFICA_MAP.remove(NOTIFICA_MAP.get(pos));
		Iterator<Notifica> it = ITEMS.iterator();
		while (it.hasNext()) {
			if (it.next().getPositionMap().equals(pos))
				it.remove();
		}
		adapter.notifyDataSetChanged();
	}

	public static void addFile(MantleFile item) {
		FILE_MAP.put(item.getIdFile(), item);
	}
}