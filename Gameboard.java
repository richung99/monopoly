import java.util.*;
public class Gameboard {
   private List<Integer> chest = new ArrayList<Integer>();
   private List<Integer> chance = new ArrayList<Integer>();
   // private Dictionary properties = new Hashtable();
   private Map<Integer, Integer> properties = new HashMap<Integer, Integer>();
   private int chanceJail;
   private int chestJail;
   private int gotoJail;
   private int doublesJail;
   
   public Gameboard(){
      chest.add(0);
      chest.add(1);
      for(int i = 0; i < 15; i++){
         chest.add(-1);
      }
      for(int i = 0; i < 10; i++){
         chance.add(i);
      }
      for(int i = 0; i < 7; i++){ // picture doesn't include throwing money up in the air case
         chance.add(-1);
      }
      
      Collections.shuffle(chest);
      Collections.shuffle(chance);
      
      for(int i = 0; i < 40; i++){
         addProperty();
      }
      
      chanceJail = 0;
      chestJail = 0;
      gotoJail = 0;
      doublesJail = 0;
      
   }
   
   public void addProperty() {
      properties.put(properties.size(), 0); // index: # times visited
   }
   
   public void updateProperty(int key){
      properties.put(key, (int) properties.get(key)+1);
   }
   
   public int chestShuffle(){
      int top = chest.remove(0);
      chest.add(top);
      return top;
   }
   
   public int chanceShuffle(){
      int top = chance.remove(0);
      chance.add(top);
      return top;
   }
   
   public List<Integer> getChest(){
      return chest;
   }
   
   public List<Integer> getChance(){
      return chance;
   }
   
   public Map<Integer, Integer> getProperties(){
      return properties;
   }
   
   // updates chanceJail, chestJail, gotoJail, and visitingJail based on argument
   public void updateJail(String jailType){
      switch(jailType){
         case "chance":
            chanceJail++;
            break;
         case "chest":
            chestJail++;
            break;
         case "goto":
            gotoJail++;
            break;
         case "doubles":
            doublesJail++;
            break;
      }
   }
   
   public int[] getJail(){
      return new int[] {chanceJail, chestJail, gotoJail, doublesJail};
   }
   
   public void resetGame(){
//       chanceJail = 0;
//       chestJail = 0;
//       gotoJail = 0;
//       doublesJail = 0;
      
      Collections.shuffle(chest);
      Collections.shuffle(chance);

      for (int key : properties.keySet()){
         properties.put(key, 0);
      }
   }
   
}