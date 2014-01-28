package com.project.mantle_v1.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import android.util.JsonReader;
import android.util.JsonWriter;

import com.project.mantle_v1.database.User;
import com.project.mantle_v1.fileNavigator.MantleFile;
import com.project.mantle_v1.notification_home.Note;

public class ParseJSON {
	
	/**
	 * Si occupa della generazione e lettura degli oggetti JSON costituenti i corpi delle mail
	 * scambiate fra gli utenti 
	 */
	
	final private String USERNAME = "username";
	final private String PUBLISHED = "published";
	final private String OBJECT_TYPE = "objectType";
	final private String FILE_LINK = "url_file";
	final private String NOTES_LINK = "url_notes";
	final private String THUMB_LINK = "url_thumb";
	final private String CONTENT = "content";
	final private String WIDTH = "width";
	final private String HEIGHT = "height";
	final private String NAME = "name";
	final private String SURNAME = "surname";
	final private String PUBLIC_KEY = "publicKey";
	final private String CIPHER_KEY = "cipherKey";
	final private String EMAIL = "email";
	final private String FILE_NAME = "fileName";
	final private String IMAGE = "image";
	final private String FULL_IMAGE = "fullImage";

	/**
	 * Costruttore relativo alla lettura di un oggetto JSON
	 * @param In relativo al JSON che bisogna analizzare
	 */
	
	public ParseJSON(StringReader In) {
		this.in = In;
	}

	/**
	 * Costruttore relativo alla creazione dell'oggetto JSON 
	 * @param Out conterrà la stringa rappresentativa del JSON
	 */
	
	public ParseJSON(StringWriter Out) {
		this.out = Out;
	}

	/**
	 * 	Costruisce un JSON contenente i dati relativi ad un media condiviso su Mantle
	 * @param mt media in questione
	 * @return la stringa relativa al JSON
	 * @throws IOException
	 */
	
	public String writeJson(MantleFile mt) throws IOException {
		this.writer = new JsonWriter(out);
		writer.setIndent("  ");

		if (!mt.isImage())
			writeFile(mt);
		else
			writeImg(mt);

		writer.close();
		return out.toString();
	}

	/**
	 * Costruisce un JSON relativo ad un'immagine
	 * 
	 * @param media relativa all'immagine, contenente i dati della stessa
	 * @throws IOException
	 */
	
	private void writeImg(MantleFile media) throws IOException {
		writer.beginObject();
		writer.name(FILE_NAME).value(media.getFileName());
		writer.name(OBJECT_TYPE).value(media.getObjectType());
		writer.name(NOTES_LINK).value(media.getLinkComment());
		writer.name(USERNAME).value(media.getUsername());
		writer.name(PUBLISHED).value(media.getDate());
		writer.name(CIPHER_KEY).value(media.getFileKey());
		writer.name(IMAGE);
		imageDetails(media);
		writer.name(FULL_IMAGE);
		fullImageDetails(media);
		writer.endObject();
	}

	/**
	 * Costruisce un JSON relativo ad un file generico
	 *  
	 * @param media relativo al file in questione, contenente i dati dello stesso
	 * @throws IOException
	 */
	
	private void writeFile(MantleFile media) throws IOException {
		writer.beginObject();
		writer.name(FILE_NAME).value(media.getFileName());
		writer.name(FILE_LINK).value(media.getLinkFile());
		writer.name(NOTES_LINK).value(media.getLinkComment());
		writer.name(OBJECT_TYPE).value(media.getObjectType());
		writer.name(USERNAME).value(media.getUsername());
		writer.name(PUBLISHED).value(media.getDate());
		writer.name(CIPHER_KEY).value(media.getFileKey());
		writer.endObject();
	}

	/**
	 * Crea stringa contenente il JSON utilizzato per la diffusione dei commenti tra i vari utenti 
	 * 
	 * @param note oggetto relativo al commento in questione
	 * @return Stringa rappresentativa del JSON
	 * @throws IOException
	 */
	
	public String writeJson(Note note) throws IOException {
		this.writer = new JsonWriter(out);
		writer.setIndent("  ");
		writeNote(note);
		writer.close();
		return out.toString();
	}
	
	/**
	 * Crea il JSON relativo al commento, con tutte le informazioni che quindi sono collegate
	 * allo stesso
	 * @param note oggetto che denota il commento in questione
	 * @throws IOException
	 */
	
	private void writeNote(Note note) throws IOException {
		writer.beginObject();
		writer.name(USERNAME).value(note.getUser());
		writer.name(PUBLISHED).value(note.getDate());
		writer.name(CONTENT).value(note.getContent());
		writer.name(FILE_LINK).value(note.getCommentLink());
		writer.endObject();

	}
	
