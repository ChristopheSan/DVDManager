import java.io.*;
import java.util.*;

public class DVDCollection {

	// Data fields
	
	/** The current number of DVDs in the array */
	private int numdvds;
	
	/** The array to contain the DVDs */
	private DVD[] dvdArray;
	
	/** The name of the data file that contains dvd data */
	private String sourceName;
	
	/** Boolean flag to indicate whether the DVD collection was
	    modified since it was last saved. */
	private boolean modified;
	
	
	/**
	 *  Constructs an empty directory as an array
	 *  with an initial capacity of 7. When we try to
	 *  insert into a full array, we will double the size of
	 *  the array first.
	 */
	public DVDCollection() {
		numdvds = 0;
		dvdArray = new DVD[7];
	}
	
	public String toString() {
		// Return a string containing all the DVDs in the
		// order they are stored in the array along with
		// the values for numdvds and the length of the array.
		// See homework instructions for proper format.
		String retString = "";
		retString = retString + "numdvds = " + numdvds +"\n\n";
		retString = retString + "dvdArray.length = " + dvdArray.length +"\n\n";
		
		for (int i = 0; i < numdvds; i++) {
			retString = retString + "dvdArray[" + i + "] = "
					+ dvdArray[i].getTitle() + "/"
					+ dvdArray[i].getRating() + "/"
					+ dvdArray[i].getRunningTime() + "min"
					+ "\n\n";
		}
		return retString;
	}

	public void addOrModifyDVD(String title, String rating, String runningTime) {
		// NOTE: Be careful. Running time is a string here
		// since the user might enter non-digits when prompted.
		// If the array is full and a new DVD needs to be added,
		// double the size of the array first.
		if(!isValidRating(rating)) {
			System.out.println("Invalid Rating.");
			return;
		}
		
		try {
			int index = findDVD(title);
			
			if (index == -1) { 			// If the movie doesn't exist in the collection
				addDVD(title, rating, runningTime);
			}
			else if (index > -1) {
				modifyDVD(title, rating, runningTime, index);
			}
		} catch (Exception e) {
			System.out.println(e);
			this.loadError = true;
			return;
		}
		this.modified = true;
	}
	
	public void removeDVD(String title) {
		if (!isEmpty()) {
			int index = findDVD(title);
			if(index != -1) {
				shiftLeft(index); 			// this should delete the item at the index of the item to remove
				--numdvds;
			}
			else {System.out.println(title + " does not exist in this collection.");}
		}
	}
	
	public String getDVDsByRating(String rating) {
		if (!isValidRating(rating)){
			//System.out.println("Invalid Rating");
			return "Invalid Rating";
		}
		String retString = "";
		for (int i = 0; i <= numdvds-1; i++) {
			String movieRating = dvdArray[i].getRating();
			if (movieRating.compareTo(rating) == 0) {
				retString = retString + "dvdArray[" + i + "] = "
						+ dvdArray[i].getTitle() + "/"
						+ dvdArray[i].getRating() + "/"
						+ dvdArray[i].getRunningTime() + "min"
						+ "\n";	
			}
		}
		return retString;	
	}

	public int getTotalRunningTime() {
		if(isEmpty()) {
			return 0;
		}
		int sum = 0;
		for (int i = 0; i < numdvds; i++) {
			sum = sum + dvdArray[i].getRunningTime();
		}
		return sum;	// STUB: Remove this line.
	}

	public void loadData(String filename) {
		File file = new File(filename);
		this.sourceName = filename;
		
		try {
			Scanner readFile = new Scanner(file);
			while (readFile.hasNextLine()) {
				String name, rating, runTime = "";
				String dvd = readFile.nextLine();
				String delims = "[,]";
				
				if(dvd == "") { // blank line, go to the next line
					continue;
				}
				
				else {
					String[] dvdAttributes = dvd.split(delims);
					name = dvdAttributes[0];
					rating = dvdAttributes[1];
					runTime = dvdAttributes[2];
					//dvdArray[numdvds++] = new DVD(name, rating, Integer.parseInt(runTime));
					addOrModifyDVD(name, rating, runTime);
					this.modified = false;
					if(this.loadError) { // if an error occurred while loading the file, exit at point of error.
						this.loadError = false;
						return;
					}
				}
			}
			readFile.close();
		} catch (Exception e) {
			System.out.println(e);
		}	
	}
	
