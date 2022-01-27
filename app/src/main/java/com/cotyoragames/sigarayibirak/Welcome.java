package com.cotyoragames.sigarayibirak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.file.attribute.DosFileAttributes;
import java.util.Calendar;
import java.util.Date;

public class Welcome extends AppCompatActivity {
    private TextView hosgeldin;
    private ImageView logo;
    private Button continuebtn;
    private int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        hosgeldin=findViewById(R.id.hosgeldintext);
        logo=findViewById(R.id.logo);
        continuebtn=findViewById(R.id.buttoncontinue);
        hosgeldin.setText(Html.fromHtml(getString(R.string.welcome2)));
        logo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.logo_512x512));




        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                if(index>1)
                {
                    Intent intent = new Intent(Welcome.this,Questions.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    Welcome.this.overridePendingTransition(0, 0);
                }
                hosgeldin.setText(getText(R.string.welcome1));
                logo.setImageDrawable(ContextCompat.getDrawable(Welcome.this, R.drawable.lifebelt));
            }
        });

    }
}