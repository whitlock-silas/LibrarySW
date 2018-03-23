package librarysw;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Library 
{	static Scanner scanner = new Scanner(System.in);
	static Statement stmt = null;
	
	public static void main(String args[])
	{
		Connection c = null;
		
		try 
		{	//sets driver and gets connection to database
			 Class.forName("org.sqlite.JDBC");
			 c = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\whitl\\eclipse-workspace\\LibrarySW\\LibraryDB.db");
			 c.setAutoCommit(true);
			//creates statement for stmt
			 stmt = c.createStatement();
			 System.out.println("Thank you for choosing LibraryDB");//greets the user
			 int input = 1;
	
	       	while(input != 999)//this loop checks the users input to see if they're ready to exit the program. 
	        {
	       		//main menu output
	         System.out.print("\n-Enter 999 to quit-\nTo view all books enter 1\nTo search a for book enter 2\nTo view customer list enter 3\nTo check for overdue books enter 4: ");
	         input =  Integer.parseInt(scanner.nextLine());
	        	//a switch select case to determine the action that the user decided to make
	       switch(input)
	       {
	        case 1 :
	        //calls a method that outputs all the books in the database onto the console
	        	viewAllBooks();
	        	break;
	        
	        case 2 :
	        	input=0;
	        	 while(input == 0)//allows the user to search and rent as many books as they like before exiting the program
	               	 {
	        	  	System.out.print("Please enter the ID of the book you are searching for (Enter 0 to view all books): ");
	        	 	input = Integer.parseInt(scanner.nextLine());
	        	        	
	        	 	if(input == 0)
	        	       	{	 //if the user has forgotten the ID of the book, they are allowed the opertunity to look through the DB again
	        		 		viewAllBooks();
	        	       	}
	        	        else
	        	        {
	        	        	//calls a method that searches the DB for the Book ID number a and then it displays the books information and whether its available or not
	        	        	titleSearch(input, c);	
	        	        }
	               	 }
	        	 	break;
	        	 	
	        case 3:	
	        	//allows the Librarian to view all the customers in who have rented a book and has had their information entered into the system
	        	viewAllCustomers();
	        	break;
	        case 4:
	        	//although this portion of the program does not work, i was planning on allowing the librarian to see which books were being rented out
	        	//then i was going to give them the option to see how many days they had been gone for. But i was unable to get to that part
	        	overdueCheck();
	        	break;
	        	
	       }
	   }
	       	//program ends
	        System.out.print("\nThank you for using LibraryDB");
			stmt.close();
	        c.close();
	       
		}catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		
	}
	
	public static void viewAllBooks()
	{
		ResultSet rs;
		try {
			
			 rs = stmt.executeQuery("SELECT * FROM books;");
			while(rs.next())//this loop runs while the ResultSet has another line of data.
			{
				//it grabs the data for the bookID, title, author, and availability of the book
				int bookId = rs.getInt("bookID");
				int availability = rs.getInt("available");
				String title = rs.getString("title");
				String author = rs.getString("author");
				String available = "";
			
				//determines what kind of message should be shown when presenting this book
			if(availability == 0)
			{
				available = "available";
			}
			else if(availability == 1)
			{
				
				available = "**rented By user ID: " + rs.getString("rentedBy") + "**";
			}
			//prints books information to the console
			System.out.println("\nID#: " + bookId + " | " + title + " | " + author + " | " + available);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public static void titleSearch(int search, Connection c)
	{	
		ResultSet rs;
		scanner.reset();//i had issues with the scanner when i ran this portion of the program so i have it reset
		String input;
		int userInput = 0;
		boolean flag = false;
		try
		{
			stmt = c.createStatement();				
			rs = stmt.executeQuery("SELECT * FROM books WHERE bookID='" + search + "';");
			
			while(rs.next())
			{//again, this loop runs while the result set has a row of information
				int availability = rs.getInt("available");
				String title = rs.getString("title");
				String author = rs.getString("author");
				String available ="";
				
				//determines if the book is available and if it is, it allows the librarian the option to rent it out
				if(availability == 0)
				{
					available = "is available for renting \nWould you like to rent this book? (1 for yes/ 0 for no): ";
					flag = true;
				}
				else if(availability == 1)
				{
					available = "is not available for renting";
				}
				
					System.out.print("\n"+author + "'s book "+title+ " "+available + "\n");
				
				if(flag)
				{//checks to see if the customer wants to rent it or not
					userInput =Integer.parseInt( scanner.nextLine());
					if(userInput == 1)
					{//if they choose to rent it, this method is called which takes them to the portion of the project that allows the librarian to rent the book out
						rentBook(c, search);
						
					}
				}
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void rentBook( Connection c, int bookId)
	{
		//Creates date formatter and gets local time (I have no idea if the date and formatter are working properly)
		ResultSet rs;
		
		String name, searchName, address, state;
		int counter = 0;
		boolean flag = false;
		int cusID = 0, input;
		try
		{

			while(!flag)
			{	//displays all the customers ID#'s, names, addresses. and States 
				//and if theyre not in the DB, it allows the librarian to enter their information into the DB
				rs = stmt.executeQuery("SELECT cusID, name, address, state FROM customers; ");
				System.out.println("--CUSTOMERS--");
			while(rs.next())
			{
				cusID = rs.getInt("cusID");
				searchName = rs.getString("name");
				address = rs.getString("address");
				state = rs.getString("state");
				
				System.out.println(cusID + " | " + searchName + " | " + address + " | " + state);
				counter++;
			}
				
				System.out.println("\nIf customer is not listed above please enter 0");
				System.out.print("\nPlease enter the customers ID# to rent out the book: ");
				input = Integer.parseInt(scanner.next());
				
				
				if(input != 0 && input <= counter)
				{
					//overloaded method call to other rentBook method and passes the connection and the entered ID number
					rentBook(input, c, bookId);
					flag = true;
				}
				else if(input == 0)
				{
					//calls addCustomer method and passes the connection
					addCustomer(c);
				}
				else
				{
					System.out.println("**Invalid ID Number**");
				}
				counter = 0;
			}
			
				
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
				
	}
	
	public static void addCustomer(Connection c)
	{
		String name, telephone, address, state;
		String sql = "INSERT INTO customers(name, telephone, address, state) VALUES(?, ?, ?, ?);";
		boolean flag = false;
		try
		{
			//allows new users to be entered into the Database
			
			while(!flag)
			{
				System.out.print("Enter the first and last name of the customer (Hyphenated): ");
				name = scanner.next();
				scanner.nextLine();
				System.out.print("Enter the customer's telephone number (i.e. xxx-xxx-xxxx): ");
				telephone = scanner.next();
				scanner.nextLine();
				System.out.print("Enter the customer's address and zip code (Hyphenated): ");
				address = scanner.next();
				scanner.nextLine();
				System.out.print("Enter the state where the customer currently lives (i.e. OH): ");
				state = scanner.next();
				scanner.nextLine();
				
				
				
				if(name != "" && telephone != "" && address != "" && state != "")
				{
					
					PreparedStatement pstmt = c.prepareStatement(sql);
					pstmt = c.prepareStatement(sql);
					pstmt.setString(1, name);
					pstmt.setString(2, telephone);
					pstmt.setString(3, address);
					pstmt.setString(4, state);
					
					pstmt.executeUpdate();
					System.out.println("Customer added");
					flag = true;
					
				}
			
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void rentBook(int input, Connection c, int bookId)
	{
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date time = null;
		//allows user to rent out books
		try
		{
			String sql = "UPDATE books SET rentedBy =?, available=1, rentedOn =? WHERE bookID=?;";
			PreparedStatement pstmt = c.prepareStatement(sql);
			
			pstmt.setInt(1, input);
			pstmt.setDate(2,  time );
			pstmt.setInt(3, bookId);
			
			//executes query that updates who is renting, when they rented it, and availability of the book	
			pstmt.executeUpdate();
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void overdueCheck()
	{ 
		ResultSet rs;
		String sql = "SELECT bookID, rentedBy, title, rentDate FROM books WHERE rentDate=;" ;
		String available, title;
		Date rentDate;
		int cusID, bookID;
		
		try
		{
			rs= stmt.executeQuery(sql);
			
			while(rs.next())
			{
				title = rs.getString("title");
				rentDate = rs.getDate("rentDate");
				cusID = rs.getInt("rentedBy");
				bookID = rs.getInt("bookID");
				
				System.out.println("Book ID #: " + bookID + " " + title + " Rented on " + rentDate + " by user ID #: " + cusID);
			}
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	public static void viewAllCustomers()
	{
		//displays all the customers information onto the console.
		ResultSet rs;
		String sql = "SELECT * FROM customers;";
		String name, telephone, address, state;
		int id;
		
		
		try
		{
			rs = stmt.executeQuery(sql);
			
			while(rs.next())
			{
				id = rs.getInt("cusID");
				name =  rs.getString("name");
				telephone = rs.getString("telephone");
				address = rs.getString("address");
				state = rs.getString("state");
				
				System.out.println("\nID#: " + id  + " | " + name + " | " + telephone + " | " + address + " | " + state);
			}
		
		
		
		}catch(SQLException e)
		{
			
			e.printStackTrace();
			
		}
		
	}
	
	
	
}
