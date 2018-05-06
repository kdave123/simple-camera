//Developing Simple Camera App. Using Phone funtions camera,flash, orientation, phone storage ..

package theway.mycam;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;
import android.hardware.Camera.CameraInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
@SuppressWarnings("deprecation")
public class MainSurface extends Activity implements SurfaceHolder.Callback,View.OnClickListener {

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private ImageButton flashCameraButton;
    private ImageButton flipCamera;
    private ImageButton captureImage;
    private int cameraId;
    private boolean flashMode = false;
    private int f =1;
    private int rotation;
    private OrientationEventListener cOrientationEventListener;
    private int mOrientation =  -1;
    private static final int ORIENTATION_PORTRAIT_NORMAL =  1;
    private static final int ORIENTATION_PORTRAIT_INVERTED =  2;
    private static final int ORIENTATION_LANDSCAPE_NORMAL =  3;
    private static final int ORIENTATION_LANDSCAPE_INVERTED =  4;
    private SurfaceView surfaceView;

    Camera.Size myBestSize;
    Camera.Size myPicSize;
    Animation rot;
    Animation rotl;
    Animation rotlr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main_surface);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        // camera surface view created
        cameraId = CameraInfo.CAMERA_FACING_BACK;
        flipCamera = (ImageButton) findViewById(R.id.flipCamera);
        flashCameraButton = (ImageButton) findViewById(R.id.flash);
        captureImage = (ImageButton) findViewById(R.id.captureImage);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        flipCamera.setOnClickListener(this);
        captureImage.setOnClickListener(this);
        flashCameraButton.setOnClickListener(this);
  //      surfaceView.setOnClickListener(this);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        rot = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate);
        rotl = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotateland);

        rotlr = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotatelandrev);
        // flipCamera.setEnabled(false);

    //    if (Camera.getNumberOfCameras() > 1) {
     //       flipCamera.setEnabled(true);
     //   }
     //   if (!getBaseContext().getPackageManager().hasSystemFeature(
     //           PackageManager.FEATURE_CAMERA_FLASH)) {
     //       flashCameraButton.setEnabled(false);
     //   }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(),
                "RESUME" ,Toast.LENGTH_LONG).show();
        if (!openCamera(CameraInfo.CAMERA_FACING_BACK))
        {
            alertCameraDialog();
        }
        if (cOrientationEventListener == null) {
            cOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {


                public void onOrientationChanged(int orientation) {

                    // determine our orientation based on sensor response
                    int lastOrientation = mOrientation;


                    Camera.CameraInfo infol =new Camera.CameraInfo();
                    



                    // portrait oriented devices
                    if (orientation >= 315 || orientation < 45) {
                        if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                            mOrientation = ORIENTATION_PORTRAIT_NORMAL;

                            flipCamera.startAnimation(rot);
                            captureImage.startAnimation(rot);
                            if(f==1)
                            {
                                flashCameraButton.startAnimation(rot);}

                        }
                    } else if (orientation < 315 && orientation >= 225) {
                        if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                            mOrientation = ORIENTATION_LANDSCAPE_NORMAL;


                            flipCamera.startAnimation(rotl);
                            captureImage.startAnimation(rotl);
                            if(f==1)
                            {
                                flashCameraButton.startAnimation(rot);}
                        }
                    } else if (orientation < 225 && orientation >= 135) {
                        if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                            mOrientation = ORIENTATION_PORTRAIT_INVERTED;


                            flipCamera.startAnimation(rot);
                            captureImage.startAnimation(rot);
                            if(f==1)
                            {
                                flashCameraButton.startAnimation(rot);
                            }
                        }
                    } else if (orientation < 135 && orientation > 45) {
                        if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                            mOrientation = ORIENTATION_LANDSCAPE_INVERTED;

                            flipCamera.startAnimation(rotlr);
                            captureImage.startAnimation(rotlr);
                            if(f==1)
                            {
                                flashCameraButton.startAnimation(rot);}
                        }
                    }


             //       if (lastOrientation != mOrientation) {
              //          changeRotation(mOrientation, lastOrientation);
               //     }
                }

            };


        }


        if (cOrientationEventListener.canDetectOrientation()) {
            cOrientationEventListener.enable();
        }
    }

    /*private void changeRotation(int orientation, int lastOrientation) {
        switch (orientation) {
            case ORIENTATION_PORTRAIT_NORMAL:
                flipCamera.setImageDrawable(getRotatedImage(R.drawable.flip, 0));
                flashCameraButton.setImageDrawable(getRotatedImage(R.drawable.flash, 0));
                captureImage.setImageDrawable(getRotatedImage(R.drawable.capture, 0));
                Log.v("CameraActivity", "Orientation = 90");
                break;
            case ORIENTATION_LANDSCAPE_NORMAL:
                flipCamera.setImageDrawable(getRotatedImage(R.drawable.flip, 90));
                flashCameraButton.setImageDrawable(getRotatedImage(R.drawable.flash,90));
                captureImage.setImageDrawable(getRotatedImage(R.drawable.capture, 90));
                Log.v("CameraActivity", "Orientation = 0");
                break;
            case ORIENTATION_PORTRAIT_INVERTED:
                flipCamera.setImageDrawable(getRotatedImage(R.drawable.flip, 180));
                flashCameraButton.setImageDrawable(getRotatedImage(R.drawable.flash, 180));
                captureImage.setImageDrawable(getRotatedImage(R.drawable.capture, 180));
                Log.v("CameraActivity", "Orientation = 270");
                break;
            case ORIENTATION_LANDSCAPE_INVERTED:
                flipCamera.setImageDrawable(getRotatedImage(R.drawable.flip, 270));
                flashCameraButton.setImageDrawable(getRotatedImage(R.drawable.flash ,270));
                captureImage.setImageDrawable(getRotatedImage(R.drawable.capture, 270));
                Log.v("CameraActivity", "Orientation = 180");
                break;
        }
    }

    private Drawable getRotatedImage(int drawableId, int degrees) {
        Bitmap original = BitmapFactory.decodeResource(getResources(), drawableId);
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);

        Bitmap rotated = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
        return new BitmapDrawable(rotated);
    }

*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           // case R.id.surfaceView:

           //     captureImage.setEnabled(false);
         //       camera.autoFocus(myAutoFocusCallback);

         //       break;
            case R.id.flash:
                flashOnButton();
                break;


            case R.id.flipCamera:
                flipCamera();
                break;


            case R.id.captureImage:
                takeImage();
                break;

            default:

                break;
        }
    }

 /*   Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
            captureImage.setEnabled(true);
        }};

*/
    @Override protected void onPause() {
        super.onPause();
      //  cOrientationEventListener.disable();
      //  releaseCamera();
    }


    private void takeImage() {

        camera.takePicture( myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);

    }


    ShutterCallback myShutterCallback = new ShutterCallback(){

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }};

    PictureCallback myPictureCallback_RAW = new PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {


        }};





    private PictureCallback myPictureCallback_JPG = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            try
            {
                //new SaveImageTask().execute(arg0);
 /*
               Matrix rotateMatrix = new Matrix();
               switch (mOrientation) {
                   case ORIENTATION_PORTRAIT_NORMAL:
                       rotateMatrix.postRotate(90);
                       break;
                   case ORIENTATION_LANDSCAPE_NORMAL:
                       rotateMatrix.postRotate(0);
                       break;
                   case ORIENTATION_PORTRAIT_INVERTED:
                       rotateMatrix.postRotate(270);
                       break;
                   case ORIENTATION_LANDSCAPE_INVERTED:
                       rotateMatrix.postRotate(180);
                       break;
               }

               Bitmap bitmapPicture = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);


               Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapPicture, 0,
                       0, bitmapPicture.getWidth(), bitmapPicture.getHeight(),
                       rotateMatrix, false);

*/
                          File pictureFile = getOutputMediaFile();
                      if (pictureFile == null) {
                            return;
                        }

                           FileOutputStream fos = new FileOutputStream(pictureFile);
                        fos.write(arg0);
                       fos.close();
                //     String s= UUID.randomUUID().toString() + ".jpg";
                //    createDirectoryAndSaveFile(rotatedBitmap, s );

                //        camera.startPreview();

                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    };



   private static File getOutputMediaFile()
   {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }











    private void setUpCamera(Camera c) {

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;

            default:

                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 360;
            rotation = (360 - rotation) % 360;
        }
        else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
        }



        c.setDisplayOrientation(rotation);
        Parameters params = c.getParameters();
        showFlashButton(params);

        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        //  params.setRotation(90);
        //  camera.setParameters(params);


    }




    private void showFlashButton(Parameters params)
    {
        boolean showFlash = (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)
                && params.getSupportedFlashModes() != null
                && params.getSupportedFocusModes().size() > 1;

        flashCameraButton.setVisibility(showFlash ? View.VISIBLE
                : View.INVISIBLE);

    }





    private void flashOnButton()
    {
        if (camera != null) {
            try {
                Parameters param = camera.getParameters();
                param.setFlashMode(!flashMode ? Parameters.FLASH_MODE_TORCH
                        : Parameters.FLASH_MODE_OFF);
                camera.setParameters(param);
                flashMode = !flashMode;
            } catch (Exception e) {
                //  handle exception
            }

        }
    }



    private void flipCamera()
    {
        int id = (cameraId == CameraInfo.CAMERA_FACING_BACK ? CameraInfo.CAMERA_FACING_FRONT
                : CameraInfo.CAMERA_FACING_BACK);

        if (!openCamera(id))
        {
            alertCameraDialog();
        }
    }




    private void alertCameraDialog() {
        AlertDialog.Builder dialog = createAlert(MainSurface.this,
                "Camera info", "error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private AlertDialog.Builder createAlert(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
        dialog.setIcon(R.drawable.ic_launcher);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }





    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        if (!openCamera(CameraInfo.CAMERA_FACING_BACK)) {
            alertCameraDialog();
        }



    }




    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        releaseCamera();
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            try {
                setUpCamera(camera);


                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                result = true;


            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }



    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }









    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Toast.makeText(getApplicationContext(),
                "Destroyed:\n" +
                        String.valueOf("!!") + " DES " + String.valueOf("!!"),
                Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        releaseCamera();
    }

}

// TODO Bugs-FlipCam Crash Bug,try alpha and disable buttons,Icon size for multiple screen support
//Disable  Change Orientation feature
//TODO get Best Preview of Surface View(More research),Buttons align below SurfaceView
//TODO Optimize Code.
//TODO Features- FOCUS,Auto-Flash,Torch








