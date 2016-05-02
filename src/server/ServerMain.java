
package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import dataPacket.DataPacket;
import dataPacket.Serializer;

class ServerMain
{
    private static DatagramSocket serverSocket;
    private static BufferServer buffer =new BufferServer();

	public static void main(String args[]) throws Exception
       {
          serverSocket = new DatagramSocket(9876);
             byte[] receiveData = new byte[200];
             //byte[] sendData = new byte[packetSize];
             while(true)
                {
                   DatagramPacket receivePacket = 
                   new DatagramPacket(receiveData, receiveData.length);
                   serverSocket.receive(receivePacket);
                   
                   DataPacket packet = (DataPacket)Serializer.toObject(receivePacket.getData());
                   buffer.addReceivedPacket(packet);
                   buffer.printData();
                   
                   //String sentence = new String(new String(packet.data) + "; SEQ: "+ Integer.toString(packet.seq));
                   //System.out.println("RECEIVED: " + sentence);
                   
                   
                   //send ACK
                   InetAddress IPAddress = receivePacket.getAddress();
                   int port = receivePacket.getPort();
                   
                   
                   //sendData = ack.getBytes();
                   DataPacket ackPacket = new DataPacket(null, 0);
                  
                   ackPacket.ack = packet.seq;
                   byte[] ackBytes = Serializer.toBytes(ackPacket);
                   
                   DatagramPacket ACK = new DatagramPacket(ackBytes, ackBytes.length, IPAddress, port);
                   serverSocket.send(ACK);
                }
       }
}