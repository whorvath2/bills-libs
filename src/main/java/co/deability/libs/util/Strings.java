package co.deability.libs.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;


/**
 * This class contains a collection of static utility methods designed to operate on instances of
 * {@link String}.
 *
 * The general philosophy of these methods is built around support for human consumption,
 * rather than machine consumption. I.e., absent other information, a null string,
 * the empty string, or a string composed entirely of whitespace are indistinguishable to a human
 * and are semantically equivalent, in that they literally mean nothing. As such, the methods here
 * tend to treat such strings as equivalent, and will usually trim trailing and leading whitespace
 * from supplied strings prior to performing whatever operations they're designed to do.
 *
 */
public final class Strings {

	private static final String
			ALL_NUMS = "^#?\\d+$",
			BLANK = "\\s+",
			SIMPLE_US_FORMAT = "M'/'d'/'yyyy' 'h:mm aa";

	private static final DateFormat HUMAN_FRIENDLY_US_FORMATTER = new SimpleDateFormat(
			SIMPLE_US_FORMAT);

	/**
	 * The line separator for the OS in which the JVM is operating, expressed as a string.
	 */
	public static final String
			NL = System.getProperty("line.separator", "\n"),
			TB = "\t",
			IN = "    ",
			SP = " ",
			EM = "";

	/**
	 * This class is a utility class, and will never be instantiated.
	 */
	private Strings() { }


	/**
	 * Returns a parenthesized String representing the current date and time; i.e., a date time
	 * stamp.
	 *
	 * @return a parenthesized String representing the current date and time.
	 * @see #dts()
	 */
	public static String parensDts() {
		return '(' + dts() + ')';
	}


	/**
	 * Returns a string representing the JVM's current date and time with minute-level
	 * specificity and in a format easily consumable by humans; e.g.:
	 *
	 * <ul>
	 *     <li>1/1/2010 1:01 AM</li>
	 *     <li>12/12/2015 11:32 PM</li>
	 *     <li>10/5/1988 12:14 PM</li>
	 * </ul>
	 *
	 * @return a String representing the current date and time.
	 */
	public static String dts() {
		Date date = new Date(System.currentTimeMillis());
		return HUMAN_FRIENDLY_US_FORMATTER.format(date);
	}


	/**
	 * Returns the supplied string with all non-numeric characters removed; e.g.:
	 * <ul>
	 *     <li>Strings.keepOnlyNumbers("1234") returns "1234" </li>
	 *     <li>Strings.keepOnlyNumbers("a1b2c3d4e") returns "1234"</li>
	 *     <li>Strings.keepOnlyNumbers("foobar") returns ""</li>
	 * </ul>
	 * This method throws a NullPointerException if the supplied string is {@code null}.
	 *
	 * @param str The string from which non-numeric characters should be removed.
	 * @return The supplied string with all non-numeric character removed.
	 * @throws NullPointerException if str is null.
	 */
	public static String keepOnlyNumbers(String str) {
//		return str.replaceAll("[^\\d]", EM); //todo test performance vs functional approach
		return str.chars()
				.filter(Character::isDigit)
				.mapToObj(String::valueOf)
				.reduce(EM, String::concat);
	}


	/**
	 * Returns {@code true} if the supplied string is null, the empty string (""), or composed
	 * entirely of whitespace characters; {@code false} otherwise. I.e., returns {@code true} if
	 * the supplied string has no human-consumable (visible) content, and {@code false} if it
	 * does not.
	 *
	 * @param str the string to be evaluated for content that may be human-consumable.
	 * @return {@code true} if the supplied string is null, the empty string (""), or composed
	 * 	 * entirely of whitespace characters; {@code false} otherwise.
	 */
	public static boolean isEmpty(String str) {
		return (str == null || str.trim().isEmpty());
	}


	/**
	 * Returns the inverse of {@link #isEmpty(String)}. I.e., returns {@code true} if the
	 * supplied string has human-consumable (visible) content; {@code false} otherwise.
	 *
	 * @param str The string to be evaluated for human-consumable content.
	 * @return {@code true} if the supplied string may have human-consumable content; {@code
	 * false} otherwise.
	 *
	 * @see #isEmpty(String)
	 */
	public static boolean isNotEmpty(String str) {
		return (!isEmpty(str));
	}


