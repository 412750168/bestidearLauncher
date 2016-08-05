package net.bestidear.bestidearlauncher;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import net.bestidear.bestidearlauncher.model.AllappcellLayout;
import net.bestidear.bestidearlauncher.model.AddLayout;
import net.bestidear.bestidearlauncher.model.CellInfo;
import net.bestidear.bestidearlauncher.model.WorkplaceLayout;
import net.bestidear.bestidearlauncher.model.ApplicationInfo;
import net.bestidear.bestidearlauncher.utils.StringUtil;
import net.bestidear.bestidearlauncher.utils.Utils;
import net.bestidear.bestidearlauncher.workplace.ContactService;
import net.bestidear.bestidearlauncher.workplace.WorkplaceConfig;
import net.bestidear.weather.SetCityActivity;
import net.bestidear.weather.WeatherHandler;
import net.bestidear.weather.WeatherInfo;
import net.bestidear.weather.WeatherUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

public class Launcher extends MyActivity {

	private static String Tag = "Launcher";

	public static boolean isUseDefault = true;

	private String jsonData;
	public static List<CellInfo> listCellinfo;
	// public static int installCount;

	private WorkplaceConfig workplaceConfig;

	private LinearLayout base;
	private WorkplaceLayout workplace;
	private HorizontalScrollView hSV;

	private AddLayout addblocklayout;

	int WifiIconLevel = -1;
	int signalLevel = -1;

	private boolean _3G_STATUS;

	private ImageView wifi_image = null;
	private ImageView bluetooth_image = null;
	private ImageView _3G_image = null;
	private ImageView net_image = null;
	private ImageView sdcard_image = null;
	private ImageView usb_image = null;

	// private WeatherHandler weatherHandler = null;

	public static final String CITY_CODE_FILE = "city_code";
	public static final String STORE_WEATHER = "store_weather";

	public static final String LAUNCHER_CUSTOM_FILE = "launcher_custom";

	private ImageView weather_icon = null;
	private TextView City_weather_text = null;
	private TextView weather_temp = null;
	private View weather_layout = null;

	private BluetoothAdapter adapter = null;

	private Context context;

	ProgressDialog myDialog;

	public static int FocusX = 0;
	public static int FocusY = 0;

	TelephonyManager tel;

	public static boolean isFirstRun = true;
	public static boolean goLive = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		IWindowManager mWindowManager = IWindowManager.Stub
				.asInterface(ServiceManager.getService("window"));
		try {
			mWindowManager.setAnimationScale(1, 0.0f);
		} catch (RemoteException e) {
		}

		context = this;

		// FocusX = 0;
		// FocusY = 0;
		Log.e(Tag, "onCreate()");

		if (isFirstRun) {
			isFirstRun = false;
			goLive = true;
			waitLive();
		}

		base = (LinearLayout) findViewById(R.id.base);

		wifi_image = (ImageView) findViewById(R.id.wifi_ico);
		bluetooth_image = (ImageView) findViewById(R.id.bluetooth_ico);
		_3G_image = (ImageView) findViewById(R.id._3G_ico);
		net_image = (ImageView) findViewById(R.id.net_ico);
		sdcard_image = (ImageView) findViewById(R.id.SDcard_ico);
		usb_image = (ImageView) findViewById(R.id.USB_ico);

		hSV = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
		workplace = (WorkplaceLayout) findViewById(R.id.workplaceLayout);
		if (hSV != null) {
			workplace.setHSW(hSV);
		}
		workplaceConfig = new WorkplaceConfig(context);

		addblocklayout = (AddLayout) findViewById(R.id.addblocklayout);
		addblocklayout.makeAddLayout();
		addblocklayout.setHandler(myHandler);

		myDialog = new ProgressDialog(this);
		// myDialog.setMessage("请稍后");
		myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		setWallpaper();

		registerIntentReceivers();

		weather_icon = (ImageView) findViewById(R.id.weather_icon);
		City_weather_text = (TextView) findViewById(R.id.day_weather);
		weather_temp = (TextView) findViewById(R.id.day_temp);
		weather_layout = findViewById(R.id.weather);

