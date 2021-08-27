package com.croghan.gifs;

import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@RestController
public class ScheduledTasks {

    private static String consumerKeyStr = "";
    private static String consumerSecretStr = "";
    private static String accessTokenStr = "";
    private static String accessTokenSecretStr = "";

    int id = 1;
    static RestTemplate restTemplate = new RestTemplate();

    @Scheduled(cron = "@weekly ")
    public void addGifOne() throws IOException {
        System.out.println("attempting to retrieve gif from /r/gifs");
        Gif gif = getGif(id, "gifs");
        String url = "http://localhost:8080/createGif";
        restTemplate.postForObject(url, gif, Gif.class);
        System.out.println("id now " + id);
    }

    @Scheduled(cron = "@weekly")
    public void addGifTwo() throws IOException {
        Gif gif = getGif(id, "gifrecipes");
        String url = "http://localhost:8080/createGif";
        restTemplate.postForObject(url, gif, Gif.class);
        System.out.println("Post saved to db " + gif.getTitle());
    }
    @Scheduled(cron = "@weekly")
    public void addGifThree() throws IOException {
        Gif gif = getGif(id, "educationalgifs");
        String url = "http://localhost:8080/createGif";
        restTemplate.postForObject(url, gif, Gif.class);
        System.out.println("Post saved to db " + gif.getTitle());
    }
    @Scheduled(cron = "@weekly")
    public void addGifFour() throws IOException {
        Gif gif = getGif(id, "bettereveryloop");
        String url = "http://localhost:8080/createGif";
        restTemplate.postForObject(url, gif, Gif.class);
        System.out.println("Post saved to db " + gif.getTitle());
    }
    @Scheduled(cron = "@weekly")
    public void addGifFive() throws IOException {
        Gif gif = getGif(id, "perfectloops");
        String url = "http://localhost:8080/createGif";
        restTemplate.postForObject(url, gif, Gif.class);
        System.out.println("Post saved to db " + gif.getTitle());
    }


    @Scheduled(cron = "0 0 0 * * MON,WED,FRI")
    public void postToTwitter() throws Exception {
        Gif gif = restTemplate.getForObject("http://localhost:8080/getTwitterGif", Gif.class);
        System.out.println("Post retrieved from DB: " + gif.getTitle() + "-- id: " + gif.getId());
        if(gif.getTitle() != null){
            String str = gif.getTitle() +" (via reddit.com/r/" +  gif.getCategory()+")\n" + gif.getUrl();
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(consumerKeyStr)
                    .setOAuthConsumerSecret(consumerSecretStr)
                    .setOAuthAccessToken(accessTokenStr)
                    .setOAuthAccessTokenSecret(accessTokenSecretStr);
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            twitter.updateStatus(str);
            gif.setPosted(true);
            restTemplate.put("http://localhost:8080/updateGif", gif);
        }
        else{
            System.out.println("Post object was null");
        }

    }

    public Gif getGif(int id, String sub) throws IOException {
        //call pushshift api
        URL url = new URL("https://api.pushshift.io/reddit/search/submission/?subreddit="+ sub +
                "&after=1w&sort=desc&sort_type=num_comments&is_video=false&over_18=false&stickied=false&size=1");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String fullStr = "";
        while ((line = inputReader.readLine()) != null) {
            fullStr += line;
        }
        JSONObject json = new JSONObject(fullStr);
        //extract url, title and subreddit from Jsonobject returned from api call and converts them to strings
        String gifURL = json.getJSONArray("data").getJSONObject(0).getString("url");
        String title = json.getJSONArray("data").getJSONObject(0).getString("title");
        String category = json.getJSONArray("data").getJSONObject(0).getString("subreddit");

        //imgur.gif is not playing in desktop, this changes it to .gifv format
        if(gifURL.substring(gifURL.length()-4).equals(".gif") && gifURL.contains("imgur"))
            gifURL = gifURL + "v";

        //trim titles longer than 255 characters
        gifURL = gifURL.substring(0, Math.min(gifURL.length(), 254));

        Gif gif = new Gif(id, title, gifURL, category);
        inputStream.close();
        inputReader.close();
        this.id++;
        return gif;
    }
}
