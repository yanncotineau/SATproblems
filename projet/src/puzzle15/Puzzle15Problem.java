package puzzle15;

import booleans.And;
import booleans.BooleanFormula;
import booleans.Equivalence;
import booleans.Implies;
import booleans.Not;
import booleans.Or;
import booleans.PropositionalVariable;
import core.Problem;
import core.Solution;
import core.Solver;

public class Puzzle15Problem extends Problem {

	private final int size = 4, N = 16, maxMoves = 80;
	private int[][] initialBoard;
	private int M;
	private PropositionalVariable[][][][] vars;
	
	public Puzzle15Problem(int[][] _initialBoard) {
		this.initialBoard = _initialBoard;
		this.vars = new PropositionalVariable[size][size][N][maxMoves + 1];
		this.M = 5;
	}
	
	// Méthodes intermédiaires
	public BooleanFormula doesNotChange(int i, int j, int t) {
		Equivalence[] eqs = new Equivalence[N];
		for (int n = 0 ; n < N; n++) {
			eqs[n] = new Equivalence(vars[i][j][n][t], vars[i][j][n][t + 1]);
		}
		return new And(eqs);
	}
	
	public int[][] adj(int i, int j) {
	    int[][] adjacentSquares = new int[4][2];
	    int count = 0;

	    // Check if adjacent squares are within the valid range and add them to the result
	    if (i - 1 >= 0) {
	        adjacentSquares[count] = new int[]{i - 1, j};
	        count++;
	    }
	    if (i + 1 <= 3) {
	        adjacentSquares[count] = new int[]{i + 1, j};
	        count++;
	    }
	    if (j - 1 >= 0) {
	        adjacentSquares[count] = new int[]{i, j - 1};
	        count++;
	    }
	    if (j + 1 <= 3) {
	        adjacentSquares[count] = new int[]{i, j + 1};
	        count++;
	    }

	    // Create a new array with the exact number of adjacent squares found
	    int[][] result = new int[count][2];
	    System.arraycopy(adjacentSquares, 0, result, 0, count);

	    return result;
	}
	
	public BooleanFormula notEqualOrAdjacentToBlank(int i, int j, int t) {
		int[][] adjacentSquares = adj(i,j);
		int k, l, index = 0;
		Not[] nots = new Not[adjacentSquares.length];
		for (int[] squareCoordinates : adjacentSquares) {
			k = squareCoordinates[0];
			l = squareCoordinates[1];
			nots[index++] = new Not(vars[k][l][N - 1][t]);
		}
		And allNots = new And(nots);
		return new And(allNots, new Not(vars[i][j][N - 1][t]));
	}
	
	public BooleanFormula oneTileMoved(int i, int j, int k, int l, int t) {
		Equivalence[] part1Eqs = new Equivalence[N];
		int x, y;
		for (int n = 0 ; n < N; n++)
			part1Eqs[n] = new Equivalence(
					vars[i][j][n][t],
					vars[k][l][n][t + 1]);
		And part1 = new And(part1Eqs);
		
		int[][] adjacentSquares = adj(i,j);
		
		int index = 0;
		BooleanFormula[] part2NoChanges = new BooleanFormula[adjacentSquares.length - 1];
		for (int[] squareCoordinates : adjacentSquares) {
			x = squareCoordinates[0];
			y = squareCoordinates[1];
			if (x != k && y != l) {
				part2NoChanges[index++] = doesNotChange(x, y, t);
			}
		}
		And part2 = new And(part2NoChanges);
		
		return new And(part1, part2);
	}
	
	public BooleanFormula boardSolved(int t) {
		PropositionalVariable[] temp = new PropositionalVariable[size * size];
		int index = 0;
		for (int i = 0 ; i < size ; i++)
			for (int j = 0 ; j < size ; j++)
				temp[index++] = vars[i][j][4*i + j][t];
		return new And(temp);
	}