		weather_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, SetCityActivity.class);
				startActivity(intent);

			}
		});

		// weatherHandler = new WeatherHandler(this);

		sendUPDATE();

		// LoadConfig();

	}

	private void waitLive() {
		new Thread() {
			@Override
			public void run() {
				try {
					this.sleep(7000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (goLive) {
					myHandler.sendEmptyMessage(LiveIntent);
				}
			}
		}.start();
	}

	private void goLive() {
		Intent liveIntent = new Intent();
		liveIntent.setClassName("com.togic.livevideo",
				"com.togic.tv.channel.TvUiActivity");
		liveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		try {
			// Toast.makeText(context,
			// "Utils.isNetworkConnected:"+Utils.isNetworkConnected(context),
			// Toast.LENGTH_LONG).show();
			if (Utils.isNetworkAvailable(context)) {
				this.startActivity(liveIntent);
			}
		} catch (Exception e) {
		}
	}

	private void LoadConfig() {
		// myDialog.show();
		new Thread() {
			@Override
			public void run() {

				listCellinfo = workplaceConfig.getConfig(0);
				if (listCellinfo != null) {

					myHandler.removeMessages(makeCelllayout);
					myHandler.sendEmptyMessage(makeCelllayout);
				}
				// myDialog.dismiss();
			}
		}.start();
	}

	public boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			android.content.pm.ApplicationInfo info = context
					.getPackageManager().getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	private synchronized void makeCelllayout() {

		checkInstall();

		SharedPreferences sp = context.getSharedPreferences(
				Launcher.LAUNCHER_CUSTOM_FILE, Launcher.MODE_PRIVATE);
		SharedPreferences.Editor editor = getSharedPreferences(
				Launcher.LAUNCHER_CUSTOM_FILE, Launcher.MODE_PRIVATE).edit();

		boolean FirstRun = sp.getBoolean("FirstRun", true);
		if (FirstRun) {
			StringBuilder sb = new StringBuilder();
			for (CellInfo cellinfo : listCellinfo) {
				if (!cellinfo.getPackageName().equals("")
						&& cellinfo.isInstall())
					sb.append(cellinfo.getPackageName() + ",");
			}

			editor.putString("apps", sb.toString());
			editor.putBoolean("FirstRun", false);
			editor.commit();
		}
/*
		if (!FirstRun) {
			String str = sp.getString("apps", "");

			List<CellInfo> list = workplaceConfig.getConfig(0);
			for (CellInfo cellinfo : list) {

				if (!cellinfo.getPackageName().equals("")
						&& checkApkExist(this,cellinfo.getPackageName())
						&& !str.contains(cellinfo.getPackageName())) {
					str = str +","+ cellinfo.getPackageName() + ",";
				}

			}
			editor.putString("apps", str);
			editor.putBoolean("FirstRun", false);
			editor.commit();
		}
*/
		checkDisplay();

		workplace.removeAllViews();
		workplace.makeCelllayout();

		// for(cellInfo info:listCellinfo){
		// Log.d("=======", "-------------------------------");
		// Log.d("=======", info.getPackageName());
		// Log.d("=======", info.isInstall()+"");
		// Log.d("=======", info.isNotDisplay()+"");
		// Log.d("=======", "-------------------------------");
		// }

		if (FocusX == 0 && FocusY == 0) {
			workplace.requestFocus();
		}
		// workplace.requestFocus ();

	}

	private void checkDisplay() {
		SharedPreferences sp = context.getSharedPreferences(
				Launcher.LAUNCHER_CUSTOM_FILE, Launcher.MODE_PRIVATE);
		String temp = sp.getString("apps", "");

		String apks[] = temp.trim().split(",");

		for (CellInfo cinfo : listCellinfo) {
			boolean isNotDisplay = true;
			if (cinfo.getType() == 0) {
				for (String tempStr : apks) {
					// Log.d("=======", cinfo.getPackageName()+"---"+tempStr);
					if (cinfo.getPackageName().equals(tempStr)) {
						isNotDisplay = false;
					}
				}
				cinfo.SetNotDisplay(isNotDisplay);
			}

		}

	}

	private void checkInstall() {
		// installCount = 0;
		int i = 0;
		for (CellInfo cinfo : listCellinfo) {
			if (cinfo.getType() == 0) {
				if (checkApkExist(cinfo.getPackageName(), i)) {
					Log.d(Tag, "cinfo.setInstall(true)");
					cinfo.setInstall(true);
					// installCount++;
				} else {
					Log.d(Tag, "cinfo.setInstall(false)");
					cinfo.setInstall(false);
				}
			} else {
				cinfo.setInstall(true);
				// installCount++;
			}
			i++;
		}
	}

	public boolean checkApkExist(String packageName, int i) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			PackageManager pm = context.getPackageManager();
			android.content.pm.ApplicationInfo info = pm.getApplicationInfo(
					packageName, 0);
			String name = (String) pm.getApplicationLabel(info);
			Drawable appicon = pm.getApplicationIcon(info);
			if (name != null && appicon != null) {
				int isUserPic = AllappcellLayout.isUsePic(packageName);
				if (isUserPic == -1) {
					listCellinfo.get(i).setAppicon(appicon);
				} else {
					listCellinfo.get(i).setAppicon(
							getResources().getDrawable(
									AllappcellLayout.appPic[isUserPic]));
				}
				listCellinfo.get(i).setCellName(name);
			}

			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	private void DisplayWeather(boolean isDisplay) {
		if (isDisplay) {
			weather_layout.setVisibility(View.VISIBLE);
		} else {
			weather_layout.setVisibility(View.GONE);
		}
	}

	private void sendUPDATE() {
		// weatherHandler.removeMessages(WeatherHandler.CHECK_UPDATE);
		// weatherHandler.sendEmptyMessage(WeatherHandler.CHECK_UPDATE);
	}

	private void setWallpaper() {
		File file = new File("/etc/biconf/", "defaultwallpaper.jpg");
		if (file.exists()) {
			BitmapDrawable bd = new BitmapDrawable(getResources(),
					file.getAbsolutePath());
			base.setBackground(bd);
		} else {
			base.setBackgroundResource(R.drawable.main_bg);
		}
	}

	private void registerIntentReceivers() {
		if (tel == null) {
			tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		}
		tel.listen(new PhoneStateMonitor(),
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
						| PhoneStateListener.LISTEN_SERVICE_STATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		// filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		// filter.addAction(EthernetManager.ETH_STATE_CHANGED_ACTION);
		// filter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(wifibluetoothReceiver, filter);

		// filter = new IntentFilter();
		// filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		// filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		// filter.addDataScheme("file");
		// registerReceiver(UsbBroadCastReceiver, filter);
		//
		// filter = new IntentFilter();
		// filter.addAction(UsbManager.ACTION_USB_STATE);
		// registerReceiver(UsbBroadCastReceiver, filter);

		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(packageReceiver, filter);

		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(HomeKeyEventBroadCastReceiver, filter);

	}

	public BroadcastReceiver HomeKeyEventBroadCastReceiver = new BroadcastReceiver() {
		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";// home key
		static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						// home处理点
						Log.e(Tag, "HOMEDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
						// Toast.makeText(context, "Home键被点击",
						// Toast.LENGTH_SHORT).show();
						FocusX = 0;
						FocusY = 0;
					}
				}
			}
		}
	};

	private final int WifiMessage_Open_MSG = 10;
	private final int WifiMessage_Close_MSG = 11;
	private final int WifiMessage_Change_MSG = 12;
	private final int Bluetooth_Open = 13;
	private final int Bluetooth_Close = 14;
	private final int _3G_Open = 15;
	private final int _3G_Close = 16;
	private final int Net_Open = 17;
	private final int Net_Close = 18;
	public final static int DisplayWeather = 20;
	public final static int GoneWeather = 21;
	private final int Sdcard_open = 22;
	private final int Sdcard_close = 23;
	private final int usb_open = 24;
	private final int usb_close = 25;
	private final int makeCelllayout = 26;
	public final static int ShowDailog = 30;
	public final static int LiveIntent = 31;

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WifiMessage_Change_MSG:
			case WifiMessage_Open_MSG:
				UpdateLauncherSearch_bar_ICO(1);
				sendUPDATE();
				break;
			case WifiMessage_Close_MSG:
				UpdateLauncherSearch_bar_ICO(2);
				break;
			case Bluetooth_Open:
				UpdateLauncherSearch_bar_ICO(3);
				break;
			case Bluetooth_Close:
				UpdateLauncherSearch_bar_ICO(4);
				break;
			case _3G_Open:
				UpdateLauncherSearch_bar_ICO(5);
				sendUPDATE();
				break;
			case _3G_Close:
				UpdateLauncherSearch_bar_ICO(6);
				break;
			case Net_Open:
				UpdateLauncherSearch_bar_ICO(7);
				sendUPDATE();
				break;
			case Net_Close:
				UpdateLauncherSearch_bar_ICO(8);
				break;
			case Sdcard_open:
				UpdateLauncherSearch_bar_ICO(9);
				break;
			case Sdcard_close:
				UpdateLauncherSearch_bar_ICO(10);
				break;
			case usb_open:
				UpdateLauncherSearch_bar_ICO(11);
				break;
			case usb_close:
				UpdateLauncherSearch_bar_ICO(12);
				break;
			case DisplayWeather:
				DisplayWeather(true);
				break;
			case GoneWeather:
				DisplayWeather(false);
				break;
			case makeCelllayout:
				makeCelllayout();
				break;
			case ShowDailog:
				showDailog();
				break;
			case LiveIntent:
				goLive();
				break;
			}
		}

	};

	private void showDailog() {
		// Log.d(Tag, "ShowDailog");
		// String[] s = new String[addLayout.appnames.size()];
		// for(int i = 0 ; i<addLayout.appnames.size();i++){
		// s[i] = addLayout.appnames.get(i).title;
		// }
		//
		// final boolean[] b = new boolean[addLayout.appstate.size()];
		// for(int i = 0 ; i<addLayout.appstate.size();i++){
		// if(addLayout.appstate.get(i)){
		// b[i]=true;
		// }
		// }
		//
		// Builder builder = new Builder(context);
		// builder.setMultiChoiceItems(s, b, new OnMultiChoiceClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// builder.setPositiveButton("确认", new DialogInterface.OnClickListener()
		// {
		// String result = "";
		// @Override
		// public void onClick(DialogInterface arg0, int arg1) {
		// // TODO Auto-generated method stub
		// for(int i= 0;i < b.length;i++){
		// if(b[i]){
		// if(!result.equals("")){
		// result += ",";
		// }
		// result += addLayout.appnames.get(i).packagename;
		// }
		// }
		// SharedPreferences.Editor editor =
		// context.getSharedPreferences(Launcher.LAUNCHER_CUSTOM_FILE,
		// Launcher.MODE_PRIVATE).edit();
		// editor.putString("apps", result);
		// editor.commit();
		//
		// LoadConfig();
		// // Toast.makeText(context, result, Toast.LENGTH_LONG).show();
		// }
		// });
		// builder.create().show();
		Intent choiceIt = new Intent(context, ChoiceActivity.class);
		if (choiceIt != null) {
			this.startActivity(choiceIt);
		}
	}

	@Override
	public void updateWeather() {
		SharedPreferences shared = context.getSharedPreferences(STORE_WEATHER,
				MODE_PRIVATE);
		SharedPreferences sharedCityCode = context.getSharedPreferences(
				CITY_CODE_FILE, MODE_PRIVATE);
		String OldCityCode = shared.getString("city", "");
		String NewCityCode = sharedCityCode
				.getString("cityname", "")
				.substring(
						sharedCityCode.getString("cityname", "").indexOf(".") + 1);
		Log.d("NewCity", NewCityCode);
		Log.d("OldCity", OldCityCode);
		long currentTime = System.currentTimeMillis();
		long vaildTime = shared.getLong("validTime", -1);

		WeatherInfo weatherInfo = WeatherUtil.getWeatherFromSP(shared);

		if (StringUtil.isToday(weatherInfo.getDate_y()) && vaildTime != -1
				&& OldCityCode.equals(NewCityCode) && currentTime <= vaildTime) {

			weather_temp.setText(weatherInfo.getCity());

			City_weather_text.setText(weatherInfo.getWeather1());

			weather_temp.setText(weather_temp.getText() + " "
					+ weatherInfo.getTemp() + "℃");

			String img_single = weatherInfo.getImg_single();
			int icon = WeatherUtil.getWeatherBitMapResource(img_single);
			weather_icon.setImageResource(icon);

			DisplayWeather(true);
		} else {
			DisplayWeather(false);
		}

		// weatherHandler.removeMessages(WeatherHandler.CHECK_UPDATE);
		// weatherHandler.sendEmptyMessageDelayed(WeatherHandler.CHECK_UPDATE,
		// 30*60*1000);
	}

	public void UpdateLauncherSearch_bar_ICO(int type) {
		if (type == 1) {
			if (WifiIconLevel <= 0) {
				wifi_image
						.setBackgroundResource(R.drawable.ic_sysbar_wifi_signal_none);
			}
			if (WifiIconLevel == 1) {
				wifi_image
						.setBackgroundResource(R.drawable.ic_sysbar_wifi_signal_1_fully);
			}
			if (WifiIconLevel == 2) {
				wifi_image
						.setBackgroundResource(R.drawable.ic_sysbar_wifi_signal_2_fully);
			}

			if (WifiIconLevel >= 3) {
				wifi_image
						.setBackgroundResource(R.drawable.ic_sysbar_wifi_signal_3_fully);
			}
			wifi_image.setVisibility(View.VISIBLE);
		} else if (type == 2) {
			wifi_image.setVisibility(View.GONE);
		} else if (type == 3) {
			bluetooth_image.setVisibility(View.VISIBLE);
		} else if (type == 4) {
			bluetooth_image.setVisibility(View.GONE);
		} else if (type == 5) {
			switch (signalLevel) {
			case 0:
				_3G_image
						.setBackgroundResource(R.drawable.stat_sys_signal_0_fully);
				break;
			case 1:
				_3G_image
						.setBackgroundResource(R.drawable.stat_sys_signal_1_fully);
				break;
			case 2:
				_3G_image
						.setBackgroundResource(R.drawable.stat_sys_signal_2_fully);
				break;
			case 3:
				_3G_image
						.setBackgroundResource(R.drawable.stat_sys_signal_3_fully);
				break;
			case 4:
				_3G_image
						.setBackgroundResource(R.drawable.stat_sys_signal_4_fully);
				break;
			}
			_3G_image.setVisibility(View.VISIBLE);
		} else if (type == 6) {
			_3G_image.setVisibility(View.GONE);
		} else if (type == 7) {
			net_image.setVisibility(View.VISIBLE);
		} else if (type == 8) {
			net_image.setVisibility(View.GONE);
		} else if (type == 9) {
			sdcard_image.setVisibility(View.VISIBLE);
		} else if (type == 10) {
			sdcard_image.setVisibility(View.GONE);
		} else if (type == 11) {
			usb_image.setVisibility(View.VISIBLE);
		} else if (type == 12) {
			usb_image.setVisibility(View.GONE);
		}
	}

	private List<String> Usb_list = new ArrayList<String>();

	private int checkUsblist(String uri) {
		int i = 0;
		for (String temp : Usb_list) {
			if (uri.equals(temp)) {
				Usb_list.remove(i);
				break;
			}
			i++;
		}

		return Usb_list.size();
	}

	// public BroadcastReceiver UsbBroadCastReceiver = new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
	// String uri = intent.getData().toString();
	// // if(uri.indexOf("external_sdcard") !=-1 ){
	// if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	// Log.d(Tag, "Received SDCard Mount Event!");
	// myHandler.sendEmptyMessage(Sdcard_open);
	// }
	// // else if(uri.indexOf("sda") !=-1||uri.indexOf("sdb") !=-1 ){
	// // Log.d(Tag, "Received USB Mount Event!");
	// // Usb_list.add(uri);
	// // if(Usb_list.size()>0){
	// // myHandler.sendEmptyMessage(usb_open);
	// // }
	// // }
	// } else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
	// String uri = intent.getData().toString();
	// // if(uri.indexOf("external_sdcard") !=-1 ){
	// if(Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)){
	// Log.d(Tag, "Received SDCard UnMount Event!");
	// myHandler.sendEmptyMessage(Sdcard_close);
	// }
	// // else{
	// // Log.d(Tag, "Received USB UnMount Event!");
	// // if(checkUsblist(uri)==0){
	// // myHandler.sendEmptyMessage(usb_close);
	// // }
	// // }
	// }
	// }
	// };

	public BroadcastReceiver wifibluetoothReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
				// Log.d(Tag, "wifiReceiver:WifiManager.RSSI_CHANGED_ACTION");
				WifiIconLevel = getStrength(context);
				// Log.d(Tag, "信号强度:"+WifiIconLevel);
				if (WifiIconLevel != 0) {
					myHandler.sendEmptyMessage(WifiMessage_Change_MSG);
				} else {
					myHandler.sendEmptyMessage(WifiMessage_Close_MSG);
				}
			} else if (intent.getAction().equals(
					"android.net.conn.CONNECTIVITY_CHANGE")) {
				Log.d("ljl", "android.net.conn.CONNECTIVITY_CHANGE");
				if (Utils.isNetworkAvailable(context)) {
					Log.d("ljl", "connected");
					int WifiIconLevel = getStrength(context);
					if (Utils.isWifiConnected(context) && WifiIconLevel > 0) {
						Log.d("ljl", "WifiWifiWificonnected");
						myHandler.sendEmptyMessage(WifiMessage_Open_MSG);
						myHandler.sendEmptyMessage(Net_Close);
					} else {
						Log.d("ljl", "EthEthconnected");
						myHandler.sendEmptyMessage(WifiMessage_Close_MSG);
						myHandler.sendEmptyMessage(Net_Open);
					}
				} else {
					Log.d("ljl", "unconnected");
					myHandler.sendEmptyMessage(WifiMessage_Close_MSG);
					myHandler.sendEmptyMessage(Net_Close);
				}

				// }else
				// if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
				// // Log.d(Tag, "WifiManager.WIFI_STATE_CHANGED_ACTION");
				// //WIFI开关
				// int
				// wifistate=intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_DISABLED);
				// // Log.d(Tag, "wifistate:"+wifistate);
				// WifiIconLevel = getStrength(context);
				// // Log.d(Tag, "信号强度:"+WifiIconLevel);
				// if(wifistate==WifiManager.WIFI_STATE_DISABLED ||
				// WifiIconLevel == 0)
				// {//如果关闭
				// // Log.d(Tag, "WifiMessage_Close_MSG");
				// myHandler.sendEmptyMessage(WifiMessage_Close_MSG);
				// }else if(wifistate==WifiManager.WIFI_STATE_ENABLED){
				// // Log.d(Tag, "WifiMessage_Open_MSG");'\
				// myHandler.sendEmptyMessage(WifiMessage_Open_MSG);
				// }
				// }else
				// if(intent.getAction().equals(EthernetManager.ETH_STATE_CHANGED_ACTION)){
				// Log.d(Tag, "EthernetManager.ETH_STATE_CHANGED_ACTION");
				// int event =
				// intent.getIntExtra(EthernetManager.EXTRA_ETH_STATE,EthernetManager.ETH_STATE_DISABLED);
				// Log.d(Tag, "event:"+event);
				// switch (event) {
				// //网络连接：
				// case EthernetStateTracker.EVENT_HW_CONNECTED:
				// case
				// EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_SUCCEEDED:
				// Log.d(Tag, "ETH_STATE_ENABLED");
				// myHandler.sendEmptyMessage(Net_Open);
				// break;
				// //网络断开：
				// case EthernetStateTracker.EVENT_HW_DISCONNECTED:
				// case
				// EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_FAILED:
				// Log.d(Tag, "ETH_STATE_DISABLED");
				// myHandler.sendEmptyMessage(Net_Close);
				// break;
				// }
			} else if (intent.getAction().equals(
					BluetoothAdapter.ACTION_STATE_CHANGED)) {
				// Log.d(Tag, "BluetoothAdapter.ACTION_STATE_CHANGED");
				adapter = BluetoothAdapter.getDefaultAdapter();
				int blueState = adapter.getState();
				if (blueState == BluetoothAdapter.STATE_ON) {// 打开蓝牙
				// Log.d(Tag, "BluetoothAdapter.STATE_ON");
					myHandler.sendEmptyMessage(Bluetooth_Open);
				} else if (blueState == BluetoothAdapter.STATE_OFF) {// 关闭蓝牙
				// Log.d(Tag, "BluetoothAdapter.STATE_OFF");
					myHandler.sendEmptyMessage(Bluetooth_Close);
				}
			}
		}
	};

	private void getConfigFromNet() {
		Log.d(Tag, "getConfigFromNet()");
		new Thread() {
			public void run() {
				ContactService service = new ContactService(context);
				try {
					boolean isSuccess = service.getConfig();
					Log.d(Tag, "getConfigFromNet.getConfig():" + isSuccess);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

	}

	public int getStrength(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info.getBSSID() != null) {
			int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
			// 链接速度
			// int speed = info.getLinkSpeed();
			// // 链接速度单位
			// String units = WifiInfo.LINK_SPEED_UNITS;
			// // Wifi源名称
			// String ssid = info.getSSID();
			return strength;

		}
		return 0;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateWeather();
		LoadConfig();
		// SDUSBcheck();
		registerIntentReceivers();

		Log.e(Tag, "onResume()");
	}

	// private void SDUSBcheck(){
	// String SdcardState = Environment.getExternalStorageState();
	// if(SdcardState != null){
	// if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	// myHandler.sendEmptyMessage(Sdcard_open);
	// }else{
	// myHandler.sendEmptyMessage(Sdcard_close);
	// }
	// }else{
	// myHandler.sendEmptyMessage(Sdcard_close);
	// }
	//
	// BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
	// if(ba != null){
	// if(ba.isEnabled()){
	// myHandler.sendEmptyMessage(Bluetooth_Open);
	// }else{
	// myHandler.sendEmptyMessage(Bluetooth_Close);
	// }
	// }else{
	// myHandler.sendEmptyMessage(Bluetooth_Close);
	// }
	// }

	@Override
	public void onPause() {
		super.onPause();
		goLive = false;

		Log.e(Tag, "onPause()");
		unregisterReceiver(packageReceiver);
		unregisterReceiver(wifibluetoothReceiver);
		unregisterReceiver(HomeKeyEventBroadCastReceiver);
		// unregisterReceiver(UsbBroadCastReceiver);
	}

	private boolean mKeydownRepeatFlag = false;
	private int repeatCount = 0;
	private String Player_PKG_NAME = "net.bestidear.bestidearvideotest";

	public static final int KEYCODE_PROG_RED = 183;
	public static final int KEYCODE_PROG_GREEN = 184;
	public static final int KEYCODE_PROG_YELLOW = 185;
	public static final int KEYCODE_PROG_BLUE = 186;
	public static final int KEYCODE_SETTING = 140;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Log.d(Tag, event.toString());
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d(Tag, "KEYCODE_BACK");
			if (mKeydownRepeatFlag == true && repeatCount >= 4) {
				String testFilePath = parseTestVideoFile();
				mKeydownRepeatFlag = false;
				repeatCount = 0;
				Log.d(Tag, "StartVIDEOTEST");
				if (testFilePath != null) {
					Intent it = new Intent("android.intent.action.player_box");
					it.setPackage(Player_PKG_NAME);
					it.putExtra("filePath", testFilePath);
					startActivity(it);
				} else {
					Log.d(Tag, "StartVIDEOTEST:but testFilePath=null");
				}
			} else {
				mKeydownRepeatFlag = true;
			}
			repeatCount++;
			return true;
		} else if (keyCode == KEYCODE_SETTING || keyCode == KEYCODE_PROG_RED
				|| keyCode == KEYCODE_PROG_GREEN
				|| keyCode == KEYCODE_PROG_YELLOW
				|| keyCode == KEYCODE_PROG_BLUE) {
			Intent keycodeintent = new Intent();
			keycodeintent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			switch (keyCode) {
			case KEYCODE_PROG_RED:
				keycodeintent.setClassName("com.togic.livevideo",
						"com.togic.tv.channel.TvUiActivity");
				break;
			case KEYCODE_PROG_YELLOW:
				keycodeintent.setClassName("net.bestidear.videopolymerization",
						"net.bestidear.videopolymerization.MainActivity");
				break;
			case KEYCODE_PROG_GREEN:
				keycodeintent.setClassName("net.bestidear.ota",
						"net.bestidear.ota.Update");
				break;
			case KEYCODE_PROG_BLUE:
				keycodeintent.setClassName("net.bestidear.tvs",
						"net.bestidear.tvs.MainActivity");
				break;
			case KEYCODE_SETTING:
				keycodeintent
						.setAction(android.provider.Settings.ACTION_SETTINGS);
				break;
			default:
				break;
			}

			try {
				context.startActivity(keycodeintent);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		// Log.d(Tag, keyCode+","+event.toString());
		return super.onKeyDown(keyCode, event);
		// return false;
	}

	private String parseTestVideoFile() {
		String filepath = System.getProperty("ro.product.test.path",
				"/mnt/sdcard/video/");
		String filename = System.getProperty("ro.product.test.filename",
				"utoo_test_hd_video");
		Log.d(Tag, "filepath" + filepath);
		Log.d(Tag, "filename" + filename);
		int i = 0;
		String filePath = "";
		File dir = new File(filepath);
		if (!dir.exists())
			return null;
		File[] files = dir.listFiles();
		for (i = 0; i < files.length; i++) {
			File f = files[i];
			String fileName = f.getName();
			int ext_pos = fileName.lastIndexOf(".");
			if (ext_pos <= 0)
				return null;
			fileName = fileName.substring(0, fileName.lastIndexOf("."))
					.toLowerCase();
			if (fileName.equals(filename)) {
				filePath = f.getPath();
				return filePath;
			}
		}
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(wifibluetoothReceiver);

	}

	public class PhoneStateMonitor extends PhoneStateListener {
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			/*
			 * signalStrength.isGsm() 是否GSM信号 2G or 3G
			 * signalStrength.getCdmaDbm(); 联通3G 信号强度
			 * signalStrength.getCdmaEcio(); 联通3G 载干比
			 * signalStrength.getEvdoDbm(); 电信3G 信号强度
			 * signalStrength.getEvdoEcio(); 电信3G 载干比
			 * signalStrength.getEvdoSnr(); 电信3G 信噪比
			 * signalStrength.getGsmSignalStrength(); 2G 信号强度
			 * signalStrength.getGsmBitErrorRate(); 2G 误码率 载干比
			 * ，它是指空中模拟电波中的信号与噪声的比值
			 */

			if (!signalStrength.isGsm()) {
				int NetType = tel.getNetworkType();
				// Log.d(Tag, "NetType : "+NetType);
				if (NetType == TelephonyManager.NETWORK_TYPE_CDMA) {
					// Log.d(Tag,
					// "NetType : "+"TelephonyManager.NETWORK_TYPE_CDMA");
					signalLevel = getCdmaLevel(signalStrength.getCdmaDbm(),
							signalStrength.getCdmaEcio());
				} else if (NetType == TelephonyManager.NETWORK_TYPE_EVDO_0
						|| NetType == TelephonyManager.NETWORK_TYPE_EVDO_A) {
					// Log.d(Tag,
					// "NetType : "+"TelephonyManager.NETWORK_TYPE_EVDO");
					signalLevel = getEvdoLevel(signalStrength.getEvdoDbm(),
							signalStrength.getEvdoSnr());
				}

				Log.d(Tag, "signalLevel : " + signalLevel);
				if (_3G_STATUS) {
					myHandler.sendEmptyMessage(_3G_Open);
				} else {
					myHandler.sendEmptyMessage(_3G_Close);
				}
			}
			// Log.d(Tag, "IsGsm : " + signalStrength.isGsm() +
			// "\nCDMA Dbm : " + signalStrength.getCdmaDbm() + "Dbm" +
			// "\nCDMA Ecio : " + signalStrength.getCdmaEcio() + "dB*10" +
			// "\nEvdo Dbm : " + signalStrength.getEvdoDbm() + "Dbm" +
			// "\nEvdo Ecio : " + signalStrength.getEvdoEcio() + "dB*10" +
			// "\nGsm SignalStrength : " + signalStrength.getGsmSignalStrength()
			// +
			// "\nGsm BitErrorRate : " + signalStrength.getGsmBitErrorRate());

			// mIcon3G.setImageLevel(Math.abs(signalStrength.getGsmSignalStrength()));
		}

		public void onServiceStateChanged(ServiceState serviceState) {
			super.onServiceStateChanged(serviceState);

			// Log.d(Tag,
			// "serviceState.getOperatorNumeric()"+serviceState.getOperatorNumeric());
			/*
			 * ServiceState.STATE_EMERGENCY_ONLY 仅限紧急呼叫
			 * ServiceState.STATE_IN_SERVICE 信号正常
			 * ServiceState.STATE_OUT_OF_SERVICE 不在服务区
			 * ServiceState.STATE_POWER_OFF 断电
			 */
			switch (serviceState.getState()) {
			case ServiceState.STATE_EMERGENCY_ONLY:
				// Log.d(Tag, "3G STATUS : STATE_EMERGENCY_ONLY");
				break;
			case ServiceState.STATE_IN_SERVICE:
				// Log.d(Tag, "3G STATUS : STATE_IN_SERVICE");
				_3G_STATUS = true;
				break;
			case ServiceState.STATE_OUT_OF_SERVICE:
				// Log.d(Tag, "3G STATUS : STATE_OUT_OF_SERVICE");
				_3G_STATUS = false;
				myHandler.sendEmptyMessage(_3G_Close);
				break;
			case ServiceState.STATE_POWER_OFF:
				// Log.d(Tag, "3G STATUS : STATE_POWER_OFF");
				break;
			default:
				break;
			}
		}
	}

	private int getEvdoLevel(int evdoDbm, int evdoSnr) {
		int levelEvdoDbm = 0;
		int levelEvdoSnr = 0;

		if (evdoDbm >= -65)
			levelEvdoDbm = 4;
		else if (evdoDbm >= -75)
			levelEvdoDbm = 3;
		else if (evdoDbm >= -90)
			levelEvdoDbm = 2;
		else if (evdoDbm >= -105)
			levelEvdoDbm = 1;
		else
			levelEvdoDbm = 0;

		if (evdoSnr >= 7)
			levelEvdoSnr = 4;
		else if (evdoSnr >= 6)
			levelEvdoSnr = 3;
		else if (evdoSnr >= 3)
			levelEvdoSnr = 2;
		else if (evdoSnr >= 1)
			levelEvdoSnr = 1;
		else
			levelEvdoSnr = 0;

		return (levelEvdoDbm < levelEvdoSnr) ? levelEvdoDbm : levelEvdoSnr;
	}

	private int getCdmaLevel(int cdmaDbm, int cdmaEcio) {
		int levelDbm = 0;
		int levelSnr = 0;

		if (cdmaDbm >= -75)
			levelDbm = 4;
		else if (cdmaDbm >= -85)
			levelDbm = 3;
		else if (cdmaDbm >= -95)
			levelDbm = 2;
		else if (cdmaDbm >= -10)
			levelDbm = 1;
		else
			levelDbm = 0;

		if (cdmaEcio >= 7)
			levelSnr = 4;
		else if (cdmaEcio >= 6)
			levelSnr = 3;
		else if (cdmaEcio >= 3)
			levelSnr = 2;
		else if (cdmaEcio >= 1)
			levelSnr = 1;
		else
			levelSnr = 0;

		return (levelDbm < levelSnr) ? levelDbm : levelSnr;
	}

	public BroadcastReceiver packageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)
					|| intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)
					|| intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
				myHandler.removeMessages(makeCelllayout);
				myHandler.sendEmptyMessage(makeCelllayout);
			}
		}
	};

}
