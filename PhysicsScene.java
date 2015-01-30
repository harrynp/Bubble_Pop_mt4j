package advanced.physics.scenes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.flickProcessor.FlickEvent;
import org.mt4j.input.inputProcessors.componentProcessors.flickProcessor.FlickProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateEvent;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import advanced.physics.physicsShapes.PhysicsCircle;
//import advanced.physics.physicsShapes.PhysicsPolygon;
import advanced.physics.physicsShapes.PhysicsRectangle;
import advanced.physics.util.PhysicsHelper;
import advanced.physics.util.UpdatePhysicsAction;

public class PhysicsScene extends AbstractScene {
	private float timeStep = 1.0f / 60.0f;
	private int constraintIterations = 10;
	
	/** THE CANVAS SCALE **/
	private float scale = 20;
	private AbstractMTApplication app;
	private World world;
	
	
	
	///////////////////////////////////////////////
	// us
	//New Variables
	private int score;
	private int timer;
	private MTTextArea scoreText;
	private MTTextArea timerText;
	//
	///////////////////////////////////////////
	
	
	
	
	private MTComponent physicsContainer;
	
	
	public PhysicsScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		this.app = mtApplication;
		
		
		
		
		///////////////////////////////
		//Initialize Score and Timer
		this.score = 0;
		this.timer = 30;
		////////////////////////////////////
		
		
		
		
		
		
		float worldOffset = 10; //Make Physics world slightly bigger than screen borders
		//Physics world dimensions
		AABB worldAABB = new AABB(new Vec2(-worldOffset, -worldOffset), new Vec2((app.width)/scale + worldOffset, (app.height)/scale + worldOffset));
//		Vec2 gravity = new Vec2(0, 10);
		Vec2 gravity = new Vec2(0, 0);
		boolean sleep = true;
		//Create the physics world
		this.world = new World(worldAABB, gravity, sleep);
		
		this.registerGlobalInputProcessor(new CursorTracer(app, this));
		
		//Update the positions of the components according the the physics simulation each frame
		this.registerPreDrawAction(new UpdatePhysicsAction(world, timeStep, constraintIterations, scale));
		
		physicsContainer = new MTComponent(app);
		//Scale the physics container. Physics calculations work best when the dimensions are small (about 0.1 - 10 units)
		//So we make the display of the container bigger and add in turn make our physics object smaller
		physicsContainer.scale(scale, scale, 1, Vector3D.ZERO_VECTOR);
		this.getCanvas().addChild(physicsContainer);
		
		//Create borders around the screen
		this.createScreenBorders(physicsContainer);
		
		
//		//Create circles
//		for (int i = 0; i < 20; i++) {
//			PhysicsCircle c = new PhysicsCircle(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 50, world, 1.0f, 0.3f, 0.4f, scale);
//			MTColor col = new MTColor(ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255));
//			c.setFillColor(col);
//			c.setStrokeColor(col);
//			PhysicsHelper.addDragJoint(world, c, c.getBody().isDynamic(), scale);
//			physicsContainer.addChild(c);
//		}
		
		
		
		
		
		
		
		//////////////////////////////HERE///////////////////////////////////
		//Creates Score and Timer Text
		MTComponent uiLayer = new MTComponent(mtApplication, new MTCamera(mtApplication));
		uiLayer.setDepthBufferDisabled(true);
		getCanvas().addChild(uiLayer);
		IFont font = FontManager.getInstance().createFont(mtApplication, "arial", 50, MTColor.WHITE);
		// text area for score
		scoreText = new MTTextArea(mtApplication, font);
		scoreText.setPickable(false);
		scoreText.setNoFill(true);
		scoreText.setNoStroke(true);
		scoreText.setPositionGlobal(new Vector3D(5,30,0));
		uiLayer.addChild(scoreText);
		// text area for time 
		timerText = new MTTextArea(mtApplication, font);
		timerText.setPickable(false);
		timerText.setNoFill(true);
		timerText.setNoStroke(true);
		timerText.setPositionGlobal(new Vector3D(mtApplication.width - 215, 30,0));
		uiLayer.addChild(timerText);
		this.updateScore();
		this.updateTimer();
		
