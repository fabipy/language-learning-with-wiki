import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * ==========================================
 * EBERHARD-KARLS UNIVERSITÄT TÜBINGEN
 * July 2016
 *
 * Course: ISCL
 * Subject: Data structures and Algorithms II
 * Final project
 * ==========================================
 *
 * Acknowledgement
 * ---------------
 * This program is based on an idea/program (WERTi) from Prof. Detmer Meurers of University of
 * Tübingen.
 *
 * Description
 * -----------
 * Class to transform a Wikipedia page for language learning purposes. It removes either articles,
 * prepositions or pronouns from a supported language and substitutes them with an HTML <selection>
 * element that contains all possible alternatives of the respective word category.
 *
 * This class expects a set of configuration files at a specified location:
 * - supported_languages
 * - articles_xx - one for every supported language.
 * - prepositions_xx - one for every supported language.
 * - pronoun_xx - one for every supported language.
 *
 * The format of supported_languages is just on language code per line, e.g. en
 * The format for the word categories - e.g. articles_en - is just one word of the language and
 * category per line e.g. the
 */
public class Parser {

	/** Name of the configuration file for the language support. */
	private static final String SUPPORTED_LANGUAGES_FILE = "supported_languages";

	/** Name part of the configuration file for the lookup list of the articles. */
	private static final String NAME_ARTICLES_FILE = "articles";

	/** Name part of the configuration file for the lookup list of the prepositions. */
	private static final String NAME_PREPOSITIONS_FILE = "prepositions";

	/** Name part of the configuration file for the lookup list of the pronouns. */
	private static final String NAME_PRONOUNS_FILE = "pronouns";

	/** Separator between the name part and the language code in the names for the lookup list. */
	private static final String SEPARATOR_INIT_FILE_NAMES = "_";

	/** Supported punctuation signs - period. */
	private static final String PUNCTUATION_PERIOD = ".";

	/** Supported punctuation signs - question mark. */
	private static final String PUNCTUATION_QUESTION = "?";

	/** Supported punctuation signs - exclamation sign. */
	private static final String PUNCTUATION_EXCLAMATION = "!";

	/** Supported punctuation signs - comma. */
	private static final String PUNCTUATION_COMMA = ",";

	/** Supported punctuation signs - semicolon. */
	private static final String PUNCTUATION_SEMICOLON = ";";

	/** Supported punctuation signs - colon. */
	private static final String PUNCTUATION_COLON = ":";

	/** Supported punctuation signs - apostrophe. */
	private static final String PUNCTUATION_APOSTROPHE = "'";

	/** Supported punctuation signs - quote. */
	private static final String PUNCTUATION_QUOTE = ".";

	/** Index into the part of the name of the lookup list, that specify the word classes. */
	private static final int INDEX_WORD_CLASS = 0;

	/** Index into the part of the language code of the lookup list, that specify the word classes. */
	private static final int INDEX_LANGUAGE_CODE = 1;

	/** The encoding schema. */
	private static final String ENCODING = "UTF-8";

	/** Placeholder; used to be replaced later on. */
	private static final String PLACE_HOLDER = "##";

	/** Base path of Wikipedia pages. */
	private static final String BASE_PATH = ".wikipedia.org";

	/** Protocol part of a URL. */
	private static final String PROTOCOL = "http://";

	/** Extended protocol part of a URL. */
	private static final String PROTOCOL_EXT = "https://";

	/** Regular expression for the separator character of a URL. */
	private static final String SEPARATOR_PROTOCOL = "\\.";

	/** Index of the language part of the URL, after removing the protocol part.*/
	private static final int LANGUAGE_PART_URL = 0;

	/** Separator used in file paths. */
	private static final String PATH_SEPARATOR = "/";

	/** Regular expression for finding any white space. */
	private static final String REGEX_WHITE_SPACE = "\\s+";

	/** An empty string. */
	private static final String EMPTY_STRING = "";

	/** A space, used to separate words in a sentence. */
	private static final String SPACE = " ";

	/** The number of words per one random removal on average. */
	private static final int WORDS_PER_REMOVAL = 10;

	/** Opening select tag. */
	private static final String SELECT_OPENING = "<select>";

	/** Closing select tag. */
	private static final String SELECT_CLOSING = "</select>";

	/** Opening option tag, with a place holder for later substitution. */
	private static final String OPTION_OPENING = "<option value=\"" + PLACE_HOLDER + "\">";

