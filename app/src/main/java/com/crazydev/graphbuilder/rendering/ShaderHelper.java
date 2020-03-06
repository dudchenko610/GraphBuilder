package com.crazydev.graphbuilder.rendering;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

public class ShaderHelper {

    private final static String TAG = "ShaderUtils";

    private static int compileVertexShader(String shaderCode) {
        return  compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    private static int compileFragmentShader(String shaderCode) {
        return  compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String code) {
        int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0) {

            Log.w(TAG, "Could not create new shader");

            return 0;
        }

        glShaderSource(shaderObjectId, code);
        glCompileShader(shaderObjectId);

        int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);


        Log.v(TAG,  "Results of compiling source: " + "\n" + code + "\n"
                + glGetShaderInfoLog(shaderObjectId));


        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId);

            Log.w(TAG, "Compilation of shader falied");

            return 0;
        }
        return shaderObjectId;
    }

    private static int linkProgram (int vertexShaderId, int fragmentShaderId) {
        int programObjectId = glCreateProgram();
        if (programObjectId == 0) {

            Log.w(TAG, "Could not create new program");

            return 0;
        }

        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);

        glLinkProgram(programObjectId);
        int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        Log.v(TAG, "Results of linking program:\n" +
                glGetProgramInfoLog(programObjectId));


        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId);

            Log.w(TAG, "Linking of program failed.");

            return 0;
        }
        return programObjectId;
    }

    private static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog:" + glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }

    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource) {
        int program;

        // compile the shaders
        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        // link them into a shader program
        program = linkProgram(vertexShader, fragmentShader);


        validateProgram(program);

        return program;
    }
}