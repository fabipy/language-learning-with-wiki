package projekt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ParserSimpleTest {
	
	private static final String ENCODING = "UTF-8";
	
	public static void main(String[] args) throws IOException{
		Parser parser = new Parser("src");
		
		// You can also call it with ...Parser.WORD_CLASS.Pronouns or ...Parser.WORD_CLASS.Random
		String changed = parser.processSite("https://de.wikipedia.org/wiki/Bud_Spencer", WordClass.RANDOM);
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				new File("src/ChangedRandom.html")), ENCODING)));  
		writer.write(changed);
		writer.close();
	}
}
