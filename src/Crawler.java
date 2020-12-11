import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Crawler {


    public static ArrayList<String> getUrlsFromPage(String str) throws Exception {
        ArrayList<String> links = new ArrayList<String>();


        Pattern check = Pattern.compile("(?m)<a href\\W\\\"([^\"]*)\">", Pattern.CASE_INSENSITIVE);
        Matcher matcher = check.matcher(str);
        while(matcher.find()){
            links.add( matcher.group(1));


        }
        return links;

    }
    public static String html;
    public static void getHtmlbody(String htmlbody) {
        html = htmlbody;
    }

    public static ArrayList<String> startConnection(ArrayList<URI> urlList) throws Exception {
        List<HttpRequest> requests = urlList
                .stream()
                .map(url -> HttpRequest.newBuilder(url))
                .map(reqBuilder -> reqBuilder.build())
                .collect(Collectors.toList());

        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<?>[] asyncs = requests
                .stream()
                .map(request -> client
                        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenAccept(str -> getHtmlbody(str))).toArray(CompletableFuture<?>[]::new);
        CompletableFuture.allOf(asyncs).join();
        ArrayList<String> test = getUrlsFromPage(html);
        return test;
    }


    public static void main(String[] args) throws Exception {

        String baseUrl = "http://localhost"; //args[0];

        ArrayList<URI> urlList = new ArrayList<URI>();

        urlList.add(URI.create("http://localhost"));

        ArrayList<String> test = startConnection(urlList);

        ArrayList<URI> urls = new ArrayList<URI>();
        for (int i = 0; i < test.size(); i++){
            urls.add(URI.create(baseUrl + test.get(i)));

        }
        for (int i = 0; i < urls.size(); i++){
            System.out.println(urls.get(i));
        }

        ArrayList<String> again = new ArrayList<String>();
        for (int i = 0; i < urls.size(); i++) {
            again = startConnection(urls);

        }
        for (int i = 0; i < urls.size(); i++) {

            System.out.println(again.get(i));
        }

    }


}