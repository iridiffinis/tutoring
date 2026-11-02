import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static File findFile(String shaFolder, String fingerPrints) throws NoSuchAlgorithmException, IOException {
        File folder = new File(shaFolder);
        File[] files = folder.listFiles();

        MessageDigest sha = MessageDigest.getInstance("SHA-256");

        Map<String,String> shaList = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(fingerPrints));
        String filePath, fileSha;
        while((filePath=br.readLine())!=null && (fileSha=br.readLine())!=null)
        {
           shaList.put(fileSha,filePath);
        }

        for(File file:files)
        {
            if(file.isFile())
            {
                byte[] buffer = Files.readAllBytes(file.toPath());
                byte[] digest = sha.digest(buffer);
                String shaB64 = Base64.getEncoder().encodeToString(digest);
                if(!shaList.containsKey(shaB64))
                {
                    return file;
                }
            }
        }

         return folder;
    }

    public static void decryptFile(String dataFile,String outFile, File keyFile) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        FileInputStream fisKey= new FileInputStream(keyFile);
        byte[] pass = fisKey.readAllBytes();

        Cipher cipher =Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(pass,"AES");

        //iv
        byte[] iv = new byte[cipher.getBlockSize()];
        int length = iv.length;
        iv[length-1]=23;
        iv[length-2]=20;
        iv[length-3]=2;
        iv[length-4]=3;

        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE,keySpec,ivParameterSpec);

        File data =new File(dataFile);
        File output = new File(outFile);

        FileInputStream fisData = new FileInputStream(data);
        FileOutputStream fos = new FileOutputStream(output);

        fos.write(cipher.doFinal(fisData.readAllBytes()));

        fos.close();
        fisData.close();
        fisKey.close();
    }

    public static void writeResponse(String dataFile, String responseFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(dataFile));
        FileWriter fw = new FileWriter(responseFile);

        fw.write(br.readLine());

        fw.close();
        br.close();
    }

    public static KeyStore getKeyStore( String keyStoreFile, String keyStorePass) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        File input = new File(keyStoreFile);
        if(!input.exists())
        {
            throw new UnsupportedOperationException("no ks file");
        }

        FileInputStream fis = new FileInputStream(input);
        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        keyStore.load(fis,keyStorePass.toCharArray());

        fis.close();
        return keyStore;
    }

    public static PrivateKey getPrivateKey( KeyStore keyStore,String alias, String password) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        if(keyStore!=null && keyStore.containsAlias(alias) && keyStore.isKeyEntry(alias))
        {
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias,password.toCharArray());
            return privateKey;
        }else{
            throw new KeyStoreException("error");
        }
    }

    private static void signResponse(String dataFile, String sigFile, PrivateKey privateKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        File input = new File(dataFile);
        File signF = new File(sigFile);

        FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(signF);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);

        byte[] buffer = fis.readAllBytes();
        signature.update(buffer);

        fos.write(signature.sign());

        fis.close();
        fos.close();
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, CertificateException, KeyStoreException, UnrecoverableKeyException, SignatureException {

        //1-identify modified file from folder => has the key
        File fileM = findFile("system32","sha2Fingerprints.txt");
        System.out.println("File with key: "+fileM.getName());

        //2
        decryptFile("financialdata.enc","financialdata.txt",fileM);

        //3
        writeResponse("financialdata.txt","myresponse.txt");
        KeyStore keyStore = getKeyStore("igkeystore.ks","passks");
        PrivateKey privateKey = getPrivateKey(keyStore,"igkey","passIG");
        signResponse("myresponse.txt","DataSignature.ds",privateKey);

    }
}
