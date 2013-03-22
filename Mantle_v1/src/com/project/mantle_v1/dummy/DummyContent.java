package com.project.mantle_v1.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.mantle_v1.notification_home.Notifica;

/**
 * E' una classe d'appoggio che serve a fornire il contenuto alle interfacce utenti create. 
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * Lista di quelle che sono le notifiche da visualizzare.
	 */
	public static List<Notifica> ITEMS = new ArrayList<Notifica>();

	/**
	 * Una semplice mappa chiave, valore che raccoglie tutti i dati delle notifiche. Il .
	 */
	public static Map<String,Notifica> ITEM_MAP = new HashMap<String, Notifica>();

	/*
	 * Queste due stutture dati sono implementate per facilitare la gestione delle notifiche. 
	 * La parte sottostante sarà eliminata in quanto utile solo per provarne il funzionamento.
	 */
	
	static {
		// Add 3 sample items.
		addItem(new Notifica("Oggi", "Pippo"), "1");
		addItem(new Notifica("ieri", "Pluto"), "2");
		addItem(new Notifica("Anni orsono", "Topolino"), "3");
	}
	
	/*
	 * Metodo che aggiunge nuovi elementi sia alla mappa che alla lista. 
	 * In base a ciò che abbiamo bisogno di visualizzare i dummyItem
	 * possono essere modificati o restare invariati
	 */
	
	private static void addItem(Notifica item, String id) {
		ITEM_MAP.put(id, item);
		ITEMS.add(item);
	}

	/* TODO: capire se è possibile integrare in un fragment un immagine e del testo
	 *	 o una cosa del genere.	
	 */
	
}	