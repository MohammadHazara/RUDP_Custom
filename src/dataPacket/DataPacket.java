package dataPacket;

import java.io.Serializable;

public class DataPacket implements Serializable{
	//private static final long serialVersionUID = 1;
    //private static final int CURRENT_SERIAL_VERSION = 1;
	
	public byte[] data;
	public int seq;
	public int ack;

	public DataPacket(byte[] data, int seq) {
		this.data = data;
		this.seq = seq;
	}

}