	/** Closing option tag. */
	private static final String OPTION_CLOSING = "</option>";

	/** Opening span tag, with a place holder for later substitution. */
	private static final String SPAN_OPENING = "<span id=\"" + PLACE_HOLDER + "\">";

	/** Closing span tag. */
	private static final String SPAN_CLOSING = "</span>";

	/** Name of a paragraph tag. */
	private static final String PARAGRAPH = "p";

	/** Opening part of an occurrence marker. Used to mark and number occurrences of replaced words. */
	private static final String MARKER_OCCURRENCE_OPENING = "<";

	/** Closing part of an occurrence marker. Used to mark and number occurrences of replaced words. */
	private static final String MARKER_OCCURRENCE_CLOSING = ">";

	/**
	 * Message page to be return, when problems occur. Maybe the only way to communicate with the
	 * end user, since he could be someone other then the one responsible for running the service.
	 */
	private static final String MESSAGE_PAGE =
			"<html>" +
			"<head>" +
			"</head>" +
			"<body>" +
			"<h1>" + PLACE_HOLDER + "</h1>" +
			"</body>" +
			"</html>";

	/** Message shown in case of pages other then Wikipedia. */
	private static final String MESSAGE_ONLY_WIKI_SUPPORTED = "No processing possible. Only Wikipedia pages supported!";

	/** Message shown in case of unsupported languages. */
	private static final String MESSAGE_LANGUAGE_NOT_SUPPORTED = "No processing possible. Language not supported!";

	/** Message shown in case of unsupported languages. */
	private static final String MESSAGE_INVALID_PARAMETER = "No processing possible. Parameter invalid!";

	/**
	 *  Occurrence marker, used to mark and number occurrences of replaced words. Contains a placeholder for later
	 *  substitution with the number of the occurred word.
	 */
	private static final String MARKER_OCCURRENCE = MARKER_OCCURRENCE_OPENING + PLACE_HOLDER + MARKER_OCCURRENCE_CLOSING;

	/** Reference part of a link. */
	private static final String REFERENCE_LINK = "href";

	/** Link to a style sheet. */
	private static final String LINK_TO_STYLESHEET = "link[rel='stylesheet']";

	/** Class of the words to remove. */
	private WordClass wordClass;

	/** Language of the Wikipedia page to process. */
	private String language;

	/** Select element with dynamically created option tags. */
	private String select;

	/** Set of the supported punctuation signs. */
	private Set<String> punctuationSigns = new HashSet<String>();

	/** Set of the supported languages, due to the initialization file 'supported_languages'.*/
	private Set<String> supportedLanguages = new HashSet<String>();

	/**
	 * Map containing maps of articles. The keys of the outer map are country codes. The keys of the
	 * inner maps are numbers to identify the words.
	 */
	private Map<String, Map<Integer, String>> mapArticles = new HashMap<String, Map<Integer, String>>();

	/**
	 * Map containing maps of prepositions. The keys of the outer map are country codes. The keys of the
	 * inner maps are numbers to identify the words.
	 */
	private Map<String, Map<Integer, String>> mapPrepositions = new HashMap<String, Map<Integer, String>>();

	/**
	 * Map containing maps of pronouns. The keys of the outer map are country codes. The keys of the
	 * inner maps are numbers to identify the words.
	 */
	private Map<String, Map<Integer, String>> mapPronouns = new HashMap<String, Map<Integer, String>>();

	/**
	 * Reference to a map with the words to remove. This depends on the chosen word class and could be
	 * either mapArticles, mapPrepositions or mapPronouns.
	 */
	private Map<String, Map<Integer, String>> mapWordsToRemove;

	/** A logger instance. */
	private static final Logger logger = Logger.getLogger(Parser.class.getName());


	/**
	 * Constructor. No default constructor exists.
	 *
	 * @param path to the directory containing the initialization file 'supported_languages', as well as
	 * the various files 'articles_xx', 'preposition_xx' and 'pronouns_xx'.
	 */
	public Parser(String path) {
		// Parameter check.
		if (path == null || path.isEmpty()) {
			logger.log(Level.SEVERE, "Parameter is invalid: " + path);
			throw new IllegalArgumentException("Invalid Parameter: " +  path);
		}

		try {
			initialize(path);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Exception occurred: " + e.getMessage());
		}
	}

