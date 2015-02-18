package engine.util;

import engine.Globals;
import java.awt.Shape;
import java.awt.geom.Area;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Util {

    // MATRIX CREATION
    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1), matrix, matrix);
        Matrix4f.scale(scale, matrix, matrix);

        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation) {
        return createTransformationMatrix(translation, rotation, new Vector3f(1, 1, 1));
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale) {
        return createTransformationMatrix(translation, rotation, new Vector3f(scale, scale, scale));
    }

    public static Matrix4f createTransformationMatrix(float x, float y, float z, float rx, float ry, float rz, float sx, float sy, float sz) {
        return createTransformationMatrix(new Vector3f(x, y, z), new Vector3f(rx, ry, rz), new Vector3f(sx, sy, sz));
    }

    public static Matrix4f createTransformationMatrix(float x, float y, float z, float rx, float ry, float rz, float scale) {
        return createTransformationMatrix(new Vector3f(x, y, z), new Vector3f(rx, ry, rz), new Vector3f(scale, scale, scale));
    }

    public static Matrix4f createTransformationMatrix(float x, float y, float z, float rx, float ry, float rz) {
        return createTransformationMatrix(new Vector3f(x, y, z), new Vector3f(rx, ry, rz), new Vector3f(1, 1, 1));
    }

    public static Matrix4f createSpriteTransformationMatrix(float x, float y, double rotation, float width, float height, float priority) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
//        Matrix4f.translate(new Vector3f(x+(width/2f), y+(height/2f), priority), matrix, matrix);
        Matrix4f.translate(new Vector3f(x, -y, priority), matrix, matrix);

        Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 0, 1), matrix, matrix);
        matrix.scale(new Vector3f(width, height, 1.0f));
        return matrix;
    }

    public static Matrix4f createOrthoMatrix(boolean ignore) {
        Matrix4f m = new Matrix4f();
        m.setZero();

        float r = Globals.WIDTH;
        float l = 0;
        float t = 0;
        float b = Globals.HEIGHT;

        float f = -1;
        float n = 1f;

        m.m00 = 2.0f / (r - l);
        m.m01 = 0.0f;
        m.m02 = 0.0f;
        m.m03 = 0.0f;

        m.m10 = 0.0f;
        m.m11 = 2.0f / (t - b);
        m.m12 = 0.0f;
        m.m13 = 0.0f;

        m.m20 = 0.0f;
        m.m21 = 0.0f;
        m.m22 = -2.0f / (f - n);
        m.m23 = 0.0f;

        m.m30 = -((r + l) / (r - l));
        m.m31 = ((t + b) / (t - b));
        m.m32 = -((f + n) / (f - n));
        m.m33 = 1.0f;
        return m;
    }

    public static Matrix4f createOrthoMatrix() {
        
        Matrix4f m = new Matrix4f();
        m.setZero();
        
        //float right = -(Globals.WIDTH / 2f);
        //float left = Globals.WIDTH / 2f;
        //float top = Globals.HEIGHT / 2f;
        //float bottom = -Globals.HEIGHT / 2f;

        float right = -(Globals.WIDTH / 2f);
        float left = (Globals.WIDTH / 2f);
        float top = Globals.HEIGHT / 2f;
        float bottom = -Globals.HEIGHT / 2f;

        float far = -100;
        float near = 0.002f;

        m.m00 = 2.0f / (right - left);
        m.m01 = 0.0f;
        m.m02 = 0.0f;
        m.m03 = 0.0f;

        m.m10 = 0.0f;
        m.m11 = 2.0f / (top - bottom);
        m.m12 = 0.0f;
        m.m13 = 0.0f;

        m.m20 = 0.0f;
        m.m21 = 0.0f;
        m.m22 = -2.0f / (far - near);
        m.m23 = 0.0f;

        m.m30 = -(right + left) / (right - left);
        m.m31 = -(top + bottom) / (top - bottom);
        m.m32 = -(far + near) / (far - near);
        m.m33 = 1.0f;
        return m;
    }

    //OTHER STUFF
    public static boolean isIntersecting(Shape a, Shape b) {
        Area aa = new Area(a);
        aa.intersect(new Area(b));
        return !aa.isEmpty();
    }

    public static boolean isIntersecting(Area a, Area b) {
        a.intersect(b);
        return !a.isEmpty();
    }

    public static Area getIntersection(Area a, Area b) {
        a.intersect(b);
        return a;
    }

    public static Area getIntersection(Shape a, Shape b) {
        Area aa = new Area(a);
        aa.intersect(new Area(b));
        return aa;
    }

    public static Vector2f setMagnitudeOfVector2f(Vector2f vec, float scale) {
        vec.normalise();
        vec.scale(scale);
        return vec;
    }

    public static int r(double num) {
        return (int) Math.round(num);
    }

    public static int r(float num) {
        return Math.round(num);
    }
    
    public static float distanceSq(Vector2f a, Vector2f b) {
        float x = a.x - b.x;
        float y = a.y - b.y;
        
        return x*x + y*y;
    }
    
    public static float distance(Vector2f a, Vector2f b) {
        float x = a.x - b.x;
        float y = a.y - b.y;
        
        return (float) Math.sqrt(x*x + y*y);
    }
    
    public static float angleToward(Vector2f from, Vector2f to) {
        double a = Math.atan2((to.y - from.y), -(to.x - from.x));
        return (float) Math.toDegrees(a);
    }

}
