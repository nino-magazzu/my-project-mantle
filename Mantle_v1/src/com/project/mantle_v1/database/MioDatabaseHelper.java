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
	private static final int DB_VERSION = 11;
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
	    public String[] login(){
	    	String selection = "service = 'mantle'";
	    	String[] columns = {"useracces","passacces"};
	    	Cursor cursor = db.query("Service", columns, selection, null, null, null, null);
	    	
	    	Integer i = cursor.getCount();
	    	Log.d("MIODATABASEHELPER","i = " + i.toString());
	    	
	    	//non c'è il servizio mantle l'utente deve registrarsi 
	    	if(i<1){
	    		String[] res = new String[2];
		    	res[0]= " ";
		    	res[1]= " ";
		    	Log.d("MiodatabaseHelper","L'utente non è registrato sto restituendo res[0]= " + res[0] + " res[1] = "  + res[1]);
		    	return res;
	    		
	    	}
	    	
	    	//ho trovato il servizio restituisco anche user e password per effettuare il controllo
	    	else{
	    		String[] res = new String[i*2];
	    		i = 0;
	    		
	    		//cursor.moveToNext();
	    		//Log.d("MioDatabaseHelper query result = ",cursor.getString(0)+cursor.getString(1)+cursor.getString(2));
	    		
	    		while (cursor.moveToNext()) {
	 	    		res[i] = cursor.getString(0);
	 	    		res[i+1]= cursor.getString(1);
	 	    		Log.d("MIO_DATABASE_HELPER", res[i]+" "+res[i+1]);
	 	    		i=i+2;
	 	    	}
	    		Log.d("MIO_DATABASE_HELPER","L'utente è registrato sto restituendo res[0]= " + res[0] + " res[1] = "  + res[1]);
	    		return res;	
	    	}
	    		
	    }

	    //elimina l'utente sia dalla tabella friend, sia da contact
	    public void deleteFriend(String email){
	    	
	    	String whereClause = "email = ?";
	    	String[] whereArgs = { email };
	    	
	    	db.delete("User", whereClause, whereArgs);
	    	Log.d("MIO_DATABASE_HELPER","Ho elimnato l'utente richiesto : " + email);
	    	
	    }

/*
	    
	    public long insertFile(SQLiteDatabase db, String name, String service, String link){
	    	ContentValues values = new ContentValues();
	        values.put("fileName" , name);
	        values.put("service" , service);
	    	values.put("link" , link);
	        long r = db.insert("file", null, values);
	        return r;
	    	
	    }
	    
*/
	    
// ============== METODI PER LA VISUALIZZAZIONE DEL DATABASE ===============


	public void showAll() {
				
		int i;
		
	    Cursor cursor = db.query("User", null, null, null, null, null, null);
	    i = cursor.getCount();
	    String[] result = new String[i];
	    i = 0;

	    Log.d("MIO_DATABASE_HELPER", "------USER-----");
	    while (cursor.moveToNext()) {
	    		result[i] = cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4)+" "+cursor.getString(5);
	    		Log.d("MIO_DATABASE_HELPER", result[i]);
	    		i++;
	    	}
	    
	    cursor = db.query("Service", null, null, null, null, null, null);
	    i = cursor.getCount();
	    result = new String[i];
	    i = 0;
	    Log.d("MIO_DATABASE_HELPER", "------SERVICE-----");
	    while (cursor.moveToNext()) {
	    		result[i] = cursor.getString(0) + " " + cursor.getString(1)+ " " + cursor.getString(2);
	    		Log.d("MIO_DATABASE_HELPER", result[i]);
	    		i++;
	    	}
	    
	    cursor = db.query("File", null, null, null, null, null, null);
	    i = cursor.getCount();
	    result = new String[i];
	    i = 0;
	    Log.d("MIO_DATABASE_HELPER", "------FILE-----");
	    while (cursor.moveToNext()) {
	    		result[i] = cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4);
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
	    		result[i] = cursor.getString(0) + " " + cursor.getString(1)+" "+cursor.getString(2);
	    		Log.d("MIO_DATABASE_HELPER", result[i]);
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
    		Log.d("MIO_DATABASE_HELPER", result[i]);
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
    		Log.d("MIO_DATABASE_HELPER", result[i]);
    		i++;
    	}
    	
	}
    
    public boolean isAlreadyFriend(String email){

    	String[] columns = {"Username"};
    	String selection = "email = ?";
    	String[] selectionArg = {email};
    	Cursor cursor = db.query("User",columns,selection,selectionArg,null,null,null);
    	Integer i = cursor.getCount();
    	
    	if(i<1){
    		return false;
    	}
    	else{
    		return true;
    	}
    
    }


	public String[] getFriends() {
		String[] columns = {"name","surname","email"};
    	String selection = "idUser != 1";
    	Cursor c = db.query("User",columns,selection,null,null,null,null);
    	int i = c.getCount();
    	String[] result = new String[i*2];
    	i = 0;
    	
    	while (c.moveToNext()) {
    		result[i] = c.getString(0) + " " + c.getString(1);
    		Log.d("MIO_DATABASE_HELPER", result[i]);
    		result[i+1] = c.getString(2);
    		i=i+2;
    	}
    	
		return result;
	}
    
}

