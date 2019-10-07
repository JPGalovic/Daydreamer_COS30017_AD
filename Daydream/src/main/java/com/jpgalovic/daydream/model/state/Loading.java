package com.jpgalovic.daydream.model.state;

import android.content.Context;

import com.jpgalovic.daydream.Data;
import com.jpgalovic.daydream.model.State;

public class Loading extends State {
    // Object Data
    // TODO: Add Object Data Here

    // State Data
    // TODO: Add State Data Here

    public Loading(Context context) {
        super("STATE_LOADING", context);
    }

    @Override
    public void init(int positionAttribute, int uvAttribute) {
        // TODO: Initialise each Object and StateData.
    }

    @Override
    public void input(float[] headView) {
        // TODO: Process User Inputs.
    }

    @Override
    public State update(int positionAttribute, int uvAttribute) {
        // Load init other states, then load navigation state.
        if(Data.flag_textures_loaded) {
            for(int i = 0; i < connected.size(); i++) {
                connected.get(i).init(positionAttribute, uvAttribute);
            }
            connected.get(0).onDisplay(positionAttribute, uvAttribute);
            return connected.get(0);
        }
        return this;
    }

    @Override
    public void render(float[] perspective, float[] view, float[] headView, int objectProgram, int objectModelViewProjectionParam) {
        // TODO: Render Scene
    }
}