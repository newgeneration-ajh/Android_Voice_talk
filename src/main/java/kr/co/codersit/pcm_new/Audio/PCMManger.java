package kr.co.codersit.pcm_new.Audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import kr.co.codersit.pcm_new.Audio.Listener.IPCMGetCompleteListener;
import kr.co.codersit.pcm_new.Audio.Memory.MemoryPool;
import kr.co.codersit.pcm_new.Audio.Runnable.PCMRecorderRunnable;

public class PCMManger implements IPCMGetCompleteListener {

    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    private int mSampleRate = 44100;
    private int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate,mChannelCount,mAudioFormat);

    private AudioRecord mAudioRecord = null;

    private ThreadPoolExecutor mThreadPoolExecutor = null;
    private PCMRecorderRunnable mPCMRecorderRunnable = null;
    private boolean isRun = false;

    public PCMManger ( ) {
        mThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        mAudioRecord = new AudioRecord( mAudioSource , mSampleRate , mChannelCount , mAudioFormat ,
                mBufferSize );
        MemoryPool.getInstance().setBufferSize(mBufferSize);

    }

    public void startPCMRecord() {
        mAudioRecord.startRecording();
        if ( mPCMRecorderRunnable == null ) {
            mPCMRecorderRunnable = new PCMRecorderRunnable(mAudioRecord,
                    this , mBufferSize );
        }
        mThreadPoolExecutor.submit(mPCMRecorderRunnable);
        isRun = true;
    }

    public void stopPCMRecord() {
        //mAudioRecord.release();
        isRun = false;
    }

    @Override
    public void onPCMGetComplete(byte[] datas) {
        Log.d("Byte Size" , datas.length + "" );
        if ( isRun ) {
            mThreadPoolExecutor.submit(mPCMRecorderRunnable);
        }
    }
}
