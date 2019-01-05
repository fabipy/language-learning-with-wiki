package projekt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;

import junit.framework.TestCase;

public class ParserTest extends TestCase {

	private ParserTestVersion parser = null;
	
	@Override
	protected void setUp() {
		parser = new ParserTestVersion();
	}
	
	@Override
	protected void tearDown() {
		parser = null;
	}
	

	public void testConstructor1() {
		try {
			parser = new ParserTestVersion(null);
			fail();
		}
		catch (Exception e) {
			// Test OK.
		}
	}
	
	public void testConstructor2() {
		try {
			parser = new ParserTestVersion("");
			fail();
		}
		catch (Exception e) {
			// Test OK.
		}
	}
	
	public void testConstructor3() {
		try {
			// Please substitute this parameter by a file existing on your system.
			parser = new ParserTestVersion("src");
		}
		catch (Exception e) {
			fail();
		}
	}

	public void testProcessSite1() {
		parser = new ParserTestVersion("src");
		String result = "";
		try {
			// Should not work, but error message via HTML page (return value).
			result = parser.processSite(null, null);
		}
		catch (Exception e) {
			fail();
		}
		assertTrue(!result.isEmpty());
	}
	
	public void testProcessSite2() {
		parser = new ParserTestVersion("src");
		String result = "";
		try {
			// Should not work since not Wikipedia page, but error message via HTML page (return value).
			result = parser.processSite("https://www.google.de/", WordClass.ARTICLES);
		}
		catch (Exception e) {
			fail();
		}
		assertTrue(!result.isEmpty());
	}
	
	public void testProcessSite3() {
		parser = new ParserTestVersion("src");
		String result = "";
		try {
			// Should not work since danish page - language not yet supported, 
			// but error message via HTML page (return value).
			result = parser.processSite("https://da.wikipedia.org/wiki/Harry_Potter", WordClass.ARTICLES);
		}
		catch (Exception e) {
			fail();
		}
		assertTrue(!result.isEmpty());
	}
	
	public void testGetSupportedLanguages() {
		parser = new ParserTestVersion("src");
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("de");
		supportedLanguages.add("en");
		supportedLanguages.add("fr");
		supportedLanguages.add("it");
		supportedLanguages.add("es");
		supportedLanguages.add("ru");
		parser.setSupportedLanguages(supportedLanguages);
		supportedLanguages = parser.getSupportedLanguages();
		
		assertTrue(supportedLanguages.contains("de"));
		assertTrue(supportedLanguages.contains("en"));
		assertTrue(supportedLanguages.contains("fr"));
		assertTrue(supportedLanguages.contains("it"));
		assertTrue(supportedLanguages.contains("es"));
		assertTrue(supportedLanguages.contains("ru"));
	}

	public void testIsLanguageSupported1() {
		parser = new ParserTestVersion("src");
		try {
			parser.isLanguageSupported(null);
			fail();
		}
		catch (Exception e) {			
		}
	}
	
	public void testIsLanguageSupported2() {
		parser = new ParserTestVersion("src");
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("de");
		supportedLanguages.add("en");
		supportedLanguages.add("fr");
		supportedLanguages.add("it");
		supportedLanguages.add("es");
		supportedLanguages.add("ru");
		parser.setSupportedLanguages(supportedLanguages);
		
		assertTrue(parser.isLanguageSupported("de"));
		assertTrue(parser.isLanguageSupported("en"));
		assertTrue(parser.isLanguageSupported("fr"));
		assertTrue(parser.isLanguageSupported("it"));
		assertTrue(parser.isLanguageSupported("es"));
		assertTrue(parser.isLanguageSupported("ru"));
	}
	
	public void testIsLanguageSupported3() {
		parser = new ParserTestVersion("src");
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("de");
		supportedLanguages.add("en");
		supportedLanguages.add("fr");
		supportedLanguages.add("it");
		supportedLanguages.add("es");
		supportedLanguages.add("ru");
		parser.setSupportedLanguages(supportedLanguages);
		
		assertFalse(parser.isLanguageSupported("da"));
	}

	public void testInitialize1() {
		try {
			parser = new ParserTestVersion();
			parser.initialize("src2");
		}
		catch (Exception e) {
			// catch exception - OK!
		}
	}
	
	public void testInitialize2() {
		try {
			parser = new ParserTestVersion();
			parser.initialize("src");
		}
		catch (Exception e) {
			//
		}
	}
	
