package com.web.service;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface WebInterface {

	public boolean addItem(String managerID, String itemID, String itemName, int quantity);
	public boolean removeItem(String managerID, String itemID, int quantity);
	public String listItem(String managerID);
	
	
	public boolean borrowItem(String userID,String itemID,int numberOfDay);
	public String findItem(String userID, String itemName);
	public boolean returnItem(String userID, String itemID);
	public boolean exchangeItem(String userID,String newItemID,String oldItemID);
	public String findBorrowedItems(String userID);
	public String findWaitingItems();
	public boolean waitingList(String userID,String itemID);

}
