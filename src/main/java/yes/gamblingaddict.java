package yes;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class gamblingaddict {

    public static String getSimpleDate(){
        SimpleDateFormat dnt = new SimpleDateFormat("MM/dd/yy");
        java.util.Date date = new Date();
        return dnt.format(date);
    }

    public static String getTime(){
        SimpleDateFormat dnt = new SimpleDateFormat("HH:mm");
        java.util.Date date = new Date();
        return dnt.format(date);
    }

    public static String getFormattedTime(){
        int hours = Integer.parseInt(getTime().substring(0, getTime().indexOf(":")));
        String pmam = "AM";
        if(hours > 12){
            hours -= 12;
            pmam = "PM";
        }
        return hours + getTime().substring(getTime().indexOf(":")) + " " + pmam;
    }

    public int random(int limit){
        return (int) (Math.random() * limit + 1);
    }

    public boolean changecoins(String user, int change){
        if(Integer.parseInt(map.get(user).get("CurrentCash")) + change < 0){
            return false;
        }
        map.get(user).replace("CurrentCash", "" + (Integer.parseInt(map.get(user).get("CurrentCash")) + change));
        writedata();
        System.out.println("Current cash:" + map.get(user).get("CurrentCash"));
        return true;
    }

    public HashMap<String, String> roll(String user){
        HashMap<String, String> hm = new HashMap<>();
        System.out.println(user);
        if(random(200) == 33){
            changecoins(user, 8000);
            hm.put("33", "+4000");
            return hm;
        }
        if(random(50) == 33){
            changecoins(user, 2000);
            hm.put("33", "+800");
            return hm;
        }
        switch (random(6)) {
            case 1 -> {
                if(changecoins(user, -175)){
                    hm.put("1", "-175");
                    return hm;
                } else{
                    map.get(user).replace("CurrentCash", "0");
                    hm.put("nill", "-175");
                }
            }
            case 2 -> {
                if(changecoins(user, -90)){
                    hm.put("2", "-90");
                    return hm;
                } else{
                    map.get(user).replace("CurrentCash", "0");
                    hm.put("nill", "-90");
                }
            }
            case 3 -> {
                if(changecoins(user, -35)){
                    hm.put("3", "-35");
                    return hm;
                } else{
                    map.get(user).replace("CurrentCash", "0");
                    hm.put("nill", "-35");
                }
            }
            case 4 -> {
                changecoins(user, 20);
                hm.put("4", "+20");
                return hm;
            }
            case 5 -> {
                changecoins(user, 60);
                hm.put("5", "+60");
                return hm;
            }
            case 6 -> {
                changecoins(user, 120);
                hm.put("6", "+120");
                return hm;
            }
        }
        hm.put("bruh", "???????");
        return hm;
    }
    static ConcurrentHashMap<String,HashMap<String, String>> map;

    public gamblingaddict() throws IOException {

        map = new ConcurrentHashMap<>();

        retrieveData();

        System.out.printf(map.toString());


    }

    public static ConcurrentHashMap<String,HashMap<String, String>> getMap(){
        return map;
    }

    public static void writedata(){
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

    public static void createUser(String userId){
        map.put(userId, new HashMap<>());
        map.get(userId).put("CurrentCash", "250");
        map.get(userId).put("MessageCount", "0");
        map.get(userId).put("CreationDate", "Joined on " + getSimpleDate() + " at " + getFormattedTime());
        writedata();

    }

    public static void addMsgCount(String userid){
        map.get(userid).replace("MessageCount", "" + (Integer.parseInt(map.get(userid).get("MessageCount")) + 1));
        return;
    }

    public void retrieveData(){
        //read from file
        try {
            File toRead=new File("fileone");
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            map=(ConcurrentHashMap<String,HashMap<String, String>>)ois.readObject();

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
        for(HashMap.Entry<String, HashMap<String, String>> entry : map.entrySet()) {
            String key = entry.getKey();
            map.remove(key);
        }
        map.put("test", new HashMap<>());
        map.get("test").put("test", "test");

        writedata();

    }

}
