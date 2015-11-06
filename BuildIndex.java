import java.io.*;
import java.util.*;

/** To parse the given Postings list and build 
 * the index in memory. */
public class BuildIndex {
	
	private File f;					//Store index file path
	private Scanner s;				//Scanner instance to read from file
	
	/** Index with terms ordered by decreasing term frequencies */
	private Hashtable<String,LinkedList<String>> taat;
	
	/** Index with terms ordered by increasing document IDs */
	private Hashtable<String,LinkedList<String>> daat;	
	
	/** Constructor to initialize file location fields 
	 * @param path Enter path of index file */
	BuildIndex(String path){
		this.f=new File(path);
		try{
			s=new Scanner(f);
		}
		catch(FileNotFoundException e){
			System.out.println("Please check index file path");
			System.exit(1);
		}
	}
	
	/** Method to construct index in memory.
	 * @return <i>true</i> If both indices are constructed successfully <br> <i>false</i> In  all other cases */
	public boolean construct(){
		
		try{
			//Initialize the Hashtables
			taat=new Hashtable<String,LinkedList<String>>();
			daat=new Hashtable<String,LinkedList<String>>();
			
						
			//Repeat process as long as new lines are present in the file
			while(s.hasNextLine()){				
				String cur_line=s.nextLine(); 						//Read next line from the file
				int index=0;
				
				//Calculate index at which '\c' occurs
				while(!(cur_line.charAt(index)=='\\' && cur_line.charAt(index+1)=='c')){
					index++;					
				}
						
				String key=cur_line.substring(0,index);				//Store key value
								
				//Calculate the index at which '\m' occurs
				while(!(cur_line.charAt(index)=='\\' && cur_line.charAt(index+1)=='m')){
					index++;					
				}
				
				//Remove '\m[' from the start of the postings list and ']' from the end
				String posting=cur_line.substring(index+3,cur_line.length()-1);			
													
				//Sort the postings list as needed
				LinkedList<String> l1=SortArr.sortAsc(posting);
				LinkedList<String> l2=SortArr.sortDsc(posting);				
							
				//Add values to the Hashtable
				daat.put(key,l1);
				taat.put(key,l2);
				}
			
			s.close();
			return true;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/** Get current Hashtable (Term At A Time)
	 * @return Hashtable<String,LinkedList<String>>
	 * @param none */
	public Hashtable<String,LinkedList<String>> getTAAT(){
		return this.taat;
	}
	
	/** Get current Hashtable (Document At A Time)
	 * @return Hashtable<String,LinkedList<String>>
	 * @param none */
	public Hashtable<String,LinkedList<String>> getDAAT(){
		return this.daat;
	}
}
