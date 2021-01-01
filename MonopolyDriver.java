import java.util.*;
import java.io.*;

public class MonopolyDriver {
   
   public static void main(String[] args){
      
      MonopolyDriver driver = new MonopolyDriver();
   
      int numPlayers = 4;
      int numturns = 100;
      int numGames = 500;
   
      // initialize Gameboard and new players
      Gameboard monopoly = new Gameboard();
   
      List<Player> playerList = new ArrayList<Player>();
      
      for(int i = 1; i <= numPlayers; i++){
         playerList.add(new Player("" + i));
      }
      
      // store properties from Gameboard into array
      List<Integer> propList = new ArrayList<Integer>();
   
      for (int key : monopoly.getProperties().keySet())
         propList.add(key);
      propList.add(0, -1);
      int[] properties = new int[monopoly.getProperties().size()+1];
      properties = propList.stream().mapToInt(i -> i).toArray();
      Arrays.sort(properties);
      
      // create matrix with properties in the first column, and number of turns in the first row
      int[][] output = new int[monopoly.getProperties().size()+1][numturns+1];
      
      for(int col = 1; col < output[0].length; col++){ // # of turns in first row
         output[0][col] = col;
      }
      
      for(int row = 0; row < output.length; row++){ // property indices in first column
         output[row][0] = properties[row];
      }
      
      int [][] outputCopy = new int[output.length][];
      for(int i = 0; i < output.length; i++)
         outputCopy[i] = output[i].clone();
      
      // for each turn, each player moves and updates the gameboard and output matrix is updated
      // for each game, the last column of the turn is stored and added to an arraylist of int[]
      // player is reset at the end of every game
      
      List<double[]> allGames = new ArrayList<double[]>();
   
      for(int game = 0; game < numGames; game++){
         for(int turn = 0; turn < numturns; turn++){
            for(Player player : playerList)
               monopoly = (Gameboard) player.move(monopoly)[1];
            for(int row = 1; row < output.length; row++)
               output[row][turn+1] = (int) monopoly.getProperties().get(output[row][0]);
         }
         // adding values of last column into temporary arraylist
         List<Integer> tempList = new ArrayList<Integer>();
         for(int row = 1; row < output.length; row++)
            tempList.add(output[row][numturns]);
         tempList.add(0, game+1);
         
         // conversion of arraylist into double[]
         double[] lastCol = tempList.stream().mapToDouble(i -> i).toArray();
         
         allGames.add(lastCol);
         
         // reset players and gameboard
         for(Player player : playerList)
            player.resetPlayer();
      
         monopoly.resetGame();
         
         outputCopy = driver.sumMatrix(outputCopy, output);
         
      }
      
      double[][] outputDouble = new double[outputCopy.length][outputCopy[0].length];
      outputDouble = driver.divideMatrix(outputCopy, numGames);
      
      allGames.add(0, Arrays.stream(properties).asDoubleStream().toArray()); // adding the property indices into first row
      
      // conversion of arraylist to int[][]
      double[][] allOutput = new double[allGames.size()][allGames.get(0).length];
      allOutput = allGames.toArray(allOutput);
      allOutput = driver.transpose(allOutput); // transposing the matrix
      allOutput = driver.computeAverage(allOutput);
   
      driver.printToCSV(outputDouble, "sampleGame.csv");
      driver.printToCSV(allOutput, "allGames.csv");
      
      // model the game board as an 11x11 matrix and create heatmaps based on the total # of visits  and average # of visits
      
      double[][] heatmapSum = new double[11][11];
      heatmapSum = driver.generateMap(heatmapSum, allOutput, 2);
      
      heatmapSum = driver.rotate(driver.rotate(heatmapSum));
      
      int[] jailStats = monopoly.getJail();
      int jailSum = Arrays.stream(jailStats).sum();
      // "just visiting" is the total # of times jail is visited - # of times players go to jail from chance, chest, doubles, and goto
      heatmapSum[10][0] = heatmapSum[10][0] - jailSum; 
      heatmapSum[9][1] = jailSum;
      
      driver.printToCSV(heatmapSum, "heatmapSum.csv");
      
      double[][] heatmapAvg = new double[11][11];
      heatmapAvg = driver.generateMap(heatmapAvg, allOutput, 1);
      
      heatmapAvg = driver.rotate(driver.rotate(heatmapAvg));
   
      heatmapAvg[10][0] = (heatmapAvg[10][0]*numGames - jailSum)/numGames;
      heatmapAvg[9][1] = jailSum/numGames;
      
      driver.printToCSV(heatmapAvg, "heatmapAvg.csv");
   
   //       System.out.println("Chance Jail: " + jailStats[0]);
   //       System.out.println("Chest Jail: " + jailStats[1]);
   //       System.out.println("Go To Jail: " + jailStats[2]);
   //       System.out.println("Doubles Jail: " + jailStats[3]);
   
      // keep track of visits by property color vs # turns in sample game
      String[][] propertyStats = new String[numturns+1][10];
      String[] propertyNames = {"Brown", "Light Blue", "Pink", "Orange", "Red", "Yellow", "Green", "Dark Blue", "Railroads", "Utilities"};
      propertyStats[0] = propertyNames;
      for(int row = 1; row < propertyStats.length; row++){
         propertyStats[row][0] = "" + (output[2][row] + output[4][row]);                                       // brown
         propertyStats[row][1] = "" + (output[7][row] + output[9][row] + output[10][row]);                     // light blue
         propertyStats[row][2] = "" + (output[12][row] + output[14][row] + output[15][row]);                   // pink
         propertyStats[row][3] = "" + (output[17][row] + output[19][row] + output[20][row]);                   // orange
         propertyStats[row][4] = "" + (output[22][row] + output[24][row] + output[25][row]);                   // red
         propertyStats[row][5] = "" + (output[27][row] + output[29][row] + output[30][row]);                   // yellow
         propertyStats[row][6] = "" + (output[32][row] + output[33][row] + output[35][row]);                   // green
         propertyStats[row][7] = "" + (output[38][row] + output[40][row]);                                     // blue
         propertyStats[row][8] = "" + (output[6][row] + output[16][row] + output[26][row] + output[36][row]);  // railroads
         propertyStats[row][9] = "" + (output[13][row] + output[29][row]);                                     // utilities
      }
      driver.printToCSV(propertyStats, "propertyStats.csv");
   }
   
