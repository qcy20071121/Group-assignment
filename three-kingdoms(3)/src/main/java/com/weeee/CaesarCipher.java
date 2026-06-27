package com.weeee;

import java.util.Base64;
import java.util.Stack;

public class CaesarCipher {
    public static String decrypt(String text, int shift) {
        boolean capital = false;
        boolean reverse = false;
        StringBuilder res = new StringBuilder();
        Stack<Character> stk = new Stack<>();
        Stack<Integer> numStack = new Stack<>();
        numStack.push(0);

        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '&') {
                int start = i + 1;
                while (start < chars.length && chars[start] != '{') {
                    start++;
                }
                if (start < chars.length) {
                    String numStr = text.substring(i + 1, start);
                    try {
                        int num = Integer.parseInt(numStr);
                        numStack.push(num);
                        i = start; // Move i to '{'
                    } catch (NumberFormatException e) {
                        res.append(c);
                    }
                } else {
                    res.append(c);
                }
            } else if (c == '{') {
                numStack.push(0);
            } else if (c == '}') {
                if (numStack.size() > 1) {
                    numStack.pop();
                }
            } else if (c == '^') {
                capital = true;
            } else if (c == '$') {
                res.append(' ');
            } else if (c == '(') {
                reverse = true;
            } else if (c == ')') {
                reverse = false;
                while (!stk.empty()) {
                    res.append(stk.pop());
                }
            } else if (c < 'a' || c > 'z') {
                res.append(c);
            } else {
                int extra = numStack.peek();
                int totalShift = (shift + extra) % 26;
                char d = (char) ((c - 'a' - totalShift + 26) % 26 + 'a');
                if (capital) {
                    d = Character.toUpperCase(d);
                    capital = false;
                }
                if (reverse) {
                    stk.add(d);
                } else {
                    res.append(d);
                }
            }
        }
        return res.toString();
    }

    public static String encrypt(String text, int shift, int num) {
        int totalShift = (shift + num) % 26;
        String encrypted = encrypt(text, totalShift);
        return "&" + num + "{" + encrypted + "}";
    }

    public static String encrypt(String text, int shift) {
        StringBuilder res = new StringBuilder();
        StringBuilder word = new StringBuilder();

        shift = ((shift % 26) + 26) % 26;

        for (int i = 0; i <= text.length(); i++) {
            boolean end = i == text.length();
            char c = end ? '\0' : text.charAt(i);
            boolean isLetter = !end && ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
            if (isLetter) {
                word.append(c);
                continue;
            }

            if (word.length() > 0) {
                res.append('(');
                for (int j = word.length() - 1; j >= 0; j--) {
                    char original = word.charAt(j);
                    boolean isUpper = original >= 'A' && original <= 'Z';
                    char lower = isUpper
                            ? (char) (original - 'A' + 'a')
                            : original;
                    char encrypted = (char) ((lower - 'a' + shift) % 26 + 'a');
                    if (isUpper) res.append('^');
                    res.append(encrypted);
                }
                res.append(')');
                word.setLength(0);
            }

            if (end) break;
            if (c == ' ') res.append('$');
            else if (c == '(' || c == ')') 
                throw new IllegalArgumentException("Plain text cannot contain parentheses: " + c);
            else res.append(c);
        }
        return res.toString();
    }

    public static String encrypt2(String text, String key) {
        var keychars = key.toCharArray();
        StringBuilder res = new StringBuilder();
        for (char c : text.toCharArray()) {
            res.append((char) (c ^ keychars[res.length() % keychars.length]));
        }
        return Base64.getEncoder().encodeToString(res.toString().getBytes());
    }

    public static String decrypt2(String text, String key) {
        var keychars = key.toCharArray();
        byte[] decodedBytes = Base64.getDecoder().decode(text);
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < decodedBytes.length; i++) {
            res.append((char) (decodedBytes[i] ^ keychars[i % keychars.length]));
        }
        return res.toString();
    }
}
