package com.project.mantle_v1.parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.util.JsonReader;
import android.util.JsonWriter;

public class ParseJSON {
	
	public ParseJSON(MediaType media) {
		this.media = media;
		this.charset = "UTF-8";
		this.out = new OutputStream() {
			
			@Override
			public void write(int oneByte) throws IOException {
				// TODO Auto-generated method stub
			}
		};
		
	}
	
	public void writerJsonStream() throws IOException {
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, charset));
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
	
	public void readMedia() {
		JsonReader reader = new JsonReader(new InputStreamReader(in, charset));
		
	}
	
	private MediaType media;
	private OutputStream out;
	private String charset;
}
