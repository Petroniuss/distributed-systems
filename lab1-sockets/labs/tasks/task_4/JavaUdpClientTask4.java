package tasks.task_4;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class JavaUdpClientTask4 {

    private final static String ID = "Task-4-UDP-Client";

    public static void main(String args[]) throws Exception {
        System.out.println(ID);
        DatagramSocket socket = null;
        int portNumber = 9011;
        final var utf8 = StandardCharsets.UTF_8.name();

        try {
            socket = new DatagramSocket();
            
            byte[] header = ByteBuffer.allocate(1)
                                      .order(ByteOrder.BIG_ENDIAN)
                                      .put((byte) 0)
                                      .array();

            InetAddress address = InetAddress.getByName("localhost");
            String sendMessage = "Java Ping!";
            byte[] sendBuffer = sendMessage.getBytes(utf8);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(header);
            outputStream.write(sendBuffer);

            byte[] buffer = outputStream.toByteArray();

            DatagramPacket sendPacket = new DatagramPacket(buffer, sendBuffer.length, address, portNumber);
            socket.send(sendPacket);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            String receviedMsg = new String(receiveBuffer);
            System.out.println(ID + " recevied message: " + receviedMsg);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
