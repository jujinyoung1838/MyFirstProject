package com.example.firstproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Person;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SearchResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //region RecyclerView 활용
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter();

        //RecyclerView안에 data 넣기(사진,이름)
        adapter.addItem(new com.example.firstproject.RecyclerView(R.drawable.ic_launcher_background,"1번"));
        adapter.addItem(new com.example.firstproject.RecyclerView(R.drawable.ic_launcher_foreground,"2번"));
        adapter.addItem(new com.example.firstproject.RecyclerView(R.drawable.ic_launcher_foreground,"3번"));
        adapter.addItem(new com.example.firstproject.RecyclerView(R.drawable.ic_launcher_background,"4번"));

        recyclerView.setAdapter(adapter);

        //View클릭 시 상세정보창 이동
        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewAdapter.ViewHolder holder, View view, int position) {
                //클릭한 item의 position값(정보) 받아오기
                com.example.firstproject.RecyclerView item = adapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(),MainContent.class);
                intent.putExtra("메세지",item.getName());
                startActivityForResult(intent,MainActivity.MAINCONTENT_CODE);
                Toast.makeText(getApplicationContext(),"아이템 선택 " + item.getName(), Toast.LENGTH_LONG).show();

            }
        });
        //endregion
    }
}