	/**
	 * Crea stringa contenente il JSON utilizzato per la diffusione delle informazioni degli utenti nella community
	 * @param user di cui si vogliono comunicare i dati
	 * @return
	 * @throws IOException
	 */
	
	public String writeJson(User user) throws IOException {
		this.writer = new JsonWriter(out);
		writer.setIndent("  ");
		writeUser(user);
		writer.close();
		return out.toString();
	}
	
	/**
	 * Crea il JSON relativo all'utente del quale bisogna diffondere i dati
	 * @param user
	 * @throws IOException
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
	
	/**
	 * JSON relativo alla condivisione di immagini raccogliendo le informazioni relative alla dimensione dell'icona della stessa
	 * @param media
	 * @throws IOException
	 */
	private void imageDetails(MantleFile media) throws IOException {
		writer.beginObject();
		writer.name(THUMB_LINK).value(media.getLinkThumb());
		writer.name(WIDTH).value("320");
		writer.name(HEIGHT).value("240");
		writer.endObject();
	}

	/**
	 * JSON relativo alla condivisione di immagini raccogliendo le informazioni relative alla dimensione della stessa
	 * @param media
	 * @throws IOException
	 */
	private void fullImageDetails(MantleFile media) throws IOException {
		writer.beginObject();
		writer.name(FILE_LINK).value(media.getLinkFile());
		writer.name(WIDTH).value("100");
		writer.name(HEIGHT).value("100");
		writer.endObject();
	}

	/**
	 * Colloca le informazioni lette da un JSON relative ad un commento in un oggetto di tipo Note
	 * @return
	 * @throws IOException
	 */
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
	
	/**
	 * Legge un oggetto di tipo Note da una JSON contenuto in una stringa
	 * @param note
	 * @throws IOException
	 */
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
	
	/**
	 * Collocazioni delle informazioni lette da un JSON contentente i dati di un file condiviso
	 * all'interno di un oggetto MantleFile
	 * @return
	 * @throws IOException
	 */
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
	
	/**
	 * Legge le informazioni relative ad un file da un JSON
	 * @param media
	 * @throws IOException
	 */
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
			else if (name.equals(CIPHER_KEY))
				media.setFileKey(reader.nextString());
		}
		reader.endObject();
	}
	
	/**
	 * legge e inserisce all'interno di un oggetto MantleFile le informazioni lette da un JSON relative 
	 * ad un file condiviso
	 * @return
	 * @throws IOException
	 */
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
	
	/**
	 * Lettore dei dati relativi ad un'immagine da un JSON
	 * @param media
	 * @throws IOException
	 */
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
			else if (name.equals(CIPHER_KEY))
				media.setFileKey(reader.nextString());
			else if (name.equals(IMAGE))
				readImage(media);
			else if (name.equals(FULL_IMAGE))
				readFullImage(media);
		}
		reader.endObject();
	}
	
	/**
	 * lettore dei dati relativi all'icona di un'immagine
	 * @param media
	 * @throws IOException
	 */
	private void readImage(MantleFile media) throws IOException {
		reader.beginObject();
		String prova;
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(THUMB_LINK))
				media.setLinkThumb(reader.nextString());
			// TODO: aggiungere eventualmente la lettura delle
			// dimensioni dell'immagine
			else if (name.equals(HEIGHT))
				prova = reader.nextString();
			else if (name.equals(WIDTH))
				prova = reader.nextString();
		}
		reader.endObject();
	}
	
	/**
	 * lettore dei dati relativi ad un'immagine
	 * @param media
	 * @throws IOException
	 */
	private void readFullImage(MantleFile media) throws IOException {
		reader.beginObject();
		String prova;
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(FILE_LINK))
				media.setLinkFile(reader.nextString());
			// TODO: aggiungere eventualmente la lettura delle
			// dimensioni dell'immagine
			else if (name.equals(HEIGHT))
				prova = reader.nextString();
			else if (name.equals(WIDTH))
				prova = reader.nextString();

		}
		reader.endObject();
	}
	
	/**
	 * lettore di dati utente da un JSON, e salvataggio delle stesse in un oggetto User
	 * @return oggetto User con i dati letti
	 * @throws IOException
	 */
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
	
	/**
	 * lettura e salvataggio dei dati letti da un JSON in un oggetto di tipo User passato
	 * @param user
	 * @throws IOException
	 */
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

	@Override
	public String toString() {
		return out.toString();
	}

	private boolean lenient = false;
	private StringWriter out;
	private StringReader in;
	private JsonWriter writer;
	private JsonReader reader;

}