package org.example;

import io.grpc.stub.StreamObserver;
import org.example.models.GuessNumberGrpc;
import org.example.models.GuessRequest;
import org.example.models.GuessResponse;
import org.example.models.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class GameService extends GuessNumberGrpc.GuessNumberImplBase {

	Logger log = LoggerFactory.getLogger(GameService.class);
	private Integer expectedNumber;
	private int attempts;

	@Override
	public StreamObserver<GuessRequest> makeGuess(StreamObserver<GuessResponse> responseObserver) {

		var requestObserver = new StreamObserver<GuessRequest>() {
			@Override
			public void onNext(GuessRequest guessRequest) {
				//Check if the server has value to be guessed
				if (expectedNumber == null) {
					expectedNumber = ThreadLocalRandom.current().nextInt(101);
					log.info("Generated expectedNumber: {}", expectedNumber);
				}

				attempts = attempts + 1;

				var guessNumber = guessRequest.getGuess();
				var outCome = Result.TOO_HIGH;
				if (guessNumber == expectedNumber) {
					outCome = Result.CORRECT;
				} else if (guessNumber < expectedNumber) {
					outCome = Result.TOO_LOW;
				}

				var response = GuessResponse.newBuilder().setAttempt(attempts).setResult(outCome).build();
				responseObserver.onNext(response);
				if (outCome.equals(Result.CORRECT)) {
					responseObserver.onCompleted();
					expectedNumber = null;
				}

			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onCompleted() {
				log.info("on completed called on server ");
				responseObserver.onCompleted();
			}
		};

		return requestObserver;
	}
}