	/**
	 * Returns {@code true} if the supplied string is non-null and contains at least one character 
	 * which is a letter (acc. to {@link Character#isLetter(char)}); {@code false} otherwise.
	 *
	 * @param str The string to be checked for alphabetic content.
	 * @return {@code true} if the supplied string is non-null contains at least one character
	 * which is a letter; {@code false} otherwise.
	 */
	public static boolean hasLetter(String str) {
		if (Strings.isEmpty(str)) {
			return false;
		}
		return str.chars()
				.parallel()
				.filter(Character::isLetter)
				.findAny()
				.isPresent();
	}


	/**
	 * Returns a string with all occurrences of the supplied character omitted from the
	 * beginning of the supplied string. If the character to be removed does not occur at least
	 * once starting at index 0 of the string's underlying array, a string that is equal to the
	 * supplied string will be returned.
	 *
	 * <p>If the supplied string is {@link #isEmpty(String) is empty}, it will be returned
	 * without modification, <em>regardless of which character to be removed is supplied.</em> This
	 * may result in a {@code null} value being returned.</p>
	 *
	 * <p>Examples of how this method behaves:
	 * <ul>
	 *     <li>{@code Strings.trimLeading("foobarf", 'f')} will return "oobarf"</li>
	 *     <li>{@code Strings.trimLeading("fffffoobar", 'f')} will return "oobar"</li>
	 *     <li>{@code Strings.trimLeading("foobar", 'o')} will return "foobar"</li>
	 *     <li>{@code Strings.trimLeading(" foobar", 'f')} will return " foobar"</li>
	 *     <li>{@code Strings.trimLeading("  \t\n  ", ' ')} will return "  \t\n "</li>
	 *     <li>{@code Strings.trimLeading(null, 'f')} will return {@code null}</li>
	 * </ul>
	 *
	 * @param str    The string to be analyzed for leading occurrences of the character
	 *               {@code remove}.
	 * @param remove The character to be removed from the beginning of {@code str}.
	 * @return A string with all occurrences of {@code remove} omitted from the beginning.
	 */
	public static String trimLeading(String str, char remove) {
		if (Strings.isEmpty(str)) return str;
		int i = 0;
		for (char c: str.toCharArray()){
			if (c != remove) {
				break;
			}
			i++;
		}
		return str.substring(i);
	}


	/**
	 * Returns a string with all occurrences of the supplied character omitted from the
	 * end of the supplied string. If the character to be removed does not occur at least
	 * once at the last index of the string's underlying array, a string that is
	 * equal to the supplied string will be returned.
	 *
	 * <p>If the supplied string is {@link #isEmpty(String) is empty}, it will be returned
	 * without modification, <em>regardless of which character to be removed is supplied.</em> This
	 * may result in a {@code null} value being returned.</p>
	 *
	 * <p>Examples of how this method behaves:</p>
	 * <ul>
	 *     <li>{@code Strings.trimTrailing("foobar", 'r')} will return "fooba"</li>
	 *     <li>{@code Strings.trimTrailing("foobarrrr", 'r')} will return "fooba"</li>
	 *     <li>{@code Strings.trimTrailing("foobar", 'a')} will return "foobar"</li>
	 *     <li>{@code Strings.trimTrailing("foobar ", 'r')} will return "foobar "</li>
	 *     <li>{@code Strings.trimTrailing("  \t\n  ", ' ')} will return "  \t\n "</li>
	 *     <li>{@code Strings.trimTrailing(null, 'r')} will return {@code null}</li>
	 * </ul>
	 *
	 * @param str    The string to be analyzed for trailing occurrences of the character
	 *               {@code remove}.
	 * @param remove The character to be removed from the end of {@code str}.
	 * @return A string with all occurrences of {@code remove} omitted from the end.
	 */

	public static String trimTrailing(String str, char remove) {
		if (Strings.isEmpty(str)) return str;
		char[] chars = str.toCharArray();
		int i = chars.length;
		while (i > 0) {
			i--;
			if (chars[i] != remove) {
				i++;
				break;
			}
		}
		return str.substring(0, i);
	}


