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
	private Map<String, HashMap<String, Integer>> alignmentCounts = new HashMap<String, HashMap<String, Integer>>();
	
	//t values for the EM algorithm
	private Map<String, HashMap<String, Double>> t = new HashMap<String, HashMap<String, Double>>();
	
	private ArrayList<Alignment> alignments = new ArrayList<Alignment>();
	
	ArrayList<String> source = new ArrayList<String>();
	ArrayList<String> target = new ArrayList<String>();
	private Vocabulary sourceVocabulary;
	private Vocabulary targetVocabulary;
	
	public IBM1(ArrayList<String> src, ArrayList<String> tgt){
		this.alignmentCounts = new HashMap<String, HashMap<String, Integer>>();
		this.alignments = new ArrayList<Alignment>();
		this.sourceVocabulary = new Vocabulary(src);
		this.targetVocabulary = new Vocabulary(tgt);
		this.source = src;
		this.target = tgt;
		//initialize t probabilities = initialization step of the EM algorithm
		for (String srcW: sourceVocabulary.getWords().keySet()){
			if (!t.containsKey(srcW)) {
				t.put(srcW, new HashMap<String, Double>());
			}
			for (String tgtW: targetVocabulary.getWords().keySet()){
				if (!t.get(srcW).containsKey(tgtW)) {
					t.get(srcW).put(tgtW, 1.0/sourceVocabulary.getSize());
				}
			}
		}
		setAllAlignments(src, tgt);
	}
	static ArrayList<ArrayList<Integer>> permute(int[] numbers) {
	    // we use a list of lists rather than a list of arrays 
	    // because lists support adding in the middle
	    // and track current length
	    ArrayList<ArrayList<Integer>> permutations = new ArrayList<ArrayList<Integer>>();
	    // Add an empty list so that the middle for loop runs
	    permutations.add(new ArrayList<Integer>());

	    for ( int i = 0; i < numbers.length; i++ ) {
	        // create a temporary container to hold the new permutations 
	        // while we iterate over the old ones
	        ArrayList<ArrayList<Integer>> current = new ArrayList<ArrayList<Integer>>();
	        for ( ArrayList<Integer> permutation : permutations ) {
	            for ( int j = 0, n = permutation.size() + 1; j < n; j++ ) {
	                ArrayList<Integer> temp = new ArrayList<Integer>(permutation);
	                temp.add(j, numbers[i]);
	                current.add(temp);
	            }
	        }
	        permutations = new ArrayList<ArrayList<Integer>>(current);
	    }

	    return permutations;
	}
	
	public void setAlignmentsForOneSentencePair (String source, String target, int exNum){
		String[] splitSource = source.split(" ");
		String[] splitTarget = target.split(" ");
		int [] nums = new int[splitSource.length];
		for (int i =0; i< nums.length; i++){
			nums[i] = i;
		}
		ArrayList<ArrayList<Integer>> permutations = new ArrayList<ArrayList<Integer>>();
		permutations = permute(nums);
		//System.out.print(permutations);
		for (ArrayList<Integer> entry: permutations){
			Map<String, String> tempMap= new HashMap<String, String>();
				for (int i = 0; i< entry.size(); i++){
					tempMap.put(splitSource[i], splitTarget[entry.get(i)]);
				}
			Alignment temp = new Alignment(tempMap, 1.0, exNum);
			//System.out.println(temp);
			this.alignments.add(temp);
		}
		
		
	}
	
	public void setAllAlignments(ArrayList<String> source, ArrayList<String> target){
		for (int i=0; i< source.size(); i++){
			//System.out.println("here");
			setAlignmentsForOneSentencePair(source.get(i), target.get(i), i+1);
		}
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
	
	//get t probability value for a certain translation
	public double getT(String source, String target){
		double res = 0.0;
		for (String src: this.t.keySet()){
			for (String tgt: this.t.get(src).keySet()){
				if (src.equals(source) && tgt.equals(target)){
					res = t.get(src).get(tgt);
				}
			}
		}
		return res;
	}
	
	//sum the probabilities of all alignments of a pair of sentences (original and translation)
	public double sumProbasOfAlignment(int exampleNumber){
		double res = 0.0;
		for (Alignment a: this.alignments) {
			if (a.getExampleNumber() == exampleNumber){
				res+= a.getProba();
			}
		}
		return res;
	}
	
	
	//normalize alignments of one pair
	public void normalizeAlignmentsOfOnePair(int exampleNumber){
		//sum probabilities before modifying them
		double normFactor = sumProbasOfAlignment(exampleNumber);
		for (Alignment a: this.alignments) {
			if (a.getExampleNumber() == exampleNumber){
				a.setProba(a.getProba()/normFactor);	
			}
		}
	}
	
	//normalize all alignments - Step 2
		public void normalizeAllAlignments(){
			//System.out.println("Normalize one pair "+ this.source.size());
			for (int i=1; i <= this.source.size(); i++){
				normalizeAlignmentsOfOnePair(i);
			}
		}
		
		//collect fractional counts for each translation pair - Step3
		public void collectFractionalCountsForTranslationPairs(){
			for (String src: this.t.keySet()){
				for (String tgt: this.t.get(src).keySet()){
					//delete previous counts
					this.t.get(src).put(tgt, 0.0);
					
					for (Alignment a: this.alignments){
						for (Map.Entry<String, String> entry : a.getAlignedWords().entrySet()) {
							if (entry.getKey().equals(src) && entry.getValue().equals(tgt)){
								this.t.get(src).put(tgt, this.t.get(src).get(tgt) + a.getProba());
							}
						}
					}
				}
			}
		}
		
		
		//normalize fractional t counts by the sum of counts where source word occurs
		public void nomalizeFractionalTCounts(){
			//make a copy of previous t values
			Map<String, HashMap<String, Double>> tempT = new HashMap<String, HashMap<String, Double>>();
			for (String source: this.t.keySet()){
				tempT.put(source, new HashMap<String, Double>());
				for (String target: this.t.get(source).keySet()){
					tempT.get(source).put(target, this.t.get(source).get(target));
				}
			}
			
			for (String src: this.t.keySet()){
				double normalizationFactor = 0.0;
				for (String copyTarget: tempT.get(src).keySet()){
					normalizationFactor+= tempT.get(src).get(copyTarget);
				}
				for (String tgt: this.t.get(src).keySet()){
					this.t.get(src).put(tgt, this.t.get(src).get(tgt)/normalizationFactor);
				}
			}
		}
	
	//********************* EM algorithm **********************************
	public void EM(int N){
				//initialization is done by the class constructor
		for (int i = 0; i < N; i++ ){		
				//compute alignment probabilities from initial t values
				
				for (Alignment a: this.alignments){
					for (Map.Entry<String, String> entry : a.getAlignedWords().entrySet()) {
						double tProba = getT(entry.getKey(), entry.getValue());
						a.setProba(a.getProba()*tProba);
					}
					
				}
				
				//normalize by the sum of probabilities for this sentence
				normalizeAllAlignments();
				
				//Step 3 - collect fractional counts for translation pairs
				collectFractionalCountsForTranslationPairs();
				
				//Step 4 - normalize t counts
				nomalizeFractionalTCounts();
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
		res+= "Alignments: \n";
		res+= "number of alignments: " + this.alignments.size() + "\n";
		for (Alignment entry : this.alignments) {
			res+= entry.toString() +"\n";
		}
		res+= "T probabilities: \n";
		for (String srcVal: this.t.keySet()){
			for (String tgtVal: this.t.get(srcVal).keySet()){
				res+= tgtVal + " -> ";
				res+= srcVal + ": " + this.t.get(srcVal).get(tgtVal) + "\n";
			}
		}
		res+= "\n";
		
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

//		String [] en = {"the blue house", "the house", "the"};
//		String [] fr = {"la maison bleue", "la maison", "la"};
		
		
		ArrayList<String> en = new ArrayList<String>();
		en.add("the blue house");
		en.add("the house");
		en.add("the");
		
		ArrayList<String> fr = new ArrayList<String>();
		fr.add("la maison bleue");
		fr.add("la maison");
		fr.add("la");

		
		//		ArrayList<String> espShort = new ArrayList<String>();
//		ArrayList<String> engShort = new ArrayList<String>();
//		File filename = new File("/Users/gadebski/Documents/NLP/alignment_tests/IBMmodels/src/corpus.en");
//		Scanner sc;
//		try {
//			sc = new Scanner(filename);
//			while (sc.hasNextLine()) {
//				engShort.add(sc.nextLine());
//			}
//			
//			System.out.println("Eng index size: " + engShort.size());
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch bock
//			e.printStackTrace();
//		}
//		
//		File filename2 = new File("/Users/gadebski/Documents/NLP/alignment_tests/IBMmodels/src/corpus.es");
//		Scanner sc2;
//		try {
//			sc2 = new Scanner(filename2);
//			
//			while (sc2.hasNextLine()) {
//				espShort.add(sc2.nextLine());
//			}
//			System.out.println("Esp index size: " + engShort.size());
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Vocabulary enVocabulary = new Vocabulary(en);
		//System.out.println(enVocabulary);
		
		Vocabulary frVocabulary = new Vocabulary(fr);
		//System.out.println(frVocabulary);
		
		IBM1 model = new IBM1(en, fr);
		model.EM(3);
		//System.out.println(model.sumProbasOfAlignment(1));

	   System.out.println(model);
	   
//	   int[] numbers = {0,1,2};
//	   System.out.println(permute(numbers));
	}
}
