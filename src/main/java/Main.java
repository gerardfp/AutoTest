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
    static File runtests = new File("runtests.sh");
    static String runtestScript = "";

    public static void main(String[] args) {
        System.out.println("Start");
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
            System.out.println("obteniendo students...");
            response.body().forEach(s -> id2username.put(s.id, s.username));

            getAssignmentId();
        });
    }

    private static void getAssignmentId(){
        api.courses(token, Config.courseId).enqueue((RCallback<Courses>) (call, response) -> {
            System.out.println("obteniendo asignId...");
            response.body().courses.forEach(c -> c.assignments.forEach(a -> { if(a.cmid.equals(Config.assignId)) assignId = a.id; }));

            getSubmissions();
        });
    }

    private static void getSubmissions() {
        System.out.println("obteniendo submissions...");
        api.submissions(token, assignId).enqueue((RCallback<Assignments>) (call, response) -> response.body().assignments.get(0).submissions.forEach(s -> {

            System.out.println(s.userid + " -> " + id2username.get(s.userid));

            String folder = Config.carpeta + id2username.get(s.userid) + "/";
            File baseDir = new File(folder);
            baseDir.mkdirs();

            addLine("\n\ncd " + baseDir.getAbsolutePath());

            s.plugins.get(0).fileareas.get(0).files.forEach(f -> {
                System.out.println("    descargando " + f.fileurl + "&token="+token);

                try {
                    File file = new File(folder + f.filename);
//                    Request.Get(f.fileurl + "?token="+token).execute().saveContent(file);
                    compileAndCreateTestScript(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            writeTestScript();
        }));
    }

    private static void writeTestScript(){
        try {
            FileUtils.writeStringToFile(runtests,runtestScript);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addLine(String line) {
        runtestScript += line + "\n";
    }

    private static void compileAndCreateTestScript(File file) throws IOException {
        String absolutePath = file.getAbsolutePath();
        String baseDir = FilenameUtils.getFullPath(absolutePath);
        String baseName = FilenameUtils.getBaseName(absolutePath);


        // Eliminar la linea "package" del codigo fuente
        String contents = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
        contents = Pattern.compile("package .*;\n").matcher(contents).replaceAll("");
        FileUtils.writeStringToFile(file, contents);


        System.out.println("   Compilando: " + Config.javac + " " + absolutePath);
        Runtime.getRuntime().exec(Config.javac + " " + absolutePath);

        for (int i = 0; i < 1000; i++) {
            if(new File(Config.carpetaTestCases + baseName + "." + i + ".IN").isFile()) {
                addLine(Config.java + " " + baseName + " < " + Config.carpetaTestCases + baseName + "." + i + ".IN > " + baseDir + baseName + "." + i + ".OUT");
            } else {
                break;
            }
        }
    }
}
