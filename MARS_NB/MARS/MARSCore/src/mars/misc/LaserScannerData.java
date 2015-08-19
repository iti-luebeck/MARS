/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package mars.misc;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class LaserScannerData {
    final private float scanningResolution;
    final private float maxRange;
    final private float minRange;
    final private float scanningAngleMax;
    final private float scanningAngleMin;
    final private float[] data;
                    
    public LaserScannerData() {
        this(0f, 0f, 0f, 0f, 0f, new float[1]);
    }

    public LaserScannerData(float scanningResolution, float maxRange, float minRange, float scanningAngleMax, float scanningAngleMin, float[] data) {
        this.scanningResolution = scanningResolution;
        this.maxRange = maxRange;
        this.minRange = minRange;
        this.scanningAngleMax = scanningAngleMax;
        this.scanningAngleMin = scanningAngleMin;
        this.data = data;
    }

    public float[] getData() {
        return data;
    }

    public float getMaxRange() {
        return maxRange;
    }

    public float getMinRange() {
        return minRange;
    }

    public float getScanningAngleMax() {
        return scanningAngleMax;
    }

    public float getScanningAngleMin() {
        return scanningAngleMin;
    }

    public float getScanningResolution() {
        return scanningResolution;
    }
}
