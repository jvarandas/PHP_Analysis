import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParserPHP {
	
	private String _file;
	
	public ParserPHP(String file){
		_file = file;
	}
	
	public List<List<String>> parsePHP() throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(_file));
		String line = new String();
		String[] aux;
		List<String> vuln = new ArrayList<String>();
		List<List<String>> vulnerabilities = new ArrayList<List<String>>();
		
		line = br.readLine();
		
		while(line != null){
			
			aux = line.split("[=;]");
			
			for(String s: aux)
				if(!s.trim().isEmpty())
					vuln.add(s.trim());			
			
			
			if(!vuln.isEmpty())
				vulnerabilities.add(vuln);
			
			vuln = new ArrayList<String>();
			
			line = br.readLine();
		}
		
		br.close();
		
		return vulnerabilities;
	}
	
}
