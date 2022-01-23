package javaCode;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.control.Slider;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;

public class MouseController {
    Rotate xRotate;
    Rotate yRotate;
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);
    private double sensitivity = 0.25;

    public void initMouseControl(Group group, Slider scrollSlider, SubScene scene, double tilt){

        group.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS), //connects rotation with angleX und Y
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );
        xRotate.angleProperty().bind( angleX );
        yRotate.angleProperty().bind( angleY );

            scene.setOnMousePressed( event -> {
                anchorX = event.getSceneX();        // anchorX, anchorY = mouse position when pressing
                anchorY = event.getSceneY();
                anchorAngleX = angleX.get();        // AnchorAngleX and Y = rotation when pressing
                anchorAngleY = angleY.get();
            } );

            scene.setOnMouseDragged( event -> {
                angleX.set( anchorAngleX - sensitivity * (anchorY - event.getSceneY()/*y-distance when pressing and dragging */) - tilt );
                //subtracts the y-distance from he x-angle
                angleY.set( anchorAngleY + sensitivity * (anchorX - event.getSceneX()) );
                //adds the x-distance to the y-angle
            } );

        scene.addEventHandler( ScrollEvent.SCROLL, event -> {
            double scrollDelta = event.getDeltaY();
            scrollSlider.setValue( scrollSlider.getValue()+
                    scrollDelta * 0.006
                    );
        } );
    }
}
