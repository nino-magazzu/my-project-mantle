package com.project.mantle_v1.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class MioDatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "Mantle";
	private static final int DB_VERSION = 11;
	final SQLiteDatabase db;

	// private String username;
	// private String email;
	// private String password;
	// private int idUser;

	// costruttore della classe
	public MioDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		db = this.getWritableDatabase();
	}

	// Creazione del database

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "";
		sql += "CREATE TABLE User (";
		sql += " idUser INTEGER PRIMARY KEY,";
		sql += " email TEXT,";
		sql += " username TEXT,";
		sql += " name TEXT,";
		sql += " surname TEXT,";
		sql += " key TEXT";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE Service (";
		sql += " service TEXT,";
		sql += " useracces TEXT,";
		sql += " passacces TEXT";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE Share (";
		sql += " idFile INTEGER,";
		sql += " idUser INTEGER";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE File (";
		sql += " idFile INTEGER PRIMARY KEY,";
		sql += " fileName TEXT,";
		sql += " linkFile TEXT,";
		sql += " linkThumbnail TEXT,";
		sql += " linkComment TEXT,";
		sql += " fileKey TEXT,";
		sql += " mimeType TEXT,";
		sql += " priority INTEGER";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE History (";
		sql += " idFile INTEGER,";
		sql += " idUser INTEGER,";
		sql += " date TEXT";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE Team (";
		sql += " idTeam INTEGER PRIMARY KEY,";
		sql += " description TEXT";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE Members (";
		sql += " idTeam INTEGER,";
		sql += " email TEXT";
		sql += ")";
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// ============== QUERY FUNZIONALI PER L'APPLICAZIONE =============== //

	//verifica se l'utente sarà mandata la richiesta d'amicizia è gia presente nella lista dei contatti
	public boolean isAlreadyFriend(String email) {

		String[] columns = { "Username" };
		String selection = "email = ?";
		String[] selectionArg = { email };
		Cursor cursor = db.query("User", columns, selection, selectionArg,
				null, null, null);
		Integer i = cursor.getCount();

		if (i < 1) {
			return false;
		} else {
			return true;
		}

	}

	// verifica che il servizio mantle esiste 
	public boolean serviceMantle() {
		String[] columns = { "service" };
		String selection = "service = 'mantle'";
		Cursor cursor = db.query("Service", columns, selection, null, null,
				null, null);

		int i = cursor.getCount();

		if (i < 1) {
			return false;
		}

		else {
			return true;
		}

	}
	
	//verifica se già esiste il team  
	public boolean teamExist(String team) {
		String[] columns = { "idTeam" };
		String selection = "description = ?";
		String[] selectionArg = { team };
		Cursor cursor = db.query("Team", columns, selection, selectionArg,
				null, null, null);
		Integer i = cursor.getCount();

		if (i < 1) {
			return false;
		} else {
			return true;
		}

	}

	// verifica se l'utente è registrato e restituisce username e password oppure " "," " se l'utente non è registrato nel db
	public String[] login() {
		String selection = "service = 'mantle'";
		String[] columns = { "useracces", "passacces" };
		Cursor cursor = db.query("Service", columns, selection, null, null,
				null, null);

		Integer i = cursor.getCount();
		Log.d("MIODATABASEHELPER", "i = " + i.toString());

		// non c'è il servizio mantle l'utente deve registrarsi
		if (i < 1) {
			String[] res = new String[2];
			res[0] = " ";
			res[1] = " ";
			Log.d("MiodatabaseHelper",
					"L'utente non è registrato sto restituendo res[0]= "
							+ res[0] + " res[1] = " + res[1]);
			return res;

		}

		// ho trovato il servizio restituisco anche user e password per
		// effettuare il controllo
		else {
			String[] res = new String[i * 2];
			i = 0;

			// cursor.moveToNext();
			// Log.d("MioDatabaseHelper query result = ",cursor.getString(0)+cursor.getString(1)+cursor.getString(2));

			while (cursor.moveToNext()) {
				res[i] = cursor.getString(0);
				res[i + 1] = cursor.getString(1);
				Log.d("MIO_DATABASE_HELPER", res[i] + " " + res[i + 1]);
				i = i + 2;
			}
			Log.d("MIO_DATABASE_HELPER",
					"L'utente è registrato sto restituendo res[0]= " + res[0]
							+ " res[1] = " + res[1]);
			return res;
		}

	}

	// esportare database
		public void exportDB() {

			try {
				File sd = new File(Environment.getExternalStorageDirectory()
						+ "/Mantle/db");
				File data = Environment.getDataDirectory();

				if (!sd.exists())
					sd.mkdirs();

				// if (sd.canWrite()) {
				String currentDBPath = "/data/com.project.mantle_v1/databases/Mantle";
				String backupDBPath = "Mantle";

				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Log.d("MIO_DATABASE_HELPER", "Mantle db exported");
				// }
			} catch (Exception e) {

				Log.w("MIO_DATABASE_HELPER", "Mantle db not exported" + e);

			}
		}

		// importare database
		public void importDB() {
			// TODO Auto-generated method stub

			try {
				File sd = new File(Environment.getExternalStorageDirectory()
						+ "/Mantle/db");
				File data = Environment.getDataDirectory();

				if (!sd.exists())
					sd.mkdirs();

				// if (sd.canWrite()) {
				String currentDBPath = "/data/com.project.mantle_v1/databases/Mantle";
				String backupDBPath = "Mantle";
				File backupDB = new File(data, currentDBPath);
				File currentDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Log.d("MIO_DATABASE_HELPER", "Mantle db imported");
				// }
			} catch (Exception e) {
				Log.w("MIO_DATABASE_HELPER", "Mantle db not imported " + e);
			}
		}

	// ============== QUERY PER L'INSERIMENTO DEI CAMPI SUL DB =============== //
	
	public long insertUser(String email, String username, String name,
			String surname, String key) {
		ContentValues values = new ContentValues();
		values.put("email", email);
		values.put("username", username);
		values.put("name", name);
		values.put("surname", surname);
		values.put("key", key);
		long r = db.insert("User", null, values);
		return r;
	}

	public long insertService(String service, String useracces, String passacces) {
		ContentValues values = new ContentValues();
		values.put("service", service);
		values.put("useracces", useracces);
		values.put("passacces", passacces);
		long r = db.insert("Service", null, values);
		return r;
	}

	public long insertFile(String fileName, String linkFile, String linkThumbnail,
			String linkComment, String fileKey, String mimeType, int priority) {
		ContentValues values = new ContentValues();
		values.put("fileName", fileName);
		values.put("linkFile", linkFile);
		values.put("linkThumbnail", linkThumbnail);
		values.put("linkComment", linkComment);
		values.put("fileKey", fileKey);
		values.put("priority", priority);
		values.put("mimeType", mimeType);
		long r = db.insert("File", null, values);
		return r;
	}

	public long insertShare(int idFile, int idUser) {
		ContentValues values = new ContentValues();
		values.put("idFile", idFile);
		values.put("idUser", idUser);
		long r = db.insert("Share", null, values);
		return r;
	}

	public long insertShare(int idFile, String email) {
		int idUser = getId(email);
		ContentValues values = new ContentValues();
		values.put("idFile", idFile);
		values.put("idUser", idUser);
		long r = db.insert("Share", null, values);
		return r;
	}

	public long insertHistory(int idFile, int idUser, String date) {
		ContentValues values = new ContentValues();
		values.put("idFile", idFile);
		values.put("idUser", idUser);
		values.put("date", date);
		long r = db.insert("History", null, values);
		return r;
	}

	public void insertLinkThumbnail(int idFile, String link) {

		ContentValues values = new ContentValues();
		values.put("linkThumbnail", link);
		String whereClause = "idFile = ?";
		String[] whereArgs = { String.valueOf(idFile) };
		db.update("File", values, whereClause, whereArgs);
	}
	
	public void insertLinkComment(int idFile, String link) {

		ContentValues values = new ContentValues();
		values.put("linkComment", link);
		String whereClause = "idFile = ?";
		String[] whereArgs = { String.valueOf(idFile) };
		db.update("File", values, whereClause, whereArgs);
	}

	public void insertPriority(int idFile, int priority) {
		ContentValues values = new ContentValues();
		values.put("priority", priority);
		String whereClause = "idFile = ?";
		String[] whereArgs = { String.valueOf(idFile) };
		db.update("File", values, whereClause, whereArgs);
	}

	public long insertTeam(String teamName) {

		ContentValues values = new ContentValues();
		values.put("description", teamName);
		long r = db.insert("Team", null, values);
		return r;
	}

	public void insertMembers(Object[] email, int idTeam) {

		ContentValues values = new ContentValues();
		values.put("idTeam", idTeam);

		for (int i = 0; i < email.length; i++) {
			values.put("email", email[i].toString());
			db.insert("Members", null, values);
		}
	}

	//============== METODI PER ELIMINARE CAMPI DAL DATABASE =============== //

	public void deleteAll() {
		db.delete("User", null, null);
		db.delete("Service", null, null);
		db.delete("File", null, null);
		db.delete("Share", null, null);
		db.delete("History", null, null);

	}

	// eliminare l'utente sia dalla tabella User,Members
	public void deleteFriend(String email) {

		String whereClause = "email = ?";
		String[] whereArgs = { email };

		db.delete("User", whereClause, whereArgs);
		db.delete("Members", whereClause, whereArgs);

		Log.d("MIO_DATABASE_HELPER", "Ho elimnato l'utente richiesto : "
				+ email);

	}

	public void deleteMembers(String idTeam, String email) {

		String whereClause = "idTeam = ? AND email = ?";
		String[] whereArgs = { idTeam, email };

		db.delete("Members", whereClause, whereArgs);

	}

	public void deleteTeam(String teamName) {
		int idTeam = getIdTeam(teamName);
		String whereClause = "idTeam = ? AND description = ?";
		String[] whereArgs = { String.valueOf(idTeam), teamName };
		db.delete("Team", whereClause, whereArgs);
		whereClause = "idTeam = ?";
		String[] whereArgs2 = { String.valueOf(idTeam) };
		db.delete("Members", whereClause, whereArgs2);
	}

	// ============== QUERY PER PRELEVARE INFORMAZIONI =============== //

	// Prelevare l'id di un determinato utente dal nome e dal cognome
	public int getId(String name, String surname) {

		// quale campo mi restituisce la query
		String[] columns = { "idUser" };
		// clausola where
		String selection = "name = ? AND surname = ?";
		// cosa devo sostituire al posto dei ?
		String[] selectionArgs = { name, surname };
		// esecuzione della query
		Cursor c = db.query("User", columns, selection, selectionArgs, null,
				null, null);

		c.moveToNext();

		return c.getInt(0);
	}

	//Ricavare l'idUser dalla sua email
	public int getId(String email) {
		// quale campo mi restituisce la query
		String[] columns = { "idUser" };
		// clausola where
		String selection = "email = ?";
		// cosa devo sostituire al posto dei ?
		String[] selectionArgs = { email };
		// esecuzione della query
		Cursor c = db.query("User", columns, selection, selectionArgs, null,
				null, null);

		c.moveToNext();

		return c.getInt(0);
	}

	//Ricavare la mail di un tente a partire dal link del file che ha condiviso
	public String getEmailFromUrl(String url) {
		// Dalla tabella file ricavo l'id del file
		String[] columns = { "idFile" };
		String selection = "linkFile = ? OR linkComment= ? ";
		String[] selectionArgs = { url, url };
		Cursor c = db.query("File", columns, selection, selectionArgs, null,
				null, null);
		c.moveToNext();
		String idFile = c.getString(0);

		Log.d("QUERY PER RICAVARE LA MAIL", "1/3) id del File = " + idFile);

		// Da share con l'idFile ricavo utente proprietario
		String[] columns2 = { "idUser" };
		selection = "idFile = ?";
		String[] selectionArgs2 = { idFile };
		c = db.query("History", columns2, selection, selectionArgs2, null,
				null, null);
		c.moveToNext();
		String idUser = c.getString(0);

		Log.d("QUERY PER RICAVARE LA MAIL", "2/3) id dell'user = " + idUser);

		// Dall'id dell'utente ricavo la mail
		String[] columns3 = { "email" };
		selection = "idUser = ?";
		String[] selectionArgs3 = { idUser };
		c = db.query("User", columns3, selection, selectionArgs3, null, null,
				null);
		c.moveToNext();
		String email = c.getString(0);

		Log.d("QUERY PER RICAVARE LA MAIL", "3/3) la mail dell'utente = "
				+ email);

		return email;
	}

	//Ricavare il link di condivisione del file a partire dal link del file dei commenti
	public String getLinkfromLinkComment(String linkComment) {
		String[] columns = { "linkFile" };
		String selection = "linkComment = ?";
		String[] selectionArgs = { linkComment };
		Cursor c = db.query("File", columns, selection, selectionArgs, null,
				null, null);
		c.moveToNext();
		String linkFile = c.getString(0);
		return linkFile;
	}

	//Ricavare un vettore contenente : 1)name,surname; 2)email; di tutti i contatti
	public String[] getFriends() {
		String[] columns = { "name", "surname", "email" };
		String selection = "idUser != 1";
		Cursor c = db.query("User", columns, selection, null, null, null, null);
		int i = c.getCount();
		String[] result = new String[i * 2];
		i = 0;

		while (c.moveToNext()) {
			result[i] = c.getString(0) + " " + c.getString(1);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			result[i + 1] = c.getString(2);
			i = i + 2;
		}

		return result;
	}

	//Ottenere un vettore 0)name,surname; 1)email; di un utente 
	public String[] getMembersInfo(String[] email) {
		String[] result = new String[(email.length) * 2];
		String[] columns = { "name", "surname" };
		for (int i = 0, j = 0; i < email.length; i++) {
			String selection = "email=?";
			String[] selectionArgs = { email[i] };
			Cursor c = db.query("User", columns, selection, selectionArgs,
					null, null, null);
			c.moveToNext();
			result[j] = c.getString(0) + " " + c.getString(1);
			result[j + 1] = email[i];
			j = j + 2;
		}

		return result;
	}
	
	//Ottenere un vettore 0)name,surname; 1)username; degli utenti con cui è stato condiviso un file	
	public String[] getSharers(String idFile) {

		String[] columns = { "idUser" };
		String selection = "idFile=?";
		String[] selectionArgs = { idFile };
		Cursor c = db.query("Share", columns, selection, selectionArgs, null,
				null, null);

		int i = c.getCount();
		
		if(i<1){
			Log.d("MIO_DATABASE_HELPER","Il file non è stato mai condiviso");
			String[] noResult={"",""};
			return noResult;
			
		}
		
		else{
			String[] idUser = new String[i];
			String[] result = new String[i * 2];
			i = 0;

			while (c.moveToNext()) {
				idUser[i] = c.getString(0);
				i++;
			}

			String[] columns2 = { "name", "surname", "username" };
			selection = "idUser=?";
			int j = 0;
					
			for ( j = 0, i = 0; i < idUser.length; j++) {
				String[] selectionArgs2 = { idUser[j] };
				c = db.query("User", columns2, selection, selectionArgs2, null,
						null, null);
				c.moveToNext();
				
				result[i] = c.getString(0) + c.getString(1);
				Log.d("MIO_DATABASE_HELPER","result["+i+"]"+result[i]);
				result[i + 1] = c.getString(2);
				i = i + 2;
			}
			return result;
		}

		
	}

	//Ottenere un vettore contenente le informazioni(il nome)di tutti i gruppi creati
	public String[] getTeams() {
		String[] columns = { "description" };
		Cursor c = db.query("Team", columns, null, null, null, null, null);
		int i = c.getCount();
		String[] result = new String[i];
		i = 0;

		while (c.moveToNext()) {
			result[i] = c.getString(0);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

		return result;

	}

	//Ottenere tutti i membri relativi ad una particolare cerchia
	public String[] getMembers(String teamName) {
		Log.d("MIO_DATABASE_HELPER", "il nome del team = " + teamName);
		String[] columns = { "idTeam" };
		String selection = "description = ?";
		String[] selectionArgs = { teamName };
		Cursor c = db.query("Team", columns, selection, selectionArgs, null,
				null, null);

		int i = c.getCount();

		c.moveToNext();

		String idTeam = c.getString(0);

		String[] columns2 = { "email" };
		selection = "idTeam = ?";
		String[] selectionArgs2 = { idTeam };
		c = db.query("Members", columns2, selection, selectionArgs2, null,
				null, null);

		i = c.getCount();
		String[] result = new String[i];
		i = 0;

		while (c.moveToNext()) {
			result[i] = c.getString(0);
			i++;
		}
		return result;
	}

	//Ottere la password del proprio indirizzo email inserito durante la registrazione
	public String getPassword(String email) {

		String[] columns = { "passacces" };
		String selection = "useracces=? AND service=?";
		String[] selectionArgs = { email, "Email" };
		Cursor c = db.query("Service", columns, selection, selectionArgs, null,
				null, null);
		c.moveToNext();
		return c.getString(0);
	}

	//Ottenere tutte le informazioni relative al proprio contatto
	public String[] getUser() {
		String selection = "idUser=1";
		Cursor c = db.query("User", null, selection, null, null, null, null);
		String[] result = new String[6];
		c.moveToNext();
		for (int i = 0; i < 6; i++) {
			result[i] = c.getString(i);
		}
		return result;
	}

	//Ottenere tutte le informazioni relative ad un utente specifico conoscendo il suo idUser
	public String[] getUser(Integer id) {
		String selection = "idUser=?";
		String[] selectionArgs = { id.toString() };
		Cursor c = db.query("User", null, selection, selectionArgs, null, null,
				null);
		String[] result = new String[6];
		c.moveToNext();
		for (int i = 0; i < 6; i++) {
			result[i] = c.getString(i);
		}
		return result;
	}

	//Ottenere tutte le informazioni relative ad un utente specifico conoscendo la sua email
	public User getUser(String email) {
		String selection = "email = ?";
		String[] selectionArgs = { email };
		Cursor c = db.query("User", null, selection, selectionArgs, null, null,
				null);
		c.moveToNext();
		User user = new User(c.getString(0), c.getString(1), c.getString(2),
				c.getString(3), c.getString(4), c.getString(5));
		return user;
	}

	//Ottenere l'idFile conoscendo il link di condivisione
	public int getIdFile(String linkFile) {
		String[] columns = { "idFile" };
		String selection = "linkFile=?";
		String[] selectionArgs = { linkFile };
		Cursor c = db.query("File", columns, selection, selectionArgs, null,
				null, null);
		c.moveToNext();
		int idFile = c.getInt(0);
		return idFile;
	}
	
	//Ottenere un vettore che contiene 0)idFile; 1)fileName; 2)linkFile; 3)linkThumbnail
	//4)linkComment; 5)fileKey; 6)mimeType; 7)priority; di un file dato il suo idFile
	public String[] getFile(String idFile) {
		String selection = "idFile=?";
		String[] selectionArgs = { idFile };
		Cursor c = db.query("File", null, selection, selectionArgs, null, null,
				null);
		String[] result = new String[8];
		c.moveToNext();
		for (int i = 0; i < 8; i++) {
			result[i] = c.getString(i);
		}
		return result;
	}

	//Ottenere l'idTeam conoscendo il suo teamName
	public int getIdTeam(String teamName) {
		String[] columns = { "idTeam" };
		String selection = "description = ?";
		String[] selectionArgs = { teamName };
		Cursor c = db.query("Team", columns, selection, selectionArgs, null,
				null, null);
		c.moveToNext();
		int idTeam = c.getInt(0);
		return idTeam;
	}

	//Ottenere la data in cui è stato condiviso il file
	public String getDateFile(int idFile) {
		String[] columns = { "Date" };
		String selection = "idFile = ?";
		String[] selectionArgs = { String.valueOf(idFile) };
		Cursor c = db.query("History", columns, selection, selectionArgs, null,
				null, null);
		c.moveToNext();
		String date = c.getString(0);
		return date;
	}

	// Da l'idFile ricavo le email delle persone con cui ho condiviso il file
	public String[] getEmailsFilesShared(int idFile) {

		String[] columns = { "idUser" };
		String selection = "idFile = ?";
		String[] selectionArgs = { String.valueOf(idFile) };
		Cursor c = db.query("Share", columns, selection, selectionArgs, null,
				null, null);
		int i = c.getCount();
		String[] result = new String[i];
		String[] emails = new String[i];
		i = 0;

		while (c.moveToNext()) {
			result[i] = c.getString(0);
			i++;
		}

		String[] columns2 = { "email" };
		selection = "idUser=?";

		for (int j = 0; j < result.length; j++) {
			String[] selectionArgs2 = { result[j] };
			c = db.query("User", columns2, selection, selectionArgs2, null,
					null, null);
			c.moveToNext();
			emails[j] = c.getString(0);
		}

		return emails;
	}

	//Ottenere un ArrayList di MantleFile di tutti i file salvati sul db
	public ArrayList<MantleFile> getAllFile() {
		
		String selection = "idFile != 1";
		Cursor c = db.query("File", null, selection, null, null, null, null);
		ArrayList<MantleFile> arr = new ArrayList<MantleFile>();

		while (c.moveToNext()) {
			MantleFile mf = new MantleFile();
			mf.setIdFile(c.getString(0));
			mf.setFileName(c.getString(1));
			mf.setLinkFile(c.getString(2));
			mf.setLinkThumb(c.getString(3));
			mf.setLinkComment(c.getString(4));
			mf.setFileKey(c.getString(5));
			mf.setObjectType(c.getString(6));
			mf.setPriority(c.getInt(7));
			arr.add(mf);
			Log.d("MIO_DATABASE_HELPER",
					"ho aggiunto questo filename:" + c.getString(1));
		}
		return arr;
	}

	// ============== METODI PER LA VISUALIZZAZIONE DEL DATABASE SUL LOG ===============

	public void showAll() {

		int i;

		Cursor cursor = db.query("User", null, null, null, null, null, null);
		i = cursor.getCount();
		String[] result = new String[i];
		i = 0;

		Log.d("MIO_DATABASE_HELPER", "------USER-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1) + " "
					+ cursor.getString(2) + " " + cursor.getString(3) + " "
					+ cursor.getString(4) + " " + cursor.getString(5);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

		cursor = db.query("Service", null, null, null, null, null, null);
		i = cursor.getCount();
		result = new String[i];
		i = 0;
		Log.d("MIO_DATABASE_HELPER", "------SERVICE-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1) + " "
					+ cursor.getString(2);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

		cursor = db.query("File", null, null, null, null, null, null);
		i = cursor.getCount();
		result = new String[i];
		i = 0;
		Log.d("MIO_DATABASE_HELPER", "------FILE-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1) + " "
					+ cursor.getString(2) + " " + cursor.getString(3) + " "
					+ cursor.getString(4)+" " + cursor.getString(5)+" " + cursor.getString(6)+" " + cursor.getString(7);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

		cursor = db.query("Share", null, null, null, null, null, null);
		i = cursor.getCount();
		result = new String[i];
		i = 0;
		Log.d("MIO_DATABASE_HELPER", "------SHARE-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

		cursor = db.query("History", null, null, null, null, null, null);
		i = cursor.getCount();
		result = new String[i];
		i = 0;
		Log.d("MIO_DATABASE_HELPER", "------HISTORY-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1) + " "
					+ cursor.getString(2);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

		cursor = db.query("Team", null, null, null, null, null, null);
		i = cursor.getCount();
		result = new String[i];
		i = 0;
		Log.d("MIO_DATABASE_HELPER", "------TEAM-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

		cursor = db.query("Members", null, null, null, null, null, null);
		i = cursor.getCount();
		result = new String[i];
		i = 0;
		Log.d("MIO_DATABASE_HELPER", "------MEMBERS-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}
	}
}
