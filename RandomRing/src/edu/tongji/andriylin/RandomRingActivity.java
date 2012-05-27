package edu.tongji.andriylin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 主界面activity
 * @author Andriy
 */
public class RandomRingActivity extends Activity {
    
    private static final int PICK_RINGTONE_REQUEST = 1;
    
    private static final int NOTIFICATION_ID = 1;

    public static final int RINGTONE_LIST_REFRESH = 1;
    public static final int RINGTONE_SHOULD_CHANGE = 2;
    public static final int RINGTONE_RANDOM_ON = 3;
    public static final int RINGTONE_RANDOM_OFF = 4;
    
    private Handler handler;
    private final Random random = new Random();
    
    private ListView ringtoneListView; 
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        handler = new Handler(new RRCallback());
        
        ringtoneListView = (ListView) findViewById(R.id.ringtoneListView);
        Message msg = Message.obtain(handler, RINGTONE_LIST_REFRESH);
        msg.sendToTarget();
        
		RingtoneUtil util = new RingtoneUtil(RandomRingActivity.this, handler);
		util.registerListener();
		
		this.showNotification(true);
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
				if (uri == null) {
					//选择了"静音"
					return;
				}
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
		Map<String, String>map = RRDBManager.get().getRingtones(RandomRingActivity.this);
		if (map.size() == 0) {
			return;
		}
		RingtoneUtil util = new RingtoneUtil(this, handler);
		Log.i("__ANDRIY__", "before: " + util.getCurrentRingtoneTitle());

		int pos = random.nextInt(map.size());
		Log.i("__ANDRIY__", "random: " + pos + " / " + map.size());
		int i = 0;
		String uriString = null;
		for (String s : map.keySet()) {
			if (i == pos) {
				uriString = map.get(s);
				Log.i("__ANDRIY__", "going to set: " + s);
				break;
			}
			i++;
		}

		Uri uri = Uri.parse(uriString);
		util.setRingtone(uri);
		Log.i("__ANDRIY", "after: " + util.getCurrentRingtoneTitle());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.basic_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about_item:
			 // 展示about信息
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(R.string.about_text);
			builder.setMessage(R.string.about_content);
			builder.create().show();
			return true;

		case R.id.quit_item:
			this.finish();
			return true;

		default:
			return false;
		}
	}


	@Override
	public void onBackPressed() {
		//后退不退出，回到主屏幕
		Intent toHome = new Intent(Intent.ACTION_MAIN);
		toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		toHome.addCategory(Intent.CATEGORY_HOME);
		startActivity(toHome);
	}


	@Override
	protected void onDestroy() {
		RingtoneUtil util = new RingtoneUtil(this, handler);
		util.unregisterListener();
		this.showNotification(false);

		super.onDestroy();
	}

	/**
	 * 当随机切换功能开启或关闭的时候，通知栏告诉
	 * @param on 开启？
	 */
	private void showNotification(boolean on) {
		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		Resources resources = this.getResources();

		CharSequence text = resources.getString(R.string.random_service)
				+ resources.getString(on ? R.string.random_on : R.string.random_off);
		int icon = on ? R.drawable.correct : R.drawable.incorrect;
		Notification notification = new Notification(icon, text, System.currentTimeMillis());
		CharSequence contentTitle = resources.getString(R.string.random_service);
		CharSequence contentText = resources.getString(on ? R.string.random_on : R.string.random_off);

		Intent notificationIntent = new Intent(this, RandomRingActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, 	notificationIntent, 0);
		notification.setLatestEventInfo(this, contentTitle, contentText,	contentIntent);
		
		nm.notify(NOTIFICATION_ID, notification);
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
				nameText.setText(ringtones.get(position));

				ImageView deleteImage = (ImageView) convertView.findViewById(R.id.deleteImage);
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
						intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "添加");
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
			if (msg.what == RINGTONE_SHOULD_CHANGE) {
				RandomRingActivity.this.changeRingtone();
				return true;
			}
			
			return false;
		}
	}

}