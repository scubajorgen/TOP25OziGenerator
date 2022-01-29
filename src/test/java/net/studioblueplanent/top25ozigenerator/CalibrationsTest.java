/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanent.top25ozigenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 *
 * @author jorgen
 */
public class CalibrationsTest
{
    
    public CalibrationsTest()
    {
    }
    
    @BeforeAll
    public static void setUpClass()
    {
    }
    
    @AfterAll
    public static void tearDownClass()
    {
    }
    
    @BeforeEach
    public void setUp()
    {
    }
    
    @AfterEach
    public void tearDown()
    {
    }

    /**
     * Test of readCalibrationsFromExcel method, of class Calibrations.
     */
    @Test
    public void testReadCalibrationsFromExcel()
    {
        System.out.println("readCalibrationsFromExcel");
        String fileName = "src/test/resources/Bladnaam_nummer_coord 25000.xls";
        Calibrations instance = new Calibrations();
        instance.readCalibrationsFromExcel(fileName);
        
        List<Calibration> cals=instance.getCalibrations();
        assertEquals(390, cals.size());
        
        assertEquals("01C", cals.get(0).getMapNumber());
        assertEquals("FORMERUM", cals.get(0).getMapName());
        assertEquals(140000, cals.get(0).getxMin());
        assertEquals(600000, cals.get(0).getyMin());
        assertEquals(150000, cals.get(0).getxMax());
        assertEquals(612500, cals.get(0).getyMax());

        assertEquals("63H", cals.get(389).getMapNumber());
    }

    /**
     * Test of readCalibrationsFromExcel method, of class Calibrations.
     */
    @Test
    public void testGetCalibration()
    {
        System.out.println("readCalibrationsFromExcel");
        String fileName = "src/test/resources/Bladnaam_nummer_coord 25000.xls";
        Calibrations instance = new Calibrations();
        instance.readCalibrationsFromExcel(fileName);
        
        Calibration c=instance.getCalibration("62E");
        assertNotNull(c);
        assertEquals("KERKRADE", c.getMapName());
        assertEquals("62E", c.getMapNumber());

        c=instance.getCalibration("PietjePuk");
        assertNull(c);

    }
    
}
