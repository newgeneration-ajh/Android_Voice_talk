package kr.co.codersit.pcm_new.Audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import kr.co.codersit.pcm_new.Audio.Listener.IPCMGetCompleteListener;
import kr.co.codersit.pcm_new.Audio.Listener.IPCMPlayCompleteListener;
import kr.co.codersit.pcm_new.Audio.Memory.MemoryPool;
import kr.co.codersit.pcm_new.Audio.Runnable.PCMPlayerRunnable;
import kr.co.codersit.pcm_new.Audio.Runnable.PCMRecorderRunnable;

public class PCMManger implements IPCMGetCompleteListener , IPCMPlayCompleteListener {

    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    private int mSampleRate = 44100;
    private int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate,mChannelCount,mAudioFormat);

    private AudioRecord mAudioRecord = null;
    private AudioTrack mAudioTrack = null;

    private ThreadPoolExecutor mThreadPoolExecutor = null;
    private PCMRecorderRunnable mPCMRecorderRunnable = null;
    private PCMPlayerRunnable mPCMPlayerRunnable = null;

    private boolean isRun = false;
    private boolean isPlay = false;

    public PCMManger ( ) {
        mThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        mAudioRecord = new AudioRecord( mAudioSource , mSampleRate , mChannelCount , mAudioFormat ,
                mBufferSize );
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC , mSampleRate , mChannelCount ,
                mAudioFormat , mBufferSize , AudioTrack.MODE_STREAM );
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

    public void startPCMPaly() {
        mAudioTrack.play();
        isPlay = true;
        if ( mPCMPlayerRunnable == null ) {
            mPCMPlayerRunnable = new PCMPlayerRunnable(mAudioTrack);
        }
    }
    public void stopPCMPlay() {
        isPlay = false;
    }

    @Override
    public void onPCMGetComplete(byte[] datas) {
        Log.d("Byte Size" , datas.length + " "  + datas );
        if ( isRun ) {
            //MemoryPool.getInstance().returnMemory(datas);
            mThreadPoolExecutor.submit(mPCMRecorderRunnable);
        }
    }

    @Override
    public void onPCMPlayComplete(byte[] datas) {
        if ( isPlay ) {

        }
    }
}
