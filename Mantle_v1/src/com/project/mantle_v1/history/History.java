package com.project.mantle_v1.history;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

import com.project.mantle_v1.xml.WriterXml;

import android.os.Environment;
import android.util.Log;

public class History {

	public String getLastFile() {
		// Used to examplify deletion of files more than 1 month old

		// Note the L that tells the compiler to interpret the number as a Long
		final long MAXFILEAGE = 2678400000L; // 1 month in milliseconds

		// Get file handle to the directory. In this case the application files
		// dir
		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/Mantle/history");

		// Optain list of files in the directory.
		// listFiles() returns a list of File objects to each file found.
		File[] files = dir.listFiles();

		if (files == null) {
			try {
				WriterXml com = new WriterXml();
				com.createComment(
						new Date(System.currentTimeMillis()) + ".xml",
						Environment.getExternalStorageDirectory().toString()
								+ "/Mantle/history");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("History", e.getMessage());
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new Date(System.currentTimeMillis()) + ".xml";
		} else {
			// Loop through all files
			for (File f : files) {

				// Get the last modified date. Miliseconds since 1970
				long lastmodified = f.lastModified();

				// Do stuff here to deal with the file..
				// For instance delete files older than 1 month
				if (lastmodified + MAXFILEAGE < System.currentTimeMillis()) {
					return f.getName();
				}

			}
			try {
				WriterXml com = new WriterXml();
				com.createComment(
						new Date(System.currentTimeMillis()) + ".xml",
						Environment.getExternalStorageDirectory().toString()
								+ "/Mantle/history");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new Date(System.currentTimeMillis()) + ".xml";
		}

	}
}