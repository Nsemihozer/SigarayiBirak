package com.cotyoragames.sigarayibirak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cotyoragames.Basarim;

import java.util.ArrayList;

public class BasarimAdapter extends BaseAdapter {
    private ArrayList<Basarim> basarimArrayList;
    private Context context;
    private LayoutInflater layoutInflater;

    public BasarimAdapter(ArrayList<Basarim> basarimArrayList, Context context) {
        this.basarimArrayList = basarimArrayList;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return basarimArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return basarimArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView = layoutInflater.inflate(R.layout.basarim_satir,null);
        ProgressBar progressBar = (ProgressBar) customView.findViewById(R.id.basarimProgress);
        TextView title = (TextView) customView.findViewById(R.id.basarimTitle);
        TextView desc = (TextView) customView.findViewById(R.id.basarimDesc);
        TextView pb = (TextView) customView.findViewById(R.id.textView);
        ImageView done= (ImageView)  customView.findViewById(R.id.basarimImage);
        if(basarimArrayList.get(position).isDone())
        {
            done.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.INVISIBLE);
        }
        else
        {
            done.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            pb.setVisibility(View.VISIBLE);
            pb.setText(basarimArrayList.get(position).getProgress()+"%");
            progressBar.setProgress(basarimArrayList.get(position).getProgress());
        }
        progressBar.setProgress(basarimArrayList.get(position).getProgress());
        title.setText(basarimArrayList.get(position).getAd());
        desc.setText(basarimArrayList.get(position).getDesc());

        return customView;
    }
}
