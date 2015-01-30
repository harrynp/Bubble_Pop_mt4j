package advanced.physics.scenes;
import org.mt4j.MTApplication;

public class inf133multitouch extends MTApplication {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		initialize();
	}
	
	@Override
	public void startUp() {
		addScene(new PhysicsScene(this, "Bubble Pop"));
	}

}
