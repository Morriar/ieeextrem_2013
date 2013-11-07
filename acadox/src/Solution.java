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
import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 */
public class Solution {
    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.parse();
    }
}

class Parser {
    Stack<Value> stack = new Stack<>();
    int countToken = 0;
    int countOperators = 0;

    void parse() {
        try {
            int next = System.in.read();
            String str = "";
            while(next != 10 && next != -1) {
                if(countToken > 20) {
                    System.out.println("ERROR");
                    System.exit(0);
                }
                char car = (char)next;

                if(car == ' ') {
                    Token token = parseToken(str);
                    applyToken(token);
                    str = "";
                } else {
                    str += car;
                }
                next = System.in.read();
            }
            Token token = parseToken(str);
            applyToken(token);

            displayResult();
        } catch (IOException ex) {
            System.out.println("ERROR");
            System.exit(0);
        }
    }

    Token parseToken(String str) {
        //System.out.println("parseToken: " + str);
        // value
        Pattern p = Pattern.compile("^[0-9A-Fa-f]{1,4}$");
        Matcher m = p.matcher(str);
        if(m.matches()) {
            return new Value(str);
        }
        // operator
        return Operator.getOperator(str);
    }

    void applyToken(Token token) {
        countToken++;
        //System.out.println("applyToken: " + token);
        if(token instanceof Value) {
            stack.push((Value)token);
        } else if(token instanceof Operator) {
            countOperators++;
            if(token instanceof UnaryOperator) {
                if(stack.isEmpty()) {
                    System.out.println("ERROR");
                    System.exit(0);
                }

                UnaryOperator operator = (UnaryOperator)token;
                Value value = stack.pop();
                value = operator.apply(value);
                stack.push(value);
            } else {
                BinaryOperator operator = (BinaryOperator)token;
                 if(stack.isEmpty()) {
                    System.out.println("ERROR");
                    System.exit(0);
                }
                Value a = stack.pop();
                 if(stack.isEmpty()) {
                    System.out.println("ERROR");
                    System.exit(0);
                }
                Value b = stack.pop();
                Value value = operator.apply(b, a);
                stack.push(value);
            }
        } else {
            System.out.println("ERROR");
            System.exit(0);
        }
    }

    void displayResult() {
        if(countOperators > 0) {
            Value value = stack.pop();
            if(!stack.isEmpty()) {
                System.out.println("ERROR");
                System.exit(0);
            }
            String hex = decToHex(value.value);
            System.out.println(hex);
        } else {
            System.out.println("ERROR");
            System.exit(0);
        }
    }

    private static final int sizeOfIntInHalfBytes = 4;
    private static final int numberOfBitsInAHalfByte = 4;
    private static final int halfByte = 0x0F;
    private static final char[] hexDigits = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    String decToHex(int dec) {
        StringBuilder hexBuilder = new StringBuilder(sizeOfIntInHalfBytes);
        hexBuilder.setLength(sizeOfIntInHalfBytes);
        for (int i = sizeOfIntInHalfBytes - 1; i >= 0; --i)
        {
          int j = dec & halfByte;
          hexBuilder.setCharAt(i, hexDigits[j]);
          dec >>= numberOfBitsInAHalfByte;
        }
        return hexBuilder.toString();
    }
}

class Token {

}


class Value extends Token {
    Integer value;

    public Value(String str) {
        this.value = Integer.parseInt(str, 16);
    }

    public Value(Integer i) {
        this.value = i;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

abstract class Operator extends Token {
    public static Operator getOperator(String str) {
        if(str.equals("+")) {
            return new PlusOperator();
        } else if(str.equals("-")) {
            return new MinusOperator();
        } else if(str.equals("&")) {
            return new AndOperator();
        } else if(str.equals("|")) {
            return new OrOperator();
        } else if(str.equals("~")) {
            return new NotOperator();
        } else if(str.equals("X")) {
            return new XorOperator();
        } else {
            return null;
        }
    }
}

abstract class UnaryOperator extends Operator {
    public abstract Value apply(Value value);
}

abstract class BinaryOperator extends Operator {
    public abstract Value apply(Value a, Value b);
}


class PlusOperator extends BinaryOperator {
    @Override
    public Value apply(Value a, Value b) {
        Integer res = a.value + b.value;
        if(res > 65535) {
            return new Value("FFFF");
        } else {
            return new Value(res);
        }
    }

    @Override
    public String toString() {
        return "+";
    }
}

class MinusOperator extends BinaryOperator {
    @Override
    public Value apply(Value a, Value b) {
        Integer res = a.value - b.value;
        if(res < 0) {
            return new Value(0);
        } else {
            return new Value(res);
        }
    }

    @Override
    public String toString() {
        return "-";
    }
}

class AndOperator extends BinaryOperator {
    @Override
    public Value apply(Value a, Value b) {
        return new Value(a.value & b.value);
    }

    @Override
    public String toString() {
        return "&";
    }
}

class OrOperator extends BinaryOperator {
    @Override
    public Value apply(Value a, Value b) {
        return new Value(a.value | b.value);
    }

    @Override
    public String toString() {
        return "|";
    }
}

class XorOperator extends BinaryOperator {
    @Override
    public Value apply(Value a, Value b) {
        return new Value(a.value ^ b.value);
    }

    @Override
    public String toString() {
        return "X";
    }
}

class NotOperator extends UnaryOperator {
    @Override
    public Value apply(Value value) {
        return new Value(~value.value);
    }

    @Override
    public String toString() {
        return "~";
    }
}
