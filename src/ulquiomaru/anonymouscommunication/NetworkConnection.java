package ulquiomaru.anonymouscommunication;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.function.Consumer;

public abstract class NetworkConnection {

    private ConnectionThread connThread = new ConnectionThread();
    private Consumer<Serializable> onReceiveCallback;
    private static final String MESSAGE_ALGORITHM_AES = "AES/CBC/PKCS5Padding";


    NetworkConnection(Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
        connThread.setDaemon(true);
    }

    void startConnection() {
        connThread.start();
    }

    private void send(Serializable data) throws Exception {
        connThread.out.writeObject(data);
    }

    void closeConnection() throws Exception {
        connThread.socket.close();
    }

    protected abstract boolean isServer();
    protected abstract String getIP();
    protected abstract int getPort();
    protected abstract SecretKey getAesKey();

    private class ConnectionThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;

        @Override
        public void run() {
            try (ServerSocket server = isServer() ? new ServerSocket(getPort()) : null;
                 Socket socket = isServer() ? server.accept() : new Socket(getIP(), getPort());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);

                while (true) {
                    byte[] data = (byte[]) in.readObject();
                    decryptMessage(data);
                }
            } catch (Exception e) {
                onReceiveCallback.accept("Connection closed");
                e.printStackTrace();
            }
        }
    }


    void encryptMessage(String message) throws Exception {
        Cipher cipher = Cipher.getInstance(MESSAGE_ALGORITHM_AES);
        byte[] iV = new byte[cipher.getBlockSize()];
        SecureRandom RNG = new SecureRandom();
        RNG.nextBytes(iV);
        cipher.init(Cipher.ENCRYPT_MODE, getAesKey(), new IvParameterSpec(iV));

        byte[] cipherText = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(iV);
        output.write(cipherText);
        send(output.toByteArray());
    }

    private void decryptMessage(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(MESSAGE_ALGORITHM_AES);
        byte[] iV = new byte[cipher.getBlockSize()];
        System.arraycopy(data, 0, iV, 0, iV.length);
        byte[] cipherText = new byte[data.length - iV.length];
        System.arraycopy(data, iV.length, cipherText, 0, cipherText.length);

        cipher.init(Cipher.DECRYPT_MODE, getAesKey(), new IvParameterSpec(iV));

        byte[] plainTextInBytes = cipher.doFinal(cipherText);

        String plainText = new String(plainTextInBytes, StandardCharsets.UTF_8);

        onReceiveCallback.accept(plainText);
    }
}
