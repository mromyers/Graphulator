package com.example.mrm.views;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mrm.graphulator.R;

public class FunctionListAdapter extends ArrayAdapter<String>{
    private final Context context;
    private final String[] slotText;
    private int[] slotColors;

    public FunctionListAdapter(Context context, String[] slotText, int[] slotColors) {
        super(context, -1, slotText);
        this.context = context;
        this.slotText = slotText;
        this.slotColors = slotColors;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.function_list_row_layout, parent,false);
        TextView textView = (TextView) rowView.findViewById(R.id.textView);
        ColorView colorView = (ColorView) rowView.findViewById(R.id.colorView);
        textView.setText(y_sub(position) +" = "+ slotText[position]);
        colorView.setColor(slotColors[position]);


        return rowView;

    }

    private String y_sub(int i){
        String[] subs = {"₀","₁","₂","₃","₄","₅","₆","₇","₈","₉"};
        String str = "";
        boolean cont = true;
        do{ str = subs[i % 10] + str;
            i = i/10;
        }while(0 < i);
        return "y" + str;
    }


}