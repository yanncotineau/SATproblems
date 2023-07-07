package queens;

import booleans.And;
import booleans.BooleanFormula;
import booleans.Implies;
import booleans.Not;
import booleans.Or;
import booleans.PropositionalVariable;
import core.Problem;
import core.Solver;

public class QueensProblem extends Problem {

	private int size;
	private int[][] boardMask;
	
	public QueensProblem(int _size) {
		this.size = _size;
	}
	
	public QueensProblem(int _size, int[][] _boardMask) {
		this(_size);
		this.boardMask = _boardMask;
	}
	
	@Override
	public int[][] encode() {
		System.out.println("Encoding Queens...");
		PropositionalVariable[] temp = null;
		int index;
		
		// Création des variables propositionnelles
		PropositionalVariable[][] vars = new PropositionalVariable[size][size];
		for (int row = 0 ; row < size ; row++)
			for (int col = 0 ; col < size ; col++) {
				String variableName = "x" + (row+1) + "_" + (col+1);
				vars[row][col] = new PropositionalVariable(variableName);
			}
		
		// Condition 1 : une dame minimum par ligne
		Or[] atLeastOnePerRowOrs = new Or[size];
		for (int row = 0 ; row < size ; row++)
			atLeastOnePerRowOrs[row] = new Or(vars[row]);
		And condition1 = new And(atLeastOnePerRowOrs);
		
		// Condition 2 : une dame minimum par colonne
		Or[] atLeastOnePerColOrs = new Or[size];
		index = 0;
		for (int col = 0 ; col < size ; col++) {
			temp = new PropositionalVariable[size];
			for (int row = 0 ; row < size ; row++)
				temp[row] = vars[row][col];
			atLeastOnePerColOrs[index++] = new Or(temp);
		}
		And condition2 = new And(atLeastOnePerColOrs);

		// Condition 3 : une dame ne doit en "voir" aucune autre
		Implies[] noOtherQueensImplies = new Implies[size * size];
		PropositionalVariable[] attackedSquares;
		for (int col = 0 ; col < size ; col++) {
			for (int row = 0 ; row < size ; row++) {
				// temp stocke les variables correspondant aux cases attaquées
				// si l'on plaçait une dame en position (row, col)
				temp = new PropositionalVariable[4*size];		
				index = 0;
				
				// Lignes
				for (int i = 0 ; i < size ; i++) {
					if (i != col)
						temp[index++] = vars[row][i];
				}
				
				// Colonnes
				for (int j = 0 ; j < size ; j++) {
					if (j != row)
						temp[index++] = vars[j][col];
				}
				
				// Diagonales
				int targetRow, targetCol;
				for (int shift = -(size - 1) ; shift < (size) ; shift++) {
					if (shift == 0) continue; 
					// descendante
					 targetRow = row + shift;
					 targetCol = col + shift;
					 if (targetRow >=0 && targetRow < size
							 && targetCol >= 0 && targetCol < size) {
						 temp[index++] = vars[targetRow][targetCol];
					 }
					 
					 // montante
					 targetRow = row - shift;
					 //targetCol = col + shift;
					 if (targetRow >=0 && targetRow < size
							 && targetCol >= 0 && targetCol < size) {
						 temp[index++] = vars[targetRow][targetCol];
					 }
				}
				
				attackedSquares = new PropositionalVariable[index];
				for (int i = 0; i < index; i++)
					attackedSquares[i] = temp[i];
				
				Or anyAttackedSquare = new Or(attackedSquares);
				
				noOtherQueensImplies[row*size + col] = new Implies(
						vars[row][col], new Not(anyAttackedSquare));
			}
		}
		And condition3 = new And(noOtherQueensImplies);
		
		// Condition 4 : respect du boardMask
		And condition4 = new And();
		if (boardMask != null) {
			for (int row = 0 ; row < size ; row++) {
				for (int col = 0 ; col < size ; col++) {
					if (boardMask[row][col] == 1)
						condition4 = new And(condition4, vars[row][col]);
					else if (boardMask[row][col] == -1)
						condition4 = new And(condition4, new Not(vars[row][col]));
				}
			}
		}
		
		
		
		And model = new And(condition1, condition2, condition3, condition4);
		BooleanFormula cnf = BooleanFormula.toCnf(model);
		return cnf.getClauses();
	}
	
	@Override
	public int[] solve(int[][] _encodedInput) {
		System.out.println("Solving Queens...");
		return Solver.solve(_encodedInput, size*size);
	}
	
	@Override
	public QueensSolution decode(int[] _encodedSolution) {
		System.out.println("Decoding Queens...");
		
		if (_encodedSolution == null) return null;
		
		int[][] board = new int[size][size];
		
		for (int i = 0 ; i < _encodedSolution.length ; i++)
			board[i / size][i % size] = _encodedSolution[i] > 0 ? 1 : 0;
		
		return new QueensSolution(board);
	}
}
