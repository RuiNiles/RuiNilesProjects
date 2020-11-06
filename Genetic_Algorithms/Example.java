//This is my example Solution
import java.lang.Math;
import java.util.*;


class Example {
	public static void main(String[] args){
		//Do not delete/alter the next line
		long startT=System.currentTimeMillis();

		System.out.println("attempting to solve the first problem");
		double[] sol1 = new Solution01().solve();
		System.out.println("attempting to solve the second problem");
		boolean[] sol2 = new Solution02().solve();
		double[] tmp = Assess.getTest2(sol2);
		double fit = Assess.getTest1(sol1);
		System.out.println();
		System.out.printf("Solution 1 Final Fitness: %.30f" , fit);
		System.out.println();
		System.out.println("Solution 2 Final Fitness: " + tmp[1]);

		//Do not delete or alter the next line
		long endT= System.currentTimeMillis();
		System.out.println("Total execution time was: " +  ((endT - startT)/1000.0) + " seconds");

	}
}


class Solution01{

	private int solutionSize = 20;
	private int populationSize = 400;
	private double mutationRate = 0.3; //the probability of a picked solution being subject to mutation.
	private double mutationSize = 0.5; //multiplier applied to the change in value of a mutation.
	private double crossOverRate = 0.7; //the probability of a picked solution being subject to crossing over.
	private int crossOverPoints = 5;
	private int tournamentSize = 50;
	private double selectionProbability = 0; //probability of selecting via fitness Proportional Selection vs tournament Selection
	private int elitism = populationSize; // the n best solutions for selection
	private int cycles = 15000;

	public Solution01(){}

	public double[] newSampleSolution(){
		double[] sol1= new double[solutionSize];
		for (int x = 0; x < solutionSize; x++) sol1[x] = Math.random()*Math.round(5.12*(Math.random() - Math.random()));
		return sol1;
	}

	public ArrayList<double[]> initialPopulation(){
		ArrayList<double[]> newPopulation = new ArrayList<>();
		for (int x = 0; x < populationSize; x++) newPopulation.add(newSampleSolution());
		return newPopulation;
	}

	public ArrayList<Double> getFitnesses(ArrayList<double[]> population){
		ArrayList<Double> populationFitnesses = new ArrayList<>();
		for (double[] solution: population) populationFitnesses.add(Assess.getTest1(solution));
		return populationFitnesses;
	}

	public ArrayList<double[]> fitnessProportionalSelection(ArrayList<double[]> population, ArrayList<Double> fitnesses){
		double sumOfFitnesses = 0;
		double[] probabilityTable = new double[elitism];
		ArrayList<double[]> newPopulation = new ArrayList<>();
		int count = 1;

		for(double fitness: fitnesses) sumOfFitnesses += 1/fitness;

		probabilityTable[0] = 1/fitnesses.get(0);
		for (int x = 1; x < elitism; x++){
			int y = 0;
			while (y <= count){
				probabilityTable[x] += (1/fitnesses.get(y))/sumOfFitnesses;
				y++;
			}
			count ++;
		}

		for (int x = 0; x < populationSize; x++){
			double pickNum = Math.random();
			boolean picked = false;
			int y = 0;
			double[] temp;

			while (!picked){
				if(pickNum <= probabilityTable[y]){
					temp = population.get(y);
					temp = mutation(temp);
					temp = crossOver(temp, population);
					newPopulation.add(temp);
					picked = true;
				}
				y++;
			}
		}
		return newPopulation;
	}

	public ArrayList<double[]> tournamentSelection(ArrayList<double[]> population, ArrayList<Double> fitnesses){
		ArrayList<double[]> newPopulation = new ArrayList<>();
		int num;
		boolean picked;
		double[] temp;
		for (int x = 0; x < populationSize; x++){
			ArrayList<Integer> selections = new ArrayList<>();
			for (int y = 0; y < tournamentSize; y++){
				picked = false;
				while(!picked){
					num = (int) Math.round(Math.random() * (elitism - 1));
					if(!(selections.contains(num))){
						selections.add(num);
						picked = true;
					}
				}
			}
			int tournamentWinner = selections.get(0);
			int current;
			double currentFitness;
			for (int y = 0; y < tournamentSize; y++){
				current = selections.get(y);
				currentFitness = fitnesses.get(current);
				if(currentFitness < fitnesses.get(tournamentWinner)) tournamentWinner = current;
			}
			temp = population.get(tournamentWinner);
			temp = mutation(temp);
			temp = crossOver(temp, population);
			newPopulation.add(temp);
		}
		return newPopulation;
	}

	public double[] mutation(double[] sol){
		double mutationPick = Math.random();
		if(mutationPick <= mutationRate){
			int dial = (int)(Math.random() * (solutionSize));
			double addition = ((Math.random() * 2) - 1) * mutationSize;
			sol[dial] += addition;
			if(sol[dial] > 5) sol[dial] = 5;
			if(sol[dial] < -5) sol[dial] = -5;
		}
		return sol;
	}

