package com.ru.usty.elevator;

public class Elevator implements Runnable {

	public int currentFloor = 0;	// Starting floor is 0
	public boolean elevatorGoingUp;	// Elevator starts by going up
	public int elevatorId;
	
	public Elevator(int numberOfElevator) {
		this.elevatorId = numberOfElevator;
	}
	
	//public static boolean elevetorIsInCriticalSection = false;
	
	
	@Override
	public void run() {
		while (true) {
			
			if(ElevatorScene.elevatorsCanStop) {
				return;
			}
			
			this.letPeopleOutAtFloor();
			this.elevatorLetsPeopleInOnFloor();
			this.changeFloor();
			
		
		}
		
	}
	
	private void elevatorLetsPeopleInOnFloor() {
		//System.out.println("Elevator " + this.elevatorId + " letting in on floor " + this.currentFloor);	
		int vacantSlots = 6 - ElevatorScene.scene.getNumberOfPeopleInElevator(this.elevatorId);
		ElevatorScene.scene.setAvailableElevatorAtFloor(this.elevatorId, this.currentFloor);
		for (int i = 0; i < vacantSlots; i++) {
			ElevatorScene.sourceFloors[this.currentFloor].release();	// Open up floor for person threads
		}
		
		sleepElevatorThread();
		ElevatorScene.elevatorAvailableAtFloorMutex.release();
		
		int offset = 6 - ElevatorScene.scene.getNumberOfPeopleInElevator(this.elevatorId);
		for (int i = 0; i < offset; i++) {
			try {
				ElevatorScene.sourceFloors[this.currentFloor].acquire();	// Fixing semaphore at floor before leaving
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	private void changeFloor() {
		
		try {
			ElevatorScene.elevatorChangeFloorMutex.acquire();
			
			if (this.currentFloor == 0) {
				// At bottom
				this.elevatorGoingUp = true;
				this.currentFloor++;
			}
			else if (this.currentFloor == ElevatorScene.scene.numberOfFloors-1) {
				// At top
				this.elevatorGoingUp = false;
				this.currentFloor--;
			}
			else if ((this.currentFloor < ElevatorScene.scene.numberOfFloors-1) && this.elevatorGoingUp) {
				// Traveling up
				this.currentFloor++;
			} 
			else if ((this.currentFloor > 0) && !this.elevatorGoingUp) {
				// Traveling down
				this.currentFloor--;
			}
			
			ElevatorScene.elevatorChangeFloorMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void letPeopleOutAtFloor() {
		//System.out.println("Elevator " + this.elevatorId + " with " + ElevatorScene.scene.getNumberOfPeopleInElevator(this.elevatorId) + "people letting out at floor " + this.currentFloor);
		int peopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(this.elevatorId);
		for (int i = 0; i < peopleInElevator; i++) {
			ElevatorScene.destinationFloors[this.currentFloor][this.elevatorId].release();	
		}
		
		sleepElevatorThread();
		
		// Fixing semaphore at floor before leaving
		int offset = ElevatorScene.scene.getNumberOfPeopleInElevator(this.elevatorId);
		for (int i = 0; i < offset; i++) {
			try {
				ElevatorScene.destinationFloors[this.currentFloor][this.elevatorId].acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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
