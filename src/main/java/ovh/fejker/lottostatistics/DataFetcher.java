package ovh.fejker.lottostatistics;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class DataFetcher {

    private final String game;
    private final int size;

    public DataFetcher(String game, int size) throws IOException {
        this.game = game;
        this.size = size;
        getData();
    }

    public void getData() throws IOException {
        System.out.println("FETCHING DATA");
        String url = "https://www.lotto.pl/api/lotteries/draw-results/by-gametype?game=" + game + "&index=1&size=" + size + "&sort=drawDate&order=DESC";
        String json = IOUtils.toString(URI.create(url), StandardCharsets.UTF_8);
        JSONObject parent = new JSONObject(json);
        System.out.println("PARENT" + parent);
        JSONArray array = new JSONArray(parent.getJSONArray("items"));
        System.out.println("ARRAY" + array);

        for(Object obj : array) {
            boolean isSpecial = true;
            long id;
            String date;
            String type;
            int[] results = null;
            int[] specialResults = null;

            JSONObject child = new JSONObject(obj.toString());
            System.out.println("CHILD" + child);
            JSONArray childArray = new JSONArray(child.getJSONArray("results"));

            JSONObject child2 = new JSONObject(childArray.get(0).toString());
            System.out.println(child2);
            JSONArray childArray2 = new JSONArray(child2.getJSONArray("resultsJson"));
            results = new int[childArray2.length()];
            int i = 0;
            for(Object obj3 : childArray2) {
                results[i] = (int) obj3;
                i++;
            }

            childArray2 = child2.getJSONArray("specialResults");

            if(childArray2.length() == 0) {
                isSpecial = false;
            }
            i = 0;
            specialResults = new int[childArray2.length()];
            for(Object obj3 : childArray2) {
                specialResults[i] = (int) obj3;
                i++;
            }

            id = Long.parseLong(child.get("drawSystemId").toString());
            date = (String) child.get("drawDate");
            type = game;

            if(isSpecial){
                Main.addToRaffleList(new Raffle(id, date, type, results, specialResults));
            } else {
                Main.addToRaffleList(new Raffle(id, date, type, results));
            }
        }

    }
}
