package com.project.mantle_v1.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import android.util.JsonWriter;

public class ParseJSON {
	
	public ParseJSON(MediaType media) {
		this.media = media;
		this.out = new OutputStream() {
			
			@Override
			public void write(int oneByte) throws IOException {
				// TODO Auto-generated method stub
			}
		};
	}
	
	public void writerJsonStream() throws IOException {
		JsonWriter writer = null;
		writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
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
		writer.name("image");
	}
	
	public void writeImage(JsonWriter writer) throws IOException {
		writer.beginObject();
		writer.name("url").value(media.getUrl());
		//writer.name("width").value(media);
		
	}
	
	
	private MediaType media;
	private OutputStream out;
}
