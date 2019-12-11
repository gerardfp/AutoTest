import java.util.List;

class Token {
    public String token;
    public String privatetoken;

}

class Student {
    String id;
    String username;
}

class Assignments {
    List<Assignment> assignments;
}

class Assignment {
    public String assignmentid;
    public List<Submission> submissions;
}

class Submission {
    String id;
    String userid;
    List<Plugin> plugins;
}

class Plugin {
    List<Filearea> fileareas;
}

class Filearea {
    List<MFile> files;
}

class MFile {
    String filename;
    String fileurl;
}


class Courses {
    List<Course> courses;
}

class Course {
    List<PAssignment> assignments;
}

class PAssignment {
    String id;
    String cmid;
}