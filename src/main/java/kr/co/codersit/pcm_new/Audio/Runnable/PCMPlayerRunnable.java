package kr.co.codersit.pcm_new.Audio.Runnable;

import android.media.AudioTrack;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

import kr.co.codersit.pcm_new.Audio.Listener.IPCMPlayCompleteListener;

public class PCMPlayerRunnable implements Runnable{

    private IPCMPlayCompleteListener mPCMPlayCompleteListener ;
    private AudioTrack mAudioTrack;
    private Queue<byte[]> mBufferQueue;

    public PCMPlayerRunnable (AudioTrack audioTrack , IPCMPlayCompleteListener ipcmPlayCompleteListener ) {
        mAudioTrack = audioTrack;
        mPCMPlayCompleteListener = ipcmPlayCompleteListener;
        mBufferQueue = new LinkedList<>();
    }

    public void setData ( byte[] buffer ) {
        mBufferQueue.offer(buffer);
    }

    @Override
    public void run() {
        if ( !mBufferQueue.isEmpty() ) {
            byte[] buffer = mBufferQueue.poll();
            mAudioTrack.write(buffer , 0 , buffer.length );
            Log.d("Byte Size" , buffer.length + " "  + buffer );
            mPCMPlayCompleteListener.onPCMPlayComplete(buffer);
        }
    }
}
