package javaCode;

import javafx.geometry.Point3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import static java.lang.Math.floor;
import static java.lang.StrictMath.*;
import static java.lang.String.format;
import static javafx.geometry.Point3D.*;
import static javafx.scene.transform.Rotate.*;

class ValueSet {
    //Physical Constants
    public static final double GRAVITATIONAL_CONSTANT = 6.67408E-11;
    public static final double EARTH_MASS = 5.9722E24;
    public static final double EARTH_RADIUS = 6371E3;
    public static final double EARTH_AXIAL_TILT = 25.5;
    public static final double SUN_DISTANCE = 149_597_870_700D;

    //Default Values
    public static double STANDARD_INCLINATION = 51.664;
    public static double STANDARD_ORBIT_HEIGHT = 424E3;

    public static double LATITUDE = 47.447839910007204;
    public static double LONGITUDE = 9.640037238574251;
    public static double ELEVATION = 402;

    public static final double VISUALISATION_SCALE_FACTOR =  1E-3;
    public static final double FRAMES_PER_SECOND = 60;
    public static final double RA_ITERATIONS_PD = 1E4; //Rotation-Axis search Iterations per degree
    public static final double RA_ITERATIONS_TOLERANCE = 1E-2; //Rotation-Axis search Iterations per degree
    public static final short BACKGROUND_LIGHT_INTENSITY = 25;
    public static final double PLANE_SIZE = 7;

    public static final int MINIMUM_SATELLITE_VISIBILITY_ANGLE = 80;
    public static final int MAX_SUNLIGHT_ANGLE = 15;

    public static final int DELTA_UNIX_YEAR = 0;//16094592000; //Seconds between Jan 1 1970 and Jan 1 2021
    public static final int DELTA_UNIX_VERNAL_EQUINOX = 1616241600;
    public static final double SIDEREAL_DAY_LENGTH = 86164.098912; // IN SEC
    public static final double SIDEREAL_YEAR_LENGTH= 31558149.504;
    public static final double GMST_VERNAL_EQUINOX = 180    ;

    public static final int SATELLITE_PRE_CALCULATION_DAYS = 5  ; // Amount of Days to Calculate for the Satellites
    public static final int SATELLITE_PRE_CALCULATION_PERIOD = SATELLITE_PRE_CALCULATION_DAYS * 24 * 60 * 60; // PRE_CALCULATION_DAYS in Seconds

    public static final byte VISIBILITY_CHECK_MULTIPLIER = 5;

    public static final byte SLOW_CALC_MULTIPLIER = 5;
    public static final int SLOW_CALC_PRE_CALCULATION_DAYS = 370  ; // Amount of Days to Calculate for Earth and CoordinateLocation
    public static final int SLOW_CALC_PRE_CALCULATION_PERIOD = SLOW_CALC_PRE_CALCULATION_DAYS * 24 * 60 * (60/SLOW_CALC_MULTIPLIER); // PRE_CALCULATION_DAYS in 5 second increments

    public static Scale uniformScale(double factor){
        return new Scale(factor, factor, factor);
    }
    public static double distanceToNormalPlane(Point3D n, Point3D p){
        return abs(n.dotProduct( p ))/(n.magnitude());
    }
    public static Point3D rotatePoint(Point3D point, Point3D rotationAxis, double angle ){
        Sphere calcSphere = new Sphere();
        calcSphere.getTransforms().add( new Translate( point.getX(), point.getY(), point.getZ()) );
        calcSphere.setRotationAxis( rotationAxis );
        calcSphere.setRotate( angle );
        return calcSphere.localToScene( ZERO );
    }
    public static Rotate[] rotateTowards(Point3D target){
        Rotate xRotate, yRotate;
        xRotate = new Rotate(asin( target.getY()/target.magnitude() ), X_AXIS);
        yRotate = new Rotate(atan2( target.getZ(), target.getX()), Y_AXIS);
        return new Rotate[]{xRotate, yRotate};
    }
    public static double unixToJulian(double unixTime) {return unixTime / 86_400 + 2_440_587.5;}
    public static int[] julianToDate(double jd) {
        int z, c, d, e;
        int year, month, day, hour, min, sec;
        double alpha, a, f, b;
        double hour_dec, min_dec;

        z = (int) floor( jd + 0.5 );
        alpha = floor( (z - 1_867_216.25) / 36_524.25 );
        a = z + 1 + alpha - floor( alpha / 4 );

        f = jd + 0.5 - z;
        b = a + 1524;
        c = (int) floor( (b - 122.1) / 365.25 );
        d = (int) floor( 365.25 * c );
        e = (int) floor( (b - d) / 30.6001 );

        double day_dec = (int) b - d - floor( 30.6001 * e ) + f;
        if (e <= 13) {
            month = e - 1;
            year = c - 4716;
        } else {
            month = e - 13;
            year = c - 4715;
        }
        day = (int) floor( day_dec );
        hour_dec = (day_dec % 1) * 24;
        hour = (int) floor( (day_dec % 1) * 24 );
        min_dec = ((hour_dec % 1) * 60);
        min = (int) floor( (hour_dec % 1) * 60 );
        sec = (int) floor( (min_dec % 1) * 60 );

        return new int[]{year, month, day, hour, min, sec};
    }
    public static String dateToString(double time) {
        int[] t = julianToDate( unixToJulian( time + DELTA_UNIX_VERNAL_EQUINOX + DELTA_UNIX_YEAR ) );
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"};
        return format( "%02d %s %02d %02d:%02d:%02d ", t[0], months[t[1] - 1], t[2], t[3], t[4], t[5] );
    }
}
