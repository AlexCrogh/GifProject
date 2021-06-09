package com.croghan.gifs;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;

@RestController
public class ScheduledTasks {

    private static String consumerKeyStr = "eoDuWMwfMSczkEhnIJYwwxE0q";
    private static String consumerSecretStr = "GtelEhKwoMqnBZAI0vcsoQdpuixLzQ2zkHEwmkDoGovLfca97z";
    private static String accessTokenStr = "1052297317267755008-5NZ8mfN31SVJOFH2gt8GlHrbhZyobE";
    private static String accessTokenSecretStr = "mCWe1KiULLblX6e8BqB7BmrXvd7PL8H8MuNUoTakpanL2";

    int id = 1;
    static RestTemplate restTemplate = new RestTemplate();

    @Scheduled(cron = "0 0 0 * * * ")
    public void addGifOne() throws IOException {
        System.out.println("attempting to retrieve gif from /r/gifs");
        Gif gif = getGif(id, "gifs");
        String url = "http://localhost:8080/createGif";
        restTemplate.postForObject(url, gif, Gif.class);
        System.out.println("id now " + id);
    }

    @Scheduled(cron = "0 0 0 * * * ")
    public void addGifTwo() throws IOException {
        Gif gif = getGif(id, "gifrecipes");
        String url = "http://localhost:8080/createGif";
        restTemplate.postForObject(url, gif, Gif.class);
        System.out.println("Post saved to db " + gif.getTitle());
    }
    @Scheduled(cron = "0 0 0 * * * ")
    public void addGifThree() throws IOException {
        Gif gif = getGif(id, "educationalgifs");
        String url = "http://localhost:8080/createGif";
        restTemplate.postForObject(url, gif, Gif.class);
        System.out.println("Post saved to db " + gif.getTitle());
    }


    @Scheduled(cron = "0 0 */12 * * * ")
    public void postToTwitter() throws Exception {
        OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(consumerKeyStr, consumerSecretStr);
        oAuthConsumer.setTokenWithSecret(accessTokenStr, accessTokenSecretStr);
        Gif gif = restTemplate.getForObject("http://localhost:8080/getTwitterGif", Gif.class);
        System.out.println("Post retrieved from DB: " + gif.getTitle() + "-- id: " + gif.getId());
        if(gif.getTitle() != null){
            String str = URLEncoder.encode(gif.getTitle() +" (via reddit.com/r/" +  gif.getCategory()+")\n" + gif.getUrl(), "UTF-8");
            HttpPost httpPost = new HttpPost("http://api.twitter.com/1.1/statuses/update.json?status=" + str );
            oAuthConsumer.sign(httpPost);
            gif.setPosted(true);
            restTemplate.put("http://localhost:8080/updateGif", gif);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            System.out.println(statusCode + ':' + httpResponse.getStatusLine().getReasonPhrase());
            System.out.println(IOUtils.toString(httpResponse.getEntity().getContent()));
        }
        else{
            System.out.println("Post object was null");
        }

    }

    public Gif getGif(int id, String sub) throws IOException {
        //call pushshift api
        URL url = new URL("http://api.pushshift.io/reddit/search/submission/?subreddit="+ sub + "&after=1d&sort=desc&sort_type=num_comments&is_video=false&size=1");
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
