package com.cervantesvirtual.util;

import com.cervantesvirtual.io.Messages;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Perform some normalization operations on text.
 *
 * @author R.C.C:
 * @version 2011.03.10
 */
public class Normalizer {

    final static java.text.Normalizer.Form decomposed = java.text.Normalizer.Form.NFD;
    final static java.text.Normalizer.Form composed = java.text.Normalizer.Form.NFC;
    final static java.text.Normalizer.Form compatible = java.text.Normalizer.Form.NFKC;

    static String stopwords = null;

    static {
        try {
            InputStream is = Normalizer.class.getResourceAsStream("/Normalizer.stopwords");
            BufferedReader reader = new java.io.BufferedReader(
                    new InputStreamReader(is, "UTF-8"));
            StringBuilder builder = new StringBuilder();

            if (reader.ready()) {
                // Do not use \s instead of \b as it does not match sequences of
                // stopwords such as "María de"
                String word = reader.readLine().trim();
                builder.append("\\b(" + word);
                while (reader.ready()) {
                    word = reader.readLine().trim();
                    builder.append('|').append(word);
                }
                builder.append(")\\b");
                stopwords = builder.toString();
            }
        } catch (IOException e) {
            Messages.severe("Could not read stopwords file");
        }

    }

    /**
     * @param s a string.
     * @return The string with its stopwords removed.
     */
    public static String removeStopwords(String s) {
        if (stopwords == null) {
            return s;
        } else {
            return reduceWS(s.replaceAll(stopwords, " "));
        }
    }

    /**
     * Remove accents (but keep tilde in ñ, Ñ and diaeresis in ü, Ü)
     *
     * @param s a string.
     * @return The string without diacritics (keeps only tilde in ñ, Ñ and
     * diaeresis in ü, Ü)
     */
    public static String removeDiacritics(String s) {
        String res = java.text.Normalizer.normalize(s, decomposed);
        return res.replaceAll("\u006E\u0303", "ñ")
                .replaceAll("\u004E\u0303", "Ñ")
                .replaceAll("\u0075\u0308", "ü")
                .replaceAll("\u0055\u0308", "Ü")
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Reduce whitespace (including line and paragraph separators)
     *
     * @param s a string.
     * @return The string with simple spaces between words.
     */
    public static String reduceWS(String s) {
        return s.replaceAll("(\\p{Space}|\u2028|\u2029)+", " ")
                .trim();
    }

    /**
     * Remove punctuation characters.
     *
     * @param s a string.
     * @return The string with only letters, digits and spaces.
     */
    public static String strip(String s) {
        return s.replaceAll("[^\\p{L}\\p{Digit}\\p{Space}]", "");
    }

    /**
     * @param s a string
     * @return the string with all punctuation symbols removed.
     */
    public static String removePunctuation(String s) {
        return s.replaceAll("\\p{P}+", "");
    }

    /**
     * @param s a string.
     * @return A normalized version of the string: lowercased after diacritics
     * and stopwords have been removed. Beware that whitespace must be
     * normalized because all (also leading/trailing) stopwords are replaced
     * with whitespace. Leading and trailing whitespaces are removed.
     */
    public static String normalize(String s) {
        String filtered = removeStopwords(s);
        String trimmed = reduceWS(strip(filtered)).trim();
        return removeDiacritics(trimmed).toLowerCase();
    }

    /**
     * @param s a string.
     * @return true if the string is normalized: it is lowercase and contains
     * neither diacritics nor stopword removed.
     */
    public boolean isNormal(String s) {
        return normalize(s).equals(s);
    }

    /**
     * @param s a string
     * @return the canonical representation of the string.
     */
    public static String toCanonical(String s) {
        return java.text.Normalizer.normalize(s, composed);
    }

    /**
     * @param s a string.
     * @return the string with characters <, >, &, " escaped
     */
    public static String encode(String s) {
        StringBuilder result = new StringBuilder();
        for (Character c : s.toCharArray()) {
            if (c.equals('<')) {
                result.append("&lt;");
            } else if (c.equals('>')) {
                result.append("&gt;");
            } else if (c.equals('"')) {
                result.append("&quot;");
            } else if (c.equals('&')) {
                result.append("&amp;");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
