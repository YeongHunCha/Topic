package chacha.com.topic;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Cha on 2017-07-12.
 */

public class User {
    String Name;
    String Email;
    String PhotoUrl;

    public User(){}
    public User(String email, String name, String photoUrl) {
        Name = name;
        Email = email;
        PhotoUrl = photoUrl;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Email", Email);
        result.put("Name", Name);
        result.put("PhotoUrl", PhotoUrl);
        return result;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        Email = email;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }
}
