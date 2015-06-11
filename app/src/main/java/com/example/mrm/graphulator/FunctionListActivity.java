package com.example.mrm.graphulator;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mrm.views.ColorPickerDialog;
import com.example.mrm.views.ColorPickerDialogReceiver;
import com.example.mrm.views.ColorView;
import com.example.mrm.views.FunctionListAdapter;


public class FunctionListActivity extends Activity {
    boolean[] changedSlots;
    boolean[] clearedSlots;
    int[] slotColors;
    String[] slotsText;
    ListView listView;
    public final static String
            SLOT_COLORS = "com.example.mrm.graphulator.FunctionListActivity.SLOT_COLORS",
            CHANGED_SLOTS = "com.example.mrm.graphulator.FunctionListActivity.CHANGED_SLOTS",
            CLEARED_SLOTS = "com.example.mrm.graphulator.FunctionListActivity.CLEARED_SLOTS",
            RETURN_MODE = "com.example.mrm.graphulator.FunctionListActivity.RETURN_MODE",
            EDIT_SLOT = "com.example.mrm.graphulator.FunctionListActivity.EDIT_SLOT";

    public final static int EDIT_RETURN = 1, SIMPLE_RETURN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equation_list);
        Intent intent = getIntent();
        slotsText = intent.getStringArrayExtra(MainActivity.SLOTS_TEXT);
        slotColors = intent.getIntArrayExtra(MainActivity.SLOT_COLORS);
        changedSlots = new boolean[slotsText.length];
        clearedSlots = new boolean[slotsText.length];

        for(int i = 0; i < slotsText.length; i++){
            changedSlots[i] = false;
            clearedSlots[i] = false;
        }

        listView = (ListView) findViewById(R.id.listView);
        FunctionListAdapter functionListAdapter = new FunctionListAdapter(this, slotsText,slotColors );
        listView.setAdapter(functionListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editReturn(position);
            }
        });

        registerForContextMenu(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_function_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                simpleReturn();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.function_list_context_menu, menu);

    }

    @Override
    public  boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.Color:
                final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
                colorPickerDialog.setPos(info.position);
                colorPickerDialog.setReceiver(new ColorPickerDialogReceiver() {
                                                  @Override
                                                  public void sendBack(int pos, int i) {
                                                      setColor(pos, i);
                                                      colorPickerDialog.dismiss();
                                                  }
                                              }

                );
                FragmentManager manager = getFragmentManager();
                colorPickerDialog.show(manager, "Pick a Color");
                return true;
            case R.id.Edit:
                editReturn(info.position);
                return true;
            case R.id.Clear:
                clearSlot(info.position);
                return true;
            default:
                return true;

        }
    }


    public void putChanged(Intent intent){
        intent.putExtra(SLOT_COLORS, slotColors);
        intent.putExtra(CHANGED_SLOTS, changedSlots);
        intent.putExtra(CLEARED_SLOTS, clearedSlots);
    }

    public void editReturn(int i){
        Intent intent = new Intent();
        putChanged(intent);
        intent.putExtra(RETURN_MODE, EDIT_RETURN);
        intent.putExtra(EDIT_SLOT, i);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void simpleReturn(){
        Intent intent = new Intent();
        putChanged(intent);
        intent.putExtra(RETURN_MODE, SIMPLE_RETURN);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void setColor(int slot, int color){
        View rowView = listView.getChildAt(slot);
        ColorView colorView = (ColorView) rowView.findViewById(R.id.colorView);
        colorView.setColor(color);
        colorView.invalidate();
        changedSlots[slot] = true;
        slotColors[slot] = color;
    }
    public void clearSlot(int slot){
        View rowView = listView.getChildAt(slot);
        TextView textView = (TextView) rowView.findViewById(R.id.textView);
        textView.setText("");
    }

    @Override
    public void onBackPressed(){
        simpleReturn();
    }
}