	/**
	 * The main access method. Converts a Wikipedia page into an enhanced version for language
	 * learning purposes.
	 *
	 * @param url - a URL of a Wikipedia page of a supported language.
	 * @param wordClass - the class of the words to remove.
	 * @return enhanced Wikipedia page or a page with a message, should an error occur.
	 * @throws IOException
	 */
	public String processSite(String url, WordClass wordClass) throws IOException {
		// Checking of parameters.
		if (url == null) {
			logger.log(Level.SEVERE, "Invalid parameter - url is null.");
			return MESSAGE_PAGE.replaceAll(PLACE_HOLDER, MESSAGE_INVALID_PARAMETER);
		}
		if (wordClass == null) {
			logger.log(Level.SEVERE, "Invalid parameter - wordClass is null.");
			return MESSAGE_PAGE.replaceAll(PLACE_HOLDER, MESSAGE_INVALID_PARAMETER);
		}
		if (!isWikiPage(url)) {
			logger.log(Level.SEVERE, "Invalid parameter (url) - only Wikipedia pages supported.");
			return MESSAGE_PAGE.replaceAll(PLACE_HOLDER, MESSAGE_ONLY_WIKI_SUPPORTED);
		}
		String language = extractLanguage(url);
		if (!isLanguageSupported(language)) {
			logger.log(Level.SEVERE, "Invalid parameter (url). Language: " + language + " not supported.");
			return MESSAGE_PAGE.replaceAll(PLACE_HOLDER, MESSAGE_LANGUAGE_NOT_SUPPORTED);
		}

		// Initialization of processing.
		setLanguage(language);
		setWordClass(wordClass);
		selectCorrespondigMap(wordClass);

		// Processing of document.
		Document doc = extractDocument(url);
		processParagraphs(doc);
		processLinks(doc);

		Elements body = doc.getElementsByTag("body");
		Elements head = doc.getElementsByTag("head");
		Elements select = doc.getElementsByTag("select");
		select.prepend("<option selected='true' disabled='disabled'>Select</option>");
		head.append("<link rel='stylesheet' href='stylesheets/wiki.css'>");
		body.append("<script src='http://code.jquery.com/jquery-1.10.2.min.js'>");
		body.append("<script src='javascripts/main.js'>");

		return doc.outerHtml();
	}

	/**
	 * Getter method for the supported languages.
	 *
	 * @return a set with the codes of all supported languages.
	 */
	public Set<String> getSupportedLanguages() {
		return supportedLanguages;
	}

	/**
	 * Checks if a given language is supported at the moment.
	 *
	 * @param language - code of a language.
	 * @return true if the given method is supported, false otherwise;
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	public boolean isLanguageSupported(String language) {
		// Parameter check.
		if (language == null) {
			logger.log(Level.SEVERE, "Invalid parameter - language is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  language);
		}

		return supportedLanguages.contains(language);
	}

	/**
	 * Reads the configuration tables into the system.
	 *
	 * @param directoryPath - path to the directory containing the configuration files.
	 * @throws IOException - if problems with IO occur.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private void initialize(String directoryPath) throws IOException {
		// Parameter check.
		if (directoryPath == null) {
			logger.log(Level.SEVERE, "Invalid parameter - directoryPath is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  directoryPath);
		}
		if (directoryPath.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - directoryPath is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  directoryPath);
		}

		initPunctuationSigns();
		initSupportedLanguages(directoryPath + PATH_SEPARATOR + SUPPORTED_LANGUAGES_FILE);
		File dir = initDirectory(directoryPath);
		initLookUpTables(dir);
	}

	/**
	 * Initializes a set containing the supported punctuation signs.
	 */
	private void initPunctuationSigns() {
		punctuationSigns.add(PUNCTUATION_PERIOD);
		punctuationSigns.add(PUNCTUATION_QUESTION);
		punctuationSigns.add(PUNCTUATION_EXCLAMATION);
		punctuationSigns.add(PUNCTUATION_COMMA);
		punctuationSigns.add(PUNCTUATION_SEMICOLON);
		punctuationSigns.add(PUNCTUATION_COLON);
		punctuationSigns.add(PUNCTUATION_APOSTROPHE);
		punctuationSigns.add(PUNCTUATION_QUOTE);
	}

