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

/**
 *
 * @author jorgen
 */
public class GeneratorTest
{
    
    public GeneratorTest()
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
     * Test of RdToLanLon method, of class Generator.
     */
    @Test
    public void testRdToLanLon()
    {
        System.out.println("RdToLanLon");
        double x = 100000.0;
        double y = 400000.0;
        Generator instance = new Generator();
        Generator.LatLon result = instance.RdToLanLon(x, y);
        assertEquals(51.586220, result.lat, 0.000001);
        assertEquals(4.593599 , result.lon, 0.000001);
    }

    /**
     * Test of rdToBesselLatLon method, of class Generator.
     */
    @Test
    public void testRdToBesselLatLon()
    {
        System.out.println("rdToBesselLatLon");
        double x = 100000.0;
        double y = 400000.0;
        Generator instance = new Generator();
        Generator.LatLon result = instance.rdToBesselLatLon(x, y);
        assertEquals(51.5871380, result.lat, 0.0000001);
        assertEquals(4.5939185 , result.lon, 0.0000001);
    }

}
