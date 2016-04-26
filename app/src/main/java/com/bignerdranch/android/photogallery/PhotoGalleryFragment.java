package com.bignerdranch.android.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simon on 4/25/16.
 */
public class PhotoGalleryFragment extends Fragment {

    public static final String TAG = "PhotoGalleryStatus";
    private RecyclerView mPhotoRecyclerView;

    private List<GalleryItem> mItems = new ArrayList<>();
    private PhotoAdapter mPhotoAdapter;

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recycler_view);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mPhotoRecyclerView.setLayoutManager(gridLayoutManager);

        new FetchItemsTask().execute(1, 100);


        mPhotoRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int itemsToLoadInOneTime) {
                new FetchItemsTask().execute(page, itemsToLoadInOneTime);

            }
        });

        //setupAdapter();


        return view;
    }



    private void setupAdapter(int start, int itemCount){
        if(isAdded()){
            if(mPhotoAdapter == null) {
                mPhotoAdapter = new PhotoAdapter(mItems);
                mPhotoRecyclerView.setAdapter(mPhotoAdapter);
            }
            else{
                mPhotoAdapter.notifyDataSetChanged();
                mPhotoAdapter.notifyItemRangeChanged(start, itemCount);
                Log.i(TAG, "TotalPhotos: "+mPhotoAdapter.getItemCount());
            }
        }
    }

    private class FetchItemsTask extends AsyncTask<Integer, Void, List<GalleryItem>>{



        @Override
        protected List<GalleryItem> doInBackground(Integer... params) {
            if(params.length == 2){
                int param1 = params[0].intValue();
                int param2 = params[1].intValue();
                return new FlickerFetchr().fetchItems(param1, param2);
            }
            else{
                return new FlickerFetchr().fetchItems();
            }

        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            int offset = mItems.size();
            int newItemsRange = galleryItems.size() - 1;
            mItems.addAll(galleryItems);
            setupAdapter(offset, newItemsRange);
        }
    }


    private class PhotoHolder extends RecyclerView.ViewHolder{
        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item){
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int i) {
            GalleryItem galleryItem = mGalleryItems.get(i);
            photoHolder.bindGalleryItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
}
