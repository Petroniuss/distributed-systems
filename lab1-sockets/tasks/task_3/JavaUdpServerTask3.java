package tasks.task_3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class JavaUdpServerTask3 {

    private final static String ID = "Task-3-UDP-Server-Java";

    public static void main(String args[]) {
        System.out.println(ID);
        DatagramSocket socket = null;
        int portNumber = 9010;

        try {
            socket = new DatagramSocket(portNumber);

            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                int number = ByteBuffer.wrap(receiveBuffer)
                                .order(ByteOrder.LITTLE_ENDIAN)
                                .getInt(); // reads first 4 bytes
                System.out.println(ID + " received value: " + String.valueOf(number));

                byte[] buffer = ByteBuffer.allocate(4)
                                        .order(ByteOrder.LITTLE_ENDIAN)
                                        .putInt(number + 1)
                                        .array();
                
                InetAddress cliAddr = receivePacket.getAddress();
                int cliPort = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, cliAddr, cliPort);
                socket.send((sendPacket));
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
