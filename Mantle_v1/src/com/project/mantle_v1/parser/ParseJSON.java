package com.project.mantle_v1.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.User;
import com.project.mantle_v1.notification_home.Note;
import android.util.JsonReader;
import android.util.JsonWriter;

public class ParseJSON {

	final private String USERNAME = "username";
	final private String PUBLISHED = "published";
	final private String OBJECT_TYPE = "objectType";
	final private String FILE_LINK = "url_file";
	final private String NOTES_LINK = "url_notes";
	final private String THUMB_LINK = "url_thumb";
	final private String CONTENT = "content";
	final private String ICON = "icon";
    final private String WIDTH = "width";
	final private String HEIGHT = "height";
	final private String NAME = "name";
	final private String SURNAME = "surname";
	final private String PUBLIC_KEY = "publicKey";
	final private String EMAIL = "email";
	final private String FILE_NAME = "fileName";
	final private String IMAGE = "image";
	final private String FULL_IMAGE = "fullImage";

	public ParseJSON(StringReader In) {
		this.in = In;
	}

	public ParseJSON(StringWriter Out) {
		this.out = Out;
	}

	public String writeJson(MantleFile mt) throws IOException {
		this.writer = new JsonWriter(out);
		writer.setIndent("  ");
		
		if(!mt.isImage())
			writeFile(mt);
		else
			writeImg(mt);
		
		writer.close();
		return out.toString();
	}

	private void writeImg(MantleFile media) throws IOException {
		writer.beginObject();
		writer.name(FILE_NAME).value(media.getFileName());
		writer.name(OBJECT_TYPE).value(media.getObjectType());
		writer.name(NOTES_LINK).value(media.getLinkComment());
		writer.name(USERNAME).value(media.getUsername());
		writer.name(PUBLISHED).value(media.getDate());
		writer.name(IMAGE);
		imageDetails(media);
		writer.name(FULL_IMAGE); 
		fullImageDetails(media);
		
	}
	
	private void writeFile(MantleFile media) throws IOException {
		writer.beginObject();
		writer.name(FILE_NAME).value(media.getFileName());
		writer.name(FILE_LINK).value(media.getLinkFile());
		writer.name(NOTES_LINK).value(media.getLinkComment());
		writer.name(OBJECT_TYPE).value(media.getObjectType());
		writer.name(USERNAME).value(media.getUsername());
		writer.name(PUBLISHED).value(media.getDate());
		writer.endObject();
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
		writer.name(FILE_LINK).value(note.getCommentLink());
		writer.endObject();

	}

	public String writeJson(User user) throws IOException {
		this.writer = new JsonWriter(out);
		writer.setIndent("  ");
		writeUser(user);
		writer.close();
		return out.toString();
	}

	private void writeUser(User user) throws IOException {
		writer.beginObject();
		writer.name(NAME).value(user.getName());
		writer.name(SURNAME).value(user.getSurname());
		writer.name(USERNAME).value(user.getUsername());
		writer.name(EMAIL).value(user.getEmail());
		writer.name(PUBLIC_KEY).value(user.getKey());
		writer.endObject();
	}

	private void imageDetails(MantleFile media) throws IOException {
		writer.beginObject();
		writer.name(THUMB_LINK).value(media.getLinkThumb());
		writer.name(WIDTH).value("320");
		writer.name(HEIGHT).value("240");
		writer.endObject();
	}

	private void fullImageDetails(MantleFile media) throws IOException {
		writer.beginObject();
		writer.name(FILE_LINK).value(media.getLinkFile());
		writer.name(WIDTH).value("100");
		writer.name(HEIGHT).value("100");
		writer.endObject();
	}
	

	public Note readNote() throws IOException {
		this.reader = new JsonReader(in);
		reader.setLenient(lenient);
		Note note = new Note();
		try {
			readNote(note);
		} finally {
			reader.close();
		}
		return note;
	}

	private void readNote(Note note) throws IOException {
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(USERNAME))
				note.setUser(reader.nextString());
			else if (name.equals(PUBLISHED))
				note.setDate(reader.nextString());
			else if (name.equals(CONTENT))
				note.setContent(reader.nextString());
			else if (name.equals(FILE_LINK))
				note.setCommentLink(reader.nextString());

		}
		reader.endObject();
	}

	public MantleFile readFileMediaJson() throws IOException {
		this.reader = new JsonReader(in);
		reader.setLenient(lenient);
		MantleFile media = new MantleFile();
		try {
			readFileMedia(media);
		} finally {
			reader.close();
		}
		return media;
	}

	private void readFileMedia(MantleFile media) throws IOException {
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(FILE_NAME))
				media.setFileName(reader.nextString());
			else if (name.equals(FILE_LINK))
				media.setLinkFile(reader.nextString());
			else if (name.equals(NOTES_LINK))
				media.setLinkComment(reader.nextString());
			else if (name.equals(OBJECT_TYPE))
				media.setObjectType(reader.nextString());
			else if (name.equals(USERNAME))
				media.setUsername(reader.nextString());
			else if (name.equals(PUBLISHED))
				media.setDate(reader.nextString());
		}
		reader.endObject();
	}

	public MantleFile readImageMediaJson() throws IOException {
		this.reader = new JsonReader(in);
		reader.setLenient(lenient);
		MantleFile media = new MantleFile();
		try {
			readImageMedia(media);
		} finally {
			reader.close();
		}
		return media;
	}
	
	private void readImageMedia(MantleFile media) throws IOException {
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(FILE_NAME))
				media.setFileName(reader.nextString());
			else if (name.equals(NOTES_LINK))
				media.setLinkComment(reader.nextString());
			else if (name.equals(OBJECT_TYPE))
				media.setObjectType(reader.nextString());
			else if (name.equals(USERNAME))
				media.setUsername(reader.nextString());
			else if (name.equals(PUBLISHED))
				media.setDate(reader.nextString());
			else if(name.equals(IMAGE))
				readImage(media);
			else if(name.equals(FULL_IMAGE))
				readFullImage(media);
		}
		reader.endObject();
	}
	
	private void readImage(MantleFile media) throws IOException {
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(THUMB_LINK))
				media.setLinkThumb(reader.nextString());
			// TODO: aggiungere eventualmente la lettura delle
			// dimensioni dell'immagine
		}
		reader.endObject();
	}
	
	private void readFullImage(MantleFile media) throws IOException {
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(FILE_LINK))
				media.setLinkFile(reader.nextString());
			// TODO: aggiungere eventualmente la lettura delle
			// dimensioni dell'immagine
		}
		reader.endObject();
	}

	public User readUserJson() throws IOException {
		this.reader = new JsonReader(in);
		reader.setLenient(lenient);
		User user = new User();
		try {
			readUser(user);
		} finally {
			reader.close();
		}
		return user;
	}

	private void readUser(User user) throws IOException {
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(NAME))
				user.setName(reader.nextString());
			else if (name.equals(SURNAME))
				user.setSurname(reader.nextString());
			else if (name.equals(USERNAME))
				user.setUsername(reader.nextString());
			else if (name.equals(EMAIL))
				user.setEmail(reader.nextString());
			else if (name.equals(PUBLIC_KEY))
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