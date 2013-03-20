package com.project.mantle_v1.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * E' una classe d'appoggio che serve a fornire il contenuto alle interfacce utenti create. 
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * Lista di quelle che sono le notifiche da visualizzare.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

	/**
	 * Una semplice mappa chiave, valore che raccoglie tutti i dati delle notifiche. Il .
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

	/*
	 * Queste due stutture dati sono implementate per facilitare la gestione delle notifiche. 
	 * La parte sottostante sarà eliminata in quanto utile solo per provarne il funzionamento.
	 */
	
	static {
		// Add 3 sample items.
		addItem(new DummyItem("1", "Item 1"));
		addItem(new DummyItem("2", "Item 2"));
		addItem(new DummyItem("3", "Item 3"));
	}
	
	/*
	 * Metodo che aggiunge nuovi elementi sia alla mappa che alla lista. 
	 * In base a ciò che abbiamo bisogno di visualizzare i dummyItem
	 * possono essere modificati o restare invariati
	 */
	
	private static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/* TODO: capire se è possibile integrare in un fragment un immagine e del testo
	 *	 o una cosa del genere.	
	 */
	public static class DummyItem {
		public String id;
		public String content;

		public DummyItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}
}