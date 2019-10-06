package com.jpgalovic.daydream.model.object.compound;

import android.content.Context;

import com.jpgalovic.daydream.R;
import com.jpgalovic.daydream.model.object.TexturedMeshObject;
import com.jpgalovic.daydream.model.util.Timer;

import java.util.ArrayList;

public class SevenSegmentTimer {
    private TexturedMeshObject units;
    private TexturedMeshObject tens;
    private TexturedMeshObject hundreds;

    private Timer timer;

    /**
     * Constructs Three Digit Seven Segment Timer
     * @param context                   Application Context
     * @param objectPositionParam       Object Position Parameter.
     * @param objectUVParam             Object UV Parameter.
     * @param x                         X Coordinate.
     * @param y                         Y Coordinate.
     * @param z                         Z Coordinate.
     */
    public SevenSegmentTimer(Context context, int objectPositionParam, int objectUVParam, float x, float y, float z) {
        float offset = 1.9f;

        units = new TexturedMeshObject(context, "OBJECT_UNITS", false, context.getResources().getString(R.string.obj_seven_segment_obj), context.getResources().getStringArray(R.array.obj_seven_segment_tex), objectPositionParam, objectUVParam, x + offset, y, z, 0.0f, 0.0f, 0.0f);
        tens = new TexturedMeshObject(context, "OBJECT_TENS", false, context.getResources().getString(R.string.obj_seven_segment_obj), context.getResources().getStringArray(R.array.obj_seven_segment_tex), objectPositionParam, objectUVParam, x, y, z, 0.0f, 0.0f, 0.0f);
        hundreds = new TexturedMeshObject(context, "OBJECT_HUNDREDS", false, context.getResources().getString(R.string.obj_seven_segment_obj), context.getResources().getStringArray(R.array.obj_seven_segment_tex), objectPositionParam, objectUVParam, x - offset, y, z, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Renders timer with current count
     * @param perspective                       Perspective.
     * @param view                              View.
     * @param objectProgram                     Object Program.
     * @param objectModelViewProjectionParam    Object Model View Projection Parameter.
     */
    public void render(float[] perspective, float[] view, int objectProgram, int objectModelViewProjectionParam) {
        int count = timer.getCount();
        ArrayList<Integer> counts = new ArrayList<>();
        while (count > 0) {
            counts.add(count % 10);
            count = count / 10;
        }

        // When count >= 1000, render number 999.
        if(counts.size() > 3) {
            hundreds.render(perspective, view, 9, objectProgram, objectModelViewProjectionParam);
            tens.render(perspective, view, 9, objectProgram, objectModelViewProjectionParam);
            units.render(perspective, view, 9, objectProgram, objectModelViewProjectionParam);
        } else {
            // Count < 1000 and > 100.
            if(counts.size() == 3) {
                hundreds.render(perspective, view, counts.get(2), objectProgram, objectModelViewProjectionParam);
                tens.render(perspective, view, counts.get(1), objectProgram, objectModelViewProjectionParam);
                units.render(perspective, view, counts.get(0), objectProgram, objectModelViewProjectionParam);
            // Count < 100 and > 10. hundreds place uses "off" texture (10).
            } else if(counts.size() == 2) {
                hundreds.render(perspective, view, 10, objectProgram, objectModelViewProjectionParam);
                tens.render(perspective, view, counts.get(1), objectProgram, objectModelViewProjectionParam);
                units.render(perspective, view, counts.get(0), objectProgram, objectModelViewProjectionParam);
            // Count < 10 and > 0. hundreds and tens place uses "off" texture (10).
            } else if(counts.size() == 1) {
                hundreds.render(perspective, view, 10, objectProgram, objectModelViewProjectionParam);
                tens.render(perspective, view, 10, objectProgram, objectModelViewProjectionParam);
                units.render(perspective, view, counts.get(0), objectProgram, objectModelViewProjectionParam);
            // Count < 100 and > 10. hundreds and tens place uses "off" texture (10). units place displays 0.
            } else {
                hundreds.render(perspective, view, 10, objectProgram, objectModelViewProjectionParam);
                tens.render(perspective, view, 10, objectProgram, objectModelViewProjectionParam);
                units.render(perspective, view, 0, objectProgram, objectModelViewProjectionParam);
            }
        }
    }

    /**
     * Starts the timer with the given number of seconds.
     * @param count number of seconds to start counter at
     */
    public void start(int count) {
        timer = new Timer(count);
        timer.start();
    }

    /**
     * Restarts the timer with the added count.
     * @param count number of seconds to add.
     */
    public void add(int count) {
        count += timer.getCount();
        start(count);
    }

    /**
     * Checks if timer count is zero.
     * @return true if timer count == 0.
     */
    public boolean zero() {
        return timer.zero();
    }
}