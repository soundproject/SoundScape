package il.ac.idc.milab.soundscape;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    // instance variables
    GPSTracker m_Gps;
    Button m_ShowLocationButton;
    Button m_RecordButton;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// init recorder
		initRecorder();
		
		// init GPS
		initGpsTracker();
		
	}

	private void initGpsTracker() {
    	// GPS related code
        m_ShowLocationButton = (Button) findViewById(R.id.welcome_btn_show_location);
        
        // show location button click event
        m_ShowLocationButton.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                // create class object
            	m_Gps = new GPSTracker(MainActivity.this);
            	
            	// Testing Click
                Toast.makeText(getApplicationContext(), "Latitud: Hello!\nLongtitud: World!", Toast.LENGTH_LONG).show();
                
                // check if GPS enabled
                if(m_Gps.canGetLocation()){
 
                    double latitude = m_Gps.getLatitude();
                    double longitude = m_Gps.getLongitude();
 
                    // This will pop up a toast to the screen with the appropriate details of the location
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLatitud: " + latitude + "\nLongtitud: " + longitude, Toast.LENGTH_LONG).show();
                }
                else {
                    // can't get location GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                	m_Gps.showSettingsAlert();
                }
            }
        });
	}

	private void initRecorder() {
		m_RecordButton = (Button) findViewById(R.id.mainBtnRecord);
		
		m_RecordButton.setOnClickListener(new OnClickListener() {			
			private Button m_ThisButton = (Button) findViewById(R.id.mainBtnRecord);
			File directoryForRecording = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
			private SoundRecorder m_SoundRecorder = new SoundRecorder(directoryForRecording);
			
			@Override
			public void onClick(View v) {
				
				if (m_SoundRecorder.isRecording())
				{
					m_SoundRecorder.stopRecording();
				} else
				{
					m_SoundRecorder.startRecording();
				}
				
				String text = m_SoundRecorder.isRecording() ? getString(R.string.main_btn_recording) :
								getString(R.string.main_btn_record);
				
				m_ThisButton.setText(text);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
