import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
 * IT&C Security Master Program
 * Secure Application Programming in Java - JCA Exam Template
 *
 * Scenario:
 * You work as a Security Engineer for CyberDefense Solutions.
 * A critical incident report is stored in msg.txt and must be securely prepared
 * for transmission to another organization.
 *
 * Rules:
 * - Do not modify main().
 * - Do not modify method headers.
 * - Do not add new methods.
 * - Implement only the TODO sections inside the given methods.
 */
public class SAPExamTemplate {

    private static final String INPUT_FILE = "msg.txt";
    private static final String HASH_FILE = "hash.bin";
    private static final String ENC_FILE = "enc_msg.aes";
    private static final String SHIFTED_FILE = "shifted_msg.bin";
    private static final String FINAL_HASH_B64_FILE = "final_hash.b64";
    private static final String SIGNATURE_FILE = "signature.sig";

    private static final String PASSWORD = "password12345678";
    private static final String KEYSTORE_FILE = "exam_keystore.jks";
    private static final String KEYSTORE_PASSWORD = "password";
    private static final String KEY_ALIAS = "examkey";

    public static void main(String[] args) throws Exception {

        byte[] hashStep1 = step1(INPUT_FILE, HASH_FILE);
        displayHex(hashStep1);

        step2(INPUT_FILE, hashStep1, ENC_FILE);

        step3(ENC_FILE, SHIFTED_FILE);

        String finalHashBase64 = computeFinalHashBase64(hashStep1, SHIFTED_FILE, FINAL_HASH_B64_FILE);
        System.out.println("====== Final hash value as Base 64: " + finalHashBase64);
        
        signBase64FinalHash(finalHashBase64, KEYSTORE_FILE, KEYSTORE_PASSWORD, KEY_ALIAS, SIGNATURE_FILE);

        System.out.println("Processing completed.");
    }

    public static byte[] step1(String inputFile, String outputFile) throws Exception {
    	// TODO: implement this method based on step 1 requirements
        return null;
    }

    public static void step2(
            String inputFile,
            byte[] hashStep1,
            String outputEncryptedFile) throws Exception {

        // TODO: implement this method based on step 2 requirements
    }

    public static void step3(
            String inputEncryptedFile,
            String outputShiftedFile) throws Exception {

        // TODO: implement this method based on step 3 requirements
    }

    public static String computeFinalHashBase64(
            byte[] hashStep1,
            String shiftedFileName,
            String outputBase64FileName) throws Exception {

        // TODO: implement this method
        return null;
    }

    public static void signBase64FinalHash(
            String finalHashBase64,
            String keyStoreFile,
            String keyStorePassword,
            String keyAlias,
            String outputSignatureFile) throws Exception {

        // TODO: implement this method
    }

    public static void displayHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        System.out.println("====== Hash value: " + sb.toString());
    }

}
