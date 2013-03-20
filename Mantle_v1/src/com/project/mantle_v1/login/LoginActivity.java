package com.project.mantle_v1.login;

import com.project.mantle_v1.Home;
import com.project.mantle_v1.R;
import com.project.mantle_v1.R.id;
import com.project.mantle_v1.R.layout;
import com.project.mantle_v1.R.menu;
import com.project.mantle_v1.R.string;
import com.project.mantle_v1.Register;
import com.project.mantle_v1.database.MioDatabaseHelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	
	/**
	 * The default Username to populate the Username field with.
	 */
	public static final String EXTRA_NAME = "Insert username";

	// Values for Username and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private MioDatabaseHelper db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		db = new MioDatabaseHelper(this);
        
		
		
		// Set up the login form.
		mUsername = getIntent().getStringExtra(EXTRA_NAME);
		mUsernameView = (EditText) findViewById(R.id.username);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid Username, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 0) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid Username address.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			//Ho commentato la riga di sopra perche noi non dobbiamo perdere tempo ad effettuare il
			//login ma possiamo lanciare direttamente l'activity seguente(home,registrazione)
			
			//////////////

			//db.deleteAll();
			db.showAll();
			///////////////
			
			String[] res = db.login();
			
			///////////////
			Log.d("LOGIN","res[0]= " + res[0] + "res[1]= " + res[1]);
			Log.d("LOGIN","username = " + mUsername+"password = "+mPassword);
			///////////////
			
			if(res[0].equals(" ")){
				///////////////
				Log.d("LOGIN","l'utente non è registrato");
				///////////////
				
				// Inserisco i dati username e password come nuovo db e 
				// avvio il form per la registrazione
				db.insertService("mantle", mUsername, mPassword);
				Intent intent = new Intent(LoginActivity.this, Register.class);
    	    	intent.putExtra("username",mUsername );
    	    	intent.putExtra("password",mPassword);
    	    	startActivityForResult(intent, 1);
			}
			
			if(res[0].equals(mUsername)&&res[1].equals(mPassword)){
				///////////////						
				Log.d("LOGIN :)", "Le stringe sono uguli");
				///////////////
				Intent intent = new Intent(LoginActivity.this, Home.class);
	    	    startActivity(intent);
				}
			
			if((!res[0].equals(mUsername))&&(!res[0].equals(" "))){
				///////////////
				Log.d("LOGIN :(", "le stringe sono diverse");
				///////////////					
				showProgress(false);
				mUsernameView.setError("username errato");
				mUsernameView.setText("");
				
			}
			
			if(!res[1].equals(mPassword)&&(!res[1].equals(" "))){
				///////////////
				Log.d("LOGIN :(", "le stringe sono diverse");
				///////////////
				showProgress(false);
				mPasswordView.setError("password errata");
				mPasswordView.setText("");
			}
			///////////////
			Log.d("LOGIN","log che non deve essere mai raggiunto");
			//android.os.SystemClock.sleep(3000);
			//showProgress(false);

			///////////////
						
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
