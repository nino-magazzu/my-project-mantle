package com.project.mantle_v1.notification_home;

import java.io.Serializable;

import android.content.SharedPreferences;

import com.project.mantle_v1.database.User;
import com.project.mantle_v1.fileNavigator.MantleFile;
import com.project.mantle_v1.parser.MantleMessage;

public class Notifica implements Serializable {

	/**
	 * Rappresenta una notifica ottenuta dalle mail. Raccoglie i dati essenziali
	 * per andare a creare le notifiche all'interno della home
	 */

	public static final boolean SEEN = true;
	public static final boolean NOT_SEEN = false;
	
	private static final long serialVersionUID = 3324254621470042381L;
	
	private String data;
	private String NotificationType;
	private User user;
	private String title;
	private String body;
	private MantleFile mFile;
	private Note note;
	private boolean status;

	/**
	 * Costruttore da usare nel caso in cui la notifica sia relativa ad una
	 * richiesta d'amicizia o all'accettazione della stessa
	 * 
	 * @param data
	 *            del momento in cui la mail è arrivata
	 * @param user
	 *            Utente che desidera stringere l'amicizia
	 * @param code
	 *            tipo di notifica
	 */

	public Notifica(String data, User user, String code) {
		super();
		this.data = data;
		this.user = user;
		this.NotificationType = code;
		this.title = user.getName() + " " + user.getSurname() + " ("
				+ user.getUsername() + ")" + ": richiesta d'amicizia";

		if (code.compareTo(MantleMessage.FRIENDSHIP_REQUEST) == 0)
			this.body = user.getName() + " " + user.getSurname() + " ("
					+ user.getUsername() + ")"
					+ " vuole stringere amicizia con te.";

		if (code.compareTo(MantleMessage.FRIENDSHIP_ACCEPTED) == 0)
			this.body = user.getName() + " " + user.getSurname() + " ("
					+ user.getUsername() + ")"
					+ " ha accettato la tua richiesta di amicizia.";
		
		this.status = NOT_SEEN; 

	}

	/**
	 * Costruttore da utilizzare in caso di notifiche di sistema
	 * 
	 * @param date
	 *            relativa alla creazione (invio) della mail
	 * @param body
	 *            corpo della notifica
	 * @param code
	 *            il tipo di notifica da creare
	 */

	public Notifica(String data, String body, String code) {
		super();
		this.data = data;
		this.NotificationType = code;

		if (NotificationType.equals(MantleMessage.FRIENDSHIP_DENIED))
			this.title = "Richiesta di amicizia rifiutata";
		else if (NotificationType.equals(MantleMessage.NOTE))
			this.title = "Nuovo Commento";
		else
			this.title = "Notifica di sistema";

		this.body = body;
		this.status = NOT_SEEN;
	}

	/**
	 * Crea la notifica relativa alla condivisione di un file
	 * 
	 * @param mFile
	 *            file condiviso
	 */

	public Notifica(MantleFile mFile, String code) {
		super();
		this.mFile = mFile;
		this.data = mFile.getDate();
		this.title = mFile.getUsername() + " ha condiviso con te questo file";
		this.NotificationType = code;
		this.status = NOT_SEEN;
	}

	/**
	 * Creata a seguito di un aggiunta di un commento da parte di un utente ad
	 * un file
	 * 
	 * @param note
	 *            Commento inserito
	 */

	public Notifica(Note note, String owner_username) {
		super();
		this.data = note.getDate();
		this.NotificationType = MantleMessage.NOTE;
		this.note = note;
		
		if(note.getUser().equals(owner_username))
			this.title = "Commento al file accettato";
		else
			this.title = note.getUser() + " ha commentato una foto";
		
		this.status = NOT_SEEN;
	}

	public String getData() {
		return data;
	}

	public String getNotificationType() {
		return NotificationType;
	}

	public String getWho() {
		if (user != null)
			return user.getName() + " " + user.getSurname() + " ("
					+ user.getUsername() + ")";
		else if (note != null)
			return note.getUser();
		else if (mFile != null)
			return mFile.getUsername();
		else
			return null;
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

	public MantleFile getmFile() {
		return mFile;
	}

	public void setmFile(MantleFile mFile) {
		this.mFile = mFile;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public boolean isSeen() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}