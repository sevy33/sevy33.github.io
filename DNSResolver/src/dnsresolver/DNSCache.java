package dnsresolver;

import java.util.HashMap;

public class DNSCache {
	private HashMap<DNSQuestion, DNSRecord> map;
	
	public DNSCache() {
		map = new HashMap<>();
	}
	public boolean search(DNSQuestion question) {
		if(map.containsKey(question)) {
			return map.get(question).timestampValid();
		} 
		return false;	
	}
	
	public void addRecord(DNSQuestion question, DNSRecord record) {
		map.put(question, record);	
	}
}
