package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import dataPacket.DataPacket;
import dataPacket.Serializer;

class ClientMain
{

	
    public static void main(String args[]) throws Exception
    {
       boolean transmissionEnded = false;
       BufferControlClient bufferControl = new BufferControlClient();
       BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
       DatagramSocket clientSocket = new DatagramSocket();
       InetAddress IPAddress = InetAddress.getByName("localhost");
       
       byte[] sendData = new byte[bufferControl.packetSize];
       byte[] receiveData = new byte[bufferControl.packetSize];
       String sentence = inFromUser.readLine();
       
       int base = 0;
       int waitingForACK = 0;
       
       bufferControl.addData(sentence);
       while (!transmissionEnded){
       //Loop runs until all packets sent
       while (bufferControl.seq < bufferControl.getBuffer().getPacketList().size() && bufferControl.seq < base+bufferControl.windowFrameSize){
       
       bufferControl.sendDataOnSocket(clientSocket);
    
      
       
       }
       
       DatagramPacket receivePacket = 
        new DatagramPacket(receiveData, receiveData.length);
       
       DataPacket ACKPacket = (DataPacket) Serializer.toObject(receivePacket.getData());
       bufferControl.
       
       
       clientSocket.receive(receivePacket);
       String modifiedSentence = new String(receivePacket.getData());
       System.out.println("FROM SERVER:" + modifiedSentence);
       
       
       transmissionEnded = true;
       
       }
       clientSocket.close();
    }
}