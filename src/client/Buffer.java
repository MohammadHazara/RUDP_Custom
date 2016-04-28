package client;

import java.util.ArrayList;

public class Buffer {

	private int base = 0;
	private ArrayList<byte[]> packetList = new ArrayList<byte[]>();
	final int windowFrameSize = 5;
	private boolean[] ackedPackets = new boolean[windowFrameSize];

	public int getBase() {
		return base;
	}

	public void setBase() {
	rotateArray();
		

	}

	public ArrayList<byte[]> getPacketList() {
		return packetList;
	}

	public void setPacketList(ArrayList<byte[]> packetList) {
		this.packetList = packetList;
	}

	public boolean[] getAckedPackets() {
		return ackedPackets;
	}

	public void setAckedPacket(short i) {
		ackedPackets[i] = true;
	}

	public  void rotateArray() {
		int dist = 0;

		for (int i = 0; i < windowFrameSize; i++) {
			// check number of consecutive received ACKs
			
			if (ackedPackets[i]) {
				System.out.println("Packet " +i+ " is " + ackedPackets[i]);
				dist++;
			} else {
				break;
			}

		}
		// adjust array accordingly

		for (int j = 0; j < dist; j++) {

			try {
				ackedPackets[j] = ackedPackets[j + dist];
			} catch (ArrayIndexOutOfBoundsException e) {
				ackedPackets[j] = false;
			}
		}
		System.out.println("Dist " + dist);
		base += dist;
		
	}

}