	/**
	 * Reads in the configuration file 'supported_languages'.
	 *
	 * @param filename - name of the file including path if necessary.
	 * @throws IOException - if problems with IO occur.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private void initSupportedLanguages(String filename) throws IOException {
		// Parameter check.
		if (filename == null) {
			logger.log(Level.SEVERE, "Invalid parameter - filename is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  filename);
		}
		if (filename.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - filename is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  filename);
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), ENCODING));
		String line;
		while((line = reader.readLine()) != null) {
			line = line.trim();
			supportedLanguages.add(line);
		}
		reader.close();
	}

	/**
	 * Initializes the File for the directory.
	 *
	 * @param directory - path to the directory.
	 * @return an initialized File object for the directory.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private File initDirectory(String directory) {
		// Parameter check.
		if (directory == null) {
			logger.log(Level.SEVERE, "Invalid parameter - directory is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  directory);
		}
		if (directory.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - directory is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  directory);
		}

		File dir = new File(directory);
		if (!dir.exists()) {
			logger.log(Level.SEVERE, "Invalid parameter - directory: " + directory + " does not exist.");
			throw new IllegalArgumentException("File: " + directory + " does not exist!");
		}
		if (!dir.isDirectory()) {
			logger.log(Level.SEVERE, "Invalid parameter - directory: " + directory + " is not a directory.");
			throw new IllegalArgumentException(directory + " is not a directory!");
		}
		return dir;
	}

	/**
	 * Reads in the configuration files 'articles_xx', 'prepositions_xx' and 'pronouns_xx'.
	 *
	 * @param dir - File object for the directory containing the configuration files.
	 * @throws IOException - if an IO problem occurs.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private void initLookUpTables(File dir) throws IOException {
		// Parameter check.
		if (dir == null) {
			logger.log(Level.SEVERE, "Invalid parameter - dir is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  dir);
		}

		// Loop over all files contained in the directory.
		for (String file : dir.list()) {
			// The respective files should have a name of the format <word-class>_<language_code>
			String[] parts = file.split(SEPARATOR_INIT_FILE_NAMES);
			if (parts.length != 2) {
				// Must be a different file.
				continue;
			}
			switch (parts[INDEX_WORD_CLASS]) {
			case NAME_ARTICLES_FILE:
				readInLookUpTable(dir.getPath() + PATH_SEPARATOR + file, mapArticles);
				break;
			case NAME_PREPOSITIONS_FILE:
				readInLookUpTable(dir.getPath() + PATH_SEPARATOR + file, mapPrepositions);
				break;
			case NAME_PRONOUNS_FILE:
				readInLookUpTable(dir.getPath() + PATH_SEPARATOR + file, mapPronouns);
				break;
			default:
				continue;
			}
		}
	}

	/**
	 * Helper method for reading in of a configuration file for the word classes.
	 *
	 * @param filename - name of the file.
	 * @param map - the respective map containing all lookup table for a certain word class.
	 * @throws IOException - if an IO problem occurs.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private void readInLookUpTable(String filename, Map<String, Map<Integer, String>> map) throws IOException {
		// Parameter check.
		if (filename == null) {
			logger.log(Level.SEVERE, "Invalid parameter - filename is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  filename);
		}
		if (filename.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - filename is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  filename);
		}
		if (map == null) {
			logger.log(Level.SEVERE, "Invalid parameter - map is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  map);
		}

		// The respective files should have name of the format <word-class>_<language_code>
		String[] parts = filename.split(SEPARATOR_INIT_FILE_NAMES);
		if (parts.length != 2) {
			// Must be a different file.
			return;
		}
		String language_table = parts[INDEX_LANGUAGE_CODE];
		if (!isLanguageSupported(language_table)) {
			return;
		}

		// Initialize new lookup table for words of a certain class of a certain language.
		Map<Integer, String> table = new HashMap<Integer, String>();
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), ENCODING));
		int i=0;
		// Fill the lookup table.
		while((line = reader.readLine()) != null) {
			line = line.trim();
			table.put(i++, line);
		}
		reader.close();
		// Add the lookup table to the map.
		map.put(language_table, table);
	}

	/**
	 * Helper method to check if URL is of a Wikipedia page.
	 *
	 * @param url - URL to check for validity.
	 * @return true if URL is of a Wikipedia page, false otherwise.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private boolean isWikiPage(String url) {
		// Parameter check.
		if (url == null) {
			logger.log(Level.SEVERE, "Invalid parameter - url is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  url);
		}
		if (url.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - url is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  url);
		}

		return url.indexOf(BASE_PATH)>=0;
	}

	/**
	 * Extract the language of a URL of a Wikipedia page.
	 *
	 * @param url - URL of a Wikipedia page.
	 * @return the language code, e.g. 'en'.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private String extractLanguage(String url) {
		// Parameter check.
		if (url == null) {
			logger.log(Level.SEVERE, "Invalid parameter - url is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  url);
		}
		if (url.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - url is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  url);
		}

		String tmp = url.replaceAll(PROTOCOL, EMPTY_STRING);
		tmp = tmp.replaceAll(PROTOCOL_EXT, EMPTY_STRING);
		String[] parts = tmp.split(SEPARATOR_PROTOCOL);
		return parts[LANGUAGE_PART_URL];
	}

	/**
	 * Selects the respective map containing the lookup table for the given word class.
	 *
	 * @param wordClass - the word class to make the selection for.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private void selectCorrespondigMap(WordClass wordClass) {
		// Parameter check.
		if (wordClass == null) {
			logger.log(Level.SEVERE, "Invalid parameter - wordClass is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  wordClass);
		}

		switch (wordClass) {
		case ARTICLES:
			mapWordsToRemove = mapArticles;
			break;
		case PREPOSITIONS:
			mapWordsToRemove = mapPrepositions;
			break;
		case PRONOUNS:
			mapWordsToRemove = mapPronouns;
			break;
		case RANDOM:
			break;
		}
	}

	/**
	 * Extract a Jsoup document from a give URL.
	 *
	 * @param url - a URL to extract a Jsoup document from.
	 * @return a Jsoup document.
	 * @throws IOException - if an IO problem occurs.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private Document extractDocument(String url) throws IOException {
		// Parameter check.
		if (url == null) {
			logger.log(Level.SEVERE, "Invalid parameter - url is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  url);
		}
		if (url.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - url is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  url);
		}

		return Jsoup.connect(url).get();
	}

	/**
	 * Processes the paragraphs of an HTML document (<p>...</p>).
	 * Within these paragraphs it removes certain words and substitutes them
	 * with dynamically created select elements.
	 *
	 * @param doc - a Jsoup document.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private void processParagraphs(Document doc) {
		// Parameter check.
		if (doc == null) {
			logger.log(Level.SEVERE, "Invalid parameter - doc is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  doc);
		}

		Elements paragraphs = doc.select(PARAGRAPH);
		// Loop over all HTML paragraphs.
		for (Element paragraph : paragraphs) {
			// Call change text to replace certain words with a placeholder and a
			// numerical marker for the occurrence.
			String text = paragraph.text();
			if (text.isEmpty()) {
				continue;
			}
			String changedText = changeText(paragraph.text());
			paragraph.text(EMPTY_STRING);
			boolean firstTime = true;
			// Split the changed text at the placeholders.
			for (String part : changedText.split(PLACE_HOLDER)) {
				if (part.isEmpty()) {
					continue;
				}
				// Look for the occurrence marker at the beginning of a word
				int begin = part.indexOf(MARKER_OCCURRENCE_OPENING);
				int end = part.indexOf(MARKER_OCCURRENCE_CLOSING);
				// Skip, if no occurrence marker can be found.
				if (begin < 0 || end < 0) {
					paragraph.append(part);
					continue;
				}
				// Extract the number from the occurrence marker.
				String tmp = part.substring(begin+1, end);
				int number = Integer.parseInt(tmp);
				part = part.substring(end+1);
				// Insert a selection element containing all respective options inside a span tag with the occurrence number.
				if (!firstTime) {
					paragraph.append(SPAN_OPENING.replaceAll(PLACE_HOLDER, String.valueOf(number)) + getSelect() + SPAN_CLOSING);
				}
				else {
					firstTime = false;
				}
				paragraph.append(part);
			}
		}
	}

	/**
	 * Change text, substituting certain words with placeholders and occurrence markers containing an identification number.
	 *
	 * @param text - text to be processed.
	 * @return processed text.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private String changeText(String text) {
		// Parameter check.
		if (text == null) {
			logger.log(Level.SEVERE, "Invalid parameter - text is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  text);
		}
		if (text.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - text is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  text);
		}

		// Choose the respective method.
		if (wordClass == WordClass.RANDOM) {
			return changeTextRandom(text);
		}
		return changeTextArticlesAndPronouns(text);
	}

	/**
	 * Change text, substituting random words with placeholders and occurrence markers containing an identification number.
	 *
	 * @param text - text to be processed.
	 * @return processed text.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private String changeTextRandom(String text){
		// Parameter check.
		if (text == null) {
			logger.log(Level.SEVERE, "Invalid parameter - text is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  text);
		}
		if (text.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - text is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  text);
		}

		List<String> wordList = tokenizeText(text);
		Map<Integer, String> removedWords = removeWordsRandomly(wordList);
		setSelect(buildSelectTag(removedWords));

		return  wordListToString(wordList);
	}

	/**
	 * Tokenizes text.
	 *
	 * @param text - text to be tokenized.
	 * @return a list containing the tokens.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private List<String> tokenizeText(String text) {
		// Parameter check.
		if (text == null) {
			logger.log(Level.SEVERE, "Invalid parameter - text is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  text);
		}
		if (text.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - text is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  text);
		}

		List<String> wordList = new ArrayList<String>();
		for (String word : text.split(REGEX_WHITE_SPACE)) {
			int length = word.length();

			String punctuationSign = "";
			if (isPunctuationSign(word.charAt(length-1))) {
				word = word.substring(0, length-1);
				punctuationSign = word.substring(length-1);

				if (!word.isEmpty()) {
					wordList.add(word);
				}
				wordList.add(punctuationSign);
			}
			else {
				wordList.add(word);
			}
		}
		return wordList;
	}

	/**
	 * Change text, substituting certain words of class article,  prepositions or pronouns
	 * with placeholders and occurrence markers containing an identification number.
	 *
	 * @param text - text to be processed.
	 * @return processed text.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private String changeTextArticlesAndPronouns(String text) {
		// Parameter check.
		if (text == null) {
			logger.log(Level.SEVERE, "Invalid parameter - text is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  text);
		}
		if (text.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - text is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  text);
		}

		StringBuffer buf = new StringBuffer();
		boolean firstTime = true;
		// Tokenize words.
		for (String word : text.split(REGEX_WHITE_SPACE)) {
			if (!firstTime) {
				buf.append(SPACE);
			}
			else {
				firstTime = false;
			}
			boolean found = false;
			// Look the word up in the respective lookup table.
			for (Entry<Integer, String> entry : mapWordsToRemove.get(language).entrySet()) {
				// Replace it by a placeholder and an occurrence marker with the respective number.
				if (entry.getValue().equals(word)) {
					found = true;
					buf.append(PLACE_HOLDER + MARKER_OCCURRENCE.replaceAll(PLACE_HOLDER, entry.getKey().toString()));
				}
			}
			// If word is not contained in the lookup table, just copy it to the output.
			if (!found) {
				buf.append(word);
			}
		}
		return buf.toString();
	}

	/**
	 * Processes links of Wikipedia pages, since they have to be slightly adapted.
	 * @param doc - a Jsoup document
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private void processLinks(Document doc) {
		// Parameter check.
		if (doc == null) {
			logger.log(Level.SEVERE, "Invalid parameter - doc is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  doc);
		}

		Elements links = doc.select(LINK_TO_STYLESHEET);
		for (Element link : links) {
			String href = link.attr(REFERENCE_LINK);
			link.attr(REFERENCE_LINK, PROTOCOL + language + BASE_PATH + href);
		}
	}

	/**
	 * Removes words from a list and substitutes them with placeholders containing
	 * an identifying number. Returns a list with the randomly removed words.
	 *
	 * @param wordList - list of words to randomly remove words from.
	 * @return list of randomly removed words.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private Map<Integer, String> removeWordsRandomly(List<String> wordList) {
		// Parameter check.
		if (wordList == null) {
			logger.log(Level.SEVERE, "Invalid parameter - wordList is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  wordList);
		}

		Map<Integer, String> removedWords = new HashMap<Integer, String>();
		int size = wordList.size();
		for (int i=0; i<size/WORDS_PER_REMOVAL; i++) {
			int random = randomNumber(size);
			String wordToRemove = wordList.get(random);
			if (wordToRemove.startsWith(PLACE_HOLDER)) {
				continue;
			}
			wordList.set(random, PLACE_HOLDER + MARKER_OCCURRENCE.replaceAll(PLACE_HOLDER, String.valueOf(i)));
			removedWords.put(new Integer(i), wordToRemove);
		}
		return removedWords;
	}

	/**
	 * Dynamically builds a select element from a list of removed words.
	 *
	 * @param removedWords - list of removed words.
	 * @return a String containing a select element with the removed words as options and the numbers as values.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private String buildSelectTag(Map<Integer, String> removedWords) {
		// Parameter check.
		if (removedWords == null) {
			logger.log(Level.SEVERE, "Invalid parameter - removedWords is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  removedWords);
		}

		StringBuffer buf = new StringBuffer();
		buf.append(SELECT_OPENING);
		for (Entry<Integer, String> entry : removedWords.entrySet()) {
			buf.append(OPTION_OPENING.replaceAll(PLACE_HOLDER, entry.getKey().toString()) + entry.getValue() + OPTION_CLOSING);
		}
		buf.append(SELECT_CLOSING);

		return buf.toString();
	}

	/**
	 * Creates from a word list a string of words with spaces between them.
	 *
	 * @param wordList - list of words
	 * @return String of words with spaces between them.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private String wordListToString(List<String> wordList) {
		// Parameter check.
		if (wordList == null) {
			logger.log(Level.SEVERE, "Invalid parameter - wordList is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  wordList);
		}

		StringBuffer buf = new StringBuffer();
		boolean firstTime = true;
		for (String word : wordList) {
			if (!firstTime) {
				buf.append(SPACE);
			}
			else {
				firstTime = false;
			}
			buf.append(word);
		}
		return buf.toString();
	}

	/**
	 * Calculates a random number between zero and max exclusively.
	 *
	 * @param max - the maximum the random number can reach.
	 * @return the random number.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private static int randomNumber(int max) {
		// Parameter check.
		if (max < 0) {
			logger.log(Level.SEVERE, "Invalid parameter - max is negativ: " + max);
			throw new IllegalArgumentException("Invalid Parameter: " +  max);
		}

		Random randomizer = new Random();
		int randomNumber = randomizer.nextInt(max);

		return randomNumber;
	}

	/**
	 * Returns a String containing a select element for the current stage of
	 * processing. In case of random word removal, method changeTextRandom()
	 * creates the corresponding select element and stores it in the attribute
	 * 'select'.
	 *
	 * In all other cases, the select is created on the fly from elements of the
	 * respective lookup table.
	 *
	 * @return a String containing a select element with removed words.
	 */
	private String getSelect() {
		if (wordClass == WordClass.RANDOM) {
			return select;
		}

		StringBuffer buf = new StringBuffer();
		buf.append(SELECT_OPENING);
		for (Entry<Integer, String> entry : mapWordsToRemove.get(language).entrySet()) {
			buf.append(OPTION_OPENING.replaceAll(PLACE_HOLDER, entry.getKey().toString()) + entry.getValue() + OPTION_CLOSING);
		}
		buf.append(SELECT_CLOSING);

		return buf.toString();
	}

