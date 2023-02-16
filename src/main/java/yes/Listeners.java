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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;


public class Listeners extends ListenerAdapter {

    public String openaikey = "bearer token here";

    public Listeners() throws IOException {
    }

    public int random(int limit){
        return (int) (Math.random() * limit + 1);
    }

    public EmbedBuilder createEmbed(){
        return new EmbedBuilder();
    }


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

    public static int subtract5hrs(int hours){
        for(int i = 0; i < 5; i++){
            hours--;
            if(hours < 0) hours = 23;
        }
        return hours;
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

    gamblingaddict config = new gamblingaddict();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) { // wtf
        //String msgId = "";
        if(!event.getAuthor().isBot()){
            if(event.getMessage().getContentRaw().equalsIgnoreCase("!register")){

                for(HashMap.Entry<String, HashMap<String, String>> entry : config.getMap().entrySet()) {
                    String key = entry.getKey();
                    if(key.equalsIgnoreCase(event.getAuthor().getId())){
                        EmbedBuilder embed = createEmbed();
                        embed.setFooter("sent at " + getFormattedTime() );
                        embed.setTitle("User " + event.getAuthor().getName() + " has already registered!");
                        embed.setColor(Color.RED);
                        embed.setDescription("do !stats to view your stats.");
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        return;
                    }
                }





                config.createUser(event.getAuthor().getId());

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(event.getAuthor().getName() + " has been registered.");
                embed.setThumbnail(event.getAuthor().getAvatarUrl());
                embed.setDescription("your registered id: ||" + event.getAuthor().getId() + "||");
                embed.addField(
                        new MessageEmbed.Field("Starting cash:", config.getMap().get(event.getAuthor().getId()).get("CurrentCash"), false));
                embed.setColor(Color.WHITE);
                embed.setFooter("sent at " + getFormattedTime() ); //"https://cdn.discordapp.com/attachments/975541046329114654/1074523115897495562/image_2.png"
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }

            if(!config.getMap().containsKey(event.getAuthor().getId())){
                return;
            }

            // update messagecount

            config.addMsgCount(event.getAuthor().getId());

            // RNG COIN DROPS
            EmbedBuilder embed2 = new EmbedBuilder();
            embed2.setColor(Color.ORANGE);
            embed2.setTitle("Random coin drop!");
            embed2.setThumbnail("https://cdn.discordapp.com/attachments/975541046329114654/1075236938363179018/Empty-Gold-Coin-Transparent.png");

                int random = random(200);
            if(random == 1){
                config.changecoins(event.getAuthor().getId(), 80);
                embed2.setDescription("**" + event.getAuthor().getName() + "** has found **" + 80 + "** coins!");
                event.getChannel().sendMessageEmbeds(embed2.build()).queue();
            }
            if(random == 2 || random == 3){
                config.changecoins(event.getAuthor().getId(), 60);
                embed2.setDescription("**" + event.getAuthor().getName() + "** has found **" + 60 + "** coins!");
                event.getChannel().sendMessageEmbeds(embed2.build()).queue();
            }
            if(random == 5 || random == 6 || random == 7){
                config.changecoins(event.getAuthor().getId(), 40);
                embed2.setDescription("**" + event.getAuthor().getName() + "** has found **" + 40 + "** coins!");
                event.getChannel().sendMessageEmbeds(embed2.build()).queue();
            }

            // RNG DROPS END



            if(event.getMessage().getContentRaw().equalsIgnoreCase("!stats")){
                EmbedBuilder yes = createEmbed();
                yes.setTitle(event.getAuthor().getName() + "'s statistics");
                yes.setDescription("Coin balance: **" + config.getMap().get(event.getAuthor().getId()).get("CurrentCash") + "**.");
                yes.setColor(Color.WHITE);
                yes.addField(new MessageEmbed.Field("Date registered:", config.getMap().get(event.getAuthor().getId()).get("CreationDate") + ".", false));
                yes.addField(new MessageEmbed.Field("Messages sent:", config.getMap().get(event.getAuthor().getId()).get("MessageCount") + ".", false));
                yes.setThumbnail(event.getAuthor().getAvatarUrl());
                yes.setFooter("sent at " + getFormattedTime() + " || statistics are specific to this bot. || niooi#2923");
                event.getChannel().sendMessageEmbeds(yes.build()).queue();
            }

            if(event.getChannel().getId().equals("1074562861407416410")){ //no life server
                return;
            }

            if(event.getChannel().getId().equals("1041392248073494618")){ //brainbert server
                return;
            }

            String msgSent = event.getMessage().getContentRaw();
            int sindex1 = msgSent.indexOf(" ");
            int sindex2 = msgSent.indexOf(" ", msgSent.indexOf(" ") + 1);
            int sindexlast = msgSent.lastIndexOf(" ");
            String mention = event.getAuthor().getAsMention();
            String name = event.getAuthor().getName();
            String authorcoins = "";


            if(config.getMap().keySet().contains(event.getAuthor().getId())){
                authorcoins = config.getMap().get(event.getAuthor().getId()).get("CurrentCash");
            }

            if(event.getAuthor().getId().equals("381851699763216386")){ // my id for admin commands



            }




            if(msgSent.equals("!wipe")){
                EmbedBuilder embed = createEmbed();
                embed.setFooter("sent at " + getFormattedTime() );
                embed.setColor(SystemColor.RED);
                embed.setTitle("**⚠ You are about to clear progression data. ⚠**");
                embed.setDescription("Are you sure you want to continue?");
                Button wipe = Button.danger("wipe","Proceed.");
                event.getChannel().sendMessageEmbeds(embed.build()).addActionRow(wipe).queue();
                return;
            }

            if(msgSent.equalsIgnoreCase("bye")){
                event.getChannel().sendMessage("Until you can tear and burn the bible to escape the EVIL ONE, it will be impossible for your educated stupid brain to know that 4 different corner harmonic 24 hour Days rotate simultaneously within a single 4 quadrant rotation of a squared equator and cubed Earth. The Solar system, the Universe, the Earth and all humans are composed of + 0 - antipodes, and equal to nothing if added as a ONE or Entity. All Creation occurs between Opposites. Academic ONEism destroys +0- brain. If you would acknowledge simple existing math proof that 4 harmonic corner days rotate simultaneously around squared equator and cubed Earth, proving 4 Days, Not 1Day,1Self,1Earth or 1God that exists only as anti-side. This page you see - cannot exist without its anti-side existence, as +0- antipodes. Add +0- as One = nothing.").queue();
                event.getChannel().sendMessage("Seek Awesome Lectures, MY WISDOM DEBUNKS GODS OF ALL RELIGIONS AND ACADEMIA.\n" +
                        "\n" +
                        "We have a Major Problem, Creation is Cubic Opposites, 2 Major Corners & 2 Minor. Mom/Dad & Son/Daughter, NOT taught Evil ONEism, which VOIDS Families.").queue();
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).queue();
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).queue();
                event.getChannel().sendMessage("Evil God Believers refuse to acknowledge 4 corner Days rotating simultaneously around 4 quadrant created Earth - in only 1 rotation, voiding the Oneism Evil 1 Day 1 God. You worship Satanic impostor guised by educators as 1 god.\n" +
                        "\n" +
                        "No 1 God equals 4 - 24 hour Days Rotating Simultaneously within 1- 24 hour Rotation of 4 quadrant created Earth. Ignoring 4 Corner Earth Days will Destroy Evil Humanity. I am organizing Children to join \"Cubic Army of 4 Days\" to convert Evil 1 Day Adults to 4 Day mentality existence, to serve perpetual humanity.\n" +
                        "\n" +
                        "\"Nothing on Earth more Evil than a human educated as 1, when composed of opposites that cancel out as an entity.\" In fact, man is the only 1 Evil, and will soon erase himself by ignoring Cubic 4 Day Creation. If a Man cannot tear a page from the bible and burn it - then he cannot be a scientist, or participate in Symposium - to measure Cubing of Earth with Cubic intelligence wiser than any man or god known. Educators have destroyed the human analytical brain to a single perspective, in spite of all creation within Universe being based upon opposites, binaries & antipodes, including Sun/Earth binary relative to the\n" +
                        "\n" +
                        "human male/female binary. No ancient insignificant dead 1 Jew godism can match or exceed the enormity of the Sun/Earth Binary. His heart is not big enough for sharing with the vastness of created opposites. 1 has no heart beat or breath, constituting death of opposites. God in Human form has human\n" +
                        "\n" +
                        "limits as body controls activity. You are taught Evil, You act Evil, You are the Evil on Earth. Only your comprehending the Divinity of Cubic Creation will your soul be saved from your created hell on Earth - induced by your ignoring the existing 4 corner harmonic simultaneous 4 Days rotating in a single cycle of the Earth sphere. Religious/ Academic Pedants cannot allow 4 Days that contradict 1Day 1God. Educators destroy your brain, but you don't know, so why care?").queue();
                event.getChannel().sendMessage("Creation ocurrs via opposites, but Religious/Academia pedants suppress it teaching Satanic One.\n" +
                        "\n" +
                        "After 30 years of research, I now possess the Order of Harmonic Antipodal Cubic Divinity Life - too large for physical form, but Binary Spirit of the masculinity Sun & feminity Earth Antipodes. ONEism is demonic Death Math. I have so much to teach you, but you ignore me you evil asses. You will recognize 4 corner Days or incur Easter Island Ending.\n" +
                        "\n" +
                        "Never a Genius knew Math to achieve my Cubic Wisdom. Cubic thought Reigns as the Highest Intelligence possible on the planet Earth. One 96 hour rotating Cube within a single rotation of Earth -- is an Ineffable Transcendence. Bible and Science falsify 1 corner day for the Cubic 4 corner Days rotating daily. A single god is not possible in our 4 Day Cubic Science, that equates Cubic Divinity. Everybody is both stupid and evil for ignoring the 4 days. Cube Divinity transcends all knowledge, Humans can't escape 4 corner Cubic Life. Fools worship mechanics of language - while they wallow in fictitious & deceitful word. Exact science based on Cubics, not on theories. Wisdom is Cubic testing of knowledge. Academia is progression of Ignorance. No god equals Simultaneous 4 Day Creation. Humans ignore their 4 corner stages of life metamorphosis. This site is a collection of data for a coming book - peruse it. No human has 2 hands as they are opposites, like plus and minus, that cancel as entity. Academia destroys your brain, your ability to think opposite. The eyes of the flounder fish were relocated, why were yours\n" +
                        "\n" +
                        "relocated? Your opposite eyes were moved to 1 corner to overlay for single perspective, but that corrupts your Opposite Brain.").queue();
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).queue();
                event.getChannel().sendMessage(event.getAuthor().getAsMention()).queue();
                event.getChannel().sendFiles(FileUpload.fromData(new File("image.PNG"),"image.png")).queue();
            }

            if(msgSent.toLowerCase().equals("!docs")){

            }


            if(msgSent.equals("test")){

            }

            if(msgSent.equals("!roll")){
                if(config.getMap().get(event.getAuthor().getId()).get("CurrentCash").equals("0")){
                    EmbedBuilder embed = createEmbed();
                    embed.setFooter("sent at " + getFormattedTime() );
                    embed.setColor(Color.black);
                    embed.setTitle("Failed.");
                    embed.setDescription("You cannot roll with a balance of 0!");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    return;
                }
                HashMap<String, String> yes;
                yes = config.roll(event.getAuthor().getId());
                authorcoins = config.getMap().get(event.getAuthor().getId()).get("CurrentCash");
                Optional<String> firstKey = yes.keySet().stream().findFirst();
                if (firstKey.isPresent()) {
                    String key = firstKey.get();
                    EmbedBuilder embed = createEmbed();
                    embed.setFooter("sent at " + getFormattedTime() );
                    if(key.equals("nill")){
                        embed.setTitle("You've gone broke (yoy're broke)");
                        embed.setDescription("Your coins: **" + authorcoins + "**. *(" + yes.get(key) + ")*");
                        embed.setColor(Color.gray);
                        embed.setThumbnail(event.getAuthor().getAvatarUrl());
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        return;
                    }
                    embed.setTitle("You've rolled a " + key + "!");
                    embed.setDescription("Your coins: **" + authorcoins + "**. *(" + yes.get(key) + ")*");
                    if(Integer.parseInt(key) <= 3)
                        embed.setColor(Color.RED);
                    else{
                        embed.setColor(Color.GREEN);
                    }
                    embed.setThumbnail(event.getAuthor().getAvatarUrl());
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                }else{
                    event.getChannel().sendMessage("error rolling......").queue();
                }
                return;

            }

            if(msgSent.equals("!roll10")){
                EmbedBuilder embed = createEmbed();
                int totalchange = 0;
                if(config.getMap().get(event.getAuthor().getId()).get("CurrentCash").equals("0")){
                    embed.setFooter("sent at " + getFormattedTime() );
                    embed.setColor(Color.black);
                    embed.setTitle("Failed.");
                    embed.setDescription("You cannot roll with a balance of 0!");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    return;
                }
                for(int i = 0; i < 10; i++){
                    HashMap<String, String> yes;
                    yes = config.roll(event.getAuthor().getId());
                    authorcoins = config.getMap().get(event.getAuthor().getId()).get("CurrentCash");
                    Optional<String> firstKey = yes.keySet().stream().findFirst();
                    if (firstKey.isPresent()) {
                        String key = firstKey.get();
                        totalchange += Integer.parseInt(yes.get(key));
                        if(key.equals("nill")){
                            embed.setTitle("You've gone broke (yoy're broke)");
                            embed.setDescription("Your coins: **" + authorcoins + "**. *(" + totalchange + ")*");
                            embed.setColor(Color.gray);
                            embed.setThumbnail(event.getAuthor().getAvatarUrl());
                            event.getChannel().sendMessageEmbeds(embed.build()).queue();
                            return;
                        }

                    }



                }
                if(totalchange < 0){
                    embed.setColor(Color.RED);

                }else{
                    embed.setColor(Color.GREEN);
                }

                embed.setTitle("Rolled 10 times!");
                embed.setDescription("Your coins: **" + authorcoins + "**. *(" + totalchange + ")*");
                embed.setThumbnail(event.getAuthor().getAvatarUrl());
                event.getChannel().sendMessageEmbeds(embed.build()).queue();

            }

            if(msgSent.startsWith("!give ")){
                EmbedBuilder embed = createEmbed();
                if(sindex1 == -1 || sindex2 == -1){
                    embed.setColor(Color.RED);
                    embed.setTitle("Incorrect usage.");
                    embed.setDescription("!give @user numberofcoins");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    return;
                }
                String recpid = msgSent.substring(msgSent.indexOf("@") + 1, msgSent.indexOf(">"));
                String authorid = event.getAuthor().getId();
                int change = Integer.parseInt(msgSent.substring(sindexlast + 1));
                embed.setFooter("sent at " + getFormattedTime() );
                if(config.getMap().get(recpid) == null){
                    embed.setColor(Color.RED);
                    embed.setTitle("Could not give coins.");
                    embed.setDescription("Recipient doesn't exist/hasn't registered yet.");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    return;
                }
                if(Integer.parseInt(config.getMap().get(authorid).get("CurrentCash")) - change < 0 || change < 0){
                    embed.setColor(Color.RED);
                    embed.setTitle("You don't have this many coins!");
                    embed.setDescription("Your current balance: **" + authorcoins + "**.");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                    return;
                }
                config.changecoins(recpid, change);
                config.changecoins(authorid, -change);
                authorcoins = config.getMap().get(event.getAuthor().getId()).get("CurrentCash");
                embed.setColor(Color.CYAN);
                Member member = event.getGuild().retrieveMemberById(recpid).complete();
                embed.setTitle("Gave " + change + " coins to " + member.getEffectiveName() + "!");
                embed.setDescription("Your new balance: **" + authorcoins + "**.\n\nRecipient's balance: **" + config.getMap().get(recpid).get("CurrentCash") + "**.");
                embed.setThumbnail(event.getAuthor().getAvatarUrl());
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                return;
            }





            
            if(msgSent.toLowerCase().startsWith("echo")){
                String chnlid = event.getChannel().getId();
                // for later event.getChannel().sendMessage(msgSent.substring(msgSent.indexOf(" ", msgSent.indexOf(" ") + 1))).queue();
                event.getMessage().delete().queue();
                event.getChannel().sendMessage(msgSent.substring(msgSent.indexOf(" "))).queue();

                return;
            }
            /*
            if(msgSent.toLowerCase().startsWith("renameall")){
                for(Member x : event.getGuild().getMember())
                event.getGuild().getMember().modifyNickname()
            }

             */

            if(msgSent.toLowerCase().startsWith("createtextchnl")){


                String categoryID = "";
                String categoryname = msgSent.substring(sindex2 + 1);
                System.out.println(categoryname);
                if(sindex2 == -1){
                    event.getGuild().createTextChannel(msgSent.substring(msgSent.indexOf(" "))).queue();
                } else{
                    String chnlname2 = msgSent.substring(sindex1 + 1, sindex2);
                    //event.getGuild().createCategory(msgSent.substring(sindex1))
                    for(Category x : event.getGuild().getCategories()){
                        if(x.getName().equalsIgnoreCase(categoryname)){
                            categoryID = x.getId();
                            break;
                        }
                    }
                    event.getGuild().createTextChannel(chnlname2, event.getGuild().getCategoryById(categoryID)).queue();
                    if(categoryID.equalsIgnoreCase("")){
                        event.getChannel().sendMessage("channel not found.......").queue();
                    }



                }


            }

            if(msgSent.toLowerCase().startsWith("createvoicechnl")){


                String categoryID = "";
                String categoryname = msgSent.substring(sindex2 + 1);
                System.out.println(categoryname);
                if(sindex2 == -1){
                    event.getGuild().createVoiceChannel(msgSent.substring(msgSent.indexOf(" "))).queue();
                } else{
                    String chnlname2 = msgSent.substring(sindex1 + 1, sindex2);
                    //event.getGuild().createCategory(msgSent.substring(sindex1))
                    for(Category x : event.getGuild().getCategories()){
                        if(x.getName().equalsIgnoreCase(categoryname)){
                            categoryID = x.getId();
                            break;
                        }
                    }
                    event.getGuild().createVoiceChannel(chnlname2, event.getGuild().getCategoryById(categoryID)).queue();



                }


            }

            if(msgSent.toLowerCase().startsWith("deletechnl")){

                String targetname = msgSent.substring(msgSent.indexOf(" ") + 1).toLowerCase();
                System.out.println("made it here!");
                System.out.println(targetname);
                for(GuildChannel chnl : event.getGuild().getChannels()){
                    System.out.println(chnl.getName().toLowerCase());
                    if(chnl.getName().toLowerCase().equals(targetname)){
                        System.out.println("in loop!");
                        chnl.delete().queue();
                        break;
                    }
                }
                return;
            }

            if(msgSent.toLowerCase().startsWith("history")){
                event.getChannel().getHistory().size();
                event.getChannel().sendMessage(event.getChannel().getHistory().toString() + "size: " + event.getChannel().getHistory().size()).queue();
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
                                    //get hour due
                                    int temp = Integer.parseInt(timeString.substring(0, timeString.indexOf(":")));
                                    courseHour = subtract5hrs(temp);
                                    //get day number
                                    courseDay = Integer.parseInt(dateString.substring(dateString.indexOf("/") + 1, dateString.indexOf("/", dateString.indexOf("/") + 1)));
                                    if(courseHour < temp) courseDay -= 1;
                                    //get year number
                                    courseYear = Integer.parseInt(dateString.substring(dateString.indexOf("/", dateString.indexOf("/") + 1) + 1));
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
                event.getMessage().reply("openai is thinking....").queue();

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
                httpConn.setRequestProperty("Authorization", openaikey);

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

        if(event.getButton().getId().equals("wipe")){
            System.out.println("got here...");
            try {
                config.clear();
                event.reply("data cleared!").queue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

    }

    //public void

}
