/*
 * Copyright (c) 2003-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package mars.waves;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.system.Timer;
import com.jme3.util.BufferUtils;
import javax.vecmath.TexCoord2f;

/**
 * <code>ProjectedGrid</code>
 * Projected grid mesh
 *
 * @author Rikard Herlitz (MrCoder)
 * @author Matthias Schellhase portage to jme3
 */
public class MyProjectedGrid extends Mesh {

    private static final long serialVersionUID = 1L;
    private int sizeX;
    private int sizeY;

    private static Vector3f calcVec1 = new Vector3f();
    private static Vector3f calcVec2 = new Vector3f();
    private static Vector3f calcVec3 = new Vector3f();
    private FloatBuffer vertBuf;
    private FloatBuffer normBuf;
    private FloatBuffer texs;
    private IntBuffer indexBuffer;
    private float viewPortWidth = 0;
    private float viewPortHeight = 0;
    private Vector2f source = new Vector2f();
    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f modelViewProjectionInverse = new Matrix4f();
    private Vector4f intersectBottomLeft = new Vector4f();
    private Vector4f intersectTopLeft = new Vector4f();
    private Vector4f intersectTopRight = new Vector4f();
    private Vector4f intersectBottomRight = new Vector4f();
    private Matrix4f modelViewMatrix1 = new Matrix4f();
    private Matrix4f projectionMatrix1 = new Matrix4f();
    private Matrix4f modelViewProjection1 = new Matrix4f();
    private Matrix4f modelViewProjectionInverse1 = new Matrix4f();
    private Vector4f intersectBottomLeft1 = new Vector4f();
    private Vector4f intersectTopLeft1 = new Vector4f();
    private Vector4f intersectTopRight1 = new Vector4f();
    private Vector4f intersectBottomRight1 = new Vector4f();
    private Vector3f camloc = new Vector3f();
    private Vector3f camdir = new Vector3f();
    private Vector4f pointFinal = new Vector4f();
    private Vector4f pointTop = new Vector4f();
    private Vector4f pointBottom = new Vector4f();
    private Vector3f realPoint = new Vector3f();
    /**
     * 
     */
    public boolean freezeProjector = false;
    /**
     * 
     */
    public boolean useReal = false;
    private Vector3f projectorLoc = new Vector3f();
    private Timer timer;
    private Camera cam;
    private float height;
    private float fovY = 45.0f;
    private HeightGenerator heightGenerator;
    private float textureScale;
    private float[] vertBufArray;
    private float[] normBufArray;
    private float[] texBufArray;

    /**
     * 
     * @param timer
     * @param cam
     * @param sizeX
     * @param sizeY
     * @param texureScale
     * @param heightGenerator
     */
    public MyProjectedGrid(Timer timer, Camera cam, int sizeX, int sizeY, float texureScale, HeightGenerator heightGenerator) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.textureScale = texureScale;
        this.heightGenerator = heightGenerator;
        this.cam = cam;

        if (cam.getFrustumNear() > 0.0f) {
            fovY = FastMath.atan(cam.getFrustumTop() / cam.getFrustumNear())
                    * 2.0f / FastMath.DEG_TO_RAD;
        }

        this.timer = timer;

        vertBufArray = new float[sizeX * sizeY * 3];
        normBufArray = new float[sizeX * sizeY * 3];
        texBufArray = new float[sizeX * sizeY * 2];

