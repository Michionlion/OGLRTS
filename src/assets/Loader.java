package assets;

import assets.models.RawModel;
import engine.Globals;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Loader {
    
    private static List<Integer> vaos = new ArrayList<>();
    private static List<Integer> vbos = new ArrayList<>();
    private static HashMap<String, Texture> textures = new HashMap<>();
    private static int renderTexture = -1;
    
    public static RawModel loadToVAO(float[] positions, float[] textureCoords, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }
    
    public static Texture getTexture(String fileName) {
        if(textures.containsKey(fileName)) return textures.get(fileName);
        else return loadTexture(fileName);
    }
    
    public static int getTextureID(String fileName) {
        if(textures.containsKey(fileName)) return textures.get(fileName).getTextureID();
        else return loadTexture(fileName).getTextureID();
    }
    
    public static int getRenderTextureID() {
        if(renderTexture < 0) {
            
            renderTexture = GL11.glGenTextures();
//            IntBuffer textureHandle = BufferUtils.createIntBuffer(1);
//            GL11.glGenTextures(textureHandle);
//            renderTexture = textureHandle.get(0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderTexture);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, Globals.WIDTH, Globals.HEIGHT, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (java.nio.ByteBuffer) null);
       //        glTexImage2D(     GL_TEXTURE_2D, 0,      GL_RGBA8,           512,            512, 0,      GL_RGBA,        GL_INT, (java.nio.ByteBuffer) null);
            
            
            
            
            
            
            return renderTexture;
        } else {
            return renderTexture;
        }
    }
    
    private static Texture loadTexture(String fileName) {
        Texture tex = null;
        
        try {
            System.out.println("loaded " + fileName);
            tex = TextureLoader.getTexture("PNG", new FileInputStream("res/art/"+fileName+".png"));
        } catch (IOException ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(tex == null) {
            System.err.println("unable to load texture: " + fileName);
            return null;
        }
        textures.put(fileName, tex);
        return tex;
    }
    
    private static int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }
    
    private static void storeDataInAttributeList(int attNum, int dataSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attNum, dataSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }
    
    private static void unbindVAO() {
        GL30.glBindVertexArray(0);
    }
    
    private static void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
    
    private static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
    
    public static void cleanUp() {
        for(int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        vaos.clear();
        
        for(int vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }
        vbos.clear();
        
        for(Texture tex : textures.values()) {
            GL11.glDeleteTextures(tex.getTextureID());
        }
        textures.clear();
        
        GL11.glDeleteTextures(renderTexture);
    }
}
