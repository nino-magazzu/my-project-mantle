package com.project.mantle_v1.notification_home;

import java.util.List;

import com.project.mantle_v1.User;
import com.project.mantle_v1.parser.MantleMessage;

public class Notifica {

	/**  Rappresenta una notifica ottenuta dalle mail. Raccoglie i dati 
	 *     essenziali per andare a creare le notifiche all'interno della 
	 *		home	
	 */
	
	// NOTIFICATION TYPE ID
	
	private String data;
	private String NotificationType;
	private User user;
	private List<Note> notes;
	private String title;
	private String username;
	private String body;

	/**
	 * Costruttore da usare nel caso in cui la notifica sia relativa ad 
	 * una richiesta d'amicizia o all'accettazione della stessa
	 * @param data: del momento in cui la mail è arrivata
	 * @param who: Utente che desidera stringere l'amicizia
	 */
	
	public Notifica(String data, User user, String code) {
		super();
		this.data = data;
		this.user = user;
		this.NotificationType = code;
		this.title = user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")" + ": richiesta d'amicizia";
		
		if(code.compareTo(MantleMessage.FRIENDSHIP_REQUEST) == 0) 
			this.body = user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")" +  " vuole stringere amicizia con te.";	
		
		if(code.compareTo(MantleMessage.FRIENDSHIP_ACCEPTED) == 0) 
			this.body = user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")" +  " ha accettato la tua richiesta di amicizia.";	
		
	}

	/**
	 * Costruttore da utilizzare in caso di notifiche di sistema
	 * @param date : relativa alla creazione (invio) della mail
	 * @param body : corpo della notifica
	 */
	
	public Notifica(String date, String body, String code) {
		super();
		this.data = date;
		this.NotificationType = code;
		
		if(NotificationType.equals(MantleMessage.FRIENDSHIP_DENIED))
			this.title ="Richiesta di amicizia rifiutata";
		else
			this.title = "Notifica di sistema";
		
		this.body = body;
	}
	
	/**
	 *  Costruttore per le notifiche di condivisione di immagini o di nuovi
	 *  commenti alle foto
	 *  
	 * @param data: del momento in cui la mail è arrivata
	 * @param notificationType: per indicare con esattezza di che tipo di notifica si tratta.
	 * NEW_SHARED_PHOTO per le nuove condivisioni. NOTE per nuovo commento  
	 * @param who: nome di chi vuole condividere la foto o di chi ha commentato
	 * @param notes: lista dei commenti alla foto
	 */
	
	public Notifica(String data, String notificationType, String who,
			List<Note> notes) {
		super();
		this.data = data;
		NotificationType = notificationType;
		this.username = who;
		this.notes = notes;
		if(notificationType.compareTo(MantleMessage.NOTE) == 0) {
			this.title = who + " ha commentato una tua foto";
		}
		else
			this.title = who + " ha condiviso una foto";
	}
	
	public String getData() {
		return data;
	}
	
	public String getNotificationType() {
		return NotificationType;
	}
	
	public String getWho() {
		if(user != null)
			return user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")";
		else
			return username;
	}
	
	public List<Note> getNotes() {
		return notes;
	}

	public String getTitle() {
		return title;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getNotificationBody() {
		return body;
	}
}