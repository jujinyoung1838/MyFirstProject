package com.example.firstproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MaindataAdapter extends RecyclerView.Adapter<MaindataAdapter.ViewHolder> {
    ArrayList<Maindata> items = new ArrayList<Maindata>();
    static OnRecyclerItemClickListener listener;

    public void addItem(Maindata item){
        items.add(item);
    }

    public void setItems(ArrayList<Maindata> items){
        this.items = items;
    }

    public Maindata getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, Maindata item){
        items.set(position, item);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //뷰홀더 생성시 자동호출
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.maincontent_item,parent,false);

        return new ViewHolder(itemView);
        //아이템 뷰로 사용될 xml infalte 시킴
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //뷰홀더 재사용
        Maindata item = items.get(position);
        //카드뷰에 이미지,텍스트 값
        holder.CSImage.setImageResource(item.getCSImage());
        holder.CSName.setText(item.getName());
    }

    //data set 전체크기
    @Override
    public int getItemCount() {
        return items.size();
    }

    //클릭 이벤트
    public void setOnItemClickListener(OnRecyclerItemClickListener listener){
        this.listener = listener;
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView CSImage;
        TextView CSName;

        //ViewHolder생성 + 클릭 이벤트(자세한 값은 SearchResult에서 조정)
        public ViewHolder(View itemView){
            super(itemView);

            CSImage = itemView.findViewById(R.id.CSImage);
            CSName = itemView.findViewById(R.id.CSName);

        }
        public void setItem(CSdata item) {
            CSImage.setImageResource(item.getCSImage());
            CSName.setText(item.getName());
        }
    }

}

