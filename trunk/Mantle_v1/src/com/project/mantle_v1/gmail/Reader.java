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
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import com.project.mantle_v1.MyHandler;
import android.os.Bundle;
import android.util.Log;

public class Reader extends Authenticator {
	
	private static final String TAG = "GMAILreader";
	private String mailhost = "imap.gmail.com";  
	private Session session;
	private Store store;
	private Folder folder;
	private final MyHandler handler;
	
	
	    public Reader(String user, String password, MyHandler handler) {

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

	    public synchronized void detectMail() throws Exception { 
	    	try { 
	    		folder = store.getFolder("INBOX"); 
	    		folder.open(Folder.READ_WRITE);
	        
	    		Message[] msg = folder.search(
	    				new FlagTerm(new Flags(Flags.Flag.SEEN), false));
	    		
	    		Log.e(TAG, String.valueOf(msg.length));			
	    		
	    		if(msg.length > 0) {
	    			readMail(msg);
	    			
	    		}
	    		/**/
	    	} catch (Exception e) { 
	    		Log.e(TAG, e.getMessage(), e); 
	    	} 
	    }

		private void readMail(Message[] msg) throws IOException,
				MessagingException {
			for (int i = 0; i < msg.length ; i++) {
				String body = "";
				MimeMultipart multiPart = (MimeMultipart) msg[0].getContent();
				for(int x = 0; x < multiPart.getCount(); x++) {
					Object content = multiPart.getBodyPart(x).getContent();
					body += content.toString();
				}
				if(body.contains(Mail.MAGIC_NUMBER)){
					notifyMessage(body);
					msg[i].setFlag(Flags.Flag.DELETED, true);
				}
			}
		} 

	public void detectNewMail() throws Exception{
		folder = store.getFolder("INBOX"); 
	    folder.open(Folder.READ_WRITE); 
		folder.addMessageCountListener(new MessageCountListener() {
			
			public void messagesRemoved(MessageCountEvent arg0) {
				// TODO Auto-generated method stub
			}
			
			public void messagesAdded(MessageCountEvent arg0) {
				try {
				Message[] msg = arg0.getMessages();
				
				Log.d(TAG, "E' arrivata una nuova email");
				
				
	    		if(msg.length > 0) {
	    			readMail(msg);
	    		}
	    		} catch (MessagingException e) {
	 						Log.e(TAG, e.getMessage());
	 					}catch (IOException e) {
	 						//problemi lettura corpo email
	 						Log.e(TAG, e.getMessage());
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

	private void notifyMessage(String body) {
	    android.os.Message msg = handler.obtainMessage();
	    Bundle b = new Bundle();
	    b.putString("body", body);
	    msg.setData(b);
	    handler.sendMessage(msg);
	  }
}