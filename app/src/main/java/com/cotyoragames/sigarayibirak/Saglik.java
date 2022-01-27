package com.cotyoragames.sigarayibirak;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.DescriptorProtos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.cotyoragames.sigarayibirak.MainActivity.idKey;


public class Saglik extends Fragment implements View.OnClickListener {

    private static final int REQUEST_LOCATION = 0;
    private static final String PURCHASE_KEY = "PURCHASE_KEY";
    public static final String MyPREFERENCES = "MyPrefs", latitudeKey = "latitudeKey", longtudeKey = "longtudeKey", idKey = "idKey", gundeKey = "gundeKey", pakettekiKey = "pakettekiKey", paketfiyatiKey = "paketfiyatiKey", tasarrufKey = "tasarrufKey";
    SharedPreferences sharedpreferences;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private Location loc;
    ImageButton sahane, iyi, orta, kotu, berbat, gunluk;
    TextView tahminitasarruftext, gunluktext, ayliktext, yilliktext, omurluktext, kurtarilanomur;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DocumentReference docRef;
    static boolean showLocPerm = false;
    LineChart lineChart,linechartduygu;
    AdView mAdView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
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


        docRef = FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey, "")).collection("status").document(dateFormat.format(Calendar.getInstance().getTime().getTime()));


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saglik, container, false);
    }
    public boolean getPurchaseValueFromPref(){
        return requireContext().getSharedPreferences(MyPREFERENCES, 0).getBoolean( PURCHASE_KEY,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().findViewById(R.id.cardviewdurum).setVisibility(View.GONE);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = view.findViewById(R.id.adViewSaglik);
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
        }
        else
        {
            mAdView.setVisibility(View.GONE);
        }
        tahminitasarruftext = view.findViewById(R.id.tahminitasarruf);
        gunluktext = view.findViewById(R.id.gunluktasarruf);
        ayliktext = view.findViewById(R.id.ayliktasarruf);
        yilliktext = view.findViewById(R.id.yilliktasarruf);
        omurluktext = view.findViewById(R.id.omurboyutasarruf);
        kurtarilanomur = view.findViewById(R.id.kurtarilanomurtext);
        lineChart = view.findViewById(R.id.linechart);
        linechartduygu=view.findViewById(R.id.linechartduygu);

        lineChart.setOnClickListener(this);

        linechartduygu.setOnClickListener(this);
         FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey, "")).collection("desires").orderBy("Tarih", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                ArrayList<Entry> barEntriesArrayList = new ArrayList<>();
                ArrayList<String> lableName = new ArrayList<>();
                for (int i = 0; i < (Math.min(documentSnapshotList.size(), 10)); i++) {
                    DocumentSnapshot documentSnapshot = documentSnapshotList.get(i);
                    Calendar month = Calendar.getInstance();
                    month.setTimeInMillis(documentSnapshot.getTimestamp("Tarih").getSeconds()*1000);
                    long sales = (long) documentSnapshot.get("status");
                    barEntriesArrayList.add(new BarEntry(i, sales));
                    lableName.add(month.get(Calendar.YEAR)+"/"+(month.get(Calendar.MONTH)+1)+"/"+month.get(Calendar.DAY_OF_MONTH));
                }


                Description description = new Description();
                description.setText("");
                lineChart.setDescription(description);
                lineChart.setPinchZoom(false);
                lineChart.setDoubleTapToZoomEnabled(false);

                LineDataSet lineDataSet = new LineDataSet(barEntriesArrayList, requireView().getContext().getString(R.string.arzular));
                lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                lineDataSet.setCircleColors(ColorTemplate.MATERIAL_COLORS);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(5f);
                LineData lineData = new LineData(lineDataSet);
                if(barEntriesArrayList.size()>0)
                {

                    lineChart.setData(lineData);
                    view.findViewById(R.id.cardGunluk).setOnClickListener(null);
                }
                else
                {
                    view.findViewById(R.id.cardGunluk).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openDaily();
                        }
                    });

                }

                YAxis yAxis = lineChart.getAxis(YAxis.AxisDependency.LEFT);
                yAxis.setGranularity(1f);

                lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) Math.floor(value));
                    }
                });
                lineChart.getAxisLeft().setAxisMinimum(0f);
                lineChart.getAxisLeft().setAxisMaximum(5.5f);
                lineChart.getAxisLeft().setLabelCount(5);
                lineChart.getAxisRight().setEnabled(false);
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(lableName));
                xAxis.setDrawGridLines(true);

                xAxis.setDrawAxisLine(true);
                xAxis.setGranularity(2f);
                xAxis.setAvoidFirstLastClipping(true);
                xAxis.setLabelRotationAngle(0);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.animateY(2000);
                lineChart.setBackgroundColor(Color.parseColor("#AA028af2"));
                xAxis.setGridColor(Color.WHITE);
                yAxis.setGridColor(Color.WHITE);
                xAxis.setTextColor(Color.WHITE);
                yAxis.setTextColor(Color.WHITE);


            }
         });

        duyguChartDraw();


        try {
            kurtarilanomur.setText(String.valueOf(MainActivity.kurtarilanomur));
        } catch (Exception ignored) {

        }

        if (MainActivity.status) {
            view.findViewById(R.id.cardviewdurum).setVisibility(View.VISIBLE);
            view.findViewById(R.id.constraindurum).setVisibility(View.VISIBLE);
            view.findViewById(R.id.duyguconstraint).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.cardviewdurum).setVisibility(View.VISIBLE);
            view.findViewById(R.id.constraindurum).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.duyguconstraint).setVisibility(View.VISIBLE);

        }


        sahane = view.findViewById(R.id.imageGuzel);
        sahane.setOnClickListener(this);
        iyi = view.findViewById(R.id.imageIyı);
        iyi.setOnClickListener(this);
        orta = view.findViewById(R.id.imageOrta);
        orta.setOnClickListener(this);
        kotu = view.findViewById(R.id.imageKotu);
        kotu.setOnClickListener(this);
        berbat = view.findViewById(R.id.imageBerbat);
        berbat.setOnClickListener(this);

        if (sharedpreferences.getInt(gundeKey, 0) != 0) {
            tahminitasarruftext.setText(getString(R.string.currency)+String.valueOf(sharedpreferences.getFloat(tasarrufKey, 0)));
            int gundeicilen = sharedpreferences.getInt(gundeKey, 0);
            int paketteki = 20;
            float paketfiyati = sharedpreferences.getFloat(paketfiyatiKey, 0);
            float gunluk = 0, aylik = 0, yillik = 0, omurluk = 0;
            gunluk = ((float)gundeicilen / paketteki) *  paketfiyati;
            aylik = gunluk * 30;
            yillik = gunluk * 365;
            omurluk = yillik * 40;
            gunluktext.setText(String.valueOf(gunluk));
            ayliktext.setText(String.valueOf(aylik));
            yilliktext.setText(String.valueOf(yillik));
            omurluktext.setText(String.valueOf(omurluk));
        }
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
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

    public void duyguChartDraw()
    {
        FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey, "")).collection("status").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                ArrayList<Entry> barEntriesArrayList = new ArrayList<>();
                ArrayList<String> lableName = new ArrayList<>();
                for (int i = 0; i < (Math.min(documentSnapshotList.size(), 10)); i++) {
                    DocumentSnapshot documentSnapshot = documentSnapshotList.get(i);
                    String month = (String) documentSnapshot.getId();
                    long sales = (long) documentSnapshot.get("status");
                    barEntriesArrayList.add(new BarEntry(i, sales));
                    lableName.add(month);
                }


                Description description = new Description();
                description.setText("");
                linechartduygu.setDescription(description);
                linechartduygu.setPinchZoom(false);
                linechartduygu.setDoubleTapToZoomEnabled(false);


                LineDataSet lineDataSet = new LineDataSet(barEntriesArrayList, requireView().getContext().getString(R.string.duygular));
                lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                lineDataSet.setCircleColors(ColorTemplate.MATERIAL_COLORS);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(5f);
                LineData lineData = new LineData(lineDataSet);
                if (barEntriesArrayList.size() > 0) {
                    linechartduygu.setData(lineData);
                }

                YAxis yAxis = linechartduygu.getAxis(YAxis.AxisDependency.LEFT);
                yAxis.setGranularity(1f);

                linechartduygu.getAxisLeft().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) Math.floor(value));
                    }
                });
                linechartduygu.getAxisLeft().setAxisMinimum(0f);
                linechartduygu.getAxisLeft().setAxisMaximum(5.5f);
                linechartduygu.getAxisLeft().setLabelCount(5);
                linechartduygu.getAxisRight().setEnabled(false);
                XAxis xAxis = linechartduygu.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(lableName));
                xAxis.setDrawGridLines(true);

                xAxis.setDrawAxisLine(true);
                xAxis.setGranularity(0.5f);
                xAxis.setLabelCount(lableName.size());
                xAxis.setAvoidFirstLastClipping(true);
                xAxis.setLabelRotationAngle(0);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                linechartduygu.animateY(2000);
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageGuzel:
                checkDocument(5);
                MainActivity.status = false;
                getView().findViewById(R.id.constraindurum).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.duyguconstraint).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.cardviewdurum).setVisibility(View.VISIBLE);
                duyguChartDraw();
                break;
            case R.id.imageIyı:
                checkDocument(4);
                MainActivity.status = false;
                getView().findViewById(R.id.constraindurum).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.duyguconstraint).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.cardviewdurum).setVisibility(View.VISIBLE);
                duyguChartDraw();
                break;
            case R.id.imageOrta:
                checkDocument(3);
                MainActivity.status = false;
                getView().findViewById(R.id.constraindurum).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.duyguconstraint).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.cardviewdurum).setVisibility(View.VISIBLE);
                duyguChartDraw();
                break;
            case R.id.imageKotu:
                checkDocument(2);
                MainActivity.status = false;
                Intent intent = new Intent(getContext(), MakaleActivity.class);
                startActivity(intent);
                break;
            case R.id.imageBerbat:
                checkDocument(1);
                MainActivity.status = false;
                Intent intent1 = new Intent(getContext(), MakaleActivity.class);
                startActivity(intent1);
                break;
            case R.id.linechart:
                openDaily();
                break;
            case  R.id.linechartduygu:
                openDuygu();
                break;


        }

    }

    private void openDaily() {
        Intent intent = new Intent(getContext(), ArzularActivity.class);
        startActivity(intent);
        //getActivity().finish();
    }
    private void openDuygu() {
        Intent intent = new Intent(getContext(), StatusActivity.class);
        startActivity(intent);
        //getActivity().finish();
    }

    private void checkDocument(final int status) {
        getView().findViewById(R.id.cardviewdurum).setVisibility(View.GONE);
        if (MainActivity.status) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", status);
            if (loc != null)
                updates.put("konum", new GeoPoint(loc.getLatitude(), loc.getLongitude()));
            else {
                updates.put("konum", new GeoPoint(sharedpreferences.getFloat(latitudeKey, 0), sharedpreferences.getFloat(longtudeKey, 0)));
            }
            FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey, "")).collection("status").document(dateFormat.format(Calendar.getInstance().getTime())).set(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                } catch (SecurityException ignored) {

                }

                // Permission is granted. Continue the action or workflow
                // in your app.
            } else {
                if (!showLocPerm) {
                    Toast.makeText(getContext(), "Konum Servisleri Kullanılamıyor", Toast.LENGTH_SHORT).show();
                    LayoutInflater inflater = (LayoutInflater)
                            getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.location_popup, null);
                    int width = LinearLayout.LayoutParams.MATCH_PARENT;
                    int height = 400;
                    boolean focusable = true; // lets taps outside the popup also dismiss it
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                    popupView.setBackgroundColor(getResources().getColor(R.color.white));

                    // show the popup window
                    // which view you pass in doesn't matter, it is only used for the window tolken
                    final View parent = getView();
                    parent.post(new Runnable() {
                        @Override
                        public void run() {
                            popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                        }
                    });

                    showLocPerm = true;
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
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }
}