package edu.learn.bootgradle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostData {
    public static void main(String[] args) throws IOException {
        PostData postData = new PostData();
        if(args.length == 0){
            System.out.println("Please provide input and output file \ne.g. D:\\ticker.csv D:\\cusip.txt");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        List<String> tickers;
        try(Stream<String> readCsvFile = Files.lines(Paths.get(inputFile))){
            tickers = readCsvFile.collect(Collectors.toList());
        }

        for(String ticker : tickers){
           postData.cusip(ticker.trim().toUpperCase());
        }


        /*String urlParameters  = "tickersymbol=DELL&sopt=symbol";
        String request = "http://www.quantumonline.com/search.cfm";
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(request))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("charset", "utf-8")
                .POST(HttpRequest.BodyProcessor.fromByteArray(urlParameters.getBytes()))
                .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandler.asString());
            System.out.println(response.body());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public void cusip(String ticker) throws IOException {

        String urlParameters  = "tickersymbol="+ticker+"&sopt=symbol";
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        String request = "http://www.quantumonline.com/search.cfm";
        URL url = new URL( request );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
        conn.setUseCaches(false);
        try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write( postData );
        }

        Scanner readResult = new Scanner(conn.getInputStream());
        StringBuffer html = new StringBuffer();
        while(readResult.hasNext()){
            html.append(readResult.nextLine());
        }

        extractCusip(ticker, html);
    }

    private void extractCusip(String ticker, StringBuffer html) throws IOException {
        Document doc = Jsoup.parse(html.toString());
        Elements findTableWithBgColor = doc.select("table[bgcolor='#DCFDD7']");
        Element findCusipSection = findTableWithBgColor.select("font[size='-1']").first();
        Path cusipFile = Paths.get("D:/cusip.txt");

        if(findCusipSection == null){
            String tickerWithCusip = ticker + ",No cusip found\n";
            Files.write(cusipFile,tickerWithCusip.getBytes(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
            System.out.printf(ticker + " added in %s ..." , cusipFile.getFileName().toString());
        }else{
            Element findCusipRow = findCusipSection.select("b").first();
            String cusipInfo = findCusipRow.html().replaceAll("&nbsp;"," ");
            int cusipIndex = cusipInfo.indexOf("CUSIP:");
            int exchangeIndex = cusipInfo.indexOf("Exchange:");

            String tickerWithCusip = ticker + "," + cusipInfo.substring(cusipIndex + 6, exchangeIndex).trim() + "\n";
            Files.write(cusipFile,tickerWithCusip.getBytes(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
            System.out.printf(tickerWithCusip + " added in %s ..." , cusipFile.getFileName().toString());
        }
    }
}