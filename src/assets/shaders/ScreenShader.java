package assets.shaders;

import engine.interfaces.SpriteShader;
import engine.util.Util;
import org.lwjgl.util.vector.Matrix4f;

public class ScreenShader extends ShaderProgram implements SpriteShader {

    private static final String VERTEX_FILE = "screen_shader.vert";
    private static final String FRAGMENT_FILE = "screen_shader.frag";
    
    private int location_transformationMatrix;
    private int location_antiAliasingOn;
    
    public ScreenShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        start();
        loadMatrix(super.getUniformLocation("projectionMatrix"), Util.createOrthoMatrix());
        setAA(false);
        stop();
    }
    
    public ScreenShader(boolean aaOn) {
        super(VERTEX_FILE, FRAGMENT_FILE);
        start();
        loadMatrix(super.getUniformLocation("projectionMatrix"), Util.createOrthoMatrix());
        setAA(aaOn);
        stop();
    }
    
    
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_antiAliasingOn = super.getUniformLocation("antiAliasingOn");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    @Override
    public void loadTransformationMatrix(Matrix4f tMatrix) {
        super.loadMatrix(location_transformationMatrix, tMatrix);
    }
    
    public final void setAA(boolean aaOn) {
        super.loadBoolean(location_antiAliasingOn, aaOn);
    }
    
}
