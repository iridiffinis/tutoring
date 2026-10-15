import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        //1-identify modified file from folder => has the key
        File fileM = findFile("system32","sha2Fingerprints.txt");
        System.out.println("File with key: "+fileM.getName());

    }
}
