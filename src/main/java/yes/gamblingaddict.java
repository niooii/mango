package yes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;

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

    public boolean changecoins(String user, double change){
        if(map.get(user).getMoney() + change < 0){
            return false;
        }
        map.get(user).setMoney(map.get(user).getMoney() + change);
        writedata();
        System.out.println("Current cash:" + map.get(user).getMoney());
        return true;
    }

    public HashMap<String, String> roll(String user){
        HashMap<String, String> hm = new HashMap<>();
        System.out.println(user);
        if(random(200) == 33){
            changecoins(user, 4000);
            hm.put("very rare number", "+4000");
            return hm;
        }
        if(random(50) == 22){
            changecoins(user, 800);
            hm.put("decently rare number", "+800");
            return hm;
        }
        if(random(200) == 11){
            changecoins(user, -4000);
            hm.put("very bad number", "-4000");
            return hm;
        }
        if(random(50) == 22){
            changecoins(user, -800);
            hm.put("bad number", "-800");
            return hm;
        }
        switch (random(6)) {
            case 1 -> {
                if(changecoins(user, -175)){
                    hm.put("1", "-175");
                    return hm;
                } else{
                    map.get(user).setMoney(0.00);
                    hm.put("nill", "-175");
                }
            }
            case 2 -> {
                if(changecoins(user, -90)){
                    hm.put("2", "-90");
                    return hm;
                } else{
                    map.get(user).setMoney(0.00);
                    hm.put("nill", "-90");
                }
            }
            case 3 -> {
                if(changecoins(user, -35)){
                    hm.put("3", "-35");
                    return hm;
                } else{
                    map.get(user).setMoney(0.00);
                    hm.put("nill", "-35");
                }
            }
            case 4 -> {
                changecoins(user, 35);
                hm.put("4", "+35");
                return hm;
            }
            case 5 -> {
                changecoins(user, 90);
                hm.put("5", "+90");
                return hm;
            }
            case 6 -> {
                changecoins(user, 175);
                hm.put("6", "+175");
                return hm;
            }
        }
        hm.put("bruh", "???????");
        return hm;
    }

    public int badroll(String user){
        System.out.println(user);
        if(random(200) == 33){
            return 4000;
        }
        if(random(50) == 22){
            return 800;
        }
        switch (random(6)) {
            case 1, 6 -> {
                    return 175;
            }
            case 2, 5 -> {
                    return 90;
            }
            case 3, 4 -> {
                    return 35;
            }
        }
        return 0;
    }
    static ConcurrentHashMap<String,user> map;

    public gamblingaddict() throws IOException {

        map = new ConcurrentHashMap<>();

        retrieveData();

        System.out.printf(map.toString());


    }

    public static ConcurrentHashMap<String,user> getMap(){
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
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void createUser(String userId, String name){
        map.put(userId, new user(userId, name));
        writedata();

    }

    public static void addMsgCount(String userid){
        map.get(userid).addMsgCount();
    }

    public void retrieveData(){
        //read from file
        try {
            File toRead=new File("fileone");
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            map=(ConcurrentHashMap<String,user>)ois.readObject();

            ois.close();
            fis.close();

            for(Map.Entry<String,user> m :map.entrySet()){
                System.out.println(m.getKey()+" : "+m.getValue());
            }

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public void clear() throws IOException {
        for(HashMap.Entry<String, user> entry : map.entrySet()) {
            String key = entry.getKey();
            map.remove(key);
        }
        map.put("test", new user("test", "test"));

        writedata();

    }

}
