package librarysw;
import java.sql.*;
import java.util.*;

public class Library 
{
	static Statement stmt = null;
	public static void main(String args[])
	{
		Connection c = null;
		int input = 0;
		Scanner scanner = new Scanner(System.in);
		try 
		{	
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\whitl\\eclipse-workspace\\LibrarySW\\LibraryDB.db");
			c.setAutoCommit(true);
			
			System.out.println("Thank you for choosing LibraryDB");
			String sql = "SELECT * FROM books;";//The Default option for users will be to view all books in the database
			stmt = c.createStatement();
	        ResultSet rs = stmt.executeQuery(sql);
			 
	        
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
			int renting =rs.getInt("rentedBy");
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
	
		Scanner scanner = new Scanner(System.in);
		String input;
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
					available = "is available for renting \nWould you like to rent this book? (y/n): ";
					flag = true;
				}
				else if(availability == 0)
				{
					available = "is not available for renting";
				}
				
				
				System.out.print("\n"+author + "'s book "+title+ " "+available + "\n");
				if(flag)
				{
					input = scanner.nextLine();
					if(input == "y" || input == "Y")
					{
						rentBook(rs, c);
						
					}
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void rentBook(ResultSet rs, Connection c)
	{//enter user id to the rented by column of this book1
		rs = stmt.executeQuery("INSERT INTO books(rentedBy) VALUES("+ +");");
				
	}
	
}
