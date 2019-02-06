import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.*;
import java.io.*;
import java.util.Scanner;

import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import javax.sound.sampled.UnsupportedAudioFileException;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;
import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.linguist.language.ngram.large.LargeNGramModel;
import edu.cmu.sphinx.linguist.WordSequence;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.LatticeOptimizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.TimerPool;
import edu.cmu.sphinx.util.props.ConfigurationManager;


public class TopicModeller {

	WordSequence words;
	InstanceList instances;
	ParallelTopicModel model;
	static String resultText;
	
	// Containers for words extracted from topic model with rank from model, all topics + topic prob and 
	//	highest probability topic + words associated with it
	
	static HashMap<String, Integer> wordsrank = new HashMap<String, Integer>();
	static Map<Integer, Double> testprobabilities = new HashMap<Integer, Double>();
	static Map<Integer, String> topicwords = new HashMap<Integer, String>();
	
	//	Transcripts of wavs for WER
	
	static String [] BC1 = "AUSTRALIAN RESEACHERS ARE OPTIMISTIC THEY HAVE FOUND A WAY TO TREAT TRIPLE NEGATIVE BREAST CANCER AN AGGRESSIVE DISEASE THAT MAINLY AFFECTS YOUNGER WOMEN".split(" ");
	static String [] BC2 = "BREAST CANCER IS THE MOST COMMON CANCER AMONGST WOMEN IN AUSTRALIA".split(" ");
	static String [] BC3 = "BREAST CANCER ELICITS SO MANY FEARS INCLUDING THOSE RELATING TO DEATH, SURGERY, LOSS OF BODY IMAGE AND LOSS OF SEXUALITY".split(" ");
	static String [] MLOBAMA = "CREATING ALGORITHMS TO INTERPRET MOUNTAINS OF DATA THE MACHINE LEARNING EXPERT PLAYED A GROUND-BREAKING ROLE AS CHIEF SCIENTIST FOR THE OBAMA FOR AMERICA CAMPAIGN".split(" ");
	static String [] HEALTH1 = "HAVE KNOWN FOR AT LEAST THREE YEARS THAT MILLIONS OF AMERICANS WOULD NOT BE ABLE TO KEEP THEIR CURRENT HEALTH CARE PLAN".split(" ");
	static String [] HEALTH2 = "LOCAL COUNCILS PLAY A SIGNIFICANT ROLE BY MAKING SURE THE ENVIRONMENTS WE LIVE, WORK AND SOCIALIZE IN ENCOURAGE REGULAR EXERCISE".split(" ");
	static String [] SPEECH1 = "RESEARCH INTO SPHINX AND LANGUAGE MODELING FOR BETTER SPEECH RECOGNITION REQUIRES MANY MORE WORDS".split(" ");
	static String [] SPEECH2 = "BEFORE YOU GET STARTED USING WINDOWS SPEECH RECOGNITION, YOU'LL NEED TO CONNECT A MICROPHONE TO YOUR COMPUTER".split(" ");
	static String [] SPEECH3 = "PEOPLE WITH DISABILITIES THAT PREVENT THEM FROM TYPING HAVE ALSO ADOPTED SPEECH RECOGNITION SYSTEMS".split(" ");
	static String [] SPEECH4 = "PROGRESS HAS COME THANKS IN PART TO STEADY PROGRESS IN THE TECHNOLOGY NEEDED TO HELP MACHINES UNDERSTAND HUMAN SPEECH".split(" ");
			
