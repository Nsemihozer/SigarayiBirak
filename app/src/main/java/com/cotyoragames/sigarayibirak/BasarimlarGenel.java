package com.cotyoragames.sigarayibirak;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cotyoragames.Basarim;

import java.util.ArrayList;
import java.util.Iterator;

import static com.cotyoragames.sigarayibirak.MainActivity.basarimlar;


public class BasarimlarGenel extends Fragment {
    BasarimAdapter basarimgenelAdapter;
    ListView basarimgenelListesi;
    public static ArrayList<Basarim> basarimlargenel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basarimlar_genel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        basarimlargenel=new ArrayList<>();
        for (Basarim basarim : basarimlar) {
            if (basarim.getType().equals("Genel")) {
                basarimlargenel.add(basarim);
            }
        }
        basarimgenelAdapter=new BasarimAdapter(basarimlargenel,requireContext());
        basarimgenelListesi=view.findViewById(R.id.basarimgenelList);
        basarimgenelListesi.setAdapter(basarimgenelAdapter);
    }
}