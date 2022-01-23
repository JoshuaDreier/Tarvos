package javaCode;

import javafx.geometry.Point3D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.lang.Math.*;
import static javaCode.ValueSet.*;
import static javafx.geometry.Point3D.*;

public class IsVisibleCalculator {
    Earth referenceObject;
    Satellite satellite;
    SunLight sun;
    CoordinateLocation location;
    double angleToSatellite, horizonAngleToSun;
    Point3D referenceObjectLocation, coordinateLocation, satelliteLocation, sunLocation;
    Point3D vector_planetToSurfaceLocation, vector_surfaceLocationToSatellite, vector_surfaceLocationToSun;
    Path output;

    IsVisibleCalculator(Earth inputReferenceObject, CoordinateLocation inputLocation,
                        Satellite inputSatellite, SunLight inputSun){

        satellite = inputSatellite;
        referenceObject = inputReferenceObject;
        location = inputLocation;
        sun = inputSun;

        output = Paths.get("output.txt");
    }
    public String isVisibleNow(double time, byte simMode, String timeText){
        String output;
        updatePositions(time, simMode);

        //visible angle of the Satellite
        angleToSatellite = vector_planetToSurfaceLocation.angle( vector_surfaceLocationToSatellite );

        //visible angle of the Sun
        horizonAngleToSun = vector_planetToSurfaceLocation.angle( vector_surfaceLocationToSun ) - 90 ;

        if (angleToSatellite < MINIMUM_SATELLITE_VISIBILITY_ANGLE) {    //= is satellite over horizon?
            output = timeText + "  ALT:" + (90-floor( angleToSatellite ));
            if (horizonAngleToSun < MAX_SUNLIGHT_ANGLE) //= is evening?
                output = "VISIBLE: " + output;
            return output + System.lineSeparator();
        } else return "";
    }
    public void isVisibleInPreCalculationPeriod(double startTime){
        try {                                           //calculates all visible passes for the next PRE_CALCULATION_DAYS
            Files.deleteIfExists( output );
            Files.createFile( output );
            for (byte simMode = 0; simMode <= 1; simMode++) {
                Files.write(output, ("SimMode: " + (simMode) + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND );
                for (int t = 0; t < SATELLITE_PRE_CALCULATION_PERIOD/VISIBILITY_CHECK_MULTIPLIER; t++) {
                    Files.write(output,
                    isVisibleNow(startTime + t*VISIBILITY_CHECK_MULTIPLIER, simMode,
                            (dateToString( startTime + t*VISIBILITY_CHECK_MULTIPLIER ))).getBytes(),
                            StandardOpenOption.APPEND);
                }
            }
        } catch (IOException e) {
            System.out.println("unable to write to file " + output.toString());
            e.printStackTrace();
        }
    }
    public void updatePositions(double time, byte simMode) {
        //Updates Point3Ds of referenceObject (Earth), satellite and the coordinates in relation to Parent Group
        referenceObjectLocation = ZERO;
        coordinateLocation = location.position[(int) time/SLOW_CALC_MULTIPLIER];
        try {
            satelliteLocation = satellite.position[simMode][(int) time];
        }catch (NullPointerException n){
            satelliteLocation = ZERO;
        }
        sunLocation = sun.sunPosition( time );

        //Vector from Planet to Location
        vector_planetToSurfaceLocation = coordinateLocation.subtract( referenceObjectLocation );
        //Vector from Location to Satellite
        vector_surfaceLocationToSatellite = satelliteLocation.subtract( coordinateLocation );
        //Vector from Location to Sun
        vector_surfaceLocationToSun = sunLocation.subtract( coordinateLocation );
    }

}