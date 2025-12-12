import io.grpc.stub.StreamObserver;
import org.example.models.GuessResponse;
import org.example.models.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class ResponseHandler implements StreamObserver<GuessResponse> {

	public static final Logger log = LoggerFactory.getLogger(ResponseHandler.class);

	private final CountDownLatch countDownLatch = new CountDownLatch(1);
	private Throwable throwable;
	private int attempts;

	private int low;
	private int upper;
	private int middle;
	private StreamObserver<org.example.models.GuessRequest> requestStreamObserver;

	@Override
	public void onNext(GuessResponse guessResponse) {
		log.info("attempt {} and result {}", guessResponse.getAttempt(), guessResponse.getResult());
		switch (guessResponse.getResult()) {
			case Result.CORRECT -> onCompleted();
			case Result.TOO_HIGH -> this.send(this.low, this.middle);
			case Result.TOO_LOW -> this.send(this.middle, this.upper);
		}
	}

	@Override
	public void onError(Throwable throwable) {
		countDownLatch.countDown();
	}

	@Override
	public void onCompleted() {
		requestStreamObserver.onCompleted();
		countDownLatch.countDown();
	}

	public void start() {
		this.send(0, 100);
	}

	public void send(int low, int high) {
		this.low = low;
		this.upper = high;
		this.middle = low + (high - low) / 2;
		log.info("guess by client {}", this.middle);
		var guessRequest = org.example.models.GuessRequest.newBuilder().setGuess(this.middle).build();
		this.requestStreamObserver.onNext(guessRequest);
	}

	public void await() {
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void setRequestObserver(StreamObserver<org.example.models.GuessRequest> requestObserver) {
		this.requestStreamObserver = requestObserver;
	}

}
