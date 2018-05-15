package kr.co.codersit.pcm_new.Audio.Runnable;

import android.media.AudioTrack;
import android.util.Log;

import kr.co.codersit.pcm_new.Audio.Listener.IPCMPlayCompleteListener;

public class PCMPlayerRunnable implements Runnable{

    private IPCMPlayCompleteListener mPCMPlayCompleteListener = null;

    private AudioTrack mAudioTrack = null;
    private byte[] mBuffer = null;

    public PCMPlayerRunnable (AudioTrack audioTrack , IPCMPlayCompleteListener ipcmPlayCompleteListener ) {
        mAudioTrack = audioTrack;
        mPCMPlayCompleteListener = ipcmPlayCompleteListener;
    }

    public void setData ( byte[] datas ) {
        Log.d("Data" , "datas!");
        mBuffer = datas;
    }

    @Override
    public void run() {
        if ( mBuffer != null ) {
            mAudioTrack.write(mBuffer , 0 , mBuffer.length );
            Log.d("Byte Size" , mBuffer.length + " "  + mBuffer );
            mPCMPlayCompleteListener.onPCMPlayComplete(mBuffer);
        }
        else {
            Log.d("Byte" , "Byte");
        }
    }
}
