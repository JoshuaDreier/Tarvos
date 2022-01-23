package javaCode;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;

import static javaCode.ValueSet.*;
import static javafx.scene.transform.Rotate.*;


public class Firmament extends Group {

    Firmament(double scaleFactor){
        //load Model
        Group model = ModelLoader.loadModel( "programm/src/resources/firmament/firmament.obj" );
        this.getChildren().add( model );

        //scale Model
        double scale = scaleFactor * EARTH_RADIUS * VISUALISATION_SCALE_FACTOR;
        model.getTransforms().add( uniformScale( scale ) );

        //rotate according to sidereal time
        Rotate siderealTimeRotate = new Rotate( GMST_VERNAL_EQUINOX, Y_AXIS);
        this.getTransforms().add( siderealTimeRotate );
    }
}
