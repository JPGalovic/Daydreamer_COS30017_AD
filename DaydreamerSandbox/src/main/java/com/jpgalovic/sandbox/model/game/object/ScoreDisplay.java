package com.jpgalovic.sandbox.model.game.object;

import android.content.Context;

import com.jpgalovic.sandbox.model.game.text.Letter;
import com.jpgalovic.sandbox.model.game.text.NumberObject;
import com.jpgalovic.sandbox.model.util.Score;

import java.util.ArrayList;

public class ScoreDisplay {
    private ArrayList<Letter> letters;
    private ArrayList<NumberObject> numbers;

    /**
     * sets objects for score at given position.
     * @param context               Context reference.
     * @param objectPositionParam   Object Position parameter.
     * @param objectUvParam         Object UV parameter.
     * @param x                     x coordinate of position.
     * @param y                     y coordinate of position.
     * @param z                     z coordinate of position.
     * @param score                 Score.
     */
    public ScoreDisplay(Context context, int objectPositionParam, int objectUvParam, float x, float y, float z, Score score) {
        float characterOffset = 1.2f;
        float centerOffset = 2.0f;

        letters = new ArrayList<>();
        numbers = new ArrayList<>();

        // Add each letter of name.
        for(int i = 0; i < score.getName().length(); i++) {
            letters.add(new Letter(context, objectPositionParam, objectUvParam, x - centerOffset - ((score.getName().length() - i - 1) * characterOffset), y, z, score.getName().charAt(i), false));
        }

        // Add each number of score.
        int value = score.getScore();
        int index = 0;
        while (value > 0) {
            numbers.add(new NumberObject(context, objectPositionParam, objectUvParam, x + centerOffset + ((score.getName().length() - index - 1) * characterOffset), y, z, value % 10, false));
            value = value / 10;
            index++;
        }
    }

    public void draw(float[] perspective, float[] view, int objectProgram, int objectModelViewProjectionParam) {
        for(int i = 0; i < letters.size(); i++) {
            letters.get(i).draw(perspective, view, objectProgram,objectModelViewProjectionParam);
        }
        for(int i = 0; i < numbers.size(); i++) {
            numbers.get(i).draw(perspective, view, objectProgram, objectModelViewProjectionParam);
        }
    }

}
