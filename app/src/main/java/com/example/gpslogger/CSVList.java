package com.example.gpslogger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVList extends AppCompatActivity {

                            @Override
                            protected void onCreate(Bundle savedInstanceState) {
                                super.onCreate(savedInstanceState);
                                setContentView(R.layout.activity_csvlist);
                                listAllFiles();
                            }

    private void listAllFiles() {
        ListView lv = findViewById(R.id.list_csv);
//        Log.i("LOGLOL: ", getExternalFilesDir(null).listFiles().toString());
        File file[] = getExternalFilesDir(null).listFiles();
        final String fileNames[] = new String[file.length];
        int cnt = 0;
        for (File _file : file) {
            fileNames[cnt++]  = _file.getName();
        }

        List fileNamesList = new ArrayList(Arrays.asList(fileNames));
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, fileNamesList);
        lv.setAdapter(listAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent2 = new Intent(getApplicationContext(), GoogleMapsActivity.class);
                intent2.putExtra("fileName", fileNames[i]);
                startActivity(intent2);
            }
        });
    }
}