package tls;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Server {
	private static ServerSocket server = null;
	private static Cipher cipher;
	private static KeysServer smac;
	private static byte[] sAllMsgBytes = null;
	private static ByteArrayOutputStream sBAO;
	
	public static void main(String[] args) throws Exception {
		sBAO = new ByteArrayOutputStream();
		
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
		
		KeyFactory kf = KeyFactory.getInstance("RSA");
		
		PrivateKey spriv = getPemPrivateKey("src/tls/serverPrivateKey.der", kf);
		
		//msPriv = new BigInteger(spriv.getEncoded());
		
		SecureRandom srand = new SecureRandom();
		BigInteger sran = new BigInteger(String.valueOf(srand.nextInt()));
		
		BigInteger spub = getPublicKey(p, g, sran);
		
		server = new ServerSocket(8129);
		
		Socket client = server.accept();
		
		//Recieve pubKey from Client
		ObjectInputStream fromClient;
		fromClient = new ObjectInputStream(client.getInputStream());
		byte[] cSendRand = (byte[]) fromClient.readObject();
		//Add to baos
		sBAO.write(cSendRand);
		
		BigInteger cPub = new BigInteger(fromClient.readObject().toString());
		//Add to baos
		sBAO.write(cPub.toByteArray());
		
		//Send pubKey to Client
		ObjectOutputStream toClient;
		toClient = new ObjectOutputStream(client.getOutputStream());
		toClient.writeObject(spub);
		
		BigInteger sSignPub = signPubKey(spriv, spub);
		
		//Send sSignPub
		toClient.writeObject(sSignPub);
		
		Certificate sCert = getCert("src/tls/CASignedServerCertificate.pem");
		//Send sCert
		toClient.writeObject(sCert);

		BigInteger cSignPub = new BigInteger(fromClient.readObject().toString());
		//Add to baos
		sBAO.write(cSignPub.toByteArray());
		
		Certificate cCert = (Certificate) fromClient.readObject();
		//Add to baos
		sBAO.write(cCert.getEncoded());
		
		boolean scheckSig = verifySignedKey(cCert, cSignPub, cPub);
		System.out.println("Signed " + scheckSig + "\n");
		
		//compute shared key
		BigInteger sSharedSecret = combine(sran, cPub, p);

		smac = new KeysServer(cSendRand, sSharedSecret);
		
		//Send server all handshake msgs
		sAllMsgBytes = encryptFile(sBAO.toByteArray(), smac.serverEncrypt, smac.serverIV);
		toClient.writeObject(sAllMsgBytes);
		
		//recieve client all handshake msgs
		byte[] allMsgs = (byte[]) fromClient.readObject();
		String allMsgHmac = decryptFile(allMsgs, smac.clientEncrypt, smac.clientIV);	
		
		byte[] data = readFileAsString("src/tls/testfile.txt");
		//byte[] data = "Hello World".getBytes();
		
		byte[] encryptedFile = encryptFile(data, smac.serverEncrypt, smac.serverIV);
		
		toClient.writeObject(encryptedFile);
		
		System.out.println(encryptedFile);
		
		byte[] encdata = (byte[]) fromClient.readObject();
		
		String decryptedFile = decryptFile(encdata, smac.clientEncrypt, smac.clientIV);
		System.out.println("Received from Client: " + decryptedFile);
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
	
	static boolean verifySignedKey(Certificate cert, BigInteger signedPub, BigInteger cpub) throws Exception {
		if(!verifyCertificate(cert)) return false;
		Signature verifier = Signature.getInstance("SHA256WithRSA");
		verifier.initVerify(cert);
		verifier.update(cpub.toByteArray());
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
		byte[] macTextByte = computeHmac(plainText, smac.serverMAC.getEncoded());

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
    		
    		byte[] msgMac = computeHmac(msg, smac.clientMAC.getEncoded());
    		
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
	
	public static byte[] readFileAsString(String fileName)throws Exception 
	{  
	    return Files.readAllBytes(Paths.get(fileName)); 
	} 

}




