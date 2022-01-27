package com.cotyoragames.sigarayibirak;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class StatusAdapter extends BaseAdapter {
    private ArrayList<StatusClass> statusList;
    private Context context;
    private LayoutInflater layoutInflater;

    public StatusAdapter(ArrayList<StatusClass> statusList, Context context) {
        this.statusList = statusList;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return statusList.size();
    }

    @Override
    public Object getItem(int position) {
        return statusList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView = layoutInflater.inflate(R.layout.statuslistview_satir,null);
        TextView tarih = (TextView) customView.findViewById(R.id.satussatirTarih);
        TextView desc = (TextView) customView.findViewById(R.id.satussatirStatus);
        LinearLayout lay = (LinearLayout) customView.findViewById(R.id.statussatirlinear);
        if(statusList.get(position).getStatus() == 5)
        {
            lay.setBackgroundColor(Color.parseColor("#808CD47E"));
            desc.setText(context.getString(R.string.sahane));
        }
        else if (statusList.get(position).getStatus() == 4)
        {
            lay.setBackgroundColor(Color.parseColor("#807ABD7E"));
            desc.setText(context.getString(R.string.iyi));
        }
        else if (statusList.get(position).getStatus() == 3)
        {
            lay.setBackgroundColor(Color.parseColor("#80F8D66D"));
            desc.setText(context.getString(R.string.orta));
        }
        else if (statusList.get(position).getStatus() == 2)
        {
            lay.setBackgroundColor(Color.parseColor("#80FFB54C"));
            desc.setText(context.getString(R.string.kotu));
        }
        else if (statusList.get(position).getStatus() == 1)
        {
            lay.setBackgroundColor(Color.parseColor("#80FF6961"));
            desc.setText(context.getString(R.string.berbat));
        }


        tarih.setText(statusList.get(position).getTarih());
        //desc.setText(String.valueOf(arzuList.get(position).getStatus()));

        return customView;
    }
}
