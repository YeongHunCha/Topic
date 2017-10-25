package chacha.com.topic;

/**
 * Created by Cha on 2017-10-26.
 */

public class Singleton {

    private static Singleton instance = null;
    private String city;

    private Singleton(){}

    public static Singleton getInstance(){
        if(instance == null){
            instance = new Singleton();
        }
        return instance;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;

    }

}