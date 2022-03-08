package org.iBeaconapp.beaconreference;

import java.io.IOException;
import java.util.Collection;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beaconreference.R;
import org.iBeaconapp.getondata.RSSItoDIST;
import org.iBeaconapp.getondata.update;
import org.json.JSONException;

import static java.lang.Math.abs;

public class RangingActivity extends Activity {
    int Rssi_a = 0;
    int Rssi_b = 0;
    int A_count = 0;
    int B_count = 0;
    double Rssia = 0;
    double Rssib = 0;

    String welcome = null;

    Button button;


    String id;
    String passuser;
    String accuser;
    String aj;
    String name;

    TextView title;
    TextView test;
    TextView wel;

    int dot = 0;


    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    //private static String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        aj = bundle.getString("aj");
        name = bundle.getString("name");

        test = findViewById(R.id.test);
        title = findViewById(R.id.textView);
        wel = findViewById(R.id.textView3);
        button = findViewById(R.id.button);
        button.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                Intent myIntent = new Intent(RangingActivity.this, main_function.class);
                //Bundle bundle = new Bundle();
                //bundle.putString("name", name);
               // myIntent.putExtras(bundle);
                startActivity(myIntent);
            }
        });


        /*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                Intent myIntent = new Intent(RangingActivity.this, Main01.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                String iid = String.valueOf(id);
                bundle.putString("id",iid);
                bundle.putString("aj",aj);
                myIntent.putExtras(bundle);
                startActivity(myIntent);
            }
        });

         */
    }

    @Override
    protected void onResume() {
        super.onResume();
        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  "+beacons.size());
                    //.Beacon firstBeacon;
                    for (Beacon beacon : beacons) {
                        if(beacon.getBluetoothAddress().equals("18:04:ED:3F:8D:07")) {
                            /*
                            if(A_count >= 10){
                                double AdoubleR = (double) Rssi_a / (double) A_count;
                                double Atest = AdoubleR - (double)beacon.getRssi();
                                if(Math.abs(Atest) <= 5){
                                    Rssi_a  = Rssi_a + beacon.getRssi();
                                    A_count = A_count + 1;
                                }
                            } else if(A_count >= 5) {
                                Rssi_a  = Rssi_a + beacon.getRssi();
                                A_count = A_count + 1;
                            }else{
                                A_count = A_count + 1;
                            }
                             */
                            Rssi_a  = Rssi_a + beacon.getRssi();
                            A_count = A_count + 1;
                        }else if(beacon.getBluetoothAddress().equals("18:04:ED:3F:8B:27")){
                            /*
                            if(B_count >= 10){
                                Rssi_b  = Rssi_b + beacon.getRssi();
                                B_count = B_count + 1;
                                double BdoubleR = (double) Rssi_b / (double) B_count;
                                double Btest = BdoubleR - (double)beacon.getRssi();
                                if(Math.abs(Btest) <= 5){
                                    Rssi_b  = Rssi_b + beacon.getRssi();
                                    B_count = B_count + 1;
                                }
                            } else if(B_count >= 5){
                                Rssi_b  = Rssi_b + beacon.getRssi();
                                B_count = B_count + 1;
                            }else {
                                B_count = B_count + 1;
                            }

                             */
                            Rssi_b  = Rssi_b + beacon.getRssi();
                            B_count = B_count + 1;
                        }
                        //logToDisplay(Rss);
                        //logToDisplay("The beacon " + beacon.toString() + " is about " + beacon.getDistance() + " meters away.");
                    }
                    //logToDisplay(welcome);
                    if(dot == 0){
                        title.setText("定位中，請稍後");
                        dot = dot + 1;
                    }else if(dot == 1){
                        title.setText("定位中，請稍後.");
                        dot = dot + 1;
                    }else if(dot == 2){
                        title.setText("定位中，請稍後..");
                        dot = dot + 1;
                    } else {
                        title.setText("定位中，請稍後...");
                        dot = 0;
                    }
                    if(A_count >= 20 || B_count >= 20){
                        //A = A /((double)A_count-1.0);
                        Rssia = (double)Rssi_a/((double)A_count);
                        Rssib = (double)Rssi_b/((double)B_count);
                        //double R = Double.parseDouble(Rssia);

                        double aj_d = Double.parseDouble(aj);
                        //logToDisplay(aj);
                        Rssia = Rssia * aj_d;
                        Rssib = Rssib * aj_d;
                        String RA = String.valueOf(Rssia);
                        String RB = String.valueOf(Rssib);
                        String teststr = aj +","+ RA +"," + RB;
                        test.setText(teststr);
                        logToDisplay("Rssia = " + RA + " Rssib " + RB);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    welcome = update.up(accuser,passuser,RA , RB);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        /*
                                        if(welcome.equals("定位完成")){
                                            // 轉換畫面
                                        }

                                         */
                                        wel.setText(welcome);
                                        button.setVisibility(View.VISIBLE);
                                        //Intent myIntent = new Intent(RangingActivity.this, function.class);
                                        //startActivity(myIntent);
                                    }
                                });


                            }
                        }).start();
                        A_count = 0;
                        Rssi_a = 0;
                        Rssi_b = 0;
                        B_count = 0;
                        logToDisplay("Rssia = " + RA + " Rssib " + RB);
                        //logToDisplay("Adist=" + Adist + " Bdist " + Bdist );

                        //logToDisplay(welcome);
                        /*
                        Thread location = new Thread(Lthread);
                        location.start();

                        try {
                            location.join();
                        }catch (InterruptedException e){
                            logToDisplay("執行中斷");
                        }

                        logToDisplay("Adist=" + Adist + " Bdist=" + Bdist +"{"+ welcome +"}");

                         */




                        //logToDisplay("A " + A +"RSSI_a"+ Rssia + " B " + B + "RSSI_b" + Rssib);
                        //logToDisplay("A " + A + " B " + B);
                    }
                }
            }

        };
        beaconManager.addRangeNotifier(rangeNotifier);
        beaconManager.startRangingBeacons(BeaconReferenceApplication.wildcardRegion);
    }





    /*
    private String rssitodist(double rssi){
        double plusR = (Math.abs(rssi) - (double)59)/15;
        double d =   Math.pow((double) 10,plusR);
        double dist = Math.round(d * 100.0) / 100.0;
        String result = String.valueOf(dist);
        return result;
    }
    

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }
    }

     */



    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.stopRangingBeacons(BeaconReferenceApplication.wildcardRegion);
        beaconManager.removeAllRangeNotifiers();
    }

    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                Log.i("Correction",line);
                //EditText editText = (EditText)MonitoringActivity.this
                //		.findViewById(R.id.monitoringText);
                //editText.setText(cumulativeLog);
            }
        });
    }
}
