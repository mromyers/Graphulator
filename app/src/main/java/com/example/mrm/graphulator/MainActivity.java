package com.example.mrm.graphulator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;

import com.example.mrm.database.DatabaseManager;
import com.example.mrm.views.GraphView;

import java.util.LinkedList;

public class MainActivity extends Activity {
    public final static String GRAPH_WINDOW = "com.example.mrm.graphulator.MainActivity.GRAPH_WINDOW";
    public final static String SLOTS_TEXT = "com.example.mrm.graphulator.MainActivity.SLOTS_TEXT";
    public final static String ACTIVE_SLOTS = "com.example.mrm.graphulator.MainActivity.ACTIVE_SLOTS";
    public final static String SLOT_COLORS = "com.example.mrm.graphulator.MainActivity.SLOT_COLORS";

    public final static int WINDOW_OPTION_REQUEST = 1;
    public final static int FUNCTION_LIST_REQUEST = 2;
    public final static String PREFS_NAME = ".config";
    private DatabaseManager databaseManager;
    public int curSlot;
    public boolean funMode;
    private GraphView graphView;
    private Spinner spinner;
    private EditText inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        curSlot = 0;

        loadSettings();
        inputText = (EditText) findViewById(R.id.inputText);
        graphView = (GraphView) findViewById(R.id.graph);
        ActionBar actionBar = getActionBar();
        final GridView gridView = (GridView) findViewById(R.id.gridView);
        spinner = (Spinner) findViewById(R.id.spinner);
        final String[] buttonText1 = new String[]{
                "+", "-", "*", "/", "^", "fun",
                "1", "2", "3", "4", "5", "clr",
                "6", "7", "8", "9", "0", "del",
                "(", ")", ".", " ", "x", "<-'"
        };

        final String[] buttonText2 = new String[]{
                "sin", "cos", "tan", "abs", "sqrt", "op",
                "1", "2", "3", "4", "5", "clr",
                "6", "7", "8", "9", "0", "del",
                "(", ")", ".", " ", "x", "<-'"
        };
        final String[] spinnerText = y_subs(9);

        final ArrayAdapter<String> gridAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, buttonText1);

        final ArrayAdapter<String> gridAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, buttonText2);


        gridView.setAdapter(gridAdapter1);
        funMode = false;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                switch (buttonText1[position]) {
                    case "del":
                        CharSequence tstring = inputText.getText();
                        inputText.setText((tstring.length() == 0) ? "" :
                                inputText.getText().subSequence(0, tstring.length() - 1).toString());
                        break;
                    case "<-'":
                        graphView.setSlot(curSlot, inputText.getText().toString());
                        break;
                    case "clr":
                        inputText.setText("");
                        graphView.clearSlot(curSlot);
                        break;
                    case "fun":
                        funMode = !funMode;
                        gridView.setAdapter(funMode ? gridAdapter2 : gridAdapter1);
                        break;
                    default:
                        inputText.setText(inputText.getText() +
                                (funMode ? buttonText2[position] : buttonText1[position]));
                        break;

                }

            }
        });

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, spinnerText);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                curSlot = pos;
                inputText.setText(graphView.getSlotText(curSlot));
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openWindowSettings();
                return true;
            case R.id.fun_list:
                openFunList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openWindowSettings() {
        Intent intent = new Intent(this, GraphOptionsActivity.class);
        final GraphView graphView = (GraphView) findViewById(R.id.graph);
        float[] window = {graphView.x_min, graphView.x_max, graphView.y_min, graphView.y_max, graphView.epsilon};
        intent.putExtra(GRAPH_WINDOW, window);
        startActivityForResult(intent, WINDOW_OPTION_REQUEST);
    }

    public void openFunList() {
        Intent intent = new Intent(this, FunctionListActivity.class);
        final GraphView graphView = (GraphView) findViewById(R.id.graph);
        String[] slotsText = graphView.getSlotsText();
        boolean[] activeSlots = graphView.getActiveSlots();
        int[] slotColors = graphView.getSlotColors();
        intent.putExtra(SLOTS_TEXT, slotsText);
        intent.putExtra(ACTIVE_SLOTS, activeSlots);
        intent.putExtra(SLOT_COLORS, slotColors);
        startActivityForResult(intent, FUNCTION_LIST_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case WINDOW_OPTION_REQUEST:
                float[] window = intent.getFloatArrayExtra(GraphOptionsActivity.GRAPH_WINDOW);
                final GraphView graphView = (GraphView) findViewById(R.id.graph);
                graphView.x_min = window[0];
                graphView.x_max = window[1];
                graphView.y_min = window[2];
                graphView.y_max = window[3];
                graphView.epsilon = window[4];
                break;
            case FUNCTION_LIST_REQUEST:
                if(resultCode == RESULT_OK) {
                    int returnMode = intent.getIntExtra(FunctionListActivity.RETURN_MODE, -1);
                    switch (returnMode) {
                        case -1:
                            break;
                        case FunctionListActivity.SIMPLE_RETURN:
                            updateFunList(intent);
                            break;
                        case FunctionListActivity.EDIT_RETURN:
                            curSlot = intent.getIntExtra(FunctionListActivity.EDIT_SLOT, curSlot);
                            updateFunList(intent);
                            break;
                    }
                }
                break;

        }
    }
    void updateFunList(Intent intent){
        boolean[] changedSlots = intent.getBooleanArrayExtra(FunctionListActivity.CHANGED_SLOTS),
                  clearedSlots = intent.getBooleanArrayExtra(FunctionListActivity.CLEARED_SLOTS);
        int[] slotColors = intent.getIntArrayExtra(FunctionListActivity.SLOT_COLORS);
        for(int i = 0; i < changedSlots.length; i++)
            if(changedSlots[i]) {
                graphView.setSlotColor(i, slotColors[i]);
                if (clearedSlots[i])
                    graphView.clearSlot(i);
            }
                spinner.setSelection(curSlot);
                inputText.setText(graphView.getSlotText(curSlot));

    }

    void saveSettings() {
        final GraphView graphView = (GraphView) findViewById(R.id.graph);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("x_min", graphView.x_min);
        editor.putFloat("x_max", graphView.x_max);
        editor.putFloat("y_min", graphView.y_min);
        editor.putFloat("y_max", graphView.y_max);
        editor.putFloat("epsilon", graphView.epsilon);

        editor.commit();
        databaseManager = new DatabaseManager(this);
        databaseManager.save(graphView.getSlots());
        databaseManager.close();
    }


    void loadSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final GraphView graphView = (GraphView) findViewById(R.id.graph);
        graphView.x_min = settings.getFloat("x_min", -20f);
        graphView.x_max = settings.getFloat("x_max", 20f);
        graphView.y_min = settings.getFloat("y_min", -10f);
        graphView.y_max = settings.getFloat("y_max", 10f);
        graphView.epsilon = settings.getFloat("epsilon", 0.1f);
        databaseManager = new DatabaseManager(this);
        LinkedList<ContentValues> list = databaseManager.getRows();
            graphView.setSlots(list);
        databaseManager.close();

    }


    @Override
    protected void onStop() {
        super.onStop();
        saveSettings();

    }

    String y_sub(int i) {
        String[] subs = {"₀", "₁", "₂", "₃", "₄", "₅", "₆", "₇", "₈", "₉" };
        String str = "";
        boolean cont = true;
        do {
            str = subs[i % 10] + str;
            i = i / 10;
        } while (0 < i);
        return "y" + str;
    }

    String[] y_subs(int i) {
        String[] ys = new String[i + 1];
        for (int j = 0; j <= i; j++)
            ys[j] = y_sub(j);
        return ys;
    }

}