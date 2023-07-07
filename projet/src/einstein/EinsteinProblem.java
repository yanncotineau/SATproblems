package einstein;

import java.util.ArrayList;
import java.util.Arrays;

import booleans.And;
import booleans.BooleanFormula;
import booleans.Implies;
import booleans.Not;
import booleans.Or;
import booleans.PropositionalVariable;
import core.Problem;
import core.Solution;
import core.Solver;

public class EinsteinProblem extends Problem {

	private final int size = 5; 
	
	private ArrayList<String> colors = new ArrayList<>(Arrays.asList("yellow", "blue", "red", "green", "white"));
	private ArrayList<String> countries = new ArrayList<>(Arrays.asList("norwegian", "danish", "english", "german", "swedish"));
	private ArrayList<String> drinks = new ArrayList<>(Arrays.asList("water", "tea", "milk", "coffee", "beer"));
	private ArrayList<String> cigars = new ArrayList<>(Arrays.asList("dunhill", "blend", "pall mall", "prince", "bluemaster"));
	private ArrayList<String> animals = new ArrayList<>(Arrays.asList("cats", "horses", "birds", "fishes", "dogs"));
	
	//private ArrayList<ArrayList<String>> attributes = new ArrayList<>(Arrays.asList(colors, countries, drinks, cigars, animals));
	

	
	@Override
	public int[][] encode() {
		System.out.println("Encoding Einstein...");
		PropositionalVariable[] temp = null;
		int index;
		
		// Création variables
		PropositionalVariable[][][] vars = new PropositionalVariable[size][size][size];
		
		for (int houseNumber = 0 ; houseNumber < size ; houseNumber++) {
			for (int attribute = 0 ; attribute < size ; attribute++) {
				for (int attIndex = 0 ; attIndex < size ; attIndex++) {
					String variableName = "x" + (houseNumber) + "_" + (attribute) + "_" + (attIndex);
					vars[houseNumber][attribute][attIndex] = new PropositionalVariable(variableName);
				}				
			}
		}
		
		// Condition 1 : Chaque attribut doit apparaitre dans au moins 1 maison
		Or[] allAttrUsedOrs = new Or[size * size];
		index = 0;
		for (int attribute = 0 ; attribute < size ; attribute++) {
			for (int attIndex = 0 ; attIndex < size ; attIndex++) {
				temp = new PropositionalVariable[size];
				for (int houseNumber = 0 ; houseNumber < size ; houseNumber++) {
					temp[houseNumber] = vars[houseNumber][attribute][attIndex];
				}
				allAttrUsedOrs[index++] = new Or(temp);
			}				
		}
		And condition1 = new And(allAttrUsedOrs);
		System.out.println(condition1);
		
		// Condition 2 : Aucun attribut n'est partagé entre 2 maisons
		Implies[] noSharedAttrImplies = new Implies[25*20];
		index = 0;
		for (int attribute = 0 ; attribute < size ; attribute++) {
			for (int attIndex = 0 ; attIndex < size ; attIndex++) {
				for (int houseNumber1 = 0 ; houseNumber1 < size ; houseNumber1++) {
					for (int houseNumber2 = 0 ; houseNumber2 < size ; houseNumber2++) {
						if (houseNumber1 != houseNumber2) {
							noSharedAttrImplies[index++] = new Implies(
									vars[houseNumber1][attribute][attIndex],
									new Not(vars[houseNumber2][attribute][attIndex]));
						}
					}
				}
			}
		}
		And condition2 = new And(noSharedAttrImplies);
		System.out.println(condition2);
		
		// Condition 3 : Une maison ne peut avoir 2 valeurs ou plus pour un attribut donné
		Implies[] noTwoAttrPerHouseImplies = new Implies[5*25*4];
		index = 0;
		for (int houseNumber = 0 ; houseNumber < size ; houseNumber++) {
			for (int attribute = 0 ; attribute < size ; attribute++) {
				for (int attIndex1 = 0 ; attIndex1 < size ; attIndex1++) {
					for (int attIndex2 = 0 ; attIndex2 < size ; attIndex2++) {
						if (attIndex1 != attIndex2) {
							noTwoAttrPerHouseImplies[index++] = new Implies(
									vars[houseNumber][attribute][attIndex1],
									new Not(vars[houseNumber][attribute][attIndex2]));
									
						}
					}
				}
			}
		}
		And condition3 = new And(noTwoAttrPerHouseImplies);
		
		// Condition 4 : Toutes les règles de l'énigme sont respectées
		BooleanFormula[] rules = new BooleanFormula[15]; // il y a 15 règles à vérifier.  
		int rulesIndex = 0;
		
		// Règle 1 : Le Britannique vit dans la maison rouge.
		Implies[] rule1Implies = new Implies[size];
		for (int houseNumber = 0 ; houseNumber < size ; houseNumber++) {
			rule1Implies[houseNumber] = new Implies(
					vars[houseNumber][1][countries.indexOf("english")],
					vars[houseNumber][0][colors.indexOf("red")]);
		}
		rules[rulesIndex++] = new Or(rule1Implies);
		
		// Règle 2 : Le Suédois a des chiens.
		Implies[] rule2Implies = new Implies[size];
		for (int houseNumber = 0 ; houseNumber < size ; houseNumber++) {
			rule2Implies[houseNumber] = new Implies(
					vars[houseNumber][1][countries.indexOf("swedish")],
					vars[houseNumber][4][animals.indexOf("dogs")]);
		}
		rules[rulesIndex++] = new Or(rule2Implies);
		
		// Règle 3 : Le Danois boit du thé.
		Implies[] rule3Implies = new Implies[size];
		for (int houseNumber = 0 ; houseNumber < size ; houseNumber++) {
			rule3Implies[houseNumber] = new Implies(
					vars[houseNumber][1][countries.indexOf("danish")],
					vars[houseNumber][2][drinks.indexOf("tea")]);
		}
		rules[rulesIndex++] = new Or(rule3Implies);
		
		// Règle 4 : La maison verte est directement à gauche de la maison blanche.
		Implies[] rule4Implies = new Implies[size - 1];
		for (int houseNumber = 0 ; houseNumber < size - 1 ; houseNumber++) {
			rule4Implies[houseNumber] = new Implies(
					vars[houseNumber][0][colors.indexOf("green")],
					vars[houseNumber + 1][0][colors.indexOf("white")]);
		}
		rules[rulesIndex++] = new Or(rule4Implies);
		
		// Règle 5 : Le propriétaire de la maison verte boit du café.
		Implies[] rule5Implies = new Implies[size];
		for (int houseNumber = 0 ; houseNumber < size ; houseNumber++) {
			rule5Implies[houseNumber] = new Implies(
					vars[houseNumber][0][colors.indexOf("green")],
					vars[houseNumber][2][drinks.indexOf("coffee")]);
		}
		rules[rulesIndex++] = new Or(rule5Implies);
		
		// Règle 6 : La personne qui fume des Pall Mall élève des oiseaux.
		Implies[] rule6Implies = new Implies[size];
		for (int houseNumber = 0 ; houseNumber < size ; houseNumber++) {
			rule6Implies[houseNumber] = new Implies(
					vars[houseNumber][3][cigars.indexOf("pall mall")],
					vars[houseNumber][4][animals.indexOf("birds")]);
		}
		rules[rulesIndex++] = new Or(rule6Implies);
		
		// Règle 7 : Le propriétaire de la maison jaune fume des Dunhill.
		Implies[] rule7Implies = new Implies[size];
		for (int houseNumber = 0 ; houseNumber < size ; houseNumber++) {
			rule7Implies[houseNumber] = new Implies(
					vars[houseNumber][0][colors.indexOf("yellow")],
					vars[houseNumber][3][cigars.indexOf("dunhill")]);
		}
		rules[rulesIndex++] = new Or(rule7Implies);
		
		// Règle 8 : La personne qui vit dans la maison du centre boit du lait.
		rules[rulesIndex++] = vars[2][2][drinks.indexOf("milk")];
		
		// Règle 9 : Le Norvégien habite dans la première maison en partant de la gauche.
		rules[rulesIndex++] = vars[0][1][countries.indexOf("norwegian")];
		
		// Règle 10 : L'homme qui fume des Blend vit à côté de celui qui a des chats.
		Implies[] rule10Implies = new Implies[size];
		for (int houseNumber = 1 ; houseNumber < size - 1 ; houseNumber++) {
			rule10Implies[houseNumber] = new Implies(
					vars[houseNumber][3][cigars.indexOf("blend")],
						new Or(
							vars[houseNumber - 1][4][animals.indexOf("cats")],
							vars[houseNumber + 1][4][animals.indexOf("cats")]
						)
					);
			rule10Implies[0] = new Implies(
					vars[0][3][cigars.indexOf("blend")],
					vars[1][4][animals.indexOf("cats")]);
			rule10Implies[4] = new Implies(
					vars[4][3][cigars.indexOf("blend")],
					vars[3][4][animals.indexOf("cats")]);
		}
		rules[rulesIndex++] = new Or(rule10Implies);
		
		// Règle 11 : L'homme qui a un cheval est le voisin de celui qui fume des Dunhill.
		Implies[] rule11Implies = new Implies[size];
		for (int houseNumber = 1 ; houseNumber < size - 1 ; houseNumber++) {
			rule11Implies[houseNumber] = new Implies(
					vars[houseNumber][4][animals.indexOf("horses")],
						new Or(
							vars[houseNumber - 1][3][cigars.indexOf("dunhill")],
							vars[houseNumber + 1][3][cigars.indexOf("dunhill")]
						)
					);
			rule11Implies[0] = new Implies(
					vars[0][4][animals.indexOf("horses")],
					vars[1][3][cigars.indexOf("dunhill")]);
			rule11Implies[4] = new Implies(
					vars[4][4][animals.indexOf("horses")],
					vars[3][3][cigars.indexOf("dunhill")]);
		}
		rules[rulesIndex++] = new Or(rule11Implies);
		
		// Règle 12 : Celui qui fume des Bluemaster boit de la bière.
		Implies[] rule12Implies = new Implies[size];
		for (int houseNumber = 0 ; houseNumber < size ; houseNumber++) {
			rule12Implies[houseNumber] = new Implies(
					vars[houseNumber][3][cigars.indexOf("bluemaster")],
					vars[houseNumber][2][drinks.indexOf("beer")]);
		}
		rules[rulesIndex++] = new Or(rule12Implies);
		
		// Règle 13 : L'Allemand fume des Prince.
		Implies[] rule13Implies = new Implies[size];
		for (int houseNumber = 0 ; houseNumber < size ; houseNumber++) {
			rule13Implies[houseNumber] = new Implies(
					vars[houseNumber][1][countries.indexOf("german")],
					vars[houseNumber][3][cigars.indexOf("prince")]);
		}
		rules[rulesIndex++] = new Or(rule13Implies);
		
		// Règle 14 : Le Norvégien vit juste à côté de la maison bleue.
		Implies[] rule14Implies = new Implies[size];
		for (int houseNumber = 1 ; houseNumber < size - 1 ; houseNumber++) {
			rule14Implies[houseNumber] = new Implies(
					vars[houseNumber][1][countries.indexOf("norwegian")],
						new Or(
							vars[houseNumber - 1][0][colors.indexOf("blue")],
							vars[houseNumber + 1][0][colors.indexOf("blue")]
						)
					);
			rule14Implies[0] = new Implies(
					vars[0][1][countries.indexOf("norwegian")],
					vars[1][0][colors.indexOf("blue")]);
			rule14Implies[4] = new Implies(
					vars[4][1][countries.indexOf("norwegian")],
					vars[3][0][colors.indexOf("blue")]);
		}
		rules[rulesIndex++] = new Or(rule14Implies);
		
		// Règle 15 : L'homme qui fume des Blend a un voisin qui boit de l'eau.
		Implies[] rule15Implies = new Implies[size];
		for (int houseNumber = 1 ; houseNumber < size - 1 ; houseNumber++) {
			rule15Implies[houseNumber] = new Implies(
					vars[houseNumber][3][cigars.indexOf("blend")],
						new Or(
							vars[houseNumber - 1][2][drinks.indexOf("water")],
							vars[houseNumber + 1][2][drinks.indexOf("water")]
						)
					);
			rule15Implies[0] = new Implies(
					vars[0][3][cigars.indexOf("blend")],
					vars[1][2][drinks.indexOf("water")]);
			rule15Implies[4] = new Implies(
					vars[4][3][cigars.indexOf("blend")],
					vars[3][2][drinks.indexOf("water")]);
		}
		rules[rulesIndex++] = new Or(rule15Implies);
		
		And condition4 = new And(rules);
		//And model = new And(condition1, condition2, condition3);
		And model = new And(condition1, condition2, condition3, condition4);
		
		BooleanFormula cnf = BooleanFormula.toCnf(model);
		System.out.println(cnf);
		for (int[] row : cnf.getClauses()) {
			for (int x : row) {
				System.out.print(x + " ");
			}
			System.out.println("");
		}
		
		return cnf.getClauses();
		
	}

