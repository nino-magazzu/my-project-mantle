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
	public void createXml(String Date, String filename)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerFactoryConfigurationError, TransformerException {

		path = Environment.getExternalStorageDirectory().toString() + "/";
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
	public void createComment(String filename)
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
		createXml(writer.toString(), filename);

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
		createXml(writer.toString(), filename);
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
		createXml(writer.toString(), f.getName());

	}

	public void deleteComment(File f) {
		f.delete();

	}

}
