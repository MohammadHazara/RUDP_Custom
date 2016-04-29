package client;

import java.sql.Timestamp;
import java.util.ArrayList;

import dataPacket.DataPacket;

public class Buffer {

	private int base = 0;
	private ArrayList<DataPacket> packetList = new ArrayList<DataPacket>();
	final int windowFrameSize = 5;
	private boolean[] ackedPackets = new boolean[windowFrameSize];

	public  void rotateArray() {
		//amount to shift
		int shifts = 0;

		// check number of consecutive received ACKs
		for (int i = 0; i < windowFrameSize; i++) {
			if (ackedPackets[i]) {
				//System.out.println("Packet " +i+ " is " + ackedPackets[i]);
				shifts++;
			} else {
				break;
			}

		}
		
		//adjust window (array) accordingly
		for (int j = 0; j < shifts; j++) {
			try {
				ackedPackets[j] = ackedPackets[j+shifts];
			} catch (ArrayIndexOutOfBoundsException e) {
				ackedPackets[j] = false;
			}
		}
		//System.out.println("Amount to shift " + shifts);
		base += shifts;
		
	}
	
	public int getBase() {
		return base;
	}

	public void setBase() {
		rotateArray();
	}

	public ArrayList<DataPacket> getPacketList() {
		return packetList;
	}

	public void setPacketList(ArrayList<DataPacket> packetList) {
		this.packetList = packetList;
	}

	public boolean[] getAckedPackets() {
		return ackedPackets;
	}

	public void setAckedPacket(int i) {
		ackedPackets[i] = true;
	}


}
