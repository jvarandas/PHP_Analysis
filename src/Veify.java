import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Veify {

	public static void main(String[] args) throws IOException {
		
		PatternsParser patterns = new PatternsParser();
		List<List<String>> owsap = new ArrayList<List<String>>();
		
		owsap = patterns.parsePatternsList();
		
		

	}

}
