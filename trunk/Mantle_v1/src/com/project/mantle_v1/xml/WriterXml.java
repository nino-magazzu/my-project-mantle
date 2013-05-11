package com.project.mantle_v1.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.project.mantle_v1.MantleFile;

import android.os.Environment;
import android.util.Log;

public class WriterXml {

	File f;
	FileWriter fw;
	BufferedWriter bw;
	String path;

	public WriterXml() {

	}

	// Il metodo
	private void createXml(String Date, String filename, String path)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerFactoryConfigurationError, TransformerException {

		// path = Environment.getExternalStorageDirectory().toString() + "/";
		f = new File(path, filename);
		fw = null;
		bw = null;
		try {
			// se il file già esiste viene fatto l'append altrimenti viene
			// sostituito
			fw = new FileWriter(f, false);
			bw = new BufferedWriter(fw);
			bw.write(Date);
			bw.close();
			fw.close();
			Log.d("WRITE_XML", "IL FILE é STATO CREATO");
		} catch (IOException e) {
			Log.w("XML_maker", "NON HA FUNZIONATO : " + e.getMessage());
			System.err.println(e.getMessage());
		} catch (Exception e) {
			Log.w("XML_maker", "NON HA FUNZIONATO : " + e.getMessage());
			e.printStackTrace();
		}
	}

	// crea la struttura di un file Xml con il solo elemento root
	public void createComment(String filename, String path)
			throws ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException,
			SAXException, IOException {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		// create root: <record>
		Element root = doc.createElement("ROOT");
		doc.appendChild(root);
		// create Transformer object
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(new DOMSource(doc), result);

		// create XML file
		createXml(writer.toString(), filename, path);

	}

	// crea la struttura di un file xml per i commenti con un primo elemento
	// inserito
	public void writeComment(String Author, String Date, String Content,
			String filename) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		// create root: <record>
		Element root = doc.createElement("ROOT");
		doc.appendChild(root);

		// create: <study>
		Element tag = doc.createElement("COMMENT");
		root.appendChild(tag);
		// add attr: date e author
		tag.setAttribute("Date", Date);
		tag.setAttribute("Author", Author);

		// create: <content>
		Element tagContent = doc.createElement("CONTENT");
		tag.appendChild(tagContent);
		tagContent.setTextContent(Content);

		// create Transformer object
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(new DOMSource(doc), result);

