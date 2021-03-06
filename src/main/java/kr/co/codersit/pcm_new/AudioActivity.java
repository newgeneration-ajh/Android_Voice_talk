package kr.co.codersit.pcm_new;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.co.codersit.pcm_new.Audio.OpusManager;
import kr.co.codersit.pcm_new.Audio.PCMManger;

public class AudioActivity extends AppCompatActivity {

    public boolean isRecording = false;

    public boolean isPlaying = false;

    public Button mBtRecord = null;
    public Button mBtPlay = null;

    public PCMManger mPCMManger = null;
    public OpusManager mOpusManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        mBtRecord = (Button)findViewById(R.id.bt_record);
        mBtPlay = (Button)findViewById(R.id.bt_play);

        mPCMManger = new PCMManger();
        mOpusManager = new OpusManager();
    }

    public void onRecord(View view) {
        if(isRecording == true) {
            isRecording = false;
            //mPCMManger.stopPCMRecord();
            mOpusManager.stopRecord();;
            mBtRecord.setText("Record");
        }
        else {
            isRecording = true;
            //mPCMManger.startPCMRecord();
            mOpusManager.startRecord();
            mBtRecord.setText("Stop");
        }
    }
    public void onPlay(View view) {
        if(isPlaying == true) {
            isPlaying = false;
            mBtPlay.setText("Play");
            //mPCMManger.stopPCMPlay();
            mOpusManager.stopPlay();
        }
        else {
            isPlaying = true;
            mBtPlay.setText("Stop");
            //mPCMManger.startPCMPaly();
            mOpusManager.startPlay();
        }

    }
}

