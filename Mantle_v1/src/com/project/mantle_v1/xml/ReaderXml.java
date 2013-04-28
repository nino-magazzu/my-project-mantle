package com.project.mantle_v1.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.project.mantle_v1.notification_home.Note;

public class ReaderXml {

	ArrayList<Note> parsedComment = new ArrayList<Note>();

	public ReaderXml() {

	}

	// metodo di accesso alla struttura dati
	public ArrayList<Note> getParsedData() {
		return parsedComment;
	}

	public void parseComment(File f) throws ParserConfigurationException,
			SAXException, IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(f);
		Node root = doc.getFirstChild();
		NodeList notes = root.getChildNodes();
		//Note n = new Note();
		if (notes.getLength() == 0) {
			Note n = new Note();
			n.setUser("nobody");
			n.setContent("Nessun commento");
			parsedComment.add(n);
		} else {
			for (int i = 0; i < notes.getLength(); i++) {
				Node c = notes.item(i);

				if (c.getNodeType() == Node.ELEMENT_NODE) {
					Note n = new Note();
					Element note = (Element) c;

					String author = note.getAttribute("Author");
					String date = note.getAttribute("Date");
					n.setUser(author);
					n.setDate(date);

					NodeList noteDetails = c.getChildNodes();

					for (int j = 0; j < noteDetails.getLength(); j++) {
						Node c1 = noteDetails.item(j);
						if (c1.getNodeType() == Node.ELEMENT_NODE) {
							Element detail = (Element) c1;
							String nodeValue = detail.getFirstChild()
									.getNodeValue();
							n.setContent(nodeValue);
						}
					}
					parsedComment.add(n);
				}
				//parsedComment.add(n);
			}
		}
	}
}
