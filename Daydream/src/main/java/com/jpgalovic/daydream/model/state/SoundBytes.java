package com.jpgalovic.daydream.model.state;

import android.content.Context;

import com.jpgalovic.daydream.Data;
import com.jpgalovic.daydream.R;
import com.jpgalovic.daydream.model.State;
import com.jpgalovic.daydream.model.object.drawable.TexturedMeshObject;
import com.jpgalovic.daydream.model.util.Timer;

public class SoundBytes extends State {
    // Object Data
    private TexturedMeshObject objectSpeakerLeft;
    private TexturedMeshObject objectSpeakerRight;

    private TexturedMeshObject objectBackButton;

    // State Data
    private Timer timer;

    private boolean FLAG_EXIT;

    public SoundBytes(Context context) {
        super("STATE_SOUND_BYTES", context);
    }

    @Override
    public void onDisplay() {
        timer = new Timer(3);
        timer.start();

        FLAG_EXIT = false;
    }

    @Override
    public void init() {
        objectSpeakerLeft = new TexturedMeshObject("OBJ_SPEAKER_FRONT_LEFT", false, Data.getMesh(context, R.array.OBJ_SPEAKER), Data.getTextures(context, R.array.OBJ_SPEAKER), -5.0f, 0.0f, -5.0f, 0.0f, 30.0f, 0.0f);
        objectSpeakerRight = new TexturedMeshObject("OBJ_SPEAKER_FRONT_LEFT", false, Data.getMesh(context, R.array.OBJ_SPEAKER), Data.getTextures(context, R.array.OBJ_SPEAKER), 5.0f, 0.0f, -5.0f, 0.0f, -30.0f, 0.0f);

        objectSpeakerLeft.setAudio(context.getResources().getStringArray(R.array.ADO_FILES)[0]); // Left Sample File
        objectSpeakerRight.setAudio(context.getResources().getStringArray(R.array.ADO_FILES)[1]); // Right Audio Sample File

        objectBackButton = new TexturedMeshObject("OBJ_BACK", false, Data.getMesh(context, R.array.OBJ_LABEL_BACK), Data.getTextures(context, R.array.OBJ_LABEL_BACK), 0.0f, 0.0f, -5.0f, 0.0f, 0.0f, 0.0f);
        objectBackButton.setScale(0.5f, 0.5f, 0.5f);
    }

    @Override
    public void input(float[] headView) {
        if(objectBackButton.isLookedAt(headView)) {
            FLAG_EXIT = true;
        }
    }

    @Override
    public State update() {
        if(timer.zero()) {
            timer = new Timer(10); // 10 second timer (rough length of audio track)
            timer.start();
            objectSpeakerLeft.playAudio(false);
            objectSpeakerRight.playAudio(false);
        }

        if(FLAG_EXIT == true) {
            objectSpeakerLeft.stopAudio();
            objectSpeakerRight.stopAudio();

            connected.get(0).onDisplay();
            return connected.get(0);
        }

        return this;
    }

    @Override
    public void render(float[] perspective, float[] view, float[] headView, int objectProgram, int objectModelViewProjectionParam) {
        objectSpeakerLeft.render(perspective, view, 0, objectProgram, objectModelViewProjectionParam);
        objectSpeakerRight.render(perspective, view, 0, objectProgram, objectModelViewProjectionParam);
        objectBackButton.render(perspective, view, headView, objectProgram, objectModelViewProjectionParam);
    }
}
