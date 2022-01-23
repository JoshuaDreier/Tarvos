package javaCode;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        try {
            //load GUI
            FXMLLoader loader = new FXMLLoader( getClass().getClassLoader().getResource( "resources/fxml/GUI_gluon.fxml" ) );
            Parent root = loader.load();
            Scene fxmlScene = new Scene( root );
            primaryStage.setScene( fxmlScene );

            //set title, icon & resizable
            primaryStage.setTitle( "Tarvos" );
            primaryStage.getIcons().add( new Image( "resources/icon/appIcon_big.png" ) );
            primaryStage.setResizable( false );

            primaryStage.show();
        }catch (Exception e){
            System.out.println("error");
            e.printStackTrace();
            System.exit( 1 );
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}

