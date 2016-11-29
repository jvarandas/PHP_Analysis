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
		ParserPHP php_parser = new ParserPHP("xss_04.txt");
		List<List<String>> php_code = new ArrayList<List<String>>();
		List<List<String>> adjacency_list = new ArrayList<List<String>>();
		String resultado = new String();
		String vuln = new String();
		
		
		patterns = patterns_parser.parsePatternsList();
		php_code = php_parser.parsePHP();
		
		adjacency_list = buildAdjacencyList(php_code, patterns);
		vuln = vulnerability(php_code, patterns);
		resultado = computeResult(adjacency_list);
		
		System.out.println(adjacency_list);
		System.out.println(resultado+ " -> "+ vuln);
	}
	
	
	public static String computeResult(List<List<String>> adj_list){
		
		String[] aux;
		String var = new String();
		int index1=0;
		int index2=0;
		
		//REFAZER
		if(adj_list.size()>1){
			for(List<String> l: adj_list){
				if(l.size()>1){
					for(String s: l){
						
						if(s.contains(":")){
							
							aux = s.split(":");
							if(aux.length >= 2){
								if(aux[1].equals("sensitive")){
									if(aux[0].contains("sentence"))
										return "Inseguro";
									else{
										index1 = l.indexOf(s);
										var = aux[0];
									}
								}
								else if(aux[1].equals("sanitization") && aux[0].contains("sentence"))
									return "Seguro";
							}
						}
					}
				}
			}
			
			for(List<String> l: adj_list){
				for(String s: l){
					
					if(s.contains(":")){
						if(s.contains(var)){
							
							aux = s.split(":");
							if(aux.length >= 2){
								index2 = l.indexOf(s);
								if(aux[1].equals("sensitive") && index2 < index1)
									return "Seguro";
								else
									return "Inseguro";
							}
						}
					}
				}
			}
		}
		else if(adj_list.size() == 1){
			
			for(String s: adj_list.get(0)){
				
				aux = s.split(":");
				
				if(aux.length >= 2){
					
					if(aux[1].equals("sanitization") || aux[1].equals("sensitive")){
						var = aux[1];
					}
					
					if(aux[1].equals("input") && var.equals("sensitive"))
						return "Inseguro";
					else if(aux[1].equals("input") && !var.equals("sensitive"))
						return "Seguro";
				}
			}
			
		}
		
		return null;
	}
	
	public static List<String> generateNodes(List<List<String>> code){
		
		List<String> nodes = new ArrayList<String>();
		
		if(!code.isEmpty()){
			for(List<String> l: code){
					
				if(l.size()>1)
					if(l.get(0).substring(0,2).trim().contains("$"))
						nodes.add(l.get(0));
			}
		}
		
		return nodes;
	}
	
	public static List<List<String>> buildAdjacencyList(List<List<String>> code, List<List<String>> patterns){
		
		List<List<String>> dependencies = new ArrayList<List<String>>();
		List<List<String>> adjacency_list = new ArrayList<List<String>>();
		Map<String, String> nature_of_vars = new HashMap<String, String>();
		List<String> aglumerado = new ArrayList<String>();
		String aux = new String();
		int flag =0;
		List<List<String>> remove = new ArrayList<List<String>>();
		List<String> remove_string = new ArrayList<String>();
		List<String> unico = new ArrayList<String>();
		String[] var;
		
		dependencies = generateDependencies(code);
		nature_of_vars = varNature(patterns, code);
		
		System.out.println(dependencies);
		System.out.println(nature_of_vars);
		
		for(List<String> list: dependencies){
			
			if(list.size()>1){
				if(nature_of_vars.size()>=1){
					if(list.get(0).contains("sentence") && nature_of_vars.containsKey(list.get(0))){
							if(nature_of_vars.get(list.get(0)).equals("sanitization") || nature_of_vars.get(list.get(0)).equals("sensitive") || nature_of_vars.get(list.get(0)).equals("input"))
						
								aglumerado.add(list.get(0)+":"+nature_of_vars.get(list.get(0)));
					}
					else if(!list.get(0).contains("sentence"))
						aglumerado.add(list.get(0));
				}
				else{
					aglumerado.add(list.get(0));
				}
				
				for(int i=1; i<list.size(); i++){
					
					if(nature_of_vars.containsKey(list.get(i))){
						aux = list.get(i) + ":"+nature_of_vars.get(list.get(i));
						aglumerado.add(aux);
					}
				}
				
				if(!aglumerado.isEmpty())
					adjacency_list.add(aglumerado);
				
				aglumerado = new ArrayList<String>();
			}
			else if(list.size() == 1){
				
				if(nature_of_vars.size()>=1){
					if(list.get(0).contains("sentence") && nature_of_vars.containsKey(list.get(0))){
						if(nature_of_vars.get(list.get(0)).equals("sanitization") || nature_of_vars.get(list.get(0)).equals("sensitive") || nature_of_vars.get(list.get(0)).equals("input"))
							
							aglumerado.add(list.get(0)+":"+nature_of_vars.get(list.get(0)));
					}
					
					//SE ENTRE DOIS NOS SO EXISTEM SENTENCES SEM SIGNIFICADO ENTAO ELES ESTAO LIGADOS ENTRE SI SQLI_4
					else if(list.get(0).trim().startsWith("$") && nature_of_vars.containsKey(list.get(0))){
						
							for(int i=dependencies.indexOf(list)-1; i>0; i--){
								for(String frase: dependencies.get(i)){	
									if(nature_of_vars.containsKey(frase)){
										aglumerado.add(frase);
										flag = 1;
										break;
									}
								}
								
								if(flag == 1) break;
							}
							
							aglumerado.add(list.get(0)+":"+nature_of_vars.get(list.get(0)));
					}
				}
				else{
					aglumerado.add(list.get(0));
				}
				
				if(!aglumerado.isEmpty())
					adjacency_list.add(aglumerado);
				
				aglumerado = new ArrayList<String>();
			}
			
			if(!aglumerado.isEmpty())
				adjacency_list.add(aglumerado);
		}
		
		//REMOVER NOS QUE NAO TEM LIGACOES -- REVER
		for(List<String> l: adjacency_list)
			if(l.size()<2 && !l.isEmpty())
				if(!l.get(0).contains(":"))
					remove.add(l);
		
		adjacency_list.removeAll(remove);
		
		//REMOVER DUPLICADOS EM CADA LISTA
		for(List<String> l: adjacency_list){
			if(l.size()>1){
				if(l.get(1).contains(":")){
					var = l.get(1).split(":");
					
					if(l.get(0).equals(var[0])){
						remove_string.addAll(l);
						flag = 3;
					}
				}
			}
		}
		
		if(flag == 3){
			remove = new ArrayList<List<String>>();
			remove.add(remove_string);
			
			unico.add(remove_string.get(1));
			
			adjacency_list.add(unico);
			adjacency_list.removeAll(remove);
		}
		
		return adjacency_list;
	}
	
	public static List<List<String>> generateDependencies(List<List<String>> code){
		
		List<List<String>> dependencies = new ArrayList<List<String>>();
		List<String> aux = new ArrayList<String>();
		String vars = new String();
		int k =1;
		String[] vec;
		
		if(!code.isEmpty()){
			
			for(List<String> l: code){
				
				if(l.size()>1){
					for(int i=1; i<l.size(); i++){
						
						if(l.get(0).substring(0,2).trim().contains("$"))
							aux.add(l.get(0));
						
						vars = varsUsed(code, l.get(i));
						
						if(vars != null)
							aux.add(vars);
						else{
							aux.add("sentence"+k);
							k++;
						}
					}
				}
				else if(l.size() == 1){
					if(l.get(0).substring(0,2).trim().contains("$"))
						aux.add(l.get(0));
					
					vars = varsUsed(code, l.get(0));
					
					if(vars != null)
						aux.add(vars);
					else{
						vec = l.get(0).split(" ");
						
						for(String s: vec){
							aux.add("sentence"+k);
							k++;
						}
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
		int k=1;
		String[] vec;
		
		for(List<String> list: code){
			if(!list.isEmpty()){
				for(int i=0; i<list.size(); i++){
					
					nature = existsIn(patterns, list.get(i));
					aux = varsUsed(code, list.get(i));
					if(aux != null){
						var_nature.put(aux, nature);
					}
					else{
						vec = list.get(i).split(" ");
						
						for(String str: vec){
							
							nature = existsIn(patterns, str);
							aux = varsUsed(code, str);
							if(aux != null){
								var_nature.put(aux, nature);
							}
							else{
								if(nature.equals("sensitive") || nature.equals("sanitization") || nature.equals("input")){
									var_nature.put("sentence"+k, nature);
									k++;
								}
							}
						}
					}
				}
			}
		}
		
		return var_nature;
	}
	
	public static String existsIn(List<List<String>> pattern, String str_code){
		
		for(List<String> l: pattern){
			for(String s: l){
				if(str_code.contains(s) && l.contains("sensitive") && !str_code.contains("sensitive"))
					return "sensitive";
				else if(str_code.contains(s) && l.contains("sanitization") && !str_code.contains("sanitization"))
					return "sanitization";
				else if(str_code.contains(s) && l.contains("input") && !str_code.contains("input"))
					return "input";
			}
		}
		
		return "neither";
	}
	
	public static String vulnerability(List<List<String>> code, List<List<String>> pattern){
		
		String res = new String();
		
		for(List<String> l: code){
			for(String s: l){
				for(List<String> list: pattern){
					for(String str: list){
						
						if(str.equals("SQLI"))
							res = "SQLI";
						else if(str.equals("XSS"))
							res = "XSS";
						
						if(s.contains(str))
							if(list.contains("sanitization") || list.contains("sensitive"))
								return res;
					}
				}
			}
		}
		
		return "unknown";
	}
	
	public static String varsUsed(List<List<String>> code, String var){
		
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
