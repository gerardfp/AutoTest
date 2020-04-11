import org.apache.http.client.fluent.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


class Config {
    static String baseUrl = "https://moodle.elpuig.xeill.net/";
    static String username = "$$$$$$$";
    static String password = "$$$$$$$";
    static String courseId = "260";
    static String assignId = "45396";

    static String carpetaDescarga = "descargas/";
}


public class Main {
    private static MoodleAPI api;

    private static String token;
    private static String assignId;
    private static HashMap<String, String> id2username = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("start");
        api =  new Retrofit.Builder()
                .baseUrl(Config.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MoodleAPI.class);

        login();
    }

    private static void login(){
        System.out.println("login...");
        api.login(Config.username, Config.password).enqueue((Callback<Token>) response -> {
            System.out.println("TOKEN = " + response.token);
            token = response.token;

            getStudents();
        });
    }

    private static void getStudents(){
        System.out.println("obteniendo students...");
        api.students(token, Config.courseId).enqueue((Callback<List<Student>>) response -> {
            response.forEach(s -> id2username.put(s.id, s.username));

            getAssignmentId();
        });
    }

    private static void getAssignmentId(){
        System.out.println("obteniendo asignId...");
        api.courses(token, Config.courseId).enqueue((Callback<Courses>) response -> {
            response.courses.forEach(c -> c.assignments.forEach(a -> { if(a.cmid.equals(Config.assignId)) assignId = a.id; }));

            getSubmissions();
        });
    }

    private static void getSubmissions() {
        System.out.println("obteniendo submissions...");
        api.submissions(token, assignId).enqueue((Callback<Assignments>) response -> response.assignments.get(0).submissions.forEach(s -> {

            String username = id2username.get(s.userid);
            System.out.println(s.userid + " -> " + username);

            String folder = Config.carpetaDescarga + username + "/";
            new File(folder).mkdirs();

            s.plugins.get(0).fileareas.get(0).files.forEach(f -> {
                System.out.println("    descargando " + f.fileurl + "&token="+token);

                try {
                    File file = new File(folder + f.filename);
                    Request.Get(f.fileurl + "?token="+token).execute().saveContent(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }));
    }
}
