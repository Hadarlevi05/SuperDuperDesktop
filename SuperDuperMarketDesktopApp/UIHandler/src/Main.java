import Controllers.MainController;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("Resources/MainScreen.fxml");
        fxmlLoader.setLocation(url);
        BorderPane head = fxmlLoader.load(url.openStream());

        MainController mainController = fxmlLoader.getController();

        Scene scene = new Scene(head, 700, 500);
        scene.getStylesheets().add(getClass().getResource("/Css/Style1.css").toExternalForm());
        primaryStage.setScene(scene);
        mainController.setPrimaryStage(primaryStage);
        mainController.setScene(scene);
        primaryStage.show();
        mainController.initialize(primaryStage);
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->{
            mainController.initialize(primaryStage);
        };


        primaryStage.widthProperty().addListener(stageSizeListener);
        primaryStage.heightProperty().addListener(stageSizeListener);


        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //primaryStage.setTitle("Hello World");
        //primaryStage.setScene(new Scene(root, 300, 275));
        //primaryStage.show();
    }

}
