package client;

import java.util.ArrayList;

public class Buffer {
	
	private int base;
	private ArrayList<byte[]> packetList = new ArrayList<byte[]>();
	
	public int getBase() {
		return base;
	}
	public void setBase(int base) {
		this.base += base;
	}
	public ArrayList<byte[]> getPacketList() {
		return packetList;
	}
	public void setPacketList(ArrayList<byte[]> packetList) {
		this.packetList = packetList;
	}

}
