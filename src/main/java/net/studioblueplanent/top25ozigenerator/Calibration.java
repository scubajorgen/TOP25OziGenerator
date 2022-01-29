/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanent.top25ozigenerator;

/**
 *
 * @author jorgen
 */
public class Calibration
{
    public String   mapNumber;
    public String   mapName;
    public int      xMin;
    public int      xMax;
    public int      yMin;
    public int      yMax;
    public int      year;

    public String getMapNumber()
    {
        return mapNumber;
    }

    public void setMapNumber(String mapNumber)
    {
        this.mapNumber = mapNumber;
    }

    public String getMapName()
    {
        return mapName;
    }

    public void setMapName(String mapName)
    {
        this.mapName = mapName;
    }

    public int getxMin()
    {
        return xMin;
    }

    public void setxMin(int xMin)
    {
        this.xMin = xMin;
    }

    public int getxMax()
    {
        return xMax;
    }

    public void setxMax(int xMax)
    {
        this.xMax = xMax;
    }

    public int getyMin()
    {
        return yMin;
    }

    public void setyMin(int yMin)
    {
        this.yMin = yMin;
    }

    public int getyMax()
    {
        return yMax;
    }

    public void setyMax(int yMax)
    {
        this.yMax = yMax;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }
    

}
