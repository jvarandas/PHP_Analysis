import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Verify {

	public static void main(String[] args) throws IOException {
		
		PatternsParser patterns_parser = new PatternsParser();
		List<List<String>> patterns = new ArrayList<List<String>>();
		ParserPHP php_parser = new ParserPHP("sqli_01.txt");
		List<List<String>> php_code = new ArrayList<List<String>>();
		List<List<String>> dependencias = new ArrayList<List<String>>();
		
		patterns = patterns_parser.parsePatternsList();
		php_code = php_parser.parsePHP();
		
		dependencias = buildAdjacencyList(php_code);
		System.out.println("");
		System.out.println(dependencias);
	}
	
	
	public static List<String> generateNodes(List<List<String>> code){
		
		List<String> nodes = new ArrayList<String>();
		
		if(!code.isEmpty()){
			for(List<String> l: code){
					
				if(l.size()>1)
					if(l.get(0).startsWith("$"))
						nodes.add(l.get(0));
			}
		}
		
		return nodes;
	}
	
	public static List<List<String>> buildAdjacencyList(List<List<String>> code){
		
		List<List<String>> dependencies = new ArrayList<List<String>>();
		List<String> aux = new ArrayList<String>();
		String vars = new String();
		
		if(!code.isEmpty()){
			
			for(List<String> l: code){
				
				if(l.size()>1){
					for(int i=1; i<l.size(); i++){
						
						if(l.get(0).startsWith("$"))
							aux.add(l.get(0));
						
						vars = varsUsed(code, l.get(i));
						
						if(vars != null)
							aux.add(vars);
						else
							aux.add("sentence");
					}
				}
				
				if(aux != null){
					aux = aux.stream().distinct().collect(Collectors.toList()); //REMOVER DUPLICADOS
					dependencies.add(aux);
				}
				
				aux = new ArrayList<String>();
			}
		}
		
		return dependencies;
	}
	
	public static Map<String,String> varNature(List<List<String>> patterns, List<List<String>> code){
		
		String nature = new String();
		String aux = new String();
		Map<String, String> var_nature = new HashMap<String, String>();
		
		for(List<String> list: code){
			for(String s: list){
				
				nature = existsIn(patterns, s);
				aux = varsUsed(code, s);
				if(aux != null){
					var_nature.put(aux, nature);
				}
				else{
					if(nature.equals("sensitive") || nature.equals("sanitization"))
						var_nature.put("sentence", nature);
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
				else if(str_code.contains(s) && l.contains("input"))
					return "input";
			}
		}
		
		return "neither";
	}
	
	public static String varsUsed(List<List<String>> code, String var){ //REVER
		
		List<String> aux = new ArrayList<String>();
		
		aux = generateNodes(code);
		
		if(aux != null){
			
			for(String s: aux)
				if(var.equals(s))
					return s;
			
			for(String s: aux)
				if(var.contains(s))
					return s;
		}
		
		return null;
	}

}
