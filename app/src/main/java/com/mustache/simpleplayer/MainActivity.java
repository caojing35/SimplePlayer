package com.mustache.simpleplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener
{

    private static final String TAG = "MainActivity";

    PlayService playService;

//    private static final boolean isEncrypt = false;
//    private static final String MUSIC_FILE = "mymusic/01.Angel.mp3";

    private static final boolean isEncrypt = true;
    private static final String MUSIC_FILE = "mymusic/Angel.mp3";//Angel_enc.mp3";

    private boolean isBound = false;

    Button playBt;

    Button pauseBt;

    Button forwardBt;

    Button backBt;

    Button stopBt;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            PlayService.PlayBinder binder = (PlayService.PlayBinder) service;
            playService = binder.getService();
//            int num = service.getRandomNumber();
        }

        //client 和service连接意外丢失时，会调用该方法
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v("hjz","onServiceDisconnected  A");
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PackageManager.PERMISSION_GRANTED == grantResults[0])
        {

            Intent intent = new Intent(MainActivity.this, PlayService.class);
            String path = Environment
                    .getExternalStorageDirectory()
//                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                    .getAbsolutePath() + File.separator + MUSIC_FILE;

            intent.putExtra("path", path);
            intent.putExtra("type", isEncrypt);
            bindService(intent, conn, BIND_AUTO_CREATE);

            Log.i(TAG, "onCreate: public music file=" + path);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBt = findViewById(R.id.play);
        pauseBt = findViewById(R.id.pause);
        forwardBt = findViewById(R.id.forward);
        backBt = findViewById(R.id.back);
        stopBt = findViewById(R.id.stop);

        playBt.setOnClickListener(this);
        pauseBt.setOnClickListener(this);
        forwardBt.setOnClickListener(this);
        backBt.setOnClickListener(this);
        stopBt.setOnClickListener(this);

        this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play:
                playService.play();
                break;
            case R.id.pause:
                playService.pause();
                break;
            case R.id.forward:
                playService.forward();
                break;
            case R.id.back:
                playService.back();
                break;
            case R.id.stop:
                playService.stop();
                break;
            default:
                break;
        }
    }
}
