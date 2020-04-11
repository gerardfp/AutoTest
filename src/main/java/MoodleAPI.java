import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface MoodleAPI {

    @GET("login/token.php?service=moodle_mobile_app")
    Call<Token> login(@Query("username") String username, @Query("password") String password);


    @GET("webservice/rest/server.php?wsfunction=core_enrol_get_enrolled_users&moodlewsrestformat=json")
    Call<List<Student>> students(@Query("wstoken") String wstoken, @Query("courseid") String courseId);

//    @GET("webservice/rest/server.php?wsfunction=mod_assign_get_assignments&moodlewsrestformat=json")
//    Call<Courses> courses(@Query("wstoken") String wstoken);

    @GET("webservice/rest/server.php?wsfunction=mod_assign_get_assignments&moodlewsrestformat=json")
    Call<Courses> courses(@Query("wstoken") String wstoken, @Query("courseids[0]") String courseid);

    @GET("webservice/rest/server.php?wsfunction=mod_assign_get_submissions&moodlewsrestformat=json")
    Call<Assignments> submissions(@Query("wstoken") String wstoken, @Query("assignmentids[0]") String asssignId);
}