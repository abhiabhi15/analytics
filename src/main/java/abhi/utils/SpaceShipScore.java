package abhi.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Author : abhishek
 * Created on 10/10/15.
 */
public class SpaceShipScore {

    class Bucket {
        long spos;
        long epos;
        Long score;
        Integer id;
    }

    class Timer {
        int id;
        Long time;

        Timer(int id, Long time) {
            this.id = id;
            this.time = time;
        }
    }

    static class TimeComparator1 implements Comparator<Timer> {
        public int compare(Timer t1, Timer t2) {
            return t1.time.compareTo(t2.time);
        }
    }

    static class TimeComparator2 implements Comparator<Timer> {
        public int compare(Timer t1, Timer t2) {
            return t2.time.compareTo(t1.time);
        }
    }

    static class ScoreComparator implements Comparator<Bucket> {
        public int compare(Bucket b1, Bucket b2) {
            int diffScore = b1.score.compareTo(b2.score);
            if (diffScore == 0) {
                return b1.id.compareTo(b2.id);
            }
            return diffScore;
        }
    }


    public static void main(String[] args) throws Exception {

        SpaceShipScore spaceScore = new SpaceShipScore();
        BufferedReader in = new BufferedReader(new FileReader("/home/abhishek/abc.txt"));
        String line = in.readLine();
        spaceScore.getMinRaceScore(in);
        in.close();
    }

    private void getMinRaceScore(BufferedReader in) throws Exception {

        Map<Integer, Bucket> bucketMap = new HashMap<>();
        List<Timer> sTimer = new ArrayList<>();
        List<Timer> eTimer = new ArrayList<>();

        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = in.readLine()) != null) {
            String[] str = line.split(" ");
            Bucket bucket = new Bucket();
            bucket.id = Integer.parseInt(str[0]);

            bucketMap.put(bucket.id, bucket);

            sTimer.add(new Timer(bucket.id, Long.parseLong(str[1])));
            eTimer.add(new Timer(bucket.id, Long.parseLong(str[2])));
        }

        Collections.sort(sTimer, new TimeComparator2());
        Collections.sort(eTimer, new TimeComparator1());

        for (int i = 0; i < sTimer.size(); i++) {
            bucketMap.get(sTimer.get(i).id).spos = i;
        }

        for (int i = 0; i < eTimer.size(); i++) {
            bucketMap.get(eTimer.get(i).id).epos = i;
        }

        List<Bucket> bucketList = new ArrayList<>();
        for (Map.Entry<Integer, Bucket> entry : bucketMap.entrySet()) {
            Bucket bkt = entry.getValue();

            bkt.score = (bkt.epos < bkt.spos) ? bkt.epos : bkt.spos;
            bucketList.add(bkt);
        }

        Collections.sort(bucketList, new ScoreComparator());
        for (Bucket bucket : bucketList) {
            sb.append(bucket.id).append(" ").append(bucket.score).append("\n");
        }
        System.out.println(sb.toString());
    }


}
