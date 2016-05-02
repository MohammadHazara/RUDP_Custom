package dataPacket;

import java.io.Serializable;
import java.sql.Timestamp;

public class DataPacket implements Serializable{
	private static final long serialVersionUID = 1;
	
	public byte[] data;
	public int seq;
	public int ack = 0;
	public transient Timestamp sentTimestamp;

	public DataPacket(byte[] data, int seq) {
		this.data = data;
		this.seq = seq;
	}

}