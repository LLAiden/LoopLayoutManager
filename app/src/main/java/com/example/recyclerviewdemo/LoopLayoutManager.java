package com.example.recyclerviewdemo;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class LoopLayoutManager extends RecyclerView.LayoutManager {

    private final int mOffsetX;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    public LoopLayoutManager() {
        this(0);
    }

    public LoopLayoutManager(int offsetX) {
        mOffsetX = offsetX;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (getItemCount() == 0) {
            return;
        }
        int recyclerViewHeight = getRecyclerViewHeight();
        int offsetX = mOffsetX;
        for (int i = 0; i < getItemCount(); i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int width = getDecoratedMeasuredWidth(view);
            int height = getDecoratedMeasuredHeight(view);
            layoutDecorated(view, offsetX, 0, offsetX + width, height);
            if (i != 1) {
                view.setScaleX(0.85F);
                view.setScaleY(0.85F);
            }
            offsetX += width;
            if (offsetX > recyclerViewHeight) {
                break;
            }
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //这个遍历 getChildCount() RecyclerView中显示的个数
        checkRecycler(dx, recycler);
        //从上往下滑动  底部会出现空白区域
        fillBlank(recycler, dx >= 0);
        offsetChildrenHorizontal(-dx);
        centerZoom();
        return dx;
    }

    private void centerZoom() {
        float midpoint = getWidth() / 2.f;
        float d0 = 0f;
        float d1 = 0.9f * midpoint;
        float s0 = 1f;
        float s1 = 1f - 0.15f;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null) {
                float childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2f;
                float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                child.setScaleX(scale);
                child.setScaleY(scale);
            }
        }
    }

    private void fillBlank(RecyclerView.Recycler recycler, boolean fillEnd) {
        if (getChildCount() == 0) return;
        if (fillEnd) {
            //填充尾部
            View anchorView = getChildAt(getChildCount() - 1);
            if (anchorView != null) {
                //获得最后一个可见的View的position
                int anchorPosition = getPosition(anchorView);
                //最后一个item的右边小于recyclerView的有边界表示存在空白需要继续添加View
                while (anchorView.getRight() < getWidth() - getPaddingRight()) {
                    //(anchorPosition + 1) % getItemCount()  小于就是是下一个正常的下标不然就是余数
                    int position = (anchorPosition + 1) % getItemCount();
                    if (position < 0) position += getItemCount();

                    View view = recycler.getViewForPosition(position);
                    addView(view);
                    measureChildWithMargins(view, 0, 0);

                    int left = anchorView.getRight();
                    int top = getPaddingTop();
                    int right = left + getDecoratedMeasuredWidth(view);
                    int bottom = top + getDecoratedMeasuredHeight(view) - getPaddingBottom();
                    layoutDecorated(view, left, top, right, bottom);
                    anchorView = view;
                }
            }
        } else {
            //填充首部
            View anchorView = getChildAt(0);
            if (anchorView != null) {
                int anchorPosition = getPosition(anchorView);
                while (anchorView.getLeft() > getPaddingLeft()) {
                    int position = (anchorPosition - 1) % getItemCount();
                    if (position < 0) position += getItemCount();
                    View view = recycler.getViewForPosition(position);
                    addView(view, 0);
                    measureChildWithMargins(view, 0, 0);
                    int right = anchorView.getLeft();
                    int top = getPaddingTop();
                    int left = right - getDecoratedMeasuredWidth(view);
                    int bottom = top + getDecoratedMeasuredHeight(view) - getPaddingBottom();
                    layoutDecorated(view, left, top, right, bottom);
                    anchorView = view;
                }
            }
        }
    }

    private void checkRecycler(int dx, RecyclerView.Recycler recycler) {
        //从最后一个可见的View进行遍历到0 进行回收
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childView = getChildAt(childCount);
            if (childView != null) {
                if (dx > 0) {
                    //从下往上
                    if (getDecoratedRight(childView) - dx < 0) {
                        Log.e("print", "scrollVerticallyBy: 从下往上 回收掉：" + childView + "---->" + childCount);
                        removeAndRecycleView(childView, recycler);
                    }
                } else {
                    //从上往下
                    //因为这个是要与getHeight进行比较所以要减去移动的dy
                    if (getDecoratedLeft(childView) - dx > getWidth() - getPaddingRight()) {
                        Log.e("print", "scrollVerticallyBy: 从上往下 回收掉：" + childView + "---->" + childCount);
                        removeAndRecycleView(childView, recycler);
                    }
                }
            }
        }
    }

    public int getRecyclerViewHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }
}
