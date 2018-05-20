package kr.co.codersit.pcm_new.Audio.Runnable;

import android.util.Log;

import com.score.rahasak.utils.OpusDecoder;

import kr.co.codersit.pcm_new.Audio.Listener.IOpusDecodeCompleteListener;
import kr.co.codersit.pcm_new.Audio.Memory.MemoryPool;

public class OpusDecodeRunnable implements Runnable{
    private OpusDecoder mOpusDecoder;
    private IOpusDecodeCompleteListener mOpusDecodeCompleteListener;
    private byte[] mEncodedData = null;

    public OpusDecodeRunnable(OpusDecoder opusDecoder
            , IOpusDecodeCompleteListener opusDecodeCompleteListener) {
        mOpusDecoder = opusDecoder;
        mOpusDecodeCompleteListener = opusDecodeCompleteListener;
    }

    public void setData ( byte[] datas ) {
        mEncodedData = datas;
    }


    @Override
    public void run() {
        if ( mEncodedData != null ) {
            byte[] decodeByte = MemoryPool.getInstance().allocate();
            Log.d("Opus" , "Decoding Running");
            int dataSize = mOpusDecoder.decode(mEncodedData , decodeByte , 160);
            mOpusDecodeCompleteListener.onOpusDecodeComplete(decodeByte , dataSize);
            mEncodedData = null;
        }
    }
}
