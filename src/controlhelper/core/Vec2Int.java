package controlhelper.core;

import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.math.geom.Vector;

public class Vec2Int implements Vector<Vec2Int>, Position {
    public int x;
    public int y;

    public Vec2Int() {
    }

    public Vec2Int(Point2 p) {
        this.x = p.x;
        this.y = p.y;
    }

    public Vec2Int(Vec2 v) {
        set(v);
    }

    public Vec2Int(Vec2Int v) {
        set(v);
    }

    public Vec2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Vec2Int cpy() {
        return new Vec2Int(this);
    }

    @Override
    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public float len2() {
        return x * x + y * y;
    }

    @Override
    public Vec2Int limit(float limit) {
        return limit2(limit * limit);
    }

    @Override
    public Vec2Int limit2(float limit2) {
        float len2 = len2();
        if (len2 > limit2) {
            return scl((float) Math.sqrt(limit2 / len2));
        }
        return this;
    }

    @Override
    public Vec2Int setLength(float len) {
        return setLength2(len * len);
    }

    @Override
    public Vec2Int setLength2(float len2) {
        float oldLen2 = len2();
        return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float) Math.sqrt(len2 / oldLen2));
    }

    @Override
    public Vec2Int clamp(float min, float max) {
        final float len2 = len2();
        if (len2 == 0f)
            return this;
        float max2 = max * max;
        if (len2 > max2)
            return scl((float) Math.sqrt(max2 / len2));
        float min2 = min * min;
        if (len2 < min2)
            return scl((float) Math.sqrt(min2 / len2));
        return this;
    }

    public Vec2Int set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vec2Int set(float x, float y) {
        return set((int) x, (int) y);
    }

    public Vec2Int set(Vec2 v) {
        return set(v.x, v.y);
    }

    @Override
    public Vec2Int set(Vec2Int v) {
        return set(v.x, v.y);
    }

    @Override
    public Vec2Int sub(Vec2Int v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    @Override
    public Vec2Int nor() {
        float len = len();
        if (len != 0) {
            x /= len;
            y /= len;
        }
        return this;
    }

    @Override
    public Vec2Int add(Vec2Int v) {
        x += v.x;
        y += v.y;
        return this;
    }

    @Override
    public float dot(Vec2Int v) {
        return x * v.x + y * v.y;
    }

    @Override
    public Vec2Int scl(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    @Override
    public Vec2Int scl(Vec2Int v) {
        x *= v.x;
        y *= v.y;
        return this;
    }

    @Override
    public Vec2Int div(Vec2Int other) {
        x /= other.x;
        y /= other.y;
        return this;
    }

    public Vec2Int div(int other) {
        x /= other;
        y /= other;
        return this;
    }

    @Override
    public float dst(Vec2Int v) {
        final float x_d = v.x - x;
        final float y_d = v.y - y;
        return (float) Math.sqrt(x_d * x_d + y_d * y_d);
    }

    @Override
    public float dst2(Vec2Int v) {
        final float x_d = v.x - x;
        final float y_d = v.y - y;
        return x_d * x_d + y_d * y_d;
    }

    @Override
    public Vec2Int lerp(Vec2Int target, float alpha) {
        final float invAlpha = 1.0f - alpha;
        this.x = (int) (x * invAlpha) + (int) (target.x * alpha);
        this.y = (int) (y * invAlpha) + (int) (target.y * alpha);
        return this;
    }

    @Override
    public Vec2Int interpolate(Vec2Int target, float alpha, Interp interpolator) {
        return lerp(target, interpolator.apply(alpha));
    }

    @Override
    public Vec2Int setToRandomDirection() {
        int theta = (int) Mathf.random(0, Mathf.PI2);
        return this.set(Mathf.cos(theta), Mathf.sin(theta));
    }

    @Override
    public boolean isUnit() {
        return isUnit(0.000000001f);
    }

    @Override
    public boolean isUnit(float margin) {
        return Math.abs(len2() - 1) < margin;
    }

    @Override
    public boolean isZero() {
        return x == 0 && y == 0;
    }

    @Override
    public boolean isZero(float margin) {
        return len2() < margin;
    }

    @Override
    public boolean isOnLine(Vec2Int other, float epsilon) {
        return Mathf.zero(x * other.y - y * other.x, epsilon);
    }

    @Override
    public boolean isOnLine(Vec2Int other) {
        return Mathf.zero(x * other.y - y * other.x);
    }

    @Override
    public boolean isCollinear(Vec2Int other, float epsilon) {
        return isOnLine(other, epsilon) && dot(other) > 0f;
    }

    @Override
    public boolean isCollinear(Vec2Int other) {
        return isOnLine(other) && dot(other) > 0f;
    }

    @Override
    public boolean isCollinearOpposite(Vec2Int other, float epsilon) {
        return isOnLine(other, epsilon) && dot(other) < 0f;
    }

    @Override
    public boolean isCollinearOpposite(Vec2Int other) {
        return isOnLine(other) && dot(other) < 0f;
    }

    @Override
    public boolean isPerpendicular(Vec2Int other) {
        return Mathf.zero(dot(other));
    }

    @Override
    public boolean isPerpendicular(Vec2Int other, float epsilon) {
        return Mathf.zero(dot(other), epsilon);
    }

    @Override
    public boolean hasSameDirection(Vec2Int other) {
        return dot(other) > 0;
    }

    @Override
    public boolean hasOppositeDirection(Vec2Int other) {
        return dot(other) < 0;
    }

    @Override
    public boolean epsilonEquals(Vec2Int other, float epsilon) {
        if (other == null)
            return false;
        if (Math.abs(other.x - x) > epsilon)
            return false;
        return !(Math.abs(other.y - y) > epsilon);
    }

    @Override
    public Vec2Int mulAdd(Vec2Int v, float scalar) {
        this.x += v.x * scalar;
        this.y += v.y * scalar;
        return this;
    }

    @Override
    public Vec2Int mulAdd(Vec2Int v, Vec2Int mulVec) {
        this.x += v.x * mulVec.x;
        this.y += v.y * mulVec.y;
        return this;
    }

    @Override
    public Vec2Int setZero() {
        this.x = 0;
        this.y = 0;
        return this;
    }

    public boolean samePos(Vec2Int v) {
        return samePos(v.x, v.y);
    }

    public boolean samePos(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public Vec2Int abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }
}