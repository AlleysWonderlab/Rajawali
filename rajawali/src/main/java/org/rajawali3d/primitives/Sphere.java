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
        int numIndices = 2 * mSegmentsW * (mSegmentsH - 1) * 3;

//        float[] vertices = new float[numVertices * 3];
        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Float> vertices2 = new ArrayList<>();
//        float[] normals = new float[numVertices * 3];
        ArrayList<Float> normals = new ArrayList<>();
        ArrayList<Float> normals2 = new ArrayList<>();
//        int[] indices = new int[numIndices];
        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<Integer> indices2 = new ArrayList<>();

//        int vertIndex = 0, index = 0;
        final float normLen = 1.0f / mRadius;

        for (int j = 0; j <= mSegmentsH; ++j) {
            float horAngle = (float) (Math.PI * j / mSegmentsH);
            float z = mRadius * (float) Math.cos(horAngle);
            float ringRadius = mRadius * (float) Math.sin(horAngle);
            for (int i = 0; i <= mSegmentsW; ++i) {
                float verAngle = (float) (2.0f * Math.PI * i / mSegmentsW);
                float x = ringRadius * (float) Math.cos(verAngle);
                float y = ringRadius * (float) Math.sin(verAngle);

//                System.out.println("b j - i :::: " + j + " - " + i);
//                System.out.println("b x :::: " + x);
//                System.out.println("b y :::: " + y);
//                System.out.println("b z :::: " + z);

//                normals[vertIndex] = x * normLen;
//                vertices[vertIndex++] = x;
//                normals[vertIndex] = z * normLen;
//                vertices[vertIndex++] = z;
//                normals[vertIndex] = y * normLen;
//                vertices[vertIndex++] = y;

//                System.out.println("Math.toDegrees(horAngle) :::: " + (int) Math.toDegrees(horAngle));
//                if ((int) Math.toDegrees(horAngle) == 90) {
//                    if (Math.toDegrees(verAngle) >= 90 && Math.toDegrees(verAngle) <= 270) {
                normals.add(x * normLen);
                normals.add(z * normLen);
                normals.add(y * normLen);

                vertices.add(x);
                vertices.add(z);
                vertices.add(y);
//                }


//                if ((int) Math.toDegrees(horAngle) >= 0 && (int) Math.toDegrees(horAngle) <= 90)
//                    if (Math.toDegrees(verAngle) > 90 && Math.toDegrees(verAngle) <= 270)
                if (i > 0 && j > 0) {
                    int a = (mSegmentsW + 1) * j + i;
                    int b = (mSegmentsW + 1) * j + i - 1;
                    int c = (mSegmentsW + 1) * (j - 1) + i - 1;
                    int d = (mSegmentsW + 1) * (j - 1) + i;

                    if (j == mSegmentsH) {
                        indices.add(a);
                        indices.add(c);
                        indices.add(d);
                    } else if (j == 1) {
                        indices.add(a);
                        indices.add(b);
                        indices.add(c);
                    } else {
                        indices.add(a);
                        indices.add(b);
                        indices.add(c);
                        indices.add(a);
                        indices.add(c);
                        indices.add(d);
                    }
                }


            }
        }

//        float phiStart = (float) (230 / 180 * Math.PI);
//        float phiLength = (float) ((float) 80 / 180 * Math.PI);
//        float thetaStart = (float) ((float) 67.5 / 180 * Math.PI);
//        float thetaLength = (float) ((float) 45 / 180 * Math.PI);
        float thetaEnd = mThetaStart + mThetaLength;
        int[][] grid = new int[mSegmentsH + 1][mSegmentsW + 1];

