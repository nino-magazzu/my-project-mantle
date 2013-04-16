package com.project.mantle_v1.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import com.project.mantle_v1.MantleImage;
import com.project.mantle_v1.User;
import com.project.mantle_v1.notification_home.Note;

import android.util.JsonReader;
import android.util.JsonWriter;

public class ParseJSON {
	
	final private String USERNAME = "username";
	final private String PUBLISHED = "published";
	final private String OBJECT_TYPE = "objectType";
	final private String FILE_LINK = "url";
	final private String CONTENT = "content";
	final private String ICON = "icon";
	final private String WIDTH = "width";
	final private String HEIGHT = "height";
	final private String NAME = "name";
	final private String SURNAME = "surname";
	final private String PUBLIC_KEY = "publicKey";
	final private String EMAIL = "email";
	final private String IMAGE = "image";
	final private String FULL_IMAGE = "fullImage";
	  
	public ParseJSON(StringReader In) {
		this.in = In;
	}
	
	public ParseJSON(StringWriter Out) {
		this.out = Out;
	}
	
	public String writeJson(Media mt) throws IOException {
		this.writer = new JsonWriter(out);
		writer.setIndent("  ");
		writeMedia(mt);
		writer.close();
		return out.toString();
	}
	
	public String writeJson(Note note) throws IOException {
		this.writer = new JsonWriter(out);
		writer.setIndent("  ");
		writeNote(note);
		writer.close();
		return out.toString();
	}
	
	private void writeNote(Note note) throws IOException {
		writer.beginObject();
		writer.name(USERNAME).value(note.getUser());
		writer.name(PUBLISHED).value(note.getDate());
		writer.name(CONTENT).value(note.getContent());
		writer.name(FILE_LINK).value(note.getFileLink());
		writer.endObject();
		
	}

	public String writeJson(User user) throws IOException {
		this.writer = new JsonWriter(out);
		writer.setIndent("  ");
		writeUser(user);
		writer.close();
		return out.toString();
	}
	/*
	public String writeJson(String content, String producer) throws IOException {
		this.writer = new JsonWriter(out);
		writer.setIndent("  ");
		writeSystemInfo(content, producer);
		writer.close();
		return out.toString();
	}
	
	private void writeSystemInfo(String content, String producer) throws IOException {
		writer.beginObject();
		writer.name(CONTENT).value(content);
		writer.name(USERNAME).value(producer);
		writer.name(PUBLISHED).value(new Date(System.currentTimeMillis()).toString());
		writer.endObject();
		
	}
*/
	private void writeUser(User user) throws IOException {
		writer.beginObject();
		writer.name(NAME).value(user.getName());
		writer.name(SURNAME).value(user.getSurname());
		writer.name(USERNAME).value(user.getUsername());
		writer.name(EMAIL).value(user.getEmail());
		writer.name(PUBLIC_KEY).value(user.getKey());
		writer.endObject();
	}

	
	private void writeMedia(Media media) throws IOException {
		writer.beginObject();
		writer.name(FILE_LINK).value(media.getUrl());
		writer.name(OBJECT_TYPE).value(media.getObjectType());
		writer.name(USERNAME).value(media.getUsername());
		writer.name(PUBLISHED).value(media.getData());
/*		if(media.isImage()) {
			writer.name(IMAGE);
			imageDetails(media);
			writer.name(FULL_IMAGE);
			fullImageDetails(media);
		}*/
		writer.endObject();
	}
	
	
	private void imageDetails(Media media) throws IOException {
		writer.beginObject();
		writer.name(ICON).value(media.getIcon());
		writer.name(WIDTH).value(48);
		writer.name(HEIGHT).value(48);
		writer.endObject();
	}
	
	
	private void fullImageDetails(MantleImage media) throws IOException {
		writer.beginObject();
		writer.name(FILE_LINK).value(media.getLink());
		writer.name(WIDTH).value(media.getWidth());
		writer.name(HEIGHT).value(media.getHeight());
		writer.endObject();
	}
	/*
	public Note readSystemInfo() throws IOException {
		this.reader = new JsonReader(in);
		reader.setLenient(lenient);
		Note note = new Note();
		try {
			readSystemInfo(note);
		}
		finally {
			reader.close();
		}
		return note;
	}
	*/
	public Note readNote() throws IOException {
		this.reader = new JsonReader(in);
		reader.setLenient(lenient);
		Note note = new Note();
		try { 
			readNote(note);
		}
		finally {
			reader.close();
		}
		return note;
	}
	
	private void readNote(Note note) throws IOException {
		reader.beginObject();
		while(reader.hasNext()) {
			String name = reader.nextName();
			if(name.equals(USERNAME))
				note.setUser(reader.nextString());
			else if(name.equals(PUBLISHED))
				note.setDate(reader.nextString());
			else if(name.equals(CONTENT))
				note.setContent(reader.nextString());
			else if(name.equals(FILE_LINK))
				note.setFileLink(reader.nextString());
			
		}
		reader.endObject();
	}
/*
	private void readSystemInfo(Note note) throws IOException {
		reader.beginObject();
		while(reader.hasNext()) {
			String name = reader.nextName();
			if(name.equals(CONTENT))
				note.setContent(reader.nextString());
			else if(name.equals(USERNAME))
				note.content[1] = reader.nextString();
			else if(name.equals(PUBLISHED))
				content[2] = reader.nextString();
		}
		reader.endObject();
	}
*/
	public Media readMediaJson() throws IOException {
		this.reader = new JsonReader(in);
		reader.setLenient(lenient);
		Media media = new Media();
		try {
			readMedia(media);
		}
		finally {
			reader.close();
		}
		return media;
		
	}
		
	private void readMedia(Media media) throws IOException {
		reader.beginObject();
		while(reader.hasNext()) {
				String name = reader.nextName();
				if(name.equals(FILE_LINK)) 
					media.setUrl(reader.nextString());
				else if(name.equals(OBJECT_TYPE))
					media.setObjectType(reader.nextString());
				else if(name.equals(USERNAME))
					media.setUsername(reader.nextString());
				else if(name.equals(PUBLISHED))
					media.setData(reader.nextString());
			/*	else if(name.equals(ICON))
					media.setIcon(reader.nextString());*/
		}
		reader.endObject();
	}

	public User readUserJson() throws IOException {
		this.reader = new JsonReader(in);
		reader.setLenient(lenient);
		User user = new User();
		try {
			readUser(user);
		} 
		finally {
			reader.close();
		}
		return user;
	}
	
	private void readUser(User user) throws IOException {
		reader.beginObject();
		while(reader.hasNext()) {
				String name = reader.nextName();
				if(name.equals(NAME)) 
					user.setName(reader.nextString());
				else if(name.equals(SURNAME))
					user.setSurname(reader.nextString());
				else if(name.equals(USERNAME))
					user.setUsername(reader.nextString());
				else if(name.equals(EMAIL))
					user.setEmail(reader.nextString());
				else if(name.equals(PUBLIC_KEY))
					user.setKey(reader.nextString());
		}
		reader.endObject();
	}

	
	public String toString() {
		return out.toString();
	}
	
	private boolean lenient = false;
	private StringWriter out;
	private StringReader in;
	private JsonWriter writer;
	private JsonReader reader;
	
}