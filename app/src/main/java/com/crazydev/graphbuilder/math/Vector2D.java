package com.crazydev.graphbuilder.math;


public class Vector2D {

    public static float TO_RADIANS = (1 / 180.0f) * (float) Math.PI;
    public static float TO_DEGREES = (1 / (float) Math.PI) * 180;
    public float x, y;

    public Vector2D () {
    }

    public Vector2D (float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D (Vector2D other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Vector2D copy() {
        return new Vector2D(x, y);
    }

    public Vector2D set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2D turnCounterClockWise() {
        float c = this.x;
        this.x = -this.y;
        this.y = c;
        return this;
    }

    public Vector2D turnClockWise() {
        float x = this.x;
        this.x  = this.y;
        this.y  = -x;

        return this;
    }

    public Vector2D set(Vector2D other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    public Vector2D add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2D add(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector2D subtract(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2D subtract(Vector2D other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public Vector2D subtractUnchanging(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public Vector2D multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public Vector2D multiplyUnchanging(float scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float lengthSquared() {
        return (float) (x * x + y * y);
    }

    public static Vector2D inverseUnchanging(Vector2D other) {
        return new Vector2D(-other.x, -other.y);
    }
    public Vector2D normalize() {
        float length = length();
        if (length != 0) {
            this.x /= length;
            this.y /= length;
        }
        return this;
    }


    public float angle() {
        float angle = (float) Math.atan2(y, x) * TO_DEGREES;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public Vector2D setAngle(float angle) {

        return new Vector2D();
    }

    public Vector2D rotate(float angle) {
//		float rad = angle * TO_RADIANS;
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        float newX = this.x * cos - this.y * sin;
        float newY = this.x * sin + this.y * cos;

        this.x = newX;
        this.y = newY;

        return this;
    }

    public float distance(Vector2D other) {
        float distX = this.x - other.x;
        float distY = this.y - other.y;
        return (float) Math.sqrt(distX * distX + distY * distY);
    }

    public float distance(float x, float y) {
        float distX = this.x - x;
        float distY = this.y - y;

        return (float) Math.sqrt(distX * distX + distY * distY);
    }

    public float distanceSquared(Vector2D other) {
        float distX = this.x - other.x;
        float distY = this.y - other.y;

        return distX * distX + distY * distY;
    }

    public float distanceSquared(float x, float y) {
        float distX = this.x - x;
        float distY = this.y - y;

        return distX * distX + distY * distY;
    }

    // Z magnityde actually

    public static Vector2D crossProduct(float a, Vector2D v) {
        return v.set(-1.0f * a * v.y,
                1.0f * a * v.x);
    }

    public float crossProduct(Vector2D other) {
        return this.x * other.y - this.y * other.x;
    }

    public static float crossProduct(Vector2D v1, Vector2D v2) {
        return v1.x * v2.y - v1.y * v2.x;
    }

    public float dotProduct(Vector2D other) {
        return x * other.x + y * other.y;
    }

    public static float dotProduct(Vector2D v1, Vector2D v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public boolean isNullLength() {
        return this.x == 0 && this.y == 0;
    }

    public static boolean areCoDirected(Vector2D v1, Vector2D v2) {
        return false;
    }
}
