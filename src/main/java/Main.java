import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.fluent.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

//class Config {
//    static String baseUrl = "https://moodle.elpuig.xeill.net/";
//    static String username = "$$$$$$$$$$$$$$$$$$";
//    static String password = "$$$$$$$$$$$$$$$$$$";
//
//    static String courseId = "260";
//    static String assignId = "45396";
//
//    static String carpeta = "submissions/";
//
//    static String javac = "/home/gerard/idea-IC-183.5912.21/jdk-13.0.1/bin/javac";
//    static String java = "/home/gerard/idea-IC-183.5912.21/jdk-13.0.1/bin/java";
//
//    static String carpetaTestCases = "/home/gerard/AutoTest/testcases/";
//}

public class Main {
    static MoodleAPI api;

    static String token;
    static String assignId;
    static HashMap<String, String> id2username = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("MOODLE API");
        api =  new Retrofit.Builder()
                .baseUrl(Config.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MoodleAPI.class);


        login();
    }

    private static void login(){
        api.login(Config.username, Config.password).enqueue((RCallback<Token>) (call, response) -> {
            System.out.println("TOKEN = " + response.body().token);
            token = response.body().token;

            getStudents();
        });
    }

    private static void getStudents(){
        api.students(token, Config.courseId).enqueue((RCallback<List<Student>>) (call, response) -> {
            System.out.println("STUDENTS=");
            response.body().forEach(s -> id2username.put(s.id, s.username));

            getAssignmentId();
        });
    }

    private static void getAssignmentId(){
        api.courses(token, Config.courseId).enqueue((RCallback<Courses>) (call, response) -> {
            System.out.println("ASSIGNID=");
            response.body().courses.forEach(c -> c.assignments.forEach(a -> { if(a.cmid.equals(Config.assignId)) assignId = a.id; }));

            getSubmissions();
        });
    }

    private static void getSubmissions() {
        System.out.println("SUBMISSIONS=");
        api.submissions(token, assignId).enqueue((RCallback<Assignments>) (call, response) -> response.body().assignments.get(0).submissions.forEach(s -> {
            if(!s.userid.equals("758")) return;
            System.out.println(s.userid + " -> " + id2username.get(s.userid));

            String folder = Config.carpeta + id2username.get(s.userid) + "/";
            new File(folder).mkdirs();
            s.plugins.get(0).fileareas.get(0).files.forEach(f -> {
                System.out.println("    " + f.fileurl);

                try {
                    File file = new File(folder + f.filename);
                    Request.Get(f.fileurl + "?token="+token).execute().saveContent(file);
                    testSubmission(file);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }));
    }

    private static void testSubmission(File file){
        String absolutePath = file.getAbsolutePath();
        String baseDir = FilenameUtils.getFullPath(absolutePath);
        String baseName = FilenameUtils.getBaseName(absolutePath);

        File baseDirFile = new File(baseDir);

        String contents = null;
        try {
            contents = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            contents = Pattern.compile("package .*;\n").matcher(contents).replaceAll("");
            FileUtils.writeStringToFile(file, contents);
        } catch (IOException e) {
            e.printStackTrace();
        }

        run(Config.javac + " " + absolutePath);

        // FIX: NO FUNCIONA!!!!
        // FIX: Hacer un for de todos los testcases
        run(Config.java + " " + baseName + " < " + Config.carpetaTestCases + baseName + ".IN > " + absolutePath + ".OUT", baseDirFile);
    }

    private static void run(String cmd){

        try {
            System.out.println("   LOG: " + cmd);
            Runtime.getRuntime().exec(cmd).waitFor();
            System.out.println("     DONE: " + cmd);
        } catch (Exception e) {
            System.out.println("  ERROR: " + cmd);
            e.printStackTrace();
        }
    }

    private static void run(String cmd, File dir){

        try {
            System.out.println("   LOG: " + cmd);
            Runtime.getRuntime().exec(cmd, null, dir).waitFor();
            System.out.println("     DONE: " + cmd);
        } catch (Exception e) {
            System.out.println("  ERROR: " + cmd);
            e.printStackTrace();
        }
    }

}
