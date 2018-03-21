package librarysw;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Library 
{	static Scanner scanner = new Scanner(System.in);
	static Statement stmt = null;
	public static void main(String args[])
	{
		Connection c = null;
		
		try 
		{	
			 Class.forName("org.sqlite.JDBC");
			 c = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\whitl\\eclipse-workspace\\LibrarySW\\LibraryDB.db");
			 c.setAutoCommit(true);
			
			 System.out.println("Thank you for choosing LibraryDB");
			 String sql = "SELECT * FROM books;";//The Default option for users will be to view all books in the database
			 stmt = c.createStatement();
	         ResultSet rs = stmt.executeQuery(sql);
			 int input = 0;
			 boolean flag = false;
			 
	        while(!flag)
	        {
	        
	         System.out.print("To view all books enter 1:\nTo search a for book enter 2: ");
	         input =  Integer.parseInt(scanner.nextLine());
	         if(input == 1)
	         {
	        	 viewAllBooks(rs);
	         }
	         else if(input == 2)
	         {
	        	 System.out.print("Please enter the ID of the book you are searching for: ");
	        	 input = Integer.parseInt(scanner.nextLine());
	        	 titleSearch(input, rs, c);	
	         }
	        
	         System.out.print("To exit enter 0\nEnter any number to continue using LibraryDB: ");
	         input = Integer.parseInt(scanner.next());
	         if(input == 0)
	         {
	        	 
	        	 flag = true;
	         }
	         
	        scanner.reset();
	        }
	
			stmt.close();
			rs.close();
	        c.close();
	       
		}catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		
	}
	
	public static void viewAllBooks(ResultSet rs)
	{
		
		try {
			System.out.println("\nBook ID");
			while(rs.next())
			{
				int bookId = rs.getInt("bookID");
				int availability = rs.getInt("available");
				String title = rs.getString("title");
				String author = rs.getString("author");
				String available = "";
			
			if(availability == 1)
			{
				available = "available\n";
			}
			else if(availability == 0)
			{
				
				available = "rented By" + rs.getString("rentedBy") + "\n";
			}
			
			System.out.println("\n" + bookId + " " + title + " " + author + " " + available);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public static void titleSearch(int search, ResultSet rs, Connection c)
	{
		scanner.reset();
		String input;
		int userInput = 0;
		boolean flag = false;
		try
		{
			stmt = c.createStatement();
			rs = stmt.executeQuery("SELECT * FROM books WHERE bookID='" + search+ "';");
			
			while(rs.next())
			{
				int availability = rs.getInt("available");
				String title = rs.getString("title");
				String author = rs.getString("author");
				String available ="";
				
				if(availability == 1)
				{
					available = "is available for renting \nWould you like to rent this book? (1 for yes/ 0 for no): ";
					flag = true;
				}
				else if(availability == 0)
				{
					available = "is not available for renting";
				}
				
					System.out.print("\n"+author + "'s book "+title+ " "+available + "\n");
				
				if(flag)
				{
					userInput =Integer.parseInt( scanner.nextLine());
					if(userInput == 1)
					{
						rentBook(rs, c, search);
						
					}
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void rentBook(ResultSet rs, Connection c, int bookId)
	{
		//Creates date formatter and gets local time
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate time = LocalDate.now();
		String name, searchName, address, state;
		int cusID, input;
		try
		{
			
			stmt = c.createStatement();
		
			System.out.print("Please enter first and last name: ");
			name = scanner.next();
			
			rs = stmt.executeQuery("SELECT cusID, name, address, state FROM customers WHERE name ='" + name +"'; ");
			
			while(rs.next())
			{
				cusID = rs.getInt("cusID");
				searchName = rs.getString("name");
				address = rs.getString("address");
				state = rs.getString("state");
				
				System.out.println(cusID + " | " + searchName + " | " + address + " | " + state);
				
			}
			
				scanner.nextLine();
				System.out.println("If customer is not listed above please enter 0");
				System.out.print("Please enter your ID number: ");
				input = Integer.parseInt(scanner.next());
		
				if(input != 0)
				{
					rs =stmt.executeQuery("SELECT name FROM customers WHERE cusID='"+ input +"';");
					name = rs.getString("name");
					//executes query that updates who is renting, when they rented it, and availability of the book
					rs = stmt.executeQuery("INSERT INTO books(rentedBy, lastRentedOn) VALUES("+ name +", "+ formatter.format(time) + ") WHERE bookID="+ bookId + ";");
				}else if(input == 0)
				{
					addCustomer(rs, c);
				}
			
			
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
				
	}
	
	public static void addCustomer(ResultSet rs, Connection c)
	{
		String name, telephone, address, state;
		boolean flag = false;
		try
		{
			stmt = c.createStatement();
			while(!flag)
			{
				System.out.print("Enter the first and last name of the customer: ");
				name = scanner.next();
			
				System.out.print("Enter the customer's telephone number (i.e. xxx-xxx-xxxx): ");
				telephone = scanner.next();
			
				System.out.print("Enter the customer's address and zip code: ");
				address = scanner.next();
			
				System.out.print("Enter the state where the customer currently lives (i.e. OH): ");
				state = scanner.next();
			
				if(name != "" && telephone != "" && address != "" && state != "")
				{
					rs = stmt.executeQuery("INSERT INTO customers(name, telephone, address, state) VALUES('" + name + "', '" + telephone + "', '" + address + "', '" + state +"');");
					System.out.println("Customer added");
					flag = true;
				}
			
			}
			
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
}
