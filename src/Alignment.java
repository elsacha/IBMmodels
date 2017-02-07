import java.util.HashMap;
import java.util.Map;

public class Alignment {
	private Map<String, String> alignedWords = new HashMap<String,String>();
	int exampleNumber = 0;
	private Double proba = 0.0;
	
	public Alignment(Map<String, String> words){
		for (String sourceWord: words.keySet()){
			this.alignedWords.put(sourceWord, words.get(sourceWord));
		}
		this.proba = 0.0;
	}
	
	public Alignment(Map<String, String> words, double probability, int exNum){
		for (String sourceWord: words.keySet()){
			this.alignedWords.put(sourceWord, words.get(sourceWord));
		}
		this.proba = probability;
		this.exampleNumber = exNum;
	}
	
	public double getProba(){
		return this.proba;
	}
	
	
	public void setProba(double Probability){
		this.proba = Probability;
	}
	
	public HashMap<String, String> getAlignedWords() {
		//return a copy of the alignment
		HashMap<String, String> temp = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : this.alignedWords.entrySet()) {
			temp.put(entry.getKey(), entry.getValue());
		}
		return temp;
	}
	
	int getExampleNumber(){
		return this.exampleNumber;
	}
	
//	@Override
//	public boolean equals(Object obj) {
//	    if (obj == null) {
//	        return false;
//	    }
//	    if (!Alignment.class.isAssignableFrom(obj.getClass())) {
//	        return false;
//	    }
//	    final Alignment other = (Alignment) obj;
//	    
//	    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
//	        return false;
//	    }
//	    if (this.age != other.age) {
//	        return false;
//	    }
//	    return true;
//	}
	
	public String toString(){
		String res = "";
		res += "Example number: " + this.getExampleNumber() + "\n";
		for (Map.Entry<String, String> entry : this.alignedWords.entrySet()) {
			res+= entry.getKey() + " -> " + entry.getValue() + "\n";
		}
		res+= "probability: " + this.getProba();
		return res;
	}
}
