package com.ru.usty.elevator;

public class Elevator implements Runnable {

	public int currentFloor;
	
	//public static boolean elevetorIsInCriticalSection = false;
	
	
	@Override
	public void run() {
		while (true) {
			
			if(ElevatorScene.elevatorsCanStop) {
				return;
			}
			System.out.println("Hi from elevator");
			elevatorArrivesAtFloor(0);
			currentFloor = 1;
			sleepElevatorThread();
			int peopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(1);
			for (int i = 0; i < peopleInElevator; i++) {
				ElevatorScene.semFloor2.release();
				ElevatorScene.scene.decrementNumberOfPeopleInElevator(1);
				ElevatorScene.scene.personExitsAtFloor(1);
				System.out.println("Person thread cleaning up on 2nd floor");
			}
		}
		
	}
	
	private void elevatorArrivesAtFloor(int floor) {
		currentFloor = floor;
		
		int vacantSlots = 6 - ElevatorScene.scene.getNumberOfPeopleInElevator(1);
		for (int i = 0; i < vacantSlots; i++) {
			ElevatorScene.semFloor1.release();	// Let persons into elevator
		}
		
		sleepElevatorThread();
		
		int offset = 6 - ElevatorScene.scene.getNumberOfPeopleInElevator(1);
		for (int i = 0; i < offset; i++) {
			try {
				ElevatorScene.semFloor1.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sleepElevatorThread();
	}
	
	private void sleepElevatorThread() {
		try {
			Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}


	
	
	
	

}
