package yes;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
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
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.*;
import com.google.api.services.classroom.Classroom;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

public class Main {





    public static void main(String[] args) throws GeneralSecurityException, IOException {


        JDA bot = JDABuilder.createDefault("MTA3NDE2MDMzNzA0NzI1NzE3OA.GdAAr0.4gL4-jZ6U4k7qO8tjEuk9zh8XHEsWRcTWZiT00", GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.playing("piano"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(new Listeners())
                .build();

        boolean stopwatchallowed = true;

        Guild server = bot.getGuildById("1074156189513945179");

        if(server != null){
            server.upsertCommand("fart", "farts really hard").queue();
        }

        //TextChannel general = event.getGuild().getTextChannelById(946147235375251501L);


    }


}