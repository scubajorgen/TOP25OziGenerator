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
    
    private static final String COMMA_DELIMITER=",";
    private List<Calibration>   calibrations;
    private String              template;
    
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
        
        filename=path+cal.mapNumber.toLowerCase()+"-top25raster-"+cal.year+".map";
        fileString=template.replace("$number$", cal.mapNumber.toLowerCase());
        fileString=fileString.replace("$name$", cal.mapName);
        fileString=fileString.replace("$xMin$", Integer.toString(cal.xMin));
        fileString=fileString.replace("$xMax$", Integer.toString(cal.xMax));
        fileString=fileString.replace("$yMin$", Integer.toString(cal.yMin));
        fileString=fileString.replace("$yMax$", Integer.toString(cal.yMax));
        fileString=fileString.replace("$year$", Integer.toString(cal.year));

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
