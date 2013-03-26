package com.project.mantle_v1.gmail;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Sender extends AsyncTask<Void, Long, Boolean> {

	private Context context;
	private String link;
	private String user = "nmagazzu@gmail.com";   	//"cann.alberto91@gmail.com";
	private String psw = "89vale46";
	private Boolean fl;
	private String addressee;

	
	public Sender(Context c, String url, String contact){
		fl=false;
		link = url;
		addressee = contact;
		context = c.getApplicationContext();
	}
	
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		sendEmail();	
		return null;
		 
	}

	private void sendEmail(){
		Mail m = new Mail(user,psw); 
		 
	    String[] toArr = {addressee}; 
	    m.setTo(toArr); 
	      
	    m.setFrom(user); 
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
