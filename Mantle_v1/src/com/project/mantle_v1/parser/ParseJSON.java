package com.project.mantle_v1.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import android.util.JsonReader;
import android.util.JsonWriter;

public class ParseJSON {
	
	public ParseJSON(MediaType media) {
		this.media = media;
	}
	
	public void writeJson(StringWriter sw) throws IOException {
		JsonWriter writer = new JsonWriter(sw);
		writer.setIndent("  ");
		writeMedia(writer);
		writer.close();
	}
	
	public void writeMedia(JsonWriter writer) throws IOException {
		writer.beginObject();
		writer.name("url").value(media.getUrl());
		writer.name("objectType").value(media.getObjectType());
		writer.name("username").value(media.getUsername());
		writer.name("published").value(media.getData());
		writer.name("icon").value(media.getIcon());
		writer.endObject();
	}
	
	public void readJson(StringReader sr) throws IOException {
		JsonReader reader = new JsonReader(sr);
		try {
			readMedia(reader);
		} 
		finally {
			reader.close();
		}
	}
		
	public void readMedia(JsonReader reader) throws IOException {
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
	
	private MediaType media;
}
