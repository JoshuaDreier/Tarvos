package javaCode;

import javafx.scene.PerspectiveCamera;
import javafx.scene.control.TextField;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import static javafx.scene.transform.Rotate.*;
import static javaCode.ValueSet.*;

public class SurfaceCamera extends PerspectiveCamera {

    public Rotate latRotate;
    public Rotate lonRotate;

    public SurfaceCamera(Earth earth, CoordinateLocation location, TextField latTF, TextField lonTF) {

        this.setFieldOfView( 120 );
        this.setNearClip( 1 );
        this.setFarClip( 5E4 );

        latRotate = new Rotate(LATITUDE,X_AXIS);
        lonRotate = new Rotate(LONGITUDE, Y_AXIS);

        Translate locationTranslate = new Translate();
        locationTranslate.xProperty().bind(location.modifiableTranslate.xProperty());
        locationTranslate.yProperty().bind(location.modifiableTranslate.yProperty());
        locationTranslate.zProperty().bind(location.modifiableTranslate.zProperty());

        this.getTransforms().addAll( locationTranslate, latRotate, lonRotate);

        updateCameraRotation( earth, location.currentLatitude, location.currentLongitude );
    }
    public void updateCameraRotation(Earth earth,double latitude, double longitude){
        latRotate.setAngle( latitude );
        lonRotate.setAngle( earth.modifiableRotate.getAngle() + longitude );
    }
}
