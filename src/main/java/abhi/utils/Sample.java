package abhi.utils;

/**
 * Author : abhishek
 * Created on 9/12/15.
 */
public class Sample {


    public static void main(String[] args) throws Exception {

        String[] parts = "2000-2001".split(" ")[0].split("-");
        System.out.println(parts[0]);


        String[] scrs = "12/122".split("/");
        System.out.println(Float.parseFloat(scrs[0])/Float.parseFloat(scrs[1]));

    }
}
