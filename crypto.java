import org.json.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class crypto {
    public static URL connect;
    private static HttpURLConnection connection;
    private static final int FIVE_SECONDS = 5000;
    private static final int THIRY_MINUTES = 1800000;
    private static final int TWO_MINUTES = 120000;
    private static final int MINIMUM_TIME_FOR_MAX_CHECKS_ON_ONE = 864000;
    private static int iD;
    public static String cryptoChoice;


    public static void main(String[] args) throws IOException, JSONException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {


        Scanner in = new Scanner(System.in);
        System.out.println("Enter the Crypto You would like to Check e.g ADA: ");
        cryptoChoice = in.nextLine();

        int isThisTheFirst = 0;
        double previous = 0;

        while(1 == 1)
        {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("FILE_PATH").getAbsoluteFile());
            Clip clipUP = AudioSystem.getClip();
            clipUP.open(audioInputStream);
            Clip clipDown = AudioSystem.getClip();
            audioInputStream = AudioSystem.getAudioInputStream((new File("FILE_PATH")));
            clipDown.open(audioInputStream);
            clipUP.open();
            clipDown.open();
            connect = new URL("https://rest.coinapi.io/v1/exchangerate/" + cryptoChoice + "?apikey=YOUR_API_KEY_HERE");
            connection = (HttpURLConnection) connect.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(FIVE_SECONDS);
            connection.setReadTimeout(FIVE_SECONDS);

            Scanner fromWebsite = new Scanner(connect.openStream());
            StringBuffer stringBuffer = new StringBuffer();

            while (fromWebsite.hasNext()) {
                stringBuffer.append(fromWebsite.next() + " ");
            }
            String result = stringBuffer.toString();
            result = result.replaceAll("<br>", "\n");
            result = result.replaceAll("<[^>]*>", "");

            JSONObject entireThing = new JSONObject(result);
            JSONArray rates = entireThing.getJSONArray("rates");


            for (int i = 0; i < rates.length(); i++) {
                JSONObject temp = rates.getJSONObject(i);
                if (temp.getString("asset_id_quote").equals("USDT")) {

                    System.out.printf("%nOne " + cryptoChoice + " is equal to $" + temp.get("rate") + "  Time Now: " + temp.get("time"));

                    if (isThisTheFirst == 0) {
                        isThisTheFirst = 1;
                        previous = (double) temp.get("rate");
                    }
                    else {
                        double increaseOrDecrease = previous - ((double) temp.get("rate"));
                        if (increaseOrDecrease < 0) {
                            clipUP.start();
                            System.out.printf("%nIt has Gone up by $%.18f", increaseOrDecrease * -1);

                        } else if (increaseOrDecrease > 0) {
                            clipDown.start();
                            System.out.printf("%nIt has Gone down by $%.18f", increaseOrDecrease);

                        } else {
                            System.out.println("%nIT HAS STAYED THE SAME!!!");
                        }
                        previous = (double) temp.get("rate");
                    }
                }
            }
            Thread.sleep(MINIMUM_TIME_FOR_MAX_CHECKS_ON_ONE);
            clipUP.stop();
            clipDown.stop();
            clipUP.close();
            clipDown.close();
        }
    }
}

