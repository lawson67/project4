import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ChatFilter {
	
	ArrayList<String> bad = new ArrayList<String>();
	
    public ChatFilter(String badWordsFileName) {
	    InputStream f;
	    String line;
		try {
			f = new FileInputStream(badWordsFileName);
		    InputStreamReader ir = new InputStreamReader(f, Charset.forName("UTF-8"));
		    BufferedReader br = new BufferedReader(ir);
		    while ((line = br.readLine()) != null) {
		    	bad.add(line.toLowerCase());
		    }
		    br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }

    public String filter(String msg) {
		String copy = msg;
		msg = msg.toLowerCase();
		
	    for (int r = 0; r < bad.size(); r++){
	        if (msg.contains(bad.get(r).toLowerCase())){
	        	String filled = "";
	        	for (int i = 0; i < bad.get(r).length(); i++){
	        		filled += "*";
	        	}
	        	msg = msg.replaceAll(bad.get(r).toLowerCase(), filled);
	        }
    	}
	    
		for (int j = 0; j < copy.length(); j++){
			if (msg.charAt(j) != '*'){
				msg = msg.substring(0,j) + copy.charAt(j) + msg.substring(j+1, msg.length());
			}
		}
		
        return msg;
    }
}
