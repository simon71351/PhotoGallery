package com.bignerdranch.android.photogallery;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by simon on 4/25/16.
 */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    //The minimum amount of items to have below your current scroll position before loading more
    private int visibleThreshold = 5;

    //Current offset index of data you have loaded
    private int currentPage = 1;

    //Total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;

    //True if we are still waiting for the last set of data to load
    private boolean loading = false;

    private int startingPageIndex = 1;

    RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions){
        int maxSize = 0;

        for(int i = 0; i < lastVisibleItemPositions.length; i++){
            if(i == 0){
                maxSize = lastVisibleItemPositions[i];
            }
            else if(maxSize < lastVisibleItemPositions[i]){
                maxSize = lastVisibleItemPositions[i];
            }
        }

        return maxSize;
    }

    //This happens many times a second during a scroll, so be wary of the code you place here.
    //We are given a few useful parameters to help us work out if we need to load somemore here.
    //but first check if we are waiting for the previous load to finish.

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = mLayoutManager.getItemCount();


        if(mLayoutManager instanceof StaggeredGridLayoutManager){
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            //get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        }
        else if(mLayoutManager instanceof LinearLayoutManager){
            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }
        else if(mLayoutManager instanceof GridLayoutManager){
            lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }

        if(totalItemCount < previousTotalItemCount){
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;

            if(totalItemCount == 0){
                this.loading = true;
            }
        }

        //If it's still loading, we check to see if the dataset count has changed, if so we conclude it has finished loading and update the current page number
        //and total item count.
        if(loading && (totalItemCount > previousTotalItemCount)){
            loading = false;
            previousTotalItemCount = totalItemCount;
            Log.i("ScrollListener", "New Page Loaded");
        }

        //If it isn't currently loading, we check to see if we have breached
        //the visibleThreshold and need to reload more data.
        //If we do need to reload some more data, we execute onLoadMore to fetch the data.
        //threshold should reflect how many total columns there are too.
        if(!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount){
            currentPage++;
            onLoadMore(currentPage, totalItemCount, visibleThreshold);
            loading = true;
            Log.i("ScrollListener", "New Page Loading");
            Log.i("ScrollListener", "Visible Threshold: "+visibleThreshold);
        }


    }

    public abstract void onLoadMore(int page,int totalItemsCount, int visibleThreshold);

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }
}
