package LinkStateRouting;
import java.io.*;
import java.util.*;
/**
 * This program simulates the Link State Routing Algorithm and implements certain cases such as 
 * Creating a Network Topology | Building a Connection Table | Finds a Shortest Path from Source to Destination | 
 * Modifying the Topology | Determining Best Router for Broadcast. 
 * This algorithm uses Dijsktra's Algorithm to find the shortest path between 2 routers along with its minimum cost. 
 * @author Raj
 * @version 1.0
 * @since 11/19/2016
 *
 */

public class Main {

	/**
	 * Declarations And Initializations
	 */
	static int[][] matrix;
	static int rows;
	static int sourcerouter = -1;
	static int destrouter = -1;
	static int [] distance;
	static int [] interrouter;
	static boolean [] visited;
	static int[] parent;
	static int removerouter = -1;
	static List<Integer> routertrack;
	
	public static void main(String args[]) throws IOException {
		
		/**
		 * A switch case for the user choices to select the simulation step
		 * Implementing the simulation step which is entered by the user by choice
		 */
		int choice=0;					
		do{
			choice= mainMenu();			
			switch(choice){
			case 1:
				System.out.println("Create a Network Topology");
				System.out.println("===============================");
				System.out.println();
				
				Scanner s = new Scanner(System.in);												//User Input For the File Name
				System.out.println("Prompt: Input original network topology matrix data file:");
				System.out.print("Input: ");
				String filename = s.nextLine();
				boolean checkfile = new File(filename).exists();								//Checking the File if it Exists
				if(checkfile){
					matrix = networkTopology(filename);
				}
				else{
					System.out.println("The File Does Not Exists!");
					System.out.println();
				}
				break;
				
			case 2:
				if(matrix == null){																//Checking if the topology file exists
					System.out.println("Network Topology not found! \nPlease create a Network Topology by typing 1 in the Main Menu");
					System.out.println();
					break;
				}
				
				System.out.println("Build a Connection Table");
				Scanner r = new Scanner(System.in);											
				System.out.println("Select a source router");
				System.out.print("Input: ");
				
				try{
					sourcerouter = Integer.parseInt(r.nextLine());								//Asking the User to input the Source Router Number
					
					if(sourcerouter > rows || sourcerouter < 1 || matrix[sourcerouter-1][sourcerouter-1] == -1){  
						System.out.println("Invalid Router!! Please Enter a Valid Router.");
						System.out.print("Input: ");
						sourcerouter = Integer.parseInt(r.nextLine());							//Asking for user input if invalid router entered

					}
					
					connectionTable(sourcerouter-1);											//Calling the method for building the connection table
					displayConnectionTable(sourcerouter-1);										//Calling the method for displaying the connection table
					
				}
				catch(NumberFormatException e){
					System.out.println("Input is invalid!! Please Enter Valid Input.");			//Handling the user input if entered something else other than number
					System.out.println();
				}
				break;
			case 3:
				if(matrix == null){																//Checking if the topology file exists
					System.out.println("Network Topology not found! \nPlease create a Network Topology using Option 1 from Main Menu...");
					System.out.println();
					break;
				}
				if(sourcerouter == -1){															
					System.out.println("No Source Router!! \nPlease Enter a Source Router using Option 2 from Main Menu...");
					System.out.println();
					break;
				}
				
				Scanner d = new Scanner(System.in);												//Asking user input for the destination router. 
				System.out.println("Select A Destination Router");
				System.out.print("Input: ");
				
				try{
					destrouter = Integer.parseInt(d.nextLine());
					
					if(matrix[sourcerouter-1][sourcerouter-1] == -1){
						break;
					}
					if(destrouter > rows || destrouter < 1 || matrix[destrouter-1][destrouter-1] == -1){	 
						System.out.println("Invalid Router!! Please Enter a Valid Router.");
						System.out.print("Input: ");
						destrouter = Integer.parseInt(d.nextLine());							//Asking for user input if invalid router entered
					}
					
					shortestPathToDestination(sourcerouter-1, destrouter-1, interrouter);		//Calling the method to find the shortest path and cost between the
																								//source and the destination
				}
				catch(NumberFormatException e){
					System.out.println("Input is invalid!! Please enter valid input.");			//Handling the user input if entered something else other than number
					System.out.println();
				}
				break;
			case 4:
				System.out.println("Modifying a Topology");
				
				if(matrix == null){																//Checking if the topology file exists
					System.out.println("Network Topology not found! \nPlease create a Network Topology using Option 1 from Main Menu!");
					System.out.println();
					break;
				}
				
				Scanner m = new Scanner(System.in);
				System.out.println("Select A Router To Remove");
				System.out.print("Input: ");
				
				try{
					removerouter = Integer.parseInt(m.nextLine());								//Taking the user input for the router to be removed
					
					while(removerouter > matrix.length || removerouter < 1 || matrix[removerouter-1][removerouter-1] == -1){	
						System.out.println("Invalid Router!! Please Enter a Valid Router.");								
						System.out.print("Input: ");																						
						removerouter = Integer.parseInt(m.nextLine());							//Asking the user input if router entered is invalid
					}
						modifyTopology(removerouter-1);											//Calling the method for removing a router from the topology
						if(matrix[removerouter-1][removerouter-1] == -1){
							System.out.println("The Router ["+removerouter+"] is removed!");
							System.out.println();
						}
					
				}
				catch(NumberFormatException e){													//Handling the user input if entered something else other than number
					System.out.println("Input is invalid!! Please Enter Valid Input.");				
					System.out.println();
				}
				
				break;
			case 5:
				System.out.println("Best Router for Broadcast");	
				System.out.println("===============================");
				System.out.println();
				bestRouter();																	//Calling the method to find the best router with the shortest path to
				break;																			//to every other router
			case 6:
				System.out.println("Exit! Good Bye CS542... ");
				break;
			}
		}while(choice!=6);
	}

