package edu.tongji.andriylin;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
    private Button testButton3;
    private Button testButton4;
    
    private static final int PICK_RINGTONE_REQUEST = 1;
    
    private RRSettings settings;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        settings = new RRSettings(this.getPreferences(MODE_PRIVATE));
        
        testButton1 = (Button) findViewById(R.id.button1);
        testButton2 = (Button) findViewById(R.id.button2);
        testButton3 = (Button) findViewById(R.id.button3);
        testButton4 = (Button) findViewById(R.id.button4);

        testButton1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "添加到铃声库中");
				RandomRingActivity.this.startActivityForResult(intent, PICK_RINGTONE_REQUEST);
				//接下来就是在onActivityResult()里接收判定了
			}
		});
        testButton2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RingtoneUtil util = new RingtoneUtil(RandomRingActivity.this);
				Toast.makeText(RandomRingActivity.this, util.getDefaultRingtoneTitle(), Toast.LENGTH_SHORT).show();
			}
		});
        testButton3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Map<String, String> map = RRDBManager.get().getRingtones(RandomRingActivity.this);
				
				AlertDialog.Builder builder = new Builder(RandomRingActivity.this);
				builder.setTitle("ALL that in the database:");
				String message = "";
				for (String s : map.keySet()) {
					message += (s + ": " + map.get(s) + "  || ");
				}
				builder.setMessage(message);
				
				builder.create().show();
			}
		});
        testButton4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_RINGTONE_REQUEST) {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				RingtoneUtil util = new RingtoneUtil(RandomRingActivity.this);
//				util.setRingtone(uri);
//				RRDBManager.get().insertRingtone(RandomRingActivity.this, util.getTitleByUri(uri), uri.toString());
				RRDBManager.get().deleteRingtone(RandomRingActivity.this, util.getTitleByUri(uri));
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
}