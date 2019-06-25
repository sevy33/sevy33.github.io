package tls;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Client {
	private static Socket connection;
	private static Cipher cipher;
	private static KeysClient cmac;
	private static byte[] cAllMsgBytes = null;
	private static ByteArrayOutputStream cBAO;

	public static void main(String[] args) throws Exception {
		cBAO = new ByteArrayOutputStream();
		
		String hex = ("FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1" + 
				"29024E08 8A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD" + 
				"EF9519B3 CD3A431B 302B0A6D F25F1437 4FE1356D 6D51C245" + 
				"E485B576 625E7EC6 F44C42E9 A637ED6B 0BFF5CB6 F406B7ED" + 
				"EE386BFB 5A899FA5 AE9F2411 7C4B1FE6 49286651 ECE45B3D" + 
				"C2007CB8 A163BF05 98DA4836 1C55D39A 69163FA8 FD24CF5F" + 
				"83655D23 DCA3AD96 1C62F356 208552BB 9ED52907 7096966D" + 
				"670C354E 4ABC9804 F1746C08 CA18217C 32905E46 2E36CE3B" + 
				"E39E772C 180E8603 9B2783A2 EC07A28F B5C55DF0 6F4C52C9" + 
				"DE2BCBF6 95581718 3995497C EA956AE5 15D22618 98FA0510" + 
				"15728E5A 8AACAA68 FFFFFFFF FFFFFFFF").replaceAll("\\s", "");
		
		BigInteger p = new BigInteger(hex, 16);
		
		BigInteger g = new BigInteger(String.valueOf(14));
		
		connection = new Socket("localhost", 8129);
		
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey cpriv = getPemPrivateKey("src/tls/clientPrivateKey.der", kf);
		
		//mcPriv = new BigInteger(cpriv.getEncoded());
		
		SecureRandom crand = new SecureRandom();
		BigInteger cran = new BigInteger(String.valueOf(crand.nextInt()));
		
		BigInteger cpub = getPublicKey(p, g, cran);
		
		//Send pubKey to Server
		ObjectOutputStream toServer;
		toServer = new ObjectOutputStream(connection.getOutputStream());
		SecureRandom cSendRand = new SecureRandom();
		byte[] bytes = new byte[32];
		cSendRand.nextBytes(bytes);
		
 		toServer.writeObject(bytes);
		toServer.writeObject(cpub);
		
		//Recieve pubKey from Server
		ObjectInputStream fromServer;
		fromServer = new ObjectInputStream(connection.getInputStream());
		BigInteger spub = new BigInteger(fromServer.readObject().toString());
		//Add to baos
		cBAO.write(spub.toByteArray());
		
		BigInteger sSignPub = new BigInteger(fromServer.readObject().toString());
		//Add to baos
		cBAO.write(sSignPub.toByteArray());
		
		Certificate sCert = (Certificate) fromServer.readObject();
		//Add to baos
		cBAO.write(sCert.getEncoded());
		
		boolean ccheckSig = verifySignedKey(sCert, sSignPub, spub);
		System.out.println("Signed " + ccheckSig + "\n");
		
		BigInteger cSignPub = signPubKey(cpriv, cpub);
		toServer.writeObject(cSignPub);
		
		Certificate cCert = getCert("src/tls/CASignedClientCertificate.pem");
		toServer.writeObject(cCert);
		
		
		
		//compute shared key
		BigInteger cSharedSecret = combine(cran, spub, p);
		
		cmac = new KeysClient(bytes, cSharedSecret);
		
		//recieve server all handshake msgs
		byte[] allMsgs = (byte[]) fromServer.readObject();
		String allMsgHmac = decryptFile(allMsgs, cmac.serverEncrypt, cmac.serverIV);
		
		//Send client all handshake msgs
		cAllMsgBytes = encryptFile(cBAO.toByteArray(), cmac.clientEncrypt, cmac.clientIV);
		toServer.writeObject(cAllMsgBytes);

		try {
			byte[] encdata = (byte[]) fromServer.readObject();
			
			System.out.println(encdata);
			
			String decryptedFile = decryptFile(encdata, cmac.serverEncrypt, cmac.serverIV);
			
			System.out.println(decryptedFile);
		
		} catch(Exception e) {
			System.err.println("Error Decrypting: " + e);
		}
		
		//Send back message
		byte[] encRes = encryptFile("Recieved File".getBytes(), cmac.clientEncrypt, cmac.clientIV);
		toServer.writeObject(encRes);
		
		
	}
	
	public static PrivateKey getPemPrivateKey(String filename, KeyFactory kf) throws Exception {
		File file = new File(filename);
        InputStream fis = new FileInputStream(file);
        byte[] bytes = fis.readAllBytes();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        return kf.generatePrivate(spec);
	} 
	
	public static BigInteger getPublicKey(BigInteger p, BigInteger g, BigInteger rand) {
		BigInteger val = g.modPow(rand,p);
		return val;
	}
	
	public static BigInteger signPubKey(PrivateKey priv, BigInteger pub) throws Exception {
		Signature sig = Signature.getInstance("SHA256WithRSA");
		sig.initSign(priv);
		sig.update(pub.toByteArray());
		BigInteger s = new BigInteger(sig.sign());
		return s;
	}
	
	public static Certificate getCert(String filename) throws Exception {
		File file = new File(filename);
		InputStream in = new FileInputStream(file);
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate)fact.generateCertificate(in);
		return cert;
	}
	
	static boolean verifySignedKey(Certificate cert, BigInteger signedPub, BigInteger spub) throws Exception {
		if(!verifyCertificate(cert)) return false;
		Signature verifier = Signature.getInstance("SHA256WithRSA");
		verifier.initVerify(cert);
		verifier.update(spub.toByteArray());
		return verifier.verify(signedPub.toByteArray());
	}
	
	static boolean verifyCertificate(Certificate cert) throws Exception {
		try {
		      ((X509Certificate) cert).checkValidity();
		      return true;
		    }
		    catch (CertificateExpiredException e) {
		      throw new CertificateExpiredException("KeyStore certificate is expired: " + e.getMessage());
		    }
		    catch (CertificateNotYetValidException e) {
		      throw new CertificateNotYetValidException("KeyStore certificate is not yet valid: " + e.getMessage());
		    }
	}
	
	private static BigInteger combine(BigInteger priv, BigInteger pub, BigInteger p) throws Exception  {
		return pub.modPow(priv, p);
	}
	
	public static byte[] computeHmac(byte[] sharedsecret, byte[] nonce) throws Exception {
		 Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		 SecretKeySpec secret_key = new SecretKeySpec(sharedsecret, "HmacSHA256");
		 sha256_HMAC.init(secret_key);
		 byte[] hash = sha256_HMAC.doFinal(nonce);
		 return hash;
	}	
	
	public static byte[] encryptFile(byte[] plainText, SecretKeySpec secretKey, IvParameterSpec iv) throws Exception {
		byte[] macTextByte = computeHmac(plainText, cmac.clientMAC.getEncoded());

		byte[] all = null;
		
        try {
        	cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        	
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            
            byte[] msg = cipher.update(plainText);
            byte[] encryptedByte = cipher.doFinal(macTextByte);
            
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(msg);
            output.write(encryptedByte);
            all = output.toByteArray();

        } catch(Exception e) {
            System.err.println("Error while initializing Cipher while encrypting text: " + e);
        }

        return all;
    }
	
	public static String decryptFile(byte[] encryptedText, SecretKeySpec secretKey, IvParameterSpec iv) throws Exception {

        String decryptedText = "";

        try {
        	cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        	
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] decryptedByte = cipher.doFinal(encryptedText);
           
            byte[] msg = new byte[decryptedByte.length-32];
    		for(int i=0;i < (decryptedByte.length-32);i++) {
    			msg[i] = decryptedByte[i];
    		}
    		
    		byte[] msgMac = computeHmac(msg, cmac.serverMAC.getEncoded());
    		
    		byte[] checkDecr = new byte[32];
    		for(int i=0;i+32 > decryptedByte.length;i++) {
    			checkDecr[i] = decryptedByte[i+32];
    		}

    		for(int i=0;i >= msgMac.length;i++) {
    			if(msgMac[i] != checkDecr[i]) System.out.println("false message");
    		}

            decryptedText = new String(msg);
            
            
        } catch(Exception e) {
            System.err.println("Error while initializing Cipher while decrypting text: " + e);
        }

        return decryptedText;
    }
}
