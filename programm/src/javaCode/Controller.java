package javaCode;

import javafx.animation.AnimationTimer;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.StringConverter;

import static java.lang.Double.parseDouble;
import static java.lang.Math.*;
import static java.lang.String.*;
import static javaCode.ValueSet.*;

public class Controller {

    //declare FXML objects
    public SubScene mainScene;
    public ImageView imageIconContainer;
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

    //declare optional program-options buttons
    public Button axesButton;
    public Button equatorialPlaneButton;
    public Button eclipticButton;
    public Button orbitTrackButton;
    public Button orbitalPlaneButton;
    public Button firmamentButton;
    public Button writeToFileButton;

    //declare Non-FXML objects
    Earth earth;
    CoordinateLocation location;
    Satellite satellite;
    Orbit orbit;
    IsVisibleCalculator isVisibleCalculator;
    Camera flyingCamera;
    SurfaceCamera surfaceCamera;
    SunLight sun;
    Axes axes;
    EquatorialPlane equatorialPlane;
    Ecliptic ecliptic;
    Firmament firmament;
    AmbientLight ambientLight;

    //Global Variables
    public double programTime;
    public double observationTime;
    public byte simMode = 0;

    //Standard Values
    public boolean enableAxes = false;
    public boolean enableEquatorialPlane = false;
    public boolean enableEcliptic = false;
    public boolean enableOrbitTrack = true;
    public boolean enableOrbitalPlane = false;
    public boolean enableFirmament = false;
    public boolean enableWriteToFile = false;

    public void initialize() {
        //initialize calculated objects
        earth       = new Earth();
        location    = new CoordinateLocation( earth );
        satellite   = new Satellite();
        sun         = new SunLight();

        //initial GUI objects
        ambientLight        = new AmbientLight( Color.grayRgb( BACKGROUND_LIGHT_INTENSITY ) );
        flyingCamera        = new PerspectiveCamera( true );
        surfaceCamera       = new SurfaceCamera( earth, location, latitudeTextField, longitudeTextField);
        isVisibleCalculator = new IsVisibleCalculator( earth, location, satellite, sun );
            //flying Camera
            Translate flyingCameraPosition  = new Translate();
            flyingCameraPosition.zProperty().set( EARTH_RADIUS * VISUALISATION_SCALE_FACTOR * - 5 );
            flyingCamera.getTransforms().add( flyingCameraPosition );
            flyingCamera.setNearClip( 10 );
            flyingCamera.setFarClip( 1E8 );
            //surface Camera
            Rotate surfaceCameraRotate      = new Rotate( 0, Rotate.Y_AXIS );
            surfaceCamera.getTransforms().add( surfaceCameraRotate );

        //initiate optional program-settings
        axes            = new Axes( 100 );
        ecliptic        = new Ecliptic(sun, PLANE_SIZE);
        equatorialPlane = new EquatorialPlane( PLANE_SIZE );
        firmament       = new Firmament( 1000 );
        orbit           = new Orbit();

        //enable/disable optional program-settings
        axes            .setVisible( enableAxes );
        ecliptic        .setVisible( enableEcliptic );
        equatorialPlane .setVisible( enableEquatorialPlane );
        firmament       .setVisible( enableFirmament);
        orbit           .setVisible( enableOrbitTrack );
        orbit           .switchOrbitalPlane( enableOrbitalPlane );

        //add objects to visible group
        Group visibleGroup = new Group();
        visibleGroup.getChildren().addAll(
                earth, location, orbit, satellite, sun, ambientLight, firmament,
                axes, equatorialPlane, ecliptic, surfaceCamera
        );
        mainScene.setRoot( visibleGroup );
        mainScene.setCamera( flyingCamera );
        mainScene.setVisible( true );

        //initiate mouse control (panning)
        MouseController mouseController = new MouseController();
        mouseController.initMouseControl( visibleGroup, scrollSlider, mainScene, 0 );

        //start animation
        ProgramTimer timer = new ProgramTimer();
        timer.start();

        pauseAndPlay();

        //GUI-CONTROLS:

        //Time Slider
        timeSlider.setMax( SLOW_CALC_PRE_CALCULATION_PERIOD * SLOW_CALC_MULTIPLIER);
        timeSlider.valueProperty().addListener( (observableValue, oldValue, newValue) -> { //Whenever Slider is used
            programTime = newValue.doubleValue();                                          //update programTime
            updateNodesToTime( programTime );
        } );

        //Speed slider
        speedSlider.setMax( log( 360_000 ) / log( 60 ) );         // Max=log_60(360'000) -> Max to actual 360'000
        speedSlider.valueProperty().addListener( (observableValue, oldValue, newValue) -> { //Whenever Slider is used
            timeScaleFactor = pow( 60, newValue.doubleValue() );                            //timeScaleFactor
            timeScaleFactorTextField.setText( Integer.toString( (int) timeScaleFactor ) );  //is updated
        } );
        speedSlider.setLabelFormatter( new StringConverter<>() { //Change slider-scale to logarithmic
            @Override
            public String toString(Double d) {
                return valueOf( (int) (pow( 60, d )) );
            }
            @Override
            public Double fromString(String s) {
                return null;
            }
        } );

        //timeScaleFactor Text Field
        timeScaleFactorTextField.textProperty().addListener( (observable, oldValue, newValue) -> {
            if (! newValue.matches( "\\d*" )) {                         // force the field to be numeric only
                timeScaleFactorTextField.setText( newValue.replaceAll( "[^\\d]", "" ) );
            }
        } );

        //Scroll Slider
        scrollSlider.setLabelFormatter( new StringConverter<>() {         //Slider-Scale to log with base 2 and
            // 2^0.5 through 2^28
            @Override
            public String toString(Double d) {
                return valueOf( (int) (pow( 2, d )) );
            }
            @Override
            public Double fromString(String s) {
                return null;
            }
        } );
        scrollSlider.valueProperty().addListener( (observableValue, oldValue, newValue) -> { //Whenever Slider is used
            flyingCameraPosition.zProperty().set( - 1 * EARTH_RADIUS * VISUALISATION_SCALE_FACTOR
                    * pow( 2, newValue.doubleValue() ) );                     // flyingCameraPosition is recalculated
            surfaceCameraRotate.setAngle( 45 * newValue.doubleValue() );      // surfaceCamera is rotated
        } );

        //set icon
        imageIconContainer.setImage( new Image( "resources/icon/appIcon_small.png" ) );

        //fill default values
        latitudeTextField   .setText( valueOf( LATITUDE ) );
        longitudeTextField  .setText( valueOf( LONGITUDE ) );
        elevationTextField  .setText( valueOf( ELEVATION ) );
        altitudeTextField   .setText( "90" );
        azimuthTextField    .setText( "0" );
        heightTextField     .setText( valueOf( STANDARD_ORBIT_HEIGHT ) );
        inclinationTextField.setText( valueOf( STANDARD_INCLINATION ) );
        recalculateButton   .setDisable( true );
    }
    public double FRAME_LENGTH = 1E9 / FRAMES_PER_SECOND;
    public double timeScaleFactor = 1;
    public double timeScaleFactorS = 1;
    public boolean timeRunning = true;

