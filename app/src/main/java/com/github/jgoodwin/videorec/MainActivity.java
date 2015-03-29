package com.github.jgoodwin.videorec;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class MainActivity extends ActionBarActivity implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;

    public MediaRecorder recorder;

    private Camera camera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(null, "Video starting");
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);

        surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "StartRecording");
        menu.add(0, 1, 0, "StopRecording");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                try {
                    startRecording();
                } catch (Exception e) {
                    String message = e.getMessage();
                    Log.i(null, "Problem Start" + message);
                    recorder.release();
                }
                break;

            case 1: //GoToAllNotes
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void startRecording() throws IOException {
        recorder = new MediaRecorder();  // Works well
        camera.setDisplayOrientation(90);

        camera.unlock();
        recorder.setCamera(camera);

        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOrientationHint(270);

        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        recorder.setPreviewDisplay(surfaceHolder.getSurface());

        String path = "/sdcard/zzzz.3gp";

        File file = new File(path);
        file.createNewFile();

        recorder.setOutputFile(path);

        recorder.prepare();
        recorder.start();
    }

    protected void stopRecording() {
        recorder.stop();
        recorder.release();
        camera.release();
    }

    private void releaseMediaRecorder() {
        if (recorder != null) {
            recorder.reset();   // clear recorder configuration
            recorder.release(); // release the recorder object
            recorder = null;
            camera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            camera.setParameters(params);
        } else {
            Toast.makeText(getApplicationContext(), "Camera not available!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }
}
