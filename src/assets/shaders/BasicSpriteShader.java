package assets.shaders;

import engine.Globals;
import engine.interfaces.Shader;
import engine.interfaces.SpriteShader;
import engine.util.Util;
import org.lwjgl.util.vector.Matrix4f;

public class BasicSpriteShader extends ShaderProgram implements SpriteShader {
    
    private static final String VERTEX_FILE = "sprite.vert";
    private static final String FRAGMENT_FILE = "sprite.frag";
    
    private int location_transformationMatrix;

    public BasicSpriteShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        start();
        loadMatrix(super.getUniformLocation("projectionMatrix"), Util.createOrthoMatrix(true));
        stop();
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }
    
    @Override
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }
}