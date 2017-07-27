package chacha.com.topic;

import java.io.Serializable;

/**
 * Created by Cha on 2017-07-08.
 */

public class Idea implements Serializable{
    String ProfilePhoto;
    String Content;
    String ContentImageUrl;
    String ProfileName;
    String Writer;

    public Idea(){}

    //getter, setter 넣으니깐 api24에서 에러
    //getter, setter 없으니깐 api19에서 에러

    public Idea(String profilePhoto, String content, String contentImageUrl, String profileName, String writer) {
        ProfilePhoto = profilePhoto;
        Content = content;
        ContentImageUrl = contentImageUrl;
        ProfileName = profileName;
        Writer = writer;
    }

    public String getWriter() {
        return Writer;
    }

    public void setWriter(String writer) {
        Writer = writer;
    }

    public String getProfilePhoto() {
        return ProfilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        ProfilePhoto = profilePhoto;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getContentImageUrl() {
        return ContentImageUrl;
    }

    public void setContentImageUrl(String contentImageUrl) {
        ContentImageUrl = contentImageUrl;
    }

    public String getProfileName() {
        return ProfileName;
    }

    public void setProfileName(String profileName) {
        ProfileName = profileName;
    }

}
