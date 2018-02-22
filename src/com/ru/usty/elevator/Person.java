package com.ru.usty.elevator;

public class Person implements Runnable {
	
	int srcFloor;
	int dstFloor;
	
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
		
	
		ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(this.srcFloor);
		ElevatorScene.scene.incrementNumberOfPeopleInElevator(1);
		
		try {
			ElevatorScene.destinationFloors[this.dstFloor].acquire();	// Person waits at destination semaphore 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ElevatorScene.scene.decrementNumberOfPeopleInElevator(1);
		ElevatorScene.scene.personExitsAtFloor(this.dstFloor);
	

	}
	

	

}
