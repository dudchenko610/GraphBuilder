package com.crazydev.graphbuilder.rendering;


import com.crazydev.graphbuilder.math.Vector2D;

public class Sprite {

    private VertexBatcher vertexBatcher;
    private TextureRegion textureRegion;
    private Vector2D position = new Vector2D();
    private float width, height;

    private float[] vertices = new float[24];

    public Sprite (VertexBatcher vertexBatcher, TextureRegion textureRegion, Vector2D position, float width, float height) {
        this.vertexBatcher = vertexBatcher;
        this.textureRegion = textureRegion;
        this.position.set(position);
        this.width         = width;
        this.height        = height;

        setPosition(position);

    }

    public void setPosition(float x, float y, float width, float height) {
        this.width  = width;
        this.height = height;
        position.set(x, y);

        setPosition(position);
    }

    public void setPosition(Vector2D position, float width, float height) {
        this.width  = width;
        this.height = height;

        setPosition(position);
    }

    public void setPosition(Vector2D position) {

        float x1 = position.x - width / 2;
        float y1 = position.y - height / 2;
        float x2 = position.x + width / 2;
        float y2 = position.y + height / 2;

        int index = 0;

        vertices[index ++] = x1;
        vertices[index ++] = y1;
        vertices[index ++] = textureRegion.u1;
        vertices[index ++] = textureRegion.v2;  // 0

        vertices[index ++] = x2;
        vertices[index ++] = y1;
        vertices[index ++] = textureRegion.u2;
        vertices[index ++] = textureRegion.v2;  // 1

        vertices[index ++] = x2;
        vertices[index ++] = y2;
        vertices[index ++] = textureRegion.u2;
        vertices[index ++] = textureRegion.v1; // 2

        vertices[index ++] = x2;
        vertices[index ++] = y2;
        vertices[index ++] = textureRegion.u2;
        vertices[index ++] = textureRegion.v1; // 2

        vertices[index ++] = x1;
        vertices[index ++] = y2;
        vertices[index ++] = textureRegion.u1;
        vertices[index ++] = textureRegion.v1; // 3

        vertices[index ++] = x1;
        vertices[index ++] = y1;
        vertices[index ++] = textureRegion.u1;
        vertices[index ++] = textureRegion.v2;  // 0


    }

    public void draw() {
        this.vertexBatcher.prepareShape(vertices);
    }




}
