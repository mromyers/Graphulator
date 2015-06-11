package com.example.mrm.views;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.mrm.graphulator.R;

public class ColorPickerDialog extends DialogFragment {
    public final int[] colors =
            {Color.RED,Color.rgb(255, 153, 0),Color.rgb(255,127,0),Color.rgb(255, 126, 0),
                    Color.YELLOW,Color.rgb(204, 255, 51),Color.GREEN,Color.rgb(51, 153, 102),
                    Color.CYAN, Color.BLUE,Color.rgb(153, 102, 255),Color.rgb(204, 0, 255),
                    Color.rgb(184, 77, 184),Color.rgb(204, 0, 102),Color.MAGENTA,Color.rgb(255, 166, 201)};
    public static final String COLOR = "com.example.mrm.views.ColorPickerDialog.COLOR";
    public static final String POS_CALLER = "com.example.mrm.views.ColorPickerDialog.POS_CALLER";
    public static final int CPD_REQUEST = 1;
    ColorPickerDialogReceiver receiver;

    public View view;

    int pos;

    public ColorPickerDialog(){;
    }

    public int getCallerPos(){
        return pos;
    }

    public void setPos(int pos){
        this.pos = pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.color_picker_fragment, container);
        GridView gridView = (GridView) view.findViewById(R.id.colorGridView);
        gridView.setAdapter(new ColorAdapter(getActivity(), colors));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                getReceiver().sendBack(getPos(), colors[pos]);

            }
        });
        getDialog().setTitle("Pick a Color:");
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setReceiver(ColorPickerDialogReceiver receiver){
        this.receiver = receiver;
    }
    public ColorPickerDialogReceiver getReceiver(){return receiver;}
    public int getPos(){return pos;}


}
