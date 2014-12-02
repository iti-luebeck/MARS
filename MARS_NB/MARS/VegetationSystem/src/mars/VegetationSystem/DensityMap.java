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
