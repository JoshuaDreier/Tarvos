package javaCode;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import static javaCode.ValueSet.*;
import static javafx.scene.transform.Rotate.*;

public class Ecliptic extends Cylinder {

    Ecliptic(SunLight sun, double size){
        //set dimensions
        super( VISUALISATION_SCALE_FACTOR * EARTH_RADIUS * size,1,32 );

        //set Material
        PhongMaterial material = new PhongMaterial();
        Image image = new Image( "resources/selfIlluminationMaps/eclipticScale.png");
        material.setSelfIlluminationMap( image );
        material.setDiffuseMap( image );
        this.setMaterial( material );

        //rotate to actual ecliptic
        Rotate eclipticRotate = new Rotate(EARTH_AXIAL_TILT, sun.rotationAxis.crossProduct( Y_AXIS ));
        this.getTransforms().add( eclipticRotate );
    }
}
