package com.wufeng.latte_core.control;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int leftSpace;
    private int rightSpace;
    private int topSpace;
    private int bottomSpace;

    public SpaceItemDecoration(int left, int top, int right, int bottom){
        leftSpace = left;
        topSpace = top;
        rightSpace = right;
        bottomSpace = bottom;
    }

    public SpaceItemDecoration(int space){
        leftSpace = space;
        topSpace = space;
        rightSpace = space;
        bottomSpace = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = leftSpace;
        outRect.right = rightSpace;
        outRect.top = topSpace;
        outRect.bottom = bottomSpace;
    }
}
