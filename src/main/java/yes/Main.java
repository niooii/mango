package yes;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;

public class Main {





    public static void main(String[] args) throws IOException {


        JDA bot = JDABuilder.createDefault("MTA3NDE2MDMzNzA0NzI1NzE3OA.Gzpuih.WvTxluiFDRwMiSirdAXV6clWDCdb4xaro4dveU", GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.playing("piano"))
                .setStatus(OnlineStatus.IDLE)
                .addEventListeners(new Listeners())
                .build();



        Guild server = bot.getGuildById("1074156189513945179");

        if(server != null){
            server.upsertCommand("fart", "farts really hard").queue();
        }
        //yes
        //testt2
        //commit test 3


        //TextChannel general = event.getGuild().getTextChannelById(946147235375251501L);


    }


}