	/**
	 * Returns a string with all occurrences of the character {@code remove} omitted from the
	 * beginning and end of {@code str}. If the supplied character does not occur at least once
	 * at the beginning or end of the supplied string's underlying character array, the supplied
	 * string will be returned.
	 *
	 * <p>If the supplied string is {@link #isEmpty(String) is empty}, it will be returned
	 * without modification, <em>regardless of which character to be removed is supplied.</em> This
	 * may result in a {@code null} value being returned.</p>

	 * <p>Examples of how this method behaves:
	 * <ul>
	 *     <li>{@code Strings.trim("foobar", 'r')} will return "fooba"</li>
	 *     <li>{@code Strings.trim("foobarrrr", 'f')} will return "oobarrrr"</li>
	 *     <li>{@code Strings.trim("rawr", 'r')} will return "aw"</li>
	 *     <li>{@code Strings.trim(" foobar ", 'r')} will return "foobar "</li>
	 *     <li>{@code Strings.trim("  \t\n  ", ' ')} will return "  \t\n "</li>
	 *     <li>{@code Strings.trim(null, 'r')} will return {@code null}</li>
	 * </ul>
	 *
	 * @param str    The string to be analyzed for leading and trailing occurrences of the
     *                  character {@code remove}.
	 * @param remove The character to be removed from the beginning and ending of {@code str}.
	 * @return The supplied string with all occurrences of the character {@code remove} omitted
	 * from its beginning and end.
	 * @see #trimTrailing(String, char)
	 * @see #trimLeading(String, char)
	 *
	 */
	public static String trim(String str, char remove) {
		return Strings.trimTrailing(Strings.trimLeading(str, remove), remove);

	}


	/**
	 * Returns the supplied string with {@code numToRemove} characters omitted from its end. If
	 * {@code numToRemove} is less than or equal to zero, the supplied string will be returned.
	 * if {@code numToRemove} is equal to or greater than the supplied string's length, the
	 * empty string will be returned.
	 *
	 * @param str         The string whose last two {@code numToRemove} characters are to be
	 *                    removed.
	 * @param numToRemove The number of characters to remove from the end of {@code str}.
	 * @return
	 */
	public static String trimLast(String str, int numToRemove) {
		int length = str.length();
		int stop = (numToRemove < 0)
				? 0
				: (Math.min(numToRemove, length));

		return str.substring(0, length - stop);
	}


	/**
	 * Returns the supplied string after all lines (including the first) are prefixed by
	 * the supplied number of tab characters, or their equivalence in four-space ("    ")
	 * strings, as determined by the supplied boolean. The lines in the supplied string are
	 * identified as such by the system-specific line separator character for the JVM. If the
	 * supplied string {@link #isEmpty(String) is empty}, a string of tabs or spaces that is as
	 * long as is specified by {@code numTabs} will be returned.
	 *
	 * <p>If the supplied number of tabs is less than or equal to zero, this method will
	 * have no effect, and the supplied string will be returned unaltered. Note this may result
	 * in a {@code null} value being returned.</p>
	 *
	 * @param str     The String whose lines are to be indented.
	 * @param numTabs The number of tabs (or blank space equivalents) to insert before each line.
	 * @param useTabs Whether to use tabs (true) or four-space strings (false) as the means of
	 *                   indenting.
	 * @return The supplied string with each line indented by {@code numTabs} tabs (or their space
	 * equivalent).
	 */
	public static String indent(String str, int numTabs, boolean useTabs) {
		if (numTabs <= 0) {
			return str;
		}
		String holder = EM;
		for (int i = 0; i < numTabs; i++) {
			holder += (useTabs) ? TB : IN;
		}
		final String indent = holder;
		if (Strings.isEmpty(str)) {
			return indent;
		}
		String[] lines = str.split(Strings.NL);
		return Arrays.stream(lines)
				.map(line -> indent + line)
				.collect(Collectors.joining(Strings.NL));
	}


	/**
	 * Returns the supplied string after its lines have been indented by one tab each. This is a
	 * convenience method for accessing {@link #indent(String, int, boolean) indent}
	 * with default arguments of {@code 1} and {@code true}.
	 *
	 * @param str The string to be indented.
	 * @return The supplied string after its lines have been indented by one tab each.
	 */
	public static String indent(String str) {
		return Strings.indent(str, 1, true);
	}


