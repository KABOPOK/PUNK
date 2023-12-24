package ru.kabopok.punk_jv.classes;

public class Photo {
    String URL;
    String cloudPath;
    public Photo(){
        this.URL = null;
        this.cloudPath = null;
    }
    public Photo(String URL, String cloudPath) {
        this.URL = URL;
        this.cloudPath = cloudPath;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getCloudPath() {
        return cloudPath;
    }

    public void setCloudPath(String cloudPath) {
        this.cloudPath = cloudPath;
    }
}
