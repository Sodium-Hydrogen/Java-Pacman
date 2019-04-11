package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Sprite {
	private boolean init = false;
	private boolean framesLoaded = false;
	private boolean useFrames;
	private int frameCount;
	private int currentFrame;
	private double x = 0;
	private double y = 0;
	private double height = 10;
	private double width = 10;
	private long animationSpeed;
	private long movementSpeed;
	private long lastAnimationUpdate;
	private long lastMovementUpdate;
	private int animationInc;
	private boolean animate = true;
	private double movementInc;
	private boolean move;
	private String name;
	private String orientation;
	private Image[] spriteFrames;
	private ImageView sprite;
	
	public Sprite() {
		super();
	}
	
	public Sprite(int frameCount, String name, boolean useFrames, String path) {
		super();
		init = true;
		framesLoaded = false;
		this.frameCount = frameCount;
		this.name = name;
		this.useFrames = useFrames;
		spriteFrames = new Image[frameCount];
		currentFrame = 0;
		sprite = new ImageView();
		this.setSize(20, 20);
		this.loadFrames(path);
		animationInc = 1;
		animationSpeed = 40;
		movementSpeed = 40;
		movementInc = 4;
		move = true;
		this.setOrientation("right");
	}

	public boolean isAnimateable() {
		return animate;
	}

	public void allowAnimate() {
		this.animate = true;
	}

	public void denyAnimate() {
		this.animate = false;
	}
	
	public double getMovementInc() {
		return movementInc;
	}

	public void setMovementInc(double movementInc) {
		this.movementInc = movementInc;
	}

	public double getX() {
		return x + (width/2.);
	}
	
	public void setX(double x) {
		this.x = x - (width/2.);
		sprite.setX(this.x);
	}
	
	public double getY() {
		return y + (height/2.);
	}
	
	public void setY(double y) {
		this.y = y - (height/2.);
		sprite.setY(this.y);
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
		if(framesLoaded) {
			sprite.setImage(spriteFrames[currentFrame]);
		}
	}

	public int getFrameCount() {
		return frameCount;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrientation() {
		return orientation;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
		sprite.setFitHeight(height);
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
		sprite.setFitWidth(width);
	}

	public void setSize(double height, double width) {
		this.width = width;
		this.height = height;
		sprite.setFitHeight(height);
		sprite.setFitWidth(width);
	}
	
	public long getAnimationSpeed() {
		return animationSpeed;
	}

	public void setAnimationSpeed(long animationSpeed) {
		this.animationSpeed = animationSpeed;
	}

	public long getMovementSpeed() {
		return movementSpeed;
	}

	public void setMovementSpeed(long movementSpeed) {
		this.movementSpeed = movementSpeed;
	}

	public long getLastAnimationUpdate() {
		return lastAnimationUpdate;
	}

	public void setLastAnimationUpdate(long lastAnimationUpdate) {
		this.lastAnimationUpdate = lastAnimationUpdate;
	}

	public long getlastMovementUpdate() {
		return lastMovementUpdate;
	}

	public void setlastMovementUpdate(long lastMovementUpdate) {
		this.lastMovementUpdate = lastMovementUpdate;
	}

	public void allowMove() {
		move = true;
	}
	
	public void denyMove() {
		move = false;
	}
	
	public boolean getMovementAble() {
		return move;
	}
	
	public boolean checkAnimationUpdate(long curTime) {
		if(curTime >= lastAnimationUpdate + animationSpeed) {
			lastAnimationUpdate = curTime;
			return true;
		}
		return false;
	}
	
	public void animateSprite() {
		if(move) {
			currentFrame += animationInc;
			this.setCurrentFrame(currentFrame);
			if(currentFrame == frameCount-1) {
				animationInc = -1;
			}else if(currentFrame == 0) {
				animationInc = 1;
			}
		}
	}
	
	public boolean checkMovementUpdate(long curTime) {
		if(curTime >= lastMovementUpdate + movementSpeed) {
			lastMovementUpdate = curTime;
			return true;
		}
		return false;
	}
	public void moveSprite(double boundsWidth, double boundsHeight, double scale) {
		if(move) {
			switch(this.getOrientation()) {
			case "up":
				this.setY(this.getY()-movementInc);
				if(this.getY() < 0-this.getHeight()) {
					this.setY(boundsHeight);
				}
				break;
			case "right":
				this.setX(this.getX()+movementInc);
				if(this.getX() > boundsWidth) {
					this.setX(0-movementInc);
				}
				break;
			case "down":
				this.setY(this.getY()+movementInc);
				if(this.getY() > boundsHeight) {
					this.setY(0-this.getHeight());
				}
				break;
			case "left":
				this.setX(this.getX()-movementInc);
				if(this.getX() < 0-this.getWidth()) {
					this.setX(boundsWidth);
				}
				break;
			}
		}
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
		if(framesLoaded && animate) {
			if(useFrames) {
				switch(this.orientation) {
				case "up":
					sprite.setRotate(-90);
					break;
				case "right":
					sprite.setRotate(0);
					break;
				case "down":
					sprite.setRotate(90);
					break;
				case "left":
					sprite.setRotate(180);
					break;
				}
			}else {
				switch(this.orientation) {
				case "up":
					sprite.setImage(spriteFrames[0]);;
					break;
				case "right":
					sprite.setImage(spriteFrames[1]);;
					break;
				case "down":
					sprite.setImage(spriteFrames[2]);;
					break;
				case "left":
					sprite.setImage(spriteFrames[3]);;
					break;
				}
			}
		}
	}

	public ImageView getSprite() {
		return sprite;
	}

	public void setSprite(ImageView sprite) {
		this.sprite = sprite;
	}
	
	public boolean getInit() {
		return framesLoaded;
	}
	
	public int getAnimationInc() {
		return animationInc;
	}

	public void setAnimationInc(int animationInc) {
		this.animationInc = animationInc;
	}

	public boolean loadFrames(String path) {
		if(init && !framesLoaded) {
			try {
				for(int i = 0; i < spriteFrames.length; i++) {
					spriteFrames[i] = new Image(new FileInputStream(path + name + Integer.toString(i) + ".png"));
				}
				sprite.setImage(spriteFrames[currentFrame]);
				framesLoaded = true;
				return true;
			}catch ( FileNotFoundException e) {
				return false;
			}
		}else {
			return false;
		}
	}
}
