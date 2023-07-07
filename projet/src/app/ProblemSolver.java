package app;

import queens.QueensProblem;
import queens.QueensSolution;

public class ProblemSolver {
	
	public static void main(String[] args) {
		QueensProblem pb = new QueensProblem(10);
		QueensSolution s = (QueensSolution) pb.process();
		s.print();
	}
}