        buildVertices();
        buildTextureCoordinates();
        buildNormals();
    }

    /**
     * 
     */
    public void switchFreeze() {
        freezeProjector = !freezeProjector;
    }

    /**
     * 
     * @return
     */
    public float getTextureScale() {
        return textureScale;
    }

    /**
     * 
     * @param textureScale
     */
    public void setTextureScale(float textureScale) {
        this.textureScale = textureScale;
    }

    /**
     * 
     * @param modelViewMatrix
     */
    public void update(Matrix4f modelViewMatrix) {
        if (freezeProjector) {
            return;
        }

        float time = timer.getTimeInSeconds();

        camloc.set(cam.getLocation());
        camdir.set(cam.getDirection());

        height = cam.getLocation().getY();

        Camera camera = cam;
        ProjectedTextureUtil.camera = camera.clone();
        viewPortWidth = camera.getWidth();
        viewPortHeight = camera.getHeight();

        modelViewMatrix.set(camera.getViewMatrix().clone());
        modelViewMatrix.transposeLocal();

        projectionMatrix.set(camera.getProjectionMatrix().clone());
        projectionMatrix.transposeLocal();

        modelViewProjectionInverse.set(modelViewMatrix).multLocal(projectionMatrix);
        modelViewProjectionInverse.invertLocal();

        source.set(0.5f, 0.5f);
        getWorldIntersection(height, source, modelViewProjectionInverse, pointFinal);

        pointFinal.multLocal(1.0f / pointFinal.getW());
        realPoint.set(pointFinal.getX(), pointFinal.getY(), pointFinal.getZ());
        projectorLoc.set(cam.getLocation());
        realPoint.set(projectorLoc).addLocal(cam.getDirection());

        Matrix4f rangeMatrix = null;
        if (useReal) {
            Vector3f fakeLoc = new Vector3f(projectorLoc);
            Vector3f fakePoint = new Vector3f(realPoint);
            fakeLoc.addLocal(0, 1000, 0);

            rangeMatrix = getMinMax(fakeLoc, fakePoint, cam);
        }

        ProjectedTextureUtil.matrixLookAt(projectorLoc, realPoint, Vector3f.UNIT_Y, modelViewMatrix);
        modelViewMatrix.transposeLocal();

        ProjectedTextureUtil.matrixProjection(fovY + 10.0f, viewPortWidth / viewPortHeight, cam.getFrustumNear(), cam.getFrustumFar(), projectionMatrix);
        projectionMatrix.transposeLocal();

        modelViewProjectionInverse.set(modelViewMatrix).multLocal(projectionMatrix);
        modelViewProjectionInverse.invertLocal();

        if (useReal && rangeMatrix != null) {
            rangeMatrix.multLocal(modelViewProjectionInverse);
            modelViewProjectionInverse.set(rangeMatrix);
        }

        source.set(0, 0);
        getWorldIntersection(height, source, modelViewProjectionInverse, intersectBottomLeft);

        source.set(0, 1);
        getWorldIntersection(height, source, modelViewProjectionInverse, intersectTopLeft);
        source.set(1, 1);
        getWorldIntersection(height, source, modelViewProjectionInverse, intersectTopRight);
        source.set(1, 0);
        getWorldIntersection(height, source, modelViewProjectionInverse, intersectBottomRight);
        

        vertBuf.rewind();
        float du = 1.0f / (float) (sizeX - 1);
        float dv = 1.0f / (float) (sizeY - 1);
        float u = 0, v = 0;
        int index = 0;
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                interpolate(intersectTopLeft, intersectTopRight, u, pointTop);
                interpolate(intersectBottomLeft, intersectBottomRight, u, pointBottom);
                interpolate(pointTop, pointBottom, v, pointFinal);
                pointFinal.x /= pointFinal.w;
                pointFinal.z /= pointFinal.w;
                realPoint.set(pointFinal.getX(),
                        heightGenerator.getHeight(pointFinal.getX(), pointFinal.getZ(), time),
                        pointFinal.getZ());

                vertBufArray[index++] = realPoint.getX();
                vertBufArray[index++] = realPoint.getY();
                vertBufArray[index++] = realPoint.getZ();

                u += du;
            }
            v += dv;
            u = 0;
        }
        vertBuf.put(vertBufArray);
        getBuffer(Type.Position).updateData(vertBuf);
        updateBound();

        // Texture stuff
        texs.rewind();
        for (int i = 0; i < getVertexCount(); i++) {
            texBufArray[i * 2] = vertBufArray[i * 3] * textureScale;
            texBufArray[i * 2 + 1] = vertBufArray[i * 3 + 2] * textureScale;
        }
        texs.put(texBufArray);
        getBuffer(Type.TexCoord).updateData(texs);


        normBuf.rewind();
        oppositePoint.set(0, 0, 0);
        adjacentPoint.set(0, 0, 0);
        rootPoint.set(0, 0, 0);
        tempNorm.set(0, 0, 0);
        int adj = 0, opp = 0, normalIndex = 0;
        for (int row = 0; row < sizeY; row++) {
            for (int col = 0; col < sizeX; col++) {
                if (row == sizeY - 1) {
                    if (col == sizeX - 1) { // last row, last col
                        // up cross left
                        adj = normalIndex - sizeX;
                        opp = normalIndex - 1;
                    } else { // last row, except for last col
                        // right cross up
                        adj = normalIndex + 1;
                        opp = normalIndex - sizeX;
                    }
                } else {
                    if (col == sizeX - 1) { // last column except for last row
                        // left cross down
                        adj = normalIndex - 1;
                        opp = normalIndex + sizeX;
                    } else { // most cases
                        // down cross right
                        adj = normalIndex + sizeX;
                        opp = normalIndex + 1;
                    }
                }
                rootPoint.set(vertBufArray[normalIndex * 3], vertBufArray[normalIndex * 3 + 1], vertBufArray[normalIndex * 3 + 2]);
                adjacentPoint.set(vertBufArray[adj * 3], vertBufArray[adj * 3 + 1], vertBufArray[adj * 3 + 2]);
                oppositePoint.set(vertBufArray[opp * 3], vertBufArray[opp * 3 + 1], vertBufArray[opp * 3 + 2]);
                tempNorm.set(adjacentPoint).subtractLocal(rootPoint).crossLocal(oppositePoint.subtractLocal(rootPoint)).normalizeLocal();

                normBufArray[normalIndex * 3] = tempNorm.x;
                normBufArray[normalIndex * 3 + 1] = tempNorm.y;
                normBufArray[normalIndex * 3 + 2] = tempNorm.z;

                normalIndex++;
            }
        }
        normBuf.put(normBufArray);
        getBuffer(Type.Normal).updateData(normBuf);

    }

    private Matrix4f getMinMax(Vector3f fakeLoc, Vector3f fakePoint, Camera cam) {
        Matrix4f rangeMatrix;
        ProjectedTextureUtil.matrixLookAt(fakeLoc, fakePoint, Vector3f.UNIT_Y, modelViewMatrix1);
        ProjectedTextureUtil.matrixProjection(fovY, viewPortWidth / viewPortHeight, cam.getFrustumNear(), cam.getFrustumFar(), projectionMatrix1);
        modelViewProjection1.set(modelViewMatrix1).multLocal(projectionMatrix1);
        modelViewProjectionInverse1.set(modelViewProjection1).invertLocal();

        source.set(0, 0);
        getWorldIntersection(height, source, modelViewProjectionInverse, intersectBottomLeft1);
        source.set(0, 1);
        getWorldIntersection(height, source, modelViewProjectionInverse, intersectTopLeft1);
        source.set(1, 1);
        getWorldIntersection(height, source, modelViewProjectionInverse, intersectTopRight1);
        source.set(1, 0);
        getWorldIntersection(height, source, modelViewProjectionInverse, intersectBottomRight1);

        Vector3f tmp = new Vector3f();
        tmp.set(intersectBottomLeft.getX(), intersectBottomLeft.getY(), intersectBottomLeft.getZ());
        modelViewProjection1.mult(tmp, tmp);
        intersectBottomLeft.set(tmp.x, tmp.y, tmp.z, intersectBottomLeft.getW());

        tmp.set(intersectTopLeft1.getX(), intersectTopLeft1.getY(), intersectTopLeft1.getZ());
        modelViewProjection1.mult(tmp, tmp);
        intersectTopLeft1.set(tmp.x, tmp.y, tmp.z, intersectTopLeft1.getW());

        tmp.set(intersectTopRight1.getX(), intersectTopRight1.getY(), intersectTopRight1.getZ());
        modelViewProjection1.mult(tmp, tmp);
        intersectTopRight1.set(tmp.x, tmp.y, tmp.z, intersectTopRight1.getW());

        tmp.set(intersectBottomRight1.getX(), intersectBottomRight1.getY(), intersectBottomRight1.getZ());
        modelViewProjection1.mult(tmp, tmp);
        intersectBottomRight1.set(tmp.x, tmp.y, tmp.z, intersectBottomRight1.getW());

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
        if (intersectBottomLeft1.getX() < minX) {
            minX = intersectBottomLeft1.getX();
        }
        if (intersectTopLeft1.getX() < minX) {
            minX = intersectTopLeft1.getX();
        }
        if (intersectTopRight1.getX() < minX) {
            minX = intersectTopRight1.getX();
        }
        if (intersectBottomRight1.getX() < minX) {
            minX = intersectBottomRight1.getX();
        }
        if (intersectBottomLeft1.getX() > maxX) {
            maxX = intersectBottomLeft1.getX();
        }
        if (intersectTopLeft1.getX() > maxX) {
            maxX = intersectTopLeft1.getX();
        }
        if (intersectTopRight1.getX() > maxX) {
            maxX = intersectTopRight1.getX();
        }
        if (intersectBottomRight1.getX() > maxX) {
            maxX = intersectBottomRight1.getX();
        }

        if (intersectBottomLeft1.getY() < minY) {
            minY = intersectBottomLeft1.getY();
        }
        if (intersectTopLeft1.getY() < minY) {
            minY = intersectTopLeft1.getY();
        }
        if (intersectTopRight1.getY() < minY) {
            minY = intersectTopRight1.getY();
        }
        if (intersectBottomRight1.getY() < minY) {
            minY = intersectBottomRight1.getY();
        }
        if (intersectBottomLeft1.getY() > maxY) {
            maxY = intersectBottomLeft1.getY();
        }
        if (intersectTopLeft1.getY() > maxY) {
            maxY = intersectTopLeft1.getY();
        }
        if (intersectTopRight1.getY() > maxY) {
            maxY = intersectTopRight1.getY();
        }
        if (intersectBottomRight1.getY() > maxY) {
            maxY = intersectBottomRight1.getY();
        }
        rangeMatrix = new Matrix4f(
                maxX - minX, 0, 0, minX,
                0, maxY - minY, 0, minY,
                0, 0, 1, 0,
                0, 0, 0, 1);
        rangeMatrix.transpose();
        return rangeMatrix;
    }

    private void interpolate(Vector4f beginVec, Vector4f finalVec, float changeAmnt, Vector4f resultVec) {
        resultVec.x = (1 - changeAmnt) * beginVec.x + changeAmnt * finalVec.x;
        resultVec.y = (1 - changeAmnt) * beginVec.y + changeAmnt * finalVec.y;
        resultVec.z = (1 - changeAmnt) * beginVec.z + changeAmnt * finalVec.z;
        resultVec.w = (1 - changeAmnt) * beginVec.w + changeAmnt * finalVec.w;
    }

    /**
     * 
     * @param camheight
     * @param screenPosition
     * @param viewProjectionMatrix
     * @param store
     * @return
     */
    public static Vector4f getWorldIntersection(float camheight, Vector2f screenPosition, Matrix4f viewProjectionMatrix, Vector4f store) {
        Vector4f origin = new Vector4f();
        Vector4f direction = new Vector4f();

        origin.set(screenPosition.getX() * 2 - 1, screenPosition.getY() * 2 - 1, -1, 1);
        direction.set(screenPosition.getX() * 2 - 1, screenPosition.getY() * 2 - 1, 1, 1);

        origin = viewProjectionMatrix.transpose().mult(origin);
        direction = viewProjectionMatrix.transpose().mult(direction);

        if (camheight > 0) {
            if (direction.getY() > 0) {
                direction.y = 0;
            }
        } else {
            if (direction.getY() < 0) {
                direction.y = 0;
            }
        }

        direction.subtractLocal(origin);

        float t = -origin.getY() / direction.getY();

        direction.multLocal(t);
        store.set(origin);
        store.addLocal(direction);
        return store;
    }


    /**
     *
     * @return
     */
    @Override
    public int getVertexCount() {
        return sizeX * sizeY;
    }


    /**
     * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
     * on the terrain. The normal is linearly interpreted from the normals of
     * the 4 nearest defined points. If the point provided is not within the
     * bounds of the height map, null is returned.
     *
     * @param position the vector representing the location to find a normal at.
     * @param store	the Vector3f object to store the result in. If null, a new one
     *                 is created.
     * @return the normal vector at the provided location.
     */
    public Vector3f getSurfaceNormal(Vector2f position, Vector3f store) {
        return getSurfaceNormal(position.getX(), position.getY(), store);
    }

    /**
     * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
     * on the terrain. The normal is linearly interpreted from the normals of
     * the 4 nearest defined points. If the point provided is not within the
     * bounds of the height map, null is returned.
     *
     * @param position the vector representing the location to find a normal at. Only
     *                 the x and z values are used.
     * @param store	the Vector3f object to store the result in. If null, a new one
     *                 is created.
     * @return the normal vector at the provided location.
     */
    public Vector3f getSurfaceNormal(Vector3f position, Vector3f store) {
        return getSurfaceNormal(position.getX(), position.getZ(), store);
    }

    /**
     * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
     * on the terrain. The normal is linearly interpreted from the normals of
     * the 4 nearest defined points. If the point provided is not within the
     * bounds of the height map, null is returned.
     *
     * @param x	 the x coordinate to check.
     * @param z	 the z coordinate to check.
     * @param store the Vector3f object to store the result in. If null, a new one
     *              is created.
     * @return the normal unit vector at the provided location.
     */
    public Vector3f getSurfaceNormal(float x, float z, Vector3f store) {
        float col = FastMath.floor(x);
        float row = FastMath.floor(z);

        if (col < 0 || row < 0 || col >= sizeX - 1 || row >= sizeY - 1) {
            return null;
        }
        float intOnX = x - col, intOnZ = z - row;

        if (store == null) {
            store = new Vector3f();
        }

        Vector3f topLeft = store, topRight = calcVec1, bottomLeft = calcVec2, bottomRight = calcVec3;

        int focalSpot = (int) (col + row * sizeX);

        // find the heightmap point closest to this position (but will always
        // be to the left ( < x) and above (< z) of the spot.
        BufferUtils.populateFromBuffer(topLeft, normBuf, focalSpot);

        // now find the next point to the right of topLeft's position...
        BufferUtils.populateFromBuffer(topRight, normBuf, focalSpot + 1);

        // now find the next point below topLeft's position...
        BufferUtils.populateFromBuffer(bottomLeft, normBuf, focalSpot + sizeX);

        // now find the next point below and to the right of topLeft's
        // position...
        BufferUtils.populateFromBuffer(bottomRight, normBuf, focalSpot + sizeX + 1);

        // Use linear interpolation to find the height.
        topLeft.interpolateLocal(topRight, intOnX);
        bottomLeft.interpolateLocal(bottomRight, intOnX);
        topLeft.interpolateLocal(bottomLeft, intOnZ);
        return topLeft.normalizeLocal();
    }

    /**
     * <code>buildVertices</code> sets up the vertex and index arrays of the
     * TriMesh.
     */
    private void buildVertices() {
        vertBuf = BufferUtils.createVector3Buffer(vertBuf, getVertexCount());

        setBuffer(Type.Position, 3, vertBuf);

        Vector3f point = new Vector3f();
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                point.set(x, 0, y);
                BufferUtils.setInBuffer(point, vertBuf, (x + (y * sizeX)));
            }
        }

        //set up the indices
        int triangleQuantity = ((sizeX - 1) * (sizeY - 1)) * 2;
        //setQuantity( triangleQuantity );
        indexBuffer = BufferUtils.createIntBuffer(triangleQuantity * 3);

        setBuffer(Type.Index, 3, indexBuffer);

        //go through entire array up to the second to last column.
        for (int i = 0; i < (sizeX * (sizeY - 1)); i++) {
            //we want to skip the top row.
            if (i % ((sizeX * (i / sizeX + 1)) - 1) == 0 && i != 0) {
//				logger.info("skip row: "+i+" cause: "+((sizeY * (i / sizeX + 1)) - 1));
                continue;
            } else {
//				logger.info("i: "+i);
            }
            //set the top left corner.
            indexBuffer.put(i);
            //set the bottom right corner.
            indexBuffer.put((1 + sizeX) + i);
            //set the top right corner.
            indexBuffer.put(1 + i);
            //set the top left corner
            indexBuffer.put(i);
            //set the bottom left corner
            indexBuffer.put(sizeX + i);
            //set the bottom right corner
            indexBuffer.put((1 + sizeX) + i);
        }
    }

    /**
     * <code>buildTextureCoordinates</code> calculates the texture coordinates
     * of the terrain.
     */
    private void buildTextureCoordinates() {
        texs = BufferUtils.createVector2Buffer(getVertexCount());



        texs.clear();

        vertBuf.rewind();
        for (int i = 0; i < getVertexCount(); i++) {
            texs.put(vertBuf.get() * textureScale);
            vertBuf.get(); // ignore vert y coord.
            texs.put(vertBuf.get() * textureScale);
        }
        setBuffer(Type.TexCoord, 2, texs);
    }
    /**
     * <code>buildNormals</code> calculates the normals of each vertex that
     * makes up the block of terrain.
     */
    Vector3f oppositePoint = new Vector3f();
    Vector3f adjacentPoint = new Vector3f();
    Vector3f rootPoint = new Vector3f();
    Vector3f tempNorm = new Vector3f();

    private void buildNormals() {
        normBuf = BufferUtils.createVector3Buffer(normBuf, getVertexCount());
        setBuffer(Type.Normal, 3, normBuf);

        oppositePoint.set(0, 0, 0);
        adjacentPoint.set(0, 0, 0);
        rootPoint.set(0, 0, 0);
        tempNorm.set(0, 0, 0);
        int adj = 0, opp = 0, normalIndex = 0;
        for (int row = 0; row < sizeY; row++) {
            for (int col = 0; col < sizeX; col++) {
                BufferUtils.populateFromBuffer(rootPoint, vertBuf, normalIndex);
                if (row == sizeY - 1) {
                    if (col == sizeX - 1) { // last row, last col
                        // up cross left
                        adj = normalIndex - sizeX;
                        opp = normalIndex - 1;
                    } else { // last row, except for last col
                        // right cross up
                        adj = normalIndex + 1;
                        opp = normalIndex - sizeX;
                    }
                } else {
                    if (col == sizeY - 1) { // last column except for last row
                        // left cross down
                        adj = normalIndex - 1;
                        opp = normalIndex + sizeX;
                    } else { // most cases
                        // down cross right
                        adj = normalIndex + sizeX;
                        opp = normalIndex + 1;
                    }
                }
                BufferUtils.populateFromBuffer(adjacentPoint, vertBuf, adj);
                BufferUtils.populateFromBuffer(oppositePoint, vertBuf, opp);
                tempNorm.set(adjacentPoint).subtractLocal(rootPoint).crossLocal(oppositePoint.subtractLocal(rootPoint)).normalizeLocal();
                BufferUtils.setInBuffer(tempNorm, normBuf, normalIndex);
                normalIndex++;
            }
        }
    }
}
