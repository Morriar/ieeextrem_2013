
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public static void main(String[] args) throws IOException {
        int nbPal = 0;
        int from = readInt();
        int to = readInt();

        for(int value = from; value <= to; value++) {
            String bin = Integer.toBinaryString(value);
            int i = 0;
            int j = bin.length() - 1;

            boolean isDiff = false;
            while(i != j && i < bin.length() && j >= 0) {
                if(bin.charAt(i) != bin.charAt(j)) {
                    isDiff = true;
                    break;
                }
                i++;
                j--;
            }

            if(!isDiff) {
                nbPal++;
            }
        }
        System.out.println(nbPal);
    }

    static Integer readInt() {
        try {
            String str = new String();
            int in = System.in.read();
            while(in != -1 && in != 10 && in != 44) {
                str += (char)in;
                in = System.in.read();
            }
            return Integer.parseInt(str);
        } catch (Exception ex) {
            System.out.println("ERROR");
            System.exit(1);
        }
        return null;
    }

}
