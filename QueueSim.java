import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

class Customer{
	private int primaryArrivingTime; 
	private int primaryProcessTime; 
	private int timeInPQueue; 
	private int timeInSQueue; 
	private int secondArrivingTime; 
	private int secondProcessTime; 
	private int secondQArriveTime;
	
	public Customer(int primaryArrivingTime, int primaryProcessTime, int secondProcesTime) {
		this.primaryArrivingTime = primaryArrivingTime;
		this.primaryProcessTime = primaryProcessTime;
		this.secondProcessTime = secondProcesTime;
	}
	public int getPrimaryArriveTime() {
		return primaryArrivingTime;
	}
	public int getPrimaryProcessTime() {
		return primaryProcessTime;
	}
	public int getSecArrivingTime() {
		return secondArrivingTime;
	}
	public void setSecArrivingTime(int secondArrivingTime) {
		this.secondArrivingTime = secondArrivingTime;
	}
	public int getSecondProcessTime() {
		return secondProcessTime;
	}
	
	public int getTimeInPQueue() {
		return timeInPQueue;
	}
	public int getTimeInSQueue() {
		return timeInSQueue;
	}
	public void setTimeInPQueue(int timeInPQueue) {
		this.timeInPQueue = timeInPQueue;
	}
	public void setTimeInSQueue(int timeInSQueue) {
		this.timeInSQueue = timeInSQueue;
	}
	public void setTimeArriveSecondQ(int secondQArriveTime) {
		this.secondQArriveTime = secondQArriveTime;
	}
	public int getTimeArriveSecondQ() {
		return secondQArriveTime;
	}
}
class Server{
	private int serverNo; 
	private String status = "available";
	private int idleTime = 0;
	private int remaining;
	private Customer currentCustomer;
	
	public Server(int serverNo) {
		this.serverNo = serverNo; 
	}
	public int getserverNo() {
		return serverNo;
	}
	public String getStatus() {
		return status;
	}
	public int getIdleTime() {
		return idleTime;
	}
	public int getRemaining() {
		return remaining;
	}
	public Customer getCurrentCustomer() {
		return currentCustomer;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}
	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}
	public void setCurrentCustomer(Customer currentCustomer) {
		this.currentCustomer = currentCustomer;
	}
	public void clear() {
		setCurrentCustomer(null);
	}
}
class Queue{ 
	private Customer[] arr; 
	private int front; 
	private int rear; 
	private int count;
	private int capacity;
	private int totalCustomer;
	
