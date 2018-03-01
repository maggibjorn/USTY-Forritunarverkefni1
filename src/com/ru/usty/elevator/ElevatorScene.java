package com.ru.usty.elevator;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * The base function definitions of this class must stay the same
 * for the test suite and graphics to use.
 * You can add functions and/or change the functionality
 * of the operations at will.
 *
 */

public class ElevatorScene {

	//TO SPEED THINGS UP WHEN TESTING,
	//feel free to change this.  It will be changed during grading
	
	public static boolean elevatorsCanStop;
	/*Semaphores in system*/
	public static Semaphore elevatorWaitMutex;
	public static Semaphore personCountMutex;
	public static Semaphore elevatorChangeFloorMutex;
	public static Semaphore elevatorPersonCountMutex;
	public static Semaphore exitedCountMutex;
	public static Semaphore elevatorAvailableAtFloorMutex;
	public static Semaphore[] sourceFloors;
	public static Semaphore[][] destinationFloors;	// First index is floor number, second index is elevator number
	
	/*--------------------*/
	
	public int[] elevatorIdAvailableAtFloor; // Index is floor number, value is elevator ID
	public static ElevatorScene scene;
	
	public static final int VISUALIZATION_WAIT_TIME = 500;  //milliseconds

	public int numberOfFloors;
	private int[] numberOfPeopleInElevator; // TODO: This will probably be an indexed array
	private int numberOfElevators;
	
	private Elevator[] elevators = null;
	private Thread[] elevatorThreads = null;

	ArrayList<Integer> personCount;	// Number of people waiting at each floor
	ArrayList<Integer> exitedCount = null;	// Number of people that have exited each floor

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {
		/**
		 * Important to add code here to make new
		 * threads that run your elevator-runnables
		 * 
		 * Also add any other code that initializes
		 * your system for a new run
		 * 
		 * If you can, tell any currently running
		 * elevator threads to stop
		 */
		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;
		
		elevatorIdAvailableAtFloor = new int[this.getNumberOfFloors()];	// Holds info about which elevator is available on each floor
		this.setNumberOfFloors(numberOfFloors);
		this.setNumberOfElevators(numberOfElevators);
		elevatorsCanStop = true;
		numberOfPeopleInElevator = new int[this.getNumberOfElevators()];
		for (int i = 0; i < this.getNumberOfElevators(); i++) {
			numberOfPeopleInElevator[i] = 0;	// Initialize elevator counters
		}
		
		if(this.elevatorThreads != null) {
			for (int i = 0; i < elevatorThreads.length; i++) {
				if(this.elevatorThreads[i].isAlive()) {
					try {
						this.elevatorThreads[i].join();	
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}

		}
		
		elevatorsCanStop = false;
		scene = this;
		
		sourceFloors = new Semaphore[this.getNumberOfFloors()];
		destinationFloors = new Semaphore[this.getNumberOfFloors()][this.getNumberOfElevators()];
		for (int i = 0; i < this.getNumberOfFloors(); i++) {
			sourceFloors[i] = new Semaphore(0);			// Semaphore for each incoming floor in system
		}
		
		for (int i = 0; i < this.getNumberOfFloors(); i++) {
			for (int j = 0; j < this.getNumberOfElevators(); j++) {
				destinationFloors[i][j] = new Semaphore(0);	// Semaphore for destination floor i and elevator j
			}
		}
		
		personCountMutex = new Semaphore(1);	
		elevatorWaitMutex = new Semaphore(1);
		elevatorPersonCountMutex = new Semaphore(1);
		elevatorChangeFloorMutex = new Semaphore(1);
		elevatorAvailableAtFloorMutex = new Semaphore(1);
		
		elevators = new Elevator[this.getNumberOfElevators()];
		elevatorThreads = new Thread[this.getNumberOfElevators()];
		for (int i = 0; i < this.getNumberOfElevators(); i++) {
			elevators[i] = new Elevator(i);
			elevatorThreads[i] = new Thread(elevators[i]);	
			elevatorThreads[i].start();	// Start thread for each elevator in system
		}

		personCount = new ArrayList<Integer>();
		for(int i = 0; i < numberOfFloors; i++) {
			this.personCount.add(0);
		}

		if(exitedCount == null) {
			exitedCount = new ArrayList<Integer>();
		}
		else {
			exitedCount.clear();
		}
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.exitedCount.add(0);
		}
		exitedCountMutex = new Semaphore(1);
	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {
		/**
		 * Important to add code here to make a
		 * new thread that runs your person-runnable
		 * 
		 * Also return the Thread object for your person
		 * so that it can be reaped in the testSuite
		 * (you don't have to join() yourself)
		 */
		Thread thread = new Thread(new Person(sourceFloor, destinationFloor));
		thread.start();
		incrementNumberOfPeopleWaitingAtFloor(sourceFloor);
		
		return thread;  // The base system will take care of waiting for all person threads to finish
	}

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int elevator) {
		return elevators[elevator].currentFloor;
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int elevator) {
		return numberOfPeopleInElevator[elevator];
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {
		return personCount.get(floor);
	}
	
	public void decrementNumberOfPeopleWaitingAtFloor(int floor) {
		try {
			personCountMutex.acquire();
			personCount.set(floor, (personCount.get(floor) - 1));
			personCountMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void incrementNumberOfPeopleWaitingAtFloor(int floor) {
		try {
			personCountMutex.acquire();
			personCount.set(floor, (personCount.get(floor) + 1));
			personCountMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void decrementNumberOfPeopleInElevator(int elevator) {
		try {
			elevatorPersonCountMutex.acquire();
			numberOfPeopleInElevator[elevator]--;
			elevatorPersonCountMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void incrementNumberOfPeopleInElevator(int elevator) {
		try {
			elevatorPersonCountMutex.acquire();
			numberOfPeopleInElevator[elevator]++;
			elevatorPersonCountMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Base function: definition must not change, but add your code if needed
	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfFloors(int numberOfFloors) {
		this.numberOfFloors = numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public int getNumberOfElevators() {
		return numberOfElevators;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfElevators(int numberOfElevators) {
		this.numberOfElevators = numberOfElevators;
	}

	//Base function: no need to change unless you choose
	//				 not to "open the doors" sometimes
	//				 even though there are people there
	public boolean isElevatorOpen(int elevator) {
		return isButtonPushedAtFloor(getCurrentFloorForElevator(elevator));
	}
	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public boolean isButtonPushedAtFloor(int floor) {
		return (getNumberOfPeopleWaitingAtFloor(floor) > 0);
	}

	//Person threads must call this function to
	//let the system know that they have exited.
	//Person calls it after being let off elevator
	//but before it finishes its run.
	public void personExitsAtFloor(int floor) {
		try {
			
			exitedCountMutex.acquire();
			exitedCount.set(floor, (exitedCount.get(floor) + 1));
			exitedCountMutex.release();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public int getExitedCountAtFloor(int floor) {
		if(floor < getNumberOfFloors()) {
			return exitedCount.get(floor);
		}
		else {
			return 0;
		}
	}
	
	public int getAvailableElevatorAtFloor(int floor) {
		return this.elevatorIdAvailableAtFloor[floor];
	}
	
	public void setAvailableElevatorAtFloor(int elevatorId, int floor) {
		try {
			elevatorAvailableAtFloorMutex.acquire();
			this.elevatorIdAvailableAtFloor[floor] = elevatorId;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
