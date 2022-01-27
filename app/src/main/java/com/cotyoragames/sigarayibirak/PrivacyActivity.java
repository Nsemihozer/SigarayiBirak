package com.cotyoragames.sigarayibirak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class PrivacyActivity extends AppCompatActivity {

    TextView popuptext;
    WebView popupweb;
    Button privacy,hizmet;
    AdView mAdView;
    private static final String PURCHASE_KEY = "PURCHASE_KEY";
    public static final String MyPREFERENCES = "MyPrefs";
    public boolean getPurchaseValueFromPref(){
        return getApplicationContext().getSharedPreferences(MyPREFERENCES, 0).getBoolean( PURCHASE_KEY,false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        privacy=findViewById(R.id.privacybtn);
        hizmet=findViewById(R.id.hizmetbtn);

        mAdView = findViewById(R.id.adViewPrivacy);
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


        View popup=findViewById(R.id.popup);
        hizmet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonShowPopupWindowClick(v);
            }
        });
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonShowPopupWindowClick(v);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
        finish();
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        popuptext=popupView.findViewById(R.id.popuptext);
        popupweb=popupView.findViewById(R.id.popupwebview);

        if(view.getId()==R.id.hizmetbtn)
        {
            popupweb.loadUrl("https://cotyoragames.com/projects/quitsmoking-privacy-policy.php");
            popuptext.setText("Hizmet");
        }
        else
        {
            popupweb.loadUrl("https://cotyoragames.com/projects/quit-smoking-terms-conditions.php");
            popuptext.setText("Gizlilik");
        }


        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

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