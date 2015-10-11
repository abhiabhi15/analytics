package abhi.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author : abhishek
 * Created on 10/10/15.
 */
public class SpaceScore {

    static int n;

    class Ship {

        Long st_time;
        Long end_time;
        int id;

        Ship(int id, Long st_time, Long end_time) {
            this.st_time = st_time;
            this.end_time = end_time;
            this.id = id;
        }
    }

    class Printer {
        Integer id;
        Integer counter;

        Printer(Integer id, Integer counter) {
            this.id = id;
            this.counter = counter;
        }
    }

    List<Ship> iterList = new ArrayList<>();
    List<Ship> shipList = new ArrayList<>();

    static class TimeComparator implements Comparator<Ship> {
        public int compare(Ship s1, Ship s2) {
            return s1.st_time.compareTo(s2.st_time);
        }
    }

    static class ScoreComparator implements Comparator<Printer> {
        public int compare(Printer s1, Printer s2) {
            int score = s1.counter.compareTo(s2.counter);
            if (score == 0) {
                return s1.id.compareTo(s2.id);
            }
            return score;
        }
    }

    private int findGreaterStartTime(Long srt_time) {

        int lo = 0;
        int hi = shipList.size() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (shipList.get(mid).st_time < srt_time) {
                lo = mid + 1;
            } else if (shipList.get(mid).st_time > srt_time) {
                hi = mid - 1;
            } else {
                return mid;     //Found the key at this position
            }
        }
        return -1;
    }

    private void getMinRaceScore(BufferedReader in) throws Exception {
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = in.readLine()) != null) {
            String[] str = line.split(" ");
            Ship ship = new Ship(Integer.parseInt(str[0]), Long.parseLong(str[1]), Long.parseLong(str[2]));
            shipList.add(ship);
            iterList.add(ship);
        }

        Collections.sort(shipList, new TimeComparator());
        List<Printer> printerList = new ArrayList<>();

        for (int i = 0; i < iterList.size(); i++) {
            Ship src_ship = iterList.get(i);
            int j = findGreaterStartTime(src_ship.st_time);
            if (j == iterList.size() - 1) {
                printerList.add(new Printer(src_ship.id, 0));
                continue;
            }

            long minEndTime = src_ship.end_time;
            int counter = 0;

            for (int k = j; k < shipList.size(); k++) {
                Ship cship = shipList.get(k);
                if (cship.end_time < minEndTime) {
                    counter++;
                }
            }
            printerList.add(new Printer(src_ship.id, counter));
        }

        Collections.sort(printerList, new ScoreComparator());
        for (Printer printer : printerList) {
            sb.append(printer.id).append(" ").append(printer.counter).append("\n");
        }
        System.out.println(sb.toString());

    }

    public static void main(String[] args) throws Exception {

        SpaceScore spaceScore = new SpaceScore();
        BufferedReader in = new BufferedReader(new FileReader("/home/abhishek/abc.txt"));
        String line = in.readLine();
        n = Integer.parseInt(line);

        spaceScore.getMinRaceScore(in);
        in.close();

    }


}
