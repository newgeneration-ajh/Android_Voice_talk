package kr.co.codersit.pcm_new;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestRecordAudioPermission();
        setContentView(R.layout.activity_main);
    }

    private void requestRecordAudioPermission() {
        //check API version, do nothing if API version < 23!
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission_group.STORAGE )) {

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECORD_AUDIO ,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
            else {
                Intent intent = new Intent (this,AudioActivity.class);
                startActivity(intent);
            }
        }
        else{
            Intent intent = new Intent (this,AudioActivity.class);
            startActivity(intent);
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Activity", "Granted!");
                    Intent intent = new Intent (this,AudioActivity.class);
                    startActivity(intent);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Activity", "Denied!");
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
