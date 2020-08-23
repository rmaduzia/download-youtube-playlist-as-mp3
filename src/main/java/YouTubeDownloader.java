import java.security.GeneralSecurityException;
import java.util.*;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.Collection;

public class YouTubeDownloader {
    private static final String CLIENT_SECRETS= "client_secret.json";
    private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");
    private static final String APPLICATION_NAME = "youtubeplaylist-287008";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        InputStream in =  YouTubeDownloader.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(Config.USER_ID);
        return credential;
    }

    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args)
            throws GeneralSecurityException, IOException, InterruptedException {
        YouTube youtubeService = getService();
        YouTube.PlaylistItems.List request = youtubeService.playlistItems()
                 .list("snippet,contentDetails");

        List<String> wholePlayList = new ArrayList<>();

        String nextPageToken;
        PlaylistItemListResponse response = request.setMaxResults(100L)
                .setPlaylistId(Config.PLAYLIST_ID)
                .execute();

        nextPageToken = response.getNextPageToken();
        wholePlayList.add(response.toString());

            do{
                PlaylistItemListResponse responseNext = request.setMaxResults(100L)
                        .setPlaylistId(Config.PLAYLIST_ID)
                        .setPageToken(nextPageToken)
                        .execute();
                wholePlayList.add(responseNext.toString());
                nextPageToken = responseNext.getNextPageToken();

            }while (nextPageToken != null);

        List<String> wholePlayListWithoutDuplicate = new ArrayList<>(new LinkedHashSet<>(wholePlayList));

        GetYouTubeVideos.getAllVideosLink(wholePlayListWithoutDuplicate);
        System.out.println("Starting downloading mp3 ");
        downloadVideosByPythonScript();

    }

    public static void downloadVideosByPythonScript() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(Config.PYTHON_PATH_ENVIRONMENT, "download_youtube_videos.py");
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();
        if(exitCode == 0)
            System.out.println("Downloading mp3 finished");
        else
            System.out.println("Try to update python - youtube_dl ");
    }
}