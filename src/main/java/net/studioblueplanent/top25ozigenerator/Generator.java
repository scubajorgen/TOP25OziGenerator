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
    
    private LatLon RdToLanLon(double x, double y)
    {
        double dX;
        double dY;
        double SomN;
        double SomE;
        double scale=0.999907900;
        
        LatLon ll=new LatLon();
        
	dX = (x - 155000) * 0.00001;
	dY = (y - 463000) * 0.00001;

/*        
	SomN = (3235.65389 * dY) + 
                (-32.58297 * dX ^ 2) + 
                (-0.2475 * dY ^ 2) + 
                (-0.84978 * dX ^ 2 * dY) + 
                (-0.0655 * dY ^ 3) + 
                (-0.01709 * dX ^ 2 * dY ^ 2) + 
                (-0.00738 * dX) + 
                (0.0053 * dX ^ 4) + 
                (-0.00039 * dX ^ 2 * dY ^ 3) + 
                (0.00033 * dX ^ 4 * dY) + 
                (-0.00012 * dX * dY)
	SomE = (5260.52916 * dX) + 
                (105.94684 * dX * dY) + 
                (2.45656 * dX * dY ^ 2) + 
                (-0.81885 * dX ^ 3) + 
                (0.05594 * dX * dY ^ 3) + 
                (-0.05607 * dX ^ 3 * dY) + 
                (0.01199 * dY) + 
                (-0.00256 * dX ^ 3 * dY ^ 2) + 
                (0.00128 * dX * dY ^ 4) + 
                (0.00022 * dY ^ 2) + 
                (-0.00022 * dX ^ 2) + 
                (0.00026 * dX ^ 5)
*/        
        
	SomN = (3235.65389 * dY) + 
               (-32.58297 * dX*dX) + 
               (-0.2475 * dY*dY) + 
               (-0.84978 * dX * dX * dY) + 
               (-0.0655 * dY * dY *dY) + 
               (-0.01709 * dX *dX * dY *dY) + 
               (-0.00738 * dX) + 
               (0.0053 * dX * dX * dX * dX) + 
               (-0.00039 * Math.pow(dX, 2) * Math.pow(dY, 3)) + 
               (0.00033 * Math.pow(dX, 4) * dY) + 
               (-0.00012 * dX * dY);
	SomE = (5260.52916 * dX) + 
                (105.94684 * dX * dY) + 
                (2.45656 * dX * Math.pow(dY, 2)) + 
                (-0.81885 * Math.pow(dX, 3)) + 
                (0.05594 * dX * Math.pow(dY, 3)) + 
                (-0.05607 * Math.pow(dX, 3) * dY) + 
                (0.01199 * dY) + 
                (-0.00256 * Math.pow(dX, 3) * Math.pow(dY, 2)) + 
                (0.00128 * dX * Math.pow(dY, 4)) + 
                (0.00022 /** Math.pow(dY, 2)*/) + 
                (-0.00022 * Math.pow(dX, 2)) + 
                (0.00026 * Math.pow(dX, 5));

	ll.lat = 52.1551744 + (SomN / 3600.0);
	ll.lon = 5.38720621 + (SomE / 3600.0);

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
        
        ll=this.RdToLanLon(cal.xMin, cal.yMax);
        fileString=fileString.replace("$nwLat$", Double.toString(ll.lat));
        fileString=fileString.replace("$nwLon$", Double.toString(ll.lon));

        ll=this.RdToLanLon(cal.xMax, cal.yMax);
        fileString=fileString.replace("$neLat$", Double.toString(ll.lat));
        fileString=fileString.replace("$neLon$", Double.toString(ll.lon));
        
        ll=this.RdToLanLon(cal.xMin, cal.yMin);
        fileString=fileString.replace("$swLat$", Double.toString(ll.lat));
        fileString=fileString.replace("$swLon$", Double.toString(ll.lon));

        ll=this.RdToLanLon(cal.xMax, cal.yMin);
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
    
    public static void main(String[] args)
    {
        Generator gen;
        
        gen=new Generator();
        gen.readCallibrations("Bladnaam_nummer_coord_25000.csv");
        gen.readTemplate("template.map");
        gen.writeMapFiles("./maps/");
        System.out.println("Done");
    }
}
