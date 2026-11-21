import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

//rename the class with your name
//use a package with the next pattern
//	ro.ase.ism.sap.lastname.firstname
public class YourSolution {

    public static String getHex(byte[] values)
    {
        StringBuilder sb = new StringBuilder();
        for(byte b:values)
        {
            sb.append(String.format("%02X",b));
        }
        return sb.toString();
    }

    // 1. Step 1: return your file name
    public static String findFile(String hash) throws NoSuchAlgorithmException, IOException {

        //read from folder -> MD content of every file -> compare with given value
        File folder = new File("safecorp_messages");

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        File[] files = folder.listFiles();

        for(File file:files)
        {
            FileInputStream fis = new FileInputStream(file);

            byte[] input = fis.readAllBytes();
            byte[] fileHash = md.digest(input);

            if(getHex(fileHash).equals(hash))
            {
                System.out.println("File name:"+file.getName());
                return file.getName();
            }
        }

        return "";
    };

    // 2. Step 2: Generate HMAC for Authentication
    public static void generateHMAC(String filename, String sharedSecret) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
          File file = new File("safecorp_messages/"+filename);

          FileInputStream fis = new FileInputStream(file);
          byte[] buffer = fis.readAllBytes();

          Mac hmac = Mac.getInstance("HmacSHA256");
          SecretKeySpec keySpec = new SecretKeySpec(sharedSecret.getBytes(),"HmacSHA256");
          hmac.init(keySpec);

          BufferedWriter bw = new BufferedWriter(new FileWriter("hmac.txt"));
          bw.write(getHex(hmac.doFinal(buffer)));

          bw.close();
          fis.close();

    }

    // 3. Step 3: Derive Key with PBKDF2
    public static byte[] deriveKeyWithPBKDF2(
            String password, int noIterations, int keySize) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        String salt ="asdfghjkl";
        FileWriter fw = new FileWriter("salt.txt");
        fw.write(salt);
        fw.close();

        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(),salt.getBytes(),noIterations,keySize);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");


        return keyFactory.generateSecret(pbeKeySpec).getEncoded();
    }

    // 4. Step 4: Encrypt File with AES and Save IV
    public static void encryptFileWithAES(String filename, byte[] key) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        File file = new File("safecorp_messages/"+filename);
        File output = new File("encrypted.txt");
        if(!file.exists())
            output.createNewFile();

        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(output);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key,"AES");

        byte[] iv = new byte[cipher.getBlockSize()];
        //The IV must have the 11th byte from left to right with the 8th bit set to 1.
        iv[10] = (byte)0b00000001;
        IvParameterSpec ivParameterSpec =new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivParameterSpec);

        byte[] buffer = fis.readAllBytes();
        byte[] result = cipher.doFinal(buffer);

        fos.write(Base64.getEncoder().encodeToString(result).getBytes());

        File ivFile = new File("iv.txt");
        if(!ivFile.exists())
            ivFile.createNewFile();
        FileOutputStream fosIv = new FileOutputStream(ivFile);
        fosIv.write(Base64.getEncoder().encodeToString(iv).getBytes());

        fis.close();
        fos.close();
        fosIv.close();


    }

    // 5. Step 5: Encrypt with 3DES for Archival
    public static void encryptWith3DES(String filename, byte[] key) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        File file = new File("safecorp_messages/"+filename);
        if(!file.exists())
            throw new RuntimeException("╰(艹皿艹 )(╬▔皿▔)╯╰(艹皿艹 )(╬▔皿▔)╯");
        File output = new File("archived.sec");
        if(!file.exists())
            output.createNewFile();
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(output);

        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(key,"DESede"));

        byte[] buffer = fis.readAllBytes();
        fos.write(cipher.doFinal(buffer));

        fis.close();
        fos.close();

    }

    // 6. Step 6: Apply Cyclic Bitwise Shift
    public static void applyCyclicShift(String filename) throws IOException {

        //Perform a cyclic (circular) left shift by 2 bits on each byte of the file contents
        File file = new File(filename);
        if(!file.exists())
            throw new RuntimeException("╰(艹皿艹 )(╬▔皿▔)╯╰(艹皿艹 )(╬▔皿▔)╯");
        File output = new File("obfuscated.txt");
        if(!output.exists())
            output.createNewFile();
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(output);

        byte[] buffer = fis.readAllBytes();
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) ((byte)((buffer[i]&0xff)<<2)|((buffer[i]&0xff)>>>6));
        }

        fos.write(buffer);

        fis.close();
        fos.close();

    }

    public static void main(String[] args) {

        String hash = "48BF79DDBE777CEBAD3A9A7961523C1F101BD640B0CD8E8923B1D853883C6B7A"; //copy it from the given Excel file
        String sharedSecret = ";N6./CrzJ[PD"; //copy it from the given Excel file
        int noIterations = 84064; //copy it from the given Excel file

        try {
            // 1. Step 1
            String filename = findFile(hash);

            // 2. Step 2: Generate HMAC for Authentication
            generateHMAC(filename, sharedSecret);

            int keySize = 128;
            byte[] key;
            // 3. Step 3: Derive Key with PBKDF2
            key = deriveKeyWithPBKDF2(sharedSecret, noIterations, keySize);

            // 4. Step 4: Encrypt File with AES and Save IV
            encryptFileWithAES(filename, key);

            // 5. Step 5: Encrypt with 3DES for Archival
            keySize = 192;
            key = deriveKeyWithPBKDF2(sharedSecret, noIterations, keySize);
            encryptWith3DES(filename, key);

            // 6. Step 6: Apply Cyclic Bitwise Shift
            applyCyclicShift("encrypted.txt");

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

