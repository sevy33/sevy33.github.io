package tls;

import java.math.BigInteger;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class KeysServer {
	public SecretKeySpec serverEncrypt;
	public SecretKeySpec clientEncrypt;
	public SecretKeySpec serverMAC;
	public SecretKeySpec clientMAC;
	public IvParameterSpec serverIV;
	public IvParameterSpec clientIV;
	
	public KeysServer(byte[] clientNonce, BigInteger sharedSecret) throws Exception {
		byte[] prk = Server.computeHmac(clientNonce, sharedSecret.toByteArray());
		serverEncrypt = new SecretKeySpec(hkdfExpand(prk, "server encrypt"), "AES");
		clientEncrypt = new SecretKeySpec(hkdfExpand(serverEncrypt.getEncoded(), "client encrypt"), "AES");
		serverMAC = new SecretKeySpec(hkdfExpand(clientEncrypt.getEncoded(), "server MAC"), "AES");
		clientMAC = new SecretKeySpec(hkdfExpand(serverMAC.getEncoded(), "client MAC"), "AES");
		serverIV = new IvParameterSpec(hkdfExpand(clientMAC.getEncoded(), "server IV"));
		clientIV = new IvParameterSpec(hkdfExpand(serverIV.getIV(), "client IV"));
	}
	
	public static byte[] hkdfExpand(byte[] cmac, String tag) throws Exception {
		byte[] byteKey = new byte[16];
		byte[] okm = Server.computeHmac(cmac, tag.getBytes());
        for(int i = 0; i < 16; i++) {
        	byteKey[i] = okm[i];
        }      
        return byteKey;
	}
	
}
