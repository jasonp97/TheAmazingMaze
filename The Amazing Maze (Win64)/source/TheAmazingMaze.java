import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import g4p_controls.*; 
import java.awt.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class TheAmazingMaze extends PApplet {




PGraphics gameView, livesView, pointsView, timeView;

int currentPoint;
int lives;
boolean lose, win;
int counter;  // This counter is used to count when to update enemies position
int numOfEnemies = 100;
int numOfPoints = 100;
Obstacles wall;
Obstacles border;
Player player;
PImage star, colonTime, wonBanner, loseBanner;
PImage fullHeart, emptyHeart;
PImage zeroTime, oneTime, twoTime, threeTime, fourTime, fiveTime, sixTime, sevenTime, eightTime, nineTime;
PImage zeroPoint, onePoint, twoPoint, threePoint, fourPoint, fivePoint, sixPoint, sevenPoint, eightPoint, ninePoint;
PImage[] pointsArr;
PImage[] timeArr;
int[][] mapArr;
Enemy[] enemies;
Point[] points;
float xRespawnPlayer = 30;
float yRespawnPlayer = 50;
int xDest = 0;  // x-coord of destination
int yDest = 0;  // y-coord of destination
GWindow infoWindow;
String mapName = "Map1.txt";

int timeStart;
int timeElapsed;

public void setup(){
  
  noStroke();
  //ellipseMode(CORNER);
  
  lose = false;
  win = false;
  currentPoint = 0;
  lives = 3;
  counter = 0;
  
  mapArr = new int[100][100];  // Creates a 2D array to store the map data
  enemies = new Enemy[numOfEnemies];    // Creates 100 enemies
  points = new Point[numOfPoints];    // Creates 100 point stars
  
  star = loadImage("./data/Images/Star/star.png");
  star.resize(10,0);
  
  fullHeart = loadImage("./data/Images/Hearts/fullHeart.png");
  emptyHeart = loadImage("./data/Images/Hearts/emptyHeart.png");
  fullHeart.resize(50,0);
  emptyHeart.resize(50,0);
  
  wonBanner = loadImage("./data/Images/Symbols/won.png");
  loseBanner = loadImage("./data/Images/Symbols/lose.png");
  
  wall = new Obstacles(0,0,10,10,"brown");
  border = new Obstacles(0,0,10,10,"blue");
  player = new Player(xRespawnPlayer,yRespawnPlayer,10,10,"white");
  
  createGUI();
  surface.setTitle("The Amazing Maze");  // Set title for the main window
  
  gameView = new PGraphics();
  gameView = GameView.getGraphics();
  gameView.ellipseMode(CORNER);
  
  livesView = new PGraphics();
  livesView = LivesView.getGraphics();
  
  pointsView = new PGraphics();
  pointsView = PointsView.getGraphics();
  
  timeView = new PGraphics();
  timeView = TimeView.getGraphics();
  
  loadPointsImages();
  loadTimeImages();
  
  createMap(mapName);
  createEnemies();
  createPointsStars();
  
  timeStart = millis();  // Starts timing the game
}

public void draw(){
  background(200);
  drawGameView(gameView);
  drawHearts(livesView);
  drawPoints(pointsView);
  drawTime(timeView);
   
}

public void drawGameView(PGraphics view){
  view.beginDraw();
  view.noStroke();
  view.background(0);
  
  // The canvas will stop drawing when player hits an enemy (and not won yet)
  if(!lose && !win){
    view.imageMode(CORNER);
    drawMap(view);
    drawPlayer(view);
    drawEnemies(view);
    drawPointsStars(view);
  
    checkCollision();
  
    if(counter > 100000){
      counter = 0;
    }
    if(counter%5 == 0){
      updateEnemyPosition();  // This function will be called each (1000/60)*5 = 83 ms
    }
  
    counter++;
  } else if (win){
    // If player has won
    // Draw "You Won" banner
    view.imageMode(CENTER);
    view.image(wonBanner, view.width/2, view.height/2);
  } else if (lose){
    // Player has lose
    // Draw "You Lose" banner
    view.imageMode(CENTER);
    view.image(loseBanner, view.width/2, view.height/2);
  }
  
  view.endDraw();
}

public void drawHearts(PGraphics view){
  view.beginDraw();
  view.background(200);
  if(lives == 3){
    view.image(fullHeart,12,0);
    view.image(fullHeart,74,0);
    view.image(fullHeart,136,0);
  } else if (lives == 2){
    view.image(fullHeart,12,0);
    view.image(fullHeart,74,0);
    view.image(emptyHeart,136,0);
  } else if (lives == 1){
    view.image(fullHeart,12,0);
    view.image(emptyHeart,74,0);
    view.image(emptyHeart,136,0);
  } else if (lives == 0){
    view.image(emptyHeart,12,0);
    view.image(emptyHeart,74,0);
    view.image(emptyHeart,136,0);
  }
  view.endDraw();
}

public void drawPoints(PGraphics view){
  if(!lose && !win){
    view.beginDraw();
    view.background(200);
    int hundreds = 0;  // 1xx
    int tens = 0;      // x1x
    int ones = 0;      // xx1
    hundreds = currentPoint/100;
    tens = (currentPoint - hundreds*100)/10;
    ones = currentPoint%10;
    view.image(pointsArr[hundreds], 12, 0);
    view.image(pointsArr[tens], 74, 0);
    view.image(pointsArr[ones], 136, 0);
    view.endDraw();
  }  
}

public void loadPointsImages(){
  // This function instantiates point images
  pointsArr = new PImage[10];
  
  zeroPoint = loadImage("./data/Images/Numbers/zeroPoint.png");  
  onePoint = loadImage("./data/Images/Numbers/onePoint.png");
  twoPoint = loadImage("./data/Images/Numbers/twoPoint.png");
  threePoint = loadImage("./data/Images/Numbers/threePoint.png");
  fourPoint = loadImage("./data/Images/Numbers/fourPoint.png");
  fivePoint = loadImage("./data/Images/Numbers/fivePoint.png");
  sixPoint = loadImage("./data/Images/Numbers/sixPoint.png");
  sevenPoint = loadImage("./data/Images/Numbers/sevenPoint.png");
  eightPoint = loadImage("./data/Images/Numbers/eightPoint.png");
  ninePoint = loadImage("./data/Images/Numbers/ninePoint.png");
  
  zeroPoint.resize(50,0);
  onePoint.resize(50,0);
  twoPoint.resize(50,0);
  threePoint.resize(50,0);
  fourPoint.resize(50,0);
  fivePoint.resize(50,0);
  sixPoint.resize(50,0);
  sevenPoint.resize(50,0);
  eightPoint.resize(50,0);
  ninePoint.resize(50,0);
  
  PImage[] tempPointsArr = {zeroPoint, onePoint, twoPoint, threePoint, fourPoint, fivePoint, sixPoint, sevenPoint, eightPoint, ninePoint};
  pointsArr = tempPointsArr;  
}

public void loadTimeImages(){
  // This function instantiates time images
  timeArr = new PImage[10];
  
  colonTime = loadImage("./data/Images/Symbols/colon.png");
  
  zeroTime = loadImage("./data/Images/Numbers/zeroTime.png");  
  oneTime = loadImage("./data/Images/Numbers/oneTime.png");
  twoTime = loadImage("./data/Images/Numbers/twoTime.png");
  threeTime = loadImage("./data/Images/Numbers/threeTime.png");
  fourTime = loadImage("./data/Images/Numbers/fourTime.png");
  fiveTime = loadImage("./data/Images/Numbers/fiveTime.png");
  sixTime = loadImage("./data/Images/Numbers/sixTime.png");
  sevenTime = loadImage("./data/Images/Numbers/sevenTime.png");
  eightTime = loadImage("./data/Images/Numbers/eightTime.png");
  nineTime = loadImage("./data/Images/Numbers/nineTime.png");
  
  colonTime.resize(40,0);
  
  zeroTime.resize(40,0);
  oneTime.resize(40,0);
  twoTime.resize(40,0);
  threeTime.resize(40,0);
  fourTime.resize(40,0);
  fiveTime.resize(40,0);
  sixTime.resize(40,0);
  sevenTime.resize(40,0);
  eightTime.resize(40,0);
  nineTime.resize(40,0);
  
  PImage[] tempTimeArr = {zeroTime, oneTime, twoTime, threeTime, fourTime, fiveTime, sixTime, sevenTime, eightTime, nineTime};
  timeArr = tempTimeArr;
}

public void drawTime(PGraphics view){
  if(!lose && !win){
    view.beginDraw();
    view.background(200);
    int minutes = 0;
    int seconds = 0;
  
    int minuteTens = 0;  // 1x:xx
    int minuteOnes = 0;  // x1:xx
    int secondTens = 0;  // xx:1x
    int secondOnes = 0;  // xx:x1
  
    timeElapsed = millis() - timeStart;  // Time ellapsed in milliseconds
    minutes = timeElapsed/(1000*60);    // Get the corresponding minutes
    seconds = (timeElapsed/1000)%60;    // Get the corresponding seconds
  
    minuteTens = minutes/10;
    minuteOnes = minutes%10;
    secondTens = seconds/10;
    secondOnes = seconds%10;
  
    view.image(timeArr[minuteTens], 0, 0);
    view.image(timeArr[minuteOnes], 40, 0);
    view.image(colonTime, 80, 0);
    view.image(timeArr[secondTens], 120, 0);
    view.image(timeArr[secondOnes], 160, 0);
    view.endDraw();
  } 
}

public void createPointsStars(){
  // Initalizes all points stars and randomly places them in the map
  for(int i = 0; i < numOfPoints; i++){
    boolean OK = false;
    float xPoint = 0.0f;
    float yPoint = 0.0f;
    int iPoint = 0;
    int jPoint = 0;
    while(!OK){
      xPoint = ((int)random(0,100))*10;
      yPoint = ((int)random(0,100))*10;
      iPoint = (int)(yPoint/10);
      jPoint = (int)(xPoint/10);
      if(mapArr[iPoint][jPoint] == 0){
        OK = true;
      }
    }
    points[i] = new Point(xPoint, yPoint, 10, 10);       
  }
}

public void drawPointsStars(PGraphics view){
  for(int i = 0; i < points.length; i++){
    view.image(star,points[i].xPos,points[i].yPos);
  }
}

public void checkCollectPoint(){
  // This function checks if the player has collected any point star
  boolean collectPoint = false;
  for(int i = 0; i < points.length; i++){
    if(points[i].xPos == player.xPos && points[i].yPos == player.yPos){
      collectPoint = true;
      // Also removes that point star from the map
      points[i].xPos = -10;  // We actually set its position outside of the map :))
      points[i].yPos = -10;
      break;
    }
  }
  
  if(collectPoint){
    currentPoint++;
    // Display the point in PointView    
  }
}

public void checkCollision(){
  // This function checks if the player has touched any enemy in the maze
  // I think this function should return boolean value indicates whether the collision has happened
  boolean collideEnemy = false;
  
  // Go through the enemy list to check an enemy position compared to the player's position
  for(int i = 0; i < enemies.length; i++){
    if(enemies[i].xPos == player.xPos && enemies[i].yPos == player.yPos){
      collideEnemy = true;
      // Brings player back to respawn point
      player.xPos = xRespawnPlayer;
      player.yPos = yRespawnPlayer;
      break;
    }
  }
  
  if(collideEnemy){
    lives--;
    if(lives <= 0){
      lose = true;
    }    
  }
}

public void checkWin(){
  // This function checks if the current player position is the destination point
  if(player.xPos == xDest && player.yPos == yDest){
    win = true;
    println("You won");
    //gameView.image(wonBanner, 0, 0);
  }
}

public void updateEnemyPosition(){
  int iE;    // Index i of enemy
  int jE;    // Index j of enemy
  // Update every single enemy in the list of enemies
  /* Enemy direction: 
     1: UP
     2: LEFT
     3: DOWN
     4: RIGHT
  */
  int eDirection = 0; 
  for(int i = 0; i < enemies.length; i++){
    eDirection = (int)random(1,5);  // Random the direction
    // Take the current position of the enemy
    iE = (int)(enemies[i].yPos/10);
    jE = (int)(enemies[i].xPos/10);
    if(eDirection == 1){
      // Move Up      
      if(mapArr[iE-1][jE] == 0){
        enemies[i].yPos -= enemies[i].enemyStep;
      }
    } else if (eDirection == 2){
      // Move Left      
      if(mapArr[iE][jE-1] == 0){
        enemies[i].xPos -= enemies[i].enemyStep;
      }
    } else if (eDirection == 3){
      // Move Down
      if(mapArr[iE+1][jE] == 0){
        enemies[i].yPos += enemies[i].enemyStep;
      }
    } else if (eDirection == 4){
      // Move Right
      if(mapArr[iE][jE+1] == 0){
        enemies[i].xPos += enemies[i].enemyStep;
      }
    }
  }
}

public void createMap(String txtMap){
  String[] lines = loadStrings("./data/Map/" + txtMap);  // Reads text file
  if(lines == null){
    println("No file found");
  }
  else {
    for (int i = 0 ; i < lines.length; i++) {      
      for(int j = 0; j < lines[i].length() ; j++){        
        if(lines[i].charAt(j) == '#'){
          // Border
          mapArr[i][j] = 2;
        }
        else if (lines[i].charAt(j) == '1'){
          // Wall
          mapArr[i][j] = 1;
        }
        else if (lines[i].charAt(j) == '*'){
          // Destination
          mapArr[i][j] = -1;
          
          //Store the position of destination to check win later on
          xDest = j*10;
          yDest = i*10;
        }
      }      
    }
  }
}

public void createEnemies(){
  // Initalizes all enemies and randomly places them in the map
  for(int i = 0; i < numOfEnemies; i++){
    boolean OK = false;
    float xE = 0.0f;
    float yE = 0.0f;
    int iE = 0;
    int jE = 0;
    while(!OK){
      xE = ((int)random(0,100))*10;
      yE = ((int)random(0,100))*10;
      iE = (int)(yE/10);
      jE = (int)(xE/10);
      if(mapArr[iE][jE] == 0){
        OK = true;
      }
    }
    enemies[i] = new Enemy(xE,yE,10,10,"blue");    
  }
}

public void drawMap(PGraphics view){
  for (int i = 0 ; i < 100; i++) {    // Index i from 0 to 99
      //println(lines.length);
      for(int j = 0; j < 100 ; j++){  // Index j from 0 to 99        
        if(mapArr[i][j] == 2){
          // Border          
          view.fill(border.obsColor);
          view.rect(border.xPos+j*10, border.yPos+i*10, border.xSize, border.ySize);
        }
        else if (mapArr[i][j] == 1){
          // Wall          
          view.fill(wall.obsColor);
          view.rect(wall.xPos+j*10, wall.yPos+i*10, wall.xSize, wall.ySize);
        }
        else if (mapArr[i][j] == -1){
          // Destination
          view.fill(0xff15fa11);
          view.rect(wall.xPos+j*10, wall.yPos+i*10, wall.xSize, wall.ySize);
        }
      }      
    }
}

public void drawPlayer(PGraphics view){
  view.fill(player.playerColor);
  view.ellipse(player.xPos, player.yPos, player.xSize, player.ySize);
}

public void drawEnemies(PGraphics view){
  for(int i = 0; i < enemies.length; i++){
    view.fill(enemies[i].enemyColor);
    view.ellipse(enemies[i].xPos, enemies[i].yPos, enemies[i].xSize, enemies[i].ySize);
  }
}

public void resetGame(){
  // This function resets everything to start a new game
  lose = false;
  win = false;
  currentPoint = 0;
  lives = 3;
  counter = 0;
  
  createMap(mapName);
  createEnemies();
  createPointsStars();
  
  // Brings player back to respawn point
  player.xPos = xRespawnPlayer;
  player.yPos = yRespawnPlayer;
  
  timeStart = millis();  // Starts timing the game
}

// Player controls
public void keyPressed(){
  int iPlayer = (int)(player.yPos/10);  // Index i of player
  int jPlayer = (int)(player.xPos/10);  // Index j of player
  if (key == CODED) {
    if (keyCode == UP) {
      if(mapArr[iPlayer-1][jPlayer] == 0 || mapArr[iPlayer-1][jPlayer] == -1){
        player.yPos -= player.playerStep;
      }      
    } else if (keyCode == DOWN) {
      if(mapArr[iPlayer+1][jPlayer] == 0 || mapArr[iPlayer+1][jPlayer] == -1){
        player.yPos += player.playerStep;
      }      
    } else if (keyCode == LEFT) {
      if(mapArr[iPlayer][jPlayer-1] == 0 || mapArr[iPlayer][jPlayer-1] == -1){
        player.xPos -= player.playerStep;
      }      
    } else if (keyCode == RIGHT) {
      if(mapArr[iPlayer][jPlayer+1] == 0 || mapArr[iPlayer][jPlayer+1] == -1){
        player.xPos += player.playerStep;
      }     
    }   
  }
  checkCollectPoint();
  checkWin();
}

public int createColor(String desc){
  int c = 0xffffffff;    // Default is White
  if("green".equals(desc)){
    c = 0xff00ff0d;
  } else if ("red".equals(desc)) {
    c = 0xffff0000;
  } else if ("orange".equals(desc)) {
    c = 0xffffbb00;
  } else if ("blue".equals(desc)){
    c = 0xff0400ff;
  } else if ("pink".equals(desc)){
    c = 0xfffb00ff;
  } else if ("cyan".equals(desc)){
    c = 0xff00f7ff;
  } else if ("yellow".equals(desc)){
    c = 0xffddff00;
  } else if ("purple".equals(desc)){
    c = 0xffaa00ff;
  } else if ("brown".equals(desc)){
    c = 0xffa52a2a;
  }
  return c;
}

public void openInfoWindow() {
  String title = "Info";
  int x = width/2;
  int y = height/2;
  int w = 500;
  int h = 250;
  infoWindow = GWindow.getWindow(this, title, x, y, w, h, JAVA2D);
  infoWindow.addDrawHandler(this, "infoWindowDraw");
  //infoWindow.image(wonBanner, 0, 0);
}

public void infoWindowDraw(PApplet info, GWinData data) {
  info.background(0);
  info.textSize(15);
  float xText = info.width/2;  // x-coord of the info text
  float yText = 15;            // y-coord of the info text
  info.textAlign(CENTER);
  String[] lines = loadStrings("./data/Info/Info.txt");  // Reads info file
  if(lines == null){
    println("No file found");
  }
  else {
    for (int i = 0 ; i < lines.length; i++) {
      info.text(lines[i], xText, yText + i*15);
    }
  }
}
// This file contains definition for an enemy
// An enemy will randomly move in a random direction

public class Enemy{
  float xPos;
  float yPos;
  float xSize;
  float ySize;
  int enemyColor;
  int enemyStep = 10;
  
  public Enemy(float xP, float yP, float xS, float yS, String clr){
    xPos = xP;
    yPos = yP;
    xSize = xS;
    ySize = yS;
    enemyColor = createColor(clr);
  }
}
// This file contains definition for a block of obstacle

public class Obstacles{
  float xPos;
  float yPos;
  float xSize;
  float ySize;
  int obsColor;
  
  public Obstacles(float xP, float yP, float xS, float yS, String clr){
    xPos = xP;
    yPos = yP;
    xSize = xS;
    ySize = yS;
    obsColor = createColor(clr);
  }
}
// This file contains definition for my character

public class Player{
  float xPos;
  float yPos;
  float xSize;
  float ySize;
  int playerColor;
  int playerStep = 10;
  
  public Player(float xP, float yP, float xS, float yS, String clr){
    xPos = xP;
    yPos = yP;
    xSize = xS;
    ySize = yS;
    playerColor = createColor(clr);
  }
}
// This file contains definition for a point to be collected (a star)

public class Point{
  float xPos;
  float yPos;
  float xSize;
  float ySize;
  
  public Point(float xP, float yP, float xS, float yS){
    xPos = xP;
    yPos = yP;
    xSize = xS;
    ySize = yS;
  }
}
/* =========================================================
 * ====                   WARNING                        ===
 * =========================================================
 * The code in this tab has been generated from the GUI form
 * designer and care should be taken when editing this file.
 * Only add/edit code inside the event handlers i.e. only
 * use lines between the matching comment tags. e.g.

 void myBtnEvents(GButton button) { //_CODE_:button1:12356:
     // It is safe to enter your event code here  
 } //_CODE_:button1:12356:
 
 * Do not rename this tab!
 * =========================================================
 */

public void btnExit_click(GButton source, GEvent event) { //_CODE_:btnExit:967411:
  exit();
} //_CODE_:btnExit:967411:

public void btnInfo_click(GButton source, GEvent event) { //_CODE_:btnInfo:828136:
  openInfoWindow();
} //_CODE_:btnInfo:828136:

public void btnNewGame_click(GButton source, GEvent event) { //_CODE_:btnNewGame:852079:
  // Reset the whole canvas
  resetGame();
} //_CODE_:btnNewGame:852079:



// Create all the GUI controls. 
// autogenerated do not edit
public void createGUI(){
  G4P.messagesEnabled(false);
  G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
  G4P.setMouseOverEnabled(false);
  surface.setTitle("Sketch Window");
  GameView = new GView(this, 200, 0, 1000, 1000, JAVA2D);
  label_lives = new GLabel(this, 50, 18, 100, 50);
  label_lives.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
  label_lives.setText("LIVES");
  label_lives.setOpaque(false);
  label1 = new GLabel(this, 50, 240, 100, 50);
  label1.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
  label1.setText("POINTS");
  label1.setOpaque(false);
  label2 = new GLabel(this, 50, 468, 100, 50);
  label2.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
  label2.setText("TIME");
  label2.setOpaque(false);
  btnExit = new GButton(this, 50, 930, 100, 50);
  btnExit.setText("EXIT");
  btnExit.setLocalColorScheme(GCScheme.RED_SCHEME);
  btnExit.addEventHandler(this, "btnExit_click");
  LivesView = new GView(this, 0, 96, 200, 50, JAVA2D);
  PointsView = new GView(this, 0, 300, 200, 100, JAVA2D);
  TimeView = new GView(this, 0, 552, 200, 100, JAVA2D);
  btnInfo = new GButton(this, 50, 850, 100, 50);
  btnInfo.setText("INFO");
  btnInfo.addEventHandler(this, "btnInfo_click");
  btnNewGame = new GButton(this, 50, 770, 100, 50);
  btnNewGame.setText("NEW GAME");
  btnNewGame.setLocalColorScheme(GCScheme.YELLOW_SCHEME);
  btnNewGame.addEventHandler(this, "btnNewGame_click");
}

// Variable declarations 
// autogenerated do not edit
GView GameView; 
GLabel label_lives; 
GLabel label1; 
GLabel label2; 
GButton btnExit; 
GView LivesView; 
GView PointsView; 
GView TimeView; 
GButton btnInfo; 
GButton btnNewGame; 
  public void settings() {  size(1200,1000); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "TheAmazingMaze" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