	@Override
	public int[] solve(int[][] _encodedInput) {
		System.out.println("Solving Einstein...");
		return Solver.solve(_encodedInput, size*size*size);
	}

	@Override
	public Solution decode(int[] _encodedSolution) {
		
		String[][] resultGrid = new String[][]{
			{"Maison", "1", "2", "3", "4", "5"},
            {"Couleur", "", "", "", "", ""},
            {"Pays", "", "", "", "", ""},
            {"Boisson", "", "", "", "", ""},
            {"Cigare", "", "", "", "", ""},
            {"Animaux", "", "", "", "", ""}};
         
        int targetRow = 1, targetCol = 1;
        for (int x : _encodedSolution) {
        	System.out.println(x);
        	if (x > 0) {
        		int houseNumber = (x-1) % size;
        		int attIndex = ((x-1) / size) % size;
        		int attribute = ((x-1) / (size*size)) % size;
        		switch (attribute) {
        			case 0:
        				resultGrid[attribute + 1][houseNumber + 1] = colors.get(attIndex);
        				break;
        			case 1:
        				resultGrid[attribute + 1][houseNumber + 1] = countries.get(attIndex);
        				break;
        			case 2:
        				resultGrid[attribute + 1][houseNumber + 1] = drinks.get(attIndex);
        				break;
        			case 3:
        				resultGrid[attribute + 1][houseNumber + 1] = cigars.get(attIndex);
        				break;
        			case 4:
        				resultGrid[attribute + 1][houseNumber + 1] = animals.get(attIndex);
        				break;
        			default:
        				resultGrid[targetRow][targetCol] = animals.get(attIndex);
        				break;
        		}
        		if (targetRow == size) {
        			targetRow = 1;
        			targetCol++;
        		} else {
        			targetRow++;
        		}
        	}
        }
            return new EinsteinSolution(resultGrid);
	}

}