	/**
	 * Returns the number of times the supplied character appears in the supplied string.
	 *
	 * @param str     The string in which to look for {@code countMe}.
	 * @param countMe The character to look for in {@code str}.
	 * @return how many times {@code countMe} appears in {@code str}.
	 */
	public static long frequency(String str, char countMe) {
		return str.chars().parallel().filter(c -> c == countMe).count();
	}


	/**
	 * This method calculates and returns an integer which indicates the number of times
	 * {@code count} appears at the end of {@code str}. If the supplied character does not occur
	 * at the end of {@code str}, zero will be returned. If {@code str} consists only of
	 * one or more occurrences of the supplied character, str.length() will be returned.
	 *
	 * @param str   The string to be analyzed for trailing occurrences of the character
	 *              {@code count}.
	 * @param count The character whose frequency of occurence at the end of {@code str} is to
	 *              be counted.
	 * @return An {@code int} indicating the number of times {@code count} occurs at the
	 * end of {@code str}.
	 */
	public static int countTrailing(String str, char count) {
		int result = 0;
		if (Strings.isNotEmpty(str)) {
			char[] chars = str.toCharArray();
			int i = chars.length - 1;
			while (i >= 0 && chars[i] == count) {
				result++;
				i--;
			}
		}
		return result;
	}


	/**
	 * This method calculates and returns an integer which indicates the number of times
	 * {@code count} appears at the end of {@code str}. If {@code count} does not  occur at the
	 * end of {@code str}, zero will be returned. <em>If either of the supplied strings is null,
	 * no exception will be thrown, and 0 will be returned.</em>
	 *
	 * @param str   The string to be analyzed for trailing occurrences of the string
	 *              {@code count}.
	 * @param count The string whose frequency of occurence at the end of {@code str} is to be
	 *              counted.
	 * @return An {@code int} indicating the number of times {@code count} occurs at the
	 * end of {@code str}.
	 */
	public static int countTrailing(String str, String count) {
		if (str == null || count == null) return 0;
		int result = 0;
		int countLength = count.length();
		String check = str;
		while (check.length() > 0 && check.endsWith(count)) {
			result++;
			check = check.substring(0, check.length() - countLength);
		}
		return result;
	}


	/**
	 * Returns {@code true} if {@code searchMe} starts with {@code pattern}
	 * @param searchMe
	 * @param pattern
	 * @return
	 */
	public static boolean startsWithIgnoreCase(String searchMe, String pattern) {
		String searchUpper = searchMe.toUpperCase();
		String patternUpper = pattern.toUpperCase();
		return searchUpper.startsWith(patternUpper);
	}


	/**
	 *
	 * @param searchMe
	 * @param pattern
	 * @return
	 */
	public static boolean endsWithIgnoreCase(String searchMe, String pattern) {
		String searchUpper = searchMe.toUpperCase();
		String patternUpper = pattern.toUpperCase();
		return searchUpper.endsWith(patternUpper);
	}


	/**
	 *
	 * @param searchMe
	 * @param pattern
	 * @return
	 */
	public static boolean matchesIgnoreCase(String searchMe, String pattern) {
		Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pat.matcher(searchMe);
		return matcher.matches();
	}


	/**
	 * Method introduced by Kavitha This method checks the searchMe string to look for the next
	 * sequence that matches the pattern wheras the matchesIgnoreCase() method looks to match the
	 * entire "searchMe" against the pattern. (in other words, the differnce between find() and
	 * matches() methods of Matcher. It then replaces the match with actual pattern parameter and
	 * returns the same.
	 *
	 * @param delimiter
	 * @param pattern
	 * @param searchMe
	 * @return
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
		}
		else {
			return searchMe;
		}

	}


	/**
	 *
	 * @param str
	 * @return
	 */
	public static String abbreviate(String str) {

		String result = EM;
		String[] words = str.split(" +");
		for (String word : words) {
			char[] chars = word.toCharArray();
			StringBuffer buff = new StringBuffer(chars.length);
			if (chars.length <= 3) {
				buff.append(chars);
				buff.append(' ');
			}
			else {
				int vowelCount = 0;
				for (char aChar : chars) {
					if (isVowel(aChar)) {
						vowelCount++;
					}
				}
				if (vowelCount > 2) {
					boolean foundVowel = false;
					for (char c : chars) {
						buff.append(c);
						boolean isVowel = isVowel(c);
						if (isVowel) {
							foundVowel = true;
						}
						else {
							if (foundVowel && !isVowel) {
								break;
							}
						}
					}
				}
				else {
					for (char c : chars) {
						if (isVowel(c)) {
							continue;
						}
						if (buff.length() < 4) {
							buff.append(c);
						}
						else {
							break;
						}
					}
				}

				buff.append('.');
				buff.append(' ');
				int j = chars.length - 1;

				if (isPunctuation(chars[j])) {
					buff.deleteCharAt(buff.length() - 1);
					while (isPunctuation(chars[j])) {
						buff.append(chars[j]);
						j--;
					}
				}
			}
			result += buff.toString();
		}
		return result.trim();
	}


