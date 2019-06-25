package dnsresolver;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNSServer {
    private static final int DNS_SERVER_PORT = 8053;
    static DNSCache cache = new DNSCache();
 
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
	    byte[] buf = new byte[1024];
	    DatagramPacket dgp = new DatagramPacket(buf, buf.length);
	    DatagramSocket sk;

	    sk = new DatagramSocket(DNS_SERVER_PORT);
	    System.out.println("Server started");
	    
	    
	    //TODO: Add Cache
	    //TODO: Support multiple questions
	    
	    
	    while (true) {
		      sk.receive(dgp);
		      DNSMessage msg = DNSMessage.decodeMessage(buf); 
		      //DNSMessage response;
		      
		      if(!cache.search(msg.getQuestions()[0])) {
			      DatagramSocket googleSocket = new DatagramSocket();
			      InetAddress ipAddress = InetAddress.getByName("8.8.8.8");
			      DatagramPacket dnsReqPacket = new DatagramPacket(buf, buf.length, ipAddress, 53);
			      googleSocket.send(dnsReqPacket);
			           
			      System.out.println("Received from Google:");
			      byte[] googleRes = new byte[1024];
				  DatagramPacket googlePack = new DatagramPacket(googleRes, googleRes.length);
			      googleSocket.receive(googlePack);
			      DNSMessage googleMsg = DNSMessage.decodeMessage(googleRes);
			      System.out.println(googleMsg);
			      
			      DNSRecord answer = googleMsg.getAnswers()[0];
			      DNSRecord[] answers = {answer};
			      msg = DNSMessage.buildResponse(msg, answers);
		      } else {
			      byte[] responseBuff = msg.toBytes(); 
			      System.out.println("\n\n");
			      DatagramPacket res = new DatagramPacket(responseBuff, responseBuff.length, dgp.getAddress(), dgp.getPort());
			      sk.send(res);
		      }
	    }
	}
}
