package kr.co.codersit.pcm_new.Audio.Runnable;

import com.score.rahasak.utils.OpusEncoder;

import kr.co.codersit.pcm_new.Audio.Listener.IOpusEncodeCompleteListener;
import kr.co.codersit.pcm_new.Audio.Memory.MemoryPool;

public class OpusEncodeRunnable implements Runnable{
    private OpusEncoder mOpusEncoder;
    private IOpusEncodeCompleteListener mOpusEncodeCompleteListener;
    private byte[] mOriginData = null;

    public OpusEncodeRunnable( OpusEncoder opusEncoder
            , IOpusEncodeCompleteListener opusEncodeCompleteListener ) {
        mOpusEncoder = opusEncoder;
        mOpusEncodeCompleteListener = opusEncodeCompleteListener;
    }
    public void setData ( byte[] datas ) {
        mOriginData = datas;
    }

    @Override
    public void run() {
        if ( mOriginData != null ) {
            byte[] encodeByte = MemoryPool.getInstance().allocate();
            int dataSize = mOpusEncoder.encode(mOriginData, 160, encodeByte);
            mOpusEncodeCompleteListener.onOpusEncodeComplete(encodeByte, dataSize);
            mOriginData = null;
        }
    }
}
