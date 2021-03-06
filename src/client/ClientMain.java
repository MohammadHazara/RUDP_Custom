package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import dataPacket.DataPacket;
import dataPacket.Serializer;

class ClientMain {
	private static boolean blockACK = true;

	public static void main(String args[]) throws Exception {
		//boolean transmissionEnded = false;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		////InetAddress IPAddress = InetAddress.getByName("10.16.235.46");
		
		BufferControlClient bufferControl = new BufferControlClient(clientSocket);

		////byte[] sendData = new byte[bufferControl.packetSize];
		//byte[] receiveData = new byte[200];
		String sentence = inFromUser.readLine();

		bufferControl.addData(sentence);
		bufferControl.send();
		/*
		while (!transmissionEnded) {
			// Loop runs until all packets sent
			while (bufferControl.seq < bufferControl.getBuffer().getPacketList().size()
					&& bufferControl.seq < bufferControl.getBufferBase() + bufferControl.getWindowFrame()
						&& bufferControl.seq >= bufferControl.getBufferBase()) {

				bufferControl.sendData();
			}

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			//receive ACK
			clientSocket.receive(receivePacket);
			DataPacket ACKPacket = (DataPacket) Serializer.toObject(receivePacket.getData());
			if(ACKPacket.ack==3 && blockACK){
			//BLOCK ACK ON SEQ3 ONE TIME - TESTING PURPOSE ONLY
				blockACK = false;
			}else{
				bufferControl.setAcked(ACKPacket.ack-bufferControl.getBufferBase()-1);
				System.out.println("Received ACK for seq: " + ACKPacket.ack);
			}
			System.out.println("base=" + bufferControl.getBufferBase());
			
				
		
			if (ACKPacket.ack-1 == bufferControl.getBufferBase()){
				System.out.println("Received ACK for first index. Moving base.");
				bufferControl.getBuffer().rotateArray();
			}
			
			if (bufferControl.getBufferBase() == bufferControl.getBuffer().getPacketList().size())
				transmissionEnded = true;

		}*/
		clientSocket.close();//-> buffercontrol.close MAKE
	}
}