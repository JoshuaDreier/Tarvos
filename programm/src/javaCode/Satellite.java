package javaCode;

import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.*;
import static java.lang.Math.sin;
import static javaCode.ValueSet.*;
import static javafx.geometry.Point3D.*;
import static javafx.scene.transform.Rotate.*;

public class Satellite extends Sphere{

    Point3D[] rotationAxis;
    Point3D[][] position;
    Translate modifiableTranslate;
    Point3D orbitAnchor;
    Boolean calculated;

    Satellite(){
        calculated = false;

        //Modifiable Transform
        modifiableTranslate = new Translate(0,0,0);
        this.getTransforms().add( modifiableTranslate );

        //MATERIAL
        PhongMaterial satelliteMaterial = new PhongMaterial(Color.BLUE);
        satelliteMaterial.setSelfIlluminationMap(
                new Image("resources/selfIlluminationMaps/selfIlluminationSatellite.png"));
        this.setMaterial(satelliteMaterial);
    }
    public void orbitToTime(byte simMode, double time){
        if (calculated) {
            modifiableTranslate.setX( position[simMode][(int) time].getX() );
            modifiableTranslate.setY( position[simMode][(int) time].getY() );
            modifiableTranslate.setZ( position[simMode][(int) time].getZ() );
        }
    }
    public void calculatePositions(int initialTime,
                                   double inclination,
                                   Orbit orbit,
                                   byte simMode){
        orbitAnchor = this.localToParent( ZERO );
        double semiMajorAxis = orbitAnchor.magnitude()/VISUALISATION_SCALE_FACTOR;
        rotationAxis = iterateRotationAxis( inclination, orbitAnchor );

        //for first to possible rotation-axes, calculate positions for the next PRE-CALCULATION_DAYS
        position = new Point3D[2][initialTime + SATELLITE_PRE_CALCULATION_PERIOD];
        double u = orbitalPeriod( semiMajorAxis);
        System.out.println(u/60);
        for (int dl = 0; dl < position.length; dl++) {
            for (int i = 0; i < SATELLITE_PRE_CALCULATION_PERIOD; i++) {
                position[dl][i + initialTime] = rotatePoint( orbitAnchor, rotationAxis[dl], i / ( u / 360) );
            }
        }
        orbit.recalculate( rotationAxis, inclination, semiMajorAxis, simMode );
        calculated = true;
    }
    public void updateScale(double h) { this.setRadius( 75 * (h + EARTH_RADIUS)/ EARTH_RADIUS ); }
    public void positionToLocation(double alt,
                                   double az,
                                   double h,
                                   double elevation,
                                   CoordinateLocation loc,
                                   Earth earth,
                                   double t) {
        Point3D locationPosition, earthToSurfaceVector,altRotated, azLonRotated, latRotated, p;

        locationPosition = loc.position[(int) t/ SLOW_CALC_MULTIPLIER];
        double earthRotation = earth.getRotationArray()[(int) t/ SLOW_CALC_MULTIPLIER];

        earthToSurfaceVector = locationPosition.normalize().multiply((EARTH_RADIUS + elevation) * VISUALISATION_SCALE_FACTOR);
        //Vector from origin to CoordinateLocation

        altRotated = rotatePoint( new Point3D( -h/sin(toRadians( alt ))* VISUALISATION_SCALE_FACTOR,0,0 ),
                // initial Point on the X-Axis distance to  origin equal to the distance from the observer
                Z_AXIS, alt);
        // Creates a Vector According to the Altitude-angle
        azLonRotated = rotatePoint( altRotated, Y_AXIS, az-loc.currentLongitude-earthRotation);
        // Rotates the alt-angle-rotated vector according to the Azimuth-Angle, Longitude and Earths Rotation
        latRotated = rotatePoint( azLonRotated,locationPosition.crossProduct( Y_AXIS ),90-loc.currentLatitude );
        // Rotates the Vector according to Latitude, by using the crossproduct of the coordinateLocation and Y-Axis as
        // the Rotation-axis

        p = earthToSurfaceVector.add( latRotated ); //Adds the vectors together

        p.normalize().multiply( (EARTH_RADIUS + h) * VISUALISATION_SCALE_FACTOR );
        //Set Height back to entered Height

        modifiableTranslate.setX( p.getX());
        modifiableTranslate.setY( p.getY());
        modifiableTranslate.setZ( p.getZ());
        updateScale(h);
    }

    public Point3D[] iterateRotationAxis(double inclination, Point3D initialPosition){
        Point3D ra;
        ArrayList<Object> deltaLambda = new ArrayList<>();
        for (int degree = 0; degree < 360; degree++) {
            for (int i = 0; i < RA_ITERATIONS_PD; i++) {
                ra = createRotationAxis( inclination, degree + i/ RA_ITERATIONS_PD );
                 if(distanceToNormalPlane( ra, initialPosition ) < RA_ITERATIONS_TOLERANCE){
                     deltaLambda.add( degree + i/ RA_ITERATIONS_PD );
                     break;
                 }
             }
        }
        System.out.println(deltaLambda);
        if (!deltaLambda.isEmpty()){
            return new Point3D[]{createRotationAxis( inclination, (Double) deltaLambda.get( 0 ) ),
                                 createRotationAxis( inclination, (Double) deltaLambda.get( 1 ) )};
        }
        else {
            System.out.println("RA iteration failed");
            return new Point3D[]{ZERO, ZERO};
        }
    }
    public Point3D createRotationAxis(double inclination, double deltaLambda) {
        return rotatePoint( new Point3D(
                sin( toRadians( inclination)),
                cos( toRadians( inclination)),
                0 ).multiply( -1 ), Y_AXIS, deltaLambda );
    }
    public static double orbitalPeriod(double radius) {
        return 2 * PI                            //2*pi
                * sqrt(                                 //root of
                pow( radius,3 )                    //r^3
                        /(GRAVITATIONAL_CONSTANT*         // /G * m
                        EARTH_MASS)
        );
    }
}