   // sums matrix1 and matrix2
   public int[][] sumMatrix(int[][] matrix1, int[][] matrix2){
      for(int i = 1; i < matrix1.length; i++){
         for(int j = 1; j < matrix1[0].length; j++){
            matrix1[i][j] += matrix2[i][j];
         }
      }
      return matrix1;
   }
   
   // divides every element in matrix by num
   public double[][] divideMatrix(int[][] matrix, int num){
      double[][] copyMatrix = new double[matrix.length][matrix[0].length];
      for(int i = 0; i < matrix.length; i++){
         for(int j = 0; j < matrix[0].length; j++){
            if(i == 0 || j == 0)
               copyMatrix[i][j] = matrix[i][j];
            else
               copyMatrix[i][j] = (double) matrix[i][j]/num;
         }
      }
      return copyMatrix;
   }
   
   public double[][] generateMap(double[][] heatmapMatrix, double[][] allOutput, int index){
      // create temporary lists to store into heatmapMatrix
      List<Double> topRowList = new ArrayList<Double>();
      for(int i = 1; i < 11; i++) // 0 to 9
         topRowList.add(allOutput[i][allOutput[0].length-index]);
      double[] topRow = topRowList.stream().mapToDouble(i -> i).toArray();
         
      List<Double> rightColList = new ArrayList<Double>();
      for(int i = 11; i < 21; i++) // 10 to 19
         rightColList.add(allOutput[i][allOutput[0].length-index]);
      double[] rightCol = rightColList.stream().mapToDouble(i -> i).toArray();
      
      List<Double> botRowList = new ArrayList<Double>();
      for(int i = 21; i < 31; i++) // 20 to 29
         botRowList.add(allOutput[i][allOutput[0].length-index]);
      Collections.reverse(botRowList); // reverse the bottom row so we can write left to right
      double[] botRow = botRowList.stream().mapToDouble(i -> i).toArray();
      
      List<Double> leftColList = new ArrayList<Double>();
      for(int i = 31; i < 41; i++) // 30 to 39
         leftColList.add(allOutput[i][allOutput[0].length-index]);
      Collections.reverse(leftColList); // reverse the left col so we can write up to down
      double[] leftCol = leftColList.stream().mapToDouble(i -> i).toArray(); 
      
      // fill top row
      for(int col = 0; col < topRow.length; col++)
         heatmapMatrix[0][col] = topRow[col];
      // fill right col
      for(int row = 0; row < rightCol.length; row++)
         heatmapMatrix[row][10] = rightCol[row];
      // fill bot row
      for(int col = 1; col <= 10; col++)
         heatmapMatrix[10][col] = botRow[col-1];
      // fill left col
      for(int row = 1; row <= 10; row++)
         heatmapMatrix[row][0] = leftCol[row-1];
      return heatmapMatrix;
   }
   
