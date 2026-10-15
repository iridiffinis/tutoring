package ro.ase.ism.sap.exam.student;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.BitSet;

public class YourSolution {

    // Use this static variables to hardcode algorithm names and other important values
    private static final String HASH_ALGORITHM = "MD5";
    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static final String SHARED_SECRET = "ZsEE\";t1hFh91234"; // Secret key for HMAC authentication from the Excel file
    private static final String AES_ALGORITHM = "";
    private static final String FOLDER_PATH = "messages";
    private static final String DIGEST_FOLDER_PATH = "digests";
    private static final String HMAC_FOLDER_PATH = "hmacs";


    // Step 1: Generate Digest values of all the files from the given folder
    public static void generateFilesDigest(String folderPath) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        File messagesFolder = new File(folderPath);
        File[] files = messagesFolder.listFiles();
        for (File file : files) {
            String fileName = file.getName().split("\\.")[0]; // abc.txt -> abc
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            byte[] digestValue = digest.digest(fileBytes);
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(DIGEST_FOLDER_PATH + "/" + fileName + ".digest")))
            {
                for(byte b : digestValue)
                {
                    bw.write(String.format("%02x", b));
                }
            }
        }
    }

    // Step 2: Generate HMAC-SHA256 authentication code
    public static void generateFilesHMAC(String folderPath, String secretKey) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        File messagesFolder = new File(folderPath);
        File[] files = messagesFolder.listFiles();
        for (File file : files) {
            String fileName = file.getName().split("\\.")[0]; // abc.txt -> abc
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            byte[] hmacValue = mac.doFinal(fileBytes);
            Base64.Encoder encoder = Base64.getEncoder();
            String hmacString = encoder.encodeToString(hmacValue);
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(HMAC_FOLDER_PATH + "/" + fileName + ".hmac")))
            {
                bw.write(hmacString);
            }
        }
    }


    // Step 3: Decrypt and verify the document
    public static boolean retrieveAndVerifyDocument(String file, String hashFile, String hmacFile, String secretKey) throws Exception {
        // Verify HMAC and digest for the given file
        // Return true if the files has not been changed
        File messageToBeChecked = new File(FOLDER_PATH+"/"+file); // -> messages/message_2_igt9k8.txt\
        try(FileInputStream fis = new FileInputStream(messageToBeChecked))
        {
            byte[] fileContent = fis.readAllBytes();
            MessageDigest md5 = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] digestValue = md5.digest(fileContent);
            StringBuilder sb = new StringBuilder();
            for (byte b : digestValue) {
                sb.append(String.format("%02x", b));
            }
            String hexDigest = sb.toString(); // a7005a45e6a78288b9ffac9757613d7c
            Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_ALGORITHM);
            hmac.init(secretKeySpec);
            byte[] hmacValue = hmac.doFinal(fileContent);
            Base64.Encoder encoder = Base64.getEncoder();
            String base64Hmac = encoder.encodeToString(hmacValue); // YSfBM6tQWzn68Lv6bgt2nHFiHzU=
            try(BufferedReader br = new BufferedReader(new FileReader(HMAC_FOLDER_PATH+"/"+hmacFile)))
            {
                String hmacFromFile = br.readLine();
                try(BufferedReader br2 = new BufferedReader(new FileReader(DIGEST_FOLDER_PATH+"/"+hashFile)))
                {
                    String digestFromFile = br2.readLine();
                    if(hexDigest.equals(digestFromFile) && base64Hmac.equals(hmacFromFile))
                        return true;
                }
            }
        }

        return false;
    }

    // Step 4: Generate AES key from the shared secret. See Excel for details
    public static byte[] generateSecretKey(String sharedSecret) throws Exception {
        byte[] shareSecretBytes = sharedSecret.getBytes();
        BitSet bitSet = BitSet.valueOf(shareSecretBytes);
        bitSet.flip(13*8+4);
        // vector -> 0011101110101010110100101011101010101110111010101010101010
        return bitSet.toByteArray();
    }


    // Step 5: Encrypt document with AES and received key
    public static void encryptDocument(String filePath, byte[] key) throws Exception {
        String fileName = filePath.split("\\.")[0]; // abc.txt -> abc
        byte[] fileContent = Files.readAllBytes(Path.of(FOLDER_PATH,filePath));
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedContent = cipher.doFinal(fileContent);
        Files.write(Path.of(fileName), encryptedContent);
    }


    public static void main(String[] args) {

        try {
            // Step 1: Generate and store file digest
            generateFilesDigest(FOLDER_PATH);

            // Step 2: Generate and store HMAC for file authentication
            generateFilesHMAC(FOLDER_PATH, SHARED_SECRET);

            String filename = "message_2_igt9k8.txt"; //choose any message.txt file from the folder and test it
            String hashFile = "message_2_igt9k8.digest"; //the corresponding hash file
            String hmacFile = "message_2_igt9k8.hmac"; //the corresponding hmac file

            // Step 3: Verify the document
            if (retrieveAndVerifyDocument(filename, hashFile, hmacFile, SHARED_SECRET)) {
                System.out.println("Document retrieved successfully. Integrity verified.");
            } else {
                System.out.println("Document verification failed!");
            }

            //Step 3: Change the file content and re-check it to be sure your solution is correct


            // Step 4: Get the derived key
            byte[] derivedKey = generateSecretKey(SHARED_SECRET);

            // Step 5: Encrypt the document
            encryptDocument(filename, derivedKey);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
