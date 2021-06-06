package com.example.firstproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CSdataAdapter extends RecyclerView.Adapter<CSdataAdapter.ViewHolder> implements OnRecyclerItemClickListener{
    ArrayList<CSdata> items = new ArrayList<CSdata>();
    static OnRecyclerItemClickListener listener;

    public void addItem(CSdata item){
        items.add(item);
    }

    public void setItems(ArrayList<CSdata> items){
        this.items = items;
    }

    public CSdata getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, CSdata item){
        items.set(position, item);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //뷰홀더 생성시 자동호출
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recyclerview_item,parent,false);

        return new ViewHolder(itemView);
        //아이템 뷰로 사용될 xml infalte 시킴
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //뷰홀더 재사용
        CSdata item = items.get(position);
        //카드뷰에 이미지,텍스트 값
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

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if(listener!=null){
            listener.onItemClick(holder,view,position);
        }
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView CSImage;
        TextView CSName;

        //ViewHolder생성 + 클릭 이벤트(자세한 값은 SearchResult에서 조정)
        public ViewHolder(View itemView){
            super(itemView);

            CSName = itemView.findViewById(R.id.CSName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setItem(CSdata item){
            CSName.setText(item.getName());
        }
    }

}