	/**
	 * This method removes a router and the Topology Matrix, Connection Table and the Shortest Path To Destination.
	 * It takes an input from user of the router number to be removed. The output will be an updated connection table
	 * and shortest path between 2 routers along with its cost.
	 * @param removerouter
	 */
	private static void modifyTopology(int removerouter) {										//Method to remove a router and updating the connection table 
		// TODO Auto-generated method stub														//and the shortest path if exists
		routertrack = new ArrayList<Integer>(matrix.length);									//Creating a list to store the removed routers				
		
		for(int i = 0; i < matrix.length; i++){													//Logic for removing a router from the topology
			matrix[i][removerouter] = -1;
			matrix[removerouter][i] = -1;
		}
		
		routertrack.add(removerouter);															//Adding the removed router to the List of removed routers
		
		if(sourcerouter != -1){																	//Checking if the source router is entered by the user
			if(sourcerouter-1 != removerouter){													//Checking if the source router is not the router which is removed
				connectionTable(sourcerouter-1);												//Updating the connection table
				displayConnectionTable(sourcerouter-1);											//Displaying the updated connection table
			
				if(destrouter != -1){															//Checking if the destination router is entered by the user
					if(destrouter-1 != removerouter){											//Checking if the destination router is the router which is removed
						shortestPathToDestination(sourcerouter-1, destrouter-1, interrouter);	//Updating and displaying the shortest path between the source and 
					}																			//the destination router along with its cost.
				}
			}
		}else{
			if(removerouter == sourcerouter-1){													//Checking if the source router is the router which is removed
    			System.out.println("This Router Is Removed! \nPlease Select Another Router From Option 2");    			
    		}
		}
	}

