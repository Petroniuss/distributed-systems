package tasks.task_1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class JavaUdpServerTask1 {

    private final static String ID = "Task-1-UDP-Server";

    public static void main(String args[]) {
        System.out.println(ID);
        DatagramSocket socket = null;
        int portNumber = 9008;

        try {
            socket = new DatagramSocket(portNumber);

            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                System.out.println(ID + " received msg: " + msg);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String response = "Hi there from the server!";
                byte[] sendBuffer = response.getBytes();
                DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                socket.send(packet);
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
