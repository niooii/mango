package yes;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseWork;
import com.google.api.services.classroom.model.ListCourseWorkResponse;
import com.google.api.services.classroom.model.ListCoursesResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Listeners extends ListenerAdapter {

    public static String getDate(){
        SimpleDateFormat dnt = new SimpleDateFormat("MM/dd/yy:HH:mm");
        java.util.Date date = new Date();
        return dnt.format(date);
    }

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

    public static String getFormattedTime2(String time){
        int hours = Integer.parseInt(time.substring(0, time.indexOf(":")));
        String pmam = "AM";
        for(int i = 0; i < 5; i++){
            hours--;
            if(hours < 0) hours = 23;
        }
        if(hours > 12){
            hours -= 12;
            pmam = "PM";
        }
        System.out.println("time being formatted: " + time);
        System.out.println("hours: " + hours);
        return hours + time.substring(getTime().indexOf(":")) + " " + pmam;
    }

    public String formatDate(String date){
        String finaldate = "";

        Object obj=JSONValue.parse(date);
        JSONObject jobj = (JSONObject) obj;
        if(jobj.get("day") == null || jobj.get("month") == null || jobj.get("year") == null){
            return "**BAD DATE**";
        }
        String day = jobj.get("day").toString();
        String month = jobj.get("month").toString();
        String year = jobj.get("year").toString();

        finaldate = month + "/" + day + "/" + year;

        return finaldate;
    }

    public String formatTime(String date){
        String finaltime = "";

        Object obj=JSONValue.parse(date);
        JSONObject jobj = (JSONObject) obj;
        if(jobj.get("hours") == null){
            return "no time provided.";
        }
        if(jobj.get("minutes") == null){
            String hours = jobj.get("hours").toString();
            finaltime = hours;
            return hours + ":00";
        } else {
            String hours = jobj.get("hours").toString();
            String minutes = jobj.get("minutes").toString();

            finaltime = hours + ":" + minutes;

            return finaltime;
        }
    }
    
    //google api copied from google bc im professional copy paster

    private static final String APPLICATION_NAME = "Google Classroom API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static List<String> SCOPES =
            Collections.singletonList(ClassroomScopes.CLASSROOM_COURSES_READONLY + " " + ClassroomScopes.CLASSROOM_COURSEWORK_ME_READONLY + " " + ClassroomScopes.CLASSROOM_STUDENT_SUBMISSIONS_STUDENTS_READONLY + " " + ClassroomScopes.CLASSROOM_COURSEWORK_ME_READONLY + " " + ClassroomScopes.CLASSROOM_ANNOUNCEMENTS_READONLY + " " + ClassroomScopes.CLASSROOM_PROFILE_PHOTOS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = ClassroomQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


    boolean stopwatchallowed = true;
    boolean breakk = false;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String msgId = "";
        if(!event.getAuthor().isBot()){
            String msgSent = event.getMessage().getContentRaw();
            String mention = event.getAuthor().getAsMention();
            String name = event.getAuthor().getName();
            
            if(msgSent.toLowerCase().startsWith("echo")){
                String chnlid = event.getChannel().getId();
                // for later event.getChannel().sendMessage(msgSent.substring(msgSent.indexOf(" ", msgSent.indexOf(" ") + 1))).queue();
                event.getMessage().delete().queue();
                event.getChannel().sendMessage(msgSent.substring(msgSent.indexOf(" "))).queue();

                return;
            }

            if(msgSent.contains("assignments")){
                event.getChannel().sendMessage("fetching assignments...").queue();
                final NetHttpTransport HTTP_TRANSPORT;
                try {
                    HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                } catch (GeneralSecurityException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Classroom service =
                        null;
                try {
                    service = new Classroom.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                            .setApplicationName(APPLICATION_NAME)
                            .build();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // List the first 10 courses that the user has access to.
                ListCoursesResponse response = null;
                try {
                    response = service.courses().list()
                            .setPageSize(10)
                            .execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                List<Course> courses = response.getCourses();

                ListCourseWorkResponse response1 = null;
                try {
                    response1 = service.courses().courseWork().list(courses.get(0).getId())
                            .execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                List<CourseWork> coursework = response1.getCourseWork();


                String dateString = "";
                String timeString = "";
                //print out coursework name
                System.out.println(coursework.get(0).getTitle());
                //get month number
                int courseMonth = -1;
                //System.out.println(courseMonth);
                //get day number
                int courseDay = -1;
                //System.out.println(courseDay);
                //get year number
                int courseYear = -1;
                //System.out.println(courseYear);
                //get hour due
                int courseHour = -1;
                //System.out.println(courseHour);
                //get minute due
                int courseMinute = -1;
                //System.out.println(courseMinute);

                //get system times "MM/dd/yy:HH:mm"
                System.out.println("retreiving system time");
                int systemMonth = -1;
                //System.out.println(systemMonth);
                //get day number
                int systemDay = -1;
                //System.out.println(systemDay);
                //get year number
                int systemYear = -1;
                //System.out.println(systemYear);
                //get hour
                int systemHour = -1;
                //System.out.println(systemHour);
                //get minute
                int systemMinute = -1;
               //System.out.println(systemMinute);



                /*
                for(CourseWork x : coursework) {
                    if(formatDate(x.getDueDate().toString()) == yes){

                    }
                }
                 */
                List<CourseWork> alist = new ArrayList<CourseWork>();


                ConcurrentHashMap<String, List<CourseWork>> assignmentMap = new ConcurrentHashMap<>();
                for (Course cours : courses) {
                    try {
                        assignmentMap.put(cours.getName(), service.courses().courseWork().list(cours.getId())
                                .execute().getCourseWork());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                //removes missing assignments.

                for(HashMap.Entry<String, List<CourseWork>> entry : assignmentMap.entrySet()) {
                    String key = entry.getKey();
                    for(int i = assignmentMap.get(key).size() - 1; i >= 0; i--) {

                            if (true) { // previous condition: assignmentMap.get(key).get(i).getWorkType().equals("ASSIGNMENT")
                                if (assignmentMap.get(key).get(i).getDueDate() == null){
                                    assignmentMap.get(key).remove(i);
                                }
                                else {
                                    dateString = formatDate(assignmentMap.get(key).get(i).getDueDate().toString());
                                    timeString = formatTime(assignmentMap.get(key).get(i).getDueTime().toString());
                                    //get month number
                                    courseMonth = Integer.parseInt(dateString.substring(0, dateString.indexOf("/")));
                                    //get day number
                                    courseDay = Integer.parseInt(dateString.substring(dateString.indexOf("/") + 1, dateString.indexOf("/", dateString.indexOf("/") + 1)));
                                    //get year number
                                    courseYear = Integer.parseInt(dateString.substring(dateString.indexOf("/", dateString.indexOf("/") + 1) + 1));
                                    //get hour due
                                    courseHour = Integer.parseInt(timeString.substring(0, timeString.indexOf(":")));
                                    //get minute due
                                    courseMinute = Integer.parseInt(timeString.substring(timeString.indexOf(":") + 1));

                                    //get system times "MM/dd/yy:HH:mm"
                                    systemMonth = Integer.parseInt(getDate().substring(0, getDate().indexOf("/")));
                                    //get day number
                                    systemDay = Integer.parseInt(getDate().substring(getDate().indexOf("/") + 1, getDate().indexOf("/", getDate().indexOf("/") + 1)));
                                    //get year number
                                    systemYear = Integer.parseInt(getDate().substring(getDate().indexOf("/", getDate().indexOf("/") + 1) + 1, getDate().indexOf(":")));
                                    //get hour
                                    systemHour = Integer.parseInt(getDate().substring(getDate().indexOf(":") + 1, getDate().indexOf(":", getDate().indexOf(":") + 1)));
                                    //get minute
                                    systemMinute = Integer.parseInt(getDate().substring(getDate().indexOf(":", getDate().indexOf(":") + 1) + 1));

                                    //  FILTER ASSIGNMENTS
                                    if (courseYear < systemYear + 2000) { //if courseyear is less than systemyear
                                        assignmentMap.get(key).remove(i);
                                        System.out.println(key);
                                        System.out.println("assignment removed! Course Year: " + courseYear + " and system Year: " + systemYear);
                                    } else if (courseYear > systemYear + 2000) { //if courseyear is GREATER than systemyear
                                        //do nothing
                                        System.out.println(key);
                                        System.out.println("nothing happened! Course Year: " + courseYear + " and system Year: " + systemYear);
                                    } else if (courseMonth < systemMonth) {
                                        System.out.println(key);
                                        assignmentMap.get(key).remove(i);
                                        System.out.println("assignment removed! Course Month: " + courseMonth + " and system Month: " + systemMonth);
                                    } else if (courseMonth > systemMonth) {
                                        //do nothing
                                        System.out.println(key);
                                        System.out.println("nothing happened! Course Month: " + courseMonth + " and system Month: " + systemMonth);
                                    } else if (courseDay < systemDay) {
                                        assignmentMap.get(key).remove(i);
                                        System.out.println(key);
                                        System.out.println("assignment removed! Course Day: " + courseDay + " and system Day: " + systemDay);
                                    } else if (courseDay > systemDay) {
                                        //do nothing
                                        System.out.println(key);
                                        System.out.println("nothing happened! Course Day: " + courseDay + " and system Day: " + systemDay);
                                    } else if (courseHour < systemHour) {
                                        assignmentMap.get(key).remove(i);
                                        System.out.println(key);
                                        System.out.println("assignment removed! Course Hour: " + courseHour + " and system Hour: " + systemHour);
                                    } else if (courseHour > systemHour) {
                                        //do nothing
                                        System.out.println(key);
                                        System.out.println("nothing happened! Course Hour: " + courseHour + " and system Hour: " + systemHour);
                                    } else if (courseMinute < systemMinute) {
                                        assignmentMap.get(key).remove(i);
                                        System.out.println(key);
                                        System.out.println("assignment removed! Course Minute: " + courseMinute + " and system Minute: " + systemMinute);
                                    } else if (courseMinute > systemMinute) {
                                        //do nothing
                                        System.out.println(key);
                                        System.out.println("nothing happened! Course Minute: " + courseMinute + " and system Minute: " + systemMinute);
                                    }else {
                                        System.out.println(key);
                                        System.out.println("assignment passed!");
                                    }
                                }

                        }
                    }
                }

                for(HashMap.Entry<String, List<CourseWork>> entry : assignmentMap.entrySet()) {
                    String key = entry.getKey();
                    for(CourseWork x : assignmentMap.get(key)) {
                        if (x.getDueDate() != null && x.getDueTime() != null) {
                            System.out.println(x.getTitle() + " due at " + formatDate(x.getDueDate().toString()) + " at " + formatTime(x.getDueTime().toString()));
                        }
                    }
                }




                if (courses == null || courses.size() == 0) {
                    System.out.println("No courses found.");
                } else {
                    System.out.println("Courses:");
                    for (Course course : courses) {
                        System.out.printf("%s\n", course.getName() + " Course ID: " + course.getId());
                    }

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(name + "'s assignments", "https://classroom.google.com/u/1/a/not-turned-in/all");

                    embed.setColor(Color.PINK);
                    embed.setFooter("sent at " + getFormattedTime() ); //"https://cdn.discordapp.com/attachments/975541046329114654/1074523115897495562/image_2.png"
                    int total = 0;
                    for(HashMap.Entry<String, List<CourseWork>> entry : assignmentMap.entrySet()) {
                        String key = entry.getKey();
                        String temp = "";
                        if(assignmentMap.get(key).size() > 0) {
                            for (CourseWork cw : assignmentMap.get(key)) {
                                if (cw.getDueDate() != null && cw.getDueTime() != null) {
                                    temp += "\n\n• __" + cw.getTitle() + "__\n" + "  *DUE: " + formatDate(cw.getDueDate().toString()) + " at " + getFormattedTime2(formatTime(cw.getDueTime().toString())) + ".*";
                                    total++;
                                    //System.out.println(cw.getTitle() + "added. YEAR: " + courseYear);
                                } else {
                                    temp += "\n\n• __" + cw.getTitle() + "__\n" + "  *NO DUE DATE.*";
                                    total++;
                                    //System.out.println(cw.getTitle() + "added.");
                                }
                            }
                            System.out.println("Assignment from " + key);
                            embed.addField(new MessageEmbed.Field("\uD83C\uDF38 -------" + key + "------- \uD83C\uDF38", temp, false));
                        }
                    }
                    embed.setDescription("Total assigned: **" + total + "**" + "\nToday's date: " + getSimpleDate());
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                }
            }


            if(msgSent.startsWith("openai")){
                //event.getChannel().sendMessage("openai response here").queue();

                //event.getChannel().sendMessage(prompt).queue();
                if(msgSent.indexOf(" ") == -1){
                    event.getChannel().sendMessage("try sending a prompt!").queue();
                    return;
                }
                String prompt = msgSent.substring(msgSent.indexOf(" "));

                URL url = null;
                try {
                    url = new URL("https://api.openai.com/v1/completions");
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                HttpURLConnection httpConn = null;
                try {
                    httpConn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    httpConn.setRequestMethod("POST");
                } catch (ProtocolException e) {
                    throw new RuntimeException(e);
                }

                int maxTokens = 4097 - prompt.length();
                String maxTokensString = "" + maxTokens;

                httpConn.setRequestProperty("Content-Type", "application/json");
                httpConn.setRequestProperty("Authorization", "Bearer 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");

                httpConn.setDoOutput(true);
                OutputStreamWriter writer = null;
                try {
                    writer = new OutputStreamWriter(httpConn.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    writer.write("{\n  \"model\": \"text-davinci-003\",\n  \"prompt\": \"}" + prompt + "{\",\n  \"max_tokens\": 4000,\n  \"temperature\": 1\n}");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    writer.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    httpConn.getOutputStream().close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                InputStream responseStream = null;
                try {
                    responseStream = httpConn.getResponseCode() / 100 == 2
                            ? httpConn.getInputStream()
                            : httpConn.getErrorStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Scanner s = new Scanner(responseStream).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";
                System.out.println(response); //IAKUWGUYAYHFWFJWH
                Object obj=JSONValue.parse(response);
                JSONObject jobj = (JSONObject) obj;
                System.out.println(jobj.get("choices"));
                if(jobj.get("choices") == null) {
                    event.getChannel().sendMessage("That model is currently overloaded with other requests. You can retry your request, or contact us through our help center at help.openai.com if the error persists.").queue();
                }
                String bruh = jobj.get("choices").toString();
                Object obj2=JSONValue.parse(bruh);
                JSONArray jobj2 = (JSONArray) obj2;
                System.out.println(jobj2.get(0));//why? why? why? why? why? why?
                String wasf = jobj2.get(0).toString();
                Object HELP = JSONValue.parse(wasf);
                JSONObject HELPME = (JSONObject) HELP;
                System.out.println(HELPME.get("text"));
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(name + "'s openai response");
                embed.setDescription(HELPME.get("text").toString().substring(0, HELPME.get("text").toString().length()-1));
                embed.setColor(Color.BLACK);

                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                //event.getChannel().sendMessage(HELPME.get("text").toString().substring(0, HELPME.get("text").toString().length()-1)).queue();




            }

            // Headers for a request
            //openai api key: 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








            //event.getChannel().sendMessage("msg sent: " + msgSent + " " + mention).queue();
            System.out.println(msgSent);


            if(msgSent.equalsIgnoreCase("hi")){
                event.getChannel().sendMessage("HIIIIIIIIII").queue();
                System.out.println("why doesnt this work. so goofy");
            }

            if(msgSent.equalsIgnoreCase("stopwatch")){
                Button stopthewatch = Button.danger("stop-stopwatch","stop stopwatch");

                if(stopwatchallowed == true) {
                    System.out.println("stopwatch is allowed.");
                    stopwatchallowed = false;

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle(name + "'s stopwatch");
                    eb.setDescription("" + 0 + "s");
                    eb.setColor(Color.CYAN);

                    event.getChannel().sendMessageEmbeds(eb.build()).addActionRow(stopthewatch).queue(message -> {
                        for (int i = 1; i < 60; i++) {
                            if (breakk == false) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                eb.setDescription("" + i + "s");
                                message.editMessageEmbeds(eb.build()).queue();
                            }
                        }
                    });

                    stopwatchallowed = true;
                }else{
                    System.out.println("stopwatch is not allowed.");
                    event.getChannel().sendMessage("THere is already an ongoing stopwtach..........").queue();
                }
            }

            if(msgSent.equalsIgnoreCase("wipe")){
                String cid = event.getChannel().getId();

            }



        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if(event.getName().equals("fart")){
            event.reply("You just farted").queue();
        }

    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if(event.getButton().getId().equals("stop-stopwatch")){

            breakk = true;

        }

    }

    //public void

}
