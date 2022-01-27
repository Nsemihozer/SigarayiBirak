package com.cotyoragames.sigarayibirak;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cotyoragames.Basarim;

import java.util.ArrayList;
import java.util.Calendar;

public class ArzuAdapter  extends BaseAdapter {
    private ArrayList<ArzuClass> arzuList;
    private Context context;
    private LayoutInflater layoutInflater;

    public ArzuAdapter(ArrayList<ArzuClass> arzuList, Context context) {
        this.arzuList = arzuList;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arzuList.size();
    }

    @Override
    public Object getItem(int position) {
        return arzuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView = layoutInflater.inflate(R.layout.arzularlistview_satir,null);
        TextView tarih = (TextView) customView.findViewById(R.id.arzusatirTarih);
        TextView desc = (TextView) customView.findViewById(R.id.arzusatirStatus);
        LinearLayout lay = (LinearLayout) customView.findViewById(R.id.arzusatirlinear);
        if(arzuList.get(position).status == 5)
        {
            lay.setBackgroundColor(Color.parseColor("#80FF6961"));
            desc.setText(context.getString(R.string.cok_guclu));
        }
        else if (arzuList.get(position).status == 4)
        {
            lay.setBackgroundColor(Color.parseColor("#80FFB54C"));
            desc.setText(context.getString(R.string.guclu));
        }
        else if (arzuList.get(position).status == 3)
        {
            lay.setBackgroundColor(Color.parseColor("#80F8D66D"));
            desc.setText(context.getString(R.string.orta));
        }
        else if (arzuList.get(position).status == 2)
        {
            lay.setBackgroundColor(Color.parseColor("#807ABD7E"));
            desc.setText(context.getString(R.string.az));
        }
        else if (arzuList.get(position).status == 1)
        {
            lay.setBackgroundColor(Color.parseColor("#808CD47E"));
            desc.setText(context.getString(R.string.cok_az));
        }
        else if (arzuList.get(position).status == 0)
        {
            lay.setBackgroundColor(Color.parseColor("#80B0F5AB"));
            desc.setText(context.getString(R.string.yok_));
        }
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(arzuList.get(position).getTarih());
        tarih.setText(calendar.get(Calendar.DAY_OF_MONTH)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR));
        //desc.setText(String.valueOf(arzuList.get(position).getStatus()));

        return customView;
    }
}
