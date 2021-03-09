package tasks.task_2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class JavaUdpServerTask2 {

    private final static String ID = "Task-2-UDP-Server-Java";

    public static void main(String args[]) {
        System.out.println(ID);
        DatagramSocket socket = null;
        int portNumber = 9009;

        try {
            socket = new DatagramSocket(portNumber);

            while(true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData(), StandardCharsets.UTF_8.name());
                System.out.println(ID + " received msg: " + msg);
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
