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

	public static void main(String args[]) throws Exception {
		boolean transmissionEnded = false;
		BufferControlClient bufferControl = new BufferControlClient();
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");

		byte[] sendData = new byte[bufferControl.packetSize];
		byte[] receiveData = new byte[200];
		String sentence = inFromUser.readLine();

		bufferControl.addData(sentence);
		while (!transmissionEnded) {
			// Loop runs until all packets sent
			while (bufferControl.seq < bufferControl.getBuffer().getPacketList().size()
					&& bufferControl.seq < bufferControl.getBufferBase() + bufferControl.getWindowFrame()
						&& bufferControl.seq >= bufferControl.getBufferBase()) {

				bufferControl.sendDataOnSocket(clientSocket);
				
			}

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			//receive ACK
			clientSocket.receive(receivePacket);
			DataPacket ACKPacket = (DataPacket) Serializer.toObject(receivePacket.getData());
			bufferControl.setAcked((short) (ACKPacket.ack-bufferControl.getBufferBase()-1));
			System.out.println(bufferControl.getBufferBase());
			
			System.out.println("Received ACK for seq: " + ACKPacket.ack);	
		
			if (ACKPacket.ack-1 == bufferControl.getBufferBase()){
				System.out.println("nus");
				bufferControl.getBuffer().rotateArray();
			}
			
			if (bufferControl.getBufferBase() == bufferControl.getBuffer().getPacketList().size())
			transmissionEnded = true;

		}
		clientSocket.close();
	}
}