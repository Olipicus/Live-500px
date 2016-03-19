package com.olipicus.liveat500px.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.olipicus.liveat500px.R;
import com.olipicus.liveat500px.adapter.PhotoListAdapter;
import com.olipicus.liveat500px.dao.PhotoItemCollectionDao;
import com.olipicus.liveat500px.manager.HttpManager;
import com.olipicus.liveat500px.manager.PhotoListManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment {

    ListView listView;
    PhotoListAdapter listAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    PhotoListManager photoListManager;

    Button btnNewPhoto;

    Boolean isLoadingMore = false;

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoListManager = new PhotoListManager();

        if(savedInstanceState != null){
            ;onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView);
        return rootView;
    }

    private void initInstances(View rootView) {

        // init instance with rootView.findViewById here
        //setRetainInstance(true);
        btnNewPhoto = (Button) rootView.findViewById(R.id.btnNewPhoto);
        btnNewPhoto.setOnClickListener(buttonClickListener);

        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new PhotoListAdapter();
        listView.setAdapter(listAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(pullToRefreshListener);
        listView.setOnScrollListener(listViewScrollListener);

        refreshData();
    }

    private void showToast(String text){
        Toast.makeText(Contextor.getInstance().getContext(),
                text,
                Toast.LENGTH_SHORT)
                .show();
    }

    private void refreshData() {
        if(photoListManager.getCount() == 0){
            reloadData();
        }
        else {
            reloadDataNewer();
        }
    }

    private void loadMoreData(){
        if(isLoadingMore){
            return ;
        }
        isLoadingMore = true;
        int minId = photoListManager.getMinimunId();
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoListBeforeId(minId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_LOAD_MORE));

    }

    private void reloadDataNewer(){
        int maxId = photoListManager.getMaximunId();
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoListAfterId(maxId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD_NEWER));

    }

    private void reloadData() {
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoList();
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save Instance State Here
        //TODO : Save PhotoListManager to outState
    }

    private void onRestoreInstanceState(Bundle savedInstanceState){
        //Restore Instance State Here
    }

    /*
     * Restore Instance State Here
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void showButtonNewPhoto(){
        btnNewPhoto.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(
                Contextor.getInstance().getContext(),
                R.anim.zoom_fade_in
                );
        btnNewPhoto.startAnimation(anim);
    }

    private void hideButtonNewPhoto(){
        btnNewPhoto.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(
                Contextor.getInstance().getContext(),
                R.anim.zoom_fade_out);
        btnNewPhoto.startAnimation(anim);
    }
    
    /* -------- Listener -------- */
    final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == btnNewPhoto) {
                hideButtonNewPhoto();
                listView.smoothScrollToPosition(0);
            }
        }
    };

    final SwipeRefreshLayout.OnRefreshListener pullToRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
        }
    };

    final AbsListView.OnScrollListener listViewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view,
                             int firstVisibleItem,
                             int visibleItemCount,
                             int totalItemCount) {
            if(view == listView){
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);

                //Control Endless Scroll
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    if (photoListManager.getCount() > 0) {
                        loadMoreData();
                    }
                }
            }
        }
    };

    /* -------- Inner Class --------- */

    class PhotoListLoadCallback implements Callback<PhotoItemCollectionDao> {

        public static final int MODE_RELOAD = 1;
        public static final int MODE_RELOAD_NEWER = 2;
        public static final int MODE_LOAD_MORE = 3;

        private int mode;

        public PhotoListLoadCallback(int mode){
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<PhotoItemCollectionDao> call, Response<PhotoItemCollectionDao> response) {

            swipeRefreshLayout.setRefreshing(false);
            if(response.isSuccess()){
                PhotoItemCollectionDao dao = response.body();

                int firstVisiblePosition = listView.getFirstVisiblePosition();
                View c = listView.getChildAt(0);
                int top = c == null ? 0 : c.getTop();

                if(this.mode == this.MODE_RELOAD_NEWER){
                    photoListManager.insertDaoAtTopPosition(dao);
                }
                else if(this.mode == this.MODE_LOAD_MORE){
                    photoListManager.appendDaoAtBottomPosition(dao);
                }
                else{
                    photoListManager.setDao(dao);
                }

                clearLoadingMoreFlagIfCapable(mode);

                listAdapter.setDao(photoListManager.getDao());
                listAdapter.notifyDataSetChanged();

                if(this.mode == this.MODE_RELOAD_NEWER){
                    //Maintain Scroll Position
                    int additionalSize =
                            (dao != null && dao.getData() != null) ? dao.getData().size() : 0;
                    listAdapter.increaseLastPosition(additionalSize);
                    listView.setSelectionFromTop(firstVisiblePosition + additionalSize, top);

                    if(additionalSize > 0){
                        showButtonNewPhoto();
                    }
                }
                else{

                }

                showToast("Load Completed");
            }
            else{

                clearLoadingMoreFlagIfCapable(mode);
                showToast(response.errorBody().toString());
            }
        }

        @Override
        public void onFailure(Call<PhotoItemCollectionDao> call, Throwable t) {

            clearLoadingMoreFlagIfCapable(mode);
            swipeRefreshLayout.setRefreshing(false);
            showToast(t.toString());
        }

        private void clearLoadingMoreFlagIfCapable(int mode){
            if(mode == MODE_LOAD_MORE)
                isLoadingMore = false;
        }
    }
}
