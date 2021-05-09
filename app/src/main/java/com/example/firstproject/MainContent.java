package com.example.firstproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainContent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);

        TextView textView = findViewById(R.id.test01);
        Intent intent = getIntent();
        String message = intent.getStringExtra("메세지");
        textView.setText(message);
    }
}