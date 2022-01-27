package com.cotyoragames.sigarayibirak;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.cotyoragames.Basarim;

import java.util.ArrayList;
import java.util.Iterator;

import static com.cotyoragames.sigarayibirak.MainActivity.basarimlar;


public class BasarimlarBeyin extends Fragment {
    BasarimAdapter basarimbeyinAdapter;
    ListView basarimbeyinListesi;
    public static ArrayList<Basarim> basarimlarbeyin;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basarimlar_beyin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        basarimlarbeyin=new ArrayList<>();
        for (Basarim basarim : basarimlar) {
            if (basarim.getType().equals("Beyin")) {
                basarimlarbeyin.add(basarim);
            }
        }
        basarimbeyinAdapter=new BasarimAdapter(basarimlarbeyin,requireContext());
        basarimbeyinListesi=view.findViewById(R.id.basarimbeyinList);
        basarimbeyinListesi.setAdapter(basarimbeyinAdapter);
    }
}