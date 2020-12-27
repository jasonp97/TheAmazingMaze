// This file contains definition for my character

public class Player{
  float xPos;
  float yPos;
  float xSize;
  float ySize;
  color playerColor;
  int playerStep = 10;
  
  public Player(float xP, float yP, float xS, float yS, String clr){
    xPos = xP;
    yPos = yP;
    xSize = xS;
    ySize = yS;
    playerColor = createColor(clr);
  }
}
