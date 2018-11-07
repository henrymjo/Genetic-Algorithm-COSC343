import cosc343.assig2.Creature;
import java.util.Random;

/**
* The MyCreate extends the cosc343 assignment 2 Creature.  Here you implement
* creatures chromosome and the agent function that maps creature percepts to
* actions.
*
* @author Henry Morrison-Jones
* @version 3.0
* @since   2017-05-01
*/
public class MyCreature extends Creature {

  // Random number generator
  Random rand = new Random();

  //Used for storing creatures ending fitness.
  public int fitness = 0;

  // chromosome to contains weights for moving towards monster, moving towards
  // creature, moving towards food, eating and random.
  // note that the 'move away' weights are the complements of the move towards
  // weights.
  private float [] chromosome = new float[8];

  //defines where section pertaining to monsters begins in the percept array.
  private int sectionOne = 0;

  //defines where the section pertaining to creatures begins in the percept array.
  private int sectionTwo = 9;

  //defines where the section pertaining to food begins in the percept array.
  private int sectionThree = 18;


  /**
  * Constructor that initialises chromosome to a random state.
  * @param numPercept number of percepts that creature will be receiving.
  * @param numActions number of action output vector that creature will need to
  * produce on every turn.
  */
  public MyCreature(int numPercepts, int numActions) {

    for(int i=0; i<chromosome.length; i++){
      chromosome[i] = rand.nextFloat();
    }
  }

  /**
  * Constructor for elitism
  */
  public MyCreature(MyCreature incarnation){
    this.chromosome = incarnation.chromosome;
  }

  /**
  * Constructor - takes two parents and initialises the chromosome using their
  * DNA.
  * @param mother First MyCreature object used for crossover
  * @param father Second MyCreature object used for crossover
  */
  public MyCreature(MyCreature mother, MyCreature father, float mutationRate){
    //Picks a random starting point for crossover to begin.
    int crossoverPoint = rand.nextInt(chromosome.length);

    for(int i = 0; i<(chromosome.length/2)+1; i++){ //for the larger half of the chromosome use mothers values
      chromosome[crossoverPoint] = mother.getChromosome()[crossoverPoint];
      crossoverPoint++;
      if(crossoverPoint == chromosome.length){
        crossoverPoint = 0;
      }
    }
    for(int i = (chromosome.length/2)+1; i < chromosome.length; i++){ //for the smaller half of the chromosome use fathers values
      chromosome[crossoverPoint] = father.getChromosome()[crossoverPoint];
      crossoverPoint++;
      if(crossoverPoint == chromosome.length){
        crossoverPoint = 0;
      }
    }

    float mutation = rand.nextFloat();
    if(mutation<mutationRate){
      chromosome[rand.nextInt(chromosome.length)] = rand.nextFloat();
      chromosome[rand.nextInt(chromosome.length)] = rand.nextFloat(); //mutates a random piece of the chromosome to a random value;
    }
  }

  /**
  * Default Constructor
  */
  public MyCreature(){

  }

  /**
  * Accessor method for returning the chromosome of a MyCreature object.
  * @return the chromosome datafield.
  */
  public float[] getChromosome(){
    return chromosome;
  }

  /**
  * Method for mapping percepts to actions using the chromosome as its framework.
  *
  * The method works by splitting the percepts into three sections and adding
  * weights to certain actions depending on which section the percept is in.
  *
  * Section one pertains to monster movement, which adds the chromosome weights
  * for moving towards monster and moving away from monster.
  *
  * Section two pertains to creature movement, which adds the chromosome weights
  * for moving towards creature percept, and moving away from creature percept.
  *
  * Section three pertains to food actions. This is the only section which has
  * the possibility of eating so is the only one which maps a weight to the
  * eating action. This also maps a move towards food weight and a move away
  * from food weight.
  *
  * Each creature also has a random weight and this is added independent to
  * percepts.
  *
  * All the weights used in this function are encoded in the chromosome. These
  * weights are learned through the genetic algorithm in order to prioritise
  * types of action.
  *
  * @param percepts an array of percepts
  * @param numPercepts the size of the array of percepts depending on the percepts
  * chosen.
  * @param numExpectedAction tells you the expected size of the returned array
  * @return an array of floats, specifying action weights
  */
  @Override
  public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) {

      float actions[] = new float[numExpectedActions];

      for(int i=sectionOne;i<sectionTwo;i++) {
        if(percepts[i]!=0){
          actions[i] += chromosome[0]; //gives move towards action corresponding weight.
          actions[8-i] += (chromosome[1]); //gives move away action corresponding weight.
        }
      }

      for(int i=sectionTwo;i<sectionThree;i++){
        if(percepts[i]!=0){
          actions[i-sectionTwo] += chromosome[2];
          actions[8-(i-sectionTwo)] += (chromosome[3]);
        }
      }

      for(int i=sectionThree; i<numPercepts; i++){
        if(percepts[i]!=0){
          actions[i-sectionThree] += chromosome[4];
          actions[8-(i-sectionThree)] += chromosome[5];

          // Only add an eating weight when there is food on the square.
          if(i==22){
            actions[9] += (percepts[i]*chromosome[7]);
          }
        }
      }
      // Random action weight added.
      actions[10] += chromosome[6];

      return actions;
  }
}
