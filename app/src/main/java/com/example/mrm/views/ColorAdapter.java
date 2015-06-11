package com.example.mrm.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ColorAdapter extends BaseAdapter{
    Context context;
    int[] colors;

    public ColorAdapter(Context context, int[] colors){
        this.context = context; this.colors = colors;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ColorView colorView = new ColorView(context);
        colorView.setColor(colors[position]);
        return colorView;
    }
    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
