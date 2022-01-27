package com.cotyoragames.sigarayibirak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cotyoragames.HedefAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static com.cotyoragames.sigarayibirak.AnaSayfa.MyPREFERENCES;
import static com.cotyoragames.sigarayibirak.AnaSayfa.hedefBaslamaKey;
import static com.cotyoragames.sigarayibirak.AnaSayfa.hedefKey;
import static com.cotyoragames.sigarayibirak.AnaSayfa.hedefNameKey;
import static com.cotyoragames.sigarayibirak.Saglik.tasarrufKey;

public class HedefActivity extends AppCompatActivity {
    private ListView hedefListView;
    private ArrayList<Hedef> hedefArrayList;
    TextView hedefMainTitleText,hedefMainDescText;
    ProgressBar hedefMainProgressBar;
    SharedPreferences sharedpreferences;
    int hedef;
    int tasarruf;
    private InterstitialAd mInterstitialAd;
    private static final String PURCHASE_KEY = "PURCHASE_KEY";
    public boolean getPurchaseValueFromPref(){
        return getApplicationContext().getSharedPreferences(MyPREFERENCES, 0).getBoolean( PURCHASE_KEY,false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hedef);
        hedefListView=findViewById(R.id.hedefListView);
        hedefMainTitleText=findViewById(R.id.hedefMainTitleText);
        hedefMainDescText=findViewById(R.id.hedefMainDescText);
        hedefMainProgressBar=findViewById(R.id.hedefMainProgressBar);
        hedefArrayList=new ArrayList<>();
        sharedpreferences=getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);

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




        hedef=sharedpreferences.getInt(hedefKey,0);
        if(hedef>0)
        {
            tasarruf=(int)sharedpreferences.getFloat(tasarrufKey,0)-sharedpreferences.getInt(hedefBaslamaKey,0);
            hedefMainTitleText.setText(sharedpreferences.getString(hedefNameKey,""));
            hedefMainDescText.setText(tasarruf+"/"+hedef);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hedefMainProgressBar.setProgress(tasarruf,true);
            }
            else
            {
                hedefMainProgressBar.setProgress(tasarruf);
            }
        }
        else
        {
            findViewById(R.id.hedefCardview).setVisibility(View.GONE);
        }
        File file = new File(this.getFilesDir(),"hedefJsonFile.json");
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            String responce = stringBuilder.toString();

            JSONArray jArray = new JSONArray(responce);
            if (jArray != null) {
                for (int i=0;i<jArray.length();i++){
                    String s=jArray.get(i).toString();
                    //s=s.substring(1,s.length()-1);
                    JSONObject json_data = new JSONObject(s);
                    Hedef hedef = new Hedef(json_data.getString("Name"),json_data.getInt("Miktar"));
                    hedefArrayList.add(hedef);
                }
            }
            HedefAdapter hedefAdapter = new HedefAdapter(hedefArrayList,this);
            hedefListView.setAdapter(hedefAdapter);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        if(hedef>0)
        {
            tasarruf=(int)sharedpreferences.getFloat(tasarrufKey,0)-sharedpreferences.getInt(hedefBaslamaKey,0);
            hedefMainTitleText.setText(sharedpreferences.getString(hedefNameKey,""));
            hedefMainDescText.setText(tasarruf+"/"+hedef);
            hedefArrayList.remove(hedefArrayList.size()-1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hedefMainProgressBar.setProgress(tasarruf/hedef  ,true);
            }
            else
            {
                hedefMainProgressBar.setProgress(tasarruf/hedef);
            }
        }
        else
        {
            findViewById(R.id.hedefCardview).setVisibility(View.GONE);
        }
    }
}