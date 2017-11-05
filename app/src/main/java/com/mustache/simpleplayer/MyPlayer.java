package com.mustache.simpleplayer;

import android.media.MediaDataSource;
import android.media.MediaPlayer;

/**
 * Created by caojing on 2017/11/3.
 */

public class MyPlayer extends MediaPlayer {

    @Override
    public void setDataSource(MediaDataSource dataSource) throws IllegalArgumentException, IllegalStateException {
        super.setDataSource(dataSource);
    }
}