		// create XML file
		String path = MantleFile.DIRECTORY_TEMP;
		createXml(writer.toString(), filename, path);
	}

	// crea la struttura di un file xml per aggiungere un commento ad un file
	// già esistente
	public void addComment(String Author, String Date, String Content, File f)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerFactoryConfigurationError, TransformerException {
		// String filepath =path+filename+".xml";
		// Log.d("XML_MAKER",filepath);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(f);

		// mi restituisce l'elemento root del file
		Node root = doc.getFirstChild();

		// crea il nuovo tag
		Element tag = doc.createElement("COMMENT");
		root.appendChild(tag);
		// add attr: author =
		tag.setAttribute("Author", Author);
		tag.setAttribute("Date", Date);

		Element tagContent = doc.createElement("CONTENT");
		tag.appendChild(tagContent);
		tagContent.setTextContent(Content);

		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(new DOMSource(doc), result);

		// create XML file
		String path = MantleFile.DIRECTORY_TEMP;
		
		createXml(writer.toString(), f.getName(), path);

	}

	public void deleteComment(File f) {
		f.delete();

	}

	public void addNoteJson(String Url, String ObjectType, String Id,
			String Published, String Content, File f)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerFactoryConfigurationError, TransformerException {
		// String filepath =path+filename+".xml";
		// Log.d("XML_MAKER",filepath);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(f);

		// mi restituisce l'elemento root del file
		Node root = doc.getFirstChild();

		// crea il nuovo tag
		Element tag = doc.createElement("NOTE");
		root.appendChild(tag);

		Element tagUrl = doc.createElement("URL");
		tag.appendChild(tagUrl);
		tagUrl.setTextContent(Url);

		Element tagObjectType = doc.createElement("OBJECT_TYPE");
		tag.appendChild(tagObjectType);
		tagObjectType.setTextContent(ObjectType);

		Element tagId = doc.createElement("ID");
		tag.appendChild(tagId);
		tagId.setTextContent(Id);

		Element tagPublished = doc.createElement("PUBLISHED");
		tag.appendChild(tagPublished);
		tagPublished.setTextContent(Published);

		Element tagContent = doc.createElement("CONTENT");
		tag.appendChild(tagContent);
		tagContent.setTextContent(Content);

		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(new DOMSource(doc), result);

		// create XML file
		String path = MantleFile.DIRECTORY_HISTORY;
		createXml(writer.toString(), f.getName(), path);

	}

	public void addPhotoJson(String ObjectType, String Id, String Published,
			String[] Image, String[] FullImage, File f)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerFactoryConfigurationError, TransformerException {
		// String filepath =path+filename+".xml";
		// Log.d("XML_MAKER",filepath);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(f);

		// mi restituisce l'elemento root del file
		Node root = doc.getFirstChild();

		// crea il nuovo tag
		Element tag = doc.createElement("PHOTO");
		root.appendChild(tag);

		Element tagUrl = doc.createElement("URL");
		tag.appendChild(tagUrl);
		tagUrl.setTextContent(FullImage[0]);

		Element tagObjectType = doc.createElement("OBJECT_TYPE");
		tag.appendChild(tagObjectType);
		tagObjectType.setTextContent(ObjectType);

		Element tagId = doc.createElement("ID");
		tag.appendChild(tagId);
		tagId.setTextContent(Id);

		Element tagPublished = doc.createElement("PUBLISHED");
		tag.appendChild(tagPublished);
		tagPublished.setTextContent(Published);

		Element tagImage = doc.createElement("IMAGE");
		tag.appendChild(tagImage);

		Element tagImageUrl = doc.createElement("URL");
		tagImage.appendChild(tagImageUrl);
		tagImageUrl.setTextContent(Image[0]);

		Element tagImageWidth = doc.createElement("WIDTH");
		tagImage.appendChild(tagImageWidth);
		tagImageWidth.setTextContent(Image[1]);

		Element tagImageHeight = doc.createElement("HEIGHT");
		tagImage.appendChild(tagImageHeight);
		tagImageHeight.setTextContent(Image[2]);

		Element tagFullImage = doc.createElement("FULL_IMAGE");
		tag.appendChild(tagFullImage);

		Element tagFullImageUrl = doc.createElement("URL");
		tagFullImage.appendChild(tagFullImageUrl);
		tagFullImageUrl.setTextContent(FullImage[0]);

		Element tagFullImageWidth = doc.createElement("WIDTH");
		tagFullImage.appendChild(tagFullImageWidth);
		tagFullImageWidth.setTextContent(FullImage[1]);

		Element tagFullImageHeight = doc.createElement("HEIGHT");
		tagFullImage.appendChild(tagFullImageHeight);
		tagFullImageHeight.setTextContent(FullImage[2]);

		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(new DOMSource(doc), result);

		// create XML file
		String path = MantleFile.DIRECTORY_HISTORY;
		createXml(writer.toString(), f.getName(), path);

	}

	public void addFileJson(String DisplayName, String FileUrl, String Url,
			String ObjectType, String Id, String Published, File f)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerFactoryConfigurationError, TransformerException {
		// String filepath =path+filename+".xml";
		// Log.d("XML_MAKER",filepath);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(f);

		// mi restituisce l'elemento root del file
		Node root = doc.getFirstChild();

		// crea il nuovo tag
		Element tag = doc.createElement("FILE");
		root.appendChild(tag);

		Element tagDisplayName = doc.createElement("DISPLAY_NAME");
		tag.appendChild(tagDisplayName);
		tagDisplayName.setTextContent(DisplayName);

		Element tagFileUrl = doc.createElement("FILE_URL");
		tag.appendChild(tagFileUrl);
		tagFileUrl.setTextContent(FileUrl);

		Element tagUrl = doc.createElement("URL");
		tag.appendChild(tagUrl);
		tagUrl.setTextContent(Url);

		Element tagObjectType = doc.createElement("OBJECT_TYPE");
		tag.appendChild(tagObjectType);
		tagObjectType.setTextContent(ObjectType);

		Element tagId = doc.createElement("ID");
		tag.appendChild(tagId);
		tagId.setTextContent(Id);

		Element tagPublished = doc.createElement("PUBLISHED");
		tag.appendChild(tagPublished);
		tagPublished.setTextContent(Published);

		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(new DOMSource(doc), result);

		// create XML file
		String path = MantleFile.DIRECTORY_HISTORY;
		createXml(writer.toString(), f.getName(), path);

	}

	public void addUserJson(String Name, String Surname, String Username,
			String Email, String PublicKey, File f)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerFactoryConfigurationError, TransformerException {
		// String filepath =path+filename+".xml";
		// Log.d("XML_MAKER",filepath);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(f);

		// mi restituisce l'elemento root del file
		Node root = doc.getFirstChild();

		// crea il nuovo tag
		Element tag = doc.createElement("USER");
		root.appendChild(tag);

		Element tagName = doc.createElement("NAME");
		tag.appendChild(tagName);
		tagName.setTextContent(Name);

		Element tagSurname = doc.createElement("SURNAME");
		tag.appendChild(tagSurname);
		tagSurname.setTextContent(Surname);

		Element tagUsername = doc.createElement("USERNAME");
		tag.appendChild(tagUsername);
		tagUsername.setTextContent(Username);

		Element tagEmail = doc.createElement("EMAIL");
		tag.appendChild(tagEmail);
		tagEmail.setTextContent(Email);

		Element tagPublicKey = doc.createElement("PUBLIC_KEY");
		tag.appendChild(tagPublicKey);
		tagPublicKey.setTextContent(PublicKey);

		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(new DOMSource(doc), result);

		// create XML file
		String path = MantleFile.DIRECTORY_HISTORY;
		createXml(writer.toString(), f.getName(), path);

	}

	public void addSystemInfo(String Content, String Username,
			String Published, File f) throws ParserConfigurationException,
			SAXException, IOException, TransformerFactoryConfigurationError,
			TransformerException {
		// String filepath =path+filename+".xml";
		// Log.d("XML_MAKER",filepath);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(f);

		// mi restituisce l'elemento root del file
		Node root = doc.getFirstChild();

		// crea il nuovo tag
		Element tag = doc.createElement("SYSTEM_INFO");
		root.appendChild(tag);

		Element tagContent = doc.createElement("CONTENT");
		tag.appendChild(tagContent);
		tagContent.setTextContent(Content);

		Element tagUsername = doc.createElement("USERNAME");
		tag.appendChild(tagUsername);
		tagUsername.setTextContent(Username);

		Element tagPublished = doc.createElement("PUBLISHED");
		tag.appendChild(tagPublished);
		tagPublished.setTextContent(Published);

		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(new DOMSource(doc), result);

		// create XML file
		String path = MantleFile.DIRECTORY_HISTORY;
		createXml(writer.toString(), f.getName(), path);

	}

}