//        for (j = 0; j <= mSegmentsH; j++) {
//            float v = (float) j / (float) mSegmentsH;
//            for (i = 0; i <= mSegmentsW; i++) {
//                float u = (float) i / (float) mSegmentsW;
//
//                float x = (float) -(mRadius * Math.cos(phiStart + u * phiLength) * Math.sin(thetaStart + v * thetaLength));
//                float y = (float) (mRadius * Math.cos(thetaStart + v * thetaLength));
//                float z = (float) (mRadius * Math.sin(phiStart + u * phiLength) * Math.sin(thetaStart + v * thetaLength));
//
//                System.out.println("j - i :::: " + j + " - " + i);
//                System.out.println("x :::: " + x);
//                System.out.println("y :::: " + y);
//                System.out.println("z :::: " + z);
//
//                vertices2.add(x);
//                vertices2.add(y);
//                vertices2.add(z);
//
//
//                normals2.add(x * normLen);
//                normals2.add(y * normLen);
//                normals2.add(z * normLen);
//            }
//        }

        int gridIndex = 0;
        for (int j = 0; j <= mSegmentsH; j++) {
            float v = (float) j / (float) mSegmentsH;
            for (int i = 0; i <= mSegmentsW; i++) {
                float u = (float) i / (float) mSegmentsW;

                float x = (float) -(mRadius * Math.cos(mPhiStart + u * mPhiLength) * Math.sin(mThetaStart + v * mThetaLength));
                float y = (float) (mRadius * Math.cos(mThetaStart + v * mThetaLength));
                float z = (float) (mRadius * Math.sin(mPhiStart + u * mPhiLength) * Math.sin(mThetaStart + v * mThetaLength));

//                System.out.println("j - i :::: " + j + " - " + i);
//                System.out.println("x :::: " + x);
//                System.out.println("y :::: " + y);
//                System.out.println("z :::: " + z);

                vertices2.add(x);
                vertices2.add(y);
                vertices2.add(z);


                normals2.add(x * normLen);
                normals2.add(y * normLen);
                normals2.add(z * normLen);

                grid[j][i] = gridIndex++;
            }
        }

        for (int j = 0; j < mSegmentsH; j++) {
            System.out.println("indices " + j + ":::: " + indices2.size());
            for (int i = 0; i < mSegmentsW; i++) {
                int a = grid[j][i + 1];
                int b = grid[j][i];
                int c = grid[j + 1][i];
                int d = grid[j + 1][i + 1];


                if (j != 0 || mThetaStart > 0) {
                    indices2.add(a);
                    indices2.add(b);
                    indices2.add(d);
                }
                if (j != mSegmentsH - 1 || thetaEnd < Math.PI) {
                    indices2.add(b);
                    indices2.add(c);
                    indices2.add(d);
                }

//                if (j != 0 || thetaStart > 0) indices.push(a, b, d);
//                if (j != mSegmentsH - 1 || thetaEnd < Math.PI) indices.push(b, c, d);
            }
        }
//
//            var verticesRow = [];
//
//
//            for ( ix = 0; ix <= widthSegments; ix ++ ) {
//
//                var u = ix / widthSegments;
//
//                // vertex
//
//                vertex.x = - radius * Math.cos( phiStart + u * phiLength ) * Math.sin( thetaStart + v * thetaLength );
//                vertex.y = radius * Math.cos( thetaStart + v * thetaLength );
//                vertex.z = radius * Math.sin( phiStart + u * phiLength ) * Math.sin( thetaStart + v * thetaLength );
//
//                vertices.push( vertex.x, vertex.y, vertex.z );
//
//                // normal
//
//                normal.set( vertex.x, vertex.y, vertex.z ).normalize();
//                normals.push( normal.x, normal.y, normal.z );
//
//                // uv
//
//                uvs.push( u, 1 - v );
//
//                verticesRow.push( index ++ );
//
//            }
//
//            grid.push( verticesRow );
//
//        }
//
//        for (j = 0; j <= mSegmentsH; ++j) {
//            for (i = 0; i <= mSegmentsW; ++i) {
//                if (i > 0 && j > 0) {
//                    int a = (mSegmentsW + 1) * j + i;
//                    int b = (mSegmentsW + 1) * j + i - 1;
//                    int c = (mSegmentsW + 1) * (j - 1) + i - 1;
//                    int d = (mSegmentsW + 1) * (j - 1) + i;
//
//                    if (j == mSegmentsH) {
//                        indices.add(a);
//                        indices.add(c);
//                        indices.add(d);
//                    } else if (j == 1) {
//                        indices.add(a);
//                        indices.add(b);
//                        indices.add(c);
//                    } else {
//                        indices.add(a);
//                        indices.add(b);
//                        indices.add(c);
//                        indices.add(a);
//                        indices.add(c);
//                        indices.add(d);
//                    }
//                }
//            }
//        }


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
//        System.out.println("index :::: " + index);
        System.out.println("indices :::: " + indices.size());
        System.out.println("indices2 :::: " + indices2.size());
        System.out.println("vertices :::: " + vertices.size());
        System.out.println("vertices2 :::: " + vertices2.size());
//
//        for (
//                Integer indice : indices)
//
//        {
//            System.out.println("indice ::::: " + indice);
//        }
//        System.out.println("-------------");
//        for (
//                Float vertice : vertices) {
//            System.out.println("vertice ::::: " + vertice);
//        }

        float[] colors = null;

        if (mCreateVertexColorBuffer) {
            int numColors = numVertices * 4;
            colors = new float[numColors];
            for (int j = 0; j < numColors; j += 4) {
                colors[j] = 1.0f;
                colors[j + 1] = 0;
                colors[j + 2] = 0;
                colors[j + 3] = 1.0f;
            }
        }

        float[] verticess = new float[vertices2.size()];
        int index = 0;
        for (
                Float vertice : vertices2)

        {
            verticess[index++] = vertice;
        }

        index = 0;
        float[] normalss = new float[normals.size()];
        for (
                Float normal : normals)

        {
            normalss[index++] = normal;
        }

        index = 0;
        int[] indicess = new int[indices2.size()];
        for (int i = indices2.size() - 1; i >= 0; i--) {
            indicess[index++] = indices2.get(i);
        }
//        for (
//                int indice : indices2)
//
//        {
//            indicess[index++] = indice;
//        }

        setData(verticess, normalss, textureCoords, colors, indicess, createVBOs);
    }
}