	/**
	 * This method displays the connection table after the Dijkstra's Logic is implemented 
	 * on the source router to every other routers.
	 */
	private static void displayConnectionTable(int sourcerouter) {								//Method for displaying the Connection Table
		// TODO Auto-generated method stub
		System.out.println("Router "+(sourcerouter+1)+" Connection Table");			
	    System.out.println("Destination	Interface");
	    System.out.println("================================");

        for (int i = 0; i < matrix.length; i++){												//Logic for getting the shortest path from source router to other routers
        	if(i == sourcerouter){
        		System.out.println("     "+(i+1)+"\t\t      -");									//Displaying the connection table
        	}else if(distance[i] == Integer.MAX_VALUE){
        		System.out.println("     "+(i+1)+"\t\t    None");
        	}else{
        		System.out.print("     "+(i+1)+"\t\t    ");
        				print(sourcerouter, i);
        				System.out.println();
        	}
        }
        System.out.println();
	}
	
	static void print(int sourcerouter, int destrouter){
		if(interrouter[destrouter] == -1){
    		return;
    	}
		print(sourcerouter, interrouter[destrouter]);
		
		if(interrouter[destrouter] == sourcerouter){
			System.out.printf("%3d", (destrouter+1));
		}
	}
	

	/**
	 * Method for determining the best router having the shortest path to all other routers.
	 * 
	 */
	private static void bestRouter() {										
		// TODO Auto-generated method stub
		List<Integer> costlist = new ArrayList<Integer>(matrix.length);		
		
		int mincost = Integer.MAX_VALUE;
		int bestrouter = 0;
		int cost = 0;
		for(int k = 0; k < matrix.length; k++){
			cost = 0;
			connectionTable(k);
			for(int c = 0; c < distance.length; c++){
				cost += distance[c];
			}
			
			if(cost <= 0 || cost == Integer.MAX_VALUE){
				continue;
			}else{
				costlist.add(cost);
				
				if(cost < mincost){
					mincost = cost;																//Storing the minimum cost for the router
					bestrouter = k+1;															//Storing the best router based on the minimum cost
				}
			}
		}
		if(costlist.isEmpty()){
			System.out.println("No Such Router");
		}else{
			System.out.println("The Best Router for Broadcast is "+bestrouter);
			System.out.println("The total cost is: "+mincost);
			System.out.println();
		}
	}

	/**
	 * Having the source router and the destination router, in this method 
	 * we find the shortest path between them along with the minimum cost
	 */
	private static void shortestPathToDestination(int sourcerouter, int destrouter, int[] interrouter) {
		// TODO Auto-generated method stub
		System.out.println("Shortest Path to Destination");
		
		if(distance[destrouter] == Integer.MAX_VALUE){
			System.out.println("Path Does Not Exist!!!");
			System.out.println();
			return;
		}
		
		List<Integer> shortestpath = new ArrayList<Integer>();
		System.out.println(Arrays.toString(distance));
		int temp = destrouter;
		while(temp != sourcerouter) {
			shortestpath.add(temp+1);
    		temp = interrouter[temp];
	    }
		shortestpath.add(sourcerouter+1);
		Collections.reverse(shortestpath);															//Reversing the shortest path for display
		

		System.out.println("The Shortest Path from Source ["+(sourcerouter+1)+"] to Destination ["+(destrouter+1)+"] is: ");
		Iterator<Integer> i = shortestpath.iterator();
		
		if(i.hasNext()){
			System.out.print(i.next());
		}
		while(i.hasNext()){
			
			System.out.print("-->"+i.next());														//Displaying the shortest path between the source and destination routers
		}
	
		System.out.println("\nThe Total Cost is: "+distance[destrouter]);
		System.out.println();
	}


