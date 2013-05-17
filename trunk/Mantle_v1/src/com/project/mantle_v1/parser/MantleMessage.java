package com.project.mantle_v1.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.project.mantle_v1.database.DatabaseHelper;
import com.project.mantle_v1.database.User;
import com.project.mantle_v1.fileNavigator.MantleFile;
import com.project.mantle_v1.notification_home.Note;
import com.project.mantle_v1.notification_home.Notifica;

/**
 * classe che si occupa di andare a creare il messaggio da inviare e della
 * creazione delle notifiche
 * 
 * @author ninux
 * 
 */

public class MantleMessage {
	public static String MAGIC_NUMBER = "Mantle";

	public static String FRIENDSHIP_REQUEST = "001 ";
	public static String FRIENDSHIP_ACCEPTED = "002 ";
	public static String FRIENDSHIP_DENIED = "003 ";
	public static String SHARING_PHOTO = "004 ";
	public static String NOTE = "005 ";
	public static String SYSTEM = "006 ";
	public static String SHARING_FILE = "007 ";

	private String jsonText;
	private String type;
	private String sender_email;
	private String message;
	private Map<String, Integer> DECODE_MAP;
	// private String history;

	private final int CODE_DIM = FRIENDSHIP_REQUEST.length();

	private Context context;

	private final String TAG = this.getClass().getSimpleName();

	/**
	 * 
	 * @param json
	 *            dell'oggetto da inviare
	 * @param MessageType
	 *            identifica il tipo di email inviata
	 */

	public MantleMessage(String json, String MessageType) {
		this.jsonText = json;
		this.type = MessageType;
	}

	public String getMessage() throws Exception {
		if (type.equals(FRIENDSHIP_REQUEST))
			return MAGIC_NUMBER + type + jsonText;

		if (type.equals(FRIENDSHIP_ACCEPTED))
			return MAGIC_NUMBER + type + jsonText;

		if (type.equals(FRIENDSHIP_DENIED))
			return MAGIC_NUMBER + type + jsonText;

		if (type.equals(NOTE))
			return MAGIC_NUMBER + type + jsonText;

		if (type.equals(SHARING_PHOTO))
			return MAGIC_NUMBER + type + jsonText;

		if (type.equals(SHARING_FILE))
			return MAGIC_NUMBER + type + jsonText;

		throw new Exception("Message Type Errato!");

	}

	public MantleMessage(String message, Context c, String email,
			String historyFileName) {
		this.message = message.substring(MantleMessage.MAGIC_NUMBER.length(),
				message.length());
		this.context = c;
		this.sender_email = email;
		// this.history = historyFileName;
		DECODE_MAP = new HashMap<String, Integer>();

		int i = 0;
		DECODE_MAP.put(FRIENDSHIP_REQUEST, ++i);
		DECODE_MAP.put(FRIENDSHIP_ACCEPTED, ++i);
		DECODE_MAP.put(FRIENDSHIP_DENIED, ++i);
		DECODE_MAP.put(SHARING_PHOTO, ++i);
		DECODE_MAP.put(NOTE, ++i);
		DECODE_MAP.put(SYSTEM, ++i);
		DECODE_MAP.put(SHARING_FILE, ++i);

	}

	public Notifica getNotifica() {
		String code = message.substring(0, CODE_DIM);
		Log.d(TAG, code);

		int CODE = DECODE_MAP.get(code);

		DatabaseHelper db = new DatabaseHelper(context);

		ParseJSON parser = null;
		User user = null;
		MantleFile media = null;
		Note note;

		switch (CODE) {
			
			/*
			 *  ===== Richiesta d'amicizia =====
			 */
			
			case 001:
				jsonText = message.substring(CODE_DIM, message.length());
				Log.d(TAG, jsonText);
				parser = new ParseJSON(new StringReader(jsonText));
				try {
					user = parser.readUserJson();
				} catch (IOException e) {
					Log.e(TAG, "Problema lettura: " + e.getMessage());
	
				}
				return new Notifica(
						new Date(System.currentTimeMillis()).toString(), user,
						FRIENDSHIP_REQUEST);
				
				/*
				 *  ===== Richiesta d'amicizia accettata =====
				 */
			
			case 002:
				jsonText = message.substring(CODE_DIM, message.length());
				Log.d(TAG, jsonText);
				parser = new ParseJSON(new StringReader(jsonText));
				try {
					user = parser.readUserJson();
				} catch (IOException e) {
					Log.e(TAG, "Problema lettura: " + e.getMessage());
				}
				db.insertUser(user.getEmail(), user.getUsername(), user.getName(),
						user.getSurname(), user.getKey());
				db.close();
				return new Notifica(
						new Date(System.currentTimeMillis()).toString(), user,
						FRIENDSHIP_ACCEPTED);
	
				/*
				 *  ===== Richiesta d'amicizia rifiutata =====
				 */
				
			case 003:
				jsonText = message.substring(CODE_DIM, message.length());
				Log.d(TAG, jsonText);
				parser = new ParseJSON(new StringReader(jsonText));
				note = new Note();
				try {
					note = parser.readNote();
				} catch (IOException e) {
					Log.e(TAG, "Problema lettura: " + e.getMessage());
				}
				return new Notifica(note.getDate(), note.getContent(),
						MantleMessage.FRIENDSHIP_DENIED);
	
				/*
				 *  ===== Condivisione di una foto =====
				 */
				
			case 004:
				jsonText = message.substring(CODE_DIM, message.length());
				Log.d(TAG, jsonText);
				parser = new ParseJSON(new StringReader(jsonText));
				try {
					media = parser.readImageMediaJson();
				} catch (IOException e) {
					Log.e(TAG, "Problema lettura: " + e.getMessage());
				}
				media.setSender_email(sender_email);
				return new Notifica(media);
	
				/*
				 *  ===== Commento ad una foto =====
				 */
				
			case 005:
				jsonText = message.substring(CODE_DIM, message.length());
				Log.d(TAG, jsonText);
				parser = new ParseJSON(new StringReader(jsonText));
				note = new Note();
				try {
					note = parser.readNote();
				} catch (IOException e) {
					Log.e(TAG, "Problema lettura: " + e.getMessage());
				}
				note.setSender_mail(sender_email);
				return new Notifica(note);
	
				/*
				 *  ===== Messaggi di sistema =====
				 */
				
			case 006:
				jsonText = message.substring(CODE_DIM, message.length());
				Log.d(TAG, jsonText);
				parser = new ParseJSON(new StringReader(jsonText));
				note = new Note();
				try {
					note = parser.readNote();
				} catch (IOException e) {
					Log.e(TAG, "Problema lettura: " + e.getMessage());
				}
				return new Notifica(note.getDate(), note.getContent(),
						MantleMessage.SYSTEM);
	
				/*
				 *  ===== Condivisione di un file =====
				 */
				
			case 007:
				jsonText = message.substring(CODE_DIM, message.length());
				Log.d(TAG, jsonText);
				parser = new ParseJSON(new StringReader(jsonText));
				try {
					media = parser.readFileMediaJson();
				} catch (IOException e) {
					Log.e(TAG, "Problema lettura: " + e.getMessage());
				}
				media.setSender_email(sender_email);
				return new Notifica(media);
	
			default:
				throw new Error("Codice Errato");
		}
	}
}
