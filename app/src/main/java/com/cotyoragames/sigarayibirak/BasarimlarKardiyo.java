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

public class BasarimlarKardiyo extends Fragment {

    BasarimAdapter basarimkardiyoAdapter;
    ListView basarimkardiyoListesi;
    public static ArrayList<Basarim> basarimlarkardiyo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basarimlar_kardiyo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        basarimlarkardiyo=new ArrayList<>();
        for (Basarim basarim : basarimlar) {
            if (basarim.getType().equals("Kardiyovasküler")) {
                basarimlarkardiyo.add(basarim);
            }
        }
        basarimkardiyoAdapter=new BasarimAdapter(basarimlarkardiyo,requireContext());
        basarimkardiyoListesi=view.findViewById(R.id.basarimkardiyoList);
        basarimkardiyoListesi.setAdapter(basarimkardiyoAdapter);
    }
}