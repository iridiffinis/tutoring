package ro.ase.ism.sap.exam.lastname.firstname;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.*;
import java.util.*;

public class YourNameExam {

    public static void main(String[] args) throws Exception {
        String fingerprintsPath = "fingerprints.txt";
        String system32Path = "system32"; // unzip manually before running
        String encryptedFilePath = "financialdata.enc";
        String decryptedFilePath = "financialdata.txt";
        String responseFilePath = "myresponse.txt";
        String responseFileMacPath = "myresponse_mac.txt";
        String macPassword = "secretism";

        // Step 1
        File modifiedFile = findModifiedFile(fingerprintsPath, system32Path);

        // Step 2
        SecretKey aesKey = extractKeyFromFile(modifiedFile);
        decryptFile(encryptedFilePath, decryptedFilePath, aesKey);

        // Step 3
        String firstIban = extractFirstIban(decryptedFilePath);
        writeToFile(responseFilePath, firstIban);
        generateMac(responseFilePath, responseFileMacPath, macPassword);
    }

    public static File findModifiedFile(String fingerprintsPath, String system32Path) throws Exception {
        return null; // replace with actual File
    }

    public static SecretKey extractKeyFromFile(File modifiedFile) throws Exception {
        return null; // replace with actual key
    }

    public static void decryptFile(String encPath, String outPath, SecretKey key) throws Exception {
        byte[] ivBytes = null;
        // Init iv with required values
        
        // Implement the decryption
    }

    public static String extractFirstIban(String filePath) throws IOException {

    	String IBAN = "";

        // Implement logic to extract the first IBAN from the decrypted file
    	
        return IBAN;
    }

    public static void writeToFile(String path, String content) throws IOException {

    }

    public static void generateMac(String inputFile, String macFile, String macPass) throws Exception {
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02X", b));
        return sb.toString();
    }
}
