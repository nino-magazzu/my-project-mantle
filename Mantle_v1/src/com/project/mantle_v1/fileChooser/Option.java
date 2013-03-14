package com.project.mantle_v1.fileChooser;

import com.project.mantle_v1.dropbox.Descriptor;


public class Option implements Comparable<Option> {
	
	static final boolean FILE = true;
	static final boolean DESCRIPTOR = false;
	
	private String name;
	private String data;
	private String path;
	private boolean isFile;
	private Descriptor[] list;
	
	public Option(String n, String d, String p, boolean i, Descriptor[] l) {
		name = n;
		data = d;
		path = p;
		isFile = i;
		list = l;
	}
	
	public String getName() {
		return name;
	}
	
	public String getData() {
		return data;
	}

	/*
	 *  Se Option è riferito ad un File questo metodo restituisce quella che è la dimensione del file. 
	 */
	
	public long getSize() {
		String[] splittedData = data.split(" ");
		return Long.valueOf(splittedData[2]);
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean isFile() {
		return isFile;
	}	
	
	public Descriptor[] listFile() {
		return list;
	}
	
	public int compareTo(Option o) {
		if(this.name != null)
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
		else
			throw new IllegalArgumentException();
	}

}
