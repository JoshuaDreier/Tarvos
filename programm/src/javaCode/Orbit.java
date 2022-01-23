package javaCode;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

import static javaCode.ValueSet.*;
import static javafx.geometry.Point3D.*;
import static javafx.scene.transform.Rotate.Y_AXIS;

class Orbit extends Group {

    public Rotate[] rotations;
    public Scale modifiableScale;
    public Rotate modifiableRotate;
    public Cylinder orbitalPlane;

    Orbit(){
       //load torus 3D-model
        this.getChildren().add( ModelLoader.loadModel( "programm/src/resources/orbitTrack/circularOrbit.obj" ));

        //create orbital Plane
        orbitalPlane = new Cylinder(1, 0.001, 128);
        PhongMaterial orbitalPlaneMaterial = new PhongMaterial();
        Image image = new Image("resources/selfIlluminationMaps/selfIlluminationOrbitalPlane.png");
        orbitalPlaneMaterial.setSelfIlluminationMap( image );
        orbitalPlaneMaterial.setDiffuseMap( image );

        //set orbital plane visibility
        orbitalPlane.setMaterial( orbitalPlaneMaterial );
        this.getChildren().add( orbitalPlane );

        rotations = new Rotate[]{new Rotate(0,ZERO), new Rotate(0, ZERO)};
        modifiableRotate = new Rotate(0, ZERO);
        modifiableScale = uniformScale(0 );
        this.getTransforms().addAll( modifiableRotate, modifiableScale );
    }
    public void recalculate(Point3D[] satelliteRotationAxis, double inclination, double semiMajorAxis, int simMode){
        //re-rotate
        for (int mode = 0; mode < 2; mode++)
            rotations[mode] = new Rotate(inclination,satelliteRotationAxis[mode].crossProduct( Y_AXIS ));
        this.getTransforms().set( 1, uniformScale( semiMajorAxis*VISUALISATION_SCALE_FACTOR ));
        switchStates( simMode );
    }
    public void switchStates(int simMode){
        this.getTransforms().set( 0,  rotations[simMode] );
    }
    public void switchOrbitalPlane(boolean enableOrbitalPlane){ orbitalPlane.setVisible( enableOrbitalPlane);}
}