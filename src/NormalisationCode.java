/*
public class NormalisationCode {
	//scanner = new Scanner(file);
	
			while (scanner.hasNextLine()) {
				        String line = scanner.nextLine();
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
				    		                unigramprobs.add(Math.pow(10,Double.valueOf(lines[0])));
				                        	}
				                }
				        }
				        
						while (scanner.hasNextLine()) {
					        String line = scanner.nextLine();
					        if (line.equals("\\2-grams:")) 
					        {
					        	break;
					        }
					}
						
						for(int i=0; i<unigramprobs.size(); i++) {
							sumprob1+=unigramprobs.get(i);
							}
						
						norm1=1/sumprob1;
						unigramprobs.clear();
			       
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
					                        	bigramprobs.add(Math.pow(10,Double.valueOf(lines[0])));
					                        	}
					                }
					        }
					        
							while (scanner.hasNextLine()) {
						        String line = scanner.nextLine();
						        if (line.equals("\\3-grams:")) 
						        {
						        	break;
						        }
						}
							
							for(int i=0; i<bigramprobs.size(); i++) {
								sumprob2+=bigramprobs.get(i);
								}
							
							norm2=1/sumprob2;
							bigramprobs.clear();
							
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
					                        	trigramprobs.add(Math.pow(10,Double.valueOf(lines[0])));
					                        	}
					                }
					        }
							
							for(int i=0; i<trigramprobs.size(); i++) {
								sumprob3+=trigramprobs.get(i);
								}
							
							norm3=1/sumprob3;
							trigramprobs.clear();
							
							System.out.println("Norm constant 1 = "+norm1+" "+sumprob1);
							System.out.println("Norm constant 2 = "+norm2+" "+sumprob2);
							System.out.println("Norm constant 3 = "+norm3+" "+sumprob3);
}
*/