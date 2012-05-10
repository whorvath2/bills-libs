package com.doxsystems.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
This class contains a collection of static utility methods designed to operate on and/or return instances of {@link String String}.
 */
public final class Strings {

    private static final String ALL_NUMS = "^#?\\d+$", BLANK = "\\s+";
    public static final String NL = System.getProperty("line.separator", "\n");
    public static final String SP = " ";
    public static final String TAB = "\t";
    public static final String EMPTY = "";
    public static final String FULLSTOP = ".";

    /**
    This class is a utility class, and will never be instantiated.
     */
    private Strings() {
    }

    /**
    This method calculates and returns a parenthesized String representing the current date and time.
     */
    public static String dts() {
        return '(' + dtsNoParens() + ')';
    }

    /**
    This method calculates and returns a String comprising a date-time stamp (via {@link #dts() dts()} with a prepended space character.  (This allows the date-time stamp string to be appended to the end of another string with minimal effort.)
     */
    public static String trailingDts() {
        return ' ' + Strings.dts();
    }

    /**
    This method calculates and returns a String comprising a date-time stamp (via {@link #dts() dts()} with an appended space character.  (This allows the date-time stamp string to be prepended to the beginning of another string with minimal effort.)
     */
    public static String leadingDts() {
        return Strings.dts() + ' ';
    }

    /**
    This method calculates and returns a String representing the current date and time.
     */
    public static String dtsNoParens() {
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("M'/'d'/'yy' 'h:mm aa");
        return formatter.format(date);
    }

    /**
     * deletes all non-numeric characters from str
     * 
     * @param str
     * @return str without non-numeric characters
     */
    public static String keep_only_numbers(String str) {
        String retValue = "";
        retValue = str.replaceAll("[^\\d]", "");

        return retValue;
    }

