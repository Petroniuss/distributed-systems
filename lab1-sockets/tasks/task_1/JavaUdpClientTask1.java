package tasks.task_1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class JavaUdpClientTask1 {

    private final static String ID = "Task-1-UDP-Client";

    public static void main(String args[]) throws Exception {
        System.out.println(ID);
        DatagramSocket socket = null;
        int portNumber = 9008;

        try {
            socket = new DatagramSocket();
            
            InetAddress address = InetAddress.getByName("localhost");
            String sendMessage = "Ping-Java-UDP";
            byte[] sendBuffer = sendMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
            socket.send(sendPacket);
            System.out.println(ID + " sent message: " + sendMessage);

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
