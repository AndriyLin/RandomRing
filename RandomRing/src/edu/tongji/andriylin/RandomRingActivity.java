package edu.tongji.andriylin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 主界面activity
 * @author Andriy
 */
public class RandomRingActivity extends Activity {
    
    private static final int PICK_RINGTONE_REQUEST = 1;

    public static final int RINGTONE_LIST_REFRESH = 1;
    public static final int RINGTONE_SHOULD_CHANGE = 2;
    
    private RRSettings settings;
    private Handler handler;
    
    private ListView ringtoneListView; 

    private Button testButton;
    private Button testButton2;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        settings = new RRSettings(this.getPreferences(MODE_PRIVATE));
        handler = new Handler(new RRCallback());
        
        ringtoneListView = (ListView) findViewById(R.id.ringtoneListView);
        Message msg = Message.obtain(handler, RINGTONE_LIST_REFRESH);
        msg.sendToTarget();
        
        testButton = (Button) findViewById(R.id.button1);
        testButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RingtoneUtil util = new RingtoneUtil(RandomRingActivity.this, handler);
				util.registerListener();
			}
		});
        testButton2 = (Button) findViewById(R.id.button2);
        testButton2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RingtoneUtil util = new RingtoneUtil(RandomRingActivity.this, handler);
				util.unregisterListener();
			}
		});
    }
    
    /**
     * 当收到信号的时候刷新list
     */
	private void refreshRingtoneList() {
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
				RingtoneUtil util = new RingtoneUtil(RandomRingActivity.this, handler);
				RRDBManager.get().insertRingtone(RandomRingActivity.this, util.getTitleByUri(uri), uri.toString());
				
				Message msg = Message.obtain(handler, RINGTONE_LIST_REFRESH);
				msg.sendToTarget();
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 随机变换铃声
	 */
	private void changeRingtone() {
		Log.i("__ANDRIY__", "about to change ringtone");

		Map<String, String>map = RRDBManager.get().getRingtones(RandomRingActivity.this);
		if (map.size() == 0) {
			Log.i("__ANDRIY__", "nothing to random");
			return;
		}

		int pos = new Random().nextInt(map.size());
		int i = 0;
		String uriString = null;
		for (String s : map.keySet()) {
			if (i == pos) {
				uriString = map.get(s);
				Log.i("__ANDRIY__", "randomed, got " + s + " : " + uriString);
				break;
			}
			i++;
		}

		Uri uri = Uri.parse(uriString);
		RingtoneUtil util = new RingtoneUtil(this, handler);
		util.setRingtone(uri);
		
		Log.i("__ANDRIY__", "new ringtone set");
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
			return this.ringtones.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (position < ringtones.size()) {
				return ringtones.get(position);
			}
			return null;
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
			
			if (position < ringtones.size()) {
				//正常的一个item
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
			}
			else {
				//额外的，显示 + 符号
				convertView = LayoutInflater.from(context).inflate(R.layout.ringtone_item_extra, null);
				ImageView addImage = (ImageView) convertView.findViewById(R.id.addImageView);
				addImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//选取铃声
						Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
						intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
						intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "添加到铃声库中");
						RandomRingActivity.this.startActivityForResult(intent, PICK_RINGTONE_REQUEST);
						//接下来就是在onActivityResult()里接收判定了
					}
				});
			}
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
				RandomRingActivity.this.refreshRingtoneList();
				return true;
			}
			else if (msg.what == RINGTONE_SHOULD_CHANGE) {
				RandomRingActivity.this.changeRingtone();
				return true;
			}
			
			return false;
		}
	}

}