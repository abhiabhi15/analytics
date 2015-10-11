package abhi.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Author : abhishek
 * Created on 9/12/15.
 */
public class Sample {


    public static void main(String[] args) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter("/home/abhishek/test2.txt"));

        Integer n = 50000;
        int D = 5000;
        StringBuilder sb = new StringBuilder();
        sb.append(n).append(" ").append(D).append("\n");
        for (int i = 0; i < n; i++) {
            sb.append((int) (Math.random() * D)).append(" ");
        }
        sb.append("\n");
        for (int i = 0; i < n; i++) {
            sb.append((int) (Math.random() * D)).append(" ");
        }
        sb.append("\n");
        bw.write(sb.toString());
        bw.close();
    }
}
