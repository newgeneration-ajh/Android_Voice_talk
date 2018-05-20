package kr.co.codersit.pcm_new.Audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.score.rahasak.utils.OpusDecoder;
import com.score.rahasak.utils.OpusEncoder;

import java.util.Arrays;
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

public class OpusManager implements IPCMGetCompleteListener, IPCMPlayCompleteListener
        , IOpusEncodeCompleteListener, IOpusDecodeCompleteListener
        , IAudioPlayer , IAudioRecorder {

    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    private int mSampleRate = 8000;
    private int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSize = 0;

    private AudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;

    private OpusEncoder mOpusEncoder;
    private OpusDecoder mOpusDecoder;

    private ThreadPoolExecutor mThreadPoolExecutor;

    private PCMRecorderRunnable mPCMRecorderRunnable;
    private PCMPlayerRunnable mPCMPlayerRunnable = null;
    private OpusDecodeRunnable mOpusDecodeRunnable;
    private OpusEncodeRunnable mOpusEncodeRunnable;

    private boolean isRun = false;
    private boolean isPlay = false;

    private Queue<byte[]> mQueue = null;

    public OpusManager(){
        mThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

        mBufferSize = 160 * 2 * 2;

        mAudioRecord = new AudioRecord( mAudioSource , mSampleRate , mChannelCount , mAudioFormat ,
                mBufferSize );

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC , mSampleRate , mChannelCount ,
                mAudioFormat , mBufferSize , AudioTrack.MODE_STREAM );

        mPCMRecorderRunnable = new PCMRecorderRunnable(mAudioRecord , this
                , mBufferSize);

        mOpusEncoder = new OpusEncoder();
        mOpusEncoder.init(mSampleRate , 2 , OpusEncoder.OPUS_APPLICATION_RESTRICTED_LOWDELAY);

        mOpusDecoder = new OpusDecoder();
        mOpusDecoder.init(mSampleRate, 2);

        mOpusEncodeRunnable = new OpusEncodeRunnable(mOpusEncoder , this);
        mOpusDecodeRunnable = new OpusDecodeRunnable(mOpusDecoder , this);

        MemoryPool.getInstance().setBufferSize(mBufferSize);
    }


    @Override
    public void startPlay() {
        isPlay = true;

        mAudioTrack.play();

        if ( mPCMPlayerRunnable == null ) {
            mPCMPlayerRunnable = new PCMPlayerRunnable(mAudioTrack , this);
            //data set code 추가
        }
        mOpusDecodeRunnable.setData(mQueue.peek());
        mThreadPoolExecutor.submit(mOpusDecodeRunnable);
    }

    @Override
    public void stopPlay() {
        isPlay = false;
        mAudioTrack.stop();
        //mOpusDecoder = null;
    }

    @Override
    public void startRecord() {
        mAudioRecord.startRecording();
        mQueue = new LinkedList<>();
        mThreadPoolExecutor.submit(mPCMRecorderRunnable);
        isRun = true;
    }

    @Override
    public void stopRecord() {
        isRun = false;
        //mOpusEncoder = null;
        mAudioRecord.stop();
    }

    @Override
    public void onOpusDecodeComplete(byte[] datas, int size) {
        Log.d("Opus Complete" , "Decode Data Size : " + size);
        mPCMPlayerRunnable.setData(datas);
        mThreadPoolExecutor.submit(mPCMPlayerRunnable);
    }

    @Override
    public void onOpusEncodeComplete(byte[] datas, int size) {
        Log.d("Opus Complete" , "Encode Data Size : " + size);
        byte[] tmpDatas = Arrays.copyOf(datas,size);
        mQueue.offer(tmpDatas);
    }

    @Override
    public void onPCMGetComplete(byte[] datas) {
        if ( isRun ) {
            mOpusEncodeRunnable.setData(datas);
            mThreadPoolExecutor.submit(mOpusEncodeRunnable);
            mThreadPoolExecutor.submit(mPCMRecorderRunnable);
        }
    }

    @Override
    public void onPCMPlayComplete(byte[] datas) {
        if ( isPlay ) {
            mOpusDecodeRunnable.setData(mQueue.poll());
            mThreadPoolExecutor.submit(mOpusDecodeRunnable);
        }
    }
}
