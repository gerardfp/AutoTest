import org.apache.http.client.fluent.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.IOException;

public class Main {
    static MoodleAPI api;
    static String token;

    public static void main(String[] args) {
        System.out.println("MOODLE API");
        api =  new Retrofit.Builder()
                .baseUrl(Config.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MoodleAPI.class);

        api.login(Config.username, Config.password).enqueue((RCallback<Token>) (call, response) -> {
            System.out.println("TOKEN = " + response.body().token);
            token = response.body().token;

//            getSubmissions();
        });
    }

    private static void getSubmissions() {
        api.submissions(token).enqueue((RCallback<Assignments>) (call, response) -> response.body().assignments.get(0).submissions.forEach(s -> {
            System.out.println(s.userid);

            new File("submissions/" + s.userid).mkdirs();
            s.plugins.get(0).fileareas.get(0).files.forEach(f -> {
                System.out.println("    " + f.fileurl);


                try {
                    Request.Get(f.fileurl + "?token="+token).execute().saveContent(new File("submissions/" + s.userid + "/" +  f.filename));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }));
    }
}
