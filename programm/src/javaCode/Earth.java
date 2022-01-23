package javaCode;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;

import static javaCode.ValueSet.*;

public class Earth extends Group {

    public Rotate modifiableRotate;
    public double[] preCalcRotation;
    public double degreesPerSecond;

    Earth(){
        //load 3D-model
        Group model = ModelLoader.loadModel( "programm/src/resources/earth2/earth.obj");
        this.getChildren().add( model );

        //scale model
        model.getTransforms().add( uniformScale( EARTH_RADIUS * VISUALISATION_SCALE_FACTOR ) );

        //pre-calculation
        degreesPerSecond = 360/ SIDEREAL_DAY_LENGTH;
        preCalcRotation = new double[SLOW_CALC_PRE_CALCULATION_PERIOD];
        for (int i = 0; i < preCalcRotation.length; i++)
            preCalcRotation[i] = (i * SLOW_CALC_MULTIPLIER * degreesPerSecond );

        //add modifiable rotation
        modifiableRotate = new Rotate( 0, Rotate.Y_AXIS.multiply( -1 ));
        this.getTransforms().add( modifiableRotate );
    }
    public void rotateToTime(double time){modifiableRotate.setAngle(preCalcRotation[(int) time/SLOW_CALC_MULTIPLIER]);}
    public double[] getRotationArray(){ return preCalcRotation; }
}
