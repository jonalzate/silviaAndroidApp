package com.neurala.silvia;


import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jalzate on 4/4/17.
 */

public class VideoFragment extends Fragment
{

    // camera device
    private CameraDevice mCamera;
    // camera ID (to distinguish between rear and front facing cameras of device)
    private String mCameraId;
    // The view which will display the preview
    private AutoFitTextureView mTextureView;
    // the surface to where the preview will be drawn
    private Surface mPreviewSurface;
    // The supported sizes by the camera (ex. 1280*720, 1024*768, etc.) Must be set
    private Size[] mSizes;
    // Builder to create a request for a camera capture. Not needed here?
    private CaptureRequest.Builder mRequestBuilder;


    // constructor
    public static VideoFragment newInstance(){return new VideoFragment();}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle SavedInstanceState)
    {
        // inflate the layout
       return inflater.inflate(R.layout.frag_video, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        // initialize the texture view and set a listener
        mTextureView = (AutoFitTextureView) getActivity().findViewById(R.id.texture);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    }

    // Texture view listener to get notified of events happening on texture view
    private AutoFitTextureView.SurfaceTextureListener mSurfaceTextureListener = new AutoFitTextureView.SurfaceTextureListener()
            {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width,
                                                      int height) {
                    // Surface available, defined the preview surface
                    mPreviewSurface = new Surface(surfaceTexture);

                    // get an instance of camera manager
                    CameraManager manager =(CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);

                    try{
                        // get id for rear facing camera
                        for (final String cameraId : manager.getCameraIdList())
                        {
                            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                            int cameraOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                            if(cameraOrientation == CameraCharacteristics.LENS_FACING_BACK)
                            {
                                mCameraId = cameraId;
                            }
                        }

                        // get camera characteristics for rear facing camera
                        CameraCharacteristics mCameraChars = manager.getCameraCharacteristics(mCameraId);

                        // get the map that contains all supported sizes
                        StreamConfigurationMap streamMap =
                                mCameraChars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                        if (streamMap != null) {
                            mSizes = streamMap.getOutputSizes(SurfaceTexture.class);
                        }

                        manager.openCamera(mCameraId, mCameraDeviceCallback, null);

                    } catch (CameraAccessException e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

                }
            };

    // Callback to notify the status of camera device
    CameraDevice.StateCallback mCameraDeviceCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            // create capture session
            mCamera = camera;

            try {
                // used to create the surface for preview
                SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();

                surfaceTexture.setDefaultBufferSize(mSizes[2].getWidth(), mSizes[2].getHeight());

                // list of surfaces to which we would like to receive the preview. Can specify more than one
                List<Surface> surfaces = new ArrayList<>();
                surfaces.add(mPreviewSurface);

                // forward request for the camera. live preview
                mRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mRequestBuilder.addTarget(mPreviewSurface);

                // camera created. Capture session is where preview will start
                camera.createCaptureSession(surfaces, cameraCaptureSessionStateCallback, new Handler());

            }catch (CameraAccessException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {

        }
    };

    // The camera capture session state callback class. This is where the preview is set and started
    CameraCaptureSession.StateCallback cameraCaptureSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session)
        {
            try{
                // set a repeating request for images
                session.setRepeatingRequest(mRequestBuilder.build(), cameraCaptureSessionCallback, new Handler());
            } catch (CameraAccessException e){
                e.printStackTrace();
            }

        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

        }
    };

    private CameraCaptureSession.CaptureCallback cameraCaptureSessionCallback =
            new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request,
                                            long timestamp, long frameNumber){
                    super.onCaptureStarted(session, request, timestamp, frameNumber);

                }

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result){
                    super.onCaptureCompleted(session, request, result);
                }

                @Override
                public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure){
                    super.onCaptureFailed(session, request, failure);
                }
    };

    public void onPause(){
        super.onPause();

        if (mCamera != null){
            mCamera.close();
        }
    }


}
