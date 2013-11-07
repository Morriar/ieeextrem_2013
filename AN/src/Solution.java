
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    static List<String> words = new ArrayList<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        List<String> lines = readInput();

        // get message
        String encoded = lines.get(0);

        Map<Character, Integer> letters = new HashMap<>();

        for(int i = 0; i < encoded.length(); i++) {
            Character car = encoded.charAt(i);
            if(letters.containsKey(car)) {
                letters.put(car, letters.get(car) + 1);
            } else {
                letters.put(car, 0);
            }
        }
        System.out.println(letters);
        /*// parse dictionnary
        String dict = lines.get(3);
        String[] parts = dict.split(" ");
        for(String part : parts) {
            words.add(part);
        }

        // crack
        Map<Integer, Integer> blockCount = new HashMap<>();
        for(int i = 1; i <= 10; i++) {
            blockCount.put(i, 0);
            List<String> blocks = blockize(i, encoded);

            for(int key1 = 1; key1 < 26; key1++) {
                String rotated = rotX(key1, blocks.get(0));
                for(String word : words) {
                    if(rotated.contains(word)) {
                        blockCount.put(i, blockCount.get(i) + 1);
                    }
                }
            }
            /*for(int key2 = 1; key2 < 26; key2++) {
                for(String word : words) {
                    String rotated = rotX(key2, blocks.get(1));
                    if(rotated.contains(word)) {
                        blockCount.put(i, blockCount.get(i) + 1);
                    }
                }
            }
            for(int key3 = 1; key3 < 26; key3++) {
                for(String word : words) {
                    String rotated = rotX(key3, blocks.get(2));
                    if(rotated.contains(word)) {
                        blockCount.put(i, blockCount.get(i) + 1);
                    }
                }
            }*/
/*
        }
        System.out.println(blockCount);

        System.out.println(encoded);
        System.out.println(dict);*/
    }

    public static String rotX(Integer rot, String input) {
        String s = "";
        for (int i = 0; i < input.length(); i++) {
            char car = (char)(input.charAt(i) + rot);
            if(car > 'Z') {
                car = (char)((car - 'A') % 26);
                car = (char)(car + 'A');
            }
            s += car;
        }
        return s;
    }

    public static List<String> blockize(Integer length, String message) {
        List<String> blocks = new ArrayList<>();

        int i;
        for(i = 0; i < message.length(); i += length) {
            if(i + length < message.length()) {
                blocks.add(message.substring(i, i + length));
            } else {
                blocks.add(message.substring(i, message.length()));
            }
        }
        return blocks;
    }

    public static List<String> readInput() {
        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            lines.add(br.readLine());
            lines.add(br.readLine());
            lines.add(br.readLine());
            lines.add(br.readLine());
        } catch (Exception ioe) {
           System.out.println("IO error trying to read line!");
           System.exit(1);
        }
        return lines;
    }

}
