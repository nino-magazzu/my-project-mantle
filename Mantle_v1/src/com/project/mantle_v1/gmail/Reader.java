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

import android.os.Bundle;
import android.util.Log;

import com.project.mantle_v1.notification_home.MyHandler;
import com.project.mantle_v1.parser.MantleMessage;

/**
 * Thread per la lettura delle mail. Si occupa di analizzare si le mail presenti
 * nella cartella INBOX non lette che di quelle che arrivano mentre il thread è
 * in vita.
 * <p>
 * 
 * @author ninux
 * 
 */

public class Reader extends Authenticator {

	private static String TAG;
	private String mailhost = "imap.gmail.com";
	private Session session;
	private Store store;
	private Folder folder;
	private final MyHandler handler;

	/**
	 * Costruttre della classe
	 * <p>
	 * 
	 * @param user
	 *            di accesso alla casella di posta elettronica
	 * @param password
	 *            della suddetta casella
	 * @param handler
	 *            oggetto che ha il compito di creare e visualizzare le
	 *            notifiche
	 */

	public Reader(String user, String password, MyHandler handler) {
		TAG = this.getClass().getSimpleName();
		this.handler = handler;

		Properties props = System.getProperties();
		if (props == null) {
			Log.e(TAG, "Properties are null !!");
		}

		try {
			session = Session.getDefaultInstance(props, null);
			store = session.getStore("imaps");
			store.connect(mailhost, user, password);

			Log.i(TAG, "Store: " + store.toString());

		} catch (NoSuchProviderException e) {
			Log.e(TAG, e.getMessage());
		} catch (MessagingException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * 
	 * @throws Exception
	 */

	public synchronized void detectMail() throws Exception {
		try {
			folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);

			Message[] msg = folder.search(new FlagTerm(new Flags(
					Flags.Flag.SEEN), false));

			Log.e(TAG,
					"DetectMail: Messaggi non letti: "
							+ String.valueOf(msg.length));

			if (msg.length > 0) {
				readMail(msg);

			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param msg
	 *            Vettore di messaggi non letti
	 * @throws IOException
	 * @throws MessagingException
	 */

	private void readMail(Message[] msg) throws IOException, MessagingException {
		for (int i = 0; i < msg.length; i++) {
			if (msg[i].getSubject() != null
					&& msg[i].getSubject().equals(Mail.SUBJECT)) {
				Log.v(TAG, "*** Leggo il messaggio ***");
				String body = "";
				MimeMultipart multiPart = (MimeMultipart) msg[0].getContent();
				for (int x = 0; x < multiPart.getCount(); x++) {
					Object content = multiPart.getBodyPart(x).getContent();
					body += content.toString();
				}
				if (body.contains(MantleMessage.MAGIC_NUMBER)) {
					notifyMessage(body, msg[i].getFrom()[0].toString());
					msg[i].setFlag(Flags.Flag.DELETED, true);
				}
			}
		}
	}

	public void detectNewMail() throws Exception {
		folder = store.getFolder("INBOX");
		folder.open(Folder.READ_WRITE);
		folder.addMessageCountListener(new MessageCountListener() {

			@Override
			public void messagesRemoved(MessageCountEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void messagesAdded(MessageCountEvent arg0) {
				try {
					Message[] msg = arg0.getMessages();

					Log.e(TAG,
							"DetectNewMail: Messaggi non letti: "
									+ String.valueOf(msg.length));

					if (msg.length > 0) {
						readMail(msg);
					}
				} catch (MessagingException e) {
					Log.e(TAG, e.getMessage());
				} catch (IOException e) {
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
/*
		for (;;) {
			Thread.sleep(10000);
			Log.d(TAG, "Check for a new email");
			folder.getMessageCount();
		}*/
	}

	private void notifyMessage(String body, String email) {
		android.os.Message msg = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putString("body", body);
		b.putString("email", email);
		msg.setData(b);
		handler.sendMessage(msg);
	}
}