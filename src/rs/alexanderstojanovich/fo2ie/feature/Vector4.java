/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.alexanderstojanovich.fo2ie.feature;

/**
 *
 * @author Alexander Stojanovich <coas91@rocketmail.com>
 */
public class Vector4 implements Vector4ifc {

    public int x;
    public int y;
    public int z;
    public int w;

    public Vector4() {

    }

    public Vector4(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public int getW() {
        return w;
    }

    @Override
    public void add(Vector4ifc vec4) {
        x += vec4.getX();
        y += vec4.getY();
        z += vec4.getZ();
        w += vec4.getW();
    }

    @Override
    public void sub(Vector4ifc vec4) {
        x -= vec4.getX();
        y -= vec4.getY();
        z -= vec4.getZ();
        w -= vec4.getW();
    }

    @Override
    public void mul(int scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        w *= scalar;
    }

    @Override
    public void div(int scalar) {
        x /= scalar;
        y /= scalar;
        z /= scalar;
        w /= scalar;
    }

    @Override
    public String getStringValue() {
        return String.valueOf(x)
                + " " + String.valueOf(y)
                + " " + String.valueOf(z)
                + " " + String.valueOf(w);
    }

    @Override
    public Type getType() {
        return Type.VECTOR4;
    }

    @Override
    public void setStringValue(String value) {
        String[] split = value.split(" ");
        if (split.length == 4) {
            x = Integer.valueOf(split[0]);
            y = Integer.valueOf(split[1]);
            z = Integer.valueOf(split[2]);
            w = Integer.valueOf(split[3]);
        }
    }

}