	/**
	 *
	 * @param c
	 * @return
	 */
	public static boolean isVowel(char c) {
		return c == 'a' || c == 'A'
				|| c == 'e' || c == 'E'
				|| c == 'i' || c == 'I'
				|| c == 'o' || c == 'O'
				|| c == 'u' || c == 'U'
				|| c == 'y' || c == 'Y';
	}


	/**
	 * Returns {@code true} if the supplied string is composed entirely of numbers; {@code false}
	 * otherwise. Note that this method does not determine whether str is a long, an integer, or
	 * any other particular flavor of whole number, and that it may exceed the maximum possible
	 * values of any such object.
	 *
	 * @param str The string to be evaluated for being 'numbers-only.'
	 * @return {@code true} if the supplied string is composed entirely of numbers; {@code false}
	 * otherwise.
	 */
	public static boolean isNumeric(String str) {
		return Strings.isNotEmpty(str) && Pattern.matches("\\d+", str.trim());
	}


	/**
	 * Returns -1, 0, or 1 if {@code aString} is alphabetically before, equivalent to,
	 * or after {@code bString}, in case-insensitive fashion, and after taking into account the
	 * value of any integers that may be contained in either string (so long as those integers
	 * are surrounded by space characters.)
	 *
	 * White space at the beginning and end of either string is ignored. All else being equal, a
	 * shorter string (e.g., "foo") is considered to be alphabetically before one that is longer
	 * (e.g., "foo bar.")
	 * 
	 * @param aString The {@code String} to be compared to {@code bString}
	 * @param bString The {@code String} to be compared to {@code aString}
	 * @return An integer greater than zero if aString comes before bString, an integer less than
	 * zero if aString comes after bString, or zero if aString and bString are alphabetically
	 * equal.
	 */
	public static int convertAndCompare(String aString, String bString) {

		// If either is empty, the other comes first. If both are empty, they're semantically equal.
		if (Strings.isEmpty(aString)) return (Strings.isEmpty(bString)) ? 0 : -1;
		if (Strings.isEmpty(bString)) return 1;

		// Get rid of spaces at the beginning and end, and multiple spaces in the middle, then
		// split around the spaces
		String[] aWords = aString.trim().replaceAll(BLANK, " ").split(BLANK);
		String[] bWords = bString.trim().replaceAll(BLANK, SP).split(BLANK);

		int result = 0;
		boolean aShorter = aWords.length < bWords.length;
		for (int i = 0, n = (aShorter ? aWords.length : bWords.length); i < n; i++) {
			if (!aWords[i].equalsIgnoreCase(bWords[i])) {
				if (aWords[i].matches(ALL_NUMS)) {
					if (bWords[i].matches(ALL_NUMS)) {
						try {
							int a = Integer.parseInt(aWords[i]);
							int b = Integer.parseInt(bWords[i]);
							result = (Integer.compare(a, b));
						}
						catch (NumberFormatException e) {
							try {
								String aStr = Strings.trimLeading(aWords[i],
										'#');
								String bStr = Strings.trimLeading(bWords[i],
										'#');
								int a = Integer.parseInt(aStr);
								int b = Integer.parseInt(bStr);
								result = (Integer.compare(a, b));
							}
							catch (NumberFormatException f) {
								f.printStackTrace();
							}

						}
					}
					else {
						result = 1;
					}
				}
				else if (bWords[i].matches(ALL_NUMS)) {
					result = -1;
				}
				else {
					result = aWords[i].compareToIgnoreCase(bWords[i]);
				}
				break;
			}
		}
		if (result == 0) {
			if (aShorter) {
				result = -1;
			}
			else if (aWords.length > bWords.length) {
				result = 1;
			}
		}
		return result;
	}


