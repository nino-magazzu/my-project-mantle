package com.project.mantle_v1.dropbox;

import java.io.File;
import java.io.Serializable;

public class Descriptor extends File implements Serializable{
	
	// Necessario per la serializzazione 
	private static final long serialVersionUID = 1L;

	// Dimensione del file
	private long DIM;
	
	// indica se il descrittore è riferito ad una cartella o ad un File
	private boolean isDirectory;
	
	// se il descrittore è riferito ad una cartella contiene i riferimenti a file o cartelle in esso contenuti
	private Descriptor[] children;
	
	public Descriptor(String path, long lenght, boolean isDirectory) {
		super(path);
		this.DIM = lenght;
		this.isDirectory = isDirectory;
	}
	
	public long getLenght() {
		return DIM;
	}
	
	public boolean isDirectory() {
		return isDirectory;
	}
	
	public Descriptor[] listFiles() {
		return children;
	}
	
	public void setFiles(Descriptor[] files) {
		this.children = files;
	}
}
