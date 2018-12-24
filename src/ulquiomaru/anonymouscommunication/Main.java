package ulquiomaru.anonymouscommunication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.Serializable;

public class Main extends Application {

    static Controller controller;
    static boolean isServer = false;

    private static TextArea txtChat;
    static NetworkConnection connection = isServer ? createServer() : createClient();

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("application.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        primaryStage.setTitle("Anonymous Communication - " + (isServer ? "Server" : "Client"));
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        txtChat = controller.txtChat;
    }

    @Override
    public void init() throws Exception {
        connection.startConnection();
    }

    @Override
    public void stop() throws Exception {
        connection.closeConnection();
    }

    private static Server createServer() {
        return new Server(55555, data -> Platform.runLater(() -> txtChat.appendText(data.toString() + "\n")));
    }

    private static Client createClient() {
        return new Client("127.0.0.1", 55555, data -> Platform.runLater(() -> txtChat.appendText(data.toString() + "\n")));
    }

    private static void readEncryptedMessage(String data) {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
