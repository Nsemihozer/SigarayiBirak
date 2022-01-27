package com.cotyoragames.sigarayibirak;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;

import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.cotyoragames.Basarim;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.icu.lang.UProperty.INT_START;
import static androidx.core.content.ContextCompat.getDrawable;
import static com.cotyoragames.sigarayibirak.MainActivity.basarimlar;
import static com.cotyoragames.sigarayibirak.MainActivity.targetKey;
import static com.cotyoragames.sigarayibirak.Saglik.tasarrufKey;


public class AnaSayfa extends Fragment  {

    public static final String MyPREFERENCES = "MyPrefs",idKey="idKey",hedefKey="hedefKey",hedefNameKey="hedefNameKey", baslamakey = "baslamaKey",hedefBaslamaKey="hedefBaslamaKey", gundeKey="gundeKey",pakettekiKey="pakettekiKey",paketfiyatiKey="paketfiyatiKey";
    private static final String PURCHASE_KEY = "PURCHASE_KEY";

    SharedPreferences sharedpreferences;
    TextView sure,tasarrufMiktarText,icilmeyensigaratext,achivimentText,hedefText,hedefadtext,hedefguntext;
    ProgressBar hedefprogress;
    EditText hedefNameText;
    ImageView achievementImage;
    double tasarrufMiktarı;
    int icilmeyensigara;
    int gundeicilen,paketteki;

    private Button hedefBtn;
    private ConstraintLayout hedefler;
    int saniye,dakika,saat,gun,hafta,ay,hedef;
    private String hedefName;
    ArrayList<String> hedefList;
    Button button;
    AdView mAdView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_ana_sayfa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mAdView = view.findViewById(R.id.adViewAnaSayfa);
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

        sure=view.findViewById(R.id.gecenSureText);
        tasarrufMiktarText=view.findViewById(R.id.tasarrufMiktarText);
        icilmeyensigaratext=view.findViewById(R.id.icilmeyensigaratext);
        achivimentText=view.findViewById(R.id.achievementText);
        hedefText=view.findViewById(R.id.hedefText);
        hedefNameText=view.findViewById(R.id.hedefTitle);
        hedefBtn=view.findViewById(R.id.hedefbutton);
        achievementImage=view.findViewById(R.id.achievementImage);
        hedefadtext=view.findViewById(R.id.anasayfahedefadtext);
        hedefguntext=view.findViewById(R.id.anasayfahedefguntext);
        hedefprogress=view.findViewById(R.id.anasayfahedefprogressBar);

        hedefler=view.findViewById(R.id.hedefler);

