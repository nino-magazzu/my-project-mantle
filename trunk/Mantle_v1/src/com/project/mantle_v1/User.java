package com.project.mantle_v1;

import android.content.Context;

public class User {
	private int idUser;
	private String email;
	private String username;
	private String name;
	private String surname;
	private String key;
	
	public User(int idUser, String email, String username, String name, String surname, String key){
		this.idUser = idUser;
		this.email = email;
		this.username = username;
		this.name = name;
		this.surname = surname;
		this.key = key;
	}
	
	// Costruttore per prendere uno specifico amico
	
	public User(Context cont, int id) {
		
	}
	
	
	// Costrutore per prendere i dati del proprietario dell'app
	public User(Context cont) {
		
	}
	
	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
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
}
