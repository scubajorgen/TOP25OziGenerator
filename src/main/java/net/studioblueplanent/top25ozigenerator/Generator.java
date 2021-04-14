/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanent.top25ozigenerator;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
    
    
    public class Calibration
    {
        public String   mapNumber;
        public String   mapName;
        public int      xMin;
        public int      xMax;
        public int      yMin;
        public int      yMax;
        public int      year;
    }
    
    public class LatLon
    {
        public double lat;
        public double lon;
    }
    
    private static final String COMMA_DELIMITER=",";
    private List<Calibration>   calibrations;
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
    
    
    private void readCallibrations(String filename)
    {
        calibrations = new ArrayList<>();
        try
        {
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) 
            {
                String line;
                System.out.println("Reading calibrations");
                br.readLine();
                br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(COMMA_DELIMITER);
                    if (values.length==8)
                    {
                        Calibration cal=new Calibration();
                        cal.mapNumber=values[0];
                        cal.mapName  =values[1];
                        cal.xMin     =Integer.parseInt(values[3]);
                        cal.yMin     =Integer.parseInt(values[4]);
                        cal.xMax     =Integer.parseInt(values[5]);
                        cal.yMax     =Integer.parseInt(values[6]);
                        cal.year     =Integer.parseInt(values[7]);
                        calibrations.add(cal);
                    }
                }
            }  
        }
        catch (IOException e)
        {
            System.err.println("Error reading calibration file "+filename);
        }
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
    
    private void writeMapFile(String path, Calibration cal)
    {
        String fileString;
        String filename;
        LatLon ll;
        
        filename=path+cal.mapNumber.toLowerCase()+"-top25raster-"+cal.year+".map";
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
    
    private void writeMapFiles(String path)
    {
        calibrations.stream().forEach(c -> writeMapFile(path, c));
    }
    
    /**
     * Create the map files
     * @param gen Generator instance
     */
    public void generate()
    {
        readCallibrations("Bladnaam_nummer_coord_25000.csv");
        readTemplate("template.map");
        writeMapFiles("./maps/");        
    }
    
    public static void main(String[] args)
    {
        Generator gen;
        
        gen=new Generator();
        gen.generate();
        
        System.out.println("Done");
    }
}
