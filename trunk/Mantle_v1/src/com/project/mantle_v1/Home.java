package com.project.mantle_v1;


import java.io.File;

import com.project.mantle_v1.database.AddFriend;
import com.project.mantle_v1.dropbox.Dropbox;
import com.project.mantle_v1.gmail.ReaderTask;


import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class Home extends Activity {
	private Button dropbox;
//    private Button googleDrive;
 //   private Button geTT;
		private Button rubrica;

		private final String HOME_DIR = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Mantle";
		
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        File HomeDir = new File(HOME_DIR);
        Log.d("HomeDir", HomeDir.getAbsolutePath());
        if(!HomeDir.exists()) {
        	HomeDir.mkdir();
        }
        
//        googleDrive = (Button) findViewById(R.id.button1);
        dropbox = (Button) findViewById(R.id.accetta);
        
        /* TODO: implementare tutto
         * 
         * 
         */
        
        
//        geTT = (Button) findViewById(R.id.button3);
        
        rubrica = (Button) findViewById(R.id.button4);
        
        
        /*
         *  	Handler oggetto per permettere la comunicazione fra activity
         */
        
        
        Handler handler = new MyHandler(Home.this);

        /*
         *  	ReaderTask ha il compito di andare a leggere le mail per andare a capire se vi sono notifiche relative all'applicazione
         */
        
        //ReaderTask thr = new ReaderTask(handler);
   	   // IBinder mbinder = thr.onBind(new Intent());
   	   //thr.start();
        
        dropbox.setOnClickListener(new View.OnClickListener() {
			@Override
        	public void onClick(View v) {
				startActivity(new Intent(Home.this, Dropbox.class));
			}
		});
        
        rubrica.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, AddFriend.class);
	    	    startActivity(intent);
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }
}
