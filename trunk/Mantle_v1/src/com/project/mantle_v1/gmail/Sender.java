package com.project.mantle_v1.gmail;


import com.project.mantle_v1.User;
import com.project.mantle_v1.database.MioDatabaseHelper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Sender extends AsyncTask<Void, Long, Boolean> {

	private Context context;
	private String link;
	private String email;   	//"cann.alberto91@gmail.com";
	private String psw;
	private Boolean fl;
	private String addressee;

	
	public Sender(Context c, String url, String contact){
		fl=false;
		link = url;
		addressee = contact;
		context = c.getApplicationContext();
		User user = new User(context);
		this.email = user.getEmail();
		MioDatabaseHelper db = new MioDatabaseHelper(context);
		this.psw = db.getPassword(email);
	}
	
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		sendEmail();	
		return null;
		 
	}

	private void sendEmail(){
		Mail m = new Mail(email,psw); 
		 
	    String[] toArr = {addressee}; 
	    m.setTo(toArr); 
	      
	    m.setFrom(email); 
	    m.setSubject("Applicazione Mantle"); 
	    m.setBody(Mail.MAGIC_NUMBER + link); 
	 
	    try { 
	    	//m.addAttachment("/sdcard/filelocation"); 
	    	
	        if(m.send()) { 
	        	fl=true;
	        } 
	        
	        else { 
	        	fl=false;
	        } 
	    } 
	    catch(Exception e) { 
	    	Log.e("MailApp", "Could not send email", e); 
	    	}		
		}
	
	@Override
    protected void onPostExecute(Boolean result) {
		if(fl){
			Toast.makeText(context, "Email was sent successfully.", Toast.LENGTH_LONG).show();
			Log.d("Email Sender", "Email was sent successfully");
		}
		else {
			Log.w("Email Sender", "Email was not sent successfully");
			Toast.makeText(context, "Email was not sent.", Toast.LENGTH_LONG).show();
		}
	}
}