    /**
    This method checks the str parameter to determine if it is null, the empty string (""), or composed entirely of whitespace characters.
    @return <code>true</code> if <code>str</code> is null, if <code>str</code> equals the empty string, or if <code>str</code> is composed entirely of whitespace characters; <code>false</code> otherwise.
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.trim().equals(""));
    }

    /**
    This method checks the str parameter to determine if it is non-null, and if it contains at least one character which is a letter (acc. to {@link Character#isLetter(char) Character.isLetter(char)}).
    @return <code>true</code> if <code>str</code> is not null nor the empty string, and if it contains at least one character which is a letter; <code>false</code> otherwise.
     */
    public static boolean hasLetter(String str) {
        if (Strings.isEmpty(str)) {
            return false;
        }
        for (int i = 0, n = str.length(); i < n; i++) {
            char c = str.charAt(i);
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    /**
    This method calculates and returns a {@link String String} with all occurrences of the character <code>remove</code> omitted from the beginning of <code>str</code>.  If the character does not occur at the beginning of <code>str</code>, a string equivalent to str will be returned.
    @param str The string to be analyzed for leading occurrences of the character <code>remove</code>.
    @param remove The character to be removed from the beginning of <code>str</code>.

     */
    public static String trimLeading(String str, char remove) {
        if (Strings.isEmpty(str)) {
            return str;
        }
        char[] chars = str.toCharArray();
        int i = 0;
        while (true) {
            char current = chars[i];
            if (current != remove) {
                break;
            }
            i++;
            if (i >= chars.length) {
                break;
            }
        }
        return String.valueOf(chars, i, chars.length - i);
    }

    /**
    This method calculates and returns a {@link String String} with all occurrences of the character <code>remove</code> omitted from the end of <code>str</code>.  If the character does not occur at the end of <code>str</code>, a string equivalent to str will be returned.  If <code>str</code> consists only of one or more occurrences of <code>remove</code>, the empty string will be returned.
    @param str The string to be analyzed for trailing occurrences of the character <code>remove</code>.
    @param remove The character to be removed from the end of <code>str</code>.
    @return a String equal to <code>str</code> with <code>remove</code> trimmed from its end. (Note that this may be the empty string (&quot;&quot;), if <code>str</code> is composed entirely of <code>remove</code>.)
     */
    public static String trimTrailing(String str, char remove) {
        String string = str;
        char[] chars = string.toCharArray();
        int i = chars.length - 1;
        while (true) {
            if (i < 0) {
                i = 0;
                break;
            }
            char current = chars[i];
            if (current != remove) {
                i++;
                break;
            }
            i--;
        }
        string = String.valueOf(chars, 0, i);
        return string;
    }

    /**
    This method calculates and returns a {@link String String} with all occurrences of the character <code>remove</code> omitted from the beginning and ending of <code>str</code>.  If the character does not occur in either the beginning or ending of <code>str</code>, a string equivalent to str will be returned.
    @param str The string to be analyzed for leading and trailing occurrences of the character <code>remove</code>.
    @param remove The character to be removed from the beginning and ending of <code>str</code>.
     */
    public static String trim(String str, char remove) {
        String string = Strings.trimLeading(str, remove);
        string = Strings.trimTrailing(string, remove);
        return string;
    }

    /**
    This method calculates and returns a {@link String String} with <code>numToRemove</code> characters omitted from the ending of <code>str</code>.  If <code>numToRemove</code> is less than zero, a string equivalent to <code>str</code> will be returned.  if <code>numToRemove</code> is equal to or greater than <code>str</code>'s length, the empty string will be returned.
    @param str The string whose last two <code>numToRemove</code> characters are to be removed.
    @param numToRemove The number of characters to remove from the end of <code>str</code>.
     */
    public static String trimLast(String str, int numToRemove) {
        int length = str.length();
        int stop = (numToRemove < 0)
                ? 0
                : ((numToRemove > length)
                ? length
                : numToRemove);

        return str.substring(0, length - stop);
    }

    /**
     * Ensures that the string is trimmed at n'th character. Where n = length.
     * 
     * @param s
     * @param length
     * @return
     */
    public static String ensureLength(String s, int length) {
        String retValue = s;
        if (s != null && s.length() > length) {
            retValue = s.substring(0, length);
        }
        return retValue;
    }

    /**
    This method examines a {@link String String} for the lines it contains, and returns a string in which all lines (including the first) are preceded by a number of tab characters (or their equivalence in space characters.)  Note that if numTabs is less than or equal to zero, this method will have no effect (i.e., it will return a String instance equal to <code>str</code>.);
    @param str The String whose lines are to be indented.
    @param numTabs The number of tabs (or blank space equivalents) to insert before each line.
    @return A String whose lines are each indented by <code>numTabs</code> tabs, or the empty string ("") if the <code>str</code> parameter is <code>null</code> or empty.
     */
    public static String indent(String str, int numTabs) {
        String tab = "   ";

        if (Strings.isEmpty(str)) {
            return "";
        }
        if (numTabs <= 0) {
            return str;
        }

        String indent = "";
        for (int i = 0; i < numTabs; i++) {
            indent += tab;
        }

        String result = "";

        String[] lines = str.split(Strings.NL);

        for (int i = 0; i < lines.length - 1; i++) {
            lines[i] = indent + lines[i];
            result += lines[i] + Strings.NL;
        }
        String last = lines[lines.length - 1];
        if (Strings.isEmpty(last)) {
            result += indent + Strings.NL;
        } else {
            result += indent + last;
        }
        return result;
    }

    /**
    This is a convenience method for accessing {@link #indent(String, int) indent} with an argument of one (1) for the <code>numTabs</code> parameter.
     */
    public static String indent(String str) {
        return Strings.indent(str, 1);
    }

    /**
    This method counts the number of times <code>countMe</code> appears in <code>str</code>.
    @param str The string in which to look for <code>countMe</code>.
    @param countMe The character to count in <code>str</code>.
    @return An int indicating how many occurrences of <code>countMe</code> are contained in <code>str</code>.
     */
    public static int frequency(String str, char countMe) {
        char[] chars = str.toCharArray();
        int counter = 0;
        for (int i = 0; i < chars.length; i++) {
            counter += (chars[i] == countMe) ? 1 : 0;
        }
        return counter;
    }

    /**
    This method calculates and returns an integer which indicates the number of times <code>count</code> appears at the end of <code>str</code>.  If the character does not occur at the end of <code>str</code>, zero will be returned.  If <code>str</code> consists only of one or more occurrences of <code>count</code>, str.length() will be returned.
    @param str The string to be analyzed for trailing occurrences of the character <code>count</code>.
    @param count The character whose frequency of occurence at the end of <code>str</code> is to be counted.
    @return An <code>int</code> indicating the number of times <code>count</code> occurs at the end of <code>str</code>.
     */
    public static int countTrailing(String str, char count) {
        int result = 0;
        char[] chars = str.toCharArray();
        int i = chars.length - 1;
        while (true) {
            if (i < 0) {
                break;
            }
            if (chars[i] != count) {
                break;
            }
            result++;
            i--;
        }
        return result;
    }

    /**
    This method calculates and returns an integer which indicates the number of times <code>count</code> appears at the end of <code>str</code>.  If <code>count</code> does not occur at the end of <code>str</code>, zero will be returned.  If <code>str</code> consists only of one or more occurrences of <code>count</code>, str.length() will be returned.
    @param str The string to be analyzed for trailing occurrences of the string <code>count</code>.
    @param count The string whose frequency of occurence at the end of <code>str</code> is to be counted.
    @return An <code>int</code> indicating the number of times <code>count</code> occurs at the end of <code>str</code>.
     */
    public static int countTrailing(String str, String count) {
        int result = 0;
        int countLength = count.length();
        String check = str;
        while (check.endsWith(count)) {
            result++;
            check = check.substring(0, check.length() - countLength);
        }
        return result;
    }

    /**

     */
    public static boolean startsWithIgnoreCase(String searchMe, String pattern) {
        String searchUpper = searchMe.toUpperCase();
        String patternUpper = pattern.toUpperCase();
        return searchUpper.startsWith(patternUpper);
    }

    /**

     */
    public static boolean endsWithIgnoreCase(String searchMe, String pattern) {
        String searchUpper = searchMe.toUpperCase();
        String patternUpper = pattern.toUpperCase();
        return searchUpper.endsWith(patternUpper);
    }

    /**

     */
    public static boolean matchesIgnoreCase(String searchMe, String pattern) {
        Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pat.matcher(searchMe);
        return matcher.matches();
    }

    /**
    Method introduced by Kavitha
    This method checks the searchMe string to look for the next sequence that
    matches the pattern wheras the matchesIgnoreCase() method looks to match
    the entire "searchMe" against the pattern. (in other words, the differnce between
    find() and matches() methods of Matcher.
    It then replaces the match with actual pattern parameter and returns the same.
     */
    public static String findReplaceIgnoreCase(String searchMe, String pattern, String delimiter) {
        Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pat.matcher(searchMe);
        StringBuffer sb = new StringBuffer();
        if (matcher.find()) {
            matcher.appendReplacement(sb, pattern);
            if (delimiter != null) {
                if (matcher.end() != searchMe.length()) {
                    sb.append(delimiter);
                }
            }
            matcher.appendTail(sb);
            return sb.toString();
        } else {
            return searchMe;
        }

    }

    /**

     */
    public static String abbreviate(String str) {

        String result = "";
        String[] words = str.split(" +");
        for (int i = 0; i < words.length; i++) {
            char[] chars = words[i].toCharArray();
            StringBuffer buff = new StringBuffer(chars.length);
            if (chars.length <= 3) {
                buff.append(chars);
                buff.append(' ');
            } else {
                int vowelCount = 0;
                for (int j = 0; j < chars.length; j++) {
                    if (isVowel(chars[j])) {
                        vowelCount++;
                    }
                }
                if (vowelCount > 2) {
                    boolean foundVowel = false;
                    for (int j = 0; j < chars.length; j++) {
                        char c = chars[j];
                        buff.append(c);
                        boolean isVowel = isVowel(c);
                        if (isVowel) {
                            foundVowel = true;
                        } else {
                            if (foundVowel && !isVowel) {
                                break;
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < chars.length; j++) {
                        char c = chars[j];
                        if (isVowel(c)) {
                            continue;
                        }
                        if (buff.length() < 4) {
                            buff.append(c);
                        } else {
                            break;
                        }
                    }
                }

                buff.append('.');
                buff.append(' ');
                int j = chars.length - 1;
                int q = Character.getType(chars[j]);

                if (punctuation(q)) {
                    buff.deleteCharAt(buff.length() - 1);
                    while (punctuation(q)) {
                        buff.append(chars[j]);
                        j--;
                        q = Character.getType(chars[j]);
                    }
                }
            }
            result += buff.toString();
        }
        return result.trim();
    }

    /**

     */
    public static boolean isVowel(char c) {
        return c == 'a' || c == 'A'
                || c == 'e' || c == 'E'
                || c == 'i' || c == 'I'
                || c == 'o' || c == 'O'
                || c == 'u' || c == 'U'
                || c == 'y' || c == 'Y';
    }

    public static boolean isNumeric(String t) {
        boolean isNumeric = false;

        isNumeric = java.util.regex.Pattern.matches("\\d+", t);


        return isNumeric;
    }

    /**
    @param aString The <code>String</code> to be compared to <code>bString</code>
    @param bString The <code>String</code> to be compared to <code>aString</code>
    @return An integer greater than zero if aString combes before bString, an integer less than zero if aString comes after bString, or zero if aString and bString are alphabetically equal.
     */
    public static int convertAndCompare(String aString, String bString) {

        if (aString == null) {
            if (bString == null) {
                return 0;
            }
            return -1;
        }
        if (bString == null) {
            return 1;
        }

        String[] aWords = aString.split(BLANK);
        String[] bWords = bString.split(BLANK);

        int result = 0;
        boolean aShorter = aWords.length < bWords.length;
        for (int i = 0, n = (aShorter ? aWords.length : bWords.length);
                i < n; i++) {
            if (!aWords[i].equalsIgnoreCase(bWords[i])) {
                if (aWords[i].matches(ALL_NUMS)) {
                    if (bWords[i].matches(ALL_NUMS)) {
                        try {
                            int a = Integer.parseInt(aWords[i]);
                            int b = Integer.parseInt(bWords[i]);
                            result = (a > b ? 1 : (a == b ? 0 : -1));
                        } catch (NumberFormatException e) {
                            try {
                                String aStr = Strings.trimLeading(aWords[i],
                                        '#');
                                String bStr = Strings.trimLeading(bWords[i],
                                        '#');
                                int a = Integer.parseInt(aStr);
                                int b = Integer.parseInt(bStr);
                                result = (a > b ? 1 : (a == b ? 0 : -1));
                            } catch (NumberFormatException f) {
                                f.printStackTrace();
                            }

                        }
                    } else {
                        result = 1;
                    }
                } else if (bWords[i].matches(ALL_NUMS)) {
                    result = -1;
                } else {
                    result = aWords[i].compareToIgnoreCase(bWords[i]);
                }
                break;
            }
        }
        if (result == 0) {
            if (aShorter) {
                result = -1;
            } else if (aWords.length > bWords.length) {
                result = 1;
            }
        }
        return result;
    }

    /**

     */
    private static boolean punctuation(int q) {
        return ((q == Character.CONNECTOR_PUNCTUATION
                || q == Character.DASH_PUNCTUATION
                || q == Character.END_PUNCTUATION
                || q == Character.FINAL_QUOTE_PUNCTUATION
                || q == Character.INITIAL_QUOTE_PUNCTUATION
                || q == Character.OTHER_PUNCTUATION
                || q == Character.START_PUNCTUATION)
                && q != '.');
    }

    /**

     */
    public String sortParagraphs(String input, String paraSep) {
        String[] groups = input.split(paraSep);
        // Paul Sep-19-08 : SortedMap map = new TreeMap() -> SortedMap<String, String> map = new TreeMap<String, String>()
        SortedMap<String, String> map =
                new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

        for (int i = 0; i < groups.length; i++) {
            String group = groups[i];
            String first = group.substring(0, group.indexOf("\n"));
            map.put(first, group);
        }
        // Paul Sep-18-08: List lit = new ArrayList() -> List<String> = new ArrayList<String>()
        List<String> list = new ArrayList<String>(map.values());
        String result = "";
        for (int i = 0, n = list.size(); i < n; i++) {
            // Paul Sep-18-08 : Removed unecessary (String) cast on list.get(i)
            result += list.get(i) + paraSep;
        }
        return result;
    }

    /**

     */
    public static String encode(String string) {
        String str = string.replaceAll("%", "%7E");
        str = str.replaceAll("/", "%2F");
        str = str.replaceAll("\\\\", "%5C");
        char[] chars = str.toCharArray();
        byte[] bytes = str.getBytes();
        StringBuffer sbuff = new StringBuffer(str);
        for (int i = 0, spos = 0; i < chars.length; i++, spos++) {
            int q = (int) chars[i];
            if (q < 48 || (q > 57 && q < 65) || (q > 90 && q < 97) || q > 122) {
                Byte b = new Byte(bytes[i]);
                String hex = "%" + Integer.toHexString(b.intValue());
                sbuff.replace(spos, spos + 1, hex);
                spos += 2;
            }
        }
        return sbuff.toString();
    }

    /**

     */
    public static String decode(String string) {
        String str = string.replaceAll("%7E", "%");
        str = str.replaceAll("%2F", "/");
        str = str.replaceAll("%5C", "\\\\");
        Pattern pattern = Pattern.compile("%(..)");
        Matcher matcher = pattern.matcher(str);
        StringBuffer sbuff = new StringBuffer();
        while (matcher.find()) {
            try {
                // Paul Aug-18-08 : changed group(2) -> group(1)
                char c = (char) Integer.parseInt(matcher.group(1), 16);
                str = String.valueOf(c);
            } catch (NumberFormatException e) {
            }
            matcher.appendReplacement(sbuff, str);
        }
        matcher.appendTail(sbuff);
        str = sbuff.toString();
        return str;
    }

    /**
     * Decodes a String, removing any escaped characters, and, if the String ends
     * with a integer, which is the case for Group names, the integer characters are
     * removed, prior to returning the String. For example, if the String
     * "Horvath,%20William%20Lee%20(wlh)%20(Not%20Read)" is received, it will return
     * "Horvath, William Lee (wlh) (Not Read)".
     * If the String "Front%20Desk%People%207" is received, which is the name of a group,
     * the String "Front Desk People" will be returned, rather than "Front Desk People 7",
     * which is what the decode(String) method would return.
     * Note. decode(String) does the heavy lifting.
     * @author Paul Wooten
     * @param string String to decode and remove the Group number.
     * @return string String decoded and absent of any Group number. Returns an empty String
     * "" if the parameter <code>String string</code> is a null reference, or if the array of
     * substrings of the parameter <code>String string</code> resulting from invoking the
     * split(" ") method is of size zero.
     * @see #decode(String)
     */
    public static String decodeGroupName(String string) {
        if (string == null) {
            return "";
        }
        // first, decode the string
        string = decode(string);
        // ensure that if the string is a group name, the group number is removed.
        String[] words = string.split(" ");
        if (words.length == 0) {
            return "";
        }

        try {

            Integer.parseInt(words[words.length - 1]);
            /*
             * If an integer was successfully parsed, that means that the last 'word',
             * should be omitted from the returned String. By word, I'm referring to
             * any sequence of consecutive characters occurring between two spaces.
             */
            string = string.substring(0, string.length() - words[words.length - 1].length());
        } catch (NumberFormatException ex) {
            /*
             * Was unable to parse an integer from the last 'word', which means that
             * the last 'word' does not represent the group number, and should not
             * be omitted from the returned String. By word, I'm referring to any
             * sequence of consecutive characters occurring between two spaces.
             */
        }
        string.trim();
        return string;
    }

    /**

     */
    public static boolean containsIgnoreCase(List searchMe, String target) {

        for (int i = 0, n = searchMe.size(); i < n; i++) {
            if (((String) searchMe.get(i)).equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * http://stackoverflow.com/questions/388461/padding-strings-in-java
     * http://www.xinotes.org/notes/note/308/
     * @param s
     * @param n
     * @return
     */
    public static String padRight(String s, int n, String replacement) {
        return String.format("%1$-" + n + "s", s).replaceAll(" ", replacement);
    }

    /**
     * http://stackoverflow.com/questions/388461/padding-strings-in-java
     * http://www.xinotes.org/notes/note/308/
     * @param s
     * @param n
     * @return
     */
    public static String padLeft(String s, int n, String replacement) {
        return String.format("%1$#" + n + "s", s).replaceAll(" ", replacement);
    }

    /**

     */
    public static String toString(String[] lines) {

        StringBuffer sbuff = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            sbuff.append(lines[i]);
            sbuff.append(Strings.NL);
        }
        return sbuff.toString();
    }

    /**
    Since this class will never be instantiated, this method will always return -1.
    @return <code>-1</code>.
     */
    @Override
    public int hashCode() {
        return -1;
    }

    /**
    Since this class will never be instantiated, this method will always return false.
    @return <code>false</code>.
     */
    @Override
    public boolean equals(Object obj) {
        return false;
    }

    /**
    Since this class will never be instantiated, this method will always return null.
    @return <code>null</code>.
     */
    @Override
    public String toString() {
        return null;
    }
}
