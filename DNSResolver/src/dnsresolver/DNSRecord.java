package dnsresolver;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;

public class DNSRecord {
	private String[] name;
	byte[] type = new byte[2];
	private byte[] cls = new byte[2];
	private byte[] ttl = new byte[4];
	private byte[] rdlength = new byte[2];
	private int rdlengthVal;
	private byte[] rdata;
	private int ttlVal;
	private Date date;
	private long d;
	
	public static DNSRecord decodeRecord(InputStream in, DNSMessage msg) throws IOException {
		//DataInputStream din = new DataInputStream(new ByteArrayInputStream(in.readAllBytes()));
		DNSRecord record = new DNSRecord();
		
        record.name = msg.readDomainName(in);
        
        record.type[0] = (byte) in.read();
        record.type[1] = (byte) in.read();
        
        record.cls[0] = (byte) in.read();
        record.cls[1] = (byte) in.read();
        
        record.ttl[0] = (byte) in.read();
        record.ttl[1] = (byte) in.read();
        record.ttl[2] = (byte) in.read();
        record.ttl[3] = (byte) in.read();

        Calendar cal = Calendar.getInstance();
        record.date = (Date) cal.getTime();
        record.ttlVal = (record.ttl[0] << 24) | record.ttl[1] << 16 | record.ttl[2] << 8 | record.ttl[3];
        
        
        record.rdlength[0] = (byte) in.read();
        record.rdlength[1] = (byte) in.read();
        
        
        
        
        record.rdlengthVal = ((record.rdlength[0] << 8) | record.rdlength[1]);
        
        record.rdata = new byte[record.rdlengthVal];
        in.read(record.rdata);
        		
		return record;
	}
	
	public void writeBytes(ByteArrayOutputStream baos, HashMap<String, Integer> domainLocations) throws IOException {
		DNSMessage.writeDomainName(baos, domainLocations, name);
		baos.write(type);
		baos.write(cls);
		
		Calendar cal = Calendar.getInstance();
        Date dateNow = (Date) cal.getTime();
        long mills = dateNow.getTime() - date.getTime();
        int ttlNow = ttlVal - (int) (mills/1000);
        		
		baos.write(ttlNow >> 24 & 0xFF);
		baos.write(ttlNow >> 16 & 0xFF);
		baos.write(ttlNow >> 8 & 0xFF);
		baos.write(ttlNow & 0xFF);
		baos.write(rdlength);
		baos.write(rdata);
	}
	
	@Override
	public String toString() {
		String dname = "";
		for(int i=0;i < name.length;i++) {
			dname += name[i];
		}
		return "DNSRecord [name=" + dname + ", type=" + type 
				+ ", cls=" + cls
				+ ", ttl=" + ttl
				+ ", rdlength=" + rdlength
				+ ", rdlengthVal=" + rdlengthVal
				+ ", rdata=" + rdata + "]";
	}
	
	public boolean timestampValid() {
		Calendar cal = Calendar.getInstance();
        Date dateNow = (Date) cal.getTime();
        if(date.getTime() < dateNow.getTime()) return true;
        return false;
	}

}
