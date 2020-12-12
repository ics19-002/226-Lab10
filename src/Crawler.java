import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

////////////////////////////////////////////////////////////////////////////
//PURPOSE: Supposed to crawl a given webpage and list all links and sublinks
//
//ISSUES: Does not use MAX_DEPTH, going deeper within was broken during retooling
////////////////////////////////////////////////////////////////////////////
public class Crawler {
    public static String baseUrl = "http://localhost"; //args[0];
    //Takes in an ArrayList<String> of paths without the base url prepended and returns
    //an ArrayList<URI> of the same paths converted into full URLs
    public static ArrayList<URI> convertStringToFullURI(ArrayList<String> list){
        ArrayList<URI> urls = new ArrayList<URI>();
        for (String s : list) {
            if (!visitedLinks.contains(baseUrl + s)){
                urls.add(URI.create(baseUrl + s));
            }

        }

        return urls;
    }
    public static ArrayList<String> links = new ArrayList<String>();
    //Takes in a full html page and catches all the <a href ...> tags and returns an ArrayList<String>
    //containing all of those paths for use within convertStringToFullURI()
    public static ArrayList<String> getUrlsFromPage(String str) {



        Pattern check = Pattern.compile("(?m)<a href\\W\"([^\"]*)\">", Pattern.CASE_INSENSITIVE);
        Matcher matcher = check.matcher(str);
        while(matcher.find()){

            links.add( matcher.group(1));



        }


        return links;

    }
    public static String html;
    //Gets the full HTML body of the linked webpage
    public static void getHtmlbody(String htmlbody) {
        getUrlsFromPage(htmlbody);
        html = htmlbody;

    }


    //Makes a connection to a webpage, from a given URI
    public static void startConnection(URI uri) throws Exception {
        System.out.println(visitedLinks.contains(uri));

        ArrayList<URI> urlList = new ArrayList<>();
        urlList.add(uri);
        List<HttpRequest> requests = urlList
                .stream()
                .map(HttpRequest::newBuilder)
                .map(HttpRequest.Builder::build)
                .collect(Collectors.toList());

        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<?>[] asyncs = requests
                .stream()
                .map(request -> client
                        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenAccept(Crawler::getHtmlbody)).toArray(CompletableFuture<?>[]::new);
        CompletableFuture.allOf(asyncs).join();

        return;
    }

    public static HashSet<URI> visitedLinks = new HashSet<>();

    public static void main(String[] args) throws Exception, IndexOutOfBoundsException {


        startConnection(URI.create(baseUrl));
        System.out.println(convertStringToFullURI(getUrlsFromPage(html)));
        //ArrayList<URI> urls = convertStringToFullURI(startConnection(URI.create(baseUrl)));


        /*for (int i = 0; i < urls.size(); i++) {

            ArrayList<URI> test = convertStringToFullURI(startConnection(urls.get(i)));

            visitedLinks.add(test.get(i));
            System.out.println("Visited Links: "+visitedLinks);


        }*/



    }


}