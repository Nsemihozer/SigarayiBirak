package com.cotyoragames.sigarayibirak;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Ayarlar extends Fragment implements PurchasesUpdatedListener {



    private EditText  pfiyatitext, gundeicilentext;
    private ImageView premium;
    private Button save, restart, exit;
    private static final String PURCHASE_KEY = "PURCHASE_KEY";
    private int gundeicilen;
    private float pfiyati;
    //private boolean perm=false;
    public static final String MyPREFERENCES = "MyPrefs", baslamakey = "baslamaKey", gundeKey = "gundeKey", pakettekiKey = "pakettekiKey", paketfiyatiKey = "paketfiyatiKey", tarihKey = "tarihKey", idKey = "idKey";
    SharedPreferences sharedpreferences;
    AdView mAdView;
    private static final String PRODUCT_ID = "sigarayibirakpremium";
    private BillingClient billingClient;

    public boolean getPurchaseValueFromPref(){
        return requireContext().getSharedPreferences(MyPREFERENCES, 0).getBoolean( PURCHASE_KEY,false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_ayarlar, container, false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pfiyatitext = view.findViewById(R.id.pfiyati);
        gundeicilentext = view.findViewById(R.id.gündeicilen);
        premium=view.findViewById(R.id.premiumimage);
        sharedpreferences = requireActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mAdView = view.findViewById(R.id.adViewAyarlar);
        if(!getPurchaseValueFromPref())
        {
            premium.setVisibility(View.VISIBLE);
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
            premium.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetupBilling();
                }
            });
        }
        else
        {
            premium.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
            view.findViewById(R.id.textView27).setVisibility(View.GONE);
        }


        if(sharedpreferences.getLong(baslamakey,0)!=0)
        {

            pfiyatitext.setText(Float.toString(sharedpreferences.getFloat(paketfiyatiKey,0)));
            gundeicilentext.setText(Integer.toString(sharedpreferences.getInt(gundeKey,0)));
        }


        save=view.findViewById(R.id.savebutton);
        restart=view.findViewById(R.id.restartbtn);
        exit=view.findViewById(R.id.btncik);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pfiyati = Float.parseFloat(pfiyatitext.getText().toString());
                gundeicilen = Integer.parseInt(gundeicilentext.getText().toString());
                if(pfiyati != 0 && gundeicilen !=0)
                {
                    SharedPreferences.Editor editor = sharedpreferences.edit();


                    Map<String,Object> updates = new HashMap<>();
                    updates.put("gundeicilen",gundeicilen);
                    updates.put("paketfiyati",pfiyati);

                    //updates.put("lat",loc.getLatitude());
                    FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey,"")).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                    editor.putInt(gundeKey,gundeicilen);
                    editor.putInt(pakettekiKey,20);
                    editor.putFloat(paketfiyatiKey,  pfiyati);
                    editor.putLong(tarihKey,Calendar.getInstance().getTimeInMillis());
                    editor.commit();
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,new AnaSayfa()).commit();
                }
                else
                {
                    Toast.makeText(view.getContext(), "Paket Fiyatını ve Günde içilen sigara sayısını doldurun", Toast.LENGTH_SHORT).show();
                }

            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> updates = new HashMap<>();
                updates.put("birakma",0);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putLong(baslamakey,0);
                editor.commit();
                FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey,"")).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
                Intent intent = new Intent(view.getContext(),ResetActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    LoginActivity.mGoogleSignInClient.signOut().addOnCompleteListener( requireActivity(), new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(view.getContext(),LoginActivity.class);
                                    startActivity(intent);
                                    requireActivity().finish();
                                }
                            });

            }
        });


        pfiyatitext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE||(event.getAction()==1 && event.getKeyCode()==KeyEvent.KEYCODE_BACK)){
                    //Clear focus here from edittext
                    pfiyatitext.clearFocus();
                }
                return false;
            }
        });
        gundeicilentext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
                    gundeicilentext.clearFocus();
                    /*gundeicilentext.setFocusable(false);
                    gundeicilentext.setFocusableInTouchMode(false);
                    gundeicilentext.setFocusable(true);
                    gundeicilentext.setFocusableInTouchMode(true);*/

                }
                return false;
            }
        });


    }

    private void SetupBilling() {
        billingClient = BillingClient.newBuilder(requireActivity())
                .setListener(this)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NotNull BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d("T", "onBillingSetupFinished:succes ");
                    List<String> skuList = new ArrayList<>();
                    skuList.add("sigarayibirakpremium");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(@NotNull BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    // Process the result.
                                    /*;*/
                                    if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK && !getPurchaseValueFromPref())
                                    {
                                        Activity activity = getActivity();
                                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                                .setSkuDetails(skuDetailsList.get(0))
                                                .build();
                                        billingClient.launchBillingFlow(Objects.requireNonNull(activity), billingFlowParams);
                                    }
                                }

                            });
                }
                else
                {
                    Log.d("TAG", "onBillingSetupFinished:fail ");
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    void handlePurchase(Purchase purchase) {
        if (PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
        {

            // else purchase is valid
            //if item is purchased and not acknowledged
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase);
            }
            //else item is purchased and also acknowledged
            else {
                // Grant entitlement to the user on item purchase
                // restart activity
                if(!getPurchaseValueFromPref()){
                    savePurchaseValueToPref(true);
                    Map<String,Object> updates = new HashMap<>();
                    updates.put("paid",true);
                    FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey,"")).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                    Toast.makeText(getContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                    requireActivity().recreate();
                }
            }
        }
        //if purchase is pending
        else if( PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
        {
            Toast.makeText(getContext(),
                    "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
        }
        //if purchase is unknown
        else if(PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
        {
            savePurchaseValueToPref(false);
        }
    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        }
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if(alreadyPurchases!=null){
                handlePurchase(alreadyPurchases.get(0));
            }
        }
        /*else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }*/
    }

    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                //if purchase is acknowledged
                // Grant entitlement to the user. and restart activity
                savePurchaseValueToPref(true);
                Map<String,Object> updates = new HashMap<>();
                updates.put("paid",true);
                FirebaseFirestore.getInstance().collection("users").document(sharedpreferences.getString(idKey,"")).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
                Toast.makeText(getContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                requireActivity().recreate();
            }
        }
    };

    private void savePurchaseValueToPref(boolean value){
        requireContext().getSharedPreferences(MyPREFERENCES, 0).edit().putBoolean(PURCHASE_KEY,value).commit();
    }




}




