import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Vocabulary {
	private Map<String, Integer> words = new HashMap<String,Integer>();
	
	public Vocabulary (ArrayList<String> input){
		for (String sentence: input){
			String[] splitInput = sentence.split(" ");
			for (String w: splitInput){
				if (this.words.containsKey(w)){
					this.words.put(w, this.words.get(w) + 1);
				}else{
					this.words.put(w, 1); 
				}
		   }
		}
	}
	
	public Map<String, Integer> getWords(){
		//return a copy of words HashMap
		Map<String, Integer> temp = new HashMap<String, Integer>();
		for (String w: words.keySet()){
			temp.put(w, words.get(w));
		}
		return temp;
	}
	
	public int getSize(){
		return this.words.size();
	}
	
	public String toString(){
		String res = "";
		for (String w: this.words.keySet()){
			res+= w + " - num occurences : " + this.words.get(w) + "\n";
		}
		return res;
	}
	
}
