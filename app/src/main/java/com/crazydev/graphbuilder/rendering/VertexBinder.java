package com.crazydev.graphbuilder.rendering;

import com.crazydev.graphbuilder.util.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;


public class VertexBinder {

    private ShaderProgram shaderProgram;
    private IntBuffer intBuffer;
    private int[] tempBuffer;

    private int STRIDE;

    public VertexBinder(ShaderProgram shaderProgram, int sizeOfVerticesArray) {
        this.shaderProgram = shaderProgram;
        this.tempBuffer = new int[sizeOfVerticesArray];

        intBuffer = ByteBuffer
                .allocateDirect(sizeOfVerticesArray * Integer.SIZE)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer();

    }


    public void setVertices(float[] vertices, int offset, int length) {
        this.intBuffer.clear();
        int len = offset + length;
        for (int i = offset, j = 0; i < len; i++, j++) {
            tempBuffer[j] = Float.floatToRawIntBits(vertices[i]);
        }

        this.intBuffer.put(tempBuffer, offset, length);
    }

    public void setShapeVertices(float[] vertices) {

        int len = vertices.length;
        for (int i = 0; i < len; i++) {
            tempBuffer[i] = Float.floatToRawIntBits(vertices[i]);
        }

        this.intBuffer.put(tempBuffer, 0, len);
    }

    public void clearNativeArray() {
        this.intBuffer.clear();
    }

    public int getStride() {
        return STRIDE / Constants.BYTES_PER_FLOAT;
    }


    public void bindData(boolean useColor) {

        if (useColor) {
            STRIDE = Constants.POSITION_COMPONENT_COUNT_2D * Constants.BYTES_PER_FLOAT;
        } else {
            STRIDE = (Constants.POSITION_COMPONENT_COUNT_2D + Constants.TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;
        }

        int offset = 0;
        intBuffer.position(offset);
        glVertexAttribPointer(shaderProgram.getPositionAttributeLocation(), Constants.POSITION_COMPONENT_COUNT_2D, GL_FLOAT, false, STRIDE, intBuffer);
        glEnableVertexAttribArray(shaderProgram.getPositionAttributeLocation());
        offset += Constants.POSITION_COMPONENT_COUNT_2D;

        if (!useColor) {
            intBuffer.position(offset);
            glVertexAttribPointer(shaderProgram.getTextureCoordinatesAttributeLocation(), Constants.TEXTURE_COORDINATES_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, intBuffer);
            glEnableVertexAttribArray(shaderProgram.getTextureCoordinatesAttributeLocation());
        }

    }

    public void unbindData(boolean useColor) {
        glDisableVertexAttribArray(shaderProgram.getPositionAttributeLocation());

        if (!useColor) {
            glDisableVertexAttribArray(shaderProgram.getTextureCoordinatesAttributeLocation());
        }

    }

    public void draw(int primiriveType, int numVertices) {
        glDrawArrays(primiriveType, 0, numVertices);

    }

}