		//Creates Timer
		ActionListener timerListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (timer != 0) {
					//Decrement Timer
					--timer;
					updateTimer();
				}
				else {
					//Clears Canvas
					MTComponent[] children = getCanvas().getChildren();
					for (int i = 0; i < children.length ; i++) {
						getCanvas().removeChild(children[i]);
					}
					IFont font = FontManager.getInstance().createFont(app, "arial", 50, MTColor.WHITE);
					MTTextArea gameOver = new MTTextArea(app, font);
					gameOver.setPickable(false);
					gameOver.setNoFill(true);
					gameOver.setNoStroke(true);
					gameOver.setPositionGlobal(new Vector3D(app.width/2 - 60, app.height/2,0));
					getCanvas().addChild(gameOver);
					gameOver.setText("Final Score: " + Integer.toString(score));
				}
			}
		};
		
		//Starts Timer
		Timer timer = new Timer(1000, timerListener);
		timer.start();
		
		//Creates 30 Bubbles that only pop when tapped
		for (int i = 0; i < 30; i++) {
			PhysicsCircle c = new PhysicsCircle(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 50, world, 1.0f, 0.3f, 0.4f, scale);
			MTColor col = new MTColor(ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255));
			c.setFillColor(new MTColor(col));
			c.setStrokeColor(new MTColor(col));
			c.registerInputProcessor(new TapProcessor(app));
			c.addGestureListener(TapProcessor.class, new IGestureEventListener(){
				public boolean processGestureEvent(MTGestureEvent e){
					TapEvent tapEvent = (TapEvent)e;
					IMTComponent3D target = tapEvent.getTarget();
					if (target instanceof PhysicsCircle){
						if (tapEvent.isTapped()){
							score += 1;
							updateScore();
							target.setVisible(false);
						}
					}
					return true;
				}
			});
			physicsContainer.addChild(c);
		}

		//Creates 5 Bubbles that only pop when rotated with 2 fingers
		for (int i = 0; i < 5; i++) {
			PhysicsCircle c = new PhysicsCircle(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 150, world, 1.0f, 0.3f, 0.4f, scale);
			MTColor col = new MTColor(ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255));
			c.setFillColor(new MTColor(col));
			c.setStrokeColor(new MTColor(col));
			c.registerInputProcessor(new RotateProcessor(app));
			c.addGestureListener(RotateProcessor.class, new IGestureEventListener(){
				public boolean processGestureEvent(MTGestureEvent e){
					RotateEvent rotateEvent = (RotateEvent)e;
					IMTComponent3D target = rotateEvent.getTarget();
					if (target instanceof PhysicsCircle){
						if (rotateEvent.getRotationDegrees() > 3){
							score += 10;
							updateScore();
							target.setVisible(false);
						}
					}
					return true;
				}
			});
			physicsContainer.addChild(c);
		}
	}
	
	//Updates Score
	private void updateScore(){
		scoreText.setText("Score: " + Integer.toString(score));
	}
	
	//Updates Timer
	private void updateTimer(){
		timerText.setText("Timer: " + Integer.toString(timer));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void createScreenBorders(MTComponent parent){
		//Left border 
		float borderWidth = 50f;
		float borderHeight = app.height;
		Vector3D pos = new Vector3D(-(borderWidth/2f) , app.height/2f);
		PhysicsRectangle borderLeft = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderLeft.setName("borderLeft");
		parent.addChild(borderLeft);
		//Right border
		pos = new Vector3D(app.width + (borderWidth/2), app.height/2);
		PhysicsRectangle borderRight = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderRight.setName("borderRight");
		parent.addChild(borderRight);
		//Top border
		borderWidth = app.width;
		borderHeight = 50f;
		pos = new Vector3D(app.width/2, -(borderHeight/2));
		PhysicsRectangle borderTop = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderTop.setName("borderTop");
		parent.addChild(borderTop);
		//Bottom border
		pos = new Vector3D(app.width/2 , app.height + (borderHeight/2));
		PhysicsRectangle borderBottom = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderBottom.setName("borderBottom");
		parent.addChild(borderBottom);
	}

	public void onEnter() {
	}
	
	public void onLeave() {	
	}

}
