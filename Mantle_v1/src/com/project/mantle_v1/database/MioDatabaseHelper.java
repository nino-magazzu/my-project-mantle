package com.project.mantle_v1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class MioDatabaseHelper extends SQLiteOpenHelper {
	
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
		sql += " linkComment TEXT,";
		sql += " fileKey TEXT";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE History (";
		sql += " idFile INTEGER,";				
		sql += " idUser INTEGER,";
		sql += " date INTEGER";
		sql += ")";
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			}

	
	
//============== METODI PER LA GESTIONE DEL DATABASE ===============
	
//Prelevo l'id di un determinato utente dal nome e dal cognome
	    public String getId(String name, String surname){
	    	
	    	//quale campo mi restituisce la query
	    	String[] columns = { "idUser" };
	    	//clausola where
	    	String selection = "name = ? AND surname = ?";
	    	//cosa devo sostituire al posto dei ?
	    	String[] selectionArgs = { name , surname };
	    	//esecuzione della query
	    	Cursor c = db.query("User", columns, selection, selectionArgs, null, null, null);

	    	c.moveToNext();
	    	
	    	return c.getString(0);
	    }
		
	    public long insertUser(String email, String username, String name, String surname, String key ){
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
	    
	    public void deleteAll(){
	    	db.delete("User", null,null);
	        db.delete("Service", null, null);
	        db.delete("File", null, null);
	        db.delete("Share", null, null);
	        db.delete("History", null, null);
	      
	    }
	    
	    //verifica che il servizio mantle esiste
	    public boolean serviceMantle(){
	    	boolean res = false;
	    	String[] columns = {"service"};
	    	String selection = "service = 'mantle'";
	    	Cursor cursor = db.query("Service", columns, selection, null, null, null, null);
	    	
	    	int i = cursor.getCount();
	    	
	    	if(i<1){
	    		return false;
	    	}
	    	
	    	else{
		    	return true;	
	    	}
	    		
	    }
	  //verifica che il servizio mantle esiste
	    public String[] login(String useracces, String passacces){
	    	String selection = "service = 'mantle'";
	    	String[] columns = {"useracces","passacces"};
	    	Cursor cursor = db.query("Service", columns, selection, null, null, null, null);
	    	
	    	Integer i = cursor.getCount();
	    	Log.d("MIODATABASEHELPER","i = " + i.toString());
	    	
	    	if(i<1){
	    		String[] res = new String[2];
		    	res[0]= " ";
		    	res[1]= " ";
		    	Log.d("MiodatabaseHelper","L'utente non è registrato sto restituendo res[0]= " + res[0] + " res[1] = "  + res[1]);
		    	return res;
	    		
	    	}
	    	
	    	else{
	    		String[] res = new String[i*2];
	    		i = 0;
	    		
	    		//cursor.moveToNext();
	    		//Log.d("MioDatabaseHelper query result = ",cursor.getString(0)+cursor.getString(1)+cursor.getString(2));
	    		
	    		while (cursor.moveToNext()) {
	 	    		res[i] = cursor.getString(0);
	 	    		res[i+1]= cursor.getString(1);
	 	    		Log.d("DATABASE HELPER", res[i]+" "+res[i+1]);
	 	    		i=i+2;
	 	    	}
	    		Log.d("MiodatabaseHelper","L'utente è registrato sto restituendo res[0]= " + res[0] + " res[1] = "  + res[1]);
	    		return res;	
	    	}
	    		
	    }
	    
		/*
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
	    	
	    	String _id = getId(name, surname);
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

*/
	public void showAll() {
				
		int i;
		
	    Cursor cursor = db.query("User", null, null, null, null, null, null);
	    i = cursor.getCount();
	    String[] result = new String[i];
	    i = 0;

	    Log.d("DATABASE HELPER", "------USER-----");
	    while (cursor.moveToNext()) {
	    		result[i] = cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4)+" "+cursor.getString(5);
	    		Log.d("DATABASE HELPER", result[i]);
	    		i++;
	    	}
	    
	    cursor = db.query("Service", null, null, null, null, null, null);
	    i = cursor.getCount();
	    result = new String[i];
	    i = 0;
	    Log.d("DATABASE HELPER", "------SERVICE-----");
	    while (cursor.moveToNext()) {
	    		result[i] = cursor.getString(0) + " " + cursor.getString(1)+ " " + cursor.getString(2);
	    		Log.d("DATABASE HELPER", result[i]);
	    		i++;
	    	}
	    
	    cursor = db.query("File", null, null, null, null, null, null);
	    i = cursor.getCount();
	    result = new String[i];
	    i = 0;
	    Log.d("DATABASE HELPER", "------FILE-----");
	    while (cursor.moveToNext()) {
	    		result[i] = cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4);
	    		Log.d("DATABASE HELPER", result[i]);
	    		i++;
	    	}
	    
	    cursor = db.query("Share", null, null, null, null, null, null);
	    i = cursor.getCount();
	    result = new String[i];
	    i = 0;
	    Log.d("DATABASE HELPER", "------SHARE-----");
	    while (cursor.moveToNext()) {
	    		result[i] = cursor.getString(0) + " " + cursor.getString(1);
	    		Log.d("DATABASE HELPER", result[i]);
	    		i++;
	    	}
	    
	    cursor = db.query("History", null, null, null, null, null, null);
	    i = cursor.getCount();
	    result = new String[i];
	    i = 0;
	    Log.d("DATABASE HELPER", "------HISTORY-----");
	    while (cursor.moveToNext()) {
	    		result[i] = cursor.getString(0) + " " + cursor.getString(1)+" "+cursor.getString(2);
	    		Log.d("DATABASE HELPER", result[i]);
	    		i++;
	    	}
		}    
    
	
	public void showUser() {
    	
    	Cursor cursor = db.query("User", null, null, null, null, null, null);
    	int i = cursor.getCount();
    	String[] result = new String[i];
    	i = 0;
    	
    	while (cursor.moveToNext()) {
    		result[i] = cursor.getString(0) + " " + cursor.getString(1)+ " " + cursor.getString(2);
    		Log.d("DATABASE HELPER", result[i]);
    		i++;
    	}
    	
	}

    
    public void showService() {
    	
    	Cursor cursor = db.query("Service", null, null, null, null, null, null);
    	int i = cursor.getCount();
    	String[] result = new String[i];
    	i = 0;
    	
    	while (cursor.moveToNext()) {
    		result[i] = cursor.getString(0) + " " + cursor.getString(1)+ " " + cursor.getString(2);
    		Log.d("DATABASE HELPER", result[i]);
    		i++;
    	}
    	
	}
    
    
}
