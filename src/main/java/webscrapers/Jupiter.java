package webscrapers;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.List;
import java.util.*;

public class Jupiter {
    public Jupiter() throws IOException, InterruptedException {
        //String loginFormUrl = "https://login.jupitered.com/login/";
        String loginActionUrl = "https://login.jupitered.com/login/index.php";
        String username = "229385620";
        String password = "passowrd";
        //HashMap<String, String> cookies = new HashMap<>();
        HashMap<String, String> formData = new HashMap<>();
        formData.put("mini", "0");
        formData.put("from", "login");
        formData.put("language", "eng");
        formData.put("loginpage", "student");
        formData.put("username1", "");
        formData.put("studid1", username);
        formData.put("text_password1", password);
        formData.put("subcode", "");
        formData.put("timeoutin1", "0");
        formData.put("access1", "1");
        formData.put("school1", "15309");
        formData.put("city1", "Bronx");
        formData.put("region1", "us_ny");
        formData.put("pagecomplete", "1");
        formData.put("doit", "checkstudent");



        Connection.Response homePage = Jsoup.connect(loginActionUrl)
                //.cookies(cookies)
                .data(formData)
                .method(Connection.Method.POST)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                .execute();

        Thread.sleep(2000);

        System.out.println(homePage.parse().html());

    }
    public static void main(String[] args) throws IOException, InterruptedException {
        Jupiter yes =  new Jupiter();
    }
}
