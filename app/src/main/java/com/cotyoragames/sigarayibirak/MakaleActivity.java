package com.cotyoragames.sigarayibirak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import java.util.Locale;

public class MakaleActivity extends AppCompatActivity {
    WebView web;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makale);
        web=findViewById(R.id.makaleweb);
        button=findViewById(R.id.button2);
        if(getIntent().getStringExtra("hosgeldin")!=null)
        {
            web.loadUrl("https://sb.cotyoragames.com/fetch_article.php?type=normal&id=5&lang="+Locale.getDefault().getLanguage());
        }
        else
        {
            web.loadUrl("https://sb.cotyoragames.com/fetch_article.php?type=random&category=2&lang="+ Locale.getDefault().getLanguage());//https://sb.cotyoragames.com/fetch_article.php?type=random&lang=tr
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}