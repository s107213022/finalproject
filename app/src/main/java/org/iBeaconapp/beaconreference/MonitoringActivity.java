package org.iBeaconapp.beaconreference;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beaconreference.R;
import org.iBeaconapp.getondata.Getdata;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author dyoung
 * @author Matt Tyler
 */
public class MonitoringActivity extends Activity implements MonitorNotifier {
	protected static final String TAG = "MonitoringActivity";
	private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
	private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
	private boolean isPermissionPassed = false;


	String data = null;
	TextView textView; // 把視圖的元件宣告成全域變數
	EditText account;
	EditText password;
	TextView test;
	Button button;
	String result; // 儲存資料用的字串
	String name; // 使用者名稱
	String B = null;
	String P;
	String A;
	int inclass;
	String ajstr;
	int id;


	boolean login = false;
	//DBhelper DB;

	private Runnable mutiThread = new Runnable() {
		public void run() {
			try {
				// 把edittext數值記錄下來

				String inputAccount = account.getText().toString();
				String inputPassword = password.getText().toString();


				JSONArray B = new JSONArray();
				B = Getdata.get(inputAccount,inputPassword);
				JSONObject jsonObject = B.getJSONObject(0);
				if(jsonObject.getInt("id")!=-1){
					name = jsonObject.getString("name");
					P = jsonObject.getString("password");
					A = jsonObject.getString("account");
					id = jsonObject.getInt("id");
					inclass = jsonObject.getInt("status");
					result = "登入成功";
					login = true;
				}else{
					//Toast.makeText(this,"登入失敗，請重新輸入",Toast.LENGTH_LONG).show();
					result = "登入失敗，請重新輸入";
				}
				//result = B;
                /*for(int i = 0;i<B.length();i++){
                    JSONObject jsonObject = B.getJSONObject(i);
                    if(jsonObject.getString("identity").compareTo("1") == 0){
                        result = "歡迎老師";
                    }else if(jsonObject.getString("identity").compareTo("0") == 0){
                        result = "登入成功";
                    }
                }
                /*
                String realaccount = null;
                String realpassword = null;
                int id = 0;
                String ans = null;
                int count = 0;
                for(int i = 0;i < B.length();i++){
                    JSONObject jsonObject = B.getJSONObject(i);
                    realaccount = jsonObject.getString("account");
                    if( realaccount.compareTo(inputAccount) == 0){
                        realpassword = jsonObject.getString("password");
                        if(realpassword.compareTo(inputPassword) == 0){
                            name = jsonObject.getString("name");
                            if(jsonObject.getInt("identity") == 0){
                                ans = "登入成功";
                                count = count + 1;
                            }else{
                                ans = "歡迎老師";
                                count = count + 1;
                            }

                        }
                    }
                    else if(count == 0 && i == B.length()-1){
                        ans = "登入失敗";
                    }
                    //box += line  +"\n";
                    // 每當讀取出一列，就加到存放字串後面
                }

                // lineNObject jsonObject = new JSONObject(builder.toString());
                //String a = jsonObject.getString("id");
                account.setText(null);
                password.setText(null);
                result = ans; // 把存放用字串放到全域變數

                 */

			} catch (Exception e) {
				result = e.toString();;
				// 讀取輸入串流並存到字串的部分
				// 取得資料後想用不同的格式
				// 例如 Json 等等，都是在這一段做處理

			} /*catch (Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
            }*/
			// 當這個執行緒完全跑完後執行
			runOnUiThread(new Runnable() {
				public void run() {
					textView.setText(result); // 更改顯示文字
					password.setText("");
					account.setText("");
					account.setFocusable(true);
					password.setFocusable(true);
					if(login) {
						if(data.equals("1")){
							dialog();
						} else {
							//textView.setText("閉嘴");
							Intent myIntent = new Intent(MonitoringActivity.this, RangingActivity.class);
							Bundle bundle = new Bundle();
							//aj = Double.parseDouble(ajstr);
							//bundle.putString("name", name);
							bundle.putString("name",name);
							String iid = String.valueOf(id);
							bundle.putString("id",iid);
							//String aj_s = String.valueOf(aj);
							bundle.putString("aj",data);
							myIntent.putExtras(bundle);
							startActivity(myIntent);
						}

					}else{
						account.setFocusable(true);
						password.setFocusable(true);
						Toast.makeText(getApplicationContext(),"登入失敗，請重新輸入",Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	};


	/*
	else if(result.compareTo("歡迎老師") == 0){
		Intent intent = new Intent(MonitoringActivity.this,Main_Teacher.class);
		Bundle bundle = new Bundle();
		bundle.putString("name", name);
		bundle.putString("account", A);
		bundle.putString("password", P);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitoring);
		verifyBluetooth();
		requestPermissions();
		getPermission();
		BeaconManager.getInstanceForApplication(this).addMonitorNotifier(this);

		//DB = new DBhelper(this);

		//Boolean test = DB.checkpersion("1","2");

		// No need to start monitoring here because we already did it in
		// BeaconReferenceApplication.onCreate
		// check if we are currently inside or outside of that region to update the display
		button = findViewById(R.id.button);
		textView = findViewById(R.id.textView);
		test = findViewById(R.id.textView2);
		account = (EditText) findViewById(R.id.account);
		password = (EditText) findViewById(R.id.password);

		if (!isPermissionPassed) {
			Toast.makeText(this, "AAA！！", Toast.LENGTH_SHORT).show();
			getPermission();
		} else {
			data = FileIO.readFile();
		}

		//writeFile();


		textView.setText(data);
		// ajstr 當資料庫抓下來的
		//ajstr = ajarray[0];


		button.setOnClickListener(new View.OnClickListener() {
			@Override
			// 按鈕事件
			public void onClick(View view) {
				// 按下之後會執行的程式碼
				// 宣告執行緒
				account.setFocusable(false);
				password.setFocusable(false);
				Thread thread = new Thread(mutiThread);
				thread.start(); // 開始執行
			}
		});
	}

	private void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("登入成功");  //設置標題
		builder.setMessage("你還沒校正，利用校正能讓定位變得準確，請問需要嗎?"); //提示訊息

		//確定 取消 一般 這三種按鈕就看你怎麼發揮你想置入的功能囉
		builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				Intent myIntent = new Intent(MonitoringActivity.this, Correction.class);
				Bundle bundle = new Bundle();
				//bundle.putString("name", name);
				bundle.putString("name",name);
				String iid = String.valueOf(id);
				bundle.putString("id",iid);
				//String aj_s = String.valueOf(aj);
				bundle.putString("aj",data);
				bundle.putBoolean("file",isPermissionPassed);
				myIntent.putExtras(bundle);
				startActivity(myIntent);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				//textView.setText("閉嘴");
				Intent myIntent = new Intent(MonitoringActivity.this, RangingActivity.class);
				Bundle bundle = new Bundle();
				//bundle.putString("name", name);
				bundle.putString("name",name);
				String iid = String.valueOf(id);
				bundle.putString("id",iid);
				bundle.putString("aj",data);
				myIntent.putExtras(bundle);
				startActivity(myIntent);
				//finish();
			}
		});
		builder.create().show();
	}

	@Override
	public void didEnterRegion(Region region) { logToDisplay("didEnterRegion called"); }
	@Override
	public void didExitRegion(Region region) {
		logToDisplay("didExitRegion called");
	}
	@Override
	public void didDetermineStateForRegion(int state, Region region) {
		logToDisplay("didDetermineStateForRegion called with state: " + (state == 1 ? "INSIDE ("+state+")" : "OUTSIDE ("+state+")"));
	}


	private void getPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
				ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
					this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
		} else {
			isPermissionPassed = true;
		}
	}

	private void requestPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
					== PackageManager.PERMISSION_GRANTED) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
					if (this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
							!= PackageManager.PERMISSION_GRANTED) {
						if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
							final AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("This app needs background location access");
							builder.setMessage("Please grant location access so this app can detect beacons in the background.");
							builder.setPositiveButton(android.R.string.ok, null);
							builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

								@TargetApi(23)
								@Override
								public void onDismiss(DialogInterface dialog) {
									requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
											PERMISSION_REQUEST_BACKGROUND_LOCATION);
								}

							});
							builder.show();
						}
						else {
							final AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("Functionality limited");
							builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
							builder.setPositiveButton(android.R.string.ok, null);
							builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

								@Override
								public void onDismiss(DialogInterface dialog) {
								}

							});
							builder.show();
						}
					}
				}
			} else {
				if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
					requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
									Manifest.permission.ACCESS_BACKGROUND_LOCATION},
							PERMISSION_REQUEST_FINE_LOCATION);
				}
				else {
					final AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Functionality limited");
					builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
						}

					});
					builder.show();
				}

			}
		}
	}



	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case PERMISSION_REQUEST_FINE_LOCATION: {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d(TAG, "fine location permission granted");
				} else {
					final AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Functionality limited");
					builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
						}

					});
					builder.show();
				}
				return;
			}
			case PERMISSION_REQUEST_BACKGROUND_LOCATION: {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d(TAG, "background location permission granted");
				} else {
					final AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Functionality limited");
					builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons when in the background.");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
						}

					});
					builder.show();
				}
				return;
			}
			case 100 :
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					/**如果用戶同意*/
					isPermissionPassed = true;
				} else {
					/**如果用戶不同意*/
					if (ActivityCompat.shouldShowRequestPermissionRationale(this
							, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
						Toast.makeText(this, "你搞毛啊！", Toast.LENGTH_SHORT).show();
						getPermission();
					}
				}
		}
	}

	public void onRangingClicked(View view) {
		Intent myIntent = new Intent(this, RangingActivity.class);
		this.startActivity(myIntent);
	}
	/*
	public void onEnableClicked(View view) {
		// This is a toggle.  Each time we tap it, we start or stop
		Button button = (Button) findViewById(R.id.enableButton);
		if (BeaconManager.getInstanceForApplication(this).getMonitoredRegions().size() > 0) {
			BeaconManager.getInstanceForApplication(this).stopMonitoring(BeaconReferenceApplication.wildcardRegion);
			button.setText("Enable Monitoring");
		}
		else {
			BeaconManager.getInstanceForApplication(this).startMonitoring(BeaconReferenceApplication.wildcardRegion);
			button.setText("Disable Monitoring");
		}
	}


	 */
	private void verifyBluetooth() {
		try {
			if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bluetooth not enabled");
				builder.setMessage("Please enable bluetooth in settings and restart this application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finishAffinity();
					}
				});
				builder.show();
			}
		}
		catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bluetooth LE not available");
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					finishAffinity();
				}

			});
			builder.show();

		}

	}
	//private String cumulativeLog = "";
	private void logToDisplay(final String line) {
		//cumulativeLog += line+"\n";
    	runOnUiThread(new Runnable() {
    	    public void run() {
				Log.i("monitor",line);
    	    	//EditText editText = (EditText)MonitoringActivity.this
    			//		.findViewById(R.id.monitoringText);
       	    	//editText.setText(cumulativeLog);
    	    }
    	});
    }
}
