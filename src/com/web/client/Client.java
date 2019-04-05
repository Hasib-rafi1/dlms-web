package com.web.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import com.web.service.WebInterface;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceRef;

public class Client {

	public static Service conService;
	public static Service mcgService;
	public static Service monService;
	static WebInterface obj;
	
	public static void main(String args[])throws MalformedURLException
	{
		URL conURL = new URL("http://localhost:8080/concordia?wsdl");
		QName conQName = new QName("http://impl.service.web.com/", "ConcordiaClassService");
		conService = Service.create(conURL, conQName);
		
		URL mcgURL = new URL("http://localhost:8081/mcgill?wsdl");
		QName mcgQName = new QName("http://impl.service.web.com/", "McgillClassService");
		mcgService = Service.create(mcgURL, mcgQName);
		
		URL monURL = new URL("http://localhost:8082/montreal?wsdl");
		QName monQName = new QName("http://impl.service.web.com/", "MontrealClassService");
		monService = Service.create(monURL, monQName);
		startSystem();
		
	}
	// Initiating client site program 
	private static void startSystem() {
		System.out.println("Enter your username: ");
		Scanner scanner = new Scanner(System.in);
		String username = scanner.nextLine().toUpperCase();
		System.out.println("You are loging as " + username);
		if(username.length()!=8) {
			System.out.println("Wrong ID");
			startSystem();
		}
		String accessParameter = username.substring(3, Math.min(username.length(), 4));
		System.out.println("You are loging as " + accessParameter);
		if(accessParameter.equals("U") || accessParameter.equals("u") ) {
			try {
				user(username);
				startSystem();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(accessParameter.equals("M") || accessParameter.equals("m")) {
			try {
				manager(username);
//				startSystem();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			System.out.println("This user is not authorized");
			startSystem();
		}
	}
	
	private static void user(String username) throws Exception
	{
		String serverPort = decideServerport(username);
		if(serverPort.equals("1")) {
			startSystem();
		}
		
		System.out.println("1. Borrow Item \n 2.Find Item \n 3. Return item \n 4. Borrowed Items \n 5. Exchange \n 6. Logout");
		System.out.println("Select the option you want to do: ");
		Scanner scanner = new Scanner(System.in);
		String menuSelection = scanner.nextLine();

		if(menuSelection.equals("1")) {
			String itemId = setItemId(username);
			int numbersOfDay = setNumbersOfDay(username);
			boolean n = obj.borrowItem(username, itemId,numbersOfDay);
			System.out.println("Item Borrowed : " + n);
			if(!n) {
				System.out.println("Item is not available now. Do you like to stay in waiting list? \n Press Y for yes and enter. N for NO and enter");
				String waitingOption = scanner.nextLine();
				if(waitingOption.equals("Y")||waitingOption.equals("y")) {
					boolean m = obj.waitingList(username, itemId);
					if(m) {
						System.out.println("Your Item will be lend to you, when it will be available.");
					}
				}else {
					user(username);
				}
			}
			user(username);
		}
		else if(menuSelection.equals("2")) {
			String itemName = setItemName(username);
			System.out.println("Item List is given below. ");
			System.out.println(obj.findItem(username, itemName));
			System.out.println("To GO back press E and enter");
			String exit = scanner.nextLine();
			if(exit.equals("E") || exit.equals("e")) {
				user(username);
			}else {
				user(username);
			}
		}
		else if(menuSelection.equals("3")) {
			String itemId = setItemId(username);
			boolean n = obj.returnItem(username, itemId);
			System.out.println("Item Returend : " + n);
			user(username);
		}
		else if (menuSelection.equals("4")) {
			System.out.println("Item List is given below. ");
			System.out.println(obj.findBorrowedItems(username));
			System.out.println("To GO back press E and enter");
			String exit = scanner.nextLine();
			if(exit.equals("E") || exit.equals("e")) {
				user(username);
			}else {
				user(username);
			}
		}
		else if(menuSelection.equals("5")) {
			String newItemId = setItemId(username);
			String oldItemId = setItemId(username);
			boolean n = obj.exchangeItem(username,newItemId, oldItemId);
			System.out.println("Item Exchanged : " + n);
			user(username);
		}
		else if (menuSelection.equals("6")) {
			startSystem();
		}
		else {
			user(username);
		}
	}
	
	private static void manager(String username) throws Exception
	{
		String serverPort = decideServerport(username);
		if(serverPort.equals("1")) {
			startSystem();
		}
		
		System.out.println("1. Add Items \n 2.Remove Item \n 3. List of the items \n 4. Waiting List \n 5. Logout");
		System.out.println("Select the option you want to do: ");
		Scanner scanner = new Scanner(System.in);
		String menuSelection = scanner.nextLine();
		if(menuSelection.equals("1")) {
			String itemId = setItemId(username);
			String itemName = setItemName(username);
			int itemQty = setItemQty(username);
			boolean n = obj.addItem(username, itemId,itemName,itemQty);
			System.out.println("Item Added : " + n);
			manager(username);
		}
		else if(menuSelection.equals("2")) {
			String itemId = setItemId(username);
			int itemQty = setItemQty(username);
			boolean n = obj.removeItem(username, itemId,itemQty);
			System.out.println("Item Removed : " + n);
			if(!n) {
				System.out.println("Check Quantity");
			}
			manager(username);
		}
		else if(menuSelection.equals("3")) {
			System.out.println("Item List is given below. ");
			System.out.println(obj.listItem(username));
			System.out.println("To GO back press E and enter");
			String exit = scanner.nextLine();
			if(exit.equals("E") || exit.equals("e")) {
				manager(username);
			}else {
				manager(username);
			}
		}
		else if(menuSelection.equals("4")) {
			System.out.println("Waiting List is given below. ");
			System.out.println(obj.findWaitingItems());
			System.out.println("To GO back press E and enter");
			String exit = scanner.nextLine();
			if(exit.equals("E") || exit.equals("e")) {
				manager(username);
			}else {
				manager(username);
			}
		}
		else if (menuSelection.equals("5")) {
			startSystem();
		}
		else {
			manager(username);
		}

	}
	
	private static String decideServerport(String username) {
		String serverPort="1";
		String serverDirection = username.substring(0, Math.min(username.length(), 3));
		if(serverDirection.equals("CON") || serverDirection.equals("con")) {
			obj = conService.getPort(WebInterface.class);
			serverPort = "2";
		}else if(serverDirection.equals("MCG") || serverDirection.equals("mcg")) {
			obj = mcgService.getPort(WebInterface.class);
			serverPort = "2";
		}else if(serverDirection.equals("MON") || serverDirection.equals("mon")) {
			obj = monService.getPort(WebInterface.class);
			serverPort = "2";
		}else {
			System.out.println("This is an invalid request. Please check your username");
		}
		return serverPort;
	}
	
	private static String setItemId(String username) {
		String libraryCode = username.substring(0, Math.min(username.length(), 3));
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Item Id: ");
		String itemId = scanner.nextLine().toUpperCase();
		String itemPrefix = itemId.substring(0, Math.min(itemId.length(), 3));
		if(itemId.length()!=7 && libraryCode !=itemPrefix) {
			System.out.println("Enter a valid Item Id: ");
			itemId = setItemId(username);
		}
		return  itemId;
	}
	
	private static String setItemName(String username) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Item name: ");
		String itemName = scanner.nextLine().toUpperCase();
	
		
		return  itemName;
	}
	
	private static int setItemQty(String username) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Item Quantity: ");
		int itemQty;
		if(scanner.hasNextInt()){
			itemQty = scanner.nextInt();
			if(itemQty<0) {
				System.out.println("Enter a valid Number: ");
				itemQty = setItemQty(username);
			}
		}else{
			System.out.println("Enter a valid Item Id: ");
			itemQty = setItemQty(username);
		}
		return  itemQty;
	}
	
	private static int setNumbersOfDay(String username) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Number Of Days: ");
		int numberOfDays;
		if(scanner.hasNextInt()){
			numberOfDays = scanner.nextInt();
		}else{
			System.out.println("Enter a valid Number: ");
			numberOfDays = setNumbersOfDay(username);
		}
		return  numberOfDays;
	}
}
