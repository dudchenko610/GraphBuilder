package com.crazydev.graphbuilder.math;

import android.opengl.Matrix;

import java.util.Random;

public class Vector3D {
    private static final float[] matrix = new float[16];
    private static final float[] inVector = new float[4];
    private static final float[] outVector = new float[4];
    public float x, y, z;

    public Vector3D() {

    }

    public Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector3D copy() {
        return new Vector3D(x, y, z);
    }

    public Vector3D set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3D set(Vector3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vector3D add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3D add(Vector3D other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Vector3D subtract(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vector3D subtract(Vector3D other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public Vector3D multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3D normalize() {
        float length = length();

        if (length != 0) {
            this.x /= length;
            this.y /= length;
            this.z /= length;
        }
        return this;
    }

    public Vector3D rotate(float angle, float axisX, float axisY, float axisZ) {
        inVector[0] = x;
        inVector[1] = y;
        inVector[2] = z;
        inVector[3] = 1;

        Matrix.setIdentityM(matrix, 0);
        Matrix.rotateM(matrix, 0, angle, axisX, axisY, axisZ);
        Matrix.multiplyMV(outVector, 0, matrix, 0, inVector, 0);

        x = outVector[0];
        y = outVector[1];
        z = outVector[2];

        return this;
    }

    public Vector3D rotate(float pitch, float yaw, float roll) {

        float sA = (float) Math.sin(pitch);
        float cA = (float) Math.cos(pitch);

        float sB = (float) Math.sin(yaw);
        float cB = (float) Math.cos(yaw);

        float sG = (float) Math.sin(roll);
        float cG = (float) Math.cos(roll);


        float X = this.x * ( cG * cB + sG * sA * sB) + this.y * sG * cA + this.z * (-sB * cG + sA * sG * cB);
        float Y = this.x * (-sG * cB + sA * sB * cG) + this.y * cG * cA + this.z * ( sB * sG + sA * cB * cG);
        float Z = this.x * ( sB * cA)                - this.y * sA      + this.z * ( cA * cB);

        this.x = X;
        this.y = Y;
        this.z = Z;

        return this;
    }

    public boolean isNullLength() {
        return this.x == 0 && this.y == 0 && this.z == 0;
    }

    public Vector3D crossProduct(Vector3D other) {
        Vector3D cross = new Vector3D(this.y * other.z - this.z * other.y,
                -(this.x * other.z - this.z * other.x),
                this.x * other.y - this.y * other.x );
        return cross;
    }

    public float dotProduct(Vector3D other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public float distance(Vector3D other) {
        float distX = this.x - other.x;
        float distY = this.y - other.y;
        float distZ = this.z - other.z;

        return (float) Math.sqrt(distX * distX + distY * distY + distZ * distZ);
    }

    public float distance(float x, float y, float z) {
        float distX = this.x - x;
        float distY = this.y - y;
        float distZ = this.z - z;

        return (float) Math.sqrt(distX * distX + distY * distY + distZ * distZ);
    }

    public float distanceSquared(Vector3D other) {
        float distX = this.x - other.x;
        float distY = this.y - other.y;
        float distZ = this.z - other.z;

        return distX * distX + distY * distY + distZ * distZ;
    }

    public float distanceSquared(float x, float y, float z) {
        float distX = this.x - x;
        float distY = this.y - y;
        float distZ = this.z - z;

        return distX * distX + distY * distY + distZ * distZ;
    }

    public static Vector3D randomVector() {
        Random r = new Random();

        float x = r.nextFloat();
        float y = r.nextFloat();
        float z = r.nextFloat();

        return new Vector3D(x, y, z);
    }


}
