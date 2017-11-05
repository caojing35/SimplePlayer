package com.mustache.simpleplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class PlayService extends Service{

    private static final String TAG = "PlayService";

    private MediaPlayer player = new MediaPlayer();

    String path;

    boolean isEncrypt;

    public class PlayBinder extends Binder {

        public PlayService getService(){
            return PlayService.this;
        }
    }

    //通过binder实现了 调用者（client）与 service之间的通信
    private PlayBinder binder = new PlayBinder();

    public PlayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        path = intent.getStringExtra("path");
        isEncrypt = intent.getBooleanExtra("type", false);
        Log.i(TAG, "onBind: music file=" + path);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, String.format("onError: what=%d, extra=%d", what, extra));
                return false;
            }
        });

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                player.start();
                Log.i(TAG, "start");
            }
        });

        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                Log.i(TAG, "onBufferingUpdate: percent="+percent);
            }
        });
        return binder;
    }


    public void play(){
        Log.i(TAG, "prepareAsync");

        if (isEncrypt) {
            player.setDataSource(new SecureDataSource(path));
        }
        else {
            player.setDataSource(new MyDataSource(path));
        }

        player.prepareAsync();

//        player.start();
    }

    public void pause(){
        Log.i(TAG, "pause");
        player.pause();
    }

    public void forward(){
        Log.i(TAG, "forward");
        player.seekTo(player.getCurrentPosition() + 30 * 1000);
    }

    public void back(){
        Log.i(TAG, "back");
        player.seekTo(player.getCurrentPosition() - 30 * 1000);

    }

    public void stop(){
        Log.i(TAG, "stop&reset");
        player.stop();
        player.reset();
    }
}
