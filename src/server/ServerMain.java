
package server;

import java.io.*;
import java.net.*;

import dataPacket.DataPacket;
import dataPacket.Serializer;

class ServerMain
{
    private static DatagramSocket serverSocket;

	public static void main(String args[]) throws Exception
       {
          serverSocket = new DatagramSocket(9876);
             byte[] receiveData = new byte[200];
             byte[] sendData = new byte[200];
             while(true)
                {
                   DatagramPacket receivePacket = 
                   new DatagramPacket(receiveData, receiveData.length);
                   serverSocket.receive(receivePacket);
                   
                   DataPacket packet = (DataPacket)Serializer.toObject(receivePacket.getData());
                   
                   String sentence = new String(new String(packet.data) + "; SEQ: "+ Integer.toString(packet.seq));
                   System.out.println("RECEIVED: " + sentence);
                   
                   
                   //send ACK
                   InetAddress IPAddress = receivePacket.getAddress();
                   int port = receivePacket.getPort();
                   
                   
                   
                   String capitalizedSentence = sentence.toUpperCase();
                   sendData = capitalizedSentence.getBytes();
                   
                   DatagramPacket ACK = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                   serverSocket.send(ACK);
                }
       }
}