   public double[][] rotate(double[][] matrix) {
      for (int i = 0; i < matrix.length / 2; i++) {
         int top = i;
         int bottom = matrix.length - 1 - i;
         for (int j = top; j < bottom; j++) {
            double temp = matrix[top][j];
            matrix[top][j] = matrix[j][bottom];
            matrix[j][bottom] = matrix[bottom][bottom - (j - top)];
            matrix[bottom][bottom - (j - top)] = matrix[bottom - (j - top)][top];
            matrix[bottom - (j - top)][top] = temp;
         }
      } 
      return matrix;
   }
   
   // returns new double[][] with 2 additional columns containing sum and average of the row
   public double[][] computeAverage(double[][] array){
      double[][] array_new = new double[array.length][array[0].length + 2];
      for(int row = 0; row < array_new.length; row++){
         double sum = 0;
         for(int col = 0; col < array_new[0].length-2; col++){
            array_new[row][col] = array[row][col];
            if(col != 0)
               sum += array[row][col];
         }
         array_new[row][array_new[0].length-2] = sum;
         array_new[row][array_new[0].length-1] = sum/array[0].length;
      }
      array_new[0][array_new[0].length-2] = -1;
      array_new[0][array_new[0].length-1] = -1;
      return array_new;
   }
   
   public double[][] transpose(double[][] array) {
      if (array == null || array.length == 0)
         return array;
   
      int width = array.length;
      int height = array[0].length;
   
      double[][] array_new = new double[height][width];
   
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            array_new[y][x] = array[x][y];
         }
      }
      return array_new;
   }
   
   public void printToCSV(int[][] output, String filename){
      try {
         File file = new File(filename);
         if(file.createNewFile())
            System.out.println("File created: " + file.getName());
         else
            System.out.println("Updated existing file: " + file.getName());
            
         BufferedWriter br = new BufferedWriter(new FileWriter(file));
         StringBuilder sb = new StringBuilder();
         for(int[] array : output){
            for(int elem : array){
               sb.append(elem);
               sb.append(",");
            }
            sb.append("\n");
         }
         br.write(sb.toString());
         br.close();
         
      } catch(IOException e) {
         System.out.println("An error has occured.");
         e.printStackTrace();
      }
   }
   
   public void printToCSV(double[][] output, String filename){
      try {
         File file = new File(filename);
         if(file.createNewFile())
            System.out.println("File created: " + file.getName());
         else
            System.out.println("Updated existing file: " + file.getName());
            
         BufferedWriter br = new BufferedWriter(new FileWriter(file));
         StringBuilder sb = new StringBuilder();
         for(double[] array : output){
            for(double elem : array){
               sb.append(elem);
               sb.append(",");
            }
            sb.append("\n");
         }
         br.write(sb.toString());
         br.close();
         
      } catch(IOException e) {
         System.out.println("An error has occured.");
         e.printStackTrace();
      }
   }
   
   public void printToCSV(String[][] output, String filename){
      try {
         File file = new File(filename);
         if(file.createNewFile())
            System.out.println("File created: " + file.getName());
         else
            System.out.println("Updated existing file: " + file.getName());
            
         BufferedWriter br = new BufferedWriter(new FileWriter(file));
         StringBuilder sb = new StringBuilder();
         for(String[] array : output){
            for(String elem : array){
               sb.append(elem);
               sb.append(",");
            }
            sb.append("\n");
         }
         br.write(sb.toString());
         br.close();
         
      } catch(IOException e) {
         System.out.println("An error has occured.");
         e.printStackTrace();
      }
   }
   
   public void printMatrix(int[][] output){
      for(int row = 0; row < output.length; row++){
         for(int col = 0; col < output[row].length; col++){
            System.out.print(output[row][col] + " ");
         }
         System.out.println();
      }
   }
}