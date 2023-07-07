package core;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;

public class Solver {
	public static int[] solve(int[][] cnf, int numberOfVariables) {
		
		int numberOfClauses = cnf.length;
		
		ISolver solver = SolverFactory.newDefault();
		
		solver.newVar(numberOfVariables);
		solver.setExpectedNumberOfClauses(numberOfClauses);
		
		try {
			for (int i = 0; i < numberOfClauses; i++)
				solver.addClause(new VecInt(cnf[i]));
		
			if (solver.isSatisfiable()) 
				return solver.model(); // Satisfaisable
			else
			    return null; // Non satisfaisable
		
		} catch (Exception e) {
			e.printStackTrace();
			return null; // Erreur quelconque, non satisfaisable
		}
	}
}
