package com.cotyoragames;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cotyoragames.sigarayibirak.Hedef;
import com.cotyoragames.sigarayibirak.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HedefAdapter extends BaseAdapter {
    private ArrayList<Hedef> hedefArrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    public HedefAdapter(ArrayList<Hedef> hedefArrayList, Context context) {
        this.hedefArrayList = hedefArrayList;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return hedefArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return hedefArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView = layoutInflater.inflate(R.layout.hedef_satir,null);

        TextView title = (TextView) customView.findViewById(R.id.hedefTitleText);
        TextView desc = (TextView) customView.findViewById(R.id.hedefDescText);

        title.setText(hedefArrayList.get(position).getName());
        desc.setText("Sigara içmeyerek "+hedefArrayList.get(position).getMiktar()+" biriktirerek bu hedefe ulaştınız");
        return customView;

    }
}