	public double[] crossOver(double[] sol1, ArrayList<double[]> population){
		double[] solOut = sol1;
		double crossOverPick = Math.random();
		if(crossOverPick <= crossOverRate){
			double[] sol2 = population.get((int)Math.round(Math.random() * (populationSize - 1)));
			solOut = new double[solutionSize];
			double[] solCurrent = sol1;
			ArrayList<Integer> points = new ArrayList<>();
			int num = 0;
			boolean found;
			for (int x = 0; x < crossOverPoints; x++){
				found = false;
				while (!found){
					num = (int)Math.round(Math.random() * (solutionSize - 1));
					if(!points.contains(num)) found = true;
				}
				points.add(num);
			}
			for (int x = 0; x < solutionSize; x++){
				solOut[x] = solCurrent[x];
				if(points.contains(x)){
					if(Arrays.equals(sol1, solCurrent)) solCurrent = sol2;
					solCurrent = sol1;
				}
			}
		}
		return solOut;
	}

	public double bestFitness(ArrayList<double[]> currentPopulation){
		double best = Assess.getTest1(currentPopulation.get(0));
		double current;
		for(double[] solution: currentPopulation){
			current = Assess.getTest1(solution);
			if(current < best) best = current;
		}
		return best;
	}

	public double[] bestSolution(ArrayList<double[]> currentPopulation){
		double[] best = currentPopulation.get(0);
		for(double[] solution: currentPopulation){
			if(Assess.getTest1(solution) < Assess.getTest1(best)) best = solution;
		}
		return best;
	}

	public double[] solve(){
		ArrayList<double[]> currentPopulation;
		currentPopulation = initialPopulation();
		double best = bestFitness(currentPopulation);
		double allTimeBest = best;
		double selectionValue;
		int cycle = 1;
		while (best > 0 && cycle < cycles){
			selectionValue = Math.random();
			if(selectionValue < selectionProbability) currentPopulation = fitnessProportionalSelection(currentPopulation, getFitnesses(currentPopulation));
			else currentPopulation = tournamentSelection(currentPopulation, getFitnesses(currentPopulation));
			best = bestFitness(currentPopulation);

			if(best < 0.1){
				selectionProbability = 0;
				//mutationSize = best/2;
				mutationSize = 0.001;
			}

			if(best < 0.01){
				selectionProbability = 0;
				//mutationSize = best/2;
				mutationSize = 0.0001;
			}

			if(best < 0.0001){
				selectionProbability = 0;
				mutationSize = 0.00001;
			}

			if(best < 0.000001){
				selectionProbability = 0;
				mutationSize = 0.0000001;
			}

			if(best < 0.00000001){
				selectionProbability = 0;
				mutationSize = 0.00000001;
			}

			if(best < allTimeBest){
				allTimeBest = best;
			}
			System.out.println();
			System.out.printf("Current Best Fitness: %.14f", best);
			System.out.print("                  population: " + cycle);
			System.out.println();
			System.out.printf("All Time Best Fitness: %.14f" , allTimeBest);
			cycle ++;
		}
		for (double num:bestSolution(currentPopulation)) {
			System.out.println(num);
		}
		return bestSolution(currentPopulation);
	}


}

class Solution02{
	private double maxWeight = 500;
	private int populationSize = 500;
	private int solutionSize = 100;
	private int tournamentSize = 5;
	private int elitism = populationSize;
	private double mutationRate = 0.8;
	private double crossOverRate = 0.00001;
	private int crossOverPoints = 5;
	private int cycles = 300;
	private int flips = 3;

	public double getWeight(boolean[] solution){
		return Assess.getTest2(solution)[0];
	}

	public double getUtility(boolean[] solution){
		return Assess.getTest2(solution)[1];
	}

	public ArrayList<Double> getUtilities(ArrayList<boolean[]> population){
		ArrayList<Double> utilities = new ArrayList<>();
		for(boolean[] solution: population){
			utilities.add(getUtility(solution));
		}
		return utilities;
	}

	public ArrayList<Double> getWeights(ArrayList<boolean[]> population){
		ArrayList<Double> weights = new ArrayList<>();
		for(boolean[] solution: population){
			weights.add(getWeight(solution));
		}
		return weights;
	}

	public double getTotalWeight(ArrayList<boolean[]> population){
		double total = 0;
		for(boolean[] solution: population){
			total += getWeight(solution);
		}
		return total;
	}