	public static void main(String[] args) throws Exception {
		
		TopicModeller tm = new TopicModeller();
		tm.buildTopicModel("resources/corpus.txt");
		
	        URL audioURL, url;
	        if (args.length > 0) {
	            audioURL = new File(args[0]).toURI().toURL();
	        } else {
	            audioURL = TopicModeller.class.getResource("BC1.wav");
	        }

	        if (args.length > 1) {
	            url = new File(args[1]).toURI().toURL();
	        } else {
	            url = TopicModeller.class.getResource("config.xml");
	        }

	        System.out.println("Loading recogniser...");
	        ConfigurationManager cm = new ConfigurationManager(url);

	        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
	        recognizer.allocate();

	        // configure the audio input for the recognizer
	        AudioFileDataSource dataSource = (AudioFileDataSource) cm.lookup("audioFileDataSource");
	        dataSource.setAudioFile(audioURL, null);

	        boolean done = false;
	        while (!done) {
	             /*This method will return when the end of speech
	            * is reached. Note that the endpointer will determine
	            * the end of speech.*/
	            
	            Result result = recognizer.recognize();

	            if (result != null) {
	                Lattice lattice = new Lattice(result);
	                LatticeOptimizer optimizer = new LatticeOptimizer(lattice);
	                optimizer.optimize();
	                lattice.dumpAllPaths();
	                resultText = result.getBestResultNoFiller();
	                System.out.println("I heard: " + resultText + '\n');
	            } else {
	                done = true;
	            }
	        }
	        
	        
	        //	calculate WER
	        
	        WordSequenceAligner werEval = new WordSequenceAligner();
			
			String [] hyp = resultText.split(" ");
			WordSequenceAligner.Alignment a = werEval.align(BC1, hyp);
			System.out.println(a);
	        
			
			
			// pass text into topic model
	    double[] testProbabilities = tm.classify(resultText);
		for (int i = 0; i < 100; i++) {
			testprobabilities.put(i, testProbabilities[i]);
			}
		
		//	Find highest probability topic		
		
		Object highesttopic=0;
		
		double maxValueInMap=(Collections.max(testprobabilities.values()));  // This will return max value in the Hashmap
		for (Entry<Integer, Double> entry : testprobabilities.entrySet()) {  // Iterate through hashmap
			if (entry.getValue()==maxValueInMap) {
                highesttopic=entry.getKey();
				System.out.println("Highest probability topic: "+highesttopic);     // Print the key with max value
            }
        }
		
		//	get words associated with highest probability topic
		
	       String words= null;
	        for(Map.Entry entry: topicwords.entrySet()){
	            if(highesttopic.equals(entry.getKey())){
	                words = entry.getValue().toString();
	                System.out.println("Words given highest probability topic: "+words);     // Print the key with max value
	            }
	        }

	        StringTokenizer st = new StringTokenizer(words, " ");
	        while( st.hasMoreElements() )
            	{
	            String key = st.nextToken().toUpperCase();
	            String value = st.nextToken();
	           // wordsrank.put(key, Integer.valueOf(value));
	            wordsrank.put(key, Integer.valueOf(value));
            	}
	        
	        //	Clear unneeded Hashmaps
	        
	        topicwords.clear();
	        testprobabilities.clear();
	        
	  //  tm.adaptProbs(wordsrank);
	        
	    loadLM();
	}
	
    public void adaptProbs(HashMap wordsrank) throws Exception {
        URL url;
        url = TopicModeller.class.getResource("config.xml");
        
    	ConfigurationManager cm = new ConfigurationManager(url);
    	edu.cmu.sphinx.linguist.language.ngram.large.KeywordOptimizerLargeNGramModel model = (edu.cmu.sphinx.linguist.language.ngram.large.KeywordOptimizerLargeNGramModel)cm.lookup("trigramModel");
    	//KeywordOptimizerLargeNGramModel model = new KeywordOptimizerLargeNGramModel();
    	//model.keywordProbs = this.wordsrank;
    	
/*    	String word1 = "AARON";
    	String word2 = "LOVES";
    	
    	words = new LargeNGramModel().getBigramProb(2, 4);*/
    	
    	//model.getProbability(words);
    }
	
