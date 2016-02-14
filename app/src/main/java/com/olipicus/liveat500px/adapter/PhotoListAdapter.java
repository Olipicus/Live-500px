package com.olipicus.liveat500px.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.olipicus.liveat500px.manager.PhotoListManager;
import com.olipicus.liveat500px.view.PhotoListItem;

/**
 * Created by olipicus on 2/14/2016 AD.
 */
public class PhotoListAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        if(PhotoListManager.getInstance().getDao() == null || PhotoListManager.getInstance().getDao().getData() == null){
            return 0;
        }
        return PhotoListManager.getInstance().getDao().getData().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /*
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position%2 == 0 ? 0 : 1;
    }
    */



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PhotoListItem item;

        if (convertView != null){
            item = (PhotoListItem) convertView;
        }
        else{
            item = new PhotoListItem(parent.getContext());
        }
        return item;

    }
}
