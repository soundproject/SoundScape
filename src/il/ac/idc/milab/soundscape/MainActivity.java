package il.ac.idc.milab.soundscape;

import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = null;
	// instance variables
    GPSTracker m_Gps;
    Button m_ShowLocationButton;
    Button m_RecordButton;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// TODO: Get list of games and create their buttons
		
		// init recorder
//		initRecorder();
		
		// init GPS
//		initGpsTracker();
		
		Button[] buttons  = initGamesButtons();
		
	}

	private Button[] initGamesButtons() {
		// TODO Auto-generated method stub

		// TODO: get games from server?
		String[] opponents = {"Alice", "Bob", "Eve"}; // this simulates list of opponents from server
		
		Button[] buttons = new Button[opponents.length];

		LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
		
		LinearLayout linear = (LinearLayout) findViewById(R.id.mainLayoutLinear);

		for(int i = 0; i < buttons.length; i++)
		{
			Button button = new Button(getApplicationContext());
			button.setLayoutParams(param);
			button.setPadding(10, 5, 10, 5);
			button.setText(opponents[i]);
			buttons[i] = button;
			linear.addView(button);
			button.setOnClickListener(createOnClickListener(button));
		}

		return buttons;
	}

	View.OnClickListener createOnClickListener(final Button button)  {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent startPlayingGameActivity = new Intent("il.ac.idc.milab.soundscape.MainGameActivity");
				startPlayingGameActivity.putExtra("Opponent", button.getText());
				startActivityForResult(startPlayingGameActivity, 0);
			}
		};
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {

		String name = (String) intentData.getExtras().get("Name");
		Log.i(TAG, "Returning to main menu..." + name);
	}
}
