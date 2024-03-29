package com.jpgalovic.daydream;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.base.AndroidCompat;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

import com.jpgalovic.daydream.model.state.FindTheBlock;
import com.jpgalovic.daydream.model.state.HighScores;
import com.jpgalovic.daydream.model.state.Loading;
import com.jpgalovic.daydream.model.state.Navigation;
import com.jpgalovic.daydream.model.State;
import com.jpgalovic.daydream.model.state.NewHighScore;
import com.jpgalovic.daydream.model.state.SoundBytes;
import com.jpgalovic.daydream.model.util.FileManager;
import com.jpgalovic.daydream.model.util.Util;
import com.jpgalovic.daydream.model.util.Values;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class GvrActivityBase extends GvrActivity implements GvrView.StereoRenderer{
    private static final String TAG="ACTIVITY_BASE";

    // OpenGL Parameters
    private int objectProgram;
    private int objectModelViewProjectionParam;
    private int objectPositionParam;
    private int objectUvParam;

    // Cameras, Views and Projection Mapping
    private float[] camera;
    private float[] view;

    private float[] headView;
    private float[] headRotation;

    // State Engine Components
    private State state;

    private Navigation navigation;
    private HighScores highScores;
    private NewHighScore newHighScore;
    private FindTheBlock findTheBlock;
    private SoundBytes soundBytes;

    private Loading loading;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Initialise Cameras, Views and Projection Mapping
        // Note: vectors are in form of float array as required by OpenGL
        camera = new float[16];
        view = new float[16];

        headView = new float[16];
        headRotation = new float[16];

        // Construct State Engine Components.
        loading = new Loading(this);
        navigation = new Navigation(this);
        highScores = new HighScores(this);
        newHighScore = new NewHighScore(this);
        findTheBlock = new FindTheBlock(this);
        soundBytes = new SoundBytes(this);

        // Link States.
        loading.addConnection(navigation);
        loading.addConnection(highScores);
        loading.addConnection(newHighScore);
        loading.addConnection(findTheBlock);
        loading.addConnection(soundBytes);

        navigation.addConnection(highScores);
        navigation.addConnection(findTheBlock);
        navigation.addConnection(soundBytes);

        findTheBlock.addConnection(newHighScore);
        findTheBlock.addConnection(highScores);

        highScores.addConnection(navigation);

        newHighScore.addConnection(highScores);

        soundBytes.addConnection(navigation);

        // Initialise GVR View.
        initialiseGvrView();

        // Initialise Assets
        FileManager.copyAssets(this);

        AppCenter.start(getApplication(), "b60f34e4-55a7-4713-8871-241447afa7fb", Analytics.class, Crashes.class);
    }

    /**
     * Initialises Google VR View.
     */
    private void initialiseGvrView() {
        // Initialise the GvrView.
        setContentView(R.layout.common_ui);
        GvrView gvrView = findViewById(R.id.gvr_view);

        gvrView.setEGLConfigChooser(8,8,8,8,16,8);
        gvrView.setRenderer(this);
        gvrView.setTransitionViewEnabled(true);
        
        // Enable Cardboard-trigger feedback.
        gvrView.enableCardboardTriggerEmulation();

        // Enable async re-projection to de-sync the app frame-rate from the display frame-rate,
        // this allows for smoother and more immersive interaction in the event that the
        // clock-rate becomes throttled.
        if (gvrView.setAsyncReprojectionEnabled(true)) {
            AndroidCompat.setSustainedPerformanceMode(this, true);
        }

        setGvrView(gvrView);
    }

    /**
     * Handles onNewFrame
     * @param headTransform contains transformations since last frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        // Build camera matrix.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f);

        // Get headView.
        headTransform.getHeadView(headView, 0);

        // Update Audio Engine
        headTransform.getQuaternion(headRotation, 0);
        Data.audio_engine.setHeadRotation(headRotation[0], headRotation[1], headRotation[2], headRotation[3]);
        Data.audio_engine.update();

        Util.checkGLError("onNewFrame");
    }

    /**
     * Draws a frame for a given eye.
     * @param eye the eye to render, includes all transformations.
     */
    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Clear screen, colour may or may not matter depending on weather background is fully
        // obstructed by an object, however the buffer should still be cleared to improve performance.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Apply the transformations to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);
        float[] perspective = eye.getPerspective(Values.Z_NEAR, Values.Z_FAR);

        // Draw each object.
        state.render(perspective, view, headView, objectProgram, objectModelViewProjectionParam);
    }

    /**
     * Handles onFinishFrame
     */
    @Override
    public void onFinishFrame(Viewport viewport) {
        state = state.update();
    }

    /**
     * Creates buffers needed to store information about the 3D environment.
     * Note: OpenGL doesn't use Java arrays, the data needs to be correctly formatted.
     * @param eglConfig the EGL configuration used when creating the surface.
     */
    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        Log.i(TAG, "onSurfaceCreated");

        // Set OpenGL Parameters
        GLES20.glClearColor(32.0f, 64.0f, 64.0f, 128.0f);

        objectProgram = Util.compileProgram(Values.OBJECT_VERTEX_SHADER, Values.OBJECT_FRAGMENT_SHADER);

        objectPositionParam = GLES20.glGetAttribLocation(objectProgram, "a_Position");
        objectUvParam = GLES20.glGetAttribLocation(objectProgram, "a_UV");
        objectModelViewProjectionParam = GLES20.glGetUniformLocation(objectProgram, "u_MVP");

        Util.checkGLError("onSurfaceCreated");

        // Load Data
        Data.initialise(this, objectPositionParam, objectUvParam);
        loading.init();
        Data.loadAssets(this, objectPositionParam, objectUvParam);
        state = loading;
    }

    @Override
    public void onSurfaceChanged(int i, int i1) {
        Log.i(TAG,"onSurfaceChanged");
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }

    /**
     * Handles Google Cardboard Trigger.
     */
    @Override
    public void onCardboardTrigger() {
        Log.i(TAG, "onCardboardTrigger");

        state.input(headView);

        super.onCardboardTrigger();
    }
}
