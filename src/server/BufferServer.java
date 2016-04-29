package server;

import java.util.ArrayList;

import javax.print.event.PrintJobAttributeListener;

import dataPacket.DataPacket;

public class BufferServer {
	private int base = 0;
	private ArrayList<DataPacket> packetList = new ArrayList<DataPacket>();
	final int windowFrameSize = 5;
	int lastPrintedSeq = 0;
	
	public void addReceivedPacket(DataPacket packet){
		int amountToExpand = packet.seq-packetList.size()+1;
		if(amountToExpand>0){
			for(int i = 0; i<amountToExpand;i++){
				packetList.add(new DataPacket(null, 0));
			}
			
		}
		packetList.set(packet.seq-1, packet);
		
	}
	
	public void printData(){
		if(lastPrintedSeq == 0 && packetList.get(lastPrintedSeq).seq != 0){//to avoid rechecking 1 element each loop
			System.out.print(new String(packetList.get(0).data));
			lastPrintedSeq++;
		}
			
		while(packetList.get(lastPrintedSeq+1).seq != 0 && packetList.get(lastPrintedSeq).seq != 0){
			System.out.print(new String(packetList.get(lastPrintedSeq).data));
			lastPrintedSeq++;
			
			//if all packets have been printed, break out of while
			if(lastPrintedSeq>=packetList.size())break;
		}

			
	}
	
}