	public void testInitSupportedLanguages1() {
		try {
			String expected = "[de, en]";
			parser = new ParserTestVersion();
			parser.initSupportedLanguages("testfiles/supported_languages");
			String actual = parser.getSupportedLanguages().toString();
			assertEquals(expected,actual);
		}
		catch (Exception e) {
			// 
		}
	}
	
	public void testInitSupportedLanguages3() {
		try {
			String expected = "[de, en]";
			parser = new ParserTestVersion();
			parser.initSupportedLanguages("bin/supported_languages.txt");
			String actual = parser.getSupportedLanguages().toString();
			assertEquals(expected,actual);
		}
		catch (Exception e) {
			// Catch Exception, Test OK.
		}
	}	
	
	public void testInitDirectory1() {
		try {
			parser = new ParserTestVersion();
			parser.initDirectory("notexistent/bla.txt");
		}
		catch (Exception e) {
			// Catch Exception, Test OK.
		}
	}

	public void testInitDirectory2() {
		try {
			parser = new ParserTestVersion();
			parser.initDirectory("");
		}
		catch (Exception e) {
			// Catch Exception, Test OK.
		}
	}
	
	public void  testInitLookUpTables1() {
		try {
			parser = new ParserTestVersion();
			parser.initDirectory("notexistent");
		}
		catch (Exception e) {
			// Catch Exception, Test OK.
		}
	}
	
	public void  testInitLookUpTables2() {
		try {
			String expected_articles = "{de={0=der, 1=die, 2=das}, en={0=the, 1=a}}";
			parser = new ParserTestVersion("testfiles");
			File newFile = parser.initDirectory("testfiles");
			parser.initLookUpTables(newFile);
			String actual_articles = parser.getMapArticles().toString();
			assertEquals(expected_articles,actual_articles);
		}
		catch (Exception e) {
			// Catch Exception, Test OK.
		}
	}
	
	public void  testInitLookUpTables3() {
		try {
			String expected_prepositions = "{de={0=an, 1=auf}, en={0=about, 1=above}}";
			parser = new ParserTestVersion("testfiles");
			File newFile = parser.initDirectory("testfiles");
			parser.initLookUpTables(newFile);
			String actual_prepositions = parser.getMapPrepositions().toString();
			assertEquals(expected_prepositions,actual_prepositions);
		}
		catch (Exception e) {
			// Catch Exception, Test OK.
		}
	}
	
	public void  testInitLookUpTables4() {
		try {
			String expected_pronouns = "{de={0=ich, 1=du}, en={0=I, 1=you}}";
			parser = new ParserTestVersion("testfiles");
			File newFile = parser.initDirectory("testfiles");
			parser.initLookUpTables(newFile);
			String actual_pronouns = parser.getMapPronouns().toString();
			assertEquals(expected_pronouns,actual_pronouns);
		}
		catch (Exception e) {
			// Catch Exception, Test OK.
		}
	}
	
	public void testIsWikiPage1() {
		try {
			parser = new ParserTestVersion();
			assertTrue(parser.isWikiPage("https://de.wikipedia.org/wiki/Barack_Obama"));
		}
		catch (Exception e) {
			// Test OK.
		}
	}
	
	public void testIsWikiPage2() {
		try {
			parser = new ParserTestVersion();
			assertFalse(parser.isWikiPage("https://de.wikipedia.org/wiki/Wikipedia:Hauptseite")); 
			//@Rainer: what if not an article page, like here?
		}
		catch (Exception e) {
			// Test OK.
		}
	}
	
	public void testIsWikiPage3() {
		try {
			parser = new ParserTestVersion();
			assertFalse(parser.isWikiPage("https://www.google.de/?gws_rd=ssl"));
		}
		catch (Exception e) {
			// Test OK.
		}
	}

	public void testExtractLanguage1() {
		try {
			String expected_url = "de";
			parser = new ParserTestVersion();
			String actual_url = parser.extractLanguage("https://de.wikipedia.org/wiki/Barack_Obama");
			assertEquals(expected_url,actual_url);
		}
		catch (Exception e) {
			// Test OK.
		}
	}
	
	public void testExtractLanguage2() {
		try {
			String expected_url = "sh";
			parser = new ParserTestVersion();
			String actual_url = parser.extractLanguage("sh.wikipedia.org/wiki/Glavna_stranica");
			assertEquals(expected_url,actual_url);
		}
		catch (Exception e) {
			// Test OK.
		}
	}
	
	public void testExtractLanguage3() {
		try {
			String expected_url = "";
			parser = new ParserTestVersion();
			String actual_url = parser.extractLanguage("wikipedia.org/wiki/Barack_Obama");
			assertEquals(expected_url,actual_url);
		}
		catch (Exception e) {
			//@Rainer: Shouldn't it catch a Exception? Cause there is no language tag
		}
	}

