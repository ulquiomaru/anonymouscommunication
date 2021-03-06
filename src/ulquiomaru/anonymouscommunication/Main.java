package ulquiomaru.anonymouscommunication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Main extends Application {

    static boolean isServer;
    private static TextArea txtChat;
    static NetworkConnection connection;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("application.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();

        primaryStage.setTitle("Anonymous Communication - " + (isServer ? "Server" : "Client"));
        controller.labelTop.setText((isServer ? "Server" : "Client"));
        controller.labelTop.setAlignment(Pos.CENTER);
        txtChat = controller.txtChat;
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

    }

    @Override
    public void init() {
        isServer = getParameters().getRaw().get(0).toLowerCase().equals("server");
        connection = isServer ? createServer(55555) : createClient(55555);
        connection.startConnection();
    }

    @Override
    public void stop() throws Exception {
        connection.closeConnection();
    }

    private static Server createServer(int port) {
        return new Server(port, data -> Platform.runLater(() -> txtChat.appendText(data.toString() + "\n")));
    }

    private static Client createClient(int port) {
        return new Client("127.0.0.1", port, data -> Platform.runLater(() -> txtChat.appendText(data.toString() + "\n")));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
