package com.example.mrm.graphulator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewDebug;
import android.widget.EditText;
import android.widget.GridView;

import com.example.mrm.views.ColorAdapter;


public class GraphOptionsActivity extends Activity {
    public final static String GRAPH_WINDOW = "com.example.mrm.graphulator.MainActivity.GRAPH_WINDOW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_options);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        Intent intent = getIntent();
        float[] window = intent.getFloatArrayExtra(MainActivity.GRAPH_WINDOW);
        EditText x_min = (EditText) findViewById(R.id.x_min);
        EditText x_max = (EditText) findViewById(R.id.x_max);
        EditText y_min = (EditText) findViewById(R.id.y_min);
        EditText y_max = (EditText) findViewById(R.id.y_max);
        EditText line_weight = (EditText) findViewById(R.id.line_weight);
        EditText epsilon = (EditText) findViewById(R.id.epsilon);
        x_min.setText(Float.toString(window[0]));
        x_max.setText(Float.toString(window[1]));
        y_min.setText(Float.toString(window[2]));
        y_max.setText(Float.toString(window[3]));
        epsilon.setText(Float.toString(window[4]));
        line_weight.setText(Float.toString(0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph_options, menu);
        return true;
    }

    void returnResult(){
        Intent intent = new Intent();
        EditText x_min = (EditText) findViewById(R.id.x_min);
        EditText x_max = (EditText) findViewById(R.id.x_max);
        EditText y_min = (EditText) findViewById(R.id.y_min);
        EditText y_max = (EditText) findViewById(R.id.y_max);
        EditText epsilon = (EditText) findViewById(R.id.epsilon);
        EditText line_weight = (EditText) findViewById(R.id.line_weight);
        try {
            float[] window = {
                    Float.parseFloat(x_min.getText().toString()),
                    Float.parseFloat(x_max.getText().toString()),
                    Float.parseFloat(y_min.getText().toString()),
                    Float.parseFloat(y_max.getText().toString()),
                    Float.parseFloat(epsilon.getText().toString()),
                    Float.parseFloat(line_weight.getText().toString())
            };
            intent.putExtra(GRAPH_WINDOW, window);
            setResult(RESULT_OK, intent);
        } finally {

            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home){
            returnResult();
            return true;

        }else if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        returnResult();
    }

}