    class ProgramTimer extends AnimationTimer {

        long last = System.nanoTime();

        { resetTime(); }

        @Override
        public void handle(long now) {   //now is in Nanoseconds

            if (abs( now - last ) > FRAME_LENGTH) {

                programTime += (now - last) / 1E9 * timeScaleFactor;
                timeSlider.setValue( programTime );

                //update nodes
                if (timeRunning) updateNodesToTime( programTime );

                //update time display
                timeDisplay.setText( dateToString( programTime ) );

                //check visibility
                if (timeRunning)
                    System.out.print(
                            isVisibleCalculator.isVisibleNow(programTime, simMode, timeDisplay.getText())
                    );

                last = now;
            }
        }
    }
    public void updateNodesToTime(double time) {
        try {
            earth.rotateToTime( time );
            location.rotateToTime( time );
            satellite.orbitToTime( simMode, time );
            sun.rotateToTime( time );
        } catch (Exception e) {
            System.out.println( "calcError" );
            if (programTime > timeSlider.getMax()) {
                programTime -= 86400; //Sets programTime back 1 day back;
                pauseAndPlay();         // and pauses
                System.out.println( "calculation has reached it's end" );
            }
        }
    }

    //GUI-methods
    public void pauseAndPlay() {
        if (timeRunning) {
            timeScaleFactorS = timeScaleFactor;
            timeScaleFactor = 0;
        } else {
            timeScaleFactor = timeScaleFactorS;
        }
        timeScaleFactorTextField.disableProperty().set( timeRunning );
        speedSlider             .disableProperty().set( timeRunning );
        timeSlider              .disableProperty().set( ! timeRunning );
        satToLocButton          .disableProperty().set( ! timeRunning );
        recalculateButton       .disableProperty().set( ! timeRunning );

        timeRunning ^= true;
    }
    public void recalculate() {
        simMode = 0;
        observationTime = programTime;
        timeToObservationButton.disableProperty().set( false );
        timeSlider.setMin( programTime );
        timeSlider.setMax( programTime + SATELLITE_PRE_CALCULATION_PERIOD );
        satellite.calculatePositions(
                (int) programTime,
                parseDouble( inclinationTextField.getText() ),
                orbit,
                simMode
        );
        satellite.updateScale(parseDouble( heightTextField.getText() ));
        if (enableWriteToFile)
            isVisibleCalculator.isVisibleInPreCalculationPeriod( observationTime );
    }
    public void resetTime(){programTime = System.currentTimeMillis() * 1E-3 - DELTA_UNIX_YEAR - DELTA_UNIX_VERNAL_EQUINOX;}
    public void satToLoc() {
        try {
            satellite.positionToLocation(
                    parseDouble( altitudeTextField.getText() ),
                    parseDouble( azimuthTextField.getText() ),
                    parseDouble( heightTextField.getText() ),
                    parseDouble( elevationTextField.getText() ),
                    location,
                    earth,
                    programTime );
            recalculateButton.setDisable( false );
        }catch(Exception e){
            System.out.println("Enter Valid Values (ALT: 0 - 90, AZ: 0 - 360, height > 0," +
                    " latitude: -90 - 90, longitude: 0 - 360, elevation > 0)");
        }
    }
    public void speedToOne() { timeScaleFactor = 1; speedSlider.setValue( 0 ); }
    public void switchCalculation(){ simMode ^= 1; updateNodesToTime( programTime); orbit.switchStates( simMode );}
    public void switchCamera() {
        if (flyingCamera.equals( mainScene.getCamera() )) {
            mainScene.setCamera( surfaceCamera );
        } else if (surfaceCamera.equals( mainScene.getCamera() )) {
            mainScene.setCamera( flyingCamera );
        } else {
            System.out.println( "Camera Error" );
        }
    }
    public void timeToObservation(){programTime = observationTime;}
    public void toCoords(){
        double lat = parseDouble( latitudeTextField.getText());
        double lon = parseDouble( longitudeTextField.getText());
        double elv = parseDouble( elevationTextField.getText());
        location.calculatePositions(earth, lat, lon, elv);
        surfaceCamera.updateCameraRotation(earth, lat, lon);
        updateNodesToTime( programTime );
    }
    public void updateTimeScaleFactorTextField(){
        double value = log( parseDouble( timeScaleFactorTextField.getText() ) ) / log( 60 );
        speedSlider.setValue(
                Double.min( value, speedSlider.getMax() ) //set SliderValue to log_60 of entered Value
        );
    }