	@Override
	public int[][] encode() {
		System.out.println("Encoding 15-puzzle...");
		PropositionalVariable[] temp;
		
		for (int i = 0 ; i < size ; i++) {
			for (int j = 0 ; j < size ; j++) {
				for (int n = 0 ; n < N; n++) {
					for (int t = 0 ; t <= maxMoves ; t++) {
						String variableName = "x" + (i) + "_" + (j) + "_" + (n) + "_" + (t);
						vars[i][j][n][t] = new PropositionalVariable(variableName);
					}
				}
			}
		}
		
		// Condition 1 : Deux nombres ne peuvent être sur la même case en même temps.
		Implies[] condition1Implies = new Implies[N * (N-1) * (maxMoves + 1) * size * size];
		int index = 0;
		for (int n = 0 ; n < N; n++)
			for (int m = 0 ; m < N; m++)
				if (m != n)
					for (int i = 0 ; i < size ; i++)
						for (int j = 0 ; j < size ; j++)
							for (int t = 0 ; t <= maxMoves ; t++)
								condition1Implies[index++] = new Implies(
										vars[i][j][n][t],
										new Not(vars[i][j][m][t]));
		
		And condition1 = new And(condition1Implies);
		
		// Condition 2 : A chaque instant, chaque nombre doit être quelque part
		Or[] condition2Ors = new Or[(maxMoves + 1) * N];
		
		index = 0;
		for (int n = 0 ; n < N; n++)
			for (int t = 0 ; t <= maxMoves ; t++) {
				temp = new PropositionalVariable[size * size];
				for (int i = 0 ; i < size ; i++)
					for (int j = 0 ; j < size ; j++)
						temp[size * i + j] = vars[i][j][n][t];
				condition2Ors[index++] = new Or(temp);
			}
		And condition2 = new And(condition2Ors);
		
		// Condition 3 : Lorsqu'une case ne change pas entre t et t+1
		Implies[] condition3Implies = new Implies[size * size * (maxMoves + 1)];
		index = 0;
		
		for (int i = 0 ; i < size ; i++)
			for (int j = 0 ; j < size ; j++)
				for (int t = 0 ; t < maxMoves ; t++)
					condition3Implies[index++] = new Implies(
							notEqualOrAdjacentToBlank(i, j, t),
							doesNotChange(i, j, t));
		And condition3 = new And(condition3Implies);
		
		// Condition 4 : Une case change entre t et t+1
		Implies[] condition4Implies = new Implies[size * size * (maxMoves + 1)];
		index = 0;
		int k, l;
		for (int i = 0 ; i < size ; i++)
			for (int j = 0 ; j < size ; j++)
				for (int t = 0 ; t < maxMoves ; t++) {
					BooleanFormula leftSide = vars[i][j][N - 1][t];
					
					int[][] adjacentSquares = adj(i,j);
					BooleanFormula[] bools = new BooleanFormula[adjacentSquares.length];
					int boolIndex = 0;
					for (int[] squareCoordinates : adjacentSquares) {
						k = squareCoordinates[0];
						l = squareCoordinates[1];
						bools[boolIndex++] = oneTileMoved(i, j, k, l, t);
					}
					Or rightSide = new Or(bools);
					condition4Implies[index++] = new Implies(
							leftSide, rightSide);
				}
		And condition4 = new And(condition4Implies);
		
		// Condition 5 : contrainte de résolution
		BooleanFormula[] condition5Bools = new BooleanFormula[5];
		index = 0;
		for (int t = this.M - 4 ; t<= this.M ; t++) {
			condition5Bools[index++] = boardSolved(t);
		}
		Or condition5 = new Or(condition5Bools);
		
		// Condition 6 : grille initiale
		BooleanFormula[] condition6Bools = new BooleanFormula[size * size];
		index = 0;
		for (int i = 0 ; i < size ; i++)
			for (int j = 0 ; j < size ; j++)
				condition6Bools[index++] = vars[i][j][initialBoard[i][j] - 1][0];
		And condition6 = new And(condition6Bools);
		//System.out.println(condition1);
		//System.out.println(condition2);
		System.out.println(condition3);
		//System.out.println(condition4);
		//System.out.println(condition5);
		//System.out.println(condition6);
		And model = new And(condition1, condition2, condition3, condition4, condition5, condition6);
		
		BooleanFormula cnf = BooleanFormula.toCnf(model);
		
		return cnf.getClauses();
	}

	@Override
	public Solution process() {
		
		while (this.M <= this.maxMoves) {
			int[][] encodedInput = encode();
			int[] encodedSolution = solve(encodedInput);
			if (encodedSolution != null)
				return decode(encodedSolution);
			System.out.println("No solution found for up to " + (M) + " moves.");
			this.M += 5;
		}
		return null;		
	}
	
	@Override
	public int[] solve(int[][] _encodedInput) {
		System.out.println("Solving 15-puzzle...");
		return Solver.solve(_encodedInput, size * size * N * (maxMoves + 1));
		
	}

	@Override
	public Solution decode(int[] _encodedSolution) {
		System.out.println("Decoding 15-puzzle...");
		for (int x : _encodedSolution) {
			System.out.println(x);
		}
		return null;
	}
	
	
	
}
