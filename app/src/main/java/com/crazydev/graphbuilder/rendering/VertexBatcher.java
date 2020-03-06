package com.crazydev.graphbuilder.rendering;



import com.crazydev.graphbuilder.math.Vector3D;

import static android.opengl.GLES20.GL_TRIANGLES;

public class VertexBatcher {

    int verticesIndex = 0;

    private VertexBinder vertexBinder;
    private ShaderProgram shaderProgram;

    private int stride;

    public VertexBatcher(ShaderProgram shaderProgram, int maxPointsLines) {
        this.shaderProgram = shaderProgram;

        this.vertexBinder = new VertexBinder(shaderProgram, maxPointsLines);
        this.stride       = vertexBinder.getStride();

    }

    public void depictCurve(Vector3D color, float alpha, float[] lines, int length, int lineType) {

        this.shaderProgram.setBoolFlag(true);
        this.shaderProgram.setColor(color.x, color.y, color.z, alpha);
        this.shaderProgram.setMVPMatrix();

        this.vertexBinder.bindData(true);
        this.vertexBinder.setVertices(lines, 0, length);
        this.vertexBinder.draw(lineType, length / vertexBinder.getStride());
        this.vertexBinder.unbindData(true);

    }

    private int verts = 0;

    public void startDepictShapes() {
        this.vertexBinder.clearNativeArray();
    }

    public void prepareShape(float[] vertices) {
        this.vertexBinder.setShapeVertices(vertices);
        verts += 6;
    }

    public void depictShapes(Texture texture) {

        this.shaderProgram.setBoolFlag(false);
        this.shaderProgram.setMVPMatrix();
        this.shaderProgram.setTexture(texture.texture);

        this.vertexBinder.bindData(false);

        this.vertexBinder.draw(GL_TRIANGLES, verts);
        this.vertexBinder.unbindData(false);

        verts = 0;
    }

}

