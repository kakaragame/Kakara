package org.kakara.engine.renderobjects;

import org.kakara.engine.math.Vector3;
import org.kakara.engine.renderobjects.renderlayouts.Layout;
import org.kakara.engine.renderobjects.renderlayouts.MeshLayout;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class RenderMesh {

    protected final int vaoId;
    protected final List<Integer> vboIdList;
    private final int vertexCount;


    public RenderMesh(List<RenderBlock> renderBlocks, TextureAtlas textureAtlas) {
        MeshLayout layout = setupLayout(renderBlocks, textureAtlas);

        FloatBuffer posBuffer = null;
        FloatBuffer texCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;

        try {
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
            texCoordsBuffer = MemoryUtil.memAllocFloat(layout.getTextCoords().length);
            texCoordsBuffer.put(layout.getTextCoords()).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            //Vertex Normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(layout.getNormals().length);
            vecNormalsBuffer.put(layout.getNormals()).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Indices VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(layout.getIndices().length);
            indicesBuffer.put(layout.getIndices()).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);


        } finally {
            if (posBuffer != null)
                MemoryUtil.memFree(posBuffer);
            if (texCoordsBuffer != null)
                MemoryUtil.memFree(texCoordsBuffer);
            if (vecNormalsBuffer != null)
                MemoryUtil.memFree(vecNormalsBuffer);
            if (indicesBuffer != null)
                MemoryUtil.memFree(indicesBuffer);
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private void initRender() {
        glBindVertexArray(this.vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }

    public void render() {
        initRender();
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        closeRender();
    }

    private void closeRender() {
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
     *
     * @param renderBlocks The blocks to be rendered.
     * @return The layout.
     */
    private MeshLayout setupLayout(List<RenderBlock> renderBlocks, TextureAtlas textureAtlas) {
        float[] positions = {};
        float[] texCoords = {};
        float[] normals = {};
        int[] indicies = {};
        int count = 0;
        for (RenderBlock rb : renderBlocks) {
            float[] posCopy = rb.getVertexFromFaces();
            float[] posBoth = Arrays.copyOf(positions, positions.length + posCopy.length);
            System.arraycopy(posCopy, 0, posBoth, positions.length, posCopy.length);
            positions = posBoth;

            float[] textCopy = rb.getTextureFromFaces(textureAtlas);
            float[] textBoth = Arrays.copyOf(texCoords, texCoords.length + textCopy.length);
            System.arraycopy(textCopy, 0, textBoth, texCoords.length, textCopy.length);
            texCoords = textBoth;

            float[] normalCopy = rb.getNormalsFromFaces();
            float[] normalBoth = Arrays.copyOf(normals, normals.length + normalCopy.length);
            System.arraycopy(normalCopy, 0, normalBoth, normals.length, normalCopy.length);
            normals = normalBoth;

            int[] indCopy = rb.getIndicesFromFaces(count);
            int[] indBoth = Arrays.copyOf(indicies, indicies.length + indCopy.length);
            System.arraycopy(indCopy, 0, indBoth, indicies.length, indCopy.length);
            indicies = indBoth;
            count += rb.getVisibleFaces().size() * 4;
        }

        final float[] finalPos = positions;
        final float[] finalTexCord = texCoords;
        final float[] finalNormals = normals;
        final int[] finalIndices = indicies;
        return new MeshLayout() {
            @Override
            public float[] getVertex() {
                return finalPos;
            }

            @Override
            public float[] getTextCoords() {
                return finalTexCord;
            }

            @Override
            public float[] getNormals() {
                return finalNormals;
            }

            @Override
            public int[] getIndices() {
                return finalIndices;
            }
        };
    }

}