package dnsresolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class DNSHeader {
	private boolean qr; //query or response
	private int opcode; //code assigned
	private boolean aa; //authoritative answer
	private boolean tc; //truncation
	private boolean rd; //recursion desired
	private boolean ra; //recursion available
	private int z; //future use
	private boolean ad;
	private boolean cd;
	private int rcode; //response code
	private short qdcount; // # questions
	private short ancount; // # answers
	private short nscount; // # authoritatice records
	private short arcount; // # additional records
	
	private byte[] id = new byte[2];
	private byte[] flags = new byte[2];
	
	
	public static DNSHeader decodeHeader(InputStream in) throws IOException {
		DNSHeader header = new DNSHeader();
		in.read(header.id);
		in.read(header.flags);
		
		byte[] shortNumber = new byte[2];
		in.read(shortNumber);
		header.qdcount = ByteBuffer.wrap(shortNumber).getShort();
		in.read(shortNumber);
		header.ancount = ByteBuffer.wrap(shortNumber).getShort();
		in.read(shortNumber);
		header.nscount = ByteBuffer.wrap(shortNumber).getShort();
		in.read(shortNumber);
		header.arcount = ByteBuffer.wrap(shortNumber).getShort();
		
		header.opcode = (header.flags[0] & 0x78) >> 3;
		header.rd = (header.flags[0] & 0x01) == 1;
		header.cd = (header.flags[1] >> 4 & 0x01) == 1;
		
		return header;
	}
	
	public static DNSHeader buildResponseHeader(DNSMessage request, DNSMessage response) {
		DNSHeader head = new DNSHeader();
		
		head.id = request.header.id;
		head.qdcount = (short) (response.questions != null ? response.questions.length : 0);
		head.ancount = (short) (response.answers != null ? response.answers.length : 0);
		head.nscount = (short) (response.authrecords != null ? response.authrecords.length : 0);
		head.arcount = (short) (response.addrecords != null ? response.addrecords.length : 0);
		
		head.qr = true;
		head.opcode = request.header.opcode;
		head.aa = false;
		head.tc = false;
		head.rd = request.header.rd;
		head.ra = true;
		head.ad = false;
		head.cd = request.header.cd;
		head.z = 0;
		head.rcode = 0;
		
		return head;
	}
	
	public void writeBytes(OutputStream out) throws IOException {
		
		out.write(id);
		byte flag1 = 0;
		
		if(qr == true) {
			flag1 = (byte) (1 << 7);
		}
		
		byte opcode1 = (byte) opcode;
		flag1 = (byte) (flag1 | (opcode << 3));
		
		byte aa1 = (byte) (aa == true ? 1 : 0);
		flag1 = (byte) (flag1 | (aa1 << 2));
		
		byte tc1 = (byte) (tc == true ? 1 : 0);
		flag1 = (byte) (flag1 | (tc1 << 1));
		
		byte rd1 = (byte) (rd == true ? 1 : 0);
		flag1 = (byte) (flag1 | (rd1));
		
		out.write(flag1);
		
		byte flag2 = (byte) (ra == true ? 0x80 : 0);
		
		flag2 = (byte) (ad == true ? flag2 | 0x20 : flag2);
		
		flag2 = (byte) (cd == true ? flag2 | 0x10 : flag2);
		
		flag2 = (byte) (flag2 | (byte) rcode);
		
		out.write(flag2);
		
		out.write(qdcount >> 8);
		out.write(qdcount & 0xFF);
		
		out.write(ancount >> 8);
		out.write(ancount & 0xFF);
		
		out.write(nscount >> 8);
		out.write(nscount & 0xFF);
		
		out.write(arcount >> 8);
		out.write(arcount & 0xFF);
		
	}
	
	@Override
	public String toString() {
		return "DNSHeader [id=" + id + ", flags=" + flags 
				+ ", questionEntries=" + qdcount
				+ ", answerEntries=" + ancount
				+ ", authorityRecords=" + nscount
				+ ", additionalRecords=" + arcount + "]";
	}
	
	public static BitSet fromByte(byte b) {
	    BitSet bits = new BitSet(8);
	    for (int i = 0; i < 8; i++)
	    {
	        bits.set(i, (b & 1) == 1);
	        b >>= 1;
	    }
	    return bits;
	}
	
	public int getQdcount() {
		return qdcount;
	}

	public int getAncount() {
		return ancount;
	}

	public int getNscount() {
		return nscount;
	}

	public int getArcount() {
		return arcount;
	}

}
