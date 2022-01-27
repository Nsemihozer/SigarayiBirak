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

public class BasarimlarCiger extends Fragment {
    BasarimAdapter basarimcigerAdapter;
    ListView basarimcigerListesi;
    public static ArrayList<Basarim> basarimlarciger;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basarimlar_ciger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        basarimlarciger=new ArrayList<>();
        for (Basarim basarim : basarimlar) {
            if (basarim.getType().equals("Ciğer")) {
                basarimlarciger.add(basarim);
            }
        }
        basarimcigerAdapter=new BasarimAdapter(basarimlarciger,requireContext());
        basarimcigerListesi=view.findViewById(R.id.basarimcigerList);
        basarimcigerListesi.setAdapter(basarimcigerAdapter);
    }
}