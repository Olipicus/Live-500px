package com.olipicus.liveat500px.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import com.olipicus.liveat500px.R;
import com.olipicus.liveat500px.dao.PhotoItemCollectionDao;
import com.olipicus.liveat500px.dao.PhotoItemDao;
import com.olipicus.liveat500px.view.PhotoListItem;

import datatype.MutableInteger;

/**
 * Created by olipicus on 2/14/2016 AD.
 */
public class PhotoListAdapter extends BaseAdapter {

    PhotoItemCollectionDao dao;
    MutableInteger lastPositionInteger;

    public PhotoListAdapter(MutableInteger lastPositionInteger) {
        this.lastPositionInteger = lastPositionInteger;
    }

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }

    @Override
    public int getCount() {
        if(this.dao == null || this.dao.getData() == null){
            return 1;
        }
        return this.dao.getData().size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return this.dao.getData().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getCount() - 1 ? 1 : 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(position == getCount() - 1){
            ProgressBar item;
            if(convertView != null){
                item = (ProgressBar) convertView;
            }
            else{
                item = new ProgressBar(parent.getContext());
            }
            return item;
        }

        PhotoListItem item;

        if (convertView != null){
            item = (PhotoListItem) convertView;
        }
        else{
            item = new PhotoListItem(parent.getContext());
        }

        PhotoItemDao dao = (PhotoItemDao) getItem(position);

        item.setName(dao.getCaption());
        item.setDescription(dao.getUserName() + "\n" + dao.getCamera());
        item.setImageUrl(dao.getImageUrl());

        if(position > lastPositionInteger.getValue()){
            Animation anim = AnimationUtils.loadAnimation(parent.getContext(), R.anim.up_from_buttom);
            item.startAnimation(anim);
            lastPositionInteger.setValue(position);
        }


        return item;

    }

    public void increaseLastPosition(int amount){
        lastPositionInteger.setValue(lastPositionInteger.getValue() + amount);
    }
}
