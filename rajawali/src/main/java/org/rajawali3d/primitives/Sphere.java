/**
 * Copyright 2013 Dennis Ippel
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.rajawali3d.primitives;

import org.rajawali3d.Object3D;

import java.util.ArrayList;

/**
 * A sphere primitive. The constructor takes two boolean arguments that indicate whether certain buffers should be
 * created or not. Not creating these buffers can reduce memory footprint.
 * <p>
 * When creating solid color sphere both <code>createTextureCoordinates</code> and <code>createVertexColorBuffer</code>
 * can be set to <code>false</code>.
 * <p>
 * When creating a textured sphere <code>createTextureCoordinates</code> should be set to <code>true</code> and
 * <code>createVertexColorBuffer</code> should be set to <code>false</code>.
 * <p>
 * When creating a sphere without a texture but with different colors per texture <code>createTextureCoordinates</code>
 * should be set to <code>false</code> and <code>createVertexColorBuffer</code> should be set to <code>true</code>.
 *
 * @author dennis.ippel
 */
public class Sphere extends Object3D {

    private final float mRadius;
    private final int mSegmentsW;
    private final int mSegmentsH;
    private final boolean mCreateTextureCoords;
    private final boolean mCreateVertexColorBuffer;
    private final boolean mMirrorTextureCoords;

    private final float mPhiStart;
    private final float mPhiLength;
    private final float mThetaStart;
    private final float mThetaLength;

    /**
     * Creates a sphere primitive. Calling this constructor will create texture coordinates but no vertex color buffer.
     *
     * @param radius    The radius of the sphere
     * @param segmentsW The number of vertical segments
     * @param segmentsH The number of horizontal segments
     */
    public Sphere(float radius, int segmentsW, int segmentsH) {
        this(radius, segmentsW, segmentsH, true, false, true, 0f, (float) Math.PI * 2, 0f, (float) Math.PI);
    }

    /**
     * Creates a sphere primitive. Calling this constructor will create texture coordinates but no vertex color buffer.
     *
     * @param radius    The radius of the sphere
     * @param segmentsW The number of vertical segments
     * @param segmentsH The number of horizontal segments
     */
    public Sphere(float radius, int segmentsW, int segmentsH, float phiStart, float phiLength, float thetaStart, float thetaLength) {
        this(radius, segmentsW, segmentsH, true, false, true, phiStart, phiLength, thetaStart, thetaLength);
    }

    /**
     * Creates a sphere primitive. Calling this constructor will create texture coordinates but no vertex color buffer.
     *
     * @param radius              The radius of the sphere
     * @param segmentsW           The number of vertical segments
     * @param segmentsH           The number of horizontal segments
     * @param mirrorTextureCoords A boolean that indicates if the texture coords should be mirrored horizontally.
     */
    public Sphere(float radius, int segmentsW, int segmentsH, boolean mirrorTextureCoords) {
        this(radius, segmentsW, segmentsH, true, false, true, mirrorTextureCoords, 0f, (float) Math.PI * 2, 0f, (float) Math.PI);
    }

    /**
     * Creates a sphere primitive.
     *
     * @param radius                   The radius of the sphere
     * @param segmentsW                The number of vertical segments
     * @param segmentsH                The number of horizontal segments
     * @param createTextureCoordinates A boolean that indicates if the texture coordinates should be calculated or not.
     * @param createVertexColorBuffer  A boolean that indicates if a vertex color buffer should be created or not.
     * @param createVBOs               A boolean that indicates if the VBOs should be created immediately.
     */
    public Sphere(float radius, int segmentsW, int segmentsH, boolean createTextureCoordinates,
                  boolean createVertexColorBuffer, boolean createVBOs, float phiStart, float phiLength, float thetaStart, float thetaLength) {
        this(radius, segmentsW, segmentsH, createTextureCoordinates, createVertexColorBuffer, createVBOs, false, phiStart, phiLength, thetaStart, thetaLength);
    }

