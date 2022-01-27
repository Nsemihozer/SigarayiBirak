package com.cotyoragames.sigarayibirak;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


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
import java.util.List;

import static com.cotyoragames.sigarayibirak.MainActivity.PURCHASE_KEY;
import static com.cotyoragames.sigarayibirak.Ayarlar.paketfiyatiKey;
import static com.cotyoragames.sigarayibirak.Ayarlar.pakettekiKey;
import static com.cotyoragames.sigarayibirak.MainActivity.gundeKey;
import static com.cotyoragames.sigarayibirak.MainActivity.idKey;


public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG ="SigninActivity" ;
    public static final String MyPREFERENCES = "MyPrefs",emailKey="emailKey",idKey="idKey",baslamakey = "baslamaKey",adkey="adKey";
    private static final int MY_REQUEST_CODE =9005 ;
    SharedPreferences sharedPreferences;
    private Button hizmet;
    public static GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore db;
    AppUpdateManager appUpdateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appUpdateManager= AppUpdateManagerFactory.create(getApplicationContext());

// Returns an intent object that you use to check for an update.


        checkUpdates();
// Checks that the platform will allow the specified type of update.



        hizmet=findViewById(R.id.buttonhizmet);
        hizmet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),PrivacyActivity.class);
                startActivity(intent);
                finish();
            }
        });
        db= FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
         mGoogleSignInClient  = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    private void checkUpdates() {
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,
                                // The current activity making the update request.
                                getParent(),
                                // Include a request code to later monitor this update request.
                                MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    // Request the update.
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(idKey,account.getId());
            editor.putString(adkey,account.getDisplayName());
            editor.putString(emailKey,account.getEmail());
            editor.commit();

            db= FirebaseFirestore.getInstance();
            String s=account.getId();
            CollectionReference cl=db.collection("users");

            final DocumentReference docRef = db.document(cl.getPath()+"/"+account.getId());
            final Intent[] intent = new Intent[1];
            final int[] i = {0};
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                      if(documentSnapshot.exists()) {
                                                          Calendar birakma= Calendar.getInstance();
                                                          if(documentSnapshot.getLong("birakma")==0)
                                                          {
                                                              Intent intent = new Intent(LoginActivity.this,ResetActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);;
                                                              LoginActivity.this.overridePendingTransition(0, 0);
                                                              startActivity(intent);
                                                              finish();
                                                          }
                                                          birakma.setTimeInMillis(documentSnapshot.getLong("birakma"));
                                                          Calendar now=Calendar.getInstance();
                                                          if(birakma.get(Calendar.DAY_OF_MONTH)==now.get(Calendar.DAY_OF_MONTH) &&
                                                                  birakma.get(Calendar.MONTH)==now.get(Calendar.MONTH) &&
                                                                  birakma.get(Calendar.YEAR)==now.get(Calendar.YEAR) && !documentSnapshot.getBoolean("bigdayShown"))
                                                          {
                                                              docRef.update("bigdayShown",true);
                                                              intent[0] = new Intent(getApplicationContext(), BigDay.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                              LoginActivity.this.overridePendingTransition(0, 0);
                                                          }
                                                          else {
                                                              SharedPreferences.Editor edit = sharedPreferences.edit();
                                                              edit.putLong(baslamakey, documentSnapshot.getLong("birakma"));
                                                              edit.putBoolean(PURCHASE_KEY,documentSnapshot.getBoolean("paid"));
                                                              edit.commit();
                                                              intent[0] = new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                              LoginActivity.this.overridePendingTransition(0, 0);
                                                          }
                                                      }
                                                      else
                                                      {
                                                          intent[0] = new Intent(getApplicationContext(),Welcome.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                          LoginActivity.this.overridePendingTransition(0, 0);
                                                      }
                                                      startActivity(intent[0]);
                                                      finish();
                                                  }



                                              });

            /*Intent intent= new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();*/
        }
        updateUI(account);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(getApplicationContext());
                alertDialogBuilder.setMessage("Eğer indirmeyi gerçekleştirmezseniz uygulamaya devam edemezsiniz. Onaylıyor musunuz?").setTitle("Çıkmak istediğinize emin misiniz?");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                });
                alertDialogBuilder.setNegativeButton("Hayır", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkUpdates();
                    }
                });
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(idKey,account.getId());
            editor.putString(adkey,account.getDisplayName());
            editor.putString(emailKey,account.getEmail());
            editor.commit();
            db= FirebaseFirestore.getInstance();
            String s=account.getId();
            CollectionReference cl=db.collection("users");

            final DocumentReference docRef = db.document(cl.getPath()+"/"+account.getId());
            final Intent[] intent = new Intent[1];
            final int[] i = {0};
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                      if(documentSnapshot.exists()) {
                                                         Calendar birakma= Calendar.getInstance();
                                                         birakma.setTimeInMillis(documentSnapshot.getLong("birakma"));
                                                         Calendar now=Calendar.getInstance();
                                                         if(birakma.get(Calendar.DAY_OF_MONTH)==now.get(Calendar.DAY_OF_MONTH) &&
                                                                 birakma.get(Calendar.MONTH)==now.get(Calendar.MONTH) &&
                                                             birakma.get(Calendar.YEAR)==now.get(Calendar.YEAR) && !documentSnapshot.getBoolean("bigdayShown"))
                                                         {
                                                             docRef.update("bigdayShown",true);
                                                             intent[0] = new Intent(getApplicationContext(), BigDay.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                             LoginActivity.this.overridePendingTransition(0, 0);
                                                         }
                                                         else
                                                         {
                                                             SharedPreferences.Editor edit = sharedPreferences.edit();
                                                             edit.putLong(baslamakey, documentSnapshot.getLong("birakma"));
                                                             edit.putBoolean(PURCHASE_KEY,documentSnapshot.getBoolean("paid"));
                                                             if(documentSnapshot.getLong("gundeicilen")!=null)
                                                             {
                                                                 edit.putInt(gundeKey, (int) ((long) documentSnapshot.getLong("gundeicilen")));
                                                             }
                                                             if(documentSnapshot.get("paketfiyati")!=null)
                                                             {
                                                                 edit.putFloat(paketfiyatiKey, (float) ((double) documentSnapshot.get("paketfiyati")));
                                                             }
                                                             edit.putInt(pakettekiKey,20);

                                                             edit.commit();
                                                             intent[0] = new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                             LoginActivity.this.overridePendingTransition(0, 0);
                                                         }

                                                          FirebaseFirestore.getInstance().collection("users").document(sharedPreferences.getString(idKey, "")).collection("hedefler").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                              @Override
                                                              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                  List<DocumentSnapshot> documentSnapshotList= queryDocumentSnapshots.getDocuments();
                                                                  ArrayList<Hedef> items = new ArrayList<Hedef>();
                                                                  for(int i=0;i< documentSnapshotList.size();i++)
                                                                  {
                                                                      DocumentSnapshot documentSnapshot= documentSnapshotList.get(i);
                                                                      String ad = documentSnapshot.getString("hedefAd");


                                                                      int status =  (int) (long) documentSnapshot.getLong("hedefMiktar");

                                                                      JSONObject jsonObject = new JSONObject();
                                                                      try {
                                                                          jsonObject.put("Name", ad);
                                                                          jsonObject.put("Miktar", status);
                                                                      } catch (JSONException e) {
                                                                          e.printStackTrace();
                                                                      }
                                                                      //hedefList.add(jsonObject.toString());
                                                                      //JSONArray jsonArray= new JSONArray();
                                                                      //jsonArray.put(jsonObject);
                                                                      File file = new File(getApplicationContext().getFilesDir(), "hedefJsonFile.json");
                                                                      try {
                                                                          file.createNewFile();
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


                                                              }
                                                          });
                                                          docRef.update("hedef",false);



                                                      }
                                                      else
                                                      {
                                                          intent[0] = new Intent(getApplicationContext(),Welcome.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                          LoginActivity.this.overridePendingTransition(0, 0);
                                                      }
                                                      startActivity(intent[0]);
                                                      finish();
                                                  }



            }

            );


            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, "signInResult:failed code=" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {

    }
}