
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright 2013 Alexandre Terrasa <alexandre@moz-code.org>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 *
 */
public class Solution {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        List<String> lines = readInput();
        // Get shows
        String[] parts = lines.get(0).split(" ");
        List<Integer> shows = new ArrayList<>();
        if(parts.length != Integer.parseInt(parts[0])+1){
            System.out.println("ERROR");
            System.exit(0);
        }
        for(int i = 1; i < parts.length; i++) {
            shows.add(Integer.parseInt(parts[i]));
        }
        //System.out.println("Shows: " + shows);

        // Get signatures
        parts = lines.get(1).split(" ");
        List<Pair<Integer, Integer>> signs = new ArrayList<>();
        if(parts.length != Integer.parseInt(parts[0])*2+1){
            System.out.println("ERROR");
            System.exit(0);
        }
        for(int i = 1; i < parts.length; i+=2) {
            int duration = Integer.parseInt(parts[i]);
            int level = Integer.parseInt(parts[i+1]);
            signs.add(new Pair<>(duration, level));
        }
        Collections.sort(signs, new PairComparator<Integer, Integer>());
        //System.out.println("Signs: " + signs);


        List<Integer> sortedShows = new ArrayList<>();
        Output sortie = null;
        List<Output> allZeOutputs = new ArrayList<>();
        for(Pair<Integer, Integer> sign : signs) {
            int[][] matrix = getSolutionMatrix(shows, sign.first);
            List<Integer> best = getOptimalSubset(matrix, shows);

            // remove subset from show
            List<Integer> sortedPartialShows = new ArrayList<>();
            for(Integer index : best) {
                sortedPartialShows.add(shows.get(index - 1));
                shows.remove(shows.get(index - 1));
            }
            //calculer overlap et level
            if(!sortedPartialShows.isEmpty()) {
                int zeLevel = sign.second;
                int zeOverlap = sign.first;
                for(Integer zeOverlapItereur : sortedPartialShows){
                    zeOverlap -= zeOverlapItereur;
                }
                sortie = new Output(sortedShows, zeOverlap, zeLevel);
                allZeOutputs.add(sortie);
                Collections.sort(sortedPartialShows);
                sortedShows.addAll(sortedPartialShows);
            }
            //System.out.println("PartialOrder: " +sortedPartialShows);
        }
        // add remaining shows
        Collections.sort(shows);
        sortedShows.addAll(shows);
        //System.out.println("Order: " + sortedShows);

        // display the first output line
        for(int i = 0; i< sortedShows.size(); i++){
            System.out.print(sortedShows.get(i));
            if ((i+1) < sortedShows.size()){
                System.out.print(" ");
            }
        }

        System.out.print("\n");

        boolean flag = false;
        for(Output o: allZeOutputs){
            if(o.overlap != 0){
                System.out.println("Overlap " + Math.abs(o.overlap) + " of Level " + o.level);
                flag = true;
            }
        }
        if(!flag) {
            System.out.println("Overlap Zero");
        } else {
            //calculer overlap et level
            if(!shows.isEmpty()) {
                Pair<Integer, Integer> sign = signs.get(signs.size() - 1);
                int zeLevel = sign.second;
                int zeOverlap = sign.first;
                for(Integer zeOverlapItereur : shows){
                    zeOverlap -= zeOverlapItereur;
                }
                sortie = new Output(shows, zeOverlap, zeLevel);
                allZeOutputs.add(sortie);
            }
        }
        // calculer overlap et levels
    }

    static List<String> readInput() {
        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = br.readLine();
            while(line != null) {
                lines.add(line);
                line = br.readLine();
            }
        } catch (Exception ioe) {
           System.out.println("IO error trying to read line!");
           System.exit(1);
        }
        return lines;
    }

    private static int[][] getSolutionMatrix(List<Integer> weights, int capacity) {
        int[][] matrix =  new int[weights.size() + 1][capacity + 1];
        for(int i = 1; i <= weights.size(); i++) {
            for (int j = 0; j <= capacity; j++) {
                if (j - weights.get(i-1)  >= 0) {
                    matrix[i][j] = Math.max(matrix[i-1][j], weights.get(i-1) + matrix[i-1][j-weights.get(i-1)]);
                } else {
                    matrix[i][j] = matrix[i-1][j];
                }
            }
        }
        return matrix;
    }

   private static List<Integer> getOptimalSubset(int[][] solutionMatrix, List<Integer> weights) {
        List<Integer> subset = new ArrayList<>();
        int numItems = 0;
        int i = solutionMatrix.length - 1;
        for (int j = solutionMatrix[0].length - 1; j >= 0 && i > 0;i--) {
            // If the item is in the optimal subset, add it and subtract its weight
            // from the column we are checking.
            if (solutionMatrix[i][j] != solutionMatrix[i-1][j]) {
                subset.add(numItems, i);
                j -= weights.get(i-1);
                numItems++;
            }
        }
        return subset.subList(0, numItems);
    }
}


class PairComparator<E, F> implements Comparator<Pair<E, F>> {

    @Override
    public int compare(Pair<E, F> t, Pair<E, F> t1) {
        return Integer.compare((Integer)t.second, (Integer)t1.second);
    }

}

class Pair<E, F> {
    E first;
    F second;

    public Pair(E first, F second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "first: " + first + " second: " + second;
    }


}

class Output {

    List<Integer> shows;
    int level, overlap;

    public Output(List<Integer> shows, int overlap, int level){
        this.shows = shows;
        this.level = level;
        this.overlap = overlap;
    }
}