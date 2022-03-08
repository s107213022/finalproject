package org.iBeaconapp.beaconreference;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beaconreference.R;
import org.iBeaconapp.getondata.adjust;
import org.iBeaconapp.getondata.update;
import org.json.JSONException;

import java.io.IOException;
import java.util.Collection;

public class Correction extends Activity {

    int Rssi_a = 0;
    int Rssi_b = 0;
    int Rssi_aj = 0;
    int A_count = 0;
    int B_count = 0;
    int Aj_count = 0;
    double Rssiaj = 0;
    boolean file;


    String id;
    String passuser;
    String accuser;
    String aj;
    String name;

    TextView result;
    TextView test;
    Button button;



    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    //private static String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction);
        result = findViewById(R.id.textView2);
        test = findViewById(R.id.textView3);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        name = bundle.getString("name");
        aj = bundle.getString("aj");
        file =bundle.getBoolean("file");
        button = findViewById(R.id.button);
        button.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                Intent myIntent = new Intent(Correction.this, RangingActivity.class);
                Bundle bundle = new Bundle();
                //bundle.putString("name", name);
                bundle.putString("name", name);
                String iid = String.valueOf(id);
                bundle.putString("id",iid);
                //String aj_s = String.valueOf(aj);
                bundle.putString("aj",aj);
                //bundle.putBoolean("file",isPermissionPassed);
                myIntent.putExtras(bundle);
                startActivity(myIntent);
            }
        });
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
                            if(A_count >= 5){
                                double AdoubleR = (double) Rssi_a / (double) A_count;
                                double Atest = AdoubleR - (double)beacon.getRssi();
                                if(Math.abs(Atest) <= 8){
                                    Rssi_a  = Rssi_a + beacon.getRssi();
                                    A_count = A_count + 1;
                                }
                            } else {
                                Rssi_a  = Rssi_a + beacon.getRssi();
                                A_count = A_count + 1;
                            }
                        }else if(beacon.getBluetoothAddress().equals("18:04:ED:3F:8B:27")){
                            if(B_count >= 5){
                                double BdoubleR = (double) Rssi_b / (double) B_count;
                                double Btest = BdoubleR - (double)beacon.getRssi();
                                if(Math.abs(Btest) <= 8){
                                    Rssi_b  = Rssi_b + beacon.getRssi();
                                    B_count = B_count + 1;
                                }
                            } else {
                                Rssi_b  = Rssi_b + beacon.getRssi();
                                B_count = B_count + 1;
                            }
                        } else if(beacon.getBluetoothAddress().equals("18:04:ED:3F:8B:0C")){
                            if(Aj_count >= 3){
                                double AjdoubleR = (double) Rssi_aj / (double) Aj_count;
                                double Ajtest = AjdoubleR - (double)beacon.getRssi();
                                if(Math.abs(Ajtest) <= 5){
                                    Rssi_aj  = Rssi_aj + beacon.getRssi();
                                    Aj_count = Aj_count + 1;
                                }
                            } else {
                                Rssi_aj  = Rssi_aj + beacon.getRssi();
                                Aj_count = Aj_count + 1;
                            }
                        }
                    }
                    logToDisplay("aj = " + aj );
/*
                    try{
                        test.setText(Rssi_aj);
                    }catch (Exception e){
                        test.setText(e.toString());
                    }

 */



                    if(Aj_count >= 5){
                        //A = A /((double)A_count-1.0);
                        //Rssia = (double)Rssi_a/((double)A_count);
                        //Rssib = (double)Rssi_b/((double)B_count);
                        Rssiaj = (double)Rssi_aj/((double)Aj_count);
                        //double R = Double.parseDouble(Rssia);
                        //double aj_d = Double.parseDouble(aj);
                        double ajaj = 59.0 / Math.abs(Rssiaj);
                        double aj_d = Math.round(ajaj*100.0)/100.0;

                        aj = String.valueOf(aj_d);

                        FileIO.writeFile(aj);

                        result.setText(aj);

                        button.setVisibility(View.VISIBLE);

                        A_count = 0;
                        Rssi_a = 0;
                        Rssi_b = 0;
                        B_count = 0;
                        Aj_count = 0;
                        Rssi_aj = 0;
                        logToDisplay("aj = " + aj );

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