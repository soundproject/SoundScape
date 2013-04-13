package il.ac.idc.milab.soundscape;



import il.ac.idc.milab.soundscape.library.SoundRecorder;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SoundRecordingActivity extends Activity {

	private static final CharSequence MAX_RECORDING_TIME = "15";
	SoundRecorder soundRecorder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_recording);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Button recordingButton = (Button) findViewById(R.id.record_button);
		TextView recordingWordTextView = (TextView) findViewById(R.id.recording_word_text_view);

		String recordingWord = getIntent().getExtras().getString("word");

		recordingWordTextView.setText(recordingWord);

		// init sound recorder:
		initSoundRecorder();

		recordingButton.setText("Start Recording");
		TextView availableTime = (TextView) findViewById(R.id.recording_time_text_view);
		availableTime.setText(MAX_RECORDING_TIME);

		recordingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (soundRecorder != null) {
					if (soundRecorder.isRecording()) {
						soundRecorder.stopRecording();
					} else {
						soundRecorder.startRecording();
					}
				}
			}
		});

	}

	private void initSoundRecorder() {
		// TODO Auto-generated method stub
		// this.soundRecorder = new SoundRecorder(this.getFilesDir());
		this.soundRecorder = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sound_recording, menu);
		return true;
	}

}