	public Queue(int size){
		arr = new Customer[size]; 
		capacity = size; 
		front = 0; 
		rear = -1; 
		count = 0; 
	}
	public Customer dequeue() {
		if(isEmpty()) {
			return null;
		}
		Customer x = arr[front];
		front = (front+1) % capacity; 
		count--; 
		
		return x; 
	}
	public void enqueue(Customer c) {
		if(isFull()) {
			return;
		}
		rear = (rear + 1) % capacity; 
		arr[rear] = c; 
		count++; 
	}
	public Customer peek() {
		if(isEmpty()) {
			System.out.println("Queue is Empty");
		}
		return arr[front]; 
	}
	public int customerInQueue() {
		totalCustomer += count;
		return count; 
	}
	public boolean isEmpty() {
		return (customerInQueue() == 0);
	}
	public boolean isFull() {
		return (customerInQueue() == capacity); 
	}
	public Customer[] getQueue() {
		return arr; 
	}
	public int totalCustomerInQueue() {
		return totalCustomer;
	}
}
public class QueueSim {
	private static ArrayList<Customer> arr = new ArrayList<>();
	private static ArrayList<Server> primaryServers = new ArrayList<>();
	private static ArrayList<Server> secondaryServers = new ArrayList<>();
	private static ArrayList<Integer> queueLength1 = new ArrayList<>();	
	private static ArrayList<Integer> queueLength2 = new ArrayList<>();	
	private static int pTimeInQueue = 0; 
	private static int sTimeInQueue = 0; 		
	private static int totalRequestTime1 = 0; 
	private static int totalRequestTime2 = 0; 
	private static double customerWaitingInQueue1 = 0;
	private static double customerWaitingInQueue2 = 0;
	private static int lastRequest = 0 ; 
	private static int NoOfObjects = 0;
	private static int total1 = 0; 
	private static int total2 = 0; 
	public static void main(String[] arg) {
		readData();
		boolean condition = false;
		Queue primaryQueue = new Queue(arr.size()); 
		Queue secondQueue = new Queue(arr.size()); 
		int minute = 1; 
		int arr_index  = 0;
		int cusComplete=0;
		while(!condition) {
			while(arr_index <arr.size()) {
				Customer newcomer = arr.get(arr_index);
				if(newcomer.getPrimaryArriveTime() == minute) {
					arr_index++;
					primaryQueue.enqueue(newcomer);
					NoOfObjects+=1;
				}else {
					break; 
				}
			}			
			int i = 0; 
			while(i<primaryServers.size() && primaryQueue.isEmpty() == false) {
					Server server = primaryServers.get(i);
					if(server.getStatus().equals("available")) {
						Customer servingCustomer = primaryQueue.dequeue();
						pTimeInQueue = minute - servingCustomer.getPrimaryArriveTime();
						servingCustomer.setTimeInPQueue(pTimeInQueue);
						server.setCurrentCustomer(servingCustomer);
						totalRequestTime1 += servingCustomer.getPrimaryProcessTime();
						server.setRemaining(servingCustomer.getPrimaryProcessTime());
						server.setStatus("busy");
					}
					i++;
			}
			queueLength1.add(primaryQueue.customerInQueue());
			for(int j = 0; j<primaryServers.size(); j++) {
				Server s = primaryServers.get(j);
				if (s.getStatus().equals("available")) {
					
					int temp_idle_time = s.getIdleTime() + 1; 
					s.setIdleTime(temp_idle_time);
				}else {
					if(s.getRemaining() == 0) {
						secondQueue.enqueue(s.getCurrentCustomer());
						s.getCurrentCustomer().setTimeArriveSecondQ(minute);
						s.clear();
						s.setStatus("available");
					}
					int servingTime = s.getRemaining() - 1; 
					s.setRemaining(servingTime);
				}
			}
			int m = 0;
			while(secondQueue.isEmpty()!=true && m<secondaryServers.size()) { 
					Server server = secondaryServers.get(m);
					if(server.getStatus().equals("available")) {
						Customer servingCustomer = secondQueue.dequeue();
						servingCustomer.setSecArrivingTime(minute);
						sTimeInQueue = minute - servingCustomer.getTimeArriveSecondQ();
						servingCustomer.setTimeInSQueue(sTimeInQueue);
						totalRequestTime2 += servingCustomer.getSecondProcessTime();
						server.setCurrentCustomer(servingCustomer);
						server.setRemaining(servingCustomer.getSecondProcessTime());
						server.setStatus("busy");
					}
					m++;
			}
			queueLength2.add(secondQueue.customerInQueue());
			for(int k = 0; k<secondaryServers.size(); k++) {
				Server s = secondaryServers.get(k);
				if (s.getStatus().equals("available")) {
					int temp_idle_time = s.getIdleTime() + 1; 
					s.setIdleTime(temp_idle_time);
				}else {
					if(s.getRemaining() == 0) {
						s.clear();
						cusComplete++;
						s.setStatus("available");
					}
					int servingTime = s.getRemaining() - 1; 
					s.setRemaining(servingTime);		
				}
			}
			minute++;
			if(cusComplete==(arr.size()-1)) {
				condition=true;
			}	
		}
		lastRequest = minute - 1; 
		for(Customer c: arr) {
			if(c.getTimeInPQueue()>=1)
				customerWaitingInQueue1++;
			total1+=c.getTimeInPQueue();
		}
		for(Customer c: arr) {
			if(c.getTimeInSQueue()>=1)
				customerWaitingInQueue2++;
			total2+=c.getTimeInSQueue();
		}
		displayStatistic();
	}
	public static void displayStatistic() {
		System.out.println("Number of people served " + NoOfObjects);
		System.out.println("Time last request is complete " + lastRequest);
		
		int average_total_service_time = (totalRequestTime1 + totalRequestTime2)/NoOfObjects;
		System.out.println("Average total service time " + average_total_service_time);
		System.out.println("------------------------------------");
		int sumIdle1 = 0; 
		for(Server s: primaryServers) {
			sumIdle1+=s.getIdleTime();
			System.out.println("Idle time of primary server " + s.getserverNo() + ": "+ s.getIdleTime());
		}
		System.out.println("Total idle time of primary servers " + sumIdle1);
		System.out.println("------------------------------------");
		int sumIdle2 = 0; 
		for(Server s: secondaryServers) {
			sumIdle2+=s.getIdleTime();
			System.out.println("Idle time of secondary server " + s.getserverNo() + ": "+ s.getIdleTime());
		}
		System.out.println("Total idle time of secondary servers " + sumIdle2);
		System.out.println("------------------------------------");
		double t = Double.valueOf(total1)/Double.valueOf(customerWaitingInQueue1);
		System.out.printf("Average total time in queue (primary) %.2f \n", t);		
		double t2 = Double.valueOf(total2)/Double.valueOf(customerWaitingInQueue2);
		System.out.printf("Average total time in queue (secondary) %.2f \n", t2);
		double avg = Double.valueOf(total1 + total2)
				/Double.valueOf(customerWaitingInQueue1 + customerWaitingInQueue2);
		System.out.printf("Average total time in queue (both) %.2f \n", avg);
		System.out.println("------------------------------------");
		System.out.printf("Average length of primary queue %.2f \n", Double.valueOf(customerWaitingInQueue1)/Double.valueOf(lastRequest));
		System.out.printf("Average length of secondary queue %.2f \n", Double.valueOf(customerWaitingInQueue2)/Double.valueOf(lastRequest));
		System.out.printf("Average length of both queue %.2f \n", Double.valueOf(customerWaitingInQueue1+customerWaitingInQueue2)
				/Double.valueOf(lastRequest));
		System.out.println("------------------------------------");
		System.out.println("Maximum length of Primary Queue " + Collections.max(queueLength1));
		System.out.println("Maximum length of Secondary Queue " + Collections.max(queueLength2));
		System.out.println("Maximum length of both Queue " + Collections.max(queueLength2));
		System.out.println();
	}
	public static void readData() {
		File data = new File("A2data6.txt");
		ArrayList<String> customers = new ArrayList<>();
		try(Scanner reader = new Scanner(data)){
			while(reader.hasNext()) {
				String oneLine = reader.nextLine();
				customers.add(oneLine); 
			}
		}
		catch (FileNotFoundException e) {
			System.out.println(e);
		}
		String[] servers = customers.get(0).split("\t"); 
		for(int i = 1; i<=Integer.valueOf(servers[0]); i++) {
			Server primServer = new Server(i);
			primaryServers.add(primServer);
		}
		for(int i = 1; i<=Integer.valueOf(servers[1]); i++) {
			Server secServer = new Server(i);
			secondaryServers.add(secServer);
		}
		for(int i = 1; i<customers.size(); i++) {
			String s = customers.get(i);
			String[] temp = s.split("\t");
			Customer c = new Customer(Integer.valueOf(temp[0]),Integer.valueOf(temp[1]), Integer.valueOf(temp[2]));
			arr.add(c);
		}
	}
}
