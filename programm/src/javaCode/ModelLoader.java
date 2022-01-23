package javaCode;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Group;
import javafx.scene.shape.MeshView;

import java.nio.file.Paths;

public class ModelLoader {
    static Group loadModel(String relativePath){
        Group modelRoot = new Group();

        String url = Paths.get(relativePath).toAbsolutePath().toString().replace("/","\\\\");
        ObjModelImporter importer = new ObjModelImporter();
        try {
            importer.read(url);

            for (MeshView view : importer.getImport()) {
                modelRoot.getChildren().add( view );
            }
        }catch (Exception e){
            System.out.println("could not load " + url);
        }
        return modelRoot;
    }
}
