package com.olipicus.liveat500px.manager;

import android.content.Context;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.olipicus.liveat500px.dao.PhotoItemCollectionDao;
import com.olipicus.liveat500px.dao.PhotoItemDao;

import java.util.ArrayList;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class PhotoListManager {

    private Context mContext;
    private PhotoItemCollectionDao dao;

    public PhotoItemCollectionDao getDao() {
        return dao;
    }

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }

    public void insertDaoAtTopPosition(PhotoItemCollectionDao newDao){
        if(dao == null){
            dao = new PhotoItemCollectionDao();
        }

        if(dao.getData() == null){
            dao.setData(new ArrayList<PhotoItemDao>());
        }

        dao.getData().addAll(0, newDao.getData());
    }

    public void appendDaoAtBottomPosition(PhotoItemCollectionDao newDao){
        if(dao == null){
            dao = new PhotoItemCollectionDao();
        }

        if(dao.getData() == null){
            dao.setData(new ArrayList<PhotoItemDao>());
        }

        dao.getData().addAll(dao.getData().size(), newDao.getData());
    }

    public PhotoListManager() {
        mContext = Contextor.getInstance().getContext();
    }

    public int getMaximunId(){
        if (dao == null || dao.getData() == null || dao.getData().size() == 0){
            return 0;
        }
        int maxId = dao.getData().get(0).getId();
        for(int i = 0; i < dao.getData().size(); i ++){
            maxId = Math.max(maxId, dao.getData().get(i).getId());
        }
        return maxId;

    }

    public int getMinimunId(){
        if (dao == null || dao.getData() == null || dao.getData().size() == 0){
            return 0;
        }
        int minId = dao.getData().get(0).getId();
        for(int i = 0; i < dao.getData().size(); i ++){
            minId = Math.min(minId, dao.getData().get(i).getId());
        }
        return minId;

    }

    public int getCount(){
        if(dao == null || dao.getData() == null){
            return 0;
        }
        return  dao.getData().size();
    }

}
