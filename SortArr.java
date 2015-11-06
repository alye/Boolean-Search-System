import java.util.*;

/** Class to sort Strings based on term frequency and document ID */
public class SortArr {
	
		
	/** Method to sort terms in ascending order of Document IDs 
	 * @param posting Enter the posting list entry
	 * @return A sorted LinkedList of Strings */
	public static LinkedList<String> sortAsc(String posting){
		
		/** Array to hold strings to be sorted */
		String[] data=posting.split(", ");				//Creating an array of input strings
		
		//Sorting data by using Bubble Sort
		for(int i=0;i<data.length;i++){
			for(int j=0;j<data.length-1;j++){
				String[] arr1=data[j].split("/");
				String[] arr2=data[j+1].split("/");
				
				if(Integer.parseInt(arr1[0])>Integer.parseInt(arr2[0])){
					String tmp=data[j+1];
					data[j+1]=data[j];
					data[j]=tmp;
				}
			}			
		}		
		/** Create a Linked list corresponding to given data */
		LinkedList<String> l=new LinkedList<String>();
		for(int i=0;i<data.length;i++){
			String[] t=data[i].split("/");
			l.add(t[0]);
		}
		return(l);		
	}
	
	/** Method to sort terms in descending order of term Frequencies 
	 * @param posting Enter the posting list entry
	 * @return A sorted LinkedList of Strings */	 
	public static LinkedList<String> sortDsc(String posting){
		
		/** Array to hold strings to be sorted */
		String[] data=posting.split(", ");				//Creating an array of input strings
		
		//Sorting data by using Bubble Sort
		for(int i=0;i<data.length;i++){
			for(int j=0;j<data.length-1;j++){
				String[] arr1=data[j].split("/");
				String[] arr2=data[j+1].split("/");
				
				if(Integer.parseInt(arr1[1])<Integer.parseInt(arr2[1])){
					String tmp=data[j+1];
					data[j+1]=data[j];
					data[j]=tmp;
				}
			}			
		}
		
		//Create a Linked list corresponding to given data
		LinkedList<String> l=new LinkedList<String>();
		for(int i=0;i<data.length;i++){
			String[] t=data[i].split("/");
			l.add(t[0]);
		}
			
		
		return(l);		
	}

}


