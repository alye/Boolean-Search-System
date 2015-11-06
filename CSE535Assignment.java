import java.util.*;
import java.io.IOException;

/**Program to build an index from a given postings list using hash tables and linked lists.
 *  
 * Created and submitted in partial fulfillment of the requirements
 * of CSE 535: Information Retrieval
 * @author Alizishaan_Khatri 
 */
public class CSE535Assignment {

	public static void main(String[] ip) throws IOException {
		//Check if correct number of arguments are entered
		if(ip.length!=4){
			System.out.println("\n Correct usage is: \n java CSE535Assignment <term_file> <output_file> <k-value> <query_file>");
			System.exit(-1);
		}
		
		//Store command-line parameters to variables
		String term_file=ip[0];
		String output_file=ip[1];
		int k=Integer.parseInt(ip[2]);
		String query_file=ip[3];
		
		//Build the index in memory from file
		BuildIndex b=new BuildIndex(term_file);
		
		//Construct index and verify
		if(!b.construct()){
			System.out.println("Failed to construct index");
			System.exit(-1);
		}
		
		//Get references to the Hashtables built in memory
		Hashtable<String,LinkedList<String>> taat = b.getTAAT();		
		Hashtable<String,LinkedList<String>> daat = b.getDAAT();
		
		//Share Hashtable references with Functions class. Also, set up output file
		Functions f=new Functions(taat,daat,output_file);
		
		//Set up query handler
		ParseInput p=new ParseInput(query_file);
		
		//Call getTopK
		f.getTopK(k, taat);
		
		//Read query file
		while(p.hasQueries()){
			//String array containing the terms in each query
			String[] queries=p.getQueryTerms();
			
			//List to hold the TAAT postings list corresponding to each query
			List<LinkedList<String>> taat_post=new ArrayList<LinkedList<String>>();	
			
			//List to hold the DAAT postings list corresponding to each query
			List<LinkedList<String>> daat_post=new ArrayList<LinkedList<String>>();	
			
			//looping through the query terms
			for(int i=0;i<queries.length;i++){
				f.getPostings(queries[i]);					//Get Postings list for each query
				
				//Build an array list containing the taat Postings list for the given query line
				taat_post.add(taat.get(queries[i]));		
				
				//Build an array list containing the daat Postings list for the given query line
				daat_post.add(daat.get(queries[i]));
				}
			
			//Build function header and write to file (TAAT_AND and TAAT_OR)
			String query_line=new String();
			for(int i=0;i<queries.length;i++){
				query_line=query_line+queries[i];
				if(i!=(queries.length-1)){
					query_line=query_line+", ";
				}
			}
			
			f.writeFile("\nFUNCTION: termAtATimeQueryAnd "+query_line);
			f.TAAT_AND(queries,false);
			
			f.writeFile("\nFUNCTION: termAtATimeQueryOr "+query_line);
			f.TAAT_OR(queries, false);
			
			f.writeFile("\nFUNCTION: docAtATimeQueryAnd "+query_line);
			f.DAAT_AND(queries);
			
			f.writeFile("\nFUNCTION: docAtATimeQueryOr "+query_line);
			f.DAAT_OR(queries);
		
		}	
		
		f.close();	

	}

}
