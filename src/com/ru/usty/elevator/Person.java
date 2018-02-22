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
			//ElevatorScene.elevatorWaitMutex.acquire();	// Elevator needs to have mutual exclusion for this		
			//ElevatorScene.elevatorWaitMutex.release();
			System.out.println("Waiting");
			ElevatorScene.semFloor1.acquire();	// Wait	
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Released");
	
		ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(this.srcFloor);
		ElevatorScene.scene.incrementNumberOfPeopleInElevator(1);
		
		try {
			ElevatorScene.semFloor2.acquire();	// Person now waiting to arrive at second floor
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Im out!!");
		
		
		
		
		
	}
	

}
