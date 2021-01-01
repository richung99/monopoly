import java.lang.Math; 
class Player{
   private int space;
   private int doubleCount = 0;
   private String name;
   
   //all players start on go
   public Player()
   {
      space = 0;
      name = "default";
   }
   
   public Player(String playerName){
      name = playerName;
   }
   
   // receives a Gameboard object
   // changes player space and returns updated Gameboard
    public Object[] move(Gameboard game){
      boolean doubles = false;
      int dice1 = (int)(Math.random() * 6 + 1);
      int dice2 = (int)(Math.random() * 6 + 1);
      // System.out.println("Player " + name + " rolled: " + dice1 + ", " + dice2);
      int move = dice1 + dice2;
      
      // check for doubles
      if(dice1 == dice2){
         doubles = true;
         doubleCount++;
      }
      
      // if doubleCount is 3, go to jail
      if(doubleCount == 3){
         space = 10;
         doubleCount = 0;
         game.updateProperty(space);
         Object[] toReturn = {(Integer)space, game};
         game.updateJail("doubles");
         // printPlayer();
         return toReturn;
      }
      
      else{
         space += move;
         
         //if you pass go again
         if(space > 39){
            space = space - 40;
         }
         
         //if it lands on a chance space
         if(space == 7 || space == 22 || space == 36){
            int card = game.chanceShuffle();
            switch(card){
               //go to jail
               case 0: 
                  space = 10;
                  doubles = false;
                  game.updateJail("chance");
                  break;
               //nearest railroad
               case 1: 
                  if(space == 7)
                     space = 5;
                  else if(space == 22)
                     space = 25;
                  else if(space == 36)
                     space = 35;
                  break;
               //reading railroad
               case 2: space = 5;
                  break;
               //next railroad
               case 3: 
                  if(space == 7)
                     space = 15;
                  else if(space == 22)
                     space = 25;
                  else if(space == 36)
                     space = 5;
                  break;
               //go back 3 spaces
               case 4: space -= 3;
                  break;
               //illinois avenue
               case 5: space = 24;
                  break;
               //st. charles place
               case 6: space = 11;
                  break;
               //nearest utility
               case 7: 
                  if(space == 7)
                     space = 12;
                  else if(space == 22)
                     space = 28;
                  else if(space == 36)
                     space = 28;
                  break;
               //boardwalk
               case 8: space = 39;
                  break;
               //advance to go
               case 9: space = 0;
                  break;
            }
         }
         //if it lands on a chest space
         else if(space == 2 || space == 17 || space == 33){
            int card = game.chestShuffle();
            switch(card){
               //advance to go
               case 0: space = 0;
                  break;
               //go to jail
               case 1: 
                  space = 10;
                  doubles = false;
                  game.updateJail("chest");
                  break;
            }
         }
         
         //if you land on "go to jail", move the game piece to jail
         else if(space == 30) {   
            space = 10;
            doubles = false;
            // game.updateProperty(space);
            // Object[] toReturn = {(Integer) space, game};
            game.updateJail("goto");
            // return toReturn;
         }
      
                  
         // printPlayer();
         
         if(doubles) {
            return move(game);
         }
         
         else {
            game.updateProperty(space);
            doubleCount = 0;
            Object[] toReturn = {(Integer)space, game};
            return toReturn;
         }
      }
   }
   
   public String getName(){
      return name;
   }
   
   public int getSpace(){
      return space;
   }
   
   public String toString(){
      String toReturn = "Player " + name + " landed on " + space;
      return toReturn;
   }
   
   public void resetPlayer(){
      space = 0;
      doubleCount = 0;
   }
   
   public void printPlayer(){
      System.out.println("Player " + name + " landed on " + space);
   }
}
