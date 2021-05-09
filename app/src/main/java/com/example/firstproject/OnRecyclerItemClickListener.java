package com.example.firstproject;

import android.view.View;

public interface OnRecyclerItemClickListener {
    public void onItemClick(RecyclerViewAdapter.ViewHolder holder, View view, int position);
}
