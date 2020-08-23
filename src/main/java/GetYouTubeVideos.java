import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetYouTubeVideos {

    public static List<String> getAllVideosID(List<String> wholePlayList){
        List<String> allYouTubeVideosID = new ArrayList<>();

        for(int i=0; i<wholePlayList.size(); i++){
            JSONObject jsonObjectPlayList = new JSONObject(wholePlayList.get(i));
            JSONArray jsonArrayPlayList = jsonObjectPlayList.getJSONArray("items");

            for (int j=0; j<jsonArrayPlayList.length(); j++){
                JSONObject videoDetails = (JSONObject) jsonArrayPlayList.getJSONObject(j).get("contentDetails");
                String youtubeVideoID = videoDetails.getString("videoId");
                allYouTubeVideosID.add(youtubeVideoID);
            }
        }
        return allYouTubeVideosID;
    }

    public static void getAllVideosLink(List<String> wholePlayList) throws IOException {
        List<String> videosID = getAllVideosID(wholePlayList);
        String basic_url = "https://www.youtube.com/watch?v=";
        List<String> videosLink = new ArrayList<>();

        for (String videoID : videosID){
            videosLink.add(basic_url+ videoID);
        }

        FileWriter writer = new FileWriter(Config.FILE_WITH_URLS);
        for(String link: videosLink) {
            writer.write(link + System.lineSeparator());
        }
        writer.close();
    }

}
