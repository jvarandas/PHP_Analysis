import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Verify {

	public static void main(String[] args) throws IOException {
		
		PatternsParser patterns_parser = new PatternsParser();
		List<List<String>> patterns = new ArrayList<List<String>>();
		ParserPHP php_parser = new ParserPHP("sqli_01.txt");
		List<List<String>> php_code = new ArrayList<List<String>>();
		

		patterns = patterns_parser.parsePatternsList();
		php_code = php_parser.parsePHP();
		
		for(List<String> s: php_code)
			System.out.println(s);
	}

}
