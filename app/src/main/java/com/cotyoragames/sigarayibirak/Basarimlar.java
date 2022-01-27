package com.cotyoragames.sigarayibirak;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class Basarimlar extends Fragment {
    BasarimAdapter basarimAdapter;
    ListView basarimListesi;

    public static final String MyPREFERENCES = "MyPrefs",baslamakey = "baslamaKey", gundeKey="gundeKey",pakettekiKey="pakettekiKey",paketfiyatiKey="paketfiyatiKey";
    SharedPreferences sharedPreferences;
    long baslama;
    int günde,paketteki;
    float paketfiyatı;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_basarimlar,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        baslama=sharedPreferences.getLong(baslamakey,0);

        //basarimAdapter=new BasarimAdapter(MainActivity.basarimlar,getContext());
        //basarimListesi=view.findViewById(R.id.basarimbeyinList);
        //basarimListesi.setAdapter(basarimAdapter);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(new BasarimlarBeyin(), view.getContext().getString(R.string.beyin));
        adapter.addFragment(new BasarimlarCiger(), view.getContext().getString(R.string.ciger));
        adapter.addFragment(new BasarimlarKardiyo(), view.getContext().getString(R.string.kardiyo));
        adapter.addFragment(new BasarimlarGenel(), view.getContext().getString(R.string.genel));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

}
