package com.cotyoragames.sigarayibirak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.cotyoragames.sigarayibirak.MainActivity.MyPREFERENCES;
import static com.cotyoragames.sigarayibirak.MainActivity.idKey;

public class StatusActivity  extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    SharedPreferences sharedpreferences;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private Location loc;
    private static final int REQUEST_LOCATION = 0;
    ListView statusListview;
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    private static final String PURCHASE_KEY = "PURCHASE_KEY";
    public boolean getPurchaseValueFromPref(){
        return getApplicationContext().getSharedPreferences(MyPREFERENCES, 0).getBoolean( PURCHASE_KEY,false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        MobileAds.initialize(getApplicationContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        sharedpreferences=getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        mAdView = findViewById(R.id.adViewDuygu);

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


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.statusmap);
        mapFragment.getMapAsync(this);
        statusListview=findViewById(R.id.duygulistview);
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

        FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey, "")).collection("status").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                ArrayList<StatusClass> statusList = new ArrayList<>();

                for (int i = 0; i <  documentSnapshotList.size(); i++) {
                    DocumentSnapshot documentSnapshot = documentSnapshotList.get(i);
                    long s =  documentSnapshot.getLong("status");
                    StatusClass status=new StatusClass(documentSnapshot.getId(),((int)s));
                    statusList.add(status);
                }
                StatusAdapter statusAdapter = new StatusAdapter(statusList,getApplicationContext());
                statusListview.setAdapter(statusAdapter);
            }

            });


        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
                            loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapStyle(Map)
        // Add a marker in Sydney and move the camera


        final LatLng[] sydney = new LatLng[1];
        sydney[0]=new LatLng(0,0);
        FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey, "")).collection("status").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList= queryDocumentSnapshots.getDocuments();
                for(int i=0;i< documentSnapshotList.size();i++)
                {
                    DocumentSnapshot documentSnapshot= documentSnapshotList.get(i);
                    sydney[0] =new LatLng(documentSnapshot.getGeoPoint("konum").getLatitude(),documentSnapshot.getGeoPoint("konum").getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(sydney[0])
                            .title(String.valueOf(documentSnapshot.getLong("status"))));
                }
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney[0]));
                if(sydney[0].longitude!=0 && sydney[0].latitude!=0)
                {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney[0],5));
                }
                else if (loc!=null)
                {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(),loc.getLongitude()),5));
                }

            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(StatusActivity.this,MainActivity.class));
        finish();
    }
}
