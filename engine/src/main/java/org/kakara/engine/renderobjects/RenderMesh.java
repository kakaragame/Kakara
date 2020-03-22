package org.kakara.engine.renderobjects;

import org.kakara.engine.renderobjects.renderlayouts.Layout;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class RenderMesh {

    protected final int vaoId;
    protected final List<Integer> vboIdList;
    private final int vertexCount;


    //TODO this needs to be redone to combine the blocks.
    public RenderMesh(List<RenderBlock> renderBlocks){
        Layout layout = setupLayout(renderBlocks);

        FloatBuffer posBuffer = null;
        FloatBuffer texCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;

        try{
            vertexCount = layout.getIndices().length;

            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(layout.getVertex().length);
            posBuffer.put(layout.getVertex()).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture Coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            texCoordsBuffer = MemoryUtil.memAllocFloat(layout.getTextureCords().length);
            texCoordsBuffer.put(layout.getTextureCords()).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            //Vertex Normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(layout.getNormal().length);
            vecNormalsBuffer.put(layout.getNormal()).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Indices VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(layout.getIndices().length);
            indicesBuffer.put(layout.getIndices()).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);


        } finally {
            if(posBuffer != null)
                MemoryUtil.memFree(posBuffer);
            if(texCoordsBuffer != null)
                MemoryUtil.memFree(texCoordsBuffer);
            if(vecNormalsBuffer != null)
                MemoryUtil.memFree(vecNormalsBuffer);
            if(indicesBuffer != null)
                MemoryUtil.memFree(indicesBuffer);
        }
    }

    public int getVertexCount(){
        return vertexCount;
    }

    private void initRender(){
        glBindVertexArray(this.vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }

    public void render(){
        initRender();
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        closeRender();
    }

    private void closeRender(){
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    /**
     * Combine all of the meshes
     * @param renderBlocks The blocks to be rendered.
     * @return The layout.
     */
    private Layout setupLayout (List<RenderBlock> renderBlocks){
        float[] positions = {};
        for(RenderBlock rb : renderBlocks){
            float[] secondArray = rb.getLayout().getVertex();
            float[] both = Arrays.copyOf(positions, positions.length+secondArray.length);
            System.arraycopy(secondArray, 0, both, positions.length, secondArray.length);
            positions = both;
        }

        final float[] finalPos = positions;

        float[] texCoords = {};
        for(RenderBlock rb : renderBlocks){
            float[] secondArray = rb.getLayout().getTextureCords();
            // We need to calculate the texture positions based upon the offset of the texture atlas.
            float[] processedArray = new float[secondArray.length];
            for(int i = 0; i < secondArray.length; i++){
                // If the texture cord is for the x-axis.
                if(i % 2 == 0)
                    processedArray[i] = secondArray[i] + rb.getTexture().getXOffset();
                else
                    processedArray[i] = secondArray[i] + rb.getTexture().getYOffset();
            }
            float[] both = Arrays.copyOf(texCoords, texCoords.length+processedArray.length);
            System.arraycopy(processedArray, 0, both, texCoords.length, processedArray.length);
            texCoords = both;
        }

        final float[] finalTexCord = texCoords;

        float[] normals = {};
        for(RenderBlock rb : renderBlocks){
            float[] secondArray = rb.getLayout().getTextureCords();
            float[] both = Arrays.copyOf(normals, normals.length+secondArray.length);
            System.arraycopy(secondArray, 0, both, normals.length, secondArray.length);
            normals = both;
        }

        final float[] finalNormals = normals;

        int[] indicies = {};
        int i = 0;
        for(RenderBlock rb : renderBlocks){
            int[] secondArray = rb.getLayout().getIndices();
            // Get the max value of the current indicies list.
            int max = Collections.max(IntStream.of(indicies).boxed().collect(Collectors.toList()));
            // THIS MAY BE A CAUSE OF ERROR.
            //Map through the second array and make all of the values add on to each other.
            secondArray = IntStream.of(secondArray).map(ind -> ind + max + 1).toArray();
            // Copy the second array
            int[] both = Arrays.copyOf(indicies, indicies.length+secondArray.length);
            System.arraycopy(secondArray, 0, both, indicies.length, secondArray.length);
            indicies = both;
            i++;
        }

        final int[] finalIndices = indicies;


        return new Layout() {
            @Override
            public float[] getVertex() {
                return finalPos;
            }

            @Override
            public float[] getTextureCords() {
                return finalTexCord;
            }

            @Override
            public float[] getNormal() {
                return finalNormals;
            }

            @Override
            public int[] getIndices() {
                return finalIndices;
            }
        };
    }
}
