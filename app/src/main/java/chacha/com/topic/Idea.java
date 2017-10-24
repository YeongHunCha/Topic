package chacha.com.topic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cha on 2017-07-08.
 */

public class Idea implements Serializable{
    public String content;
    public String contentImageUrl;
    public String uid;
    public String createdAt;
    public String timeStamp;
    public int loveCount = 0;
    public int descCount = 9999;
    public Map<String, Boolean> loves = new HashMap<>();

    public Idea(){}

    //getter, setter 넣으니깐 api24에서 에러
    //getter, setter 없으니깐 api19에서 에러

    public Idea(String content, String contentImageUrl, String uid, String createdAt, String timeStamp) {
        this.content = content;
        this.contentImageUrl = contentImageUrl;
        this.uid = uid;
        this.createdAt = createdAt;
        this.timeStamp = timeStamp;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("contentImageUrl", contentImageUrl);
        result.put("uid", uid);
        result.put("createdAt", createdAt);
        result.put("timeStamp", timeStamp);
        result.put("loveCount", loveCount);
        result.put("descCount", descCount);
        result.put("loves", loves);
        return result;
    }

    public String getContent() {        return content;    }
    public void setContent(String content) {        this.content = content;    }

    public String getContentImageUrl() {        return contentImageUrl;    }
    public void setContentImageUrl(String contentImageUrl) {        this.contentImageUrl = contentImageUrl;    }

    public String getCreatedAt() {        return createdAt;    }
    public void setCreatedAt(String createdAt) {        this.createdAt = createdAt;    }

    public String getTimeStamp() {        return timeStamp;    }
    public void setTimeStamp(String timeStamp) {        this.timeStamp = timeStamp;    }

    public int getLoveCount() {
        return loveCount;
    }
    public void setLoveCount(int loveCount) {
        this.loveCount = loveCount;
    }

    public int getDescCount() {        return descCount;    }
    public void setDescCount(int descCount) {
        this.descCount = descCount;
    }
}
