package com.example.googlenearbymobile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button runBtn;
    Button cookieBtn;
    Button locationBtn;
    Map<String, String> cookies;
    String locationData;
    TextView textResult;

    ActivityResultLauncher<Intent> cookieFileLauncher;
    ActivityResultLauncher<Intent> locationFileLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runBtn = findViewById(R.id.runBtn);
        cookieBtn = findViewById(R.id.cookieBtn);
        locationBtn = findViewById(R.id.locationBtn);
        textResult = findViewById(R.id.resultText);

        // allow bad code practice
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        cookieFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Check if the result is OK
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Get the data from the result
                        Intent data = result.getData();
                        // Get the Uri of the selected file
                        assert data != null;
                        Uri uri = data.getData();
                        // Read the content of the file and display it in a TextView
                        try {
                            assert uri != null;
                            InputStream in = getContentResolver().openInputStream(uri);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line).append("\n");
                            }
                            reader.close();
                            assert in != null;
                            in.close();
                            String[] cookieLines = builder.toString().split("\n");
                            String[] currentColumns;
                            cookies = new HashMap<>();
                            for (String columns : cookieLines) {
                                if (!columns.startsWith("#") && !columns.isEmpty()) {
                                    currentColumns = columns.split("\t");
                                    // putting name and values into hashmap
                                    cookies.put(currentColumns[5], currentColumns[6]);
                                }
                            }
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        locationFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Check if the result is OK
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Get the data from the result
                        Intent data = result.getData();
                        // Get the Uri of the selected file
                        assert data != null;
                        Uri uri = data.getData();
                        // Read the content of the file and display it in a TextView
                        try {
                            assert uri != null;
                            InputStream in = getContentResolver().openInputStream(uri);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line).append("\n");
                            }
                            reader.close();
                            assert in != null;
                            in.close();
                            locationData = builder.toString();
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );


        cookieBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/plain");
            cookieFileLauncher.launch(intent);
        });

        locationBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/plain");
            locationFileLauncher.launch(intent);
        });

        runBtn.setOnClickListener(view -> {
            Intent serviceIntent = new Intent(MainActivity.this, LocationCheckService.class);
            serviceIntent.putExtra("Cookies", (Serializable) cookies);
            serviceIntent.putExtra("Locations", locationData);
            startForegroundService(serviceIntent);
        });
    }
}
