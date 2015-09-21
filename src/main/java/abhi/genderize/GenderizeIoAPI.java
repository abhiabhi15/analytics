package abhi.genderize;

public class GenderizeIoAPI {

    private GenderizeIoAPI() { }
    
    public static Genderize create() {
        return new DefaultGenderize();
    }
}
