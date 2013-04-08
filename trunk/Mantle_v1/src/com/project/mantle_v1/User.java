package com.project.mantle_v1;

import com.project.mantle_v1.database.MioDatabaseHelper;

import android.content.Context;

public class User {
	
	private String idUser;
	private String email;
	private String username;
	private String name;
	private String surname;
	private String key;
	private MioDatabaseHelper db;
	
	public User(String idUser, String email, String username, String name, String surname, String key){
		this.idUser = idUser;
		this.email = email;
		this.username = username;
		this.name = name;
		this.surname = surname;
		this.key = key;
	}

	public User() {
		super();
		this.idUser = null;
		this.email = null;
		this.username = null;
		this.name = null;
		this.surname = null;
		this.key = null;
	}


	
	// Costruttore per prendere uno specifico amico
	 
	public User(Context cont) {
		db = new MioDatabaseHelper(cont);
		String[] user = db.getUser();
		this.idUser = user[0];
		this.email = user[1];
		this.username = user[2];
		this.name = user[3];
		this.surname = user[4];
		this.key = user[5];
		
	}
	
	
	// Costrutore per prendere i dati del proprietario dell'app
	public User(Context cont, int idUser) {
		db = new MioDatabaseHelper(cont);
		String[] user = db.getUser(String.valueOf(idUser));
		this.idUser = user[0];
		this.email = user[1];
		this.username = user[2];
		this.name = user[3];
		this.surname = user[4];
		this.key = user[5];
		db.close();
	}
	
	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getLongName() {
		return getName() + " " + getSurname() + " (" + getUsername() + ")";
	}
}
