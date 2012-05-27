package edu.tongji.andriylin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
    
    private static final int RINGTONE_LIST_REFRESH = 1;
    
    private RRSettings settings;
    private Handler handler;
    
    private ListView ringtoneListView; 

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        settings = new RRSettings(this.getPreferences(MODE_PRIVATE));
        handler = new Handler(new RRCallback());

        
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
        
        ringtoneListView = (ListView) findViewById(R.id.ringtoneListView);
        Message msg = Message.obtain(handler, RINGTONE_LIST_REFRESH);
        msg.sendToTarget();
    }
    
	private void resetSongList() {
		Map<String, String> ringtones = RRDBManager.get().getRingtones(this);
		List<String> keys = new ArrayList<String>();
		for (String s : ringtones.keySet()) {
			keys.add(s);
		}
		ringtoneListView.setAdapter(new RingtoneItemAdapter(this, keys));
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_RINGTONE_REQUEST) {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				RingtoneUtil util = new RingtoneUtil(RandomRingActivity.this);
//				util.setRingtone(uri);
				RRDBManager.get().insertRingtone(RandomRingActivity.this, util.getTitleByUri(uri), uri.toString());
//				RRDBManager.get().deleteRingtone(RandomRingActivity.this, util.getTitleByUri(uri));
				
				Message msg = Message.obtain(handler, RINGTONE_LIST_REFRESH);
				msg.sendToTarget();
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
	/**
	 * 用于ListView的adapter
	 * @author Andriy
	 */
	private class RingtoneItemAdapter extends BaseAdapter {

		private final Context context;
		private final List<String> ringtones;
		public RingtoneItemAdapter(Context aContext, List<String> names) {
			this.context = aContext;
			this.ringtones = names;
		}
		
		@Override
		public int getCount() {
			return this.ringtones.size();
		}

		@Override
		public Object getItem(int position) {
			return ringtones.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView != null) {
				//据说这里要判断、要优化，不知道怎么确认老的view和新的view是同一种类型并且不需要重绘
			}
			convertView = LayoutInflater.from(context).inflate(R.layout.ringtone_item, null);
			TextView nameText = (TextView) convertView.findViewById(R.id.ringtone_name);
			ImageView deleteImage = (ImageView) convertView.findViewById(R.id.deleteImage);
			nameText.setText(ringtones.get(position));
			deleteImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					RRDBManager.get().deleteRingtone(context, ringtones.get(position));
					Message msg = Message.obtain(handler, RINGTONE_LIST_REFRESH);
					msg.sendToTarget();
				}
			});
			return convertView;
		}
	}
	
	/**
	 * 用于消息机制的回调，处理message
	 * @author Andriy
	 */
	private class RRCallback implements Callback {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == RINGTONE_LIST_REFRESH) {
				RandomRingActivity.this.resetSongList();
				return true;
			}
			
			return false;
		}
		
	}
}