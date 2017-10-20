package chacha.com.topic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cha on 2017-07-08.
 */

public class Idea implements Serializable{
    String profilePhoto;
    String content;
    String contentImageUrl;
    String profileName;
    String writer;
    String createdAt;

    public Idea(){}

    //getter, setter 넣으니깐 api24에서 에러
    //getter, setter 없으니깐 api19에서 에러

    public Idea(String profileName, String profilePhoto, String content, String contentImageUrl, String writer, String createdAt) {
        this.profilePhoto = profilePhoto;
        this.content = content;
        this.contentImageUrl = contentImageUrl;
        this.profileName = profileName;
        this.writer = writer;
        this.createdAt = createdAt;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("profilePhoto", profilePhoto);
        result.put("content", content);
        result.put("contentImageUrl", contentImageUrl);
        result.put("profileName", profileName);
        result.put("writer", writer);
        result.put("createdAt", createdAt);

        return result;
    }

    public String getProfilePhoto() {        return profilePhoto;    }
    public void setProfilePhoto(String profilePhoto) {        this.profilePhoto = profilePhoto;    }

    public String getContent() {        return content;    }
    public void setContent(String content) {        this.content = content;    }

    public String getContentImageUrl() {        return contentImageUrl;    }
    public void setContentImageUrl(String contentImageUrl) {        this.contentImageUrl = contentImageUrl;    }

    public String getProfileName() {        return profileName;    }
    public void setProfileName(String profileName) {        this.profileName = profileName;    }

    public String getWriter() {        return writer;    }
    public void setWriter(String writer) {        this.writer = writer;    }

    public String getCreatedAt() {        return createdAt;    }
    public void setCreatedAt(String createdAt) {        this.createdAt = createdAt;    }
}