	public void testSelectCorrespondigMap1() { //null
		parser = new ParserTestVersion("src");
		
		try {
			parser.selectCorrespondigMap(null);
			fail();
		}
		catch (Exception e) {			
		}
	}
	
	public void testSelectCorrespondigMap2() {
		// TODO: implement test.
	}
	
	public void testSelectCorrespondigMap3() {
		// TODO: implement test.
	}

	public void testExtractDocument1() { //null
		
		parser = new ParserTestVersion("src");
		
		try {
			parser.extractDocument(null);
			fail();
		}
		catch (Exception e) {
		}
	}
	
	public void testExtractDocument2() { //empty string
		parser = new ParserTestVersion("src");
		
		try {
			parser.extractDocument("");
			fail();
		}
		catch (Exception e) {
		}
	}
	
	public void testExtractDocument3() { //random url 
		parser = new ParserTestVersion("src");
		
		try {
			Document doc = parser.extractDocument("http://wwww.google.com");
			assertTrue(doc.hasText());
		}
		catch (Exception e) {
			fail();
		}
	}
	
	public void testExtractDocument4() { //wiki url
		parser = new ParserTestVersion("src");
		
		try{
		Document doc = parser.extractDocument("https://en.wikipedia.org/wiki/Russian_grammar");
		
		assertTrue(doc.hasText());
		}
		catch(Exception e){
			fail();
		}
	}

	public void testProcessParagraphs1() { //null
		parser = new ParserTestVersion("src");
		
		try {
			parser.processParagraphs(null);
			fail();
		}
		catch (Exception e) {
		}
	}
	
	public void testProcessParagraphs2() {
		// TODO: implement test.
	}
	
	public void testProcessParagraphs3() {
		// TODO: implement test.
	}

	public void testChangeText1() { //null
		parser = new ParserTestVersion("src");
		
		try {
			parser.changeText(null);
			fail();
		}
		catch (Exception e) {
		}
	}
	
	public void testChangeText2() { //empty string
		parser = new ParserTestVersion("src");
		
		try {
			parser.changeText("");
			fail();
		}
		catch (Exception e) {
		}
	}
	
	public void testChangeText3() {
		// TODO: implement test.
	}

	public void testChangeTextRandom1() { //null
		parser = new ParserTestVersion("src");
		
		try {
			parser.changeTextRandom(null);
			fail();
		}
		catch (Exception e) {
		}
	}
	
	public void testChangeTextRandom2() { //empty String
		parser = new ParserTestVersion("src");
		
		try {
			parser.changeTextRandom("");
			fail();
		}
		catch (Exception e) {
		}
	}
	
	public void testChangeTextRandom3() {
		// TODO: implement test.
	}

	public void testTokenizeText1() { //null
		parser = new ParserTestVersion("src");
		
		try {
			parser.tokenizeText(null);
			fail();
		}
		catch (Exception e) {
		}
	}
	
	public void testTokenizeText2() { //empty String
		parser = new ParserTestVersion("src");
		
		try {
			parser.tokenizeText("");
			fail();
		}
		catch (Exception e) {
		}
	}
	
	public void testTokenizeText3() { //that is what she said
		parser = new ParserTestVersion("src");
		
		List<String> testList = new ArrayList<String>();
		testList.add("that");
		testList.add("is");
		testList.add("what");
		testList.add("she");
		testList.add("said");
		
		List<String> wordList = parser.tokenizeText("that is what she said");
		
		assertTrue(testList.equals(wordList));
	}
	
	public void testTokenizeText4() { //that's simply not true
		parser = new ParserTestVersion("src");
		
		List<String> testList = new ArrayList<String>();
		testList.add("that's");
		testList.add("simply");
		testList.add("not");
		testList.add("true");
		
		List<String> wordList = parser.tokenizeText("that's     simply	not"
				+ "\n" + "true");
		
		assertTrue(testList.equals(wordList));
	}

	public void testChangeTextArticlesAndPronouns1() { //null
		parser = new ParserTestVersion("src");
		
		try {
			parser.changeTextArticlesAndPronouns(null);
			fail();
		}
		catch (Exception e) {
		}
	}
	
	public void testChangeTextArticlesAndPronouns2() { //empty String
		parser = new ParserTestVersion("src");
		
		try {
			parser.changeTextArticlesAndPronouns("");
			fail();
		}
		catch (Exception e) {
		}
	}

