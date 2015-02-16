package engine.interfaces;

import org.lwjgl.util.vector.Matrix4f;

public interface Shader {
    
    public void loadTransformationMatrix(Matrix4f tMatrix);
    public void start();
    public void stop();
}
