package src.main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    // variables
    static final char[] CHARSET = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
    static final int PASSWORD_LENGTH = 7;
    static final int CHAIN_LENGTH = 2000;
    static final int NUM_PASSWORDS = 2000;

    static Map<String, String> rainbowTable = new HashMap<>();


    public static void main(String[] args) {
        List<String> initialPasswords = new ArrayList<>();


        // Generate the first 2000 base36 passwords (padded to 7 characters using toBase36String)
        for (int i = 0; i < NUM_PASSWORDS; i++) {
            initialPasswords.add(toBase36String(i, PASSWORD_LENGTH));
        }




    }

    public static String toBase36String(int num, int length) {
        String base36 = Integer.toString(num, 36);
        return String.format("%" + length + "s", base36).replace(' ', '0');
    }



}