    /**
     * Creates a sphere primitive.
     *
     * @param radius                   The radius of the sphere
     * @param segmentsW                The number of vertical segments
     * @param segmentsH                The number of horizontal segments
     * @param createTextureCoordinates A boolean that indicates if the texture coordinates should be calculated or not.
     * @param createVertexColorBuffer  A boolean that indicates if a vertex color buffer should be created or not.
     * @param createVBOs               A boolean that indicates if the VBOs should be created immediately.
     * @param mirrorTextureCoords      A boolean that indicates if the texture coords should be mirrored horizontally.
     */
    public Sphere(float radius, int segmentsW, int segmentsH, boolean createTextureCoordinates,
                  boolean createVertexColorBuffer, boolean createVBOs, boolean mirrorTextureCoords,
                  float phiStart, float phiLength, float thetaStart, float thetaLength) {
        super();
        mRadius = radius;
        mSegmentsW = segmentsW;
        mSegmentsH = segmentsH;
        mCreateTextureCoords = createTextureCoordinates;
        mCreateVertexColorBuffer = createVertexColorBuffer;
        mMirrorTextureCoords = mirrorTextureCoords;
        mPhiStart = phiStart;
        mPhiLength = phiLength;
        mThetaStart = thetaStart;
        mThetaLength = thetaLength;
        init(createVBOs);
    }

    protected void init(boolean createVBOs) {
        int numVertices = (mSegmentsW + 1) * (mSegmentsH + 1);

        float[] vertices = new float[numVertices * 3];
        float[] normals = new float[numVertices * 3];
        ArrayList<Integer> indices = new ArrayList<>();

        final float normLen = 1.0f / mRadius;
        float thetaEnd = mThetaStart + mThetaLength;
        int[][] grid = new int[mSegmentsH + 1][mSegmentsW + 1];

        int index = 0;
        int gridIndex = 0;
        for (int j = 0; j <= mSegmentsH; j++) {
            float v = (float) j / (float) mSegmentsH;
            for (int i = 0; i <= mSegmentsW; i++) {
                float u = (float) i / (float) mSegmentsW;

                float x = (float) -(mRadius * Math.cos(mPhiStart + u * mPhiLength) * Math.sin(mThetaStart + v * mThetaLength));
                float y = (float) (mRadius * Math.cos(mThetaStart + v * mThetaLength));
                float z = (float) (mRadius * Math.sin(mPhiStart + u * mPhiLength) * Math.sin(mThetaStart + v * mThetaLength));

                vertices[index] = x;
                normals[index++] = x * normLen;
                vertices[index] = y;
                normals[index++] = y * normLen;
                vertices[index] = z;
                normals[index++] = z * normLen;

                grid[j][i] = gridIndex++;
            }
        }

        for (int j = 0; j < mSegmentsH; j++) {
            for (int i = 0; i < mSegmentsW; i++) {
                int a = grid[j][i + 1];
                int b = grid[j][i];
                int c = grid[j + 1][i];
                int d = grid[j + 1][i + 1];

                if (j != 0 || mThetaStart > 0) {
                    indices.add(a);
                    indices.add(b);
                    indices.add(d);
                }
                if (j != mSegmentsH - 1 || thetaEnd < Math.PI) {
                    indices.add(b);
                    indices.add(c);
                    indices.add(d);
                }
            }
        }

        float[] textureCoords = null;
        if (mCreateTextureCoords)

        {
            int numUvs = (mSegmentsH + 1) * (mSegmentsW + 1) * 2;
            textureCoords = new float[numUvs];

            numUvs = 0;
            for (int j = 0; j <= mSegmentsH; ++j) {
                for (int i = mSegmentsW; i >= 0; --i) {
                    float u = (float) i / mSegmentsW;
                    textureCoords[numUvs++] = mMirrorTextureCoords ? 1.0f - u : u;
                    textureCoords[numUvs++] = (float) j / mSegmentsH;
                }
            }
        }

        index = 0;
        int[] indicess = new int[indices.size()];
        for (int i = indices.size() - 1; i >= 0; i--) {
            indicess[index++] = indices.get(i);
        }
        setData(vertices, normals, textureCoords, null, indicess, createVBOs);
    }
}
