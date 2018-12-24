package ulquiomaru.anonymouscommunication;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.function.Consumer;

public class Client extends NetworkConnection {

    private String ip;
    private int port;
    private SecretKey aesKey;

    Client(String ip, int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.ip = ip;
        this.port = port;
        this.aesKey = readKey();
    }

    @Override
    protected boolean isServer() {
        return false;
    }

    @Override
    protected String getIP() {
        return ip;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    protected SecretKey getAesKey() {
        return aesKey;
    }

    private SecretKey readKey() {
        try {
            ObjectInputStream oIS = new ObjectInputStream(new FileInputStream(new File("aes.key")));
            SecretKey aesKey = (SecretKey) oIS.readObject();
            oIS.close();
            return aesKey;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