	/**
	 * Returns {@code true} if the supplied character is a punctuation mark; {@code false}
	 * otherwise.
	 *
	 * @param ch The character to be tested to see if it is a punctuation mark.
	 * @return {@code true} if the supplied character is a punctuation mark; {@code false}
	 * otherwise.
	 */
	public static boolean isPunctuation(char ch) {
		int type = Character.getType(ch);
		//todo ensure these are in optimal order acc. to statistical data regarding usage
		return Strings.punctuationCheck.test(type);
	}


	/**
	 * Returns {@code true} if the supplied Unicode code point is valid and represents a
	 * punctuation mark; {@code false} otherwise.
	 *
	 * @param codePoint An integer value
	 * @return {@code true} if the supplied Unicode code point is valid and represents a
	 * punctuation mark; {@code false} otherwise.
	 * @throws IllegalArgumentException if the supplied integer is not a valid
	 * <a href="https://web.archive.org/web/20180919061218/https://www.unicode.org/versions/Unicode11.0.0/ch02.pdf">Unicode
	 * code point.</a>
	 */
	public static boolean isPunctuation(int codePoint){
		if (!Character.isValidCodePoint(codePoint)){
			throw new IllegalArgumentException(codePoint + " is not a valid Unicode code point " +
					"value.");
		}
		int type = Character.getType(codePoint);
		return Strings.punctuationCheck.test(type);
	}


	/**
	 * Tests an integer to see if it is equal to one of the {@link Character#getType(int) Java
	 * constants} that correspond to a Unicode category that indicates a form of punctuation, and
	 * returns {@code true} if that is the case.
	 *
	 * <p>Most clients of this function will use it as follows:</p>
	 * <pre>{@code
	 * String str = "Foobar.";
	 * char[] letters = str.toCharArray();
	 * for (char c : letters){
	 *     // Get the unicode category constant...
	 *     int type = Character.getType(c);
	 *     // Test the constant to see if it represents a punctuation category...
	 *     if (Strings.punctuationCheck.test(type)){
	 *         ...Do something with this character c that's some form of punctuation...
	 *     }
	 * }
	 * }</pre>
	 *
	 * <p>The Unicode categories considered by this test to be punctuation include:</p>
	 * <ul>
	 *     <li>{@link Character#END_PUNCTUATION}</li>
	 *     <li>{@link Character#START_PUNCTUATION}</li>
	 *     <li>{@link Character#DASH_PUNCTUATION}</li>
	 *     <li>{@link Character#CONNECTOR_PUNCTUATION}</li>
	 *     <li>{@link Character#INITIAL_QUOTE_PUNCTUATION}</li>
	 *     <li>{@link Character#FINAL_QUOTE_PUNCTUATION}</li>
	 *     <li>{@link Character#OTHER_PUNCTUATION}</li>
	 * </ul>
	 *
	 *
	 */
	//todo ensure these are in optimal order acc. to statistical data regarding punctuation usage
	public static final Predicate<Integer> punctuationCheck = (unicodeCategory) ->
				unicodeCategory == Character.END_PUNCTUATION
				|| unicodeCategory == Character.START_PUNCTUATION
				|| unicodeCategory == Character.DASH_PUNCTUATION
				|| unicodeCategory == Character.CONNECTOR_PUNCTUATION
				|| unicodeCategory == Character.INITIAL_QUOTE_PUNCTUATION
				|| unicodeCategory == Character.FINAL_QUOTE_PUNCTUATION
				|| unicodeCategory == Character.OTHER_PUNCTUATION;


