package dnsresolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DNSMessage {
	DNSHeader header;
	private byte[] bytesIn;
	//private static DNSQuestion question;
	//private static DNSRecord record;
	
	DNSQuestion[] questions;
	DNSRecord[] answers;
	DNSRecord[] authrecords;
	DNSRecord[] addrecords;
	
	public static DNSMessage decodeMessage(byte[] bytes) throws IOException {
		DNSMessage msg = new DNSMessage();
		DataInputStream din = new DataInputStream(new ByteArrayInputStream(bytes));
		msg.bytesIn = bytes;
		
		msg.header = DNSHeader.decodeHeader(din);
		
		msg.questions = new DNSQuestion[msg.header.getQdcount()];
		for(int i=0;i < msg.header.getQdcount();i++) {
			DNSQuestion question = DNSQuestion.decodeQuestion(din, msg);
			msg.questions[i] = question;
		}
		
		msg.answers = new DNSRecord[msg.header.getAncount()];
		for(int i=0;i < msg.header.getAncount();i++) {
			DNSRecord rec = DNSRecord.decodeRecord(din, msg);
			msg.answers[i] = rec;
		}
		
		msg.authrecords = new DNSRecord[msg.header.getNscount()];
		for(int i=0;i < msg.header.getNscount();i++) {
			DNSRecord rec = DNSRecord.decodeRecord(din, msg);
			msg.authrecords[i] = rec;
		}
		
		msg.addrecords = new DNSRecord[msg.header.getArcount()];
		for(int i=0;i < msg.header.getArcount();i++) {
			DNSRecord rec = DNSRecord.decodeRecord(din, msg);
			msg.addrecords[i] = rec;
		}
		
		for (int i=0;i < msg.header.getAncount(); ++i) {
            if (!DNSServer.cache.search(msg.questions[0])) {
                DNSServer.cache.addRecord(msg.questions[0], msg.answers[0]);
            }
        }
		
		return msg;
	}

	public String[] readDomainName(InputStream in) throws IOException {
		
		ArrayList<String> domainName = new ArrayList<String>();
		
		byte b1 = (byte) in.read();
		
		if((b1 & 0xC0) == 0xC0) {
			byte b2 = (byte) in.read();
			int offset = (((b1 & 0x3F) << 8) | b2);
			
			return readDomainName(offset);
			
		} else {
			int length = b1;
			
	        while (length > 0) {
	            byte[] namerec = new byte[length];

//	            for (int i = 0; i < length; i++) {
//	            	namerec[i] = in.read();
//	            }
	            
	            in.read(namerec);
	            
	            domainName.add(new String(namerec));
	            length = in.read();
	        }
		}
		
		return domainName.toArray(new String[domainName.size()]);
		
	}
	
	public String[] readDomainName(int firstByte) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytesIn, firstByte, bytesIn.length - firstByte);
		return readDomainName(bais);
	}
	
	public static DNSMessage buildResponse(DNSMessage request, DNSRecord[] answers) {
		DNSMessage response = new DNSMessage();
		
		response.questions = request.questions;
		response.answers = answers;
		
		for(int i=0;i < request.addrecords.length;i++) {
			DNSRecord record = request.addrecords[i];
			if(record.type[0] == 0 && record.type[1] == 41) {
				response.addrecords = new DNSRecord[]{record};
			}
		}
	
		response.header = DNSHeader.buildResponseHeader(request, response);
		
		return response;
	}

	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HashMap<String,Integer> domainLocations = new HashMap<String,Integer>();
		
		header.writeBytes(baos);

		if(questions != null) {
			for(DNSQuestion q : questions) {
				q.writeBytes(baos, domainLocations);
			}
		}
		if(answers != null) {
			for(DNSRecord r : answers) {
				r.writeBytes(baos, domainLocations);
			}
		}
		if(authrecords != null) {
			for(DNSRecord r : authrecords) {
				r.writeBytes(baos, domainLocations);
			}
		}
		if(addrecords != null) {
			for(DNSRecord r : addrecords) {
				r.writeBytes(baos, domainLocations);
			}
		}
		
		byte[] bytes = baos.toByteArray();
		return bytes;
	}

	public static void writeDomainName(ByteArrayOutputStream baos, HashMap<String,Integer> domainLocations, String[] domainPieces) throws IOException {
		
		String dname = "";
		for(int i=0;i < domainPieces.length;i++) {
			dname += domainPieces[i];
		}
		
		if(domainLocations.containsKey(dname)) {
			int position = domainLocations.get(dname);
			
			byte b1 = (byte) (position >> 8 & 0xFF);
			b1 = (byte) (b1 | 0xC0);
			
			
		} else {
			int position = baos.size();
			
			for(int i=0;i < domainPieces.length;i++) {
				int len = domainPieces[i].length();
				baos.write((byte) len);
				baos.write(domainPieces[i].getBytes());
			}
			
			baos.write(0);
			domainLocations.put(dname, position);
		}
	}
	
	public String octetsToString(String[] octets) {
		String oct = new String();
		for(int i=0;i < octets.length;i++) {
			oct += oct + "."; 
		}
		return oct;
	}
	
	@Override
	public String toString() {
		String qu = "";
		String rec = "";
		String rec2 = "";
		String rec3 = "";
		for(DNSQuestion q : questions) {
			qu += q;
		}
		for(DNSRecord r : answers) {
			rec += r;
		}
		for(DNSRecord r : authrecords) {
			rec2 += r;
		}
		for(DNSRecord r : addrecords) {
			rec3 += r;
		}
		
		return "DNSMessage [header=" + header + ", questions=" + qu + ", answers=" + rec + ", authrecords="
				+ rec2 + ", addrecords=" + rec3 + "]";
		
//		return null;
	}
	
	public DNSRecord[] getAnswers()
	{
		return answers;
	}
	
	public DNSQuestion[] getQuestions() {
		return questions;
	}
	
}
