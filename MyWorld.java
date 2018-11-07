import cosc343.assig2.World;
import cosc343.assig2.Creature;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * The MyWorld extends the cosc343 assignment 2 World.  Here you can set
 * some variables that control the simulations and override functions that
 * generate populations of creatures that the World requires for its
 * simulations.
 *
 * @author Henry Morrison-Jones
 * @version 3.0
 * @since   2017-05-01
 */
public class MyWorld extends World {

  /* Here you can specify the number of turns in each simulation
   * and the number of generations that the genetic algorithm will
   * execute.
   */
  private final int _numTurns = 200;
  private final int _numGenerations = 30;

  // Tracks the current generation number.
  private int generationTrack = 0;

  // Stores the average fitness value for each generation.
  private float[] avgFitnessData = new float[_numGenerations+1];




  /* Constructor.
   *
   Input: griSize - the size of the world
   windowWidth - the width (in pixels) of the visualisation window
   windowHeight - the height (in pixels) of the visualisation window
   repeatableMode - if set to true, every simulation in each
   generation will start from the same state
   perceptFormat - format of the percepts to use: choice of 1, 2, or 3
   */
  public MyWorld(int gridSize, int windowWidth, int windowHeight, boolean repeatableMode, int perceptFormat) {
    // Initialise the parent class - don't remove this
    super(gridSize, windowWidth,  windowHeight, repeatableMode, perceptFormat);

    // Set the number of turns and generations
    this.setNumTurns(_numTurns);
    this.setNumGenerations(_numGenerations);
    avgFitnessData[0] = 0; //sets the generation 0 to 0 fitness in data
  }

  /* The main function for the MyWorld application
   *
   */
  public static void main(String[] args) {
    // Here you can specify the grid size, window size and whether to run
    // in repeatable mode or not
    int gridSize = 50;
    int windowWidth =  1600;
    int windowHeight = 900;
    boolean repeatableMode = false;

    // Set percept format type.
    int perceptFormat = 2;

    // Instantiate MyWorld object.  The rest of the application is driven
    // from the window that will be displayed.
    MyWorld sim = new MyWorld(gridSize, windowWidth, windowHeight, repeatableMode, perceptFormat);
  }


  /* The MyWorld class must override this function, which is
   used to fetch a population of creatures at the beginning of the
   first simulation.  This is the place where you need to  generate
   a set of creatures with random behaviours.

   Input: numCreatures - this variable will tell you how many creatures
   the world is expecting

   Returns: An array of MyCreature objects - the World will expect numCreatures
   elements in that array
   */
  @Override
  public MyCreature[] firstGeneration(int numCreatures) {

    int numPercepts = this.expectedNumberofPercepts();
    int numActions = this.expectedNumberofActions();

    // This is just an example code.  You may replace this code with
    // your own that initialises an array of size numCreatures and creates
    // a population of your creatures
    MyCreature[] population = new MyCreature[numCreatures];
    for(int i=0;i<numCreatures;i++) {
      population[i] = new MyCreature(numPercepts, numActions);
    }
    return population;
  }

