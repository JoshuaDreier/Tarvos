package javaCode;

import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class testController {

    public SubScene mainScene;
    public Slider speedSlider;
    public Slider scrollSlider;
    public Slider timeSlider;
    public Text timeDisplay;
    public TextField timeScaleFactorTextField;
    public TextField altitudeTextField;
    public TextField azimuthTextField;
    public TextField heightTextField;
    public TextField inclinationTextField;
    public TextField longitudeTextField;
    public TextField latitudeTextField;
    public TextField elevationTextField;
    public Button oneXButton;
    public Button toCoordsButton;
    public Button timeResetButton;
    public Button switchCameraButton;
    public Button pauseButton;
    public Button satToLocButton;
    public Button recalculateButton;
    public Button timeToObservationButton;
    public Button switchCalcButton;

    public void initialize() {

        System.out.println("yay");
        mainScene.setVisible( true );
    }
    public void speedToOne() {

    }
    public void satToLoc() {
    }
    public void pauseAndPlay() {
    }
    public void updateTimeScaleFactorTextField() {
    }
    public void resetTime(){}
    public void timeToObservation(){}
    public void switchCamera() {

    }
    public void switchCalculation(){ }
    public void recalculate() {
    }
    public void toCoords(){

    }
}