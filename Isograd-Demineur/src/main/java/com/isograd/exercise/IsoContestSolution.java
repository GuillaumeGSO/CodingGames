/****************************************************************
 /***/
/***/
/** SOLUTION BY ylm*/
/***/
/***/
/********************************************************************/
/*******
 * Read input from System.in
 * Use System.out.println to ouput your result.
 * Use:
 * IsoContestBase.localEcho( String variable)
 * to display variable in a dedicated area.
 * ***/
package com.isograd.exercise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Scanner;
import java.util.stream.Collectors;

    public class IsoContestSolution {

        private static Scanner input() throws Exception {
            // return new Scanner(new FileInputStream(IsoContestBase.BASE_PATH + "input1.txt"));
            return new Scanner(System.in);
        }

        public static void main(String[] argv) throws Exception {
            //IsoContestBase.localEcho("=================================");
            try (Scanner sc = input().useLocale(Locale.ROOT)) {

                int H = sc.nextInt();
                int L = sc.nextInt();

                char[][] map = new char[H][L];

                Sq start = null;
                for (int i = 0; i < H; ++i) {
                    String s = sc.next();
                    for (int j = 0; j < L; ++j) {
                        map[i][j] = s.charAt(j);

                        if (map[i][j] == 'x') {
                            start = new Sq(i, j);
                            map[i][j] = '.';
                        }

                    }
                }

                boolean visited[][] = new boolean[H][L];

                Queue<Sq> queue = new LinkedList<>();
                queue.add(start);

                int count = 0;

                while (!queue.isEmpty()) {
                    Sq curr = queue.poll();

                    if (visited[curr.i][curr.j]) {
                        continue;
                    }

                    visited[curr.i][curr.j] = true;
                    ++count;

                    if (!hasNothingAround(curr.i, curr.j, H, L, map)) {
                        continue;
                    }

                    for (Sq neighbor : neighbors(curr.i, curr.j, H, L)) {
                        if (visited[neighbor.i][neighbor.j]) {
                            continue;
                        }
                        queue.add(neighbor);
                    }
                }

                System.out.println(count);
            }
        }

        private static List<Sq> neighbors(int i, int j, int H, int L) {
            List<Sq> res = new ArrayList<>();

            if (i > 0) {
                if (j > 0) {
                    res.add(new Sq(i - 1, j - 1));
                }
                if (j + 1 < L) {
                    res.add(new Sq(i - 1, j + 1));
                }
                res.add(new Sq(i - 1, j));
            }

            if (i + 1 < H) {
                if (j > 0) {
                    res.add(new Sq(i + 1, j - 1));
                }
                if (j + 1 < L) {
                    res.add(new Sq(i + 1, j + 1));
                }
                res.add(new Sq(i + 1, j));
            }

            if (j > 0) {
                res.add(new Sq(i, j - 1));
            }
            if (j + 1 < L) {
                res.add(new Sq(i, j + 1));
            }

            return res;
        }

        private static boolean hasNothingAround(int i, int j, int H, int L, char[][] map) {
            if (i > 0 && map[i - 1][j] == '*')
                return false;
            if (i > 0 && j > 0 && map[i - 1][j - 1] == '*')
                return false;
            if (i > 0 && j + 1 < L && map[i - 1][j + 1] == '*')
                return false;

            if (j > 0 && map[i][j - 1] == '*')
                return false;
            if (j + 1 < L && map[i][j + 1] == '*')
                return false;

            if (i + 1 < H && map[i + 1][j] == '*')
                return false;
            if (i + 1 < H && j > 0 && map[i + 1][j - 1] == '*')
                return false;
            if (i + 1 < H && j + 1 < L && map[i + 1][j + 1] == '*')
                return false;

            return true;
        }

        private static class Sq {
            final int i;
            final int j;

            public Sq(int i, int j) {
                this.i = i;
                this.j = j;
            }
        }
}
