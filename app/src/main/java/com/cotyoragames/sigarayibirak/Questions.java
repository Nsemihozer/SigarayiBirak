package com.cotyoragames.sigarayibirak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Questions extends AppCompatActivity {
    private TextView question;
    private Button erkek,kadin,diger,simdi,sonra,zaten,cnt;
    private String cinsiyet="";
    private EditText yasText;
    private Date birakma,yas;
    DatePicker picker;
    private Calendar myCalendar;
    int index=0;
    public static final String MyPREFERENCES = "MyPrefs",emailKey="emailKey",idKey="idKey",adkey="adKey",baslamakey = "baslamaKey";
    SharedPreferences sharedPreferences;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        cnt=findViewById(R.id.btncntquestion);
        question=findViewById(R.id.questiontext);
        erkek=findViewById(R.id.answererkek);
        kadin=findViewById(R.id.answerkadin);
        diger=findViewById(R.id.answerdiger);
        simdi=findViewById(R.id.answernow);
        sonra=findViewById(R.id.answerlater);
        zaten=findViewById(R.id.answerearly);
        yasText=findViewById(R.id.yasText);
        //birakma=new Date();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        myCalendar = Calendar.getInstance();



        question.setText(getString(R.string.question_cinsiyet));
        findViewById(R.id.questioncinsiyet).setVisibility(View.VISIBLE);
        findViewById(R.id.questionbirakma).setVisibility(View.INVISIBLE);
        findViewById(R.id.questionyas).setVisibility(View.INVISIBLE);
        cnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cinsiyet !="")
                {
                    index++;
                    if(index==2 && birakma==null)
                    {
                        index--;
                        Toast.makeText(Questions.this,  getText(R.string.birakmatarihibos), Toast.LENGTH_SHORT).show();
                    }

                    if(index==3 && yas==null)
                    {
                        index--;
                        Toast.makeText(Questions.this, getText(R.string.dogumtarihibos), Toast.LENGTH_SHORT).show();
                    }

                }

               updateUI();
            }
        });
        final DatePickerDialog.OnDateSetListener date2= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar. set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                yas=myCalendar.getTime();
                yasText.setText(myCalendar.get(Calendar.DAY_OF_MONTH)+"/"+(myCalendar.get(Calendar.MONTH)+1)+"/"+myCalendar.get(Calendar.YEAR));
            }
        };
        yasText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog =new DatePickerDialog(Questions.this,android.R.style.Theme_Holo_Dialog, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                //dialog.setButton(DatePickerDialog.BUTTON_POSITIVE,"Onayla", Message.obtain());
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis()-568025136000L);
                dialog.show();

            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                birakma= myCalendar.getTime();
                //Intent intent = new Intent(Questions.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //startActivity(intent);
                //Questions.this.overridePendingTransition(0, 0);
                //updateLabel();
            }

        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.answererkek || v.getId()==R.id.answerkadin || v.getId()==R.id.answerdiger )
                {
                    erkek.setBackgroundColor(getResources().getColor(R.color.white));
                    kadin.setBackgroundColor(getResources().getColor(R.color.white));
                    diger.setBackgroundColor(getResources().getColor(R.color.white));
                    erkek.setTextColor(Color.parseColor("#3E3E3E"));
                    kadin.setTextColor(Color.parseColor("#3E3E3E"));
                    diger.setTextColor(Color.parseColor("#3E3E3E"));
                    Button b = (Button)v;
                    cinsiyet = b.getText().toString();
                    b.setBackgroundColor(getResources().getColor(R.color.green));
                    b.setTextColor(Color.WHITE);
                }
                else if(v.getId()==R.id.answernow)
                {
                    birakma= Calendar.getInstance().getTime();
                    Button b = (Button)v;
                    b.setBackgroundColor(getResources().getColor(R.color.green));
                    sonra.setBackgroundColor(getResources().getColor(R.color.white));
                    zaten.setBackgroundColor(getResources().getColor(R.color.white));
                    sonra.setTextColor(Color.parseColor("#3E3E3E"));
                    zaten.setTextColor(Color.parseColor("#3E3E3E"));
                    b.setTextColor(Color.WHITE);
                }
                else if(v.getId()==R.id.answerlater)
                {
                    simdi.setBackgroundColor(getResources().getColor(R.color.white));
                    zaten.setBackgroundColor(getResources().getColor(R.color.white));
                    simdi.setTextColor(Color.parseColor("#3E3E3E"));
                    zaten.setTextColor(Color.parseColor("#3E3E3E"));
                    Button b = (Button)v;
                    b.setBackgroundColor(getResources().getColor(R.color.green));
                    b.setTextColor(Color.WHITE);
                    DatePickerDialog pickerDialog= new DatePickerDialog(Questions.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                    pickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()+86400000);
                    pickerDialog.show();
                    birakma= myCalendar.getTime();
                }
                else if(v.getId()==R.id.answerearly)
                {
                    simdi.setBackgroundColor(getResources().getColor(R.color.white));
                    sonra.setBackgroundColor(getResources().getColor(R.color.white));
                    simdi.setTextColor(Color.parseColor("#3E3E3E"));
                    sonra.setTextColor(Color.parseColor("#3E3E3E"));
                    Button b = (Button)v;
                    b.setBackgroundColor(getResources().getColor(R.color.green));
                    b.setTextColor(Color.WHITE);
                    DatePickerDialog pickerDialog= new DatePickerDialog(Questions.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                    pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()-86400000);
                    pickerDialog.show();
                    birakma= myCalendar.getTime();
                }
            }
        };

        erkek.setOnClickListener(listener);
        kadin.setOnClickListener(listener);
        diger.setOnClickListener(listener);
        simdi.setOnClickListener(listener);
        sonra.setOnClickListener(listener);
        zaten.setOnClickListener(listener);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        index--;
        updateUI();
        if(index<0)
        {
            index++;
            finish();
        }
    }
    private void updateUI()
    {
        if(index>2)
        {
            Map<String, Object> user = new HashMap<>();
            user.put("ad", sharedPreferences.getString(adkey,""));
            user.put("cinsiyet", cinsiyet);
            user.put("yas", yas);
            user.put("birakma", birakma.getTime());
            user.put("paid", false);
            if((DateFormat.format("dd/MM/yyyy",birakma).toString().equals(DateFormat.format("dd/MM/yyyy",Calendar.getInstance().getTime()).toString())) || birakma.getTime() < Calendar.getInstance().getTimeInMillis() )
            {
                user.put("bigdayShown",true);
            }
            else
            {
                user.put("bigdayShown",false);
                FirebaseMessaging.getInstance().subscribeToTopic(DateFormat.format("ddMMyyyy",birakma).toString() +"-"+ (Locale.getDefault().getLanguage().equals(new Locale("tr").getLanguage()) ? "tr":"en"))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
            }
            FirebaseMessaging.getInstance().subscribeToTopic("all")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
            user.put("email",sharedPreferences.getString(emailKey,""));
            user.put("hedef",false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(baslamakey,birakma.getTime());
            editor.commit();
            db.collection("users").document(sharedPreferences.getString(idKey,"")).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent intent = new Intent(Questions.this,MakaleActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("hosgeldin","hosgeldin");
                    startActivity(intent);
                    Questions.this.overridePendingTransition(0, 0);
                    finish();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Questions.this, getText(R.string.birhata), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
        else if(index==2)
        {
            question.setText(getString(R.string.question_yas));
            findViewById(R.id.questioncinsiyet).setVisibility(View.INVISIBLE);
            findViewById(R.id.questionbirakma).setVisibility(View.INVISIBLE);
            findViewById(R.id.questionyas).setVisibility(View.VISIBLE);
        }
        else if(index==1)
        {
            question.setText(getString(R.string.question_birakma));
            findViewById(R.id.questioncinsiyet).setVisibility(View.INVISIBLE);
            findViewById(R.id.questionbirakma).setVisibility(View.VISIBLE);
            findViewById(R.id.questionyas).setVisibility(View.INVISIBLE);
        }
        else if(index == 0)
        {
            question.setText(getString(R.string.question_cinsiyet));
            findViewById(R.id.questioncinsiyet).setVisibility(View.VISIBLE);
            findViewById(R.id.questionbirakma).setVisibility(View.INVISIBLE);
            findViewById(R.id.questionyas).setVisibility(View.INVISIBLE);
        }
    }
}