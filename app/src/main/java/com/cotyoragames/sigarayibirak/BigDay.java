package com.cotyoragames.sigarayibirak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.text.Html;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.Locale;

public class BigDay extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs",emailKey="emailKey",idKey="idKey",adkey="adKey",baslamakey = "baslamaKey";
    private Calendar myCalendar;
    Button biraktim,birakmadim;
    FirebaseFirestore db;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_day);
        birakmadim=findViewById(R.id.bigdaybirakamadim);
        biraktim=findViewById(R.id.bigdaybiraktim);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        myCalendar = Calendar.getInstance();
        db= FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.document("users/"+sharedPreferences.getString(idKey,""));
        /*docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username=documentSnapshot.getString("ad");
            }
        });*/
        ((TextView)findViewById(R.id.textView6)).setText(Html.fromHtml(getString(R.string.bigday,sharedPreferences.getString(adkey,""))));
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar. set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                docRef.update("birakma",myCalendar.getTimeInMillis());
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putLong(baslamakey,myCalendar.getTimeInMillis());
                editor.commit();

                FirebaseMessaging.getInstance().subscribeToTopic(DateFormat.format("ddMMyyyy",myCalendar).toString() +"-"+ (Locale.getDefault().getLanguage().equals(new Locale("tr").getLanguage()) ? "tr":"en"))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                Intent intent = new Intent(BigDay.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                BigDay.this.overridePendingTransition(0, 0);
                finish();
                //updateLabel();
            }

        };

        biraktim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BigDay.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                BigDay.this.overridePendingTransition(0, 0);
                finish();
            }
        });
        birakmadim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(BigDay.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() -1000);
                dialog.show();
                /*new DatePickerDialog(BigDay.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/
            }
        });





    }
}