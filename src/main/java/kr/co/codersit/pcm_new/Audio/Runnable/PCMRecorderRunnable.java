package kr.co.codersit.pcm_new.Audio.Runnable;

import android.media.AudioRecord;

import kr.co.codersit.pcm_new.Audio.Listener.IPCMGetCompleteListener;
import kr.co.codersit.pcm_new.Audio.Memory.MemoryPool;

public class PCMRecorderRunnable implements  Runnable {

    private IPCMGetCompleteListener mPCMompleteListener = null;
    private AudioRecord mAudioRecord = null;
    private int mBufferSize = 0;

    public PCMRecorderRunnable ( AudioRecord audioRecord ,
                                 IPCMGetCompleteListener pcmGetCompleteListener ,
                                 int bufferSize ) {
        mAudioRecord = audioRecord;
        mPCMompleteListener = pcmGetCompleteListener;
        mBufferSize = bufferSize;
    }

    @Override
    public void run() {
        // Read Code 넣기
        byte[] datas = MemoryPool.getInstance().allocate();
        mAudioRecord.read(datas,0,mBufferSize);
        mPCMompleteListener.onPCMGetComplete(datas);
    }
}
