// This file contains definition for an enemy
// An enemy will randomly move in a random direction

public class Enemy{
  float xPos;
  float yPos;
  float xSize;
  float ySize;
  color enemyColor;
  int enemyStep = 10;
  
  public Enemy(float xP, float yP, float xS, float yS, String clr){
    xPos = xP;
    yPos = yP;
    xSize = xS;
    ySize = yS;
    enemyColor = createColor(clr);
  }
}
