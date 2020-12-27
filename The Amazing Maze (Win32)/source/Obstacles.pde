// This file contains definition for a block of obstacle

public class Obstacles{
  float xPos;
  float yPos;
  float xSize;
  float ySize;
  color obsColor;
  
  public Obstacles(float xP, float yP, float xS, float yS, String clr){
    xPos = xP;
    yPos = yP;
    xSize = xS;
    ySize = yS;
    obsColor = createColor(clr);
  }
}
