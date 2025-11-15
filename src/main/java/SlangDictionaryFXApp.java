import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SlangDictionaryFXApp extends Application {

    private MainController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Parent root = loader.load();

        controller = loader.getController();

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Slang Dictionary");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            if(controller != null) {
                controller.cleanup();
            }
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
