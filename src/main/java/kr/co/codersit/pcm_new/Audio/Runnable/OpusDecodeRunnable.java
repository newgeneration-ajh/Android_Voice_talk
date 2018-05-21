package kr.co.codersit.pcm_new.Audio.Runnable;

import android.util.Log;

import com.score.rahasak.utils.OpusDecoder;

import java.util.LinkedList;
import java.util.Queue;

import kr.co.codersit.pcm_new.Audio.Listener.IOpusDecodeCompleteListener;
import kr.co.codersit.pcm_new.Audio.Memory.MemoryPool;

public class OpusDecodeRunnable implements Runnable{
    private OpusDecoder mOpusDecoder;
    private IOpusDecodeCompleteListener mOpusDecodeCompleteListener;
    private Queue<byte[]> mEncodedDataQueue;

    public OpusDecodeRunnable(OpusDecoder opusDecoder
            , IOpusDecodeCompleteListener opusDecodeCompleteListener) {
        mOpusDecoder = opusDecoder;
        mOpusDecodeCompleteListener = opusDecodeCompleteListener;
        mEncodedDataQueue = new LinkedList<>();
    }

    public void setData ( byte[] datas ) {
        mEncodedDataQueue.offer(datas);
    }


    @Override
    public void run() {
        if ( !mEncodedDataQueue.isEmpty() ) {
            byte[] encodedByte = mEncodedDataQueue.poll();
            byte[] decodeByte = new byte[160 * 2 * 2];
            Log.d("Opus" , "Decoding Running");
            int dataSize = mOpusDecoder.decode(encodedByte , decodeByte , 160);
            mOpusDecodeCompleteListener.onOpusDecodeComplete(decodeByte , dataSize);
        }
    }
}
