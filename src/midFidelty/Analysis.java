package midFidelty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.ConfusionMatrix;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.JointClassification;
import com.aliasi.classify.JointClassifier;
import com.aliasi.classify.JointClassifierEvaluator;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Files;

/*
 * @author: Mujtaba Ali
 */
public class Analysis {
	
	private static File TRAINING_DIR
    = new File("dataset/train");

	private static File TESTING_DIR
    =  new File("dataset/test");

	private static String[] CATEGORIES
    = { "teaching",
        "research" };

	private static int NGRAM_SIZE = 6;
	
	
	private JointClassifierEvaluator<CharSequence> evaluator;
	private  Classification classification;
	private JointClassification jc;
	private JointClassifier<CharSequence> compiledClassifier;
	
	
	
	
	public String classify2(String txt){
		String bestCategory="";
		String details="";

	        JointClassification jc = compiledClassifier.classify(txt);
	        bestCategory = jc.bestCategory();
	        details = jc.toString();
	        
	        System.out.println("Got best category of: " + bestCategory);
	 //       System.out.println(details);
	        System.out.println("---------------");
	    
	   
	    
	//	ConfusionMatrix confMatrix = evaluator.confusionMatrix();
	//	System.out.println("Total Accuracy: " + confMatrix.totalAccuracy());
		
	    return (String.valueOf(bestCategory.charAt(0))).toUpperCase();
	}
	
	
	
	
	public void train() throws IOException, ClassNotFoundException{
		
        DynamicLMClassifier<NGramProcessLM> classifier
        = DynamicLMClassifier.createNGramProcess(CATEGORIES,NGRAM_SIZE);

    for(int i=0; i<CATEGORIES.length; ++i) {
        File classDir = new File(TRAINING_DIR,CATEGORIES[i]);
        if (!classDir.isDirectory()) {
            String msg = "Could not find training directory="
                + classDir
                + "\nHave you unpacked 4 newsgroups?";
            System.out.println(msg); // in case exception gets lost in shell
            throw new IllegalArgumentException(msg);
        }

        String[] trainingFiles = classDir.list();
        for (int j = 0; j < trainingFiles.length; ++j) {
            File file = new File(classDir,trainingFiles[j]);
            String text = Files.readFromFile(file,"ISO-8859-1");
            System.out.println("Training on " + CATEGORIES[i] + "/" + trainingFiles[j]);
            classification
                = new Classification(CATEGORIES[i]);
            Classified<CharSequence> classified
                = new Classified<CharSequence>(text,classification);
            classifier.handle(classified);
        }
    }
    //compiling
    System.out.println("Compiling");
    @SuppressWarnings("unchecked") // we created object so know it's safe
    JointClassifier<CharSequence> compiledClassifier
        = (JointClassifier<CharSequence>)
        AbstractExternalizable.compile(classifier);
    this.compiledClassifier = compiledClassifier;
    boolean storeCategories = true;
    evaluator
        = new JointClassifierEvaluator<CharSequence>(this.compiledClassifier,
                                                     CATEGORIES,
                                                     storeCategories);
    
    System.out.println(compiledClassifier+"	"+this.compiledClassifier);
	}
	
	
	
	
	public String classify(String [] bagOfWords) throws IOException{
		ArrayList<String> research = new ArrayList<String>();
		ArrayList<String> teaching = new ArrayList<String>();
		BufferedReader reader1 = new BufferedReader(new FileReader("teach.txt"));
		BufferedReader reader2 = new BufferedReader(new FileReader("res.txt"));
		String line = "", type="";
		float R=0, T=0;
		 while ((line = reader1.readLine()) != null)
		    {
		      teaching.add(line);
		      T++;
		    }
		    reader1.close();
		    
		    while ((line = reader2.readLine()) != null)
		    {
		      research.add(line);
		      R++;
		    }
		    reader2.close();
		
		    float res=0, teach = 0;
		    for(int i=0;i<bagOfWords.length;i++){
		    	
		    	for(int j=0;j<teaching.size();j++){
		    	//	if(bagOfWords[i].equalsIgnoreCase(teaching.get(j)) || teaching.get(j).toLowerCase().contains(bagOfWords[i].toLowerCase()))
	    			if(bagOfWords[i].equalsIgnoreCase(teaching.get(j)))
		    			teach++;
		    	}
		    
		    	for(int k=0;k<research.size();k++){
		    	//	if(bagOfWords[i].equalsIgnoreCase(research.get(k)) || research.get(k).toLowerCase().contains(bagOfWords[i].toLowerCase()))
	    			if(bagOfWords[i].equalsIgnoreCase(research.get(k)))
		    			res++;
		    	}
		    }
		    
		    res = res/R;
		    teach = teach/T;
		    
		    if(res > teach){
		    	if((res-teach)/res > 0.10){
		    		System.out.println("Classified as Research");
		    		type = "R";
		    	}
		    	else{
		    		System.out.println("Classified as Gray");
		    		type = "G";
		    	}
		    }
		    
		    else if(res < teach){
		    	if((teach-res)/teach > 0.10){
		    		System.out.println("Classified as Teaching");
		    		type = "T";
		    	}
		    	else{
		    		System.out.println("Classified as Gray");
		    		type = "G";
		    	}
		    }
		    
		    System.out.println("Research : "+ (res)+"	|	"+R);
		    System.out.println("Teaching : "+ (teach)+"	|	"+T+"\n\n");
		    
		    return type;
	}
}
