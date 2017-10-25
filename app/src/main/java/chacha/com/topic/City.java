package chacha.com.topic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cha on 2017-10-26.
 */

public class City {
    public String cityName;
    public String cityPhoto;

    public City() {}
    public City(String cityName, String cityPhoto) {
        this.cityName = cityName;
        this.cityPhoto = cityPhoto;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cityName", cityName);
        result.put("cityPhoto", cityPhoto);
        return result;
    }
}
