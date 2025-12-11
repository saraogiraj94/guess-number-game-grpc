package org.example;

public class Main {

	public static void main(String[] args) {
		GrpcServer.create(new GameService()).start().await();
	}
}
