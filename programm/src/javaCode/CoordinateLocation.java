package javaCode;

import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import javafx.scene.transform.Translate;

import static java.lang.Math.*;
import static javaCode.ValueSet.*;
import static javafx.scene.transform.Rotate.*;

public class CoordinateLocation extends Sphere {

    Translate modifiableTranslate;
    Point3D[] position;
    double currentLatitude, currentLongitude, currentElevation;

    public CoordinateLocation(Earth earth) {
        //set radius
        super( 50 );

        //set material
        PhongMaterial material = new PhongMaterial( Color.ORANGERED);
        material.setSelfIlluminationMap(new Image("resources/selfIlluminationMaps/selfIlluminationLocation.png"));
        this.setMaterial( material );

        currentLatitude = LATITUDE;
        currentLongitude = LONGITUDE;
        currentElevation = ELEVATION;

        calculatePositions( earth, currentLatitude, currentLongitude, currentElevation );

        modifiableTranslate = new Translate(0,0,0);
        this.getTransforms().add( modifiableTranslate );
    }
    public void calculatePositions(Earth earth, double inputLatitude, double inputLongitude, double elevation){
        //calculates coordinate locations for given latitude, longitude and elevation
        currentLatitude = inputLatitude;
        currentLongitude = inputLongitude;

        Point3D initialPosition = new Point3D(
                cos( toRadians( inputLatitude) ) * cos( toRadians( inputLongitude )),
                -sin( toRadians( inputLatitude) ),
                cos( toRadians( inputLatitude) ) * sin( toRadians( inputLongitude )))
                .multiply( (EARTH_RADIUS + elevation) * VISUALISATION_SCALE_FACTOR );
        double[] er = earth.getRotationArray();

        position = new Point3D[SLOW_CALC_PRE_CALCULATION_PERIOD];
        for (int i = 0; i < position.length; i++)                       //rotates location according to earth rotation
            position[i] = rotatePoint( initialPosition, Y_AXIS.multiply( -1 ), er[i] );
    }
    public void rotateToTime(double time) {
        modifiableTranslate.setX( position[(int) time/SLOW_CALC_MULTIPLIER].getX() );
        modifiableTranslate.setY( position[(int) time/SLOW_CALC_MULTIPLIER].getY() );
        modifiableTranslate.setZ( position[(int) time/SLOW_CALC_MULTIPLIER].getZ() );
    }
}
