package com.cotyoragames.sigarayibirak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.cotyoragames.Basarim;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    BottomNavigationView bottomNavigation;
    Fragment fragment;
    DocumentReference docRef;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final String PURCHASE_KEY = "PURCHASE_KEY";
    public static final String MyPREFERENCES = "MyPrefs", idKey="idKey", gundeKey="gundeKey",targetKey="targetKey", baslamaKey="baslamaKey";
    SharedPreferences sharedpreferences;
    public static boolean status=false,target=false;
    public static ArrayList<Basarim> basarimlar;
    public static int kurtarilanomur;
    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences=getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        if(sharedpreferences.getLong(baslamaKey,0)==0)
        {
            Intent intent = new Intent(this,ResetActivity.class);
            startActivity(intent);
            finish();
        }
        if(!getPurchaseValueFromPref())
        {
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



        frameLayout=findViewById(R.id.framelayout);
        bottomNavigation= findViewById(R.id.bottomNavigationView);
        long baslama=sharedpreferences.getLong(baslamaKey,0);
        long now=Calendar.getInstance().getTimeInMillis();
        basarimlar=new ArrayList<>();
        Basarim biraktin=new Basarim("Genel",getString(R.string.biraktin),getString(R.string.tebrikler_biraktin), baslama> now ? (int) (((float)now/baslama) * 100) :100, baslama <= Calendar.getInstance().getTimeInMillis());
        Basarim kardiyo1=new Basarim("Kardiyovasküler",getString(R.string.sigarasiz_1,"1",getString(R.string.saat)),getString(R.string.nabiz), now > baslama ? ProgressHesapla((now-baslama)*100,3600000L):0, ProgressHesapla((now - baslama) * 100, 3600000L) == 100);
        Basarim kardiyo2=new Basarim("Kardiyovasküler",getString(R.string.sigarasiz_1,"18",getString(R.string.saat)),getString(R.string.parmak_uc), now > baslama ? ProgressHesapla((now-baslama)*100,64800000L):0, ProgressHesapla((now - baslama) * 100, 64800000L) == 100);
        Basarim kardiyo3=new Basarim("Kardiyovasküler",getString(R.string.sigarasiz_1,"6",getString(R.string.gun)),getString(R.string.kandegerleri), now > baslama ? ProgressHesapla((now-baslama)*100,518400000L):0, ProgressHesapla((now - baslama) * 100, 518400000L) == 100);
        Basarim kardiyo4=new Basarim("Kardiyovasküler",getString(R.string.sigarasiz_1,"3",getString(R.string.hafta)),getString(R.string.kalpkrizi), now > baslama ? ProgressHesapla((now-baslama)*100,1814400000L):0, ProgressHesapla((now - baslama) * 100, 1814400000L) == 100);
        Basarim kardiyo5=new Basarim("Kardiyovasküler",getString(R.string.sigarasiz_1,"2",getString(R.string.ay)),getString(R.string.kosmak), now > baslama ? ProgressHesapla((now-baslama)*100,5184000000L):0, ProgressHesapla((now - baslama) * 100, 5184000000L) == 100);
        Basarim beyin1=new Basarim("Beyin",getString(R.string.sigarasiz_1,"1",getString(R.string.hafta)),getString(R.string.gunicinde), now > baslama ? ProgressHesapla((now-baslama)*100,604800000L):0,ProgressHesapla((now-baslama)*100,604800000L)==100);
        Basarim beyin2=new Basarim("Beyin",getString(R.string.sigarasiz_1,"8",getString(R.string.saat)),getString(R.string.nikotintoleransi),now > baslama ? ProgressHesapla((now-baslama)*100,28800000L):0, ProgressHesapla((now - baslama) * 100, 28800000L) == 100);
        Basarim beyin3=new Basarim("Beyin",getString(R.string.sigarasiz_1,"48",getString(R.string.saat)),getString(R.string.tatvekoku),now > baslama ? ProgressHesapla((now-baslama)*100,172800000L):0, ProgressHesapla((now - baslama) * 100, 172800000L) == 100);
        Basarim beyin4=new Basarim("Beyin",getString(R.string.sigarasiz_1,"72",getString(R.string.saat)),getString(R.string.butunvucut),now > baslama ? ProgressHesapla((now-baslama)*100,259200000L):0, ProgressHesapla((now - baslama) * 100, 259200000L) == 100);
        Basarim beyin5=new Basarim("Beyin",getString(R.string.sigarasiz_1,"6",getString(R.string.hafta)),getString(R.string.sinirlenmeniz),now > baslama ? ProgressHesapla((now-baslama)*100,1814400000L):0, ProgressHesapla((now - baslama) * 100, 1814400000L) == 100);
        Basarim beyin6=new Basarim("Beyin",getString(R.string.sigarasiz_1,"6",getString(R.string.ay)),getString(R.string.sigaraictiginiz),now > baslama ? ProgressHesapla((now-baslama)*100,2419200000L*6):0, ProgressHesapla((now - baslama) * 100, 2419200000L*6) == 100);
        Basarim beyin7=new Basarim("Beyin",getString(R.string.sigarasiz_1,"24",getString(R.string.saat)),getString(R.string.sigarayibirakmak),now > baslama ? ProgressHesapla((now-baslama)*100,86400000L):0,ProgressHesapla((now-baslama)*100,86400000L)==100);
        Basarim ciger1=new Basarim("Ciğer",getString(R.string.sigarasiz_1,"12",getString(R.string.saat)),getString(R.string.oksijen),now > baslama ? ProgressHesapla((now-baslama)*100,43200000L):0,ProgressHesapla((now-baslama)*100,43200000L)==100);
        Basarim ciger2=new Basarim("Ciğer",getString(R.string.sigarasiz_1,"4",getString(R.string.gun)),getString(R.string.dahaenerjik),now > baslama ? ProgressHesapla((now-baslama)*100,345600000L):0,ProgressHesapla((now-baslama)*100,345600000L)==100);
        Basarim ciger3=new Basarim("Ciğer",getString(R.string.sigarasiz_1,"1",getString(R.string.ay)),getString(R.string.akciger), now > baslama ? ProgressHesapla((now-baslama)*100,2419200000L):0,ProgressHesapla((now-baslama)*100,2419200000L)==100);
        Basarim ciger4=new Basarim("Ciğer",getString(R.string.sigarasiz_1,"6",getString(R.string.hafta)),getString(R.string.cigerleriniz), now > baslama ? ProgressHesapla((now-baslama)*100,3628800000L):0,ProgressHesapla((now-baslama)*100,3628800000L)==100);
        Basarim genel1=new Basarim("Genel",getString(R.string.sigarasiz_1,"20",getString(R.string.gun)),getString(R.string.tebrikler_gun,"20",getString(R.string.gun)) ,now > baslama ? ProgressHesapla((now-baslama)*100,1728000000L):0,ProgressHesapla((now-baslama)*100,1728000000L)==100);
        Basarim genel2=new Basarim("Genel",getString(R.string.sigarasiz_1,"100",getString(R.string.gun)),getString(R.string.tebrikler_gun,"100",getString(R.string.gun)), now > baslama ? ProgressHesapla((now-baslama)*100,1728000000L*5):0,ProgressHesapla((now-baslama)*100,1728000000L*5)==100);
        Basarim genel3=new Basarim("Genel",getString(R.string.sigarasiz_1,"365",getString(R.string.gun)),getString(R.string.tebrikler_gun,"365",getString(R.string.gun)), now > baslama ? ProgressHesapla((now-baslama)*100,2419200000L*12L):0,ProgressHesapla((now-baslama)*100,2419200000L*12L)==100);
        Basarim genel9=new Basarim("Genel",getString(R.string.omrunuzu_uzattiniz,"30",getString(R.string.dakika)),getString(R.string.tebrikler_uzattiniz,"30",getString(R.string.dakika)), now > baslama ? (int) ((now - baslama)/5 * 100 / 1800000) :0,(now - baslama)/5>=1800000);
        Basarim genel10=new Basarim("Genel",getString(R.string.omrunuzu_uzattiniz,"60",getString(R.string.dakika)),getString(R.string.tebrikler_uzattiniz,"60",getString(R.string.dakika)), now > baslama ? (int) ((now - baslama)/5 * 100 / (1800000*2)) :0,(now - baslama)/5>=1800000*2);
        Basarim genel11=new Basarim("Genel",getString(R.string.omrunuzu_uzattiniz,"6",getString(R.string.saat)),getString(R.string.tebrikler_uzattiniz,"6",getString(R.string.saat)), now > baslama ? (int) ((now - baslama)/5 * 100 / (1800000*12)) :0,(now - baslama)/5>=1800000*12);
        Basarim genel12=new Basarim("Genel",getString(R.string.omrunuzu_uzattiniz,"12",getString(R.string.saat)),getString(R.string.tebrikler_uzattiniz,"12",getString(R.string.saat)), now > baslama ? (int) ((now - baslama)/5 * 100 / (1800000*24)) :0,(now - baslama)/5>=1800000*24);
        Basarim genel13=new Basarim("Genel",getString(R.string.omrunuzu_uzattiniz,"1",getString(R.string.gun)),getString(R.string.tebrikler_uzattiniz,"1",getString(R.string.gun)), now > baslama ? (int) ((now - baslama)/5 * 100 / (86400000L)) :0,(now - baslama)/5>=86400000L);
        Basarim genel14=new Basarim("Genel",getString(R.string.omrunuzu_uzattiniz,"35",getString(R.string.gun)),getString(R.string.tebrikler_uzattiniz,"35",getString(R.string.gun)), now > baslama ? (int) ((now - baslama)/5 * 100 / (86400000L*35)) :0,(now - baslama)/5>=86400000L*35);

        if (now>baslama)
        {
            kurtarilanomur= (int) ((now-baslama)/(5*86400000L));
        }

        basarimlar.add(biraktin);
        basarimlar.add(kardiyo1);
        basarimlar.add(kardiyo2);
        basarimlar.add(kardiyo3);
        basarimlar.add(kardiyo4);
        basarimlar.add(kardiyo5);
        basarimlar.add(beyin1);
        basarimlar.add(beyin2);
        basarimlar.add(beyin3);
        basarimlar.add(beyin4);
        basarimlar.add(beyin5);
        basarimlar.add(beyin6);
        basarimlar.add(beyin7);
        basarimlar.add(ciger1);
        basarimlar.add(ciger2);
        basarimlar.add(ciger3);
        basarimlar.add(ciger4);
        basarimlar.add(genel1);
        basarimlar.add(genel2);
        basarimlar.add(genel3);
        basarimlar.add(genel9);
        basarimlar.add(genel10);
        basarimlar.add(genel11);
        basarimlar.add(genel12);
        basarimlar.add(genel13);
        basarimlar.add(genel14);

        if(sharedpreferences.getInt(gundeKey,0)!=0 && now > baslama)
        {
            int gun= (int)((now-baslama)/86400000L);
            Basarim genel4=new Basarim("Genel",getString(R.string.on_sigara,"10"),getString(R.string.tebrikler_10,"10"),  sharedpreferences.getInt(gundeKey,0)*gun>=10 ? 100:sharedpreferences.getInt(gundeKey,0)*100*gun/10,sharedpreferences.getInt(gundeKey,0)*gun>=10);
            Basarim genel5=new Basarim("Genel",getString(R.string.on_sigara,"100"),getString(R.string.tebrikler_10,"10"),  sharedpreferences.getInt(gundeKey,0)*gun>=100 ? 100:sharedpreferences.getInt(gundeKey,0)*100*gun/100,sharedpreferences.getInt(gundeKey,0)*gun>=100);
            Basarim genel6=new Basarim("Genel",getString(R.string.on_sigara,"200"),getString(R.string.tebrikler_10,"10"),  sharedpreferences.getInt(gundeKey,0)*gun>=200 ? 100:sharedpreferences.getInt(gundeKey,0)*100*gun/200,sharedpreferences.getInt(gundeKey,0)*gun>=200);
            Basarim genel7=new Basarim("Genel",getString(R.string.on_sigara,"500"),getString(R.string.tebrikler_10,"10"),  sharedpreferences.getInt(gundeKey,0)*gun>=500 ? 100:sharedpreferences.getInt(gundeKey,0)*100*gun/500,sharedpreferences.getInt(gundeKey,0)*gun>=500);
            Basarim genel8=new Basarim("Genel",getString(R.string.on_sigara,"1000"),getString(R.string.tebrikler_10,"10"),  sharedpreferences.getInt(gundeKey,0)*gun>=1000 ? 100:(sharedpreferences.getInt(gundeKey,0)*100*gun)/1000,sharedpreferences.getInt(gundeKey,0)*gun>=1000);
            basarimlar.add(genel4);
            basarimlar.add(genel5);
            basarimlar.add(genel6);
            basarimlar.add(genel7);
            basarimlar.add(genel8);

        }

        docRef= FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey,"")).collection("status").document(dateFormat.format(Calendar.getInstance().getTimeInMillis()));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                status= !documentSnapshot.exists();
            }
        });
        FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey,"")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getBoolean("hedef"))
                {
                    target=true;
                    SharedPreferences.Editor editor= sharedpreferences.edit();
                    editor.putBoolean(targetKey,target);
                    editor.commit();

                }
                else
                {
                    target=false;
                    SharedPreferences.Editor editor= sharedpreferences.edit();
                    editor.putBoolean(targetKey,target);
                    editor.commit();
                }
            }
        });


        if(sharedpreferences.getInt(gundeKey,0)!=0)
        {
            getSupportFragmentManager().beginTransaction().add(R.id.framelayout,new AnaSayfa()).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().add(R.id.framelayout,new Ayarlar()).commit();
            bottomNavigation.setSelectedItemId(R.id.actions_setting);
        }
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.actions_home) {
                    fragment = new AnaSayfa();
                } else if (itemId == R.id.actions_health) {
                    fragment = new Saglik();
                } else if (itemId == R.id.actions_achieviments) {
                    fragment = new Basarimlar();
                } else if (itemId == R.id.actions_setting) {
                    fragment = new Ayarlar();
                }
                if(sharedpreferences.getInt(gundeKey,0)==0)
                {
                    fragment=new Ayarlar();
                    //bottomNavigation.setSelectedItemId(R.id.actions_setting);
                    Toast.makeText(MainActivity.this, getString(R.string.lutfen_ayarlar), Toast.LENGTH_SHORT).show();
                }
                getSupportFragmentManager().beginTransaction().replace(frameLayout.getId(),fragment).commit();

                return true;
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("all-"+(Locale.getDefault().getLanguage().equals(new Locale("tr").getLanguage()) ? "tr":"en"))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TAG", msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public int ProgressHesapla(long fark,long hedef)
    {
        return (fark)/hedef >100 ? 100 :  (int) (fark/hedef);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
    public boolean getPurchaseValueFromPref(){
        return getApplicationContext().getSharedPreferences(MyPREFERENCES, 0).getBoolean( PURCHASE_KEY,false);
    }
}