import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Verify {

	public static void main(String[] args) throws IOException {
		
		PatternsParser patterns_parser = new PatternsParser();
		List<List<String>> patterns = new ArrayList<List<String>>();
		ParserPHP php_parser = new ParserPHP("sqli_01.txt");
		List<List<String>> php_code = new ArrayList<List<String>>();
		Map<String, String> test = new HashMap<String, String>();
		

		patterns = patterns_parser.parsePatternsList();
		php_code = php_parser.parsePHP();
		
		for(List<String> s: php_code)
			for(String str: s)
				test = varNature(patterns, str);
		
		System.out.println(test);
	}
	
	public static Map<String,String> varNature(List<List<String>> patterns, String var){
		
		String nature = new String();
		Map<String, String> var_nature = new HashMap<String, String>();
		
		for(List<String> l: patterns){
			for(String s: l){
				if(var.contains(s) && l.contains("sensitive")){
					nature = "sensitive";
					var_nature.put(var, nature);
				}
				else if(var.contains(s) && l.contains("sanitization")){
					nature = "sanitization";
					var_nature.put(var, nature);
				}
			}
		}
		
		return var_nature;
	}

}
