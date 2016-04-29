package client;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import dataPacket.DataPacket;
import dataPacket.Serializer;

public class BufferControlClient {
	DatagramSocket sock;
	public final int packetSize = 10;
	int seq = 0;
	private Buffer buffer;
	InetAddress IP;
	
	//timer
	private Timer timer;
	private final long timeout = 3000;
	private int timerIndex = 0;
	private boolean timerIsScheduled;

	public BufferControlClient(DatagramSocket sock) throws Exception {
		IP = InetAddress.getByName("10.16.235.46");
		buffer = new Buffer();
		timer = new Timer(true);
		this.sock = sock;
	}
	
	public int getWindowFrame(){
		return this.buffer.windowFrameSize;
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
		   //split sequence into byte blocks and create packets
	       int packetSplits = data.length/packetSize;	   
	    	   for (int i = 0; i < packetSplits+1; i++) {
	    		   byte[] dataSeg = Arrays.copyOfRange(data, i*packetSize, i*packetSize+packetSize);
	    		   DataPacket packet = new DataPacket(dataSeg, buffer.getPacketList().size()+1);
	    		   addPacket(packet);
	    		 
	    	   }
		
	}
	
	private void addPacket(DataPacket packet){
		try {
			buffer.getPacketList().add(packet);
		} catch (Exception e) {
			System.out.println("Couldnt serialize object");
			e.printStackTrace();
		}
		
		
	}
	
	public void sendData(){
		try {
			DataPacket packetToSend = buffer.getPacketList().get(seq);
			byte[] bytesToSend = Serializer.toBytes(packetToSend);
			DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, IP, 9876);
			System.out.println("Sending packet with seq: "+ packetToSend.seq);
			sock.send(sendPacket);
			
			//set packet sent timestamp
			packetToSend.sentTimestamp = new Timestamp(System.currentTimeMillis()%1000);
			
			seq++;//shouldnt be in finally. is seq redundant here? is base enough, since seq is set on constructor
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(!timerIsScheduled)//start timer
				{timer.schedule(new RescheduleTask(), timeout);
				timerIsScheduled = true;
				System.out.println("start timer");}
			 //seq++;
		}
	}
	
	private void resendPacketAtIndex(int i){
		System.out.println("resending packet at index: "+i);
		try {
			DataPacket packetToSend = buffer.getPacketList().get(i);
			byte[] bytesToSend = Serializer.toBytes(packetToSend);
			DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, IP, 9876);
			sock.send(sendPacket);
			
			//reset packet sent timestamp
			packetToSend.sentTimestamp = new Timestamp(System.currentTimeMillis()%1000);
			
			//seq++;//shouldnt be in finally. is seq redundant here? is base enough, since seq is set on constructor
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			 //seq++;
		}
	}
	
	private class RescheduleTask extends TimerTask{
		@Override
		public void run() {
			//find oldest timestamp not ACKed and calc time diff to last index that was timerfocus
			Timestamp lastIndexStamp = buffer.getPacketList().get(timerIndex).sentTimestamp;
			Timestamp oldestTimestamp = null;
			for (int i = 0; i < getWindowFrame(); i++) {
				if(!buffer.getAckedPackets()[i] && i<buffer.getPacketList().size()){//if index has not been ACKed yet, we will consider
					Timestamp t = buffer.getPacketList().get(i+getBufferBase()).sentTimestamp;
					if(oldestTimestamp == null || t.before(oldestTimestamp)){
						oldestTimestamp = t;
						
						timerIndex = i+getBufferBase();
						System.out.println("timerindex="+timerIndex);
					}
				}
			}
			System.out.println("inside timer");
			if(oldestTimestamp != null){
				//find time difference
				long timeDiff =  lastIndexStamp.getTime() - oldestTimestamp.getTime();
				if(timeDiff>=timeout || timeDiff<=0)
					timeDiff = timeout;
				
				//resend packet at timerIndex
				resendPacketAtIndex(timerIndex);
				//reschedule timer, dont reschedule if transmission is not ongoing (current transit has ended)
				if(getBufferBase() < buffer.getPacketList().size()){
					timer.schedule(new RescheduleTask(), timeDiff);
					System.out.println("reschedule timer with time: " +timeDiff);}
				else 
					timerIsScheduled = false;
				
			}
		}	
	}
	


	public Buffer getBuffer() {
		return buffer;
	}
	
	public void setAcked(int i){	
		this.buffer.setAckedPacket(i);
	}
	
	public int getBufferBase(){
		return this.buffer.getBase();
	}


}