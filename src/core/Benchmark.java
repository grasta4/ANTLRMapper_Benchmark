package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import it.unical.mat.embasp.specializations.dlv.DLVAnswerSets;

public class Benchmark {
	public static void main(String[] args) {
		/*
		 * This portion reads the output file of the solver and stores it in a String.
		 */
		String test = "";
		
		try(final BufferedReader bufferedReader = new BufferedReader(new FileReader("rsc/DLVTest0.txt"))) {
			String line;
			
			while((line = bufferedReader.readLine()) != null)
	            test += line + "\n";
	    } catch (final IOException exception) {
	        exception.printStackTrace();
	    }
		/*
		 * 
		 */
		
		/*
		 * This portion counts the values in the .csv in order to determine if a carriage return is required (there must be one every ten values)
		 */
		String[] values = null;
		
	    try(final BufferedReader bufferedReader = new BufferedReader(new FileReader("rsc/results.csv"))) {
	    	String line;
			
	    	while((line = bufferedReader.readLine()) != null)
	            values = line.split(";");
	    } catch (final IOException exception) {
	        exception.printStackTrace();
	    }
		/*
		 * 
		 */
	    
	    /*
	     * This portion does the actual testing.
	     */
	    final DLVAnswerSets dlvAnswerSets = new DLVAnswerSets(test, "");
		final long start = System.nanoTime();
		
		dlvAnswerSets.getAnswersets();
		
		final long end = System.nanoTime();
		/*
		 * 
		 */
		
		/*
		 * This portion stores the value in the .csv file. 
		 */
		final String carriageReturn = (values == null || values.length % 10 != 0) ? "" : "\n";
		
		try(final FileWriter fileWriter = new FileWriter("rsc/results.csv", true); final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter); final PrintWriter out = new PrintWriter(bufferedWriter)) {
	        out.print(carriageReturn + ((end - start) / 1000000) + ";");
		} catch (final IOException exception) {
			exception.printStackTrace();
		}
		/*
		 *
		 */
		
		dlvAnswerSets.getAnswersets().forEach(System.out::println);
	}
}