        if(MainActivity.target || sharedpreferences.getInt(hedefKey,0)>0)
        {
            /*Bitmap bitmapImage = BitmapFactory.decodeFile(Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +R.drawable.savings).toString());
            int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
            ((ImageView)view.findViewById(R.id.imageView4)).setImageBitmap(scaled);*/
            view.findViewById(R.id.hedefcard2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent  intent =  new Intent(getContext(),HedefActivity.class);
                    startActivity(intent);
                }
            });
            hedef=sharedpreferences.getInt(hedefKey,0);
            view.findViewById(R.id.hedefCard).setVisibility(View.GONE);
            view.findViewById(R.id.hedefcard2).setVisibility(View.VISIBLE);
            int tasarruf=(int)sharedpreferences.getFloat(tasarrufKey,0)-sharedpreferences.getInt(hedefBaslamaKey,0);
            hedefadtext.setText(sharedpreferences.getString(hedefNameKey,""));
            int gundeicilen=sharedpreferences.getInt(gundeKey, 0);
            int paketteki = 20;
            float paketfiyati = sharedpreferences.getFloat(paketfiyatiKey, 0);
            float gunluk = 0;
            gunluk = ((float)gundeicilen / paketteki) *  paketfiyati;
            hedefguntext.setText (view.getContext().getString(R.string.bu_hedef_icin,(int)((float)hedef/gunluk)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hedefprogress.setProgress((int)(tasarruf*100/(float)hedef)  ,true);
            }
            else
            {
                hedefprogress.setProgress((int)(tasarruf*100/(float)hedef));
            }
            ((TextView)view.findViewById(R.id.textView28)).setText(String.valueOf((int)(tasarruf*100/(float)hedef))+"%");
        }
        else
        {
            view.findViewById(R.id.hedefCard).setVisibility(View.VISIBLE);
            view.findViewById(R.id.hedefcard2).setVisibility(View.GONE);
        }






        hedefList = new ArrayList<>();

        hedefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hedefText.getText().toString().equals("")  && tryParse(hedefText.getText().toString().substring(2),0) >0)
                {
                    String s=hedefText.getText().toString();
                    String a= hedefNameText.getText().toString();
                    onButtonShowPopupWindowClick(v);
                    //hedef=Integer.valueOf(hedefText.getText().toString());
                }
                else
                {
                    Toast.makeText(getContext(), view.getContext().getText(R.string.gerekli_bilgiler), Toast.LENGTH_SHORT).show();
                }
            }
        });
        Iterator<Basarim> iterator = basarimlar.iterator();
        int basarimdone=0;
        while (iterator.hasNext()) {
            Basarim basarim = iterator.next();
            if (!basarim.isDone()) {

                achivimentText.setText(Html.fromHtml("<b> "+basarim.getAd()+" <b>")+" \n"+basarim.getDesc());
                ProgressBar prog=(ProgressBar)view.findViewById(R.id.progressBar2);
                prog.setProgress(basarim.getProgress());
                TextView anasayfabasarimpbtext= view.findViewById(R.id.anasayfabasarimpbtext);
                anasayfabasarimpbtext.setText(String.valueOf(basarim.getProgress())+"%");
                if (basarim.getType()=="Beyin")
                {
                    achievementImage.setImageDrawable(getDrawable(view.getContext(),R.drawable.beyin));
                }
                if (basarim.getType()=="Ciğer")
                {
                    achievementImage.setImageDrawable(getDrawable(view.getContext(),R.drawable.ciger));
                }
                if (basarim.getType()=="Kardiyovasküler")
                {
                    achievementImage.setImageDrawable(getDrawable(view.getContext(),R.drawable.cardio));
                }
                if (basarim.getType()=="Genel")
                {
                    achievementImage.setImageDrawable(getDrawable(view.getContext(),R.drawable.eklem));
                }

                basarimdone=1;
                break;
            }
        }
        if (basarimdone == 0) {
            achivimentText.setText(view.getContext().getText(R.string.bütün_basarimlar));
        }
        if(sharedpreferences.getLong(baslamakey,0)!=0) {
            hedef=sharedpreferences.getInt(hedefKey,0);
            final Date Baslama = Calendar.getInstance().getTime();
            Date now = Calendar.getInstance().getTime();
            Baslama.setTime(sharedpreferences.getLong(baslamakey, 0));
            if (Baslama.getTime() > now.getTime()) {
                final Date baslamagiris=Calendar.getInstance().getTime();
               // baslamagiris.setTime(sharedpreferences.getLong(tarihKey,0));
                final long[] fark = {Baslama.getTime() - now.getTime()};

                CountDownTimer timer = new CountDownTimer(fark[0], 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long f=Baslama.getTime()-baslamagiris.getTime();
                        fark[0] = millisUntilFinished;
                        long gecengun =fark[0] / (1000 * 60 * 60 * 24);
                        long topgun=f/(1000 * 60 * 60 * 24);
                        int progress=(int)(gecengun*100/(topgun+1));

                        long a=(1000L * 60 * 60 * 24  * 4*7);
                       // double x= ;
                        ay = (int)(fark[0]/a);

                        fark[0] -= ay * a;
                        hafta = (int) (fark[0] / (1000L * 60 * 60 * 24 * 7));
                        fark[0] -= hafta * (1000L * 60 * 60 * 24 * 7);
                        gun = (int) (fark[0] / (1000 * 60 * 60 * 24));
                        fark[0] -= gun * (1000 * 60 * 60 * 24);
                        saat = (int) (fark[0]/ (1000 * 60 * 60));
                        fark[0] -= saat * (1000 * 60 * 60);
                        dakika = (int) TimeUnit.MINUTES.convert(fark[0], TimeUnit.MILLISECONDS);
                        fark[0] -= dakika * (1000 * 60);
                        saniye = (int) TimeUnit.SECONDS.convert(fark[0], TimeUnit.MILLISECONDS);


                        sure.setText(view.getContext().getString(R.string.sure,String.valueOf(ay),view.getContext().getString(R.string.ay).charAt(0),String.valueOf(hafta),view.getContext().getString(R.string.hafta).charAt(0),String.valueOf(gun) , view.getContext().getString(R.string.gun).charAt(0),String.valueOf(saat) ,String.valueOf(dakika) , String.valueOf(saniye)));

                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            } else {
                gundeicilen = (sharedpreferences.getInt(gundeKey, 0));
                paketteki = (sharedpreferences.getInt(pakettekiKey, 0));
                final float saatteicilen = ((float)gundeicilen / 24);
                final Date start = new Date();
                start.setTime(sharedpreferences.getLong(baslamakey, 0));
                CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Date now = Calendar.getInstance().getTime();
                        long fark = now.getTime() - start.getTime();
                        long gecengun =fark / (1000 * 60 * 60 * 24);

                        ay = (int) ((float)fark / (1000L * 60 * 60 * 24 * 30));

                        fark -= ay * (1000L * 60 * 60 * 24 * 30);
                        hafta = (int) (fark / (1000L * 60 * 60 * 24 * 7));
                        fark -= hafta * (1000L * 60 * 60 * 24 * 7);
                        gun = (int) (fark / (1000 * 60 * 60 * 24));
                        fark -= gun * (1000 * 60 * 60 * 24);
                        saat = (int) (fark / (1000 * 60 * 60));
                        fark -= saat * (1000 * 60 * 60);
                        dakika = (int) TimeUnit.MINUTES.convert(fark, TimeUnit.MILLISECONDS);
                        fark -= dakika * (1000 * 60);
                        saniye = (int) TimeUnit.SECONDS.convert(fark, TimeUnit.MILLISECONDS);
                        sure.setText(view.getContext().getString(R.string.sure,String.valueOf(ay),view.getContext().getString(R.string.ay).charAt(0),String.valueOf(hafta),view.getContext().getString(R.string.hafta).charAt(0),String.valueOf(gun) , view.getContext().getString(R.string.gun).charAt(0),String.valueOf(saat) ,String.valueOf(dakika) , String.valueOf(saniye)));
                        if (ay > 0) {
                            gun += ay * 30;
                        }
                        if (hafta > 0)
                            gun += hafta * 7;

                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        icilmeyensigara = (int) ((gundeicilen * gun) + saatteicilen * saat);
                        icilmeyensigaratext.setText(String.valueOf(icilmeyensigara));
                        tasarrufMiktarı = ((float)icilmeyensigara / paketteki) * sharedpreferences.getFloat(paketfiyatiKey, 0);
                        tasarrufMiktarText.setText(String.format("%s%s", String.valueOf(tasarrufMiktarı), view.getContext().getString(R.string.currency)));
                        if(hedef!=0)
                        {
                            if(hedef+sharedpreferences.getInt(hedefBaslamaKey,0)<=tasarrufMiktarı)
                            {
                                hedef=0;
                                MainActivity.target=false;
                                FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey,"")).update("hedef",false);
                                editor.putInt(hedefKey,hedef);
                                editor.putString(targetKey,"false");
                                view.findViewById(R.id.hedefCard).setVisibility(View.VISIBLE);
                                getView().findViewById(R.id.hedefcard2).setVisibility(View.GONE);
                            }
                        }
                        editor.putFloat(tasarrufKey, (float) tasarrufMiktarı);
                        editor.apply();
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            }
        }

    }
    public void onButtonShowPopupWindowClick(View view) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle(getView().getContext().getText(R.string.hedef));

        // Setting Dialog Message
        alertDialog.setMessage(getView().getContext().getText(R.string.hedefi_bir));
        final EditText input = new EditText(getContext());
        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });
        input.setHint(getView().getContext().getText(R.string.hedef_adi));
        input.setMaxLines(1);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        String s = hedefText.getText().toString().split(" ")[1];
                        hedef = Integer.parseInt(s);
                        hedefName = input.getText().toString();
                        if (!hedefName.isEmpty()) {
                            hedefler.setVisibility(View.INVISIBLE);
                            Map<String, Object> hedefObj = new HashMap<>();
                            hedefObj.put("hedefMiktar", hedef);
                            hedefObj.put("hedefAd", hedefName);
                            FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey, "")).collection("hedefler").add(hedefObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getContext(), requireView().getContext().getText(R.string.hedef_basari_eklendi), Toast.LENGTH_SHORT).show();
                                }
                            });
                            FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey, "")).update("hedef", true);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putInt(hedefKey, hedef);
                            editor.putString(hedefNameKey, hedefName);
                            editor.putInt(hedefBaslamaKey, tasarrufMiktarı != 0 ? (int) tasarrufMiktarı : 0);
                            editor.putString(targetKey, "true");
                            editor.apply();
                            hedefadtext.setText(hedefName);
                            int gundeicilen=sharedpreferences.getInt(gundeKey, 0);
                            int paketteki = 20;
                            float paketfiyati = sharedpreferences.getFloat(paketfiyatiKey, 0);
                            float gunluk = 0;
                            gunluk = ((float)gundeicilen / paketteki) *  paketfiyati;
                            hedefguntext.setText (requireView().getContext().getString(R.string.bu_hedef_icin,(int)((float)hedef/gunluk)));
                            hedefprogress.setProgress(0);
                            JSONObject jsonObject = new JSONObject();
                            requireView().findViewById(R.id.hedefcard2).setVisibility(View.VISIBLE);
                            requireView().findViewById(R.id.hedefCard).setVisibility(View.GONE);
                            requireView().findViewById(R.id.hedefcard2).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent  intent =  new Intent(getContext(),HedefActivity.class);
                                    startActivity(intent);
                                }
                            });
                            try {
                                jsonObject.put("Name", hedefName);
                                jsonObject.put("Miktar", hedef);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //hedefList.add(jsonObject.toString());
                            //JSONArray jsonArray= new JSONArray();
                            //jsonArray.put(jsonObject);
                            File file = new File(requireView().getContext().getFilesDir(), "hedefJsonFile.json");
                            try {
                                boolean x= file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            FileReader fileReader = null;

                            try {
                                fileReader = new FileReader(file);
                                BufferedReader bufferedReader = new BufferedReader(fileReader);
                                StringBuilder stringBuilder = new StringBuilder();
                                String line = bufferedReader.readLine();
                                while (line != null) {
                                    stringBuilder.append(line).append("\n");
                                    line = bufferedReader.readLine();
                                }
                                bufferedReader.close();
                                String responce = stringBuilder.toString();
                                JSONArray jArray;
                                if (responce == "") {
                                    jArray = new JSONArray();
                                } else {
                                    jArray = new JSONArray(responce);
                                }
                                jArray.put(jsonObject);
                                FileWriter fileWriter = null;
                                fileWriter = new FileWriter(file);
                                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                                bufferedWriter.write(jArray.toString());
                                bufferedWriter.close();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else
                        {
                            Toast.makeText(getContext(),
                                    "Hedef Adı boş olamaz", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    public int tryParse(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }


    public boolean getPurchaseValueFromPref(){
        return requireView().getContext().getSharedPreferences(MyPREFERENCES, 0).getBoolean( PURCHASE_KEY,false);
    }



}

