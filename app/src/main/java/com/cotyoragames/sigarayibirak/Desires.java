package com.cotyoragames.sigarayibirak;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Desires extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_LOCATION = 0;
    static boolean showLocPerm = false;
    private Button desireBtn;
    private EditText desireDate;
    private SeekBar desireSeekBar;
    AdView mAdView;
    int desire;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private Location loc;
    public static final String MyPREFERENCES = "MyPrefs", latitudeKey = "latitudeKey", longtudeKey = "longtudeKey", idKey = "idKey";
    SharedPreferences sharedpreferences;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar date;
    private InterstitialAd mInterstitialAd;
    private static final String PURCHASE_KEY = "PURCHASE_KEY";
    public boolean getPurchaseValueFromPref(){
        return getApplicationContext().getSharedPreferences(MyPREFERENCES, 0).getBoolean( PURCHASE_KEY,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desires);
        desireBtn = findViewById(R.id.btnArzu);
        desireDate = findViewById(R.id.desireDate);
        desireSeekBar = findViewById(R.id.arzuseekBar);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mAdView = findViewById(R.id.adViewDesire);
        if(!getPurchaseValueFromPref())
        {

            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);
                }
                // Only implement methods you need.
            });

            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-3955653227838008/7457761968");
            AdRequest adRequest1 = new AdRequest.Builder().build();
            mInterstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
                @Override
                public void onAdLoaded() {

                    if(sharedpreferences.getLong("rk",0)==0 || (Calendar.getInstance().getTimeInMillis()-sharedpreferences.getLong("rk",0)>=120000L)) {
                        mInterstitialAd.show();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putLong("rk", Calendar.getInstance().getTimeInMillis());
                        editor.apply();
                        // Code to be executed when an ad finishes loading.
                        Log.i("Ads", "onAdLoaded");
                    }
                }

            });
            mInterstitialAd.loadAd(adRequest1);
        }
        else
        {
            mAdView.setVisibility(View.GONE);
        }




        date = Calendar.getInstance();



        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.set(year, month, dayOfMonth);
                desireDate.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
            }
        };


        desireSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                desire = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        desireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(Desires.this, dateSetListener, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });
        desireBtn.setOnClickListener(this);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loc = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION
            );
        } else {
            loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        }


    }

    @Override
    public void onClick(View v) {


        if (date != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", desire);
            updates.put("Tarih", date.getTime());
            if (loc != null)
                updates.put("konum", new GeoPoint(loc.getLatitude(), loc.getLongitude()));
            else {
                updates.put("konum", new GeoPoint(sharedpreferences.getFloat(latitudeKey, 0), sharedpreferences.getFloat(longtudeKey, 0)));
            }
            FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey, "")).collection("desires").add(updates).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(Desires.this, "Bilgiler Başarıyla Eklendi", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Tarih Bilgisi Boş Olamaz", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {
                    if(!showLocPerm) {
                        Toast.makeText(this, "Konum Servisleri Kullanılamıyor", Toast.LENGTH_SHORT).show();
                        LayoutInflater inflater = (LayoutInflater)
                                getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.location_popup, null);
                        int width = LinearLayout.LayoutParams.MATCH_PARENT;
                        int height = 400;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                        popupView.setBackgroundColor(getResources().getColor(R.color.white));

                        // show the popup window
                        // which view you pass in doesn't matter, it is only used for the window tolken
                        popupWindow.showAtLocation(findViewById(R.id.desireDate), Gravity.CENTER, 0, 0);
                        showLocPerm=true;
                        // dismiss the popup window when touched
                        popupView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                popupWindow.dismiss();
                                return true;
                            }
                        });

                    }
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Desires.this,MainActivity.class));
    }
}