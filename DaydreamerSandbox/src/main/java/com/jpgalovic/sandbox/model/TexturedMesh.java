package com.jpgalovic.sandbox.model;

import android.content.Context;

import android.opengl.GLES20;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Object Renderer.
 */
public class TexturedMesh {
    private static final String TAG = "TexturedMesh";

    private final FloatBuffer vertices;
    private final FloatBuffer uv;
    private final ShortBuffer indices;
    private final int positionAttrib;
    private final int uvAttrib;

    /**
     * Initalishes mesh from an obj file.
     *
     * @param context Context for loading the .obj file.
     * @param objFilePath Path to the .obj file.
     * @param positionAttrib The position attribute in the shader.
     * @param uvAttrib The uv attribute int the shader.
     */
    public TexturedMesh(Context context, String objFilePath, int positionAttrib, int uvAttrib) throws IOException {
        InputStream objInputStream = context.getAssets().open(objFilePath);
        Obj obj = ObjUtils.convertToRenderable(ObjReader.read(objInputStream));
        objInputStream.close();

        IntBuffer intIndices = ObjData.getFaceVertexIndices(obj, 3); // Gets buffer of face vertex indices
        vertices = ObjData.getVertices(obj); // gets vertices of mesh
        uv = ObjData.getTexCoords(obj, 2); // gets coordinates for texture.

        // Convert intIndicies into shorts (GLES doesn't support int indicies)
        indices = ByteBuffer.allocateDirect(2 * intIndices.limit()).order(ByteOrder.nativeOrder()).asShortBuffer();
        while (intIndices.hasRemaining()) {
            indices.put((short) intIndices.get());
        }
        indices.rewind();

        this.positionAttrib = positionAttrib;
        this.uvAttrib = uvAttrib;
    }

    /**
     * Draws the mesh.
     * Note: before this is called a u_MVP should be set with glUniformMatrix4fv(), and a texture
     * should be bound to GL_TEXTURE0.
     */
    public void draw() {
        GLES20.glEnableVertexAttribArray(positionAttrib);
        GLES20.glVertexAttribPointer(positionAttrib, 3, GLES20.GL_FLOAT, false, 0, vertices);
        GLES20.glEnableVertexAttribArray(uvAttrib);
        GLES20.glVertexAttribPointer(uvAttrib, 2, GLES20.GL_FLOAT,false, 0, uv);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.limit(), GLES20.GL_UNSIGNED_SHORT, indices);
    }
}
