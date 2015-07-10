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
package mars.VegetationSystem;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author oven-_000
 */
public class DensityMap {
    private int width;
    private int height;
    private int[][] densityR;
    private int[][] densityG;
    private int[][] densityB;
    
    /**
     * Creates a density map by a 2D image with the rgb values of its pixels
     * @param path the path of the image 
     */
    public DensityMap(String path){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        width = image.getWidth();
        height = image.getHeight();
        densityR = new int[width][height];
        densityG = new int[width][height];
        densityB = new int[width][height];
        Color color;
        
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                color = new Color(image.getRGB(i, j));
                densityR[i][j] = color.getRed();
                densityG[i][j] = color.getGreen();
                densityB[i][j] = color.getBlue();
            }
        }
    }
    
    /**
     * Map of the red pixel values
     * @return two dimensional array of red pixel values
     */
    public int[][] getDensitysRed(){
        return densityR;
    }
    
    /**
     *Map of the green pixel values
     * @return two dimensional array of green pixel values
     */
    public int[][] getDensitysGreen(){
        return densityG;
    }
           
    /**
     * Map of the blue pixel values
     * @return two dimensional array of blue pixel values
     */
    public int[][] getDensitysBlue(){
        return densityB;
    }
    
    /**
     * The width of the image
     * @return the number of pixels in the images with
     */
    public int getWidth(){
        return width;
    }
    
    /**
     * The hight of the image
     * @return the number of pixels in th images height
     */
    public int getHeight(){
        return height;
    }
}
