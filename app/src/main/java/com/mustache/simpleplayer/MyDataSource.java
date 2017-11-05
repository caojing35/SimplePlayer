package com.mustache.simpleplayer;

import android.media.MediaDataSource;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by caojing on 2017/11/3.
 */

public class MyDataSource extends MediaDataSource {

    private static final String TAG = "MyDataSource";

    RandomAccessFile randomAccessFile;

    byte[] temp;

    public MyDataSource(String path) {
        try {
            randomAccessFile = new RandomAccessFile(new File(path), "r");
            temp = new byte[(int) randomAccessFile.length()];
            randomAccessFile.read(temp, 0, temp.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int readAt(long pos, byte[] bytes, int offset, int size) throws IOException {
        Log.i(TAG, String.format("readAt before: pos=%d, bytes.length=%d, offset=%d, size=%d"
                , pos, bytes.length, offset, size));
//        if (pos != randomAccessFile.getFilePointer())
//        {
//            randomAccessFile.seek(pos);
//        }
//
//        int readLen = randomAccessFile.read(bytes, 0, size);

        System.arraycopy(temp, (int)pos, bytes, 0, size);

        Log.i(TAG, String.format("readAt after: readLen=%d"
                , size));

        return size;
    }

    @Override
    public long getSize() throws IOException {
        return randomAccessFile.length();
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }
}
