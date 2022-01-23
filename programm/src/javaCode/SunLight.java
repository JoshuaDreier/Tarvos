package javaCode;

import javafx.geometry.Point3D;
import javafx.scene.PointLight;

import static java.lang.StrictMath.*;
import static javaCode.ValueSet.*;

public class SunLight extends PointLight {

    public Point3D p;
    public Point3D p0;
    public Point3D rotationAxis;

    SunLight(){
        p0 = new Point3D( SUN_DISTANCE*VISUALISATION_SCALE_FACTOR,0, 0 );
        rotationAxis = new Point3D(
                0,
                cos( toRadians( EARTH_AXIAL_TILT) ),
                sin( toRadians( EARTH_AXIAL_TILT) ))
                .multiply( -1 );
    }
    public Point3D sunPosition(double programTime){
        double angle = (programTime)/(SIDEREAL_YEAR_LENGTH)*360;
        return rotatePoint(p0,rotationAxis, angle);
    }
    public void rotateToTime(double time){
        p = sunPosition( time );
        this.setTranslateX( p.getX() );
        this.setTranslateY( p.getY() );
        this.setTranslateZ( p.getZ() );
    }

}
