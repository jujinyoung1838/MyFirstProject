package com.example.firstproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainContent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);

        //제품 이름 받기
        TextView textView = findViewById(R.id.Name1);
        Intent intent = getIntent();
        String message = intent.getStringExtra("메세지");
        textView.setText(message);

        RecyclerView recyclerView2 = findViewById(R.id.recyclerView2);
        //세로 레이아웃 활용
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(layoutManager);
        MaindataAdapter adapter = new MaindataAdapter();

        //RecyclerView안에 data 넣기(사진,이름)
        adapter.addItem(new Maindata(R.drawable.ic_launcher_background,"1번"));
        adapter.addItem(new Maindata(R.drawable.ic_launcher_foreground,"2번"));
        adapter.addItem(new Maindata(R.drawable.ic_launcher_foreground,"3번"));
        adapter.addItem(new Maindata(R.drawable.ic_launcher_background,"4번"));

        recyclerView2.setAdapter(adapter);



    }
}