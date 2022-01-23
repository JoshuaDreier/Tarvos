package javaCode;

import javafx.scene.Group;

class Axes extends Group {

    Axes(double size){

       //3D-model
        this.getChildren().add( ModelLoader.loadModel( "programm/src/resources/axis/axes.obj" ) );

        //Scale to visual Size
        this.getTransforms().add( ValueSet.uniformScale(size) );

        }
}