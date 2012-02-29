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

import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.Renderer;
import com.jme3.texture.Texture;


/**
 * <code>ProjectedTextureUtil</code>
 *
 * @author Rikard Herlitz (MrCoder)
 *
 * @author Joshua Ellen (basixs)
 * [1-16-2009] - Abstracted, removed direct calls to openGL
 * @author Matthias Schellhase portage to jme3
 */
public class ProjectedTextureUtil {

    static Camera camera = null;
    private static Matrix4f lightProjectionMatrix = new Matrix4f();
    private static Matrix4f lightViewMatrix = new Matrix4f();
    private static Matrix4f biasMatrix = new Matrix4f( 0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.5f, 0.5f, 0.5f,
            1.0f ); // bias from [-1, 1] to [0, 1]

    /**
     * Updated texture matrix on the provided texture
     *
     * @param texture
     *            Texture to update texturematrix on
     * @param fov
     *            Projector field of view, in angles
     * @param aspect
     *            Projector frustum aspect ratio
     * @param near
     *            Projector frustum near plane
     * @param far
     *            Projector frustum far plane
     * @param pos
     *            Projector position
     * @param aim
     *            Projector look at position
     */

    /**
     * Populates a <code>Matrix4f</code> with the proper look at transformations
     * from the ModelView matrix.
     * @param location the 'Where' in result matrix
     * @param at the 'At' in the result matrix
     * @param up the world up
     * @param result the altered <code>Matrix4f</code>
     */
    public static void matrixLookAt( Vector3f location, Vector3f at,
            Vector3f up, Matrix4f result ) {



        camera.setLocation( location );
        camera.lookAt( at, up );

        result.set( camera.getViewMatrix() );
    }

    /**
     * Populates a <code>Matrix4f</code> with the proper frustum transformations
     * from the ModelView matrix.
     * @param fovY the Field of View
     * @param aspect the aspect ratio
     * @param near the near plane of the frustum
     * @param far the far frame of the frustum
     * @param result the altered <code>Matrix4f</code>
     */
    public static void matrixPerspective( float fovY, float aspect,
            float near, float far, Matrix4f result ) {



        camera.setFrustumPerspective( fovY, aspect, near, far );

        result.set( camera.getViewMatrix() );
    }

    /**
     * Populates a <code>Matrix4f</code> with the proper frustum transformations
     * from the Projection matrix.
     * @param fovY the Field of View
     * @param aspect the aspect ratio
     * @param near the near plane of the frustum
     * @param far the far frame of the frustum
     * @param result the altered <code>Matrix4f</code>
     */
    public static void matrixProjection( float fovY, float aspect, float near,
            float far, Matrix4f result ) {



        camera.setFrustumPerspective( fovY, aspect, near, far );

        result.set( camera.getProjectionMatrix() );
    }

    /**
     * Populates a <code>Matrix4f</code> with the proper frustum transformations
     * from the Projection matrix.
     * @param frustumLeft the left plane of the frustum
     * @param frustumRight the right plane of the frustum
     * @param frustumBottom the bottom plane of the frustum
     * @param frustumTop the top plane of the frustum
     * @param frustumNear the near plane of the frustum
     * @param frustumFar the far plane of the frustum
     * @param result the altered <code>Matrix4f</code>
     */
    public static void matrixFrustum( float frustumLeft, float frustumRight,
            float frustumBottom, float frustumTop, float frustumNear,
            float frustumFar, Matrix4f result ) {



        camera.setFrustum( frustumFar, frustumFar, frustumLeft, frustumRight,
                frustumTop, frustumFar );

        result.set( camera.getProjectionMatrix() );
    }


}

