package com.mustache.simpleplayer;

import android.media.MediaDataSource;
import android.util.Log;

import com.mustache.simpleplayer.security.ChaCha20;
import com.mustache.simpleplayer.util.ClockCount;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

/**
 * Created by caojing on 2017/11/5.
 */

public class SecureDataSource extends MediaDataSource {

    private static final String TAG = "SecureDataSource";

    private static int BLOCK_SIZE = 2048;

    RandomAccessFile cipherFile;

    long size = 0;

    byte[] allData;

    public SecureDataSource(String path){

        try {
//            allData = readEncryptDataFromFile(path);
            cipherFile = new RandomAccessFile(new File(path), "r");
            size = cipherFile.length();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        try{
            ClockCount count = new ClockCount("readAt");

//            Log.i(TAG, String.format("readAt: position=%d, offset=%d, size=%d", position, offset, size));

            //start read file
            long startPos = getStartPos(position);
            long endPos = startPos + BLOCK_SIZE - 1;//getEndPos(position, size);
            int buffSize = (int) (endPos - startPos + 1);
            cipherFile.seek(startPos);
            byte[] buff = new byte[buffSize];
            cipherFile.read(buff);
//            count.addClick("readFile");
//            Log.i(TAG, String.format("readFile: startPos=%d, buffSize=%d", startPos, buffSize));

            //start decrypt
//            int encryptLen = 0;
//            byte[] encBuff = new byte[BLOCK_SIZE];
//            while(encryptLen != buffSize)
//            {
//                System.arraycopy(buff, encryptLen, encBuff, 0, BLOCK_SIZE);
//                byte[] result = ChaCha20.getEncryptor().decrypt(encBuff, BLOCK_SIZE);
//                System.arraycopy(result, 0, buff, encryptLen, BLOCK_SIZE);
//                encryptLen += BLOCK_SIZE;
////                count.addClick("decrypt a block");
//            }

            //copy data
            int bufStart = (int) (position - startPos);
            int datalen = Math.min((buffSize - bufStart), (size));//size;
            System.arraycopy(buff, bufStart, buffer, 0, datalen);
//            Log.i(TAG, String.format("copyData: bufStart=%d, datalen=%d", bufStart, datalen));

//            count.addClick("copy buffer");
//        Log.i(TAG, "readAt: "+count.desc());

            return datalen;
        }catch (RuntimeException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getSize() throws IOException {
        Log.i(TAG, "getSize: return="+size);
        return size;
    }

    @Override
    public void close() throws IOException {
        if(cipherFile != null)
        {
            cipherFile.close();
            cipherFile = null;
        }
    }

    static byte[] readEncryptDataFromFile(String path) throws IOException {
        ClockCount clockCount = new ClockCount("readEncryptDataFromFile");
        RandomAccessFile file = new RandomAccessFile(new File(path), "r");
        long size = file.length();
        byte[] allData = new byte[(int) size];

        byte[] encBuff = new byte[BLOCK_SIZE];
        int readLen = 0;
        int blockCount = 0;
        while(readLen != size)
        {
            int currLen = file.read(encBuff);
            byte[] data = ChaCha20.getEncryptor().decrypt(encBuff, currLen);
            Log.i(TAG, "readEncryptDataFromFile: write to allData=" + arrToString(data, data.length));
            System.arraycopy(data, 0, allData, readLen, currLen);
//            clockCount.addClick("decrypt a block");
            readLen += currLen;
            blockCount++;
        }
        Log.i(TAG, String.format("readEncryptDataFromFile: size=%d, readLen=%d, blockCount=%d",
                size, readLen, blockCount));
        Log.i(TAG, "readEncryptDataFromFile: "+clockCount.desc());
        file.close();
        return allData;
    }

    private long getBlockIndex(long position)
    {
        return position / BLOCK_SIZE;
    }

    private long getBlockStartPos(long position)
    {
        return getBlockIndex(position) * BLOCK_SIZE;
    }

    private long getBlockEndPos(long position)
    {
        return getBlockIndex(position) * BLOCK_SIZE + BLOCK_SIZE - 1;
    }

    private long getEndPos(long position, int size)
    {
        return getBlockEndPos(position + size - 1);
    }

    private long getStartPos(long position)
    {
        return getBlockStartPos(position);
    }

    static String arrToString(byte[] text, int len)
    {
        byte[] str = new byte[10];
        System.arraycopy(text, 0, str, 0, 5);
        System.arraycopy(text, len-5, str, 5, 5);
        return Arrays.toString(str);
    }
}
