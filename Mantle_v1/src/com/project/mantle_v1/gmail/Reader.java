package com.project.mantle_v1.gmail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;

import com.project.mantle_v1.Configuratore;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class Reader extends Authenticator {
	private static final String TAG = "GMAILreader";
	private String mailhost = "imap.gmail.com";  
	private Session session;
	private Store store;
	private Folder folder;
	private Handler handler;
	
	
	    public Reader(String user, String password, Handler handler) {

	    	this.handler = handler;
	    	
	        Properties props = System.getProperties();
	        if (props == null){
	         Log.e(TAG, "Properties are null !!");
	        }

	        try {
	        session = Session.getDefaultInstance(props, null);
	        store = session.getStore("imaps");
	        store.connect(mailhost, user, password);

	        Log.i(TAG, "Store: "+store.toString());
	        
	        } catch (NoSuchProviderException e) {
	        	Log.e(TAG, e.getMessage());
	        } catch (MessagingException e) {
	        	Log.e(TAG, e.getMessage());
	        }
	}

	    public synchronized void readMail() throws Exception { 
	    	try { 
	    		folder = store.getFolder("INBOX"); 
	    		folder.open(Folder.READ_WRITE);
	        
	    		Message[] msg = folder.getMessages();
	        
	    		for (int i = 0; i < msg.length ; i++) {
	    			if(!msg[i].isSet(Flags.Flag.SEEN)){
	    				Log.d("From", msg[i].getFrom()[0] + "");  
	    				Log.d("Subject", msg[i].getSubject() + "");
	    				String content = msg[i].getContent().toString();
	    				Log.d("content", content + "");
	    			}
	    		}
	    	} catch (Exception e) { 
	    		Log.e(TAG, e.getMessage(), e); 
	    	} 
	    } 

	public void readNewMail() throws Exception{
		folder = store.getFolder("INBOX"); 
	    folder.open(Folder.READ_WRITE); 
		folder.addMessageCountListener(new MessageCountListener() {
			
			public void messagesRemoved(MessageCountEvent arg0) {
				// TODO Auto-generated method stub
			}
			
			public void messagesAdded(MessageCountEvent arg0) {
				Message[] msgs = arg0.getMessages();

				/*
				 * 		WARNING: manca una condizione che mi vada a leggere il corpo delle mail e crei le notifiche SOLO per le 
				 * 		mail interne al programma mantle
				 * 
				 * 		UPDATE: aggiunto un confronto con il Magic Number per stabilire se la mail è relativa all'applicazione
				 */
				
	 			Log.d(TAG, "E' arrivata una nuova mail");
	 			for(int i = 0; i < msgs.length; i++) {
	 				try {
	 					String[] splitted = msgs[i].getContent().toString().split(" ");
	 					String magicNumber = splitted[0];
	 					if(magicNumber.compareTo(Configuratore.getMagicNumber()) == 0) {
	 						notifyMessage(msgs[0].getFrom()[0].toString(), splitted[1]);
	 						msgs[0].setFlag(Flags.Flag.DELETED, true);
	 					}
	 					} catch (MessagingException e) {
	 						Log.e(TAG, e.getMessage());
	 					}catch (IOException e) {
	 						//problemi lettura corpo email
	 						Log.e(TAG, e.getMessage());
	 					}
	 			}
	 			try {
					folder.close(true);
					folder = store.getFolder("INBOX"); 
		 		    folder.open(Folder.READ_WRITE);
				} catch (MessagingException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		});     
	     
	     for (;;){
	     	Thread.sleep(10000);
	     	folder.getMessageCount();
	     }
		
	}

	private void notifyMessage(String user, String link) {
	    android.os.Message msg = handler.obtainMessage();
	    Bundle b = new Bundle();
	    b.putString("notification", ""+user);
	    b.putString("link", ""+link);
	    msg.setData(b);
	    handler.sendMessage(msg);
	  }
}
