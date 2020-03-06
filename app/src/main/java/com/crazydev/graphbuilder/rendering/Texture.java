package com.crazydev.graphbuilder.rendering;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import com.crazydev.graphbuilder.io.IOManager;

import java.io.IOException;
import java.io.InputStream;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;

public class Texture {

    private IOManager ioManager;
    private String fileName;
    public int texture;
    int minFilter;
    int magFilter;
    public int width;
    public int height;


    public Texture(IOManager ioManager, String fileName) {
        this.ioManager = ioManager;
        this.fileName = fileName;
        load();
    }

    private void load() {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        texture = textureObjectIds[0];

        InputStream in = null;

        try {
            in = ioManager.readAsset(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            width = bitmap.getWidth();
            height = bitmap.getHeight();

            glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

      /*      GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);*/

            setFilters(GL_NEAREST, GL_LINEAR);
            glBindTexture(GL_TEXTURE_2D, 0);
            bitmap.recycle();

        } catch (IOException e) {
            Log.d("fdg", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }
        }

    }

    public void setFilters(int minFilter, int magFilter) {
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter); // mipmapping
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
    }

    public void reload() {
        load();
        //	bind();
        setFilters(minFilter, magFilter);
        //	glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public void dispose() {
        glBindTexture(GL_TEXTURE_2D, texture);
        int[] textureIds = {texture};
        glDeleteTextures(1, textureIds, 0);
    }
}
