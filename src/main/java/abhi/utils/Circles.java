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
public class Circles {

    static int n;
    static long D;

    class Gear {

        Long size;
        Long cost;
        int pos;

        Gear(Long type, Long cost, int pos) {
            this.size = type;
            this.cost = cost;
            this.pos = pos;
        }
    }

    List<Gear> iterList = new ArrayList<>();
    List<Gear> gearList = new ArrayList<>();

    static class GearComparator implements Comparator<Gear> {
        public int compare(Gear g1, Gear g2) {
            return g1.size.compareTo(g2.size);
        }
    }

    public void getMinCost(String[] types, String[] costs) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            Gear gear = new Gear(Long.parseLong(types[i]), Long.parseLong(costs[i]), i);
            gearList.add(gear);
            iterList.add(gear);
        }

        Collections.sort(gearList, new GearComparator());
        for (int i = 0; i < iterList.size(); i++) {
            Long sgear = D - iterList.get(i).size;
            int j = findLowerGear(sgear);
            if (j == -1) {
                sb.append("0 ");
                continue;
            }

            long minPos = gearList.get(j).pos;
            long minCost = gearList.get(j).cost;
            for (int k = j + 1; k < gearList.size() && k != i; k++) {
                if (gearList.get(k).cost < minCost) {
                    minCost = gearList.get(k).cost;
                    minPos = gearList.get(k).pos;
                }
            }
            sb.append(minPos + 1).append(" ");
        }
        System.out.println(sb.toString());

    }

    private int findLowerGear(Long sgear) {

        int lo = 0;
        int hi = gearList.size() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (gearList.get(mid).size < sgear) {
                lo = mid + 1;
            } else if (gearList.get(mid).size > sgear) {
                hi = mid - 1;
            } else {
                return mid;     //Found the key at this position
            }
        }
        return -1;
    }

    public static void main(String[] args) throws Exception {

        long t1 = System.currentTimeMillis();
        BufferedReader in = new BufferedReader(new FileReader("/home/abhishek/abc.txt"));
        String[] str = in.readLine().split(" ");
        n = Integer.parseInt(str[0]);
        D = Long.parseLong(str[1]);

        str = in.readLine().split(" ");
        String[] cost = in.readLine().split(" ");
        in.close();

        Circles circles = new Circles();
        circles.getMinCost(str, cost);

        System.out.println("Time = " + (System.currentTimeMillis() - t1) + "ms");
    }

}
