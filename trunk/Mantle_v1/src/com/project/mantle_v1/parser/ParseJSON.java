package com.project.mantle_v1.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import com.project.mantle_v1.User;
import android.util.JsonReader;
import android.util.JsonWriter;

public class ParseJSON {
	
	public ParseJSON(StringReader Sr) {
		this.sr = Sr;
	}
	
	public ParseJSON(StringWriter Sw) {
		this.sw = Sw;
	}
	
	public String writeJson(MediaType mt) throws IOException {
		this.writer = new JsonWriter(sw);
		writer.setIndent("  ");
		writeMedia(mt);
		writer.close();
		return sw.toString();
	}
	
	public String writeJson(User user) throws IOException {
		this.writer = new JsonWriter(sw);
		writer.setIndent("  ");
		writeUser(user);
		writer.close();
		return sw.toString();
	}
	
	private void writeUser(User user) throws IOException {
		writer.beginObject();
		writer.name("name").value(user.getName());
		writer.name("surname").value(user.getSurname());
		writer.name("username").value(user.getUsername());
		writer.name("email").value(user.getEmail());
		writer.name("publicKey").value(user.getKey());
		writer.endObject();
	}

	private void writeMedia(MediaType media) throws IOException {
		writer.beginObject();
		writer.name("url").value(media.getUrl());
		writer.name("objectType").value(media.getObjectType());
		writer.name("username").value(media.getUsername());
		writer.name("published").value(media.getData());
		if(media.isImage()) {
			writer.name("image");
			imageDetails(media);
			writer.name("fullImage");
			fullImageDetails(media);
		}
		writer.endObject();
	}
	
	
	private void imageDetails(MediaType media) throws IOException {
		writer.beginObject();
		writer.name("icon").value(media.getIcon());
		writer.name("width").value(48);
		writer.name("height").value(48);
		writer.endObject();
	}
	
	private void fullImageDetails(MediaType media) throws IOException {
		writer.beginObject();
		writer.name("url").value(media.getUrl());
		writer.name("width").value(media.getBitmap().getWidth());
		writer.name("height").value(media.getBitmap().getHeight());
		writer.endObject();
	}
	

	public MediaType readMediaJson() throws IOException {
		this.reader = new JsonReader(sr);
		MediaType media = new MediaType();
		try {
			readMedia(media);
		}
		finally {
			reader.close();
		}
		return media;
		
	}
		
	private void readMedia(MediaType media) throws IOException {
		reader.beginObject();
		while(reader.hasNext()) {
				String name = reader.nextName();
				if(name.equals("url")) 
					media.setUrl(reader.nextString());
				else if(name.equals("objectType"))
					media.setObjectType(reader.nextString());
				else if(name.equals("username"))
					media.setUsername(reader.nextString());
				else if(name.equals("published"))
					media.setData(reader.nextString());
				else if(name.equals("icon"))
					media.setIcon(reader.nextString());
		}
		reader.endObject();
	}

	public User readUserJson() throws IOException {
		this.reader = new JsonReader(sr);
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
				if(name.equals("name")) 
					user.setName(reader.nextString());
				else if(name.equals("surname"))
					user.setSurname(reader.nextString());
				else if(name.equals("username"))
					user.setUsername(reader.nextString());
				else if(name.equals("email"))
					user.setEmail(reader.nextString());
				else if(name.equals("publicKey"))
					user.setKey(reader.nextString());
		}
		reader.endObject();
	}

	public String toString() {
		return sw.toString();
	}
	
	private StringWriter sw;
	private StringReader sr;
	private JsonWriter writer;
	private JsonReader reader;
}

