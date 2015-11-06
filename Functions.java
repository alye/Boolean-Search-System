import java.util.*;
import java.io.*;

/** Contains publicly accessible methods to: <br> 1. Get top 'k' terms
 * <br> 2. Get postings for a given term
 * <br> 3. Term At A Time Query(AND)
 * <br> 4. Term At A Time Query(OR)
 * <br> 5. Document At A Time Query(AND) 
 * <br> 6. Document At A Time Query(OR)
 * <br> 7. Write function headers to the output file
 * @author Alizishaan Khatri */
public class Functions {
	private Hashtable<String, LinkedList<String>> taat,daat;
	private PrintWriter outfile;
	private int comparisons=0;
	
	/** Store references to the two Hashtables created in memory in the previous stage.
	 * <br> Also create an output file for logging
	 * @param t Reference to the hashtable ordered by term frequency 
	 * @param d Reference to the hashtable ordered by Document ID
	 * @param fil Path of output file */
	Functions(Hashtable<String, LinkedList<String>> t,Hashtable<String, LinkedList<String>> d, String fil){
		this.taat=t;
		this.daat=d;
			
		try{
			outfile=new PrintWriter(fil,"UTF-8");
		}catch(FileNotFoundException e){
			System.out.println("Please check output file path!");
			System.exit(1);
		}catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported Encoding!");
			System.exit(1);
		}
	}
	
	/** Closes the output file 
	 * @param none
	 * @return void	 */
	public void close(){
		outfile.close();
	}
	
	/** Gets postings for a given query term from both Hashtable instances 
	 * and writes the results to a file.
	 * @param term <i>String</i> Enter the term that you want to search
	 * @return void	 */
	public void getPostings(String term){
		//Retrieve terms from Hashtable
		LinkedList<String> l=taat.get(term);
		LinkedList<String> m=daat.get(term);
		
		//Write function description to file
		outfile.write("\nFUNCTION: getPostings "+term);
		
		//Check if term exists in the index
		if(!(l==null || m==null)){
			
			String l_temp=l.toString();
			String m_temp=m.toString();
			
			outfile.write("\nOrdered by doc IDs: "+l_temp.substring(1,l_temp.length()-1));
			outfile.write("\nOrdered by TF: "+m_temp.substring(1,m_temp.length()-1));
		}else{
			outfile.write("\nterm not found");
		}
		}
	
	/** Calculates the top 'k' terms of the given data
	 * @param k <i>int</i> No of top terms required
	 * @param index The Hashmap containing the postings lists
	 * @return void	 */
	public void getTopK(int k,Hashtable<String,LinkedList<String>> index){
		Enumeration<String> l=index.keys();
		
		/** ArrayList of tok_freq objects to store token and corresponding postings list size */
		List<tok_freq> c=new ArrayList<tok_freq>();
				
		while(l.hasMoreElements()){
			String tok = l.nextElement();
			int freq =index.get(tok).size();
			tok_freq d=new tok_freq(tok,freq);
			c.add(d);		
		}
		
		//Sort the given list using an inbuilt implementation of TimSort Algorithm
		Collections.sort(c);
		
		//Write function header to file
		outfile.write("FUNCTION: getTopK "+k+"\nResult: ");
		
		//Write output results to file
		if(k<=c.size()){
			for(int i=0;i<k;i++){
				outfile.write(c.get(i).tok);
				if(i!=k){
					outfile.write(", ");
				}
			}
		}		
	}
	
	/** Perform AND on the given set of queries using Term At A Time
	 * @param A List object of LinkedList(String) containing postings lists	 * 
	 * corresponding to the entered queries 
	 * @param isOptimized Set to true if list is optimized */
	public void TAAT_AND(String[] queries, boolean isOptimized){
		
		//Record start time		
		long s_time=System.currentTimeMillis();
		
		//Create ArrayList of Postings lists corresponding to the given queries
		ArrayList<LinkedList<String>> postings=new ArrayList<LinkedList<String>>();		
		for(int i=0;i<queries.length;i++)
			postings.add(taat.get(queries[i]));
				
		//count comparisons
		comparisons=0;
		
		//Create LinkedList to store final answer
		LinkedList<String> result_and=new LinkedList<String>();
		
		//Initialize result_array with first term
		if(postings.get(0)!=null){
			String p=postings.get(0).toString();
			p=p.substring(1,p.length()-1);
			String[] s=p.split(", ");
			for(int i=0;i<s.length;i++)
				result_and.add(s[i]);
		}else{
			result_and.add("term not found");						//Case when 1st query is not in index
		}
		
		//Run a loop to go through all the n postings lists
		for(int ii=1;ii<postings.size();ii++)
			if (rec_AND(postings.get(ii),result_and)==false){		//Case when any query term except the first one is not in index
				result_and.clear();
				result_and.add("term not found");
				break;				
			}
						
		
		//Record end time
		long end_time=System.currentTimeMillis();
		
		//Determine number of documents in result
		int doc_size;						//store number of documents in result
		boolean isResultValid=true;		//changed to false in case term not found
		
		if(result_and.isEmpty()){
			doc_size=0;
		}else if(result_and.getFirst().equals("term not found")){
			doc_size=0;
			isResultValid=false;
		}else{
			doc_size=result_and.size();
		}
		
		/* Write unoptimized output to file 
		 * and optimize the output */
		if(isOptimized==false){
			outfile.write("\n"+doc_size+" documents are found");
			outfile.write("\n"+comparisons+" comparisons are made");
			outfile.write("\n"+((end_time-s_time)*0.003)+" seconds are used"); 	//Converting milliseconds to seconds
			
			//Create String[] of queries arranged for optimization
			String[] optimized_quer=queries.clone();
			//Sort the query list in ascending order of Posting list size
			if(queries.length>=2 && isResultValid){
				for(int i=0;i<optimized_quer.length-1;i++)
					for(int j=0;j<optimized_quer.length-1;j++)
						if(taat.get(optimized_quer[j]).size()>taat.get(optimized_quer[j+1]).size()){
							String temp=optimized_quer[j];
							optimized_quer[j]=optimized_quer[j+1];
							optimized_quer[j+1]=temp;			
										
						}
			}				
			
			//Process optimized list
			TAAT_AND(optimized_quer, true);			
		}else{
			outfile.write("\n"+comparisons+" comparisons are made with optimization");
			
			/* Parse the results according to specified output format */			
			String result=result_and.toString(); 						//Get String representation of list
			if(result.length()>2){										//Check if list is non-empty
				result=result.substring(1,result.length()-1);			//Strip out the terminal square brackets 
			}else{
				result="";
			}
			
			outfile.write("\n"+"Result: "+result);
			
		}
	}
	
	/** Internal method to perform TAAT AND on two Array Lists
	 * @param new_data Enter new array here
	 * @param answer_data Reference to answer Array */	
	private boolean rec_AND(LinkedList<String> new_data,LinkedList<String> answer_data){
		boolean match;
		
		//Check if entered postings list is empty
		if(new_data==null){
			// result_and.add("term not found");
			return false;
		}else{
		/* Compare each element of the answer list to each element of the data list */
		for(int i=0;i<answer_data.size();i++){
				match=false;
				//Store current element of answer list in an integer variable			
				int ext=Integer.parseInt(answer_data.get(i));
				
				//Compare current element of answer list to all elements of data until a match is found or end of list
				for(int j=0;j<new_data.size();j++){
					//Increment comparisons counter
					comparisons++;
					//Check if data element matches the one in the answer list 
					if(ext==Integer.parseInt(new_data.get(j))){
						match=true;		//report match
						break;			//exit 'j' loop
						}
					}
				
				/* Case when current element of answer list fails to match any element
				 * of new data.*/
				if (match==false){
					answer_data.remove(i); 			//remove the element from the answer list
					/* decrement 'i' as the next value in the answer list will
					 *  be moved the current position. */
					--i;							
					}
				}
		}
		//Since we have reached here means the method has executed successfully
		return true;
	}	
	
	/** Perform OR on the given set of queries using Term At A Time
	 * @param A List object of LinkedList(String) containing postings lists	 * 
	 * corresponding to the entered queries 
	 * @param isOptimized Set to true if list is optimized */
	public void TAAT_OR(String[] queries, boolean isOptimized){		
		
		//Record start time		
		long s_time=System.currentTimeMillis();
		
		
		//Create ArrayList of Postings lists corresponding to the given queries
		ArrayList<LinkedList<String>> postings=new ArrayList<LinkedList<String>>();		
		for(int i=0;i<queries.length;i++)
			postings.add(taat.get(queries[i]));
				
		//count comparisons
		comparisons=0;
		
		//Create LinkedList to store final answer
		LinkedList<String> result_and=new LinkedList<String>();
		
		//Initialize result list with first postings list
		if(postings.get(0)!=null){
			String p=postings.get(0).toString();
			p=p.substring(1,p.length()-1);
			String[] s=p.split(", ");
			for(int i=0;i<s.length;i++)
				result_and.add(s[i]);
		}
		
		boolean isResultValid=false;						//Indicates the validity of result set. Empty result set is considered as false
		
		//Run a loop to go through all the n postings lists
		for(int ii=1;ii<postings.size();ii++)
			isResultValid=isResultValid || rec_OR(postings.get(ii),result_and); 	//isResultValid becomes true when first non-null term is encountered in query list
						
		
		//Record end time
		long end_time=System.currentTimeMillis();
		
		//Determine number of documents in result
		int doc_size=result_and.size();						//store number of documents in result
		
		
		/*if(result_and.isEmpty()){
			doc_size=0;
		}else if(result_and.getFirst().equals("term not found")){
			doc_size=0;
			isResultValid=false;
		}else{
			doc_size=result_and.size();
		}*/
		
		
		
		/* Write unoptimized output to file 
		 * and optimize the output */
		if(isOptimized==false){
			outfile.write("\n"+doc_size+" documents are found");
			outfile.write("\n"+comparisons+" comparisons are made");
			outfile.write("\n"+((end_time-s_time)*0.003)+" seconds are used"); 	//Converting milliseconds to seconds
			System.out.println("Unoptimized Result for doc size :"+result_and.size()+" is: "+result_and);
			
			//Create String[] of queries arranged for optimization
			String[] optimized_quer=new String[queries.length];
			
			for(int i=0;i<queries.length;i++)
				optimized_quer[i]=String.valueOf(queries[i]);
			
			
			//Sort the query list in ascending order of Posting list size
			if(queries.length>=2 && isResultValid){
				for(int i=0;i<optimized_quer.length;i++)
					for(int j=0;j<optimized_quer.length-1;j++)
						if(taat.get(optimized_quer[j]).size()>taat.get(optimized_quer[j+1]).size()){
							String temp=new String(optimized_quer[j]);
							optimized_quer[j]=optimized_quer[j+1];
							optimized_quer[j+1]=temp;										
						}
			}
			
			for(int i=0;i<optimized_quer.length;i++)
				System.out.println(optimized_quer[i]);
			
			System.out.println("Comparisons before optimization: "+comparisons);
			//Process optimized list
			TAAT_OR(optimized_quer, true);			
		}else{
			outfile.write("\n"+comparisons+" comparisons are made with optimization");
			
			/* Parse the results according to specified output format */			
			String result=result_and.toString(); 						//Get String representation of list
			if(result.length()>2){										//Check if list is non-empty
				result=result.substring(1,result.length()-1);			//Strip out the terminal square brackets 
			}else{
				result="";
			}
			
			outfile.write("\n"+"Result: "+result);
			System.out.println("Optimized Result is : "+result_and);
		}
	}
	
	/** Internal method to perform TAAT OR on two Array Lists
	 * @param new_data Enter new array here
	 * @param answer_data Reference to answer Array */	
	private boolean rec_OR(LinkedList<String> new_data,LinkedList<String> answer_data){
		
		boolean match=false;
		
		//Check if entered postings list is empty
		if(new_data==null){
			// result_and.add("term not found");
			return false;
		}else{
		/* Compare each element of the data list to each element of the answer list */
			for(int i=0;i<new_data.size();i++){
					match=false;
					//Store current element of answer list in an integer variable			
					int ext=Integer.parseInt(new_data.get(i));
					
					//Compare element of data list to each element in answer list
					for(int j=0;j<answer_data.size();j++){
						//Increment comparisons counter
						comparisons++;
						int dat=Integer.parseInt(answer_data.get(j));
						//Check if data element matches the one in the answer list 
						if(ext==dat){
							match=true;			//report match
							break;				//exit 'j' loop
						}					
					}
					if(match==false)//add new element to the answer list
						answer_data.add(new_data.get(i));
			}
		}
		//Since we have reached here means the method has executed successfully
		return true;
	}
	
	/** Performs 'AND' retrieval between a set of queries 
	 * using Document At A Time approach 
	 * @param queries A String array containing the query terms
	 * @return void
	 */
	public void DAAT_AND(String[] queries){
		
		//Record start time		
		long s_time=System.currentTimeMillis();
		long end_time; 								//to store the time when the process terminates
		
		//Set comparison counter to zero
		comparisons=0;
		
		//Store length of query set
		int n=queries.length;
		
		//Create integer to display document count
		int doc_count=0;
		
			
		//Create ArrayList of postings lists
		ArrayList<LinkedList<String>> postings=new ArrayList<LinkedList<String>>();
		
		//Create ArrayList of list iterators
		ArrayList<ListIterator<String>> iter=new ArrayList<ListIterator<String>>();
		
		//Initialize boolean varibles
		boolean isNull=false;			//Assuming that we have no null postings lists
		boolean loop=true;				//To start the AND loop
				
	
		//Create answer field
				LinkedList<String> answers=new LinkedList<String>();
		
		//Populate the postings lists
		for(int i=0;i<n;i++)
			if(daat.get(queries[i])==null){
				isNull=true;
				break;
			}else{
				postings.add(daat.get(queries[i]));
			}
		
		if(!isNull){
		//Assign List Iterators to the respective postings list
		for(int i=0;i<n;i++)
			iter.add(postings.get(i).listIterator());
				
		//Create workspace
		String[] working=new String[25000];
		
		
		//Check if any of the postings lists are null
		
		//Initialize workspace with the first element of all the postings list
		for(int i=0;i<n;i++)
			if(iter.get(i).hasNext()){
				working[i]=iter.get(i).next();
			}else{
				loop=false;
			}
		
		while(loop==true){
			boolean isSame=true;
			String temp=working[0];
			int temp_idx=0;
			
			//Check if all values are the same
			for(int i=1;i<n;i++){
				comparisons++;
				if(Integer.parseInt(working[i])!=Integer.parseInt(temp)){
					isSame=false;
					break;
				}}
			
			if(isSame==true){//if same, add to answers list and increment pointer of all the postings lists
				answers.add(temp);
				
				for(int i=0;i<n;i++){
				 if(iter.get(i).hasNext()){
					 working[i]=iter.get(i).next();
				 }else{
					 loop=false;
				 }
				}					
			}else{
			    //Calculate minimum value and its index
				for(int i=1;i<n;i++){
					comparisons++;
					if(Integer.parseInt(working[i])<Integer.parseInt(temp)){
						temp=working[i];
						temp_idx=i;
						}
					}
				if(iter.get(temp_idx).hasNext()){
				working[temp_idx]=iter.get(temp_idx).next();
				}else{
					loop=false;
				}				
			}
			
			
		}
		doc_count=answers.size();
		}else{
		answers.clear();
		answers.add("term not found");
		doc_count=0;
		}
		end_time=System.currentTimeMillis();
		
		outfile.write("\n"+doc_count+" documents are found");
		outfile.write("\n"+comparisons+" comparisions are made");
		outfile.write("\n"+((end_time-s_time)*0.003)+" seconds are used"); 	//Converting milliseconds to seconds
		
		//Parse results as per the required output format
		String result=answers.toString();
		if(!result.equals("term not found"))
			result=result.substring(1,result.length()-1);
		
		outfile.write("\nResult: "+result);	
		
	}
	
	/** Performs 'OR' retrieval between a set of queries 
	 * using Document At A Time approach 
	 * @param queries A String array containing the query terms
	 * @return void
	 */
	public void DAAT_OR(String[] queries){
		
		int n=queries.length;
		
		//Create ArrayList of postings' list corresponding to the query terms 
		ArrayList<LinkedList<String>> postings=new ArrayList<LinkedList<String>>();
		
		//Create Hashtable to store answers
		Hashtable<String,Integer> answers=new Hashtable<String,Integer>();
		
		//Create working array
		String[] working=new String[25000];
		
		//Count 'null' instances
		int null_count=0;
		
		//Store index of null term
		int null_index=0;
		
		//Set comparision counter
		comparisons=0;
		
		//Create ArrayList of iterators corresponding to the above postings lists
		ArrayList<ListIterator<String>> iter=new ArrayList<ListIterator<String>>();
		
		//Populate the postings lists
		for(int i=0;i<n;i++)
			if(daat.get(queries[i])==null){
				postings.add(i, new LinkedList<String>());
				null_count++;
			}else{
				postings.add(i,daat.get(queries[i]));
				null_index=i;
			}
		
		//Attach the iterators to the postings lists
		for(int i=0;i<n;i++)
			iter.add(postings.get(i).listIterator());
		
		//Initialize the workspace
		for(int i=0;i<n;i++)
			if(iter.get(i).hasNext()){
				working[i]=iter.get(i).next();
			}else{
				working[i]=null;
			}		
		
		if(null_count>(n-1)){
			answers.put("term not found",0);			
		}else if(null_count==(n-1)){
			while(iter.get(null_index).hasNext()){
				answers.put(iter.get(null_index).next(),0);				
			}
		}else{
			boolean loop=true; //Set to false when loop's stop condition is activated
			boolean isSame=true; //Lets start by assuming that all the values in the workspace are the same
			
				
			while(loop==true){
				
				null_count=0;			//reset null pointer counter 
				null_index=-1;			//reset null value index
				//Calculate first non null index in workspace
				int first_non_null_index=0;
				
				while(working[first_non_null_index]==null){
					++first_non_null_index;
				}
				
				//Check if all entries are the same
				for(int i=first_non_null_index;i<n;i++){
					comparisons++;
					if((working[i]!=null)){
						if(!working[i].equals(working[first_non_null_index])){
							isSame=false;
							break;
						}
					}
				}
				
				if(isSame){
					//Add value to the hashtable
					if(answers.get(working[first_non_null_index])==null)
						answers.put(working[first_non_null_index], 0);
					
					for(int i=0;i<n;i++){
						if(iter.get(i).hasNext()){
							working[i]=iter.get(i).next();
						}else{
							null_count++;
							null_index=i;
						}
					}
					if(null_count>=(n-1)){
						loop=false;
					}
				}else{
					int min=Integer.parseInt(working[first_non_null_index]);
					int min_index=first_non_null_index;
					
					for(int i=first_non_null_index;i<n;i++)
						if(Integer.parseInt(working[i])<min && working[i]!=null){
							min=Integer.parseInt(working[i]);
							min_index=i;
						}
					
					//Add minimum value to answers hashtable if it isn't already there
					if(answers.get(working[min_index])==null){
						answers.put(working[min_index], 0);
					}
					
					//Update min_index field in workspace
					if(iter.get(min_index).hasNext()){
						working[min_index]=iter.get(min_index).next();
					}
					
					}		
				
				}
			
			//Count number of 'null' entries in table
			null_count=0;
			for(int i=0;i<n;i++)
				if(working[i]==null)
					null_count++;
					
			//Exit condition for while loop
			if(null_count>=(n-1))
				loop=false;
				
			}
			
			if(null_count==(n-1)){
				for(int i=0;i<n;i++){
					if(working[i]!=null){
						null_index=i;
						break;
					}
				}
			while(iter.get(null_index).hasNext()){
				answers.put(iter.get(null_index).next(), 0);
			}			
			
		}
		
		System.out.println("Result: "+answers.keys().toString());
		
	}
	
	/** Nested class to store token and its corresponding posting list size 
	 * @author Alizishaan Khatri */
	private class tok_freq implements Comparator<tok_freq>,Comparable<tok_freq>{
		private String tok;
		private int freq;
		
		tok_freq(String tok1, int freq1){
			this.tok=tok1;
			this.freq=freq1;
		}

		/** Method to describe how to compare two tok_freq objects 
		 * @param o1 <i>tok_freq</i> First object
		 * @param o2 <i>tok_freq</i> Second object
		 * @return Difference between the 'freq' fields of objects 1 & 2 */
		@Override		
		public int compare(tok_freq o1, tok_freq o2) {
			return(o1.freq-o2.freq);			
		}
		
		/** Method to determine if sorting is in ascending or descending order. 
		 * Change sign of return value in function definition to sort in Ascending order 
		 * @param o str_tok Object for comparison */
		@Override
		public int compareTo(tok_freq o) {
			return(o.freq-this.freq);
		}
	}
	
	/** Writes data to file open in class
	 * @param S Enter the String to be written to file 
	 * @return <i>true</i> if data has been written successfully to file.<br> <i>false</i> in all other cases */
	public boolean writeFile(String S){
		
		try{
			outfile.write(S);
			return true;
		}catch(Exception e){
			return false;
		}
	}
}
