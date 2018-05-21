package kr.co.codersit.pcm_new.Audio.Runnable;

import android.util.Log;

import com.score.rahasak.utils.OpusEncoder;

import java.util.LinkedList;
import java.util.Queue;

import kr.co.codersit.pcm_new.Audio.Listener.IOpusEncodeCompleteListener;
import kr.co.codersit.pcm_new.Audio.Memory.MemoryPool;

public class OpusEncodeRunnable implements Runnable{
    private OpusEncoder mOpusEncoder;
    private IOpusEncodeCompleteListener mOpusEncodeCompleteListener;

    private Queue<byte[]> mOriginDataQueue;

    public OpusEncodeRunnable( OpusEncoder opusEncoder
            , IOpusEncodeCompleteListener opusEncodeCompleteListener ) {
        mOpusEncoder = opusEncoder;
        mOpusEncodeCompleteListener = opusEncodeCompleteListener;
        mOriginDataQueue = new LinkedList<>();
    }
    public void setData ( byte[] originData ) {
        mOriginDataQueue.offer(originData);
    }

    @Override
    public void run() {
        if ( !mOriginDataQueue.isEmpty() ) {
            byte[] originData = mOriginDataQueue.poll();
            byte[] encodeByte = new byte[200];
            int dataSize = mOpusEncoder.encode(originData, 160, encodeByte);
            Log.d("Opus", "Data Size " + originData.length + " to " + dataSize);
            mOpusEncodeCompleteListener.onOpusEncodeComplete(encodeByte, dataSize);
        }
    }
}
