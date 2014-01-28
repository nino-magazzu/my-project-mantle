package com.project.mantle_v1.gmail;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.project.mantle_v1.database.DatabaseHelper;
import com.project.mantle_v1.database.User;
import com.project.mantle_v1.parser.MantleMessage;

public class Sender extends AsyncTask<Void, Long, Boolean> {

	private Context context;
	private String link;
	private String email;
	private String psw;
	private Boolean fl;
	private String addressee;
	private String code;

	private final String TAG = this.getClass().getSimpleName();

	public Sender(Context c, String url, String contact, String code) {
		fl = false;
		link = url;
		addressee = contact;
		context = c.getApplicationContext();
		User user = new User(context);
		this.email = user.getEmail();
		DatabaseHelper db = new DatabaseHelper(context);
		this.psw = db.getPassword(email);
		db.close();
		this.code = code;

	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		sendEmail();
		return null;

	}

	private void sendEmail() {
		Mail m = new Mail(email, psw);
		
		String[] toArr = { addressee };
		m.setTo(toArr);
		m.setFrom(email);
		m.setSubject(Mail.SUBJECT);
		
		try {
			m.setBody(new MantleMessage(link, code).getMessage());
		} catch (Exception e1) {
			Log.d(TAG, e1.getMessage());
		}

		try {
			// m.addAttachment("/sdcard/filelocation");

			if (m.send()) {
				fl = true;
			} /*
			 * else { fl=false; }
			 */
		} catch (Exception e) {
			Log.e("MAIL APP", e.getMessage());
			Log.e("MailApp", "Could not send email");
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (fl) {
			Toast.makeText(context, "Email was sent successfully.",
					Toast.LENGTH_LONG).show();
			Log.d("Email Sender", "Email was sent successfully");
		} else {
			Log.w("Email Sender", "Email was not sent successfully");
			Toast.makeText(context, "Email was not sent.", Toast.LENGTH_LONG)
					.show();
		}
	}
}
