import java.util.List;

class Token {
    public String token;
    public String privatetoken;

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
