import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MoodleAPI {

    @GET("login/token.php?service=moodle_mobile_app")
    Call<Token> login(@Query("username") String username, @Query("password") String password);


    @GET("https://moodle.elpuig.xeill.net/webservice/rest/server.php?wsfunction=mod_assign_get_submissions&assignmentids[0]=10124&moodlewsrestformat=json")
    Call<Assignments> submissions(@Query("wstoken") String wstoken);

}


/*

Ver mis cursos
https://moodle.elpuig.xeill.net/webservice/rest/server.php?wsfunction=mod_assign_get_assignments&wstoken=1f8e4af9b0bdcd0e4d66b1c25d559965&moodlewsrestformat=json

Ver alumnos matriculados
https://moodle.elpuig.xeill.net/webservice/rest/server.php?wsfunction=core_enrol_get_enrolled_users&courseid=260&wstoken=1f8e4af9b0bdcd0e4d66b1c25d559965&moodlewsrestformat=json
 */
