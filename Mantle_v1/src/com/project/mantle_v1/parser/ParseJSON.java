package com.project.mantle_v1.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import com.project.mantle_v1.MantleImage;
import com.project.mantle_v1.User;
import android.util.JsonReader;
import android.util.JsonWriter;

public class ParseJSON {
	  private boolean lenient = false;
	  
	public ParseJSON(StringReader Sr) {
		this.sr = Sr;
	}
	
	public ParseJSON(StringWriter Sw) {
		this.sw = Sw;
	}
	
	public String writeJson(Media mt) throws IOException {
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
	
	public String writeJson(String content, String producer) throws IOException {
		this.writer = new JsonWriter(sw);
		writer.setIndent("  ");
		writeSystemInfo(content, producer);
		writer.close();
		return sw.toString();
	}
	
	private void writeSystemInfo(String content, String producer) throws IOException {
		writer.beginObject();
		writer.name("content").value(content);
		writer.name("username").value(producer);
		writer.name("published").value(new Date(System.currentTimeMillis()).toString());
		writer.endObject();
		
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

	
	private void writeMedia(Media media) throws IOException {
		writer.beginObject();
		writer.name("url").value(media.getUrl());
		writer.name("objectType").value(media.getObjectType());
		writer.name("username").value(media.getUsername());
		writer.name("published").value(media.getData());
/*		if(media.isImage()) {
			writer.name("image");
			imageDetails(media);
			writer.name("fullImage");
			fullImageDetails(media);
		}*/
		writer.endObject();
	}
	
	
	private void imageDetails(Media media) throws IOException {
		writer.beginObject();
		writer.name("icon").value(media.getIcon());
		writer.name("width").value(48);
		writer.name("height").value(48);
		writer.endObject();
	}
	
	
	private void fullImageDetails(MantleImage media) throws IOException {
		writer.beginObject();
		writer.name("url").value(media.getLink());
		writer.name("width").value(media.getWidth());
		writer.name("height").value(media.getHeight());
		writer.endObject();
	}
	
	public String[] readSystemInfo() throws IOException {
		this.reader = new JsonReader(sr);
		reader.setLenient(lenient);
		String[] content = new String[3];
		try {
			readSystemInfo(content);
		}
		finally {
			reader.close();
		}
		return content;
	}
	
	private void readSystemInfo(String[] content) throws IOException {
		reader.beginObject();
		while(reader.hasNext()) {
			String name = reader.nextName();
			if(name.equals("content"))
				content[0] = reader.nextString();
			else if(name.equals("username"))
				content[1] = reader.nextString();
			else if(name.equals("published"))
				content[2] = reader.nextString();
		}
		reader.endObject();
	}

	public Media readMediaJson() throws IOException {
		this.reader = new JsonReader(sr);
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
				if(name.equals("url")) 
					media.setUrl(reader.nextString());
				else if(name.equals("objectType"))
					media.setObjectType(reader.nextString());
				else if(name.equals("username"))
					media.setUsername(reader.nextString());
				else if(name.equals("published"))
					media.setData(reader.nextString());
			/*	else if(name.equals("icon"))
					media.setIcon(reader.nextString());*/
		}
		reader.endObject();
	}

	public User readUserJson() throws IOException {
		this.reader = new JsonReader(sr);
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