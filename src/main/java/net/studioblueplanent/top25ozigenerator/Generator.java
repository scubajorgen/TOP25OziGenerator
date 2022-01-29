/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanent.top25ozigenerator;

import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author jorgen
 */
public class Generator
{
    // Coordinates
    // NW(xmin, ymax)       NE(xmax, ymax)
    //
    // SW(xmin, ymin)       SE(xmax, ymin)
    
    

    
    public class LatLon
    {
        public double lat;
        public double lon;
    }
    
    private static final String EXCELFILE="TOP25raster_Productinformatie/Bladnaam_nummer_coord 25000.xls";
    private static final String TIFFDIRECTORY="TOP25raster_GEOTIFF";
    private static final String COMMA_DELIMITER=",";
    private Calibrations        calibrations;
    private String              template;
    
    /**
     * Calculate lat and lon based on RD coordinates. See method
     * https://media.thomasv.nl/2015/07/Transformatieformules.pdf
     * @param x Easting
     * @param y Northing
     * @return WGS84 lat/lon coordinate
     */
    public LatLon RdToLanLon(double x, double y)
    {
        double dX;
        double dY;
        double SomN;
        double SomE;
        
        LatLon ll=new LatLon();
        
	dX = (x - 155000) * 0.00001;
	dY = (y - 463000) * 0.00001;

	SomN = (3235.65389 * dY) + 
               (-32.58297  * Math.pow(dX, 2)) + 
               (-0.24750   * Math.pow(dY, 2)) + 
               (-0.84978   * Math.pow(dX, 2) * dY) + 
               (-0.0655    * Math.pow(dY, 3)) + 
               (-0.01709   * Math.pow(dX, 2) * Math.pow(dY, 2)) + 
               (-0.00738   * dX) + 
               (0.00530    * Math.pow(dX, 4)) + 
               (-0.00039   * Math.pow(dX, 2) * Math.pow(dY, 3)) + 
               (0.00033    * Math.pow(dX, 4) * dY) + 
               (-0.00012   * dX * dY);
	SomE = (5260.52916 * dX) + 
                (105.94684 * dX * dY) + 
                (2.45656   * dX * Math.pow(dY, 2)) + 
                (-0.81885  * Math.pow(dX, 3)) + 
                (0.05594   * dX * Math.pow(dY, 3)) + 
                (-0.05607  * Math.pow(dX, 3) * dY) + 
                (0.01199   * dY) + 
                (-0.00256  * Math.pow(dX, 3) * Math.pow(dY, 2)) + 
                (0.00128   * dX * Math.pow(dY, 4)) + 
                (0.00022   * Math.pow(dY, 2)) + 
                (-0.00022  * Math.pow(dX, 2)) + 
                (0.00026   * Math.pow(dX, 5));

	ll.lat = 52.15517440 + (SomN / 3600.0);
	ll.lon = 5.38720621  + (SomE / 3600.0);

        return ll;
    }
    
    /**
     * Conversion of RD coordinates to Bessel 1841 LatLon. 
     * @param x RD Easting in m
     * @param y RD Northing in m
     * @return Latitude/Longitude
     */
    public LatLon rdToBesselLatLon(double x, double y)
    {
        double a        =6377397.155;       // m
        double e        =0.081696831222;    // 
        double phi0     =52.156160556/360.0*2.0*Math.PI;      // rad
        double lambda0  = 5.387638889/360.0*2.0*Math.PI;      // rad
        double B0       =52.121097249/360.0*2.0*Math.PI;      // rad
        double L0       = 5.387638889/360.0*2.0*Math.PI;      // rad
        double n        =1.00047585668;
        double m        =0.003773953832;
        double R        =6382644.571;       // m
        double k        =0.9999079;
        double x0       =155000;            // m
        double y0       =463000;            // m 

        double r        =Math.sqrt(Math.pow(x-x0,2)+Math.pow(y-y0, 2));
        double sinAlpha =(x-x0)/r;
        double cosAlpha =(y-y0)/r;
        double psi      =2.0*Math.atan(r/(2.0*k*R));
        double B        =Math.asin(cosAlpha*Math.cos(B0)*Math.sin(psi)+Math.sin(B0)*Math.cos(psi));
        double deltaL   =Math.asin(sinAlpha*Math.sin(psi)/Math.cos(B));
        double lambda   =deltaL/n+lambda0;

        double w        =Math.log(Math.tan(0.5*B+0.25*Math.PI));
        double q        =(w-m)/n;
        double phix     =2.0*Math.atan(Math.exp(q))-Math.PI*0.5;   
        
        double deltaQ;
        int i=0;
        while (i<4)
        {
            deltaQ=0.5*e*Math.log((1+e*Math.sin(phix))/(1-e*Math.sin(phix)));
            phix     =2.0*Math.atan(Math.exp(q+deltaQ))-Math.PI*0.5; 
            i++;
        }
        
        LatLon ll=new LatLon();
        ll.lon=lambda*360.0/2.0/Math.PI;
        ll.lat=phix  *360.0/2.0/Math.PI;
        return ll;
    }
    
    
    private void readTemplate(String filename)
    {
        StringBuilder contentBuilder = new StringBuilder();
 
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) 
        {
            String line;

            System.out.println("Reading template");
            while ((line = br.readLine()) != null) 
            {
                contentBuilder.append(line).append("\n");                
            }

        }         
        catch (IOException e) 
        {
            System.err.println("Error reading template file "+filename);
        }
 
