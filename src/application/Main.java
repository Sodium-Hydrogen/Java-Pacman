package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Main extends Application {
	Pane root;
	Stage stage;
	String path = "resources/pacman/";
	BackgroundThread maze = new BackgroundThread();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			stage = primaryStage;
			root = new Pane();
			Scene scene = new Scene(root,300,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
			
			scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
				public void handle(KeyEvent key) {
					if(maze.isAlive())
						maze.manageKeys(key.getCode().toString(), key.getCode().isLetterKey());
				}
			});
			primaryStage.show();
			startScreen();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		if(maze.isAlive())
			maze.killThread();
	}
	
	public void startScreen() {
		String[] characters = {"pacman", "blinky", "pinky", "inky", "clyde"};
		Sprite[] chars = new Sprite[characters.length];
		try {
			ImageView logo = new ImageView(new Image(new FileInputStream(path + "logo.png")));
			logo.setScaleX(.8);
			logo.setScaleY(.8);
			logo.setX(150-logo.getImage().getWidth()/2.);
			logo.setY(30);
			root.getChildren().add(logo);
		}catch (FileNotFoundException e) {
			System.out.println("Unable to open logo");
		}

		Text info = new Text("Controls\n-----------------\nArrow Keys - Move\nESC - End Game\nSpace - Pause");
		info.setFill(Color.WHITE);
		info.setFont(Font.font("sans", FontWeight.NORMAL, 15*.9));
		info.setTextAlignment(TextAlignment.CENTER);
		info.setY(300);
		info.setX(150-info.getLayoutBounds().getWidth()/2.);
		Button startButton = new Button("Start");
		startButton.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		startButton.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, new CornerRadii(50), new BorderWidths(2))));
		startButton.setTextFill(Color.BLUE);
		startButton.setFont(Font.font("sans", FontWeight.BOLD, 15));
		startButton.setMinWidth(80);
		startButton.setLayoutX(110);
		startButton.setLayoutY(160);
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent action) {
				root.getChildren().remove(0, root.getChildren().size());
				loadMaze();
			}
		});
		for(int i = 0; i < characters.length; i++) {
			if(characters[i] == "pacman") {
				chars[i] = new Sprite(5, "pacman", true, path);
				chars[i].setCurrentFrame(3);
			}else {
				chars[i] = new Sprite(4, characters[i], false, path);
			}
			chars[i].setX(150-15*(2-i));
			chars[i].setY(120);
			chars[i].setOrientation("left");
			chars[i].setWidth(15);
			chars[i].setHeight(15);
			root.getChildren().add(chars[i].getSprite());
		}
		root.getChildren().add(info);
		root.getChildren().add(startButton);
	}
	
	public void loadMaze() {
		Sprite pacman = new Sprite(5, "pacman", true, path);
		Sprite blinky = new Sprite(4, "blinky", false, path);
		Sprite inky = new Sprite(4, "inky", false, path);
		Sprite pinky = new Sprite(4, "pinky", false, path);
		Sprite clyde = new Sprite(4, "clyde", false, path);
		maze.setSprites(pacman, blinky, pinky, clyde, inky);
		maze.setupMaze(0, 0, 15, root, stage);
		stage.setWidth(maze.getboundsWidth());
		stage.setHeight(maze.getboundsHeight());
		maze.start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
