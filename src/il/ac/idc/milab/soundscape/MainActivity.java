package il.ac.idc.milab.soundscape;

import android.R.bool;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button recordButton = (Button) findViewById(R.id.mainBtnRecord);
		
		recordButton.setOnClickListener(new OnClickListener() {			
			private Button m_ThisButton = (Button) findViewById(R.id.mainBtnRecord);
			private SoundRecorder m_SoundRecorder = new SoundRecorder();
			
			private boolean m_Recording; 
			
			@Override
			public void onClick(View v) {
				
				if (m_Recording)
				{
					m_SoundRecorder.stopRecording();
				} else
				{
					m_SoundRecorder.startRecording();
				}
				
				String text = m_Recording ? getString(R.string.main_btn_record) :
								getString(R.string.main_btn_recording);
				
				m_Recording = !m_Recording;
				
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
