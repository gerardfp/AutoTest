import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface MoodleAPI {

    @GET("login/token.php?service=moodle_mobile_app")
    Call<Token> login(@Query("username") String username, @Query("password") String password);


    @GET("webservice/rest/server.php?wsfunction=core_enrol_get_enrolled_users")
    Call<List<Student>> students(@Query("wstoken") String wstoken, @Query("courseid") String courseId);

//    @GET("webservice/rest/server.php?wsfunction=mod_assign_get_assignments&moodlewsrestformat=json")
//    Call<Courses> courses(@Query("wstoken") String wstoken);

    @GET("webservice/rest/server.php?wsfunction=mod_assign_get_assignments")
    Call<Courses> courses(@Query("wstoken") String wstoken, @Query("courseids[0]") String courseid);

    @GET("webservice/rest/server.php?wsfunction=mod_assign_get_submissions")
    Call<Assignments> submissions(@Query("wstoken") String wstoken, @Query("assignmentids[0]") String asssignId);

    @GET("webservice/rest/server.php?wsfunction=mod_assign_get_submissions")
    Call<Assignments> submissions(@Query("wstoken") String wstoken, @Query("assignmentids[]") List<String> asssignIds);

    @GET("webservice/rest/server.php?wsfunction=mod_forum_get_forum_discussions_paginated")
    Call<Discusiones> discussions(@Query("wstoken") String wstoken, @Query("forumid") String forumid);

    @GET("webservice/rest/server.php?wsfunction=mod_assign_get_assignments")
    Call<Courses> assignments(@Query("wstoken") String wstoken);
}