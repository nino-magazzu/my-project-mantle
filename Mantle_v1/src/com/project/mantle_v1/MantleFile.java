package com.project.mantle_v1;

import android.content.Context;
import com.project.mantle_v1.database.MioDatabaseHelper;

public class MantleFile {
	private String idFile;
	private String fileName;
	private String linkFile;
	private String linkComment;
	private String fileKey;
	private MioDatabaseHelper db;
	
	public MantleFile(){
		this.idFile=null;
		this.fileName=null;
		this.linkFile=null;
		this.linkComment=null;
		this.fileKey=null;
	}
	
	public MantleFile(String idFile, String filename, String linkFile, String linkComment, String fileKey){
		this.idFile=idFile;
		this.fileName=filename;
		this.linkFile=linkFile;
		this.linkComment=linkComment;
		this.fileKey=fileKey;
	}

	// Costrutore per prendere uno specifico file
	public MantleFile(Context cont, int idUser) {
		db = new MioDatabaseHelper(cont);
		String[] file = db.getFile(String.valueOf(idUser));
		this.idFile = file[0];
		this.fileName = file[1];
		this.linkFile = file[2];
		this.linkComment = file[3];
		this.fileKey = file[4];
		db.close();
	}

	public String getIdFile() {
		return idFile;
	}

	public void setIdFile(String idFile) {
		this.idFile = idFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLinkFile() {
		return linkFile;
	}

	public void setLinkFile(String linkFile) {
		this.linkFile = linkFile;
	}

	public String getLinkComment() {
		return linkComment;
	}

	public void setLinkComment(String linkComment) {
		this.linkComment = linkComment;
	}

	public String getFileKey() {
		return fileKey;
	}

	public void setFileKey(String fileKey) {
		this.fileKey = fileKey;
	}
}