  /* The MyWorld class must override this function, which is
   used to fetch the next generation of the creatures.  This World will
   proivde you with the old_generation of creatures, from which you can
   extract information relating to how they did in the previous simulation...
   and use them as parents for the new generation.

   Input: old_population_btc - the generation of old creatures before type casting.
   The World doesn't know about MyCreature type, only
   its parent type Creature, so you will have to
   typecast to MyCreatures.  These creatures
   have been simulated over and their state
   can be queried to compute their fitness
   numCreatures - the number of elements in the old_population_btc
   array


   Returns: An array of MyCreature objects - the World will expect numCreatures
   elements in that array.  This is the new population that will be
   use for the next simulation.
   */
  @Override
  public MyCreature[] nextGeneration(Creature[] old_population_btc, int numCreatures) {

    // Typecast old_population of Creatures to array of MyCreatures
    MyCreature[] old_population = (MyCreature[]) old_population_btc;

    // Create a new array for the new population
    MyCreature[] new_population = new MyCreature[numCreatures];

    // Creature with the highest fitness function.
    MyCreature queen = new MyCreature();

    // Variables for proving genetic progress.
    float avgLifeTime= 0f;
    int nSurvivors = 0;
    float avgFitness =0f;

    for(MyCreature creature : old_population) {

      // For calculating creature's ending fitness.
      int fitness = 0;

      // The energy of the creature.  This is zero if creature starved to
      // death, non-negative otherwise.  If this number is non-zero, but the
      // creature is dead, then this number gives the energy of the creature
      // at the time of death.
      int energy = creature.getEnergy();
      fitness+=energy/5;

      // This querry can tell you if the creature died during simulation
      // or not.
      boolean dead = creature.isDead();

      if(dead) {
        // If the creature died during simulation, you can determine
        // its time of death (in turns)
        int timeOfDeath = creature.timeOfDeath();
        avgLifeTime += (float) timeOfDeath;
        fitness += timeOfDeath;
      } else {
        nSurvivors += 1;
        avgLifeTime += _numTurns;
        fitness += _numTurns;
      }
      // Set the creatures ending fitness datafield
      creature.fitness = fitness;
      avgFitness += (float)fitness;

      // Highest fitness creature is retained in queen.
      if(creature.fitness > queen.fitness){
        queen = creature;
      }
    }

    // Calculates the average fitness and lifetime of the agents in a generation.
    avgLifeTime /= (float) numCreatures;
    avgFitness /= (float) numCreatures;

    // Displays fitness.
    System.out.println("Simulation stats:");
    System.out.println("  Survivors    : " + nSurvivors + " out of " + numCreatures);
    System.out.println("  Avg life time: " + avgLifeTime + " turns");
    System.out.println("  Avg fitness " + avgFitness);
    System.out.println("  Highest fitness " + queen.fitness);
    /*System.out.println("  Winning Chromosome:");
    for(int i = 0; i<queen.getChromosome().length; i++){
      System.out.print(queen.getChromosome()[i] + " ");
    }
  */
    System.out.print("\n");

    /* This section returns the array of next generation creatures.
     * the first two creatures in the array use elitism and are the king and
     * queen of the previous generation. The remaining creatures use tournament
     * selection.
     * The mutation rate is also contained in this section.
     */
    for(int i=0;i<numCreatures; i++){
      Random r = new Random();

      // Used for storing the mother and father to be passed to Creature
      // constructor.
      MyCreature mother = new MyCreature();
      MyCreature father = new MyCreature();

      //Here you can change the mutation rate
      float mutationRate = 0.03f;

      // Elitism with Queen.
      if(i == 0){
        new_population[i] = new MyCreature(queen);

      }else{ //for everything but the two most successful

        // Here you can set the pool size for tournament selection and the
        // the index for where crossover begins is randomly initiated.
        int sampleSize = (old_population.length)/5;
        int sampleIndex = r.nextInt(old_population.length);

        // This code picks a mother and father using tournament selection.
        for(int index = 0; index < sampleSize; index++){
          if(old_population[sampleIndex].fitness > mother.fitness){
            father = mother;
            mother = old_population[sampleIndex];
          }else if(old_population[sampleIndex].fitness > father.fitness){
            father = old_population[sampleIndex];
          }
          if(sampleIndex==(old_population.length-1)){
            sampleIndex = 0;
          }else{
            sampleIndex += 1;
          }
        }
        new_population[i] = new MyCreature(mother, father, mutationRate);
      }
    }

    // Increments the number of generations and stores average fitness in
    // array to be graphed.
    generationTrack++;
    avgFitnessData[generationTrack] = avgFitness;

    // When at generation 500, graph the results.
    if(generationTrack==_numGenerations){
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.add(new GraphingData(avgFitnessData));
      f.setSize(1000, 600);
      f.setLocation(200, 200);
      f.setVisible(true);
    }
    return new_population;
  }


  /**
   * Inner class is used for graphing the Average Fitness over generation.
   *
   */
  public class GraphingData extends JPanel {
    // Array of data.
    float[] data;

    // Preferred padding size for the graph to display.
    final int PADDING = 50;

    /**
     * Constructor sets the data array.
     */
    public GraphingData(float[] data){
      this.data = data;
    }

    /**
     * Method draws and displays the data array as a graph.
     * @param g graphics object required for graphing data.
     */
    protected void paintComponent(Graphics g) {

      super.paintComponent(g);

      // Create and initialise Graphics2D object for the graph.
      Graphics2D g2 = (Graphics2D)g;

      // Set width and height;
      int width = getWidth();
      int height = getHeight();

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


      // Sets background to white.
      g2.setPaint(Color.white);
      g2.fillRect(0,0,width,height);

      // Draws the two axes.
      g2.setPaint(Color.black);
      g2.drawLine(PADDING, PADDING, PADDING, height-PADDING);
      g2.drawLine(PADDING, height-PADDING, width-PADDING, height-PADDING);

      //Draws graph labels and max fitness.
      g.drawString("Average Fitness Over Generations", (width/2)-2*PADDING, PADDING/2);
      g.drawString("(Pool = 1/5, mutation = 0.03)", (width/2)-2*PADDING+10, PADDING/2+15);
      g.drawString("Average Fitness", 10, PADDING-10);
      g.drawString("Generation", width/2-25, height-5);
      g.drawString("Highest Average Fitness = " + Float.toString(getMax()), (width/2)-2*PADDING, height/2);


      // Sets the increment size for x-axis and scale for y-axis.
      double xInc = (double)(width - 2*PADDING)/(data.length-1);
      double scale = (double)(height - 2*PADDING)/getMax();

      // Draws x axis points and labels.
      for(int i = 0; i < data.length; i++){
        g2.draw(new Line2D.Double(i*xInc+PADDING, height-PADDING-10, i*xInc+PADDING, height-PADDING+10));
        g.drawString(Integer.toString(i), ((int)xInc*i + PADDING)-10, height-PADDING+30);
      }

      // Draws and connects data points.
      g2.setPaint(Color.blue);
      double lastY = height-PADDING;
      double lastX = PADDING;
      for(int i = 0; i < data.length; i++) {
        double x = PADDING + i*xInc;
        double y = height - PADDING - scale*data[i];
        g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        g2.draw(new Line2D.Double(lastX, lastY, x, y));
        lastY = y;
        lastX = x;
      }
    }

    /**
     * Method for returning the greatest value of our fitness data.
     * @return highest value in the data array.
     */
    private float getMax() {
      float max = -Integer.MAX_VALUE;
      for(int i = 0; i < data.length; i++) {
        if(data[i] > max)
          max = data[i];
      }
      return max;
    }
  }
}
