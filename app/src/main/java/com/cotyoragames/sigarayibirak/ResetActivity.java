package com.cotyoragames.sigarayibirak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.intellij.lang.annotations.Language;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResetActivity extends AppCompatActivity implements View.OnClickListener {

    InterstitialAd  mInterstitialAd;
    AdView mAdView;
    private Button simdi,sonra,kaydet;
    private Calendar birakma;
    DatePickerDialog.OnDateSetListener date;
    public static final String MyPREFERENCES = "MyPrefs",idKey="idKey",baslamakey = "baslamaKey";
    SharedPreferences sharedPreferences;
    private static final String PURCHASE_KEY = "PURCHASE_KEY";
    public boolean getPurchaseValueFromPref(){
        return getApplicationContext().getSharedPreferences(MyPREFERENCES, 0).getBoolean( PURCHASE_KEY,false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        simdi=findViewById(R.id.resetanswernow);
        sonra=findViewById(R.id.resetanswerlater);
        kaydet=findViewById(R.id.btnResetSave);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mAdView = findViewById(R.id.adViewReset);
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

                    if(sharedPreferences.getLong("rk",0)==0 || (Calendar.getInstance().getTimeInMillis()-sharedPreferences.getLong("rk",0)>=120000L)) {
                        mInterstitialAd.show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
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


        simdi.setOnClickListener(this);
        sonra.setOnClickListener(this);
        kaydet.setOnClickListener(this);
        birakma=Calendar.getInstance();
        date= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                birakma.set(year,month,dayOfMonth);
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.resetanswerlater)
        {
            DatePickerDialog picker= new DatePickerDialog(this,date,birakma.get(Calendar.YEAR),birakma.get(Calendar.MONTH),birakma.get(Calendar.DAY_OF_MONTH));
            picker.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
            picker.show();
        }
        else if(v.getId()==R.id.btnResetSave)
        {
            Map<String, Object> update = new HashMap<>();
            update.put("birakma", birakma.getTimeInMillis());

            if(birakma.getTimeInMillis()>Calendar.getInstance().getTimeInMillis())
            {
                update.put("bigdayShown",false);
            }
            else
            {
                update.put("bigdayShown",true);
            }
            FirebaseMessaging.getInstance().subscribeToTopic(DateFormat.format("ddMMyyyy",birakma).toString()+"-"+ (Locale.getDefault().getLanguage().equals(new Locale("tr").getLanguage()) ? "tr":"en"))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
            FirebaseFirestore.getInstance().collection("users").document(sharedPreferences.getString(idKey, "")).update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ResetActivity.this, "Bilgiler Başarıyla Eklendi", Toast.LENGTH_SHORT).show();
                }
            });
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(baslamakey,birakma.getTimeInMillis());
            editor.commit();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }



    }
}