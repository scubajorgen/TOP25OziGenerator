/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanent.top25ozigenerator;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
/**
 *
 * @author jorgen
 */
public class Calibrations
{
    private final List<Calibration>   calibrations;    
    
    /**
     * Constructor. Initialises the calibrations list
     */
    public Calibrations()
    {
        calibrations=new ArrayList<>();
    }
    
    /**
     * Read the calibrations from the PDOK excel file
     * @param fileName Name of the file
     */
    public void readCalibrationsFromExcel(String fileName)
    {
        try
        {
            FileInputStream file = new FileInputStream(new File(fileName));
            Workbook workbook = new HSSFWorkbook(file);
            
            Sheet sheet = workbook.getSheetAt(0);
            
            int i = 0;
            for (Row row : sheet) 
            {
                if (i>1 && row!=null)
                {
                    Calibration cal=new Calibration();
                    if (row.getLastCellNum()==7)
                    {
                        cal.setMapNumber(row.getCell(0).getStringCellValue());
                        cal.setMapName(row.getCell(1).getStringCellValue());
                        cal.setxMin((int)(row.getCell(3).getNumericCellValue()+0.5));
                        cal.setyMin((int)(row.getCell(4).getNumericCellValue()+0.5));
                        cal.setxMax((int)(row.getCell(5).getNumericCellValue()+0.5));
                        cal.setyMax((int)(row.getCell(6).getNumericCellValue()+0.5));
                        calibrations.add(cal);
                    }
                }
                i++;
            }

        }
        catch (FileNotFoundException e)
        {
            System.err.println("File "+fileName+" not found: "+e.getMessage());
        }
        catch (IOException e)
        {
            System.err.println("Error reading file "+fileName+": "+e.getMessage());
        }
        System.out.println("Calibrations read from "+fileName);
    }
    
    /**
     * Get the list of calibrations
     * @return The list of calibrations
     */
    public List<Calibration> getCalibrations()
    {
        return calibrations;
    }
    
    /**
     * Returns calibration from the list based on map number
     * @param mapNumber The number to look for
     * @return The found calibration or null if not found
     */
    public Calibration getCalibration(String mapNumber)
    {
        Iterator<Calibration>   it;
        Calibration             c;
        Calibration             found;
        
        it      =calibrations.iterator();
        found   =null;
        while (it.hasNext() && found==null)
        {
            c=it.next();
            if (c.getMapNumber().equals(mapNumber))
            {
                found=c;
            }
        }
        return found;
    }
}
