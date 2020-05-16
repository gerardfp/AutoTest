package com.example;

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
    public String id;
    public String cmid;
    public String assignmentid;
    public List<Submission> submissions;
}

class Submission {
    String id;
    String userid;
    List<Plugin> plugins;
    String gradingstatus;
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
    List<Assignment> assignments;
}

class Course {
    String id;
    String fullname;
    List<PAssignment> assignments;
}

class PAssignment {
    String id;
    String cmid;
    String name;
}

class Discusiones {
    List<Discusion> discussions;
}

class Discusion {
    int id;
    String name;
    String subject;
    String message;
}