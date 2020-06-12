package com.example;

import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Scanner;


public class Main {
    private static MoodleAPI api;

    public static void main(String[] args) {
        api =  new Retrofit.Builder()
                .baseUrl("https://moodle.elpuig.xeill.net/")
                .client(new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
                                .addQueryParameter("moodlewsrestformat", "json")
                                .build();

                        Request.Builder requestBuilder = original.newBuilder()
                                .url(url);

                        Request request = requestBuilder.build();

//                            System.out.println(("INTERCEPTOR " + String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers())));
                        return chain.proceed(request);
                    }).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MoodleAPI.class);

        login();
    }

    private static void login(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Moodle username: ");
        String username = scanner.nextLine();
        System.out.println("Moodle password: ");
        String password = scanner.nextLine();
        api.login(username, password).enqueue((Callback<Token>) response -> {
            getAssignments(response.token);
        });
    }

    private static void getAssignments(String token){

        try {
            api.assignments(token).execute().body().courses.forEach(course -> {
                course.assignments.forEach(assignment -> {
                    try {
                        api.submissions(token, assignment.id).execute().body().assignments.forEach(assignment1 -> {
                            assignment.submissions = assignment1.submissions;
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                course.assignments.removeIf(assignment -> {
                    if(assignment.submissions != null) {
                        for (Submission submission : assignment.submissions) {
                            if (submission.gradingstatus.equals("notgraded")) {
                                return false;
                            }
                        }
                    }
                    return true;
                });

                if(course.assignments.size() > 0){
                    System.out.println();
                    System.out.println();
                    System.out.println(course.fullname);
                    for(Assignment assignment:course.assignments){
                        System.out.println();
                        System.out.println("\t"+assignment.name);
                        if(assignment.submissions != null) {
                            for (Submission submission : assignment.submissions) {
                                if (submission.gradingstatus.equals("notgraded")) {
                                    System.out.println("\t\thttps://moodle.elpuig.xeill.net/mod/assign/view.php?id=" + assignment.cmid + "&rownum=0&action=grader&userid=" + submission.userid);
                                }
                            }
                        }
                    }
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static void getSubmissions(String token, String assignId, String assignCmid, String courseName, String assignmentName){
//        api.submissions(token, assignId).enqueue((Callback<Assignments>) response -> {
//            System.out.println("\t" + courseName);
//            System.out.println("\t\t" + assignmentName);
//            response.assignments.forEach(a ->{
//                a.submissions.forEach(s -> {
//                    if(s.gradingstatus.equals("notgraded")){
//                        System.out.println("https://moodle.elpuig.xeill.net/mod/assign/view.php?id=" + assignCmid +"&rownum=0&action=grader&userid="+ s.userid);
//                    }
//                });
//            });
//        });
//    }

//    private static void getDiscussions(){
//        api.discussions(token, Config.forumid).enqueue((com.example.Callback<com.example.Discusiones>) response -> {
//            response.discussions.forEach(d -> System.out.format("%s%n%s%n%s%n%s%n-----%n", d.id, d.name, d.message, d.subject));
//        });
//    }



//    private static void getSubmissions(){
//        api.submissions(token, assignIds).enqueue((com.example.Callback<com.example.Assignments>) response -> {
//            response.assignments.forEach(a ->{
//                a.submissions.forEach(s -> {
//                    if(s.gradingstatus.equals("notgraded")){
//                        System.out.println(s.id);
//                    }
//                });
//            });
//        });
//    }
//    private static void getStudents(){
//        System.out.println("obteniendo students...");
//        api.students(token, Config.courseId).enqueue((com.example.Callback<List<com.example.Student>>) response -> {
//            response.forEach(s -> id2username.put(s.id, s.username));
//
//            getAssignmentId();
//        });
//    }
//
//    private static void getAssignmentId(){
//        System.out.println("obteniendo asignId...");
//        api.courses(token, Config.courseId).enqueue((com.example.Callback<com.example.Courses>) response -> {
//            response.courses.forEach(c -> c.assignments.forEach(a -> { if(a.cmid.equals(Config.assignId)) assignId = a.id; }));
//
//            getSubmissions();
//        });
//    }
//
//    private static void getSubmissions() {
//        System.out.println("obteniendo submissions...");
//        api.submissions(token, assignId).enqueue((com.example.Callback<com.example.Assignments>) response -> response.assignments.get(0).submissions.forEach(s -> {
//
//            String username = id2username.get(s.userid);
//            System.out.println(s.userid + " -> " + username);
//
//            String folder = Config.carpetaDescarga + username + "/";
//            new File(folder).mkdirs();
//
//            s.plugins.get(0).fileareas.get(0).files.forEach(f -> {
//                System.out.println("    descargando " + f.fileurl + "&token="+token);
//
//                try {
//                    File file = new File(folder + f.filename);
//                    Request.Get(f.fileurl + "?token="+token).execute().saveContent(file);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//        }));
//    }
}
