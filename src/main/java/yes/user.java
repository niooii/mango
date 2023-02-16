public class user{

private String id;
private double money;
private String name;
private String creationdate;
private int msgcount;
private int earnedrewards;

public user(id){
this.id = id;
money = 250;
//initialize name
//initialize creationdate within this class
msgcount = 0;
earnedrewards = 0;
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
return msg;
} 

//end of accessor methods

public void changeCoins(){

} 

} 
