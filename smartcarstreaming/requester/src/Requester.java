/**
 * Created by jobro on 20.06.2017.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *  This class is only to be run locally, as a Requester that simulates the api requests of the cars
 */
public class Requester {

    private final String USER_AGENT = "Mozilla/5.0";
    private final String POSTADDRESS1 = "http://localhost:8080/api/carpacket";

    //Requester to simulate cars driving
    public static void main(String[] args) throws Exception {

        Requester http = new Requester();
        Files.lines(Paths.get("C:\\Users\\jobro\\Documents\\Studium\\2.Semester\\ASE\\Miniproject\\smartcarstreaming\\requester\\src\\testinput")).forEach(s -> {
            try {
                http.sendPost(s);
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });


    }

    // HTTP POST request
    private void sendPost(String body) throws Exception {


        URL obj = new URL(POSTADDRESS1);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "text/plain");


        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(body);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);
        System.out.println(con.getResponseMessage());

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

}