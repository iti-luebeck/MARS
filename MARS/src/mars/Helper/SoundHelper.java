/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.Helper;

/**
 *
 * @author Thomas Tosik
 */
public class SoundHelper {
    
    public static float getUnderWaterSoundSpeedBilaniukWong(){
        throw new UnsupportedOperationException();
    }
    
    public static float getUnderWaterSoundSpeedBelogolskiiSekoyan(){
        throw new UnsupportedOperationException();
    }
    
    public static float getUnderWaterSoundSpeedLubberGraaffA(float temperature){
        return 1404.3f + 4.7f*temperature - 0.04f*(float)(Math.pow(temperature,2));
    }
    
    public static float getUnderWaterSoundSpeedLubberGraaffB(float temperature){
        return 1405.03f + 4.624f*temperature - 3.83E-2f*(float)(Math.pow(temperature,2));
    }
    
    public static float getUnderWaterSoundSpeedMarczak(float temperature){
        return 1.402385E3f + 5.038813f*temperature - 5.799136E-2f*(float)(Math.pow(temperature,2)) + 3.287156E-4f*(float)(Math.pow(temperature,3)) - 1.398845E-6f*(float)(Math.pow(temperature,4)) + 2.787860E-9f*(float)(Math.pow(temperature,5));
    }
    
    public static float getUnderWaterSoundSpeedCoppens(float temperature_deg,float salinity, float depth){
        float temperature = temperature_deg/10f;
        return CoppensC(temperature_deg, salinity) + (16.23f + 0.253f*temperature)*depth + (0.213f-0.1f*temperature)*(float)(Math.pow(depth,2)) + (0.016f + 0.0002f*(salinity-35f))*(salinity - 35f)*temperature*depth;
    }
    
    private static float CoppensC(float temperature_deg,float salinity){
        float temperature = temperature_deg/10f;
        return 1449.05f + 45.7f*temperature - 5.21f*(float)(Math.pow(temperature,2)) + 0.23f*(float)(Math.pow(temperature,3)) + (1.333f - 0.126f*temperature + 0.009f*(float)(Math.pow(temperature,2)))*(salinity - 35f); 
    }

    public static float getUnderWaterSoundSpeedMackenzie(float temperature,float salinity, float depth){
        return 1448.96f + 4.591f*temperature - 5.304E-2f*(float)(Math.pow(temperature,2)) + 2.374E-4f*(float)(Math.pow(temperature,3)) + 1.340f*(salinity-35f) + 1.630E-2f*depth + 1.675E-7f*(float)(Math.pow(depth,2)) - 1.025E2f*temperature*(salinity - 35f) - 7.139E-13f*temperature*(float)(Math.pow(depth,3));
    }
    
    public static float getUnderWaterSoundSpeedDelGrosso(float temperature,float salinity, float pressure){
        float C000 = 1402.392f;
        return C000 + DelGrossoCT(temperature) + DelGrossoCS(salinity) + DelGrossoCP(pressure) + DelGrossoCSTP(temperature, salinity, pressure);
    }
    
    private static float DelGrossoCT(float temperature){
        float CT1 = 0.5012285E1f;
        float CT2 = -0.551184E-1f;
        float CT3 = 0.221649E-3f;
        return CT1*temperature + CT2*(float)(Math.pow(temperature,2)) + CT3*(float)(Math.pow(temperature,3));
    }
    
    private static float DelGrossoCS(float salinity){
        float CS1 = 0.1329530E1f;
        float CS2 = 0.1288598E-3f;
        return CS1*salinity + CS2*(float)(Math.pow(salinity,2));
    }
    
    private static float DelGrossoCP(float pressure){
        float CP1 = 0.1560592f;
        float CP2 = 0.2449993E-4f;
        float CP3 = -0.8833959E-8f;
        return CP1*pressure + CP2*(float)(Math.pow(pressure,2)) + CP3*(float)(Math.pow(pressure,3));
    }
    
    private static float DelGrossoCSTP(float temperature,float salinity, float pressure){
        float CST = -0.1275936E-1f;
        float CTP = 0.6353509E-2f;
        float CT2P2 = 0.2656174E-7f;
        float CTP2 = -0.1593895E-5f;
        float CTP3 = 0.5222483E-9f;
        float CT3P = -0.4383615E-6f;
        float CS2P2 = -0.1616745E-8f;
        float CST2 = 0.9688441E-4f;
        float CS2TP = 0.4857614E-5f;
        float CSTP = -0.3406824E-3f;
        return CTP*temperature*pressure + CT3P*(float)(Math.pow(temperature,3))*pressure + CTP2*(float)(Math.pow(temperature,2))*pressure + CT2P2*(float)(Math.pow(temperature,2))*(float)(Math.pow(pressure,2)) + CTP3*(float)(Math.pow(pressure,3))*temperature + CST*salinity*temperature + CST2*(float)(Math.pow(temperature,2))*salinity + CSTP*salinity*temperature*pressure + CS2TP*(float)(Math.pow(salinity,2))*pressure*temperature + CS2P2*(float)(Math.pow(salinity,2))*(float)(Math.pow(pressure,2));
    }
    