	/**
	 * Setter for the current language.
	 *
	 * @param language - code of the current language.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private void setLanguage(String language) {
		// Parameter check.
		if (language == null) {
			logger.log(Level.SEVERE, "Invalid parameter - language is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  language);
		}
		if (language.isEmpty()) {
			logger.log(Level.SEVERE, "Invalid parameter - language is empty.");
			throw new IllegalArgumentException("Invalid Parameter: " +  language);
		}
		this.language = language;
	}

	/**
	 * Setter for the dynamically created select element.
	 *
	 * @param select - a select element
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private void setSelect(String select) {
		// Parameter check.
		if (select == null) {
			logger.log(Level.SEVERE, "Invalid parameter - select is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  select);
		}
		this.select = select;
	}

	/**
	 * Setter for wordClass.
	 *
	 * @param wordClass - a word class.
	 * @throws IllegalArgumentExcpetion - if parameter is not valid.
	 */
	private void setWordClass(WordClass wordClass) {
		// Parameter check.
		if (wordClass == null) {
			logger.log(Level.SEVERE, "Invalid parameter - wordClass is null.");
			throw new IllegalArgumentException("Invalid Parameter: " +  wordClass);
		}
		this.wordClass = wordClass;
	}

	/**
	 * Checks if a given character is a supported punctuation sign
	 *
	 * @param c - a character to be checked for punctuation sign
	 * @return true if the character is a supported punctuation sign, false otherwise.
	 */
	private boolean isPunctuationSign(char c) {
		String character = String.valueOf(c);
		return punctuationSigns.contains(character);
	}
}