	/**
	 *
	 * @param input
	 * @param paraSep
	 * @return
	 */
	public String sortParagraphs(String input, String paraSep) {
		String[] groups = input.split(paraSep);
		// Paul Sep-19-08 : SortedMap map = new TreeMap() -> SortedMap<String, String> map = new
        // TreeMap<String, String>()
		SortedMap<String, String> map =
				new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

		for (int i = 0; i < groups.length; i++) {
			String group = groups[i];
			String first = group.substring(0, group.indexOf("\n"));
			map.put(first, group);
		}
		// Paul Sep-18-08: List lit = new ArrayList() -> List<String> = new ArrayList<String>()
		List<String> list = new ArrayList<String>(map.values());
		String result = EM;
		for (int i = 0, n = list.size(); i < n; i++) {
			// Paul Sep-18-08 : Removed unecessary (String) cast on list.get(i)
			result += list.get(i) + paraSep;
		}
		return result;
	}


	/**
	 *
	 * @param string
	 * @return
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
	 *
	 * @param string
	 * @return
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
			}
			catch (NumberFormatException e) {
			}
			matcher.appendReplacement(sbuff, str);
		}
		matcher.appendTail(sbuff);
		str = sbuff.toString();
		return str;
	}


	/**
	 * Decodes a String, removing any escaped characters, and, if the String ends with a integer,
	 * which is the case for Group names, the integer characters are removed, prior to returning
     * the
	 * String. For example, if the String "Horvath,%20William%20Lee%20(wlh)%20(Not%20Read)" is
	 * received, it will return "Horvath, William Lee (wlh) (Not Read)". If the String
	 * "Front%20Desk%People%207" is received, which is the name of a group, the String "Front Desk
	 * People" will be returned, rather than "Front Desk People 7", which is what the decode
     * (String)
	 * method would return. Note. decode(String) does the heavy lifting.
	 *
	 * @param string String to decode and remove the Group number.
	 * @return string String decoded and absent of any Group number. Returns an empty String "" if
	 * the parameter {@code String string} is a null reference, or if the array of substrings
	 * of the parameter {@code String string} resulting from invoking the split(" ") method is
	 * of size zero.
	 * @author Paul Wooten
	 * @see #decode(String)
	 */
	public static String decodeGroupName(String string) {
		if (string == null) {
			return EM;
		}
		// first, decode the string
		string = decode(string);
		// ensure that if the string is a group name, the group number is removed.
		String[] words = string.split(SP);
		if (words.length == 0) {
			return EM;
		}

		try {

			Integer.parseInt(words[words.length - 1]);
			/*
			 * If an integer was successfully parsed, that means that the last 'word',
			 * should be omitted from the returned String. By word, I'm referring to
			 * any sequence of consecutive characters occurring between two spaces.
			 */
			string = string.substring(0, string.length() - words[words.length - 1].length());
		}
		catch (NumberFormatException ex) {
			/*
			 * Was unable to parse an integer from the last 'word', which means that
			 * the last 'word' does not represent the group number, and should not
			 * be omitted from the returned String. By word, I'm referring to any
			 * sequence of consecutive characters occurring between two spaces.
			 */
		}
		string = string.trim();
		return string;
	}


	/**
	 *
	 * @param searchMe
	 * @param target
	 * @return
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
	 *
	 * @param s
	 * @param n
	 * @return
	 */
	public static String padRight(String s, int n, String replacement) {
		return String.format("%1$-" + n + "s", s).replaceAll(SP, replacement);
	}


	/**
	 * http://stackoverflow.com/questions/388461/padding-strings-in-java
	 * http://www.xinotes.org/notes/note/308/
	 *
	 * @param s
	 * @param n
	 * @return
	 */
	public static String padLeft(String s, int n, String replacement) {
		return String.format("%1$#" + n + "s", s).replaceAll(SP, replacement);
	}


	/**
	 *
	 * @param lines
	 * @return
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
	 * Since this class will never be instantiated, this method will always return -1.
	 *
	 * @return {@code -1}.
	 */
	@Override
	public int hashCode() {
		return -1;
	}


	/**
	 * Since this class will never be instantiated, this method will always return false.
	 *
	 * @return {@code false}.
	 */
	@Override
	public boolean equals(Object obj) {
		return false;
	}


	/**
	 * Since this class will never be instantiated, this method will always return null.
	 *
	 * @return {@code null}.
	 */
	@Override
	public String toString() {
		return null;
	}
}