    //optional program-option functions
    public void switchAxes(){
        enableAxes ^= true;
        axes.setVisible( enableAxes );
        axesButton.setText(switchEnableDisable(axesButton.getText(),enableAxes));
    }
    public void switchEquatorialPlane(){
        enableEquatorialPlane ^= true;
        equatorialPlane.setVisible( enableEquatorialPlane );
        equatorialPlaneButton.setText(switchEnableDisable(equatorialPlaneButton.getText(),enableEquatorialPlane));
    }
    public void switchEcliptic(){
        enableEcliptic ^= true;
        ecliptic.setVisible( enableEcliptic );
        eclipticButton.setText(switchEnableDisable(eclipticButton.getText(),enableEcliptic));
    }
    public void switchFirmament(){
        enableFirmament ^= true;
        firmament.setVisible( enableFirmament );
        firmamentButton.setText( switchEnableDisable( firmamentButton.getText(), enableFirmament ) );
    }
    public void switchOrbitTrack(){
        enableOrbitTrack ^= true;
        orbit.setVisible( enableOrbitTrack );
        orbitTrackButton.setText(switchEnableDisable(orbitTrackButton.getText(),enableOrbitTrack));
    };
    public void switchOrbitalPlane(){
        enableOrbitalPlane ^= true;
        orbit.switchOrbitalPlane( enableOrbitalPlane );
        orbitalPlaneButton.setText(switchEnableDisable(orbitalPlaneButton.getText(),enableOrbitalPlane));
    }
    public void switchWriteToFile(){
        enableWriteToFile ^= true;
        writeToFileButton.setText( switchEnableDisable( writeToFileButton.getText(), enableWriteToFile ) );
    }
    public String switchEnableDisable(String s, Boolean mode){
        if (!mode) {
            return s.replace( "Disable", "Enable" );
        } else {
            return s.replace( "Enable", "Disable" );
        }
    }
}