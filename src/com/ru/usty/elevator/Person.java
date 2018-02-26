package com.ru.usty.elevator;

public class Person implements Runnable {
	
	private int srcFloor;
	private int dstFloor;
	private int personCurrentElevator;
	
	public Person(int sourceFloor, int destFloor) {
		this.srcFloor = sourceFloor;
		this.dstFloor = destFloor;
	}
	
	
	@Override
	public void run() {
		try {
			ElevatorScene.sourceFloors[this.srcFloor].acquire();	// Wait at incoming floor semaphore
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("Person going into elevator " + ElevatorScene.scene.getAvailableElevatorAtFloor(this.srcFloor));
		this.personCurrentElevator = ElevatorScene.scene.getAvailableElevatorAtFloor(this.srcFloor);
		ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(this.srcFloor);
		ElevatorScene.scene.incrementNumberOfPeopleInElevator(this.personCurrentElevator); 	// Increment available elevator
		
		try {
			ElevatorScene.destinationFloors[this.dstFloor][this.personCurrentElevator].acquire();	// Person waits at corresponding elevator at destination floor 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ElevatorScene.scene.decrementNumberOfPeopleInElevator(this.personCurrentElevator);
		ElevatorScene.scene.personExitsAtFloor(this.dstFloor);
	

	}
	

	

}
