package kr.co.codersit.pcm_new.Audio.Runnable;

import android.media.AudioTrack;
public class PCMPlayerRunnable implements Runnable{

    private AudioTrack mAudioTrack = null;
    private byte[] mBuffer = null;

    public PCMPlayerRunnable (AudioTrack audioTrack ) {
        mAudioTrack = audioTrack;
    }

    public void setData ( byte[] datas ) {
        mBuffer = datas;
    }

    @Override
    public void run() {
        if ( mBuffer != null ) {
            mAudioTrack.write(mBuffer , 0 , mBuffer.length );
            mBuffer = null;
        }
    }
}
