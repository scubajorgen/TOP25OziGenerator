# TOP25Raster OZI map generator
## Introduction
Excellent topographic raster maps of the Netherlands can be loaded at [PDOK](https://www.pdok.nl/introductie/-/article/dataset-basisregistratie-topografie-brt-topraster).
The format is GeoTIFF. However, this format is not supported directly by [OziExplorer](https://www.oziexplorer4.com/). Though OziExplorer has a function to import DRG maps, the resulting map file
does not seem to have the right coordinates.

This quick and dirty java application generates the OziExplorer map files based on the enclosed excel file in the Top25Raster package.

I tried OziExplorer and MAPC2MAPC to convert GeoTIFF to Ozi. Both resulted in maps that seem not to be correct when comparing the coordinates of well known calibration points. Therefore I decided to write this little program.

## Usage
1. Download the Top25Raster complete set zip from [PDOK](https://www.pdok.nl/downloads/-/article/dataset-basisregistratie-topografie-brt-topraster).
2. Unzip
3. The metadata you need is in */TOP25raster_GEOTIFF\_[month]\_[year]/TOP25raster_productinformatie/* 
   Open the excel file enclosed in this directory. 
   The file is called *Bladnaam_nummer_coord 25000.xlsx*.
   Add a new column 'JAAR' stating the year of the map files and add the year. This year can be found on the file *BRT_Actualiteitskaart_februari_2021.pdf* or simply look at the .tif files.
2. Export the excel to csv and give it the name: *Bladnaam_nummer_coord_25000.csv*. Move it to the program directory.
3. In the program directory, modify the *template.map* file if required. This file is the template for the OziExplorer map files that will be created. Values between $...$ will be substituted.
4. Run the software in this directory, now containing *Bladnaam_nummer_coord_25000.csv* and *template.map*. Make sure a directory *./maps* exists.

The .map files will be created for the .tif files in the *./map* directory. 

## About the Top25 Raster files
The map datum is [Rijksdriehoeksmeting](https://nl.wikipedia.org/wiki/Rijksdriehoeksco%C3%B6rdinaten), with false northing 155000 and false easting -5316592.
The projection happens to be **transverse mercator**, with central meridian E 5.387633333. This is odd since rijksdriehoeksmeting usually is used with stereographic projection (ETRS89).


