package com.project.mantle_v1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MioDatabaseHelper extends SQLiteOpenHelper{
	
	private static final String DB_NAME = "Mantle";
	private static final int DB_VERSION = 6;
	final SQLiteDatabase db;
	private String username;
	private String email;
	private String password;
	private int idUser;
	
	//costruttore della classe
	public MioDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		db = this.getWritableDatabase();
	}

	
	//Creazione del database

	@Override
	public void onCreate(SQLiteDatabase db) {
	
		String sql = "";
		sql += "CREATE TABLE User (";
		sql += " idUser INTEGER,";
		sql += " email TEXT,";
		sql += " username TEXT,";
		sql += " name TEXT,";
		sql += " surname TEXT,";
		sql += " key TEXT,";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE service (";
		sql += " service TEXT,";
		sql += " useracces TEXT,";
		sql += " passacces TEXT,";
		sql += ")";
		db.execSQL(sql);
		
		sql = "";
		sql += "CREATE TABLE share (";
		sql += " idFile INTEGER,";				
		sql += " idFriend INTEGER,";
		sql += ")";
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			}

	
	//-- metodo di inizializzazione
		// settare i parametri username, password....di mioi databasehelper
		// verifica se i parametri ci sono nella tabella user
			//se ci sono mi setta anche l'id user e restituisce true
			//se non ci sono li aggiunge setta l'id user e restituisce false 
	//--mentodo che controlla email
	//--metodi per l'inserimenti nelle tabelle
	
//============== METODI PER LA GESTIONE DEL DATABASE ===============
    
	
//Prelevo l'id di un determinato utente dal nome e dal cognome
	    public String getId(SQLiteDatabase db, String name, String surname){
	    	
	    	//quale campo mi restituisce la query
	    	String[] columns = { "_id" };
	    	//clausola where
	    	String selection = "name = ? AND surname = ?";
	    	//cosa devo sostituire al posto dei ?
	    	String[] selectionArgs = { name , surname };
	    	//esecuzione della query
	    	Cursor c = db.query("friend", columns, selection, selectionArgs, null, null, null);

	    	c.moveToNext();
	    	
	    	return c.getString(0);
	    }
		
		
	    public long insertFriend(SQLiteDatabase db,String name,String surname){
	    	ContentValues values = new ContentValues();
	        values.put("name", name);
	        values.put("surname", surname);
	        long r = db.insert("friend", null, values);
	        return r;
	    }
	    
		
	    public long insertContact(SQLiteDatabase db, String name, String surname, String email){
	    	ContentValues values = new ContentValues();
	        String _id = getId(db, name, surname);
	    	values.put("_id",_id);
	    	values.put("email", email);
	        long r = db.insert("contact", null, values);
	        return r;
	    	
	    }
	    
	    public long insertFile(SQLiteDatabase db, String name, String service, String link){
	    	ContentValues values = new ContentValues();
	        values.put("fileName" , name);
	        values.put("service" , service);
	    	values.put("link" , link);
	        long r = db.insert("file", null, values);
	        return r;
	    	
	    }
	    
	    //elimina l'utente sia dalla tabella friend, sia da contact
	    public void deleteFriend(SQLiteDatabase db, String name , String surname){
	    	
	    	String _id = getId(db, name, surname);
	    	Log.d("Id : ",_id);
	    	
	    	String whereClause = "_id = ?";
	    	String[] whereArgs = { _id };
	    	
	    	db.delete("friend", whereClause, whereArgs);
	    	db.delete("contact", whereClause, whereArgs);
	    	
	    }
	    
	    public void deleteAll(SQLiteDatabase db){
	    	db.delete("friend", null,null);
	        db.delete("contact", null, null);
	        db.delete("file", null, null);
	      
	    }
	

// ============== METODI PER LA VISUALIZZAZIONE DEL DATABASE ===============

	    
	  
    public String[] showFrind(SQLiteDatabase db) {
    	
    	Cursor cursor = db.query("friend", null, null, null, null, null, null);
    	int i = cursor.getCount();
    	String[] result = new String[i];
    	i = 0;
    	
    	while (cursor.moveToNext()) {
    		result[i] = cursor.getString(1) + " " + cursor.getString(2);
    		i++;
    	}
    	
    	return result;
	}
    
    
    public String[] showContact(SQLiteDatabase db) {
    	
    	Cursor cursor = db.query("contact", null, null, null, null, null, null);
    	int i = (cursor.getCount())*2;
    	String[] result = new String[i];
    	i = 0;
    	
    	while (cursor.moveToNext()) {
    	      
    		result[i] = cursor.getString(0);
    		result[i+1] = cursor.getString(1);
    		i=i+2;
    	}
    	
    	return result;
    	
	}
    

    public void showFile(SQLiteDatabase db) {
    	Cursor cursor = db.query("file", null, null, null, null, null, null);

    	while (cursor.moveToNext()) {
    		String idFile = cursor.getString(0);
    		String name = cursor.getString(1);
    		String service = cursor.getString(2);
    		String link = cursor.getString(3);
    		
    		Log.d("id : ",idFile);
    		Log.d("name : ",name);
    		Log.d("service : ",service);
    		Log.d("link : ",link);
    	
    	}

	}

	
}
