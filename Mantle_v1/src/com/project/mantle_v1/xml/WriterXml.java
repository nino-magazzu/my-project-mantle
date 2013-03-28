package com.project.mantle_v1.xml;

import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class WriterXml {

	//nuovo link sui serializer segui quello :)
/*
   <?xml version='1.0' encoding='UTF-8'?>" +
    <record>" +
       <study id='%d'>" +
           <topic>%s</topic>" +
           <content>%s</content>" +
           <author>%s</author>" +
           <date>%s</date>" +
       </study>" +
    </record>"
*/
	public static String writeUsingXMLSerializer() throws Exception {
	    XmlSerializer xmlSerializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	 
	    xmlSerializer.setOutput(writer);
	    // start DOCUMENT
	    xmlSerializer.startDocument("UTF-8", true);
	    // open tag: <record>
	    xmlSerializer.startTag("", "Study.RECORD");
	    // open tag: <study>
	    xmlSerializer.startTag("", "Study.STUDY");
	    xmlSerializer.attribute("", "Study.ID", "String.valueOf(study.mId)");
	 
	    // open tag: <topic>
	    xmlSerializer.startTag("", "Study.TOPIC");
	    xmlSerializer.text("study.mTopic");
	    // close tag: </topic>
	    xmlSerializer.endTag("", "Study.TOPIC");
	 
	    // open tag: <content>
	    xmlSerializer.startTag("", "Study.CONTENT");
	    xmlSerializer.text("study.mContent");
	    // close tag: </content>
	    xmlSerializer.endTag("","Study.CONTENT");
	 
	    // open tag: <author>
	    xmlSerializer.startTag("", "Study.AUTHOR");
	    xmlSerializer.text("study.mAuthor");
	    // close tag: </author>
	    xmlSerializer.endTag("", "Study.AUTHOR");
	 
	    // open tag: <date>
	    xmlSerializer.startTag("", "Study.DATE");
	    xmlSerializer.text("study.mDate");
	    // close tag: </date>
	    xmlSerializer.endTag("", "Study.DATE");
	 
	    // close tag: </study>
	    xmlSerializer.endTag("", "Study.STUDY");
	    // close tag: </record>
	    xmlSerializer.endTag("", "Study.RECORD");
	 
	    // end DOCUMENT
	    xmlSerializer.endDocument();
	 
	    return writer.toString();
	}
	
}
