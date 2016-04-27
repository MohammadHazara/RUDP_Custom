package client;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import dataPacket.DataPacket;
import dataPacket.Serializer;

public class BufferControlClient {
	public ArrayList<byte[]> packetList = new ArrayList<byte[]>();
	public final int packetSize = 10;
	final int windowFrameSize = 5;
	int seq = 0;
	InetAddress IP;

	public BufferControlClient() throws Exception {
		IP = InetAddress.getByName("localhost");
	}
	
	private byte[] intToByteArray(int value) {
		String s = Integer.toString(value);
		System.out.println(s.getBytes().length);
		return s.getBytes();
		

//		byte[] result = new byte[4];
//		result[0] = (byte) ((data & 0xFF000000) >> 24);
//		result[1] = (byte) ((data & 0x00FF0000) >> 16);
//		result[2] = (byte) ((data & 0x0000FF00) >> 8);
//		result[3] = (byte) ((data & 0x000000FF) >> 0);
//		
//		return result;
	}
	
	public void addData(String s){
		byte[] data = s.getBytes();
		   //split sequence into byte blocks
	       int packetSplits = data.length/packetSize;
	       if(packetSplits>0){
	    	   for (int i = 0; i < packetSplits+1; i++) {
	    		   byte[] dataSeg = Arrays.copyOfRange(data, i*packetSize, i*packetSize+packetSize);
	    		   DataPacket packet = new DataPacket(dataSeg, packetList.size()+1);
	    		   addPacket(packet);
	    	   }
	    	   
	       }
		
	}
	
	
	
	public void sendDataOnSocket(DatagramSocket sock){
		DatagramPacket sendPacket = new DatagramPacket(packetList.get(seq), packetList.get(seq).length, IP, 9876);
		try {
			sock.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			seq++;
		}
		System.out.println("CHECKPOINT");
	}
	
	private void addPacket(DataPacket packet){
		try {
			packetList.add(Serializer.toBytes(packet));
		} catch (Exception e) {
			System.out.println("Couldnt serialize object");
			e.printStackTrace();
		}
		
		
	}
	
	//we are only sending atm
	public void setACK(int ack){
		//ACK = intToByteArray(ack);
		//packet.setData(ACK, 4, ACK.length);
	}


}