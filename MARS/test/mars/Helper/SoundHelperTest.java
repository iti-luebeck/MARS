/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.Helper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tosik
 */
public class SoundHelperTest {
    
    public SoundHelperTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getUnderWaterSoundSpeedBilaniukWong method, of class SoundHelper.
     */
    @Test
    public void testGetUnderWaterSoundSpeedBilaniukWong() {
        System.out.println("getUnderWaterSoundSpeedBilaniukWong");
        float expResult = 0.0F;
        float result = SoundHelper.getUnderWaterSoundSpeedBilaniukWong();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnderWaterSoundSpeedBelogolskiiSekoyan method, of class SoundHelper.
     */
    @Test
    public void testGetUnderWaterSoundSpeedBelogolskiiSekoyan() {
        System.out.println("getUnderWaterSoundSpeedBelogolskiiSekoyan");
        float expResult = 0.0F;
        float result = SoundHelper.getUnderWaterSoundSpeedBelogolskiiSekoyan();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnderWaterSoundSpeedLubberGraaffA method, of class SoundHelper.
     */
    @Test
    public void testGetUnderWaterSoundSpeedLubberGraaffA() {
        System.out.println("getUnderWaterSoundSpeedLubberGraaffA");
        float temperature = 0.0F;
        float expResult = 0.0F;
        float result = SoundHelper.getUnderWaterSoundSpeedLubberGraaffA(temperature);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnderWaterSoundSpeedLubberGraaffB method, of class SoundHelper.
     */
    @Test
    public void testGetUnderWaterSoundSpeedLubberGraaffB() {
        System.out.println("getUnderWaterSoundSpeedLubberGraaffB");
        float temperature = 0.0F;
        float expResult = 0.0F;
        float result = SoundHelper.getUnderWaterSoundSpeedLubberGraaffB(temperature);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnderWaterSoundSpeedMarczak method, of class SoundHelper.
     */
    @Test
    public void testGetUnderWaterSoundSpeedMarczak() {
        System.out.println("getUnderWaterSoundSpeedMarczak");
        float temperature = 0.0F;
        float expResult = 0.0F;
        float result = SoundHelper.getUnderWaterSoundSpeedMarczak(temperature);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnderWaterSoundSpeedCoppens method, of class SoundHelper.
     */
    @Test
    public void testGetUnderWaterSoundSpeedCoppens() {
        System.out.println("getUnderWaterSoundSpeedCoppens");
        float temperature_deg = 0.0F;
        float salinity = 0.0F;
        float depth = 0.0F;
        float expResult = 0.0F;
        float result = SoundHelper.getUnderWaterSoundSpeedCoppens(temperature_deg, salinity, depth);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnderWaterSoundSpeedMackenzie method, of class SoundHelper.
     */
    @Test
    public void testGetUnderWaterSoundSpeedMackenzie() {
        System.out.println("getUnderWaterSoundSpeedMackenzie");
        float temperature = 0.0F;
        float salinity = 0.0F;
        float depth = 0.0F;
        float expResult = 0.0F;
        float result = SoundHelper.getUnderWaterSoundSpeedMackenzie(temperature, salinity, depth);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnderWaterSoundSpeedDelGrosso method, of class SoundHelper.
     */
    @Test
    public void testGetUnderWaterSoundSpeedDelGrosso() {
        System.out.println("getUnderWaterSoundSpeedDelGrosso");
        float temperature = 0.0F;
        float salinity = 0.0F;
        float pressure = 0.0F;
        float expResult = 0.0F;
        float result = SoundHelper.getUnderWaterSoundSpeedDelGrosso(temperature, salinity, pressure);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnderWaterSoundSpeedChenMillero method, of class SoundHelper.
     */
    @Test
    public void testGetUnderWaterSoundSpeedChenMillero() {
        System.out.println("getUnderWaterSoundSpeedChenMillero");
        float temperature = 0.0F;
        float salinity = 0.0F;
        float pressure = 0.0F;
        float expResult = 0.0F;
        float result = SoundHelper.getUnderWaterSoundSpeedChenMillero(temperature, salinity, pressure);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
