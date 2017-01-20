import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class IBM1 {
	
	//private Map<String,Integer> alignmentCounts = new HashMap<String, Integer>();
	public Map<String, HashMap<String, Integer>> alignmentCounts = new HashMap<String, HashMap<String, Integer>>();
	
	public IBM1(){
		this.alignmentCounts = new HashMap<String, HashMap<String, Integer>>();
	}
	public void addAlignmentCounts (String source, String target){
		String[] splitSource = source.split(" ");
		String[] splitTarget = target.split(" ");
		for (String srcWord: splitSource){
			if (!alignmentCounts.containsKey(srcWord)) {
				alignmentCounts.put(srcWord, new HashMap<String, Integer>());
			}
			for (String tgtWord: splitTarget){
				if (!alignmentCounts.get(srcWord).containsKey(tgtWord)) {
					alignmentCounts.get(srcWord).put(tgtWord, 1);
				}else{
					alignmentCounts.get(srcWord).put(tgtWord, alignmentCounts.get(srcWord).get(tgtWord) +1);
				}
				
			}
		}
		
	}
	
	public String bestTranslation (String source){
		Integer max = 0;
		String bestTrans = "";
		Map <String, Integer> translations  = this.alignmentCounts.get(source);
		for (Map.Entry<String, Integer> entry : translations.entrySet()) {
			if (entry.getValue() >= max){
				max = entry.getValue();
				bestTrans = entry.getKey();
			}
		}
		return bestTrans + ", score: " + max;
	}
	public Map<String, String> bestTranslations (){
		Map<String, String> res = new HashMap<String, String>();
		for (Map.Entry<String, HashMap<String, Integer>> sourceEntry : this.alignmentCounts.entrySet()) {
			res.put(sourceEntry.getKey(), bestTranslation(sourceEntry.getKey()));
		}
		return res;
	}
	
	public String toString(){
		String res = "";
//		res += "Alignment counts: \n";
//		for (Map.Entry<String, HashMap<String, Integer>> sourceEntry : this.alignmentCounts.entrySet()) {
//		    for (Entry<String, HashMap<String, Integer>> targetEntry : ((Map<String, HashMap<String, Integer>>) sourceEntry).entrySet()) {
//			String source = sourceEntry.getKey();
//		    String target = targetEntry.getKey();
//		    Integer count = targetEntry.getValue();
//		    res+= key + " : "+ value + "\n";   
//		}
		res+= "Best translations: \n";
		for (Map.Entry<String, String> entry : this.bestTranslations().entrySet()) {
			res+= entry.getKey() + " - " + entry.getValue() + "\n";
		}
		return res;
		
	}
	
	
	public static void main(String[] args) {
//		String [] esp = {"mi grupo no ve razón alguna para no conceder la aprobación de la gestión correspondiente al presupuesto de 1996 .", "espero que el nuevo parlamento y la nueva comisión puedan hacer uso de ellas .", "es una coincidencia , aunque quizá una feliz coincidencia , que esta misma semana , que es la última de la legislatura de esta asamblea , sea también la primera semana de vida del tratado de amsterdam ."};
//		String [] eng = {"my group can see no reason not to grant discharge for the 1996 budget .", "i hope that the new parliament and the new commission will make use of them .", "it is a coincidence , but perhaps a fortunate one , that this very week when we are in the last week of the life of this house , we are also in the first week of the life of the amsterdam treaty ."};
//		
		
		ArrayList<String> espShort = new ArrayList<String>();
		ArrayList<String> engShort = new ArrayList<String>();
		File filename = new File("/Users/gadebski/Documents/NLP/alignment_tests/IBMmodels/src/corpus.en");
		Scanner sc;
		try {
			sc = new Scanner(filename);
			while (sc.hasNextLine()) {
				engShort.add(sc.nextLine());
			}
			
			System.out.println("Eng index size: " + engShort.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch bock
			e.printStackTrace();
		}
		
		File filename2 = new File("/Users/gadebski/Documents/NLP/alignment_tests/IBMmodels/src/corpus.es");
		Scanner sc2;
		try {
			sc2 = new Scanner(filename2);
			
			while (sc2.hasNextLine()) {
				espShort.add(sc2.nextLine());
			}
			System.out.println("Esp index size: " + engShort.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		IBM1 model = new IBM1();
		
		for (int i=0; i < engShort.size();  i++){
			model.addAlignmentCounts(engShort.get(i), espShort.get(i));
		}
//		System.out.println(eng[0]);
//		model.addAlignmentCounts(eng[0], fra[0]);
//		model.addAlignmentCounts(eng[1], fra[1]);
//		model.addAlignmentCounts(eng[2], fra[2]);
//	   System.out.println(model.alignmentCounts.get("the").get("la"));
//	   System.out.println(model.alignmentCounts.get("the").get("la"));
//	   System.out.println(model.bestTranslation("the"));
	   System.out.println(model);
	}
}
