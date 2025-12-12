import org.example.GameService;
import org.example.GrpcServer;
import org.example.models.GuessNumberGrpc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractTest extends AbstractChannelTest {

	private final GrpcServer grpcServer = GrpcServer.create(new GameService());
	protected GuessNumberGrpc.GuessNumberStub gameStub;

	@BeforeAll
	public void setup() {
		this.grpcServer.start();
		this.gameStub = GuessNumberGrpc.newStub(channel);
	}

	@AfterAll
	public void stop() {
		this.grpcServer.stop();
	}
}
