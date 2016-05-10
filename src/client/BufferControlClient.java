package client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import dataPacket.DataPacket;
import dataPacket.Serializer;

public class BufferControlClient {
	DatagramSocket sock;
	public final int dataSize = 10;
	int seq = 0;
	private Buffer buffer;
	InetAddress IP;
	
	//timer
	private Timer timer;
	private final long timeout = 1000;
	private int timerIndex = 0;
	private boolean timerIsScheduled;

	public BufferControlClient(DatagramSocket sock) throws Exception {
		IP = InetAddress.getByName("localhost");//test IP 10.16.163.221 on LAN
		buffer = new Buffer();
		timer = new Timer(true);
		this.sock = sock;
	}
	
	public int getWindowFrame(){
		return this.buffer.windowFrameSize;
	}
	
	
	public void addData(String s){
		byte[] data = s.getBytes();
		   //split sequence into byte blocks and create packets
	       int packetSplits = data.length/dataSize;	 
	       if((data.length%dataSize)==0)--packetSplits;
	    	   for (int i = 0; i < packetSplits+1; i++) {
	    		   byte[] dataSeg = Arrays.copyOfRange(data, i*dataSize, i*dataSize+dataSize);
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
	
	public void send() throws Exception{
		boolean transmissionEnded = false;
		boolean blockACK = true;
		
		while (!transmissionEnded) {
			// Loop runs until all packets sent
			while (seq < getBuffer().getPacketList().size()
					&& seq < getBufferBase() + getWindowFrame()
						&& seq >= getBufferBase()) {

				sendData();
			}
			byte[] receiveData = new byte[200];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			//receive ACK
			sock.receive(receivePacket);
			DataPacket ACKPacket = (DataPacket) Serializer.toObject(receivePacket.getData());
			if(ACKPacket.ack==3 && blockACK){
			//BLOCK ACK ON SEQ3 ONE TIME - TESTING PURPOSE ONLY
				blockACK = false;
			}else{
				setAcked(ACKPacket.ack-getBufferBase()-1);
				System.out.println("Received ACK for seq: " + ACKPacket.ack);
			}
			System.out.println("base=" + getBufferBase());
			
				
		
			if (ACKPacket.ack-1 == getBufferBase()){
				System.out.println("Received ACK for first index. Moving base.");
				getBuffer().rotateArray();
			}
			
			if (getBufferBase() == getBuffer().getPacketList().size())
				transmissionEnded = true;

		}
	}
	
	public void sendData(){
		try {
			DataPacket packetToSend = buffer.getPacketList().get(seq);
			byte[] bytesToSend = Serializer.toBytes(packetToSend);
			DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, IP, 9876);
			
			System.out.println("Sending packet with seq: "+ packetToSend.seq);
			//System.out.println("packet length:" +sendPacket.getLength());
			sock.send(sendPacket);
			
			//set packet sent timestamp
			packetToSend.sentTimestamp = new Timestamp(System.currentTimeMillis()%1000);
			
			seq++;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(!timerIsScheduled)//start timer
				{timer.schedule(new RescheduleTask(), timeout);
				timerIsScheduled = true;
				System.out.println("start timer");}
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
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class RescheduleTask extends TimerTask{
		@Override
		public void run() {
			//find oldest timestamp not ACKed and calc time diff to last index that was timerfocus
			Timestamp lastIndexStamp = buffer.getPacketList().get(timerIndex).sentTimestamp;
			Timestamp oldestTimestamp = null;
			for (int i = 0; i < getWindowFrame(); i++) {
				if(!buffer.getAckedPackets()[i] && i+getBufferBase()<buffer.getPacketList().size()){//if index has not been ACKed yet, we will consider
					Timestamp t = buffer.getPacketList().get(i+getBufferBase()).sentTimestamp;
					if(oldestTimestamp == null || t.before(oldestTimestamp)){
						oldestTimestamp = t;
						
						timerIndex = i+getBufferBase();
						//System.out.println("timerindex="+timerIndex);
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