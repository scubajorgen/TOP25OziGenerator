# TOP25Raster OZI Explorer map file generator
## Introduction
Excellent topographic raster maps of the Netherlands can be loaded at [PDOK](https://www.pdok.nl/introductie/-/article/dataset-basisregistratie-topografie-brt-topraster).
The format is GeoTIFF. However, this format is not supported directly by [OziExplorer](https://www.oziexplorer4.com/). Though OZI Explorer has a function to import DRG maps, the resulting map file does not seem to have the right coordinates.

This quick and dirty java application generates the **OZI Explorer map files* based on the enclosed excel file in the Top25Raster package.

I tried OziExplorer and MAPC2MAPC to convert GeoTIFF to Ozi. Both resulted in maps that seem not to be correct when comparing the coordinates of well known calibration points. Therefore I decided to write this little program.

## What it does
OZI explorer maps basically consists of two files:
* The **image file** graphically representing the map. It can be a variety of formats: regular image formats like png, tiff or jpg, or OZI Explorer specific formats like ozf2, ozf3 or ozf4. The latter are tile based, which means rendering is very fast.
* The **.map** file specifying map metadata, like the projection and map datum used, calibration, moving map, etc. The **.map** file is a CSV file like the example below. Given an image file, you can create your corresponding **.map** file by the OZI Exlorer function *Load and calibrate map* from the *File* menu.

```
OziExplorer Map Data File Version 2.2
FORMERUM
01c-top25raster-2019.tif
1 ,Map Code,
Rijksdriehoeksmeting,WGS 84,   0.0000,   0.0000,WGS 84
Reserved 1
Reserved 2
Magnetic Variation,,,E
Map Projection,Transverse Mercator,PolyCal,No,AutoCalOnly,No,BSBUseWPX,No
Point01,xy,    0,    0,in, deg,    ,        ,,    ,        ,, grid,   , 140000    , 612500    ,N
Point02,xy, 8000,10000,in, deg,    ,        ,,    ,        ,, grid,   , 150000    , 600000    ,N
Point03,xy,     ,     ,in, deg,    ,        ,,    ,        ,, grid,   ,           ,           ,
...
Projection Setup,     0.000000000,     5.387633333,     0.999907900,       155000.00,-5316592.00,    ,    ,,,
Map Feature = MF ; Map Comment = MC     These follow if they exist
Track File = TF      These follow if they exist
Moving Map Parameters = MM?    These follow if they exist
MM0,Yes
MMPNUM,4
MMPXY,1,0,0
MMPXY,2,8000,0
MMPXY,3,8000,10000
MMPXY,4,0,10000
MMPLL,1,   5.161580295747831,  53.499587042238
MMPLL,2,   5.3122857720828875,  53.4997753865503
MMPLL,3,   5.312482611894164,  53.3874518010623
MMPLL,4,   5.1621708085172795,  53.38726408289282
MM1B,2.496705
LL Grid Setup
LLGRID,Yes,10 Sec,Yes,255,16711680,0,No Labels,0,16777215,7,1,Yes,x
Other Grid Setup
GRGRID,No,1 Km,No,14737632,13684944, Labels,0,16777215,8,1,Yes,No,No,x
MOP,Map Open Position,0,0
MOPLL,Map Open Position,0.0000000,0.0000000,100
IWH,Map Image Width/Height,4000,5000
MLP,Map Last Position,5.2966377,6.1094781,500
```

Like stated, OZI Explorer does not support metadata from the  GeoTIFF .tif files. We therefore need an alternative way to create the .map files for the .tif image files. Here comes in this tool.

Enclosed in the download package from PDOK is an Excel file (_/TOP25raster_GEOTIFF_[month]_[year]\TOP25raster_Produktinformatie\Bladnaam_nummer_coord 25000.xls_) containing an overview of the enclosed maps together with the coordinates covered by the map. The tool will this information and a .map template to generate the .map files.

During the process the Rijksdriehoeks coordinates are converted to the regular WGS84 in the moving map section. This section is used by OZI Mapmerge a.o.

## Building
Build the tool with maven:
```
mvn clean install
```
Or build it in your favourite IDE (Netbeans, ...)


## Usage
1. Download the Top25Raster complete set zip from [PDOK](https://www.pdok.nl/downloads/-/article/dataset-basisregistratie-topografie-brt-topraster).
2. Enter the program directory _TOP25OziGenerator_
2. Copy the tif files (from the zip) to TOP25raster_GEOTIFF. Basically you need only to copy the tif files you need .map files for
3. Copy the excel file _Bladnaam_nummer_coord 25000.xls_ from the zip to _/TOP25raster_Productinformatie_
4. Run the software: 
   _java -jar target/TOP25OziGenerator-1.0-SNAPSHOT-jar-with-dependencies.jar_.
   This wil generate the map files in the directory _TOP25raster_GEOTIFF_
5. You can now open the map in OZI Explorer 


## About the Top25 Raster files
The map datum is [Rijksdriehoeksmeting](https://nl.wikipedia.org/wiki/Rijksdriehoeksco%C3%B6rdinaten), with false northing 155000 and false easting -5316592.
The projection happens to be **transverse mercator**, with central meridian E 5.387633333. This is odd since rijksdriehoeksmeting usually is used with stereographic projection (ETRS89).