	public void test_processParagraphs() {
		try {
			Document doc = new Document("doc path"); // add the path or take what we already have if you think it is better
			// we need to declare get method or change the method from private
			// into public but i had problems with that
			parser.processParagraphs(doc);
			assertTrue(doc != null);
//			assertEquals(doc,parser.processParagraphs(doc));
//			System.out.println("paragraph process results" + doc + "\n");
		} catch (Exception e) {
		}

	}

	
	public void test_processSite() {
		try {
			WordClass word = WordClass.ARTICLES;
			String site = "https://ar.wikipedia.org/";
			parser.processSite(site, word);
			assertTrue(site != null);
			assertEquals(word+" "+site, parser.processSite(site, word));
			System.out.println("site processed results" + word + " " + site + "\n");
		} catch (Exception e) {
		}
	}

	
	public void test_getSelect() {
		try {
			String getted_string=parser.getSelect();
			assertTrue(getted_string != null);
			assertEquals(getted_string!=null, getted_string);
			System.out.println("the selected word" + getted_string + "\n");
		} catch (Exception e) {
		}

	}
	
	public void test_selectCorrespondigMap() {
		try {
			WordClass word2 = WordClass.PREPOSITIONS;
			parser.selectCorrespondigMap(word2);
			assertTrue(word2 != null);
			assertEquals("selectCorrespondigMap", word2);
			System.out.println("select correspondig map results" + word2 + "\n");
		} catch (Exception e) {
		}

	}

	
	public void test_readInLookUpTable() {
		try {
			Map<String, Map<Integer, String>> Map1 = null;
			String file_name = "file name"; // add the file name please
			parser.readInLookUpTable(file_name, Map1);
			assertTrue(file_name != null);
			assertEquals("readInLookUpTable", file_name, Map1);
			System.out.println("read In Look Up Table Results" + file_name + " " + Map1.toString() + "\n");
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("null")
	
	public void test_removeWordsRandomly() {
		try {
			List<String> list = null;
			list.add("list content");
			Map<Integer, String> removed_rand_list = parser.removeWordsRandomly(list);
			assertTrue(removed_rand_list != null);
			assertEquals(list, removed_rand_list);
			System.out.println("removed list results" + removed_rand_list.toString() + "\n");
		} catch (Exception e) {
		}
	}
	
	
	@SuppressWarnings("null")
	
	public void test_buildSelectTag() {
		try {
			Map<Integer, String> removedWords = null;
			removedWords.put(10, "value_word");
			String removed_rand_list = parser.buildSelectTag(removedWords);
			assertTrue(removed_rand_list != null);
			assertEquals(removed_rand_list!=null, parser.buildSelectTag(removedWords));
			System.out.println("removed list results" + removed_rand_list.toString() + "\n");
		} catch (Exception e) {
		}
	}
	
	
	public void test_setLanguage() {
		try {
			String lang = "english";
			parser.setLanguage(lang);
			assertTrue(lang != null);
			assertEquals("set language to", lang);
			System.out.println("language set to" + lang + "\n");
		} catch (Exception e) {
		}
	}
	
	
	
	public void test_wordListToString() {
		try {
			List<String> list = null;
			list.add("list content");
			parser.wordListToString(list);
			assertTrue(list!=null);
			assertEquals("list", parser.wordListToString(list));
			System.out.println("the word to string result are: " + list + "\n");
		} catch (Exception e) {
		}
	}
	
	
	
	
	public void test_randomNumber() {
		try {
			String lang = "english"; // more languages to be added?
			int value=100;
			@SuppressWarnings("static-access")
			int getted_rand_value= parser.randomNumber(value);
			assertTrue(value> 0);
			assertEquals(getted_rand_value>0, getted_rand_value);
			System.out.println("the randome number is" + getted_rand_value + "\n");
		} catch (Exception e) {
		}
	}
	
	public void test_setSelect() {
		try {
			String set_select = "https://ar.wikipedia.org/"; //this is only an example add all the websites we need
			parser.setSelect(set_select);
			assertTrue(set_select != null);
			assertEquals("set select", set_select);
			System.out.println("the setted select" + set_select + "\n");
		} catch (Exception e) {
		}

	}

	public void test_setWordClass() {
		try {
			WordClass word3 = WordClass.PRONOUNS;
			String word_exp="my"; // what else?
			parser.setWordClass(word3);
			assertTrue(word3 != null);
			assertEquals(word_exp, word3);
			System.out.println("setted word calss" + word3 + "\n");
		} catch (Exception e) {
		}
	}
}
