package ulquiomaru.anonymouscommunication;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.util.function.Consumer;

public class Server extends NetworkConnection {

    private int port;
    private SecretKey aesKey;

    Server(int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.port = port;
        this.aesKey = createKey();
    }

    @Override
    protected boolean isServer() {
        return true;
    }

    @Override
    protected String getIP() {
        return null;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    protected SecretKey getAesKey() {
        return aesKey;
    }

    private SecretKey createKey() {
        try {
            SecretKey aesKey = KeyGenerator.getInstance("AES").generateKey();
            ObjectOutputStream oOS = new ObjectOutputStream(new FileOutputStream(new File("aes.key")));
            oOS.writeObject(aesKey);
            oOS.close();
            return aesKey;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
