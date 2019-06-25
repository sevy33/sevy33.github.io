package dnsresolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DNSQuestion {
	private String[] qname;
	private byte[] qtype = new byte[2];
	private byte[] qclass = new byte[2];
	
	public static DNSQuestion decodeQuestion(InputStream in, DNSMessage msg) throws IOException {
		
		DNSQuestion question = new DNSQuestion();
		question.qname = msg.readDomainName(in);
        question.qtype[0] = (byte) in.read();
        question.qtype[1] = (byte) in.read();
        
        question.qclass[0] = (byte) in.read();
        question.qclass[1] = (byte) in.read();
		
		return question;
	}

	public void writeBytes(ByteArrayOutputStream baos, HashMap<String,Integer> domainNameLocations) throws IOException {
		DNSMessage.writeDomainName(baos, domainNameLocations, qname);
		
		baos.write(qtype);
		baos.write(qclass);
	}

	@Override
	public String toString() {
		String dname = "";
		for(int i=0;i < qname.length;i++) {
			dname += qname[i];
		}
		return "DNSQuestion [qname=" + dname + ", qtype=" + qtype + ", qclass=" + qclass + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(qclass);
		result = prime * result + Arrays.hashCode(qname);
		result = prime * result + Arrays.hashCode(qtype);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DNSQuestion other = (DNSQuestion) obj;
		if (!Arrays.equals(qclass, other.qclass))
			return false;
		if (!Arrays.equals(qname, other.qname))
			return false;
		if (!Arrays.equals(qtype, other.qtype))
			return false;
		return true;
	}

	public String[] getQname() {
		return qname;
	}	

}