	public void buildTopicModel(String fileName) throws Exception {
		// Begin by importing documents from text to feature sequences
				ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

				// Pipes: lowercase, tokenize, remove stopwords, map to features
				pipeList.add( new CharSequenceLowercase() );
				pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
				pipeList.add( new TokenSequenceRemoveStopwords(new File("resources/en.txt"), "UTF-8", false, false, false) );
				pipeList.add( new TokenSequence2FeatureSequence() );

				instances = new InstanceList (new SerialPipes(pipeList));

				Reader fileReader = new InputStreamReader(new FileInputStream(new File(fileName)), "UTF-8");
				instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
													   3, 2, 1)); // data, label, name fields

				// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
				//  Note that the first parameter is passed as the sum over topics, while
				//  the second is 
				int numTopics = 100;
				model = new ParallelTopicModel(numTopics, 1.0, 0.01);

				model.addInstances(instances);

				// Use two parallel samplers, which each look at one half the corpus and combine
				//  statistics after every iteration.
				model.setNumThreads(2);

				// Run the model for 50 iterations and stop (this is for testing only, 
				//  for real applications, use 1000 to 2000 iterations)
				model.setNumIterations(50);
				model.estimate();

				// Show the words and topics in the first instance

				// The data alphabet maps word IDs to strings
				Alphabet dataAlphabet = instances.getDataAlphabet();
				
				FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
				LabelSequence topics = model.getData().get(0).topicSequence;
				
				Formatter out = new Formatter(new StringBuilder(), Locale.US);
				for (int position = 0; position < tokens.getLength(); position++) {
					out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
				}
				
				// Estimate the topic distribution of the first instance, 
				//  given the current Gibbs state.
				double[] topicDistribution = model.getTopicProbabilities(topics);
				
				// Get an array of sorted sets of word ID/count pairs
		        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		        
				  // Show top 5 words in topics with proportions for the first document
		        
		        for (int topic = 0; topic < numTopics; topic++) {
		            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
		            
		            out = new Formatter(new StringBuilder(), Locale.US);
		            out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
		            int rank = 0;
		            while (iterator.hasNext()) {
		                IDSorter idCountPair = iterator.next();
		                out.format("%s %.0f ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
		                rank++;
		            }
		            
		            //	Put all topics and words into a container for querying later
		            
		            String[] words = out.toString().split("\t");
		            topicwords.put(Integer.valueOf(words[0]), words[2]);
		        }
	}
	
	public double[] classify(String text) {
		// Create a new instance named "test instance" with empty target and source fields.

		InstanceList testing = new InstanceList(instances.getPipe());
		testing.addThruPipe(new Instance(text, null, "test instance", null));

		TopicInferencer inferencer = model.getInferencer();
		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		return testProbabilities;
		
	}

	public static void loadLM() throws Exception {
		
		File file = new File("resources/input.lm");
		

		
		Scanner scanner = new Scanner(file);
		
		FileWriter writer = new FileWriter("resources/output.lm");
		
		while (scanner.hasNextLine()) {
		        String line = scanner.nextLine();
		        writer.write(line+"\n");
		        if (line.equals("\\1-grams:")) 
		        {
		        	break;
		        }
		}
       
		        if (!scanner.hasNextLine()) {System.out.println("crap no 1grams!");}
		        else {
		                while (scanner.hasNextLine()) {
		                        String line = scanner.nextLine();
		                        if (line.equals("")) 
		                        {
		                        	break;
		                        }      
		                        else {
		                        		String[] lines = line.split(" ");
		                        		
		                        		//	Check unigrams against word list
		                        		String inc="1.0";
		    		                	for(String word: lines){
		    		                    if(wordsrank.containsKey(word))
		    		                    {  
		    		                    	DecimalFormat df = new DecimalFormat( "##0.0000");
		    		                    	//System.out.println(word + " matches a unigram");
		    		                        
		    		                    	//	Proportionate rank from topic model
		    		                    	
		    		                    	int num=wordsrank.size();
		    		                    	double rank=1.0*(num/wordsrank.size());
		    		                    	num--;
		    		                    	//	Adjusted probability
		    		                    	
		    		                        double prob=Double.parseDouble(lines[0])+(Double.parseDouble(inc)*(rank));
		    		                        String logprob=df.format(prob);
		    		                        lines[0]=logprob;
		    		                    } 
		    		                	}
		    		                    writer.write(lines[0] + " " + lines[1] + " " + lines[2]+"\n");
		                        	}
		                }
		        }
				while (scanner.hasNextLine()) {
			        String line = scanner.nextLine();
			        if (line.equals("\\2-grams:")) 
			        {
			        	writer.write("\n\\2-grams:\n");
			        	break;
			        }
			}
		        if (!scanner.hasNextLine()) {System.out.println("crap no 2grams!");}
		        else {
		                while (scanner.hasNextLine()) {
		                        String line = scanner.nextLine();
		                        if (line.equals("")) 
		                        {
		                        	break;
		                        }      
		                        else {
	                        		String[] lines = line.split(" ");
	                        		
	                        		//	Check bigrams against word list
	                        		
	        		                String inc="1.0";
	    		                	for(String word: lines){
	    		                    if(wordsrank.containsKey(word))
	    		                    {  
	    		                    	DecimalFormat df = new DecimalFormat( "##0.0000");
	    		                    	
	    		                    	//System.out.println(word + " matches a bigram");
	    		                    	
	    		                    	//	Proportionate rank from topic model
	    		                    	
	    		                    	int num=wordsrank.size();
	    		                    	double rank=1.0*(num/wordsrank.size());
	    		                    	num--;
	    		                    	
	    		                    	//	Adjusted probability
	    		                    	
	    		                        double prob=Double.parseDouble(lines[0])+(Double.parseDouble(inc)*(rank));
	    		                        String logprob=df.format(prob);
	    		                        lines[0]=logprob;
	    		                    } 
	    		                	}
	    		                    writer.write((lines[0]) + " " + lines[1] + " " + lines[2] + " " + lines[3] + "\n");
		                        }
		                }
		        }
		        
				while (scanner.hasNextLine()) {
			        String line = scanner.nextLine();
			        if (line.equals("\\3-grams:")) 
			        {
			        	writer.write("\n\\3-grams:\n");
			        	break;
			        }
			        
			}
		        if (!scanner.hasNextLine()) {System.out.println("crap no 3grams!");}
		        else {
		                while (scanner.hasNextLine()) {
		                        String line = scanner.nextLine();
		                        if (line.equals("")) 
		                        {
		                        	break;
		                        }      
		                        else {
	                        		String[] lines = line.split(" ");
	                        		
	                        		//	Check trigrams against word list
	                        		
	                        		String inc="1.0";
	    		                	for(String word: lines){
	    		                    if(wordsrank.containsKey(word))
	    		                    {  
	    		                    	DecimalFormat df = new DecimalFormat( "##0.0000");
	    		                    	
	    		                    	//System.out.println(word + " matches a trigram");
	    		                    	
	    		                    	//	Proportionate rank from topic model
	    		                    	
	    		                    	int num=wordsrank.size();
	    		                    	double rank=1.0*(num/wordsrank.size());
	    		                    	num--;
	    		                    	
	    		                    	//	Adjusted probability
	    		                    	
	    		                        double prob=Double.parseDouble(lines[0])+(Double.parseDouble(inc)*(rank));
	    		                        String logprob=df.format(prob);
	    		                        lines[0]=logprob;
	    		                    } 
	    		                	}
	    		                    writer.write((lines[0]) + " " + lines[1] + " " + lines[2] + " " + lines[3] + "\n");
		                        }
		                }
		        }
		        
		        writer.write("\n\\end\\");
		        writer.close();

		//	testing
		
		/*for(int i=0; i<u	nigram.size(); i++)
		{
		       System.out.println(unigram.get(i));
		}
		
		for (Map.Entry entry : testprobabilities.entrySet()) {
		System.out.println(entry.getKey() + "," + entry.getValue());
		}
		
		for (Map.Entry entry : topicwords.entrySet()) {
		System.out.println(entry.getKey() + "," + entry.getValue());
		}*/	
		
		// delete input file and rename output as input
		
		try{
 
    		if(file.delete()){
    			System.out.println(file.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation has failed.");
    		}
    		
    		File output =new File("resources/output.lm");
     
    		if(output.renameTo(file)){
    			System.out.println("Rename successful");
    		}else{
    			System.out.println("Rename failed");
    		}
 
    	}catch(Exception e){
 
    		e.printStackTrace();
 
    	}
		
//		Convert .lm to .dmp
		
			// set up the command and parameter
		
			String ScriptPath = "resources/builddmp.sh";
			String[] cmd = new String[2];
			cmd[0] = "sh";
			cmd[1] = ScriptPath;
			 
			// create runtime to execute external command
			
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(cmd);
			 
			// retrieve output from script
			
			BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while((line = bfr.readLine()) != null) {
			
				// display each output line from script
				
			System.out.println(line);
			}	
		}
	}