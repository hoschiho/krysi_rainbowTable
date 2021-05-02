package ch.hoschiho;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.security.MessageDigest;

public class Main {

    public static final int chainLength = 2000;
    public static final int tableLength = 2000;
    public static final int passwordLength = 7;
    public static final String toFind = "1d56a37fb6b08aa709fe90e12ca59e12";

    public static final char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
        'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static void main(String[] args) throws NoSuchAlgorithmException {

        HashMap<String, String> rainbowTable = new HashMap<>();
        createRainbowTable(rainbowTable);

        String startValue = (findStartValue(rainbowTable));
        System.out.println("password is: " + findInChain(startValue));

    }

    public static String hash(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return new BigInteger(1, bytes).toString(16);
    }

    //implements reducefunction as described on slide 3.27
    public static String reduce(String hash, int level) {
        StringBuilder reduced = new StringBuilder();
        BigInteger hashAsNumber = new BigInteger(hash, 16); //hash as natural number
        BigInteger H = hashAsNumber.add(BigInteger.valueOf(level)); // H = H+Stufe
        for (int i = 0; i < passwordLength; i++) {
            BigInteger r = H.mod(BigInteger.valueOf(chars.length)); // H mod |Z|
            H = H.divide(BigInteger.valueOf(chars.length)); // H div |Z|
            reduced.append(chars[r.intValue()]);
        }
        reduced.reverse();
        return reduced.toString();
    }

    public static String chain(String password) throws NoSuchAlgorithmException {
        //is hashing & reducing and returns the last reduced value of the chain
        for (int i = 0; i < chainLength; i++) {
            String hash = hash(password);
            password = reduce(hash, i);
        }
        return password;
    }


    public static void createRainbowTable(HashMap<String, String> rainbowTable) throws NoSuchAlgorithmException {
        //goes through the last 3 digits and changes one number. For a larger table, mor outer loops needs to be added.
        StringBuilder pw = new StringBuilder("0000000");
        for (int k = 0; k < chars.length; k++) {
            pw.setCharAt(4, chars[k]);
            if (rainbowTable.size() >= tableLength) {
                break;
            }

            for (int j = 0; j < chars.length; j++) {
                pw.setCharAt(5, chars[j]);
                if (rainbowTable.size() >= tableLength) {
                    break;
                }

                for (int i = 0; i < chars.length; i++) {
                    pw.setCharAt(6, chars[i]);
                    rainbowTable.put(chain(pw.toString()),// chaining the password and saves the last reduced value as key
                        pw.toString()); // & the original password as value
                    if (rainbowTable.size() >= tableLength) {
                        break;
                    }
                }
            }
        }
    }



    public static String findStartValue(HashMap<String, String> rainbowTable) throws NoSuchAlgorithmException {
        //starts chaining at the end and always goes a level down, until hash is found.
        for (int startLevel = chainLength - 1; startLevel >= 0; startLevel--) {
            String hash = toFind;
            for (int i = startLevel; i < chainLength; i++) {
                String password = reduce(hash, i);
                String value = rainbowTable.get(password); //if end value is found, return the startvalue of the table.
                if (value != null) {
                    return value;
                } else {
                    hash = hash(password);
                }
            }
        }
        return null;
    }

    public static String findInChain(String password) throws NoSuchAlgorithmException {
        //regular chaining function, but returns the password if found.
        for (int i = 0; i < chainLength; i++) {
            String hash = hash(password);
            if (hash.equals(toFind)) {
                return password;
            }
            password = reduce(hash, i);
        }
        return null;
    }







}


