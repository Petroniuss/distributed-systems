package tasks.task_4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JavaUdpServerTask4 {

    private final static String ID = "Task-4-UDP-Server-Java";

    // Client message: [header-byte]-[actual-message]
    // If first byte equals to 0 then client is python
    // if first byte equals to 1 then client is java
    // BIG_ENDIAN - UTF_8

    public static void main(String args[]) {
        final var utf8 = Charset.forName(StandardCharsets.UTF_8.name());

        System.out.println(ID);
        DatagramSocket socket = null;
        int portNumber = 9011;

        try {
            socket = new DatagramSocket(portNumber);

            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                // first byte
                byte clientHeader = ByteBuffer.wrap(receiveBuffer)
                                .order(ByteOrder.BIG_ENDIAN)
                                .get(0);

                // omit first byte
                byte[] messageBuffer = Arrays.copyOfRange(receiveBuffer, 1, receiveBuffer.length);
                String message = new String(messageBuffer, utf8);

                String response = switch (clientHeader) {
                    case 0 ->  "Hi Java client! -> " + message;
                    case 1 ->  "Hello Python client! -> " + message;
                    default -> "Unknown client!";
                };

                byte[] buffer = response.getBytes(utf8);
                
                InetAddress cliAddr = receivePacket.getAddress();
                int cliPort = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, cliAddr, cliPort);
                socket.send(sendPacket);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
