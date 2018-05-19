package kr.co.codersit.pcm_new.Audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.score.rahasak.utils.OpusDecoder;
import com.score.rahasak.utils.OpusEncoder;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import kr.co.codersit.pcm_new.Audio.Listener.IOpusDecodeCompleteListener;
import kr.co.codersit.pcm_new.Audio.Listener.IOpusEncodeCompleteListener;
import kr.co.codersit.pcm_new.Audio.Listener.IPCMGetCompleteListener;
import kr.co.codersit.pcm_new.Audio.Listener.IPCMPlayCompleteListener;
import kr.co.codersit.pcm_new.Audio.Memory.MemoryPool;
import kr.co.codersit.pcm_new.Audio.Runnable.OpusDecodeRunnable;
import kr.co.codersit.pcm_new.Audio.Runnable.OpusEncodeRunnable;
import kr.co.codersit.pcm_new.Audio.Runnable.PCMPlayerRunnable;
import kr.co.codersit.pcm_new.Audio.Runnable.PCMRecorderRunnable;

public class PCMManger implements IPCMGetCompleteListener , IPCMPlayCompleteListener
        , IOpusEncodeCompleteListener , IOpusDecodeCompleteListener {

    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    private int mSampleRate = 8000;
    private int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate,mChannelCount,mAudioFormat);

    private AudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;

    private OpusEncoder mOpusEncoder;
    private OpusDecoder mOpusDecoder;

    private ThreadPoolExecutor mThreadPoolExecutor;

    private PCMRecorderRunnable mPCMRecorderRunnable = null;
    private PCMPlayerRunnable mPCMPlayerRunnable = null;
    private OpusDecodeRunnable mOpusDecodeRunnable;
    private OpusEncodeRunnable mOpusEncodeRunnable;

    private boolean isRun = false;
    private boolean isPlay = false;

    private Queue<byte[]> mQueue = null;

    public PCMManger ( ) {
        mThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        mAudioRecord = new AudioRecord( mAudioSource , mSampleRate , mChannelCount , mAudioFormat ,
                mBufferSize );
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC , mSampleRate , mChannelCount ,
                mAudioFormat , mBufferSize , AudioTrack.MODE_STREAM );

        mOpusEncoder = new OpusEncoder();
        mOpusEncoder.init(mSampleRate , 2 , OpusEncoder.OPUS_APPLICATION_VOIP);

        mOpusEncodeRunnable = new OpusEncodeRunnable(mOpusEncoder , this);
        mOpusDecodeRunnable = new OpusDecodeRunnable(mOpusDecoder , this);

        mOpusDecoder = new OpusDecoder();
        mOpusDecoder.init(mSampleRate, 2);

        MemoryPool.getInstance().setBufferSize(mBufferSize);

    }

    public void startPCMRecord() {
        mAudioRecord.startRecording();
        mQueue = new LinkedList<>();
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
        isPlay = true;

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC , mSampleRate , mChannelCount ,
                mAudioFormat , mBufferSize , AudioTrack.MODE_STREAM );
        mAudioTrack.play();

        if ( mPCMPlayerRunnable == null ) {
            mPCMPlayerRunnable = new PCMPlayerRunnable(mAudioTrack , this);
            //data set code 추가
        }
        mPCMPlayerRunnable.setData(mQueue.remove());
        mThreadPoolExecutor.submit(mPCMPlayerRunnable);
    }
    public void stopPCMPlay() {
        isPlay = false;/*
        mAudioTrack.stop();
        mAudioTrack.release();*/
    }

    @Override
    public void onPCMGetComplete(byte[] datas) {
        Log.d("Byte Size" , datas.length + " "  + datas );
        if ( isRun ) {
            mQueue.offer(datas);
            mThreadPoolExecutor.submit(mPCMRecorderRunnable);
        }
    }

    @Override
    public void onPCMPlayComplete(byte[] datas) {
        Log.d("Byte Size" , datas.length + " "  + datas );

        Log.d("Byte Size" , mQueue.size() + " " );
        if ( isPlay ) {
            //data set code 추가
            if ( mQueue.isEmpty() ) {
                isPlay = false;
                //mAudioTrack.stop();
                //AudioTrack.release();
                return;
            }
            mPCMPlayerRunnable.setData(mQueue.remove());
            mThreadPoolExecutor.submit(mPCMPlayerRunnable);
        }
        MemoryPool.getInstance().returnMemory(datas);
    }

    @Override
    public void onOpusDecodeComplete(byte[] datas, int size) {

    }

    @Override
    public void onOpusEncodeComplete(byte[] datas, int size) {

    }
}