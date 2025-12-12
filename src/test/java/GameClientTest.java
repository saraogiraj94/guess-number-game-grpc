import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class GameClientTest extends AbstractTest {

	@RepeatedTest(5)
	public void createTest() {
		var responseObserver = new ResponseHandler();
		var requestObserver = this.gameStub.makeGuess(responseObserver);
		responseObserver.setRequestObserver(requestObserver);
		responseObserver.start();
		responseObserver.await();
	}

}