	/**
	 * Method uses the Dijsktra's algorithm logic and calculates the minimum distance 
	 * and path between the source router and every other routers.
	 * Storing the minimum distances and the interface routers for displaying in the connection table
	 */
	private static void connectionTable(int sourcerouter) {
		// TODO Auto-generated method stub
		
		distance = new int [matrix.length];	  
		interrouter = new int [matrix.length];
	    visited = new boolean [matrix.length];
	    for (int i=0; i<distance.length; i++) 
	    {
	    	interrouter[sourcerouter] = -1;
	    	 distance[i] = Integer.MAX_VALUE;														//Initializing the distances to the max value
	    	 visited[i] = false;																	
	    }
	    distance[sourcerouter] = 0;
	    
	    for(int k = 0; k < distance.length - 1; k++){
	    	int nextrouter = nearestRouter(distance, visited);

	    	visited[nextrouter] =  true;
	    	
	    	for(int j=0; j< distance.length; j++){
	    		if(matrix[nextrouter][j] == -1){													
	    			matrix[nextrouter][j] = 0;
	    		}
	    		if(!visited[j] && matrix[nextrouter][j] != 0 && distance[nextrouter] <= Integer.MAX_VALUE && distance[nextrouter]+ matrix[nextrouter][j] <= distance[j]){
	    			distance[j] = distance[nextrouter] + matrix[nextrouter][j]; 
	    			interrouter[j] = nextrouter;																
	    		}
	    	}	    	
	    }
	}

	
	/**
	 * Calculating the minimum value within the distances stored for the routers
	 * It returns the position of the minimum router to Dijkstra's Implementation
	 */
	private static int nearestRouter(int[] distance, boolean[] visited) {
		// TODO Auto-generated method stub
		int minimumdist = Integer.MAX_VALUE;
		int nc = -1; 																				//node not connected or unvisited
		
		for(int i=0;i<distance.length;i++){
			if(!visited[i] && distance[i] <= minimumdist){
				nc = i;
				minimumdist = distance[i];
			}
		}
		return nc;																					//returning the index of the minimum distance	
	}

	
	/**
	 * This method contains the Main Menu, where it asks the user to enter a choice/command to simulate
	 * any of the specified simulations.
	 * It returns the user choice to the Main method to switch to that particular simulation method.
	 */
	private static int mainMenu() {
		// TODO Auto-generated method stub
		System.out.println("CS542 Link State Routing Simulator");
		System.out.println("========================================");
		System.out.println();
		System.out.println("(1) Create a Network Topology");
		System.out.println("(2) Build a Connection Table");
		System.out.println("(3) Shortest Path to Destination Router");
		System.out.println("(4) Modify a Topology");
		System.out.println("(5) Best Router for Broadcast");
		System.out.println("(6) Exit");
		System.out.println();
		
		Scanner ch = new Scanner(System.in);
		System.out.print("Master Command: ");
		int choice = 0;
		try{
			choice = Integer.parseInt(ch.nextLine());
			System.out.println();
			
			if(choice > 6 || choice < 1){
				System.out.println("No such Command!! Please enter valid command.");
				System.out.println();
			}
		}
		catch(NumberFormatException e){
			System.out.println("Input is invalid!! Please enter valid input.");
			System.out.println();
		}
		return choice;
	}

	/**
	 * Creating a Network topology taking the file containing the topology matrix
	 * Taking the topology matrix and converting it in to a 2D matrix and displaying the same.
	 */
	private static int[][] networkTopology(String filename){
		// TODO Auto-generated method stub
			try{
				
				System.out.println("Prompt: Review original topology matrix");
				
				Scanner countlines = new Scanner(new File(filename));
				rows = countlines.nextLine().split(" ").length;
				
				matrix = new int[rows][rows];
				countlines.close();
				

				Scanner srcin = new Scanner(new File(filename));
				List<String> lines = new ArrayList<String>();
				
				while(srcin.hasNextLine())
				{
					String line = srcin.nextLine().trim();
					if(line.isEmpty()==false){
					 lines.add(line);
					}
				}

				
				String[] val = new String[lines.size()];
				for(int i = 0; i < lines.size(); i++){
					int j = 0;
					String value = lines.get(i);
					
					val = value.split(" ");
					while(j < lines.size()){
						matrix[i][j] = Integer.parseInt(val[j++]);										//Creating the topology matrix
					}
				}

				System.out.println("------------------------------------------------------------");
				for(int[] row : matrix ){
					for(int col : row){
						System.out.printf("%3d", col);													//Displaying the topology matrix to user for review
					}
					System.out.println(" ");
				}
				System.out.println("------------------------------------------------------------");
				System.out.println();
				srcin.close();

				return matrix;
			}catch(Exception e){																		//Handling exceptions for the file
				System.out.println(e);
			}
		return null;
	}
}