        template=contentBuilder.toString();
    }
    
    /** 
     * Write the map file using the template and the calibration passed
     * @param filename Name of the file
     * @param cal Calibration of the map file
     */
    private void writeMapFile(String filename, Calibration cal)
    {
        String fileString;
        LatLon ll;
        
        fileString=template.replace("$number$", cal.mapNumber.toLowerCase());
        fileString=fileString.replace("$name$", cal.mapName);
        fileString=fileString.replace("$xMin$", Integer.toString(cal.xMin));
        fileString=fileString.replace("$xMax$", Integer.toString(cal.xMax));
        fileString=fileString.replace("$yMin$", Integer.toString(cal.yMin));
        fileString=fileString.replace("$yMax$", Integer.toString(cal.yMax));
        fileString=fileString.replace("$year$", Integer.toString(cal.year));
        
        ll=this.rdToBesselLatLon(cal.xMin, cal.yMax);
        fileString=fileString.replace("$nwLat$", Double.toString(ll.lat));
        fileString=fileString.replace("$nwLon$", Double.toString(ll.lon));

        ll=this.rdToBesselLatLon(cal.xMax, cal.yMax);
        fileString=fileString.replace("$neLat$", Double.toString(ll.lat));
        fileString=fileString.replace("$neLon$", Double.toString(ll.lon));
        
        ll=this.rdToBesselLatLon(cal.xMin, cal.yMin);
        fileString=fileString.replace("$swLat$", Double.toString(ll.lat));
        fileString=fileString.replace("$swLon$", Double.toString(ll.lon));

        ll=this.rdToBesselLatLon(cal.xMax, cal.yMin);
        fileString=fileString.replace("$seLat$", Double.toString(ll.lat));
        fileString=fileString.replace("$seLon$", Double.toString(ll.lon));
        

        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            System.out.println("Writing "+filename);
            writer.write(fileString);
            writer.close();
        }
        catch (IOException e) 
        {
            System.err.println("Error reading template file "+filename);
        }        
    }


    /**
     * Write the .map files based on the .tif files in the directory passed
     * @param tiffDirectory The directory containing the tiff files 
     */
    private void writeMapFiles(String tiffDirectory)
    {
        String      mapNumber;
        String      mapYear;
        Calibration c;
        
        File        folder = new File(tiffDirectory);
        File[]      listOfFiles = folder.listFiles();  
        
        int i=0;
        while (i<listOfFiles.length)
        {
            System.out.println("Processing file "+listOfFiles[i].getName());
            
            // Get the first part (01c-top25raster-2019) of the filename (01c-top25raster-2019.tif)
            if (listOfFiles[i].getName().endsWith(".tif"))
            {
                String fileName=listOfFiles[i].getName().replace(".tif", "");
                if (fileName!=null)
                {
                    // Split the first part into subparts
                    String[] parts = fileName.split("-");
                    if (parts.length==3)
                    {
                        mapNumber=parts[0].toUpperCase();
                        mapYear  =parts[2];
                        c=calibrations.getCalibration(mapNumber);
                        if (c==null)
                        {
                            System.err.println("Calibration not found for map "+listOfFiles[i].getName());
                        }
                        else
                        {
                            System.out.println("Map "+c.getMapNumber()+", "+c.getMapName());
                            c.setYear(Integer.parseInt(mapYear));
                            writeMapFile(listOfFiles[i].getAbsolutePath().replace(".tif", ".map"), c);
                        }
                    }
                    else
                    {
                        System.err.println("Unexpected file: "+listOfFiles[i].getName());
                    }
                }
            }
            i++;
        }
    }
    
    
    /**
     * Create the map files
     * @param gen Generator instance
     */
    public void generate()
    {
        calibrations=new Calibrations();
        calibrations.readCalibrationsFromExcel(EXCELFILE);
        readTemplate("template.map");
        writeMapFiles(TIFFDIRECTORY);        
    }
    
    public static void main(String[] args)
    {
        Generator gen;
        
        gen=new Generator();
        gen.generate();
       
        System.out.println("Done");
    }
}