    public static float getUnderWaterSoundSpeedChenMillero(float temperature,float salinity, float pressure){
        //define constants
        float C00 = 1402.388f;
        float C01 = 5.03830f;
        float C02 = 5.81090E-2f;
        float C03 = 3.3432E-4f;
        float C04 = -1.47797E-6f;
        float C05 = 3.1419E-9f;
        float C10 = 0.153563f;
        float C11 = 6.8999E-4f;
        float C12 = -8.1829E-6f;
        float C13 = 1.3632E-7f;
        float C14 = -6.1260E-10f;
        float C20 = 3.1260E-5f;
        float C21 = -1.7111E-6f;
        float C22 = 2.5986E-8f;
        float C23 = -2.5353E-10f;
        float C24 = 1.0415E-12f;
        float C30 = -9.7729E-9f;
        float C31 = 3.8513E-10f;
        float C32 = -2.3654E-12f;
        float A00 = 1.389f;
        float A01 = -1.262E-2f;
        float A02 = 7.166E-5f;
        float A03 = 2.008E-6f;
        float A04 = -3.21E-8f;
        float A10 = 9.4742E-5f;
        float A11 = -1.2583E-5f;
        float A12 = -6.4928E-8f;
        float A13 = 1.0515E-8f;
        float A14 = -2.0142E-10f;
        float A20 = -3.9064E-7f;
        float A21 = 9.1061E-9f;
        float A22 = -1.6009E-10f;
        float A23 = 7.994E-12f;
        float A30 = 1.100E-10f;
        float A31 = 6.651E-12f;
        float A32 = -3.391E-13f;
        float B00 = -1.922E-2f;
        float B01 = -4.42E-5f;
        float B10 = 7.3637E-5f;
        float B11 = 1.7950E-7f;
        float D00 = 1.727E-3f;
        float D10 = -7.9836E-6f;

        return ChenMilleroCW(temperature, pressure) + ChenMilleroA(temperature, pressure)*salinity + ChenMilleroB(temperature, pressure)*(float)(Math.pow(salinity, 3f/2f)) + ChenMilleroD(pressure)*(float)(Math.pow(salinity, 2));
    }
    
    private static float ChenMilleroCW(float temperature, float pressure){
        float C00 = 1402.388f;
        float C01 = 5.03830f;
        float C02 = -5.81090E-2f;
        float C03 = 3.3432E-4f;
        float C04 = -1.47797E-6f;
        float C05 = 3.1419E-9f;
        float C10 = 0.153563f;
        float C11 = 6.8999E-4f;
        float C12 = -8.1829E-6f;
        float C13 = 1.3632E-7f;
        float C14 = -6.1260E-10f;
        float C20 = 3.1260E-5f;
        float C21 = -1.7111E-6f;
        float C22 = 2.5986E-8f;
        float C23 = -2.5353E-10f;
        float C24 = 1.0415E-12f;
        float C30 = -9.7729E-9f;
        float C31 = 3.8513E-10f;
        float C32 = -2.3654E-12f;
        
        return (C00 + C01*temperature + C02*(float)(Math.pow(temperature, 2)) + C03*(float)(Math.pow(temperature,3)) + C04*(float)(Math.pow(temperature,4)) + C05*(float)(Math.pow(temperature,5))) + (C10 + C11*temperature + C12*(float)(Math.pow(temperature, 2)) + C13*(float)(Math.pow(temperature,3)) + C14*(float)(Math.pow(temperature,4)))*pressure + (C20 + C21*temperature + C22*(float)(Math.pow(temperature, 2)) + C23*(float)(Math.pow(temperature,3)) + C24*(float)(Math.pow(temperature,4)))*(float)(Math.pow(pressure,2)) + (C30 + C31*temperature + C32*(float)(Math.pow(temperature, 2)))*(float)(Math.pow(pressure,3));
    }
    
    private static float ChenMilleroA(float temperature, float pressure){
        float A00 = 1.389f;
        float A01 = -1.262E-2f;
        float A02 = 7.166E-5f;
        float A03 = 2.008E-6f;
        float A04 = -3.21E-8f;
        float A10 = 9.4742E-5f;
        float A11 = -1.2583E-5f;
        float A12 = -6.4928E-8f;
        float A13 = 1.0515E-8f;
        float A14 = -2.0142E-10f;
        float A20 = -3.9064E-7f;
        float A21 = 9.1061E-9f;
        float A22 = -1.6009E-10f;
        float A23 = 7.994E-12f;
        float A30 = 1.100E-10f;
        float A31 = 6.651E-12f;
        float A32 = -3.391E-13f;
        
        return (A00 + A01*temperature + A02*(float)(Math.pow(temperature, 2)) + A03*(float)(Math.pow(temperature,3)) + A04*(float)(Math.pow(temperature,4))) + (A10 + A11*temperature + A12*(float)(Math.pow(temperature, 2)) + A13*(float)(Math.pow(temperature,3)) + A14*(float)(Math.pow(temperature,4)))*pressure + (A20 + A21*temperature + A22*(float)(Math.pow(temperature, 2)) + A23*(float)(Math.pow(temperature,3)))*(float)(Math.pow(pressure,2)) + (A30 + A31*temperature + A32*(float)(Math.pow(temperature, 2)))*(float)(Math.pow(pressure,3));
    }
    
    private static float ChenMilleroB(float temperature, float pressure){
        float B00 = -1.922E-2f;
        float B01 = -4.42E-5f;
        float B10 = 7.3637E-5f;
        float B11 = 1.7950E-7f;
        return B00+B01*temperature+(B10+B11*temperature)*pressure;
    }
    
    private static float ChenMilleroD(float pressure){
        float D00 = 1.727E-3f;
        float D10 = -7.9836E-6f;
        return D00+(D10*pressure);
    }
}
