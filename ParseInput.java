import java.util.*;
import java.io.*;

/**Class to parse the query file.
 * Each line represents a unique query. 
 * Multiple terms are listed on one line
 * This class returns separated terms of each query. 
 * It also indicates End Of File*/
public class ParseInput {
	
	private File query_file;
	private Scanner s;
	private String[] queries;
	
	/**Constructor to load the query file into the object
	 * @param address Complete file path of the query file	 */
	ParseInput(String address){
		
		query_file=new File(address);
		try{
			s=new Scanner(query_file);
		}catch(FileNotFoundException e){
			System.out.println("Query file not found!");
			System.exit(1);
		}
	}	
	
	/**Function to return the terms of each query line
	 * @param none
	 * @return An array containing all the terms in the query line */
	public String[] getQueryTerms(){
		
		String query=s.nextLine();	//Read the next line from file
		queries=query.split(" "); 	//Split on whitespace to get the query terms
		return(queries);			//return the query terms to the calling method		
	}
	
	/**Checks if there are any unread query lines in the file.
	 * @param none
	 * @return True,if query lines are yet to be read. False, otherwise. */
	public boolean hasQueries(){
		
		if(s.hasNextLine()==true)
			return true;
		else{								//EOF condition reached
			s.close();						//Close File
			return false;
		}
	}

}
