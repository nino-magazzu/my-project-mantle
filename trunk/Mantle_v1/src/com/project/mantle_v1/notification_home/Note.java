package com.project.mantle_v1.notification_home;

import java.io.Serializable;

public class Note implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3499183884482698945L;
	private String user;
	private String content;
	private String date;
	private String fileLink;

	// l'ho inserito per rendere più immediato
	// l'inserimento del commento nel corretto
	// file xml.
	private String commentLink;
	private String sender_mail;

	public Note() {
		super();
	}

	public Note(String user, String content) {
		super();
		this.user = user;
		this.content = content;
	}

	/**
	 * Costruttore da usare per leggere i commenti provenienti dalle mail
	 * 
	 * @param username
	 *            autore del commento
	 * @param content
	 *            corpo del commento
	 * @param date
	 *            data di inserimento
	 * @param fileLink
	 *            file commentato
	 */

	public Note(String username, String content, String date, String fileLink) {
		this.user = username;
		this.content = content;
		this.date = date;
		this.commentLink = fileLink;

	}

	/**
	 * Costruttore da usare per leggere i commenti dal file xml
	 * 
	 * @param username
	 *            autore del commento
	 * @param content
	 *            corpo del commento
	 * @param date
	 *            data di inserimento
	 */

	public Note(String username, String content, String date) {
		this.user = username;
		this.content = content;
		this.date = date;

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFileLink() {
		return fileLink;
	}

	public void setFileLink(String fileLink) {
		this.fileLink = fileLink;
	}

	public String getCommentLink() {
		return commentLink;
	}

	public void setCommentLink(String commentLink) {
		this.commentLink = commentLink;
	}

	public String getSender_mail() {
		return sender_mail;
	}

	public void setSender_mail(String sender_mail) {
		this.sender_mail = sender_mail;
	}
}