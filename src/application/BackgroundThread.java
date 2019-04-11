package application;

import java.lang.Thread;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class BackgroundThread extends Thread{

	private boolean terminate;
	private int score = 0;
	private double boundsHeight;
	private double boundsWidth;
	private double scale;
	private double[] offsets = new double[2];
	private int[][] layout;
	private double[] pacmanStart = {13.5, 23};
	private Circle[][] grid;
	private Sprite pacman;
	private Sprite[] ghosts;
	private Pane parent;
	private String nextDirection = "null";
	private boolean nextLevel = false;
	private Text scoreDisp;
	private Text levelDisp;
	private long ghostTimingCounter = 0;
	private int ghostsInQue = 3;
	private Sprite[] ghostsQue = new Sprite[3];
	private int powerPelletCounter = 0;
	private int queDirection = 1;
	private int levelCounter = 1;
	private int levelTimingCounter = 0;
	private boolean started = false;
	private boolean death = false;
	private int deathTimingCounter = 0;
	private int lives = 3;
	private Sprite[] livesDisp = new Sprite[8];
	private Text messages = new Text(" ");
	private Text popup = new Text(" ");
	private int popupCounter = 0;
	private boolean gameOver = false;
	private boolean eatGhosts = false;
	private int scaredTimingCounter = 0;
	private int numGhostsEaten = 0;
	private int ghostEaten = -1;
	private Image[] scared = new Image[2];
	private Image eyes;
	private boolean paused = false;
	private Stage stage;
	private Text[] initials = new Text[3];
	private int currentInitial = 0;
	private boolean setScore = false;
	private int scoreLoc = 10;
	private String scores[][] = new String[scoreLoc][2];
	private String highscoreFile = "resources/pacman/highscore.txt";

	/*
		Wall key
			ctr = corner top to right
			ctl = corner top to left
			cbr = corner bottom to right
			cbl = corner bottom to left
			how = horizontal wall
			vew = vertical wall
			"   " = no wall
			ggt = ghost gate
			gvw = ghost vertical wall
			ght = ghost horizontal top
			ghb = ghost horizontal bottom
			gtr = ghost corner top to right
			gtl = ghost corner top to left
			gbr = ghost corner bottom to right
			gbl = ghost corner bottom to left			
	 */
	private String[][] wall = {
			{"cbr","how","how","how","how","how","how","how","how","how","how","how","how","cbl","cbr","how","how","how","how","how","how","how","how","how","how","how","how","cbl"},
			{"vew","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","vew","vew","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","vew"},
			{"vew","   ","cbr","how","how","cbl","   ","cbr","how","how","how","cbl","   ","vew","vew","   ","cbr","how","how","how","cbl","   ","cbr","how","how","cbl","   ","vew"},
			{"vew","   ","vew","   ","   ","vew","   ","vew","   ","   ","   ","vew","   ","vew","vew","   ","vew","   ","   ","   ","vew","   ","vew","   ","   ","vew","   ","vew"},
			{"vew","   ","ctr","how","how","ctl","   ","ctr","how","how","how","ctl","   ","ctr","ctl","   ","ctr","how","how","how","ctl","   ","ctr","how","how","ctl","   ","vew"},
			{"vew","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","vew"},
			{"vew","   ","cbr","how","how","cbl","   ","cbr","cbl","   ","cbr","how","how","how","how","how","how","cbl","   ","cbr","cbl","   ","cbr","how","how","cbl","   ","vew"},
			{"vew","   ","ctr","how","how","ctl","   ","vew","vew","   ","ctr","how","how","cbl","cbr","how","how","ctl","   ","vew","vew","   ","ctr","how","how","ctl","   ","vew"},
			{"vew","   ","   ","   ","   ","   ","   ","vew","vew","   ","   ","   ","   ","vew","vew","   ","   ","   ","   ","vew","vew","   ","   ","   ","   ","   ","   ","vew"},
			{"ctr","how","how","how","how","cbl","   ","vew","ctr","how","how","cbl","   ","vew","vew","   ","cbr","how","how","ctl","vew","   ","cbr","how","how","how","how","ctl"},
			{"   ","   ","   ","   ","   ","vew","   ","vew","cbr","how","how","ctl","   ","ctr","ctl","   ","ctr","how","how","cbl","vew","   ","vew","   ","   ","   ","   ","   "},
			{"   ","   ","   ","   ","   ","vew","   ","vew","vew","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","vew","vew","   ","vew","   ","   ","   ","   ","   "},
			{"   ","   ","   ","   ","   ","vew","   ","vew","vew","   ","gbr","ght","ght","ggt","ggt","ght","ght","gbl","   ","vew","vew","   ","vew","   ","   ","   ","   ","   "},
			{"how","how","how","how","how","ctl","   ","ctr","ctl","   ","gvl","   ","   ","   ","   ","   ","   ","gvr","   ","ctr","ctl","   ","ctr","how","how","how","how","how"},
			{"   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","gvl","   ","   ","   ","   ","   ","   ","gvr","   ","   ","   ","   ","   ","   ","   ","   ","   ","   "},
			{"how","how","how","how","how","cbl","   ","cbr","cbl","   ","gvl","   ","   ","   ","   ","   ","   ","gvr","   ","cbr","cbl","   ","cbr","how","how","how","how","how"},
			{"   ","   ","   ","   ","   ","vew","   ","vew","vew","   ","gtr","ghb","ghb","ghb","ghb","ghb","ghb","gtl","   ","vew","vew","   ","vew","   ","   ","   ","   ","   "},
			{"   ","   ","   ","   ","   ","vew","   ","vew","vew","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","vew","vew","   ","vew","   ","   ","   ","   ","   "},
			{"   ","   ","   ","   ","   ","vew","   ","vew","vew","   ","cbr","how","how","how","how","how","how","cbl","   ","vew","vew","   ","vew","   ","   ","   ","   ","   "},
			{"cbr","how","how","how","how","ctl","   ","ctr","ctl","   ","ctr","how","how","cbl","cbr","how","how","ctl","   ","ctr","ctl","   ","ctr","how","how","how","how","cbl"},
			{"vew","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","vew","vew","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","vew"},
			{"vew","   ","cbr","how","how","cbl","   ","cbr","how","how","how","cbl","   ","vew","vew","   ","cbr","how","how","how","cbl","   ","cbr","how","how","cbl","   ","vew"},
			{"vew","   ","ctr","how","cbl","vew","   ","ctr","how","how","how","ctl","   ","ctr","ctl","   ","ctr","how","how","how","ctl","   ","vew","cbr","how","ctl","   ","vew"},
			{"vew","   ","   ","   ","vew","vew","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","vew","vew","   ","   ","   ","vew"},
			{"ctr","how","cbl","   ","vew","vew","   ","cbr","cbl","   ","cbr","how","how","how","how","how","how","cbl","   ","cbr","cbl","   ","vew","vew","   ","cbr","how","ctl"},
			{"cbr","how","ctl","   ","ctr","ctl","   ","vew","vew","   ","ctr","how","how","cbl","cbr","how","how","ctl","   ","vew","vew","   ","ctr","ctl","   ","ctr","how","cbl"},
			{"vew","   ","   ","   ","   ","   ","   ","vew","vew","   ","   ","   ","   ","vew","vew","   ","   ","   ","   ","vew","vew","   ","   ","   ","   ","   ","   ","vew"},
			{"vew","   ","cbr","how","how","how","how","ctl","ctr","how","how","cbl","   ","vew","vew","   ","cbr","how","how","ctl","ctr","how","how","how","how","cbl","   ","vew"},
			{"vew","   ","ctr","how","how","how","how","how","how","how","how","ctl","   ","ctr","ctl","   ","ctr","how","how","how","how","how","how","how","how","ctl","   ","vew"},
			{"vew","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","   ","vew"},
			{"ctr","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","how","ctl"},
	};


	@Override
	public void run() {
		//		lastMazeEvent = System.currentTimeMillis();
		while(!terminate && !gameOver) {
			long curTime = System.currentTimeMillis();
			if(!paused) {
				if(pacman.checkMovementUpdate(curTime)) {
					this.blinkPowerPellets();
					this.queGhosts();
					this.pacmanMoves();
					this.advanceLevel();
					this.detectCollision();
					this.scaredGhosts();
					this.manageDeath();
					this.clearPopup();
					pacman.animateSprite();
					pacman.moveSprite(boundsWidth, boundsHeight, scale);
					for(int i = 0; i < 4; i++) {
						this.manageGhostsMove(ghosts[i]);
						ghosts[i].moveSprite(boundsWidth, boundsHeight, scale);
					}
				}
			}
		}
		if(!terminate) {
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for(int i = 0; i < parent.getChildren().size(); i++) {
				parent.getChildren().get(i).setVisible(false);
			}
			this.loadSetScorePage();

			while(!terminate){
				long curTime = System.currentTimeMillis();
				if(pacman.checkMovementUpdate(curTime)) {
					if(setScore) {
						this.blinkText();
					}
				}
			}
		}
	}

	private void readHighScores() {
		try {
			File file = new File(highscoreFile);
			Scanner reader = new Scanner(file);
			for(int i = 0; i < scores.length; i++) {
				String tmp = reader.nextLine();
				scores[i][0] = tmp.substring(0, 3);
				scores[i][1] = tmp.substring(4);
			}
			reader.close();
		}catch(FileNotFoundException e) {
			System.out.printf("%s not found\nAttempting to create it\n", highscoreFile);
			for(int i = 0; i < scores.length; i++) {
				scores[i][0] = "AAA";
				scores[i][1] = "0";		
			}
			saveScore();
		}
	}
	
	private void loadSetScorePage() {
		int length = 1;
		stage.setWidth(300);
		stage.setHeight(400);
		this.messages.setVisible(true);
		messages.setText("Score: " + Integer.toString(score));
		messages.setY(20);
		messages.setX(150 - messages.getLayoutBounds().getWidth()/2.);
		this.scoreDisp.setVisible(true);
		scoreDisp.setText("");
		scoreDisp.setFill(Color.BLUE);
		scoreDisp.setFont(Font.font("monospace", FontWeight.BOLD, 15));
		readHighScores();
		for(int i = scores.length - 1; i >= 0; i--) {
			if(scores[i][1].length() > length) {
				length = scores[i][1].length();
			}
			if(score > Integer.parseInt(scores[i][1])) {
				scoreLoc -= 1;
				setScore = true;
			}
		}
		if(setScore) {
			scoreDisp.setY(100);
		}else {
			scoreDisp.setY(50);
		}
		for(int i = 0; i < scores.length; i++) {
			String spaces = ".  ";
			if(i > 8) {
				spaces = ". "; 
			}
			scoreDisp.setText(scoreDisp.getText() + (i+1) + spaces + scores[i][0] + " ");
			for(int n = -1; n <= length-scores[i][1].length(); n++) {
				scoreDisp.setText(scoreDisp.getText() + " ");
			}
			scoreDisp.setText(scoreDisp.getText() + scores[i][1] + "\n");
		}
		scoreDisp.setX(150 - scoreDisp.getLayoutBounds().getWidth()/2.);
		for(int i = 0; i < initials.length && setScore; i++) {
			initials[i].setText("A");
			initials[i].setFont(Font.font("Sans", FontWeight.BOLD, 20));
			initials[i].setFill(Color.BLUE);
			initials[i].setY(60);
			initials[i].setX(150 - initials[i].getLayoutBounds().getWidth()/2. + 22*(i-1));
			initials[i].setVisible(true);
		}
	}
	
	private void saveScore() {
		for(int i = scores.length-1; i >= 0 && setScore; i--) {
			if(scoreLoc <= i) {
				if(i+1 < scores.length)
					scores[i+1] = scores[i].clone();
			}
			if(scoreLoc == i) {
				scores[i][1] = Integer.toString(score);
				String init = "";
				for(int n = 0; n < initials.length; n++) {
					init += initials[n].getText();
				}
				scores[i][0] = init;
			}
		}

		try {
			PrintWriter writeAbleFile = new PrintWriter(new File(highscoreFile));
			for(int i = 0; i < scores.length; i++) {
				writeAbleFile.write(scores[i][0]);
				writeAbleFile.write(",");
				writeAbleFile.write(scores[i][1]);
				writeAbleFile.write("\n");
			}
			writeAbleFile.close();
		} catch (FileNotFoundException e) {
			System.out.printf("Unable to open or create file %s\n", highscoreFile);
		}
	}
	
	private void blinkText() {
		powerPelletCounter++;
		for(int i = 0; i < initials.length; i++) {
			if(i != currentInitial && !initials[i].isVisible())
				initials[i].setVisible(true);
		}
		if(powerPelletCounter > 10) {
			initials[currentInitial].setVisible(!initials[currentInitial].isVisible());
			powerPelletCounter = 0;
		}
	}
	
	public void setSprites(Sprite pacman, Sprite one, Sprite two, Sprite three, Sprite four) {
		ghosts = new Sprite[4];
		this.pacman = pacman;
		ghosts[0] = one;
		ghosts[1] = two;
		ghosts[2] = three;
		ghosts[3] = four;  
		terminate = false;
	}

	public void setupMaze(double xOffSet, double yOffSet, double scale, Pane parent, Stage stage) {
		offsets[0] = xOffSet+(scale/2.);
		offsets[1] = yOffSet+(scale/2.)+scale*.75;
		boundsWidth = 27*scale + offsets[0]+scale;
		boundsHeight = 31*scale + offsets[1]+scale*2.8;

		try {
			scared[0] = new Image(new FileInputStream("resources/pacman/scared0.png"));
			scared[1] = new Image(new FileInputStream("resources/pacman/scared1.png"));
			eyes = new Image(new FileInputStream("resources/pacman/eyes.png"));
		}catch ( FileNotFoundException e) {
			System.out.println("Unable to load Image");;
		}
		levelDisp = new Text("Level 1");
		levelDisp.setY(scale);
		levelDisp.setX(scale/2.);
		levelDisp.setFont(Font.font("sans", FontWeight.BOLD, scale));
		levelDisp.setFill(Color.WHITE);
		levelDisp.setStyle("font-weight: bold");
		
		scoreDisp = new Text(Integer.toString(score));
		scoreDisp.setY(scale);
		scoreDisp.setX((22)*scale);
		scoreDisp.setFont(Font.font("sans", FontWeight.BOLD, scale));
		scoreDisp.setFill(Color.WHITE);

		messages.setText("Ready!\n");
		messages.setY(17*scale+offsets[1]+scale/3.);
		messages.setX(13.5*scale+offsets[0]-(messages.getLayoutBounds().getWidth()/2.));
		messages.setFont(Font.font("sans", FontWeight.BOLD, scale));
		messages.setFill(Color.YELLOW);
		
		popup.setFill(Color.AQUA);
		popup.setFont(Font.font("sans", FontWeight.BOLD, scale*.8));
				
		this.stage = stage;
		this.scale = scale;
		this.parent = parent;
		parent.getChildren().add(scoreDisp);
		parent.getChildren().add(levelDisp);
		parent.getChildren().add(messages);
		for(int i = 0; i < initials.length; i++) {
			initials[i] = new Text("A");
			initials[i].setVisible(false);
			parent.getChildren().add(initials[i]);
		}
		resetGrid();
		this.drawMaze();
		this.drawPellets();
		pacman.setMovementInc(.25*scale);
		parent.getChildren().add(pacman.getSprite());
		for(int i = 0; i < 4; i++) {
			ghosts[i].setMovementInc(.25*scale);
			parent.getChildren().add(ghosts[i].getSprite());			
		}
		this.drawLives();
		parent.getChildren().add(popup);
	}

	private void drawLives() {
		String path = "resources/pacman/";
		for(int i = 0; i < livesDisp.length; i++) {
			livesDisp[i] = new Sprite(5, "pacman", true, path);
			livesDisp[i].setWidth(scale*.8);
			livesDisp[i].setHeight(scale*.8);
			livesDisp[i].denyMove();
			livesDisp[i].setCurrentFrame(3);
			livesDisp[i].setOrientation("left");
			livesDisp[i].setY(layout.length*scale+offsets[1]);
			livesDisp[i].setX((i+1)*scale);
			if(lives-i <= 0)
				livesDisp[i].getSprite().setVisible(false);;
			parent.getChildren().add(livesDisp[i].getSprite());
		}
	}
	
	private void updateLives() {
		for(int i = 0; i < livesDisp.length; i++) {
			if(lives-i <= 0)
				livesDisp[i].getSprite().setVisible(false);
			else
				livesDisp[i].getSprite().setVisible(true);
		}
	}
	
	private void resetGrid() {
		int[][] makeGrid = {
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
				{0,2,2,2,2,2,2,2,2,2,2,2,2,0,0,2,2,2,2,2,2,2,2,2,2,2,2,0},
				{0,2,0,0,0,0,2,0,0,0,0,0,2,0,0,2,0,0,0,0,0,2,0,0,0,0,2,0},
				{0,3,0,0,0,0,2,0,0,0,0,0,2,0,0,2,0,0,0,0,0,2,0,0,0,0,3,0},
				{0,2,0,0,0,0,2,0,0,0,0,0,2,0,0,2,0,0,0,0,0,2,0,0,0,0,2,0},
				{0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,0},
				{0,2,0,0,0,0,2,0,0,2,0,0,0,0,0,0,0,0,2,0,0,2,0,0,0,0,2,0},
				{0,2,0,0,0,0,2,0,0,2,0,0,0,0,0,0,0,0,2,0,0,2,0,0,0,0,2,0},
				{0,2,2,2,2,2,2,0,0,2,2,2,2,0,0,2,2,2,2,0,0,2,2,2,2,2,2,0},
				{0,0,0,0,0,0,2,0,0,0,0,0,1,0,0,1,0,0,0,0,0,2,0,0,0,0,0,0},
				{0,0,0,0,0,0,2,0,0,0,0,0,1,0,0,1,0,0,0,0,0,2,0,0,0,0,0,0},
				{0,0,0,0,0,0,2,0,0,1,1,1,1,1,1,1,1,1,1,0,0,2,0,0,0,0,0,0},
				{0,0,0,0,0,0,2,0,0,1,0,0,0,0,0,0,0,0,1,0,0,2,0,0,0,0,0,0},
				{0,0,0,0,0,0,2,0,0,1,0,0,0,0,0,0,0,0,1,0,0,2,0,0,0,0,0,0},
				{1,1,1,1,1,1,2,1,1,1,0,0,0,0,0,0,0,0,1,1,1,2,1,1,1,1,1,1},
				{0,0,0,0,0,0,2,0,0,1,0,0,0,0,0,0,0,0,1,0,0,2,0,0,0,0,0,0},
				{0,0,0,0,0,0,2,0,0,1,0,0,0,0,0,0,0,0,1,0,0,2,0,0,0,0,0,0},
				{0,0,0,0,0,0,2,0,0,1,1,1,1,1,1,1,1,1,1,0,0,2,0,0,0,0,0,0},
				{0,0,0,0,0,0,2,0,0,1,0,0,0,0,0,0,0,0,1,0,0,2,0,0,0,0,0,0},
				{0,0,0,0,0,0,2,0,0,1,0,0,0,0,0,0,0,0,1,0,0,2,0,0,0,0,0,0},
				{0,2,2,2,2,2,2,2,2,2,2,2,2,0,0,2,2,2,2,2,2,2,2,2,2,2,2,0},
				{0,2,0,0,0,0,2,0,0,0,0,0,2,0,0,2,0,0,0,0,0,2,0,0,0,0,2,0},
				{0,2,0,0,0,0,2,0,0,0,0,0,2,0,0,2,0,0,0,0,0,2,0,0,0,0,2,0},
				{0,3,2,2,0,0,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,0,0,2,2,3,0},
				{0,0,0,2,0,0,2,0,0,2,0,0,0,0,0,0,0,0,2,0,0,2,0,0,2,0,0,0},
				{0,0,0,2,0,0,2,0,0,2,0,0,0,0,0,0,0,0,2,0,0,2,0,0,2,0,0,0},
				{0,2,2,2,2,2,2,0,0,2,2,2,2,0,0,2,2,2,2,0,0,2,2,2,2,2,2,0},
				{0,2,0,0,0,0,0,0,0,0,0,0,2,0,0,2,0,0,0,0,0,0,0,0,0,0,2,0},
				{0,2,0,0,0,0,0,0,0,0,0,0,2,0,0,2,0,0,0,0,0,0,0,0,0,0,2,0},
				{0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,0},
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		};
		layout = makeGrid.clone();	
		this.resetGhosts();
		this.resetPacman();
	}

	private void resetPellets() {
		for(int y = 0; y < layout.length; y++) {
			for(int x = 0; x <layout[y].length; x++) {
				if(layout[y][x] > 1)
					grid[y][x].setFill(Color.WHEAT);
			}
		}
	}
	
	private void detectCollision() {
		int currentLoc[] = {
				(int)Math.round((pacman.getX() - offsets[0])/scale),
				(int)Math.round((pacman.getY() - offsets[1])/scale),
				(int)pacman.getX(),
				(int)pacman.getY()
		};
		for(int i = 0; i < ghosts.length; i++) {
			int ghostsLoc[] = {
					(int)Math.round((ghosts[i].getX() - offsets[0])/scale),
					(int)Math.round((ghosts[i].getY() - offsets[1])/scale),		
					(int)ghosts[i].getX(),
					(int)ghosts[i].getY()
			};
			if((ghostsLoc[0] == currentLoc[0] && ghostsLoc[1] == currentLoc[1])||(ghostsLoc[2] == currentLoc[2] && ghostsLoc[3] == currentLoc[3])) {
				if((ghosts[i].getSprite().getImage() != scared[0] || ghosts[i].getSprite().getImage() != scared[1]) && ghosts[i].isAnimateable()) {
					death = true;
				}else if(ghosts[i].getSprite().getImage() == scared[0] || ghosts[i].getSprite().getImage() == scared[1]){
					ghostEaten = i;
				}
			}
		}
	}
	
	private void manageDeath() {
		if(death) {
			if(deathTimingCounter == 0) {
				for(int i = 0; i < ghosts.length; i++) {
					ghosts[i].getSprite().setVisible(false);
					ghosts[i].denyMove();
				}
			}
			pacman.denyMove();
			deathTimingCounter++;
			if(deathTimingCounter > 20) {
				this.resetGhosts();
				this.resetPacman();
				started = false;
				death = false;
				deathTimingCounter = 0;
				lives--;
				messages.setText("Ready!");
				messages.setX(13.5*scale+offsets[0]-(messages.getLayoutBounds().getWidth()/2.));
				if(lives < 0) {
					pacman.denyMove();
					pacman.getSprite().setVisible(false);
					for(int i = 0; i < ghosts.length; i++) {
						ghosts[i].denyMove();
						ghosts[i].getSprite().setVisible(false);
					}
					gameOver = true;
					messages.setText("Game Over");
					messages.setX(13.5*scale+offsets[0]-(messages.getLayoutBounds().getWidth()/2.));
				}
				this.updateLives();
			}
		}
	}
	
	private void resetPacman() {
		pacman.setX(pacmanStart[0]*scale+offsets[0]);
		pacman.setY(pacmanStart[1]*scale+offsets[1]);
		pacman.setOrientation("right");
		pacman.setCurrentFrame(0);
		pacman.setAnimationInc(1);
		pacman.denyMove();
	}
	
	private void resetGhosts() {
		ghosts[0].setX(13.5*scale+offsets[0]);
		ghosts[0].setY(11*scale+offsets[1]);
		ghosts[0].setOrientation("right");
		ghosts[1].setX(13.5*scale+offsets[0]);
		ghosts[1].setY(14*scale+offsets[1]);
		ghosts[2].setX(11.5*scale+offsets[0]);
		ghosts[2].setY(14*scale+offsets[1]);
		ghosts[3].setX(15.5*scale+offsets[0]);
		ghosts[3].setY(14*scale+offsets[1]);		
		for(int i  = 0; i < ghosts.length; i++) {
			ghosts[i].denyMove();
			ghosts[i].allowAnimate();
			ghosts[i].setMovementInc(.25*scale);
			ghosts[i].setOrientation(ghosts[i].getOrientation());
			ghosts[i].getSprite().setVisible(true);
			ghosts[i].setOrientation("right");
		}
		ghostsQue[0] = ghosts[1];
		ghostsQue[1] = ghosts[2];
		ghostsQue[2] = ghosts[3];
		ghostsInQue = 3;
		ghostTimingCounter = 0;
		eatGhosts = false;
	}

	private void advanceLevel() {
		if(nextLevel) {
			levelTimingCounter++;
			if(levelTimingCounter > 20) {
				levelTimingCounter = 0;
				levelCounter++;
				nextLevel = false;
				nextDirection = "null";
				resetGrid();
				this.resetPellets();
				started = false;
				levelDisp.setText(String.format("Level %d", levelCounter));
				pacman.setMovementSpeed(pacman.getMovementSpeed()-1);
				messages.setText("Ready!");
				messages.setX(13.5*scale+offsets[0]-(messages.getLayoutBounds().getWidth()/2.));
			}
		}
	}
	
	private void scaredGhosts() {
		if(eatGhosts) {
			if(scaredTimingCounter == 0) {
				numGhostsEaten = 0;
				for(int i = 0; i < ghosts.length; i++) {
					if(ghosts[i].getSprite().getImage() != eyes) {
						ghosts[i].denyAnimate();
						ghosts[i].getSprite().setImage(scared[0]);
						ghosts[i].setMovementInc(.125*scale);
					}
				}
			}
			if(ghostEaten > -1) {
				ghosts[ghostEaten].getSprite().setImage(eyes);
				ghosts[ghostEaten].setX(Math.round((ghosts[ghostEaten].getX()-offsets[0])/scale)*scale+offsets[0]);
				ghosts[ghostEaten].setY(Math.round((ghosts[ghostEaten].getY()-offsets[1])/scale)*scale+offsets[1]);
				ghosts[ghostEaten].setMovementInc(.5*scale);
				numGhostsEaten++;
				int prescore = score;
				score += Math.pow(2, numGhostsEaten)*100;
				popup.setVisible(true);
				popup.setText(Integer.toString((int)(Math.pow(2, numGhostsEaten)*100)));
				popup.setY(ghosts[ghostEaten].getY());
				popup.setX(ghosts[ghostEaten].getX()-popup.getBoundsInLocal().getWidth()/2.);
				popupCounter = 0;
				if(score/10000 > prescore/10000) {
					lives++;
					this.updateLives();
				}
				ghostEaten = -1;
			}
			scaredTimingCounter++;
			if(scaredTimingCounter > 170 && scaredTimingCounter%10 == 0) {
				for(int i = 0; i < ghosts.length; i++) {
					if(ghosts[i].getSprite().getImage() != eyes && !ghosts[i].isAnimateable())
						if(ghosts[i].getSprite().getImage() == scared[0]) {
							ghosts[i].getSprite().setImage(scared[1]);
						}else {
							ghosts[i].getSprite().setImage(scared[0]);
						}
				}
			}
			if(scaredTimingCounter > 250) {
				for(int i = 0; i < ghosts.length; i++) {
					if(!ghosts[i].isAnimateable() && ghosts[i].getSprite().getImage() != eyes) {
						if(ghosts[i].getMovementAble()) {
							if(ghosts[i].getX()*100-(int)(ghosts[i].getX()*100) == .5) {
								ghosts[i].setX(ghosts[i].getX()-.125*scale);
							}
							if(ghosts[i].getY()*100-(int)(ghosts[i].getY()*100) == .5) {
								ghosts[i].setY(ghosts[i].getY()-.125*scale);
							}
						}
						ghosts[i].allowAnimate();
						ghosts[i].setMovementInc(.25*scale);
						ghosts[i].setOrientation(ghosts[i].getOrientation());
					}
				}
				scaredTimingCounter = 0;
				eatGhosts = false;
				numGhostsEaten = 0;
			}
		}
	}

	private void clearPopup() {
		if(popup.isVisible()) {
			popupCounter++;
			if(popupCounter > 40) {
				popup.setVisible(false);
			}
		}
	}
	
	private void queGhosts() {
		if(started || eatGhosts) {
			String directions[] = {"right", "left"};
			double locations[] = {13.5, 11.5, 15.5};
			double center[] = {13.5*scale+offsets[0], 14*scale+offsets[1]};
			double top = 13*scale+offsets[1];
			double bottom = 15*scale+offsets[1];
			for(int i = 0; i < ghosts.length; i++) {
				if(ghosts[i].getX() == 13.5*scale+offsets[0] && ghosts[i].getY() == 11*scale+offsets[1]) {
					if(ghosts[i].getSprite().getImage() == eyes && ghostsInQue < 4) {
						ghosts[i].denyMove();
						ghostsQue[ghostsInQue] = ghosts[i];
						ghostsInQue++;
					}
					
				}
			}
			
			if(ghostsInQue > 0) {
				for(int n = 0; n < ghostsInQue; n++) {
					int direction = -1;
					if(ghostsQue[n].getX() == 13.5*scale+offsets[0]) {
						direction = 1;
					}				
					if(!ghostsQue[n].getMovementAble() && ghostsQue[n].getSprite().getImage() != eyes) {
						ghostsQue[n].setY(ghostsQue[n].getY()+(.125*scale)*queDirection*direction);
						if(queDirection == direction*-1) {
							ghostsQue[n].setOrientation("up");
						}else {
							ghostsQue[n].setOrientation("down");
						}
					}
				}
				for(int i = 0; i < ghostsInQue; i++) {
					if(ghostsQue[i].getSprite().getImage() == eyes) {
						if(ghostsQue[i].getY() != center[1]) {
							ghostsQue[i].allowMove();
							ghostsQue[i].setOrientation("down");
						}else if(ghostsQue[i].getX() != locations[i]*scale+offsets[0]) {
							if(locations[i] > locations[0]) {
								ghostsQue[i].setOrientation("right");
							}else if(locations[i] < locations[0]) {
								ghostsQue[i].setOrientation("left");
							}
						}else {
							ghostsQue[i].denyMove();
							ghostsQue[i].allowAnimate();
							ghostsQue[i].setOrientation(ghostsQue[i].getOrientation());
							ghostsQue[i].setMovementInc(.125*scale);
						}
					}
				}
				boolean changed = false;
				for(int i = 0; i < ghostsInQue && !changed; i++) {
					if(!ghostsQue[i].getMovementAble() && ghostsQue[i].getSprite().getImage() != eyes) {
						if(ghostsQue[i].getY() <= top || ghostsQue[i].getY() >= bottom) {
							queDirection *= -1;
							changed = true;
						}
					}
				}
				ghostTimingCounter++;
				if(ghostsQue[0].getY() == 11*scale+offsets[1] && ghostsQue[0].getSprite().getImage() != eyes) {
					ghostsQue[0].setOrientation(directions[(int)(Math.random()*2)]);
					if(ghostsQue[0].isAnimateable()) {
						ghostsQue[0].setMovementInc(.25*scale);
					}
					ghostsInQue--;
					for(int i = 0; i < ghostsQue.length-1; i++) {
						ghostsQue[i] = ghostsQue[i+1];
					}
				}
				if(ghostTimingCounter >= 70) {
					if(ghostsQue[0].getX() == center[0] && ghostsQue[0].getSprite().getImage() != eyes) {
						ghostsQue[0].setOrientation("up");
						ghostsQue[0].allowMove();
						ghostTimingCounter = 0;
					}else if(ghostsQue[0].getY() == center[1] && ghostsQue[0].getSprite().getImage() != eyes) {
						if(ghostsQue[0].getX() < center[0]) {
							ghostsQue[0].setOrientation("right");
							ghostsQue[0].allowMove();
							ghostsQue[0].setMovementInc(.125*scale);
						}else if(ghostsQue[0].getX() > center[0]){
							ghostsQue[0].setOrientation("left");
							ghostsQue[0].allowMove();
							ghostsQue[0].setMovementInc(.125*scale);
						}
					}
				}
			}
		}
	}

	private void drawPellets() {
		grid = new Circle[layout.length][layout[0].length];
		for(int i = 0; i < layout.length; i++) {
			for(int n = 0; n < layout[i].length; n++) {
				if(layout[i][n] == 4) {
					System.out.println(Integer.toString(i) + "," + Integer.toString(n) );
				}
				if(layout[i][n] == 2 || layout[i][n] == 3) {
					if(layout[i][n] == 2) {
						grid[i][n] = new Circle(n*scale+offsets[0], i*scale+offsets[1], scale/10);
					}else {
						grid[i][n] = new Circle(n*scale+offsets[0], i*scale+offsets[1], scale/2.5);
					}
					grid[i][n].setFill(Color.WHEAT);
					parent.getChildren().add(grid[i][n]);
				}
			}
		}
	}

	public void manageKeys(String key, boolean isLetterKey) {
		if((key == "RIGHT" || key == "LEFT" || key == "UP" || key == "DOWN") && !nextLevel && !gameOver){
			if(!started) {
				started = true;
				ghosts[0].allowMove();
				messages.setText("");
			}
			if(key.toLowerCase() != pacman.getOrientation()) {
				this.nextDirection = key.toLowerCase();
			}
		}else if(key == "ESCAPE" && !gameOver && !paused) {
			gameOver = true;
			messages.setText("Game Over");
			for(int i = 0; i < ghosts.length; i++) {
				ghosts[i].getSprite().setVisible(false);
			}
			pacman.getSprite().setVisible(false);
			messages.setX(13.5*scale+offsets[0]-(messages.getLayoutBounds().getWidth()/2.));
		}else if(key == "SPACE" && !gameOver) {
			if(paused) {
				paused = false;
				messages.setText("");
				for(int i = 0; i < ghosts.length; i++) {
					ghosts[i].getSprite().setVisible(true);
				}
				pacman.getSprite().setVisible(true);
			}else {
				paused = true;
				for(int i = 0; i < ghosts.length; i++) {
					ghosts[i].getSprite().setVisible(false);
				}
				pacman.getSprite().setVisible(false);
				messages.setText("Paused");
				messages.setX(13.5*scale+offsets[0]-(messages.getLayoutBounds().getWidth()/2.));
			}
		}else if(gameOver && !terminate) {
			if(key == "RIGHT") {
				currentInitial++;
				if(currentInitial >= initials.length) 
					currentInitial = initials.length-1;
			}else if(key == "LEFT") {
				currentInitial--;
				if(currentInitial < 0)
					currentInitial = 0;
			}else if(key == "ENTER") {
				currentInitial++;
				if(currentInitial >= initials.length) 
					currentInitial = initials.length-1;
					terminate = true;
					saveScore();
					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					stage.close();

			}else if(isLetterKey){
				initials[currentInitial].setText(key);
				if(currentInitial < initials.length-1)
					currentInitial++;
			}
		}
	}

	private void pacmanMoves() {
		if(nextLevel) {
			pacman.denyMove();
			started = false;
		}
		double currentLoc[] = {((pacman.getX() - offsets[0])/scale),(pacman.getY() - offsets[1])/scale};
		int offset[] = {(int)(currentLoc[0]*100)-((int)(currentLoc[0]))*100, (int)(currentLoc[1]*100)-((int)(currentLoc[1]))*100};
		int nextLoc[] = new int[4];
		nextLoc[0] = (int)Math.round(currentLoc[0]);
		nextLoc[1] = (int)Math.round(currentLoc[1]);
		nextLoc[2] = nextLoc[0];
		nextLoc[3] = nextLoc[1];
		boolean outOfBounds = (nextLoc[0] < 0 || nextLoc[1] < 0 || nextLoc[1] >= layout.length || nextLoc[0] >= layout[0].length);
		if(!outOfBounds) {
			if(layout[nextLoc[1]][nextLoc[0]] >= 2) {
				int prevScore = score;
				if(layout[nextLoc[1]][nextLoc[0]] == 2) {
					score += 10;
				}else if(layout[nextLoc[1]][nextLoc[0]] == 3) {
					score += 50;
					eatGhosts = true;
					scaredTimingCounter = 0;
				}
				if(score/10000 > prevScore/10000) {
					lives++;
					this.updateLives();
				}
				scoreDisp.setText(Integer.toString(score));
				layout[nextLoc[1]][nextLoc[0]] = 1;
				grid[nextLoc[1]][nextLoc[0]].setFill(Color.BLACK);
				boolean noAdvance = true;
				for(int i = 0; i < layout.length && noAdvance; i++) {
					for(int n = 0; n < layout[0].length && noAdvance; n++) {
						if(layout[i][n] > 1) {
							noAdvance = false;
						}
					}
				}
				if(noAdvance) {
					nextLevel = true;
				}
			}
		}
		switch(pacman.getOrientation()) {
		case "right":
			nextLoc[0] += 1;
			break;
		case "left":
			nextLoc[0] -= 1;
			break;
		case "up":
			nextLoc[1] -= 1;
			break;
		case "down":
			nextLoc[1] += 1;
			break;
		}
		switch(nextDirection) {
		case "right":
			nextLoc[2] += 1;
			break;
		case "left":
			nextLoc[2] -= 1;
			break;
		case "up":
			nextLoc[3] -= 1;
			break;
		case "down":
			nextLoc[3] += 1;
			break;
		case "null":
			nextLoc[2] = 0;
			nextLoc[3] = 0;
			break;
		}
		outOfBounds = (nextLoc[0] < 0 || nextLoc[1] < 0 || nextLoc[1] >= layout.length || nextLoc[0] >= layout[0].length);
		outOfBounds = outOfBounds || (nextLoc[2] < 0 || nextLoc[3] < 0 || nextLoc[3] >= layout.length || nextLoc[2] >= layout[0].length);
		if (!outOfBounds){
			if(layout[nextLoc[1]][nextLoc[0]] == 0 && offset[0] < 10 && offset[1] < 10) {
				pacman.denyMove();
			}
			if(layout[nextLoc[3]][nextLoc[2]] != 0 && offset[0] < 10 && offset[1] < 10) {
				pacman.setOrientation(nextDirection);
				nextDirection = "null";
				pacman.allowMove();
			}
			if(nextDirection != "null" && !pacman.getMovementAble() && layout[nextLoc[1]][nextLoc[0]] != 0) {
				pacman.allowMove();
			}
		}
	}

	private void manageGhostsMove(Sprite ghost) {
		if(ghost.getMovementAble()) {
			double currentLoc[] = {((ghost.getX() - offsets[0])/scale),(ghost.getY() - offsets[1])/scale};
			int offset[] = {(int)(currentLoc[0]*100)-((int)(currentLoc[0]))*100, (int)(currentLoc[1]*100)-((int)(currentLoc[1]))*100};
			int nextLoc[] = new int[2];
			nextLoc[0] = (int)Math.round(currentLoc[0]);
			nextLoc[1] = (int)Math.round(currentLoc[1]);
			int options[][] = {{nextLoc[0], nextLoc[1]},{nextLoc[0], nextLoc[1]}};
			String moves[] = {"up", "down"};
			switch(ghost.getOrientation()) {
			case "right":
				options[0][1] = nextLoc[1] -1;
				options[1][1] = nextLoc[1] +1;
				nextLoc[0] += 1;
				break;
			case "left":
				options[0][1] = nextLoc[1] -1;
				options[1][1] = nextLoc[1] +1;
				nextLoc[0] -= 1;
				break;
			case "up":
				moves[0] = "right";
				moves[1] = "left";
				options[0][0] = nextLoc[0] +1;
				options[1][0] = nextLoc[0] -1;
				nextLoc[1] -= 1;
				break;
			case "down":
				moves[0] = "right";
				moves[1] = "left";
				options[0][0] = nextLoc[0] +1;
				options[1][0] = nextLoc[0] -1;
				nextLoc[1] += 1;
				break;
			}
			if(nextLevel) {
				ghost.denyMove();
			}
			boolean outOfBounds = (nextLoc[0] < 0 || nextLoc[1] < 0 || nextLoc[1] >= layout.length || nextLoc[0] >= layout[0].length);
			outOfBounds = outOfBounds || (options[0][0] < 0 || options[0][1] < 0 || options[0][1] >= layout.length || options[0][0] >= layout[0].length);
			outOfBounds = outOfBounds || (options[1][0] < 0 || options[1][1] < 0 || options[1][1] >= layout.length || options[1][0] >= layout[0].length);
			if (!outOfBounds){
				int deviate = (int)(Math.random()*2);
				if(ghost.getSprite().getImage() == eyes) {
					int choice = 0;					
					if((ghost.getX()-offsets[0])/scale > 13.5 && moves[0] == "right") {
						choice = 1;
					}
					if((ghost.getY()-offsets[1])/scale < 11) {
						choice = 1;
					}
					int optionOne = layout[options[choice][1]][options[choice][0]];
					int optionTwo = layout[options[Math.abs(choice-1)][1]][options[Math.abs(choice-1)][0]];
					if(optionOne != 0 && deviate == 0) {
						if(offset[0] < 10 && offset[1] < 10) {
							if(optionOne != 0) {
								ghost.setOrientation(moves[choice]);
							}else if(optionTwo != 0) {
								ghost.setOrientation(moves[Math.abs(choice-1)]);
							}
						}
					}
					if(layout[nextLoc[1]][nextLoc[0]] == 0 && offset[0] < 10 && offset[1] < 10) {
						if(optionOne != 0) {
							ghost.setOrientation(moves[choice]);
						}else if(optionTwo != 0) {
							ghost.setOrientation(moves[Math.abs(choice-1)]);
						}
					}
					
				}else {
					int choice = (int)(Math.random()*2);
					int optionOne = layout[options[choice][1]][options[choice][0]];
					int optionTwo = layout[options[Math.abs(choice-1)][1]][options[Math.abs(choice-1)][0]];
					if(optionOne != 0 || optionTwo != 0) {
						if(deviate == 1 && offset[0] < 10 && offset[1] < 10) {
							if(optionOne != 0) {
								ghost.setOrientation(moves[choice]);
							}else if(optionTwo != 0) {
								ghost.setOrientation(moves[Math.abs(choice-1)]);
							}
						}
					}
					if(layout[nextLoc[1]][nextLoc[0]] == 0 && offset[0] < 10 && offset[1] < 10) {
						if(optionOne != 0) {
							ghost.setOrientation(moves[choice]);
						}else if(optionTwo != 0) {
							ghost.setOrientation(moves[Math.abs(choice-1)]);
						}

					}
				}
			}
		}
	}

	private void blinkPowerPellets() {
		powerPelletCounter += 1;
		if(powerPelletCounter > 6) {
			powerPelletCounter = 0;
			for(int y = 0; y < layout.length; y++) {
				for(int x = 0; x < layout[y].length; x++) {
					if(layout[y][x] == 3) {
						if(grid[y][x].getFill() == Color.WHEAT) {
							grid[y][x].setFill(Color.BLACK);
						}else {
							grid[y][x].setFill(Color.WHEAT);
						}
					}
				}
			}
		}
	}

	public double getboundsWidth() {
		return boundsWidth-offsets[0];
	}

	public double getboundsHeight() {
		return boundsHeight;
	}

	public void killThread() {
		terminate = true;
	}


	private void drawMaze() {
		Line[] outline = new Line[4];
		for(int i = 0; i < layout.length; i++) {
			for(int n = 0; n < layout[i].length; n++) {
				if(wall[i][n] != "   " && wall[i][n] != "now") {
					Arc corner = new Arc();
					switch(wall[i][n]) {
					case "how":
						outline[0] = new Line(n*scale+offsets[0]-scale/2, i*scale+offsets[1], n*scale+offsets[0]+scale/2, i*scale+offsets[1]);
						break;
					case "vew":
						outline[0] = new Line(n*scale+offsets[0], i*scale+offsets[1]-scale/2, n*scale+offsets[0], i*scale+offsets[1]+scale/2);
						break;
					case "cbl":
						corner.setCenterX(n*scale+offsets[0]-scale/2.);
						corner.setCenterY(i*scale+offsets[1]+scale/2.);
						corner.setStartAngle(0);
						break;
					case "cbr":
						corner.setCenterX(n*scale+offsets[0]+scale/2.);
						corner.setCenterY(i*scale+offsets[1]+scale/2.);
						corner.setStartAngle(90);
						break;
					case "ctr":
						corner.setCenterX(n*scale+offsets[0]+scale/2.);
						corner.setCenterY(i*scale+offsets[1]-scale/2.);
						corner.setStartAngle(180);
						break;
					case "ctl":
						corner.setCenterX(n*scale+offsets[0]-scale/2.);
						corner.setCenterY(i*scale+offsets[1]-scale/2.);
						corner.setStartAngle(270);
						break;
					case "ggt":
						outline[0] = new Line(n*scale+offsets[0]-scale/2., i*scale+offsets[1]+scale/5.-1, n*scale+offsets[0]+scale/2., i*scale+offsets[1]+scale/5.-1);
						outline[0].setStroke(Color.PINK);
						outline[0].setStrokeWidth(scale/5.);
						outline[0].setStrokeLineCap(StrokeLineCap.BUTT);
						break;
					case "ght":
						outline[0] = new Line(n*scale+offsets[0]-scale/2, i*scale+offsets[1]+scale/3, n*scale+offsets[0]+scale/2, i*scale+offsets[1]+scale/3);
						outline[1] = new Line(n*scale+offsets[0]-scale/2, i*scale+offsets[1], n*scale+offsets[0]+scale/2, i*scale+offsets[1]);
						break;
					case "ghb":
						outline[0] = new Line(n*scale+offsets[0]-scale/2, i*scale+offsets[1]-scale/3, n*scale+offsets[0]+scale/2, i*scale+offsets[1]-scale/3);
						outline[1] = new Line(n*scale+offsets[0]-scale/2, i*scale+offsets[1], n*scale+offsets[0]+scale/2, i*scale+offsets[1]);
						break;
					case "gvr":
						outline[0] = new Line(n*scale+offsets[0]-scale/3, i*scale+offsets[1]-scale/2, n*scale+offsets[0]-scale/3, i*scale+offsets[1]+scale/2);
						outline[1] = new Line(n*scale+offsets[0], i*scale+offsets[1]-scale/2, n*scale+offsets[0], i*scale+offsets[1]+scale/2);
						break;
					case "gvl":
						outline[0] = new Line(n*scale+offsets[0]+scale/3, i*scale+offsets[1]-scale/2, n*scale+offsets[0]+scale/3, i*scale+offsets[1]+scale/2);
						outline[1] = new Line(n*scale+offsets[0], i*scale+offsets[1]-scale/2, n*scale+offsets[0], i*scale+offsets[1]+scale/2);
						break;
					case "gbr":
						outline[0] = new Line(n*scale+offsets[0]+scale/3, i*scale+offsets[1]+scale/3, n*scale+offsets[0]+scale/3, i*scale+offsets[1]+scale/2);
						outline[1] = new Line(n*scale+offsets[0], i*scale+offsets[1], n*scale+offsets[0], i*scale+offsets[1]+scale/2);
						outline[2] = new Line(n*scale+offsets[0]+scale/3, i*scale+offsets[1]+scale/3, n*scale+offsets[0]+scale/2, i*scale+offsets[1]+scale/3);
						outline[3] = new Line(n*scale+offsets[0], i*scale+offsets[1], n*scale+offsets[0]+scale/2, i*scale+offsets[1]);
						break;
					case "gbl":
						outline[0] = new Line(n*scale+offsets[0]-scale/3, i*scale+offsets[1]+scale/3, n*scale+offsets[0]-scale/3, i*scale+offsets[1]+scale/2);
						outline[1] = new Line(n*scale+offsets[0], i*scale+offsets[1], n*scale+offsets[0], i*scale+offsets[1]+scale/2);
						outline[2] = new Line(n*scale+offsets[0]-scale/3, i*scale+offsets[1]+scale/3, n*scale+offsets[0]-scale/2, i*scale+offsets[1]+scale/3);
						outline[3] = new Line(n*scale+offsets[0], i*scale+offsets[1], n*scale+offsets[0]-scale/2, i*scale+offsets[1]);
						break;
					case "gtr":
						outline[0] = new Line(n*scale+offsets[0]+scale/3, i*scale+offsets[1]-scale/3, n*scale+offsets[0]+scale/3, i*scale+offsets[1]-scale/2);
						outline[1] = new Line(n*scale+offsets[0], i*scale+offsets[1], n*scale+offsets[0], i*scale+offsets[1]-scale/2);
						outline[2] = new Line(n*scale+offsets[0]+scale/3, i*scale+offsets[1]-scale/3, n*scale+offsets[0]+scale/2, i*scale+offsets[1]-scale/3);
						outline[3] = new Line(n*scale+offsets[0], i*scale+offsets[1], n*scale+offsets[0]+scale/2, i*scale+offsets[1]);
						break;
					case "gtl":
						outline[0] = new Line(n*scale+offsets[0]-scale/3, i*scale+offsets[1]-scale/3, n*scale+offsets[0]-scale/3, i*scale+offsets[1]-scale/2);
						outline[1] = new Line(n*scale+offsets[0], i*scale+offsets[1], n*scale+offsets[0], i*scale+offsets[1]-scale/2);
						outline[2] = new Line(n*scale+offsets[0]-scale/3, i*scale+offsets[1]-scale/3, n*scale+offsets[0]-scale/2, i*scale+offsets[1]-scale/3);
						outline[3] = new Line(n*scale+offsets[0], i*scale+offsets[1], n*scale+offsets[0]-scale/2, i*scale+offsets[1]);
						break;
					}
					if(wall[i][n].charAt(0) != 'c') {
						if(wall[i][n] != "ggt") {
							outline[0].setStroke(Color.BLUE);
							if(wall[i][n].charAt(0) == 'g') {
								outline[1].setStroke(Color.BLUE);
								parent.getChildren().add(outline[1]);
								if(wall[i][n].charAt(1) == 't' || wall[i][n].charAt(1) == 'b') {
									outline[2].setStroke(Color.BLUE);
									outline[3].setStroke(Color.BLUE);
									parent.getChildren().add(outline[2]);
									parent.getChildren().add(outline[3]);
								}
							}
						}				
						parent.getChildren().add(outline[0]);						

					}else{
						corner.setRadiusX(scale/2.);
						corner.setRadiusY(scale/2.);
						corner.setLength(90);
						corner.setStroke(Color.BLUE);
						parent.getChildren().add(corner);
					}
				}
			}
		}
	}
}
