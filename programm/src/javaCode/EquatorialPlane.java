package javaCode;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import static javaCode.ValueSet.*;

public class EquatorialPlane extends Cylinder {

    EquatorialPlane(double size){
        //set dimensions
        super( VISUALISATION_SCALE_FACTOR * EARTH_RADIUS * size,1,32);

        //set material
        PhongMaterial material = new PhongMaterial();
        Image image = new Image( "resources/selfIlluminationMaps/equatorialPlaneScale.png");
        material.setSelfIlluminationMap( image );
        material.setDiffuseMap( image );
        this.setMaterial( material );
    }
}
