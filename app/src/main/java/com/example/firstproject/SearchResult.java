package com.example.firstproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SearchResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //region RecyclerView 활용
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //세로 레이아웃 활용
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        CSdataAdapter adapter = new CSdataAdapter();

        //RecyclerView안에 data 넣기(사진,이름)
        adapter.addItem(new CSdata(R.drawable.ic_launcher_background,"1번"));
        adapter.addItem(new CSdata(R.drawable.ic_launcher_foreground,"2번"));
        adapter.addItem(new CSdata(R.drawable.ic_launcher_foreground,"3번"));
        adapter.addItem(new CSdata(R.drawable.ic_launcher_background,"4번"));

        recyclerView.setAdapter(adapter);

        //View클릭 시 상세정보창 이동
        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(CSdataAdapter.ViewHolder holder, View view, int position) {
                Toast.makeText(getApplicationContext(), "에러 확인용"  ,Toast.LENGTH_LONG).show();
                //클릭한 item의 position값(정보) 받아오기
                CSdata item = adapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(),MainContent.class);
                intent.putExtra("메세지",item.getName());
                startActivityForResult(intent,MainActivity.MAINCONTENT_CODE);

                Toast.makeText(getApplicationContext(),"아이템 선택 " + item.getName(), Toast.LENGTH_LONG).show();

            }
        });
        //endregion
    }
}