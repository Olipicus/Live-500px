package com.olipicus.liveat500px.manager;

import android.content.Context;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.olipicus.liveat500px.dao.PhotoItemCollectionDao;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class PhotoListManager {

    private static PhotoListManager instance;

    public static PhotoListManager getInstance() {
        if (instance == null)
            instance = new PhotoListManager();
        return instance;
    }

    private Context mContext;
    private PhotoItemCollectionDao dao;

    public PhotoItemCollectionDao getDao() {
        return dao;
    }

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }

    private PhotoListManager() {
        mContext = Contextor.getInstance().getContext();
    }

}
