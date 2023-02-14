package yes;
import java.io.*;
import java.util.*;
public class gamblingaddict {
    static HashMap<String,HashMap<String, String>> map;

    public gamblingaddict() throws IOException {

        map = new HashMap<>();

        retrieveData();

        System.out.printf(map.toString());


    }

    public static HashMap<String,HashMap<String, String>> getMap(){
        return map;
    }

    public static void createUser(String userId){
        map.put(userId, new HashMap<>());
        map.get(userId).put("CurrentCash", "250");
        try {
            File fileOne=new File("fileone");
            FileOutputStream fos=new FileOutputStream(fileOne);
            ObjectOutputStream oos=new ObjectOutputStream(fos);

            oos.writeObject(map);
            oos.flush();
            oos.close();
            fos.close();
        } catch(Exception e) {}

    }

    public void retrieveData(){
        //read from file
        try {
            File toRead=new File("fileone");
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            map=(HashMap<String,HashMap<String, String>>)ois.readObject();

            ois.close();
            fis.close();
            /*print All data in MAP
            for(Map.Entry<String,String> m :mapInFile.entrySet()){
                System.out.println(m.getKey()+" : "+m.getValue());
            }
             */
        } catch(Exception e) {}
    }

    public void clear() throws IOException {
        File fileone =new File("fileone");
        if(fileone.delete()){
            fileone.createNewFile();
        }else{
            //throw an exception indicating that the file could not be cleared
        }

        map.put("test", new HashMap<>());
        map.get("test").put("test", "test");
        try {
            File fileOne=new File("fileone");
            FileOutputStream fos=new FileOutputStream(fileOne);
            ObjectOutputStream oos=new ObjectOutputStream(fos);

            oos.writeObject(map);
            oos.flush();
            oos.close();
            fos.close();
        } catch(Exception e) {}

    }

}