	public ArrayList<boolean[]> initialPopulation(){
		ArrayList<boolean[]> newPopulation = new ArrayList<>();
		for(int x = 0; x < populationSize; x++){
			ArrayList<Integer> picked = new ArrayList<>();
			boolean[] newSol = new boolean[solutionSize];
			int dial = 0;
			while(getWeight(newSol) < maxWeight){
				dial = (int)(Math.random() * (solutionSize));
				if(!picked.contains(dial)){
					picked.add(dial);
					newSol[dial]= true;
				}
			}
			newSol[dial]= false;
			newPopulation.add(newSol);
		}
		return newPopulation;
	}

	public ArrayList<boolean[]> tournamentSelection(ArrayList<boolean[]> population,ArrayList<Double> fitnesses){
		ArrayList<boolean[]> newPopulation = new ArrayList<>();
		int num;
		boolean picked;
		boolean[] temp;
		for (int x = 0; x < populationSize; x++) {
			ArrayList<Integer> selections = new ArrayList<>();
			for (int y = 0; y < tournamentSize; y++) {
				picked = false;
				while (!picked) {
					num = (int) Math.round(Math.random() * (elitism - 1));
					if (!(selections.contains(num))) {
						selections.add(num);
						picked = true;
					}
				}
			}
			int tournamentWinner = selections.get(0);
			int current;
			double currentFitness;
			for (int y = 0; y < tournamentSize; y++){
				current = selections.get(y);
				currentFitness = fitnesses.get(current);
				if(currentFitness > fitnesses.get(tournamentWinner) && getWeight(population.get(current)) < maxWeight) tournamentWinner = current;
			}
			temp = population.get(tournamentWinner);
			temp = mutation(temp);
			temp = crossOver(temp, population);
			newPopulation.add(temp);
		}
		return newPopulation;
	}

	public boolean[] mutation(boolean[] sol){
		boolean[] solOut = sol.clone();
		double mutationPick = Math.random();
		if(mutationPick <= mutationRate){
			for(int x = 0; x < flips; x++){
				int dial = (int)(Math.random() * (solutionSize));
				solOut[dial] =  !solOut[dial];
			}

		}
		if(getWeight(solOut) < maxWeight) return solOut;
		else return sol;
	}

	public boolean[] crossOver(boolean[] sol1, ArrayList<boolean[]> population){
		boolean[] solOut = sol1.clone();
		double crossOverPick = Math.random();
		if(crossOverPick <= crossOverRate){
			boolean[] sol2 = population.get((int)Math.round(Math.random() * (populationSize - 1)));
			solOut = new boolean[solutionSize];
			boolean[] solCurrent = sol1;
			ArrayList<Integer> points = new ArrayList<>();
			int num = 0;
			boolean found;
			for (int x = 0; x < crossOverPoints; x++){
				found = false;
				while (!found){
					num = (int)Math.round(Math.random() * (solutionSize - 1));
					if(!points.contains(num)) found = true;
				}
				points.add(num);
			}
			for (int x = 0; x < solutionSize; x++){
				solOut[x] = solCurrent[x];
				if(points.contains(x)){
					if(Arrays.equals(solCurrent, sol1)) solCurrent = sol2;
					solCurrent = sol1;
				}
			}
		}
		if(getWeight(solOut) < maxWeight) return solOut;
		return sol1;
	}

	public double bestFitness(ArrayList<boolean[]> currentPopulation){
		double best = getUtility(currentPopulation.get(0));
		double current;
		for(boolean[] solution: currentPopulation){
			current =getUtility(solution);
			if(current > best) best = current;
		}
		return best;
	}

	public boolean[] bestSolution(ArrayList<boolean[]> currentPopulation){
		boolean[] best = currentPopulation.get(0);
		for(boolean[] solution: currentPopulation){
			if(getUtility(solution) > getUtility(best)) best = solution;
		}
		return best;
	}

	public boolean[] solve(){
		ArrayList<boolean[]> currentPopulation;
		currentPopulation = initialPopulation();
		double best = bestFitness(currentPopulation);
		double allTimeBest = best;
		boolean[] allTimeBestVal = new boolean[solutionSize];
		int cycle = 1;
		while (best > 0 && cycle < cycles){

			currentPopulation = tournamentSelection(currentPopulation, getUtilities(currentPopulation));
			best = bestFitness(currentPopulation);

			if(best > allTimeBest){
				allTimeBest = best;
				allTimeBestVal = bestSolution(currentPopulation);
			}
			System.out.println();
			System.out.printf("Current Average Weight: %.14f", getTotalWeight(currentPopulation)/populationSize);
			System.out.println();
			System.out.printf("Current Best Fitness: %.14f", best);
			System.out.print("                  population: " + cycle);
			System.out.println();
			System.out.printf("All Time Best Fitness: %.14f" , allTimeBest);
			cycle ++;
		}
		System.out.println();
		System.out.println("Weight: " + getWeight(bestSolution(currentPopulation)));
		System.out.println();
		return allTimeBestVal;
	}
}
