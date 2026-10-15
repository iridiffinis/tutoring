
package ism.sap.exam.lastname.firstname;


import java.nio.charset.StandardCharsets;

public class YourJavaSolution {

    public static void main(String[] args) {
        try {
            // === Input file names ===
            String fragmentsFolder   = "vault_fragments";
            String referenceHashFile = "reference_hash.txt";
            String saltAesFile       = "salt_aes.bin";
            String payloadEncFile    = "payload.enc";

            // === Output file names ===
            String fragmentAuthFile  = "fragment.auth";
            String payloadTxtFile    = "payload.txt";

            // ============================================
            // STEP 1A: Find the correct fragment filename
            // ============================================
            System.out.println("=== STEP 1A ===");
            String fragmentName = step1FindFile(fragmentsFolder, referenceHashFile);
            System.out.println("Selected fragment: " + fragmentName);

            // ============================================
            // STEP 1B: Read fragment content
            // ============================================
            System.out.println("=== STEP 1B: Read fragment content ===");
            byte[] fragmentContentBytes = step1ReadFileContent(fragmentsFolder, fragmentName);
            String keyPartA = new String(fragmentContentBytes, StandardCharsets.UTF_8).trim();
            System.out.println("KEY_PART_A (from fragment) = " + keyPartA);

            // ============================================
            // STEP 2 Compute HMAC and store it as hex string in fragmentAuthFile 
            // ============================================
            System.out.println("=== STEP 2 ===");
            
            String macKey = "SuperSecretKey2025";
            
            step2Solution(fragmentContentBytes, macKey, fragmentAuthFile);
            System.out.println("HMAC saved to: " + fragmentAuthFile);

            // ============================================
            // STEP 3
            // ============================================
            int noIterations = 10000;
            String password = "Pa$$w0rd!";
            
            System.out.println("=== STEP 3 ===");
            byte[] aesKey = step3Solution(password, noIterations, saltAesFile);
            System.out.println("AES key length = " + aesKey.length + " bytes");

            // ============================================
            // STEP 4: Bitwise transform on each key byte
            // ============================================
            System.out.println("=== STEP 4 ===");
            byte[] transformedKey = step4Solution(aesKey);

            // ============================================
            // STEP 5: AES-CBC decryption using transformed key
            // ============================================
            
            String ivValue = "rVRI0aqx8OJfNRD9q3z+iQ==";
            
            System.out.println("=== STEP 5 ===");
            byte[] payloadPlaintext = step5Solution(payloadEncFile, ivValue, transformedKey, payloadTxtFile);
            String keyPartB = new String(payloadPlaintext, StandardCharsets.UTF_8).trim();
            System.out.println("KEY_PART_B (from payload.txt) = " + keyPartB);

            // ============================================
            // STEP 6: Final Value Reconstruction
            // ============================================
            System.out.println("=== STEP 6 ===");
            String flag = step6Solution(keyPartA, keyPartB);
            System.out.println("FLAG = " + flag);

            System.out.println("=== ALL STEPS COMPLETED (if implementations are correct) ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ============================================================
    // Helper methods (use it if you want)
    // ============================================================

    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }

    // ============================================================
    // STEP 1A
    // ============================================================
    public static String step1FindFile(String folder, String referenceHashFile) throws Exception {
        // TODO:
        // When match is found, return that filename
        // If no match is found, throw an Exception.

        throw new UnsupportedOperationException("STEP 1A not implemented yet");
    }

    // ============================================================
    // STEP 1B Read selected fragment content
    // ============================================================

    public static byte[] step1ReadFileContent(String folderName, String filename) throws Exception {
        // Read all bytes from that file and return them.

        throw new UnsupportedOperationException("STEP 1B not implemented yet");
    }

    // ============================================================
    // STEP 2 HMAC
    // ============================================================
    public static void step2Solution(byte[] fragmentContent,
                                     String macKey,
                                     String outputAuthFile) throws Exception {
        // TODO:
        // Write HMAC value as a hex string to outputAuthFile.
        throw new UnsupportedOperationException("STEP 2 not implemented yet");
    }

    // ============================================================
    // STEP 3 PBKDF2
    // ============================================================
    public static byte[] step3Solution(String password,
                                       int iterations,
                                       String saltFile) throws Exception {
        //Return aesKey as byte array.

        throw new UnsupportedOperationException("STEP 3 not implemented yet");
    }

    // ============================================================
    // STEP 4 Cyclic right shift
    // ============================================================
    public static byte[] step4Solution(byte[] aesKey) throws Exception {
        throw new UnsupportedOperationException("STEP 4 not implemented yet");
    }

    // ============================================================
    // STEP 5 AES decryption using transformed key
    // ============================================================
    public static byte[] step5Solution(String payloadEncFile,
                                       String ivValue,
                                       byte[] transformedKey,
                                       String outputPayloadTxt) throws Exception {
        // TODO:
        // Write plaintext (UTF-8) to outputPayloadTxt.
        // Return plaintext as byte array.

        throw new UnsupportedOperationException("STEP 5 not implemented yet");
    }

    // ============================================================
    // STEP 6 Final Value reconstruction
    // ============================================================
    public static String step6Solution(String keyPartA, String keyPartB) throws Exception {
        // TODO:
        // Concatenate keyPartA + keyPartB and return the value
        throw new UnsupportedOperationException("STEP 6 not implemented yet");
    }



}
