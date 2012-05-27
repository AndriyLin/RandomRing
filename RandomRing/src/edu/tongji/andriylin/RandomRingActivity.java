package edu.tongji.andriylin;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 主界面activity
 * @author Andriy
 */
public class RandomRingActivity extends Activity {

    private Button testButton1;
    private Button testButton2;
    
    private static final int PICK_RINGTONE_REQUEST = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        testButton1 = (Button) findViewById(R.id.button1);
        testButton2 = (Button) findViewById(R.id.button2);
        testButton1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "添加到铃声库中");
				RandomRingActivity.this.startActivityForResult(intent, PICK_RINGTONE_REQUEST);
			}
		});
        testButton2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RingtoneUtil util = new RingtoneUtil(RandomRingActivity.this);
				Toast.makeText(RandomRingActivity.this, util.getDefaultRingtoneTitle(), Toast.LENGTH_SHORT).show();
			}
		});
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_RINGTONE_REQUEST) {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				RingtoneUtil util = new RingtoneUtil(RandomRingActivity.this);
				util.setRingtone(uri);
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
}