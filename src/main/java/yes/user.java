package yes;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.math.*;

public class user implements Serializable {


    private String id;
    private double money;
    private String name;
    private String creationdate;
    private int msgcount;
    private int earnedrewards;

    public user(String id, String name){
        this.id = id;
        money = (int)(Math.random()*550) * 0.87 + 250;
        this.name = name;
        creationdate = "Joined on " + getSimpleDate() + " at " + getFormattedTime() + ".";
        msgcount = 0;
        earnedrewards = 0;
    }

    private static BigDecimal truncate(double x,int numberofDecimals)
    {
        if ( x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
        }
    }

    //accessor methods

    public String getName(){
    return name;
    }

    public double getMoney(){
    return money;
    }

    public String getCreationDate(){
    return creationdate;
    }

    public int getMsgCount(){
    return msgcount;
    }

    //end of accessor methods'

    public void setMoney(double money){
        this.money = money;
    }

    public void addMsgCount(){
        this.msgcount++;
    }

    //time methods

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

public void changeCoins(){

}

}
