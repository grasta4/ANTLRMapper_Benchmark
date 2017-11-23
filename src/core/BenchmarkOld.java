package core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import it.unical.mat.embasp.languages.pddl.Action;
import it.unical.mat.embasp.languages.pddl.PDDLMapper;
import it.unical.mat.embasp.languages.asp.ASPMapper;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.specializations.clingo.ClingoAnswerSets;
import it.unical.mat.embasp.specializations.dlv.DLVAnswerSets;
import it.unical.mat.embasp.specializations.dlv2.DLV2AnswerSets;
import it.unical.mat.embasp.specializations.solver_planning_domains.SPDPlan;

public class BenchmarkOld {
	public static void main(String[] args) throws IOException {
		final HashMap <String, String[]> problems = new HashMap <> ();
		final HashMap <String, String[]> solvers = new HashMap <> ();
		final String [] paradigms = {"asp", "pddl"};
		
		problems.put(paradigms[0], new String [] {"3-col", "ancestor", "graph_colouring", "hanoi_tower", "keys", "labyrinth", "ladder", "maximal_clique_problem", "my_tests", "network", "stable_marriage", "visit_all"});
		problems.put(paradigms[1], new String [] {"blocksworld", "depots", "gripper", "logistics"});
		solvers.put(paradigms[0], new String [] {"clingo", "dlv", "dlv2"});
		solvers.put(paradigms[1], new String [] {"spd"});
		
		for(final String paradigm : paradigms)
			for(final String problem : problems.get(paradigm))
				for(final String solver : solvers.get(paradigm))
					try(Stream <Path> paths = Files.walk(Paths.get("rsc/problems/" + paradigm + "/" + problem + "/" + solver + "Outputs"), 1)) {
						final String dataFilePath = "rsc/data/" + problem + ".csv";
						
						new File(dataFilePath).createNewFile();
						Files.write(Paths.get(dataFilePath), ("OLD" + solver + ";\n").getBytes(), StandardOpenOption.APPEND);
						System.out.print("Doing: <" + paradigm + "/" + problem + "> with solver <" + solver + "> ");
						paths.filter(Files::isRegularFile).forEach(path -> {
							try {
								List<Action> pddlList = null;
								List<AnswerSet> aspList = null;
								long start = 0, end = 0;
								String pddlErrors = "";
								final String fileName = path.getFileName().toString(), file = String.join("\n", Files.readAllLines(path)) + "\n";
								
								Files.write(Paths.get(dataFilePath), (fileName + ";").getBytes(), StandardOpenOption.APPEND);
								System.out.print(fileName + ", ");
								
								for(int counter = 1; counter <= 10; counter++) {
									aspList = null;
									pddlList = null;
									pddlErrors = "";
									start = end = 0;
									
									switch(solver) {
										case "clingo": final ClingoAnswerSets clingoAnswerSets = new ClingoAnswerSets(file); start = System.nanoTime(); clingoAnswerSets.getAnswersets(); end = System.nanoTime(); aspList = clingoAnswerSets.getAnswersets(); break;
										case "dlv": final DLVAnswerSets dlvAnswerSets = new DLVAnswerSets(file); start = System.nanoTime(); dlvAnswerSets.getAnswersets(); end = System.nanoTime(); aspList = dlvAnswerSets.getAnswersets(); break;
										case "dlv2": final DLV2AnswerSets dlv2AnswerSets = new DLV2AnswerSets(file); start = System.nanoTime(); dlv2AnswerSets.getAnswersets(); end = System.nanoTime(); aspList = dlv2AnswerSets.getAnswersets(); break;
										case "spd": final SPDPlan plan = new SPDPlan(file, ""); start = System.nanoTime(); plan.getActions(); end = System.nanoTime(); pddlList = plan.getActions(); pddlErrors = plan.getErrors(); break;
									}
									
									Files.write(Paths.get(dataFilePath), ((double)(end - start) / 1000000000.0 + ";").getBytes(), StandardOpenOption.APPEND);
								}
								
								Files.write(Paths.get(dataFilePath), "\n".getBytes(), StandardOpenOption.APPEND);
								
								final String solutionFile = "rsc/problems/" + paradigm + "/" + problem + "/" + solver + "Outputs/regexSolutions/" + fileName.substring(0, 4) + ".sol";
								int asID = 0;
								
								new File(solutionFile).createNewFile();
								
								if(pddlList != null) {
									for(final Action action : pddlList)
										Files.write(Paths.get(solutionFile), (action.getName() + "\n").getBytes(), StandardOpenOption.APPEND);
									
									Files.write(Paths.get(solutionFile), (pddlErrors + "\n").getBytes(), StandardOpenOption.APPEND);
									
									final String mapperFile = "rsc/data/mappers/" + problem + ".csv";
									
									new File(mapperFile).createNewFile();
									Files.write(Paths.get(mapperFile), (fileName + ";").getBytes(), StandardOpenOption.APPEND);
									
									for(final Action action : pddlList)
										for(int counter = 1; counter <= 10; counter++) {
											start = end = 0;
											start = System.nanoTime();
											
											PDDLMapper.getInstance().getObject(action.getName());
											
											end = System.nanoTime();
											
											Files.write(Paths.get(mapperFile), ((double)(end - start) / 1000000000.0 + ";").getBytes(), StandardOpenOption.APPEND);
										}
									
									Files.write(Paths.get(mapperFile), "\n".getBytes(), StandardOpenOption.APPEND);
								} else if(aspList != null)
									for(final AnswerSet answerSet : aspList) {
										Files.write(Paths.get(solutionFile), (answerSet.getAnswerSet() + "    " + answerSet.getWeights() + "\n").getBytes(), StandardOpenOption.APPEND);
								
										if(solver.equals("clingo") && !problem.equals("maximal_clique_problem")) {
											final String mapperFile = "rsc/data/mappers/" + problem + ".csv";
											
											new File(mapperFile).createNewFile();
											Files.write(Paths.get(mapperFile), (fileName + ";" + asID++ + ";").getBytes(), StandardOpenOption.APPEND);
											
											for(int counter = 1; counter <= 10; counter++) {
												start = end = 0;
												start = System.nanoTime();
												
												for(final String atom : answerSet.getAnswerSet())
													ASPMapper.getInstance().getObject(atom);
												
												end = System.nanoTime();
												
												Files.write(Paths.get(mapperFile), ((double)(end - start) / 1000000000.0 + ";").getBytes(), StandardOpenOption.APPEND);
											}
											
											Files.write(Paths.get(mapperFile), "\n".getBytes(), StandardOpenOption.APPEND);
										}
									}
							} catch (final Exception exception) {
								exception.printStackTrace();
							}
						});
						System.out.println("-> DONE.");
					}
	}
}
