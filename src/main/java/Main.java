package src.main.java;

import java.math.BigInteger;
import java.security.MessageDigest;
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


        // Generate the first 2000 passwords (padded to 7 characters using toBase36String)
        for (int i = 0; i < NUM_PASSWORDS; i++) {
            initialPasswords.add(toBase36String(i, PASSWORD_LENGTH));
        }

        // Build the rainbow table
        for (String startPassword : initialPasswords) {
            String current = startPassword;

            for (int step = 0; step < CHAIN_LENGTH; step++) {
                String hash = md5(current);
                current = reduce(hash, step);
            }

            rainbowTable.put(current, startPassword); // Save: end -> start
        }

        // print the first 4 steps of the first chain
        System.out.println("Test: First 4 steps of the first chain");
        String test = "0000000";
        for (int i = 0; i < 4; i++) {
            System.out.println(test);
            String hash = md5(test);
            System.out.println(hash);
            test = reduce(hash, i);
        }

        // crack the Hash
        String hashToCrack = "1d56a37fb6b08aa709fe90e12ca59e12";
        String result = crackHash(hashToCrack);

        if (result != null) {
            System.out.println("Klartext gefunden: " + result);
        } else {
            System.out.println("Nicht gefunden. Vermutlich ist das Passwort nicht in der Rainbow Table enthalten.");
        }

    }




    // Convert integer to a base36 password padded to length
    public static String toBase36String(int num, int length) {
        String base36 = Integer.toString(num, 36);
        return String.format("%" + length + "s", base36).replace(' ', '0');
    }

    // Hash string using MD5 and returns the hex string -> MessageDigest from ChatGPT
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // reduce function -> ChatGPT recommended BigInteger for this
    public static String reduce(String hash, int step) {

        // Convert full hash to a big integer
        BigInteger num = new BigInteger(hash, 16);

        // Add the current step
        num = num.add(BigInteger.valueOf(step));

        StringBuilder password = new StringBuilder();

        BigInteger charsetSize = BigInteger.valueOf(CHARSET.length);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            BigInteger[] divRem = num.divideAndRemainder(charsetSize);
            password.insert(0, CHARSET[divRem[1].intValue()]);
            num = divRem[0];
        }

        return password.toString();
    }

    public static String crackHash(String targetHash) {
        for (int i = CHAIN_LENGTH - 1; i >= 0; i--) {
            String tempHash = targetHash;
            String pwd = null;

            // Simulate steps from i to CHAIN_LENGTH - 1
            for (int j = i; j < CHAIN_LENGTH; j++) {
                pwd = reduce(tempHash, j);
                tempHash = md5(pwd);
            }

            // Check if this reduced password is an end of a chain
            if (pwd != null && rainbowTable.containsKey(pwd)) {
                String current = rainbowTable.get(pwd);

                for (int k = 0; k < CHAIN_LENGTH; k++) {
                    String hash = md5(current);
                    if (hash.equals(targetHash)) {
                        return current; // success
                    }
                    current = reduce(hash, k);
                }
            }
        }

        return null;
    }



}


