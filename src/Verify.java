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
		
		test = varNature(patterns, php_code);
		
		System.out.println(test);
		
	}
	
	public static Map<String,String> varNature(List<List<String>> patterns, List<List<String>> code){
		
		String nature = new String();
		String aux = new String();
		Map<String, String> var_nature = new HashMap<String, String>();
		
		for(List<String> list: code){
			for(String s: list){
				
				nature = existsIn(patterns, s);
				if(nature != null){
					aux = varsUsed(code, s);
					if(aux != null){
						var_nature.put(aux, nature);
					}
				}
			}
		}
		
		return var_nature;
	}
	
	public static String existsIn(List<List<String>> pattern, String str_code){
		
		for(List<String> l: pattern){
			for(String s: l){
				if(str_code.contains(s) && l.contains("sensitive"))
					return "sensitive";
				else if(str_code.contains(s) && l.contains("sanitization"))
					return "sanitization";
			}
		}
		
		return "neither";
	}
	
	public static String varsUsed(List<List<String>> code, String var){ //REVER
		
		List<String> aux = new ArrayList<String>();
		
		if(!code.isEmpty()){
			for(List<String> list: code){
				if(!list.isEmpty())
					aux.add(list.get(0));
			}
		}
		
		for(String s: aux)
			if(var.equals(s))
				return s;
		
		for(String s: aux)
			if(var.contains(s))
				return s;
		
		return null;
	}

}
