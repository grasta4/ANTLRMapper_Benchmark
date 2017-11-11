package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Stream;

public class Benchmark {
	private static final void makeSolutions(final String solver, final String problem) throws InterruptedException, IOException {
		assert (solver.equals("clingo") || solver.equals("dlv") || solver.equals("dlv2")) && (problem.equals("hanoi_tower") || problem.equals("labyrinth") || problem.equals("myTests") || problem.equals("solitaire")); 
	
		final LinkedList<String> instances = new LinkedList <> ();
		
		try(Stream<Path> paths = Files.walk(Paths.get("rsc/problems/asp/" + problem + "/instances"))) {
		    paths.filter(Files::isRegularFile).forEach(element -> instances.add(element.getFileName().toString()));
		}
		
		for(final String instance : instances) {
			final LinkedList<String> lines = new LinkedList <> ();
			final Process process = new ProcessBuilder("rsc/" + solver, "rsc/problems/asp/" + problem + "/instances/" + instance, "rsc/problems/asp/" + problem + "/encoding.asp").start();
			
			System.out.print("Making output of '" + solver + "' with instance '" + instance + "' of '" + problem + "' -> ");
			process.waitFor();

			try(final BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
		
				while((line = processOutput.readLine()) != null)
					lines.add(line);
			}
		
			final File file = new File("rsc/problems/asp/" + problem + "/dlvOutputs/" + instance.charAt(0));
		
			file.getParentFile().mkdir();
		
			try(final PrintWriter writer = new PrintWriter(file)) {
				lines.forEach(element -> writer.println(element));
			}
			
			System.out.println("DONE.");
		}
	}
	
	public static void main(String[] args) {
		try {
			makeSolutions("dlv", "myTests");
		} catch (final InterruptedException | IOException exception) {
			exception.printStackTrace();
		}
	}
}