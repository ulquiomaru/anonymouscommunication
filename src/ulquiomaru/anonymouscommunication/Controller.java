package ulquiomaru.anonymouscommunication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller{

    @FXML
    Label labelTop;

    @FXML
    private Button btnMessage;

    @FXML
    private Button btnFile;

    @FXML
    TextArea txtChat;

    @FXML
    private TextField txtInput;

    public Controller() { }

    @FXML
    private void initialize() { }

    @FXML
    private void sendMessageClicked() throws Exception {
        if (txtInput.getText().length() > 0) {
            String message = Main.isServer ? "Server: " : "Client: ";
            message += txtInput.getText();
            txtChat.appendText(message + "\n");
            txtInput.clear();
            txtInput.requestFocus();
            Main.connection.encryptMessage(message);
//            Main.connection.send(message);
        }
    }

    @FXML
    private void sendFileClicked() {
        txtChat.appendText("Send File Button Clicked!\n");
        txtInput.requestFocus();
    }
}