	public void save() {
		if(!this.modified) {
			System.out.println("The collection has not been modified. Exiting without save...");
			return;
		}
		System.out.println("The collection has been modified. Saving...");
		
		//File saveFile = new File(this.sourceName);
		try {
			//if(saveFile.createNewFile()) {
				FileWriter writer = new FileWriter(this.sourceName);
				for (int i = 0; i < numdvds-1; i++) {
					writer.write(dvdArray[i].toString()+"\n");
				}
				writer.write(dvdArray[numdvds-1].toString()); // no new line char for last item
				writer.close();
				this.modified = false;
			//}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// Additional private helper methods go here:
	private boolean isFull() {
		// If the last spot in the array is populated then return true
		return numdvds == dvdArray.length;
	}
	
	private boolean isEmpty() {
		return numdvds == 0;
	}
	
	// When the dvd array has reached max capacity then we need to resize it.
	// This will copy over the existing DVDs and double the current capacity
	private void resizeCollection() {
		// Resize
		DVD newArr[] = new DVD[2*numdvds];
		
		// Copy
		for (int i = 0; i < dvdArray.length; i++) {
			newArr[i] = dvdArray[i];
		}
		
		// Assign
		this.dvdArray = newArr;
	}
 
	// returns the index at which the given title is stored in the collection.
	// if not found, return -1.
	private int findDVD(String title) {
		if (!isEmpty()) {
			int first = 0;
			int last = numdvds - 1;
			int mid = first + (last - first) / 2;
			
			while (first <= last) {	
				String curr = dvdArray[mid].getTitle();
				if (curr.compareToIgnoreCase(title) == 0) {
					return mid;
				}
				else if (curr.compareToIgnoreCase(title) > 0) { // If current title is greater lexicographically, look at the left
					last = mid - 1;
				}
				else { // if the current title is less than our target, look right
					first = mid + 1;
				}
				mid = first + (last - first) / 2;
			}
		}
		return -1;
	}
	
	// For inserting
	private void shiftRight(int start) {
		for(int i = numdvds - 1; i >= start; i--) {
			dvdArray[i+1] = dvdArray[i];
		}
	}
	
	// For deleting
	private void shiftLeft(int start) {
		for(int i = start; i < numdvds-1; i++) {
			dvdArray[i] = dvdArray[i+1];
		}
	}
	
	// Helper method to add a DVD into the collection.
	// If the dvdArray is at max capacity, it will be resized.
	//
	private void addDVD(String title, String rating, String runTime) {
		if( isFull() ) {
			resizeCollection();
		}	
		try {
			DVD toInsert = new DVD(title, rating, Integer.parseInt(runTime));
		
			// Start Cases
			
			if(isEmpty()) { 
				dvdArray[0] = toInsert;
			}
			
			//check if we can insert at start or end
			else if(title.compareToIgnoreCase(dvdArray[0].getTitle()) < 0) { // given title is before the first item
				shiftRight(0);
				dvdArray[0] = toInsert;
			}
			else if (title.compareToIgnoreCase(dvdArray[numdvds-1].getTitle()) > 0) { // given title is after the last item
				dvdArray[numdvds] = toInsert;
			}
			else { // The dvd has to be inserted inbetween 2 indexes
				
				int first = 0;
				int last = numdvds - 1;
				int mid = first + (last - first) / 2;
				
				while (first <= last) {	
					String curr = dvdArray[mid].getTitle();
					if (curr.compareToIgnoreCase(title) > 0) { // If current title is greater lexicographically, look at the left
						last = mid - 1;
					}
					else { // if the current title is less than our target, look right
						first = mid + 1;
					}
					mid = first + (last - first) / 2;
				}
				if (dvdArray[first].getTitle().compareToIgnoreCase(title) > 0) {
					shiftRight(first);
					dvdArray[first] = toInsert;
				}
	//			else {
	//				shiftRight(first-1);
	//				dvdArray[mid-1] = toInsert;
	//			}
			}
		} catch (Exception e) {
			throw e;
		}
		numdvds++;
	}
	
	private void modifyDVD(String title, String rating, String runTime, int index) {
		try {
			dvdArray[index].setTitle(title);
			dvdArray[index].setRating(rating);
			dvdArray[index].setRunningTime(Integer.parseInt(runTime));
		} catch (Exception e) {
			throw e;
		}
		//this.modified = true; z
	}
	
	private boolean isValidRating(String rating) {
		String VALIDRATING[] = {"G", "PG", "PG-13", "R", "UR", "NR"}; // UNRATED AND NOT RATED ACCEPTABLE
		for (int i = 0; i < VALIDRATING.length; i++) {
			if(VALIDRATING[i].compareToIgnoreCase(rating) == 0) {
				return true;
			}
		}
		return false;
	}
	
	private boolean loadError;
}
