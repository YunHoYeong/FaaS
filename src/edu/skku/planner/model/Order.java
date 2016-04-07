package edu.skku.planner.model;

import java.util.Date;

public class Order{
	
	String project_ID;
	int i_DX;
	String product_ID;
	int order_Qty;
	Date due_Date;
	int order_Type;
	int order_Status;

	
	public Order(int iDX, String projectID, String productID, int orderQty, Date dueDate, int orderType, int orderStatus) {
		i_DX = iDX;
		project_ID = projectID;
		product_ID = productID;
		order_Qty = orderQty;
		due_Date = dueDate;
		order_Type = orderType;
		order_Status = orderStatus;
	}
	
	public void set_iDX(int iDX){
		i_DX = iDX;
	}
	
	public int get_iDX(){
		return i_DX;
	}
	
	public void set_projectID(String projectID){
		project_ID = projectID;
	}
	
	public String get_projectID(){
		return project_ID;
	}
	
	public void set_productID(String productID){
		product_ID = productID;
	}
	
	public String get_productID(){
		return product_ID;
	}
	
	public void set_orderQty(int orderQty){
		order_Qty = orderQty;
	}
	
	public int get_orderQty(){
		return order_Qty;
	}
	
	public void set_duedate(Date dueDate){
		due_Date = dueDate;
	}
	
	public java.util.Date get_duedate(){
		return due_Date;
	}
	
	public void set_ordertype(int orderType) {
		order_Type = orderType;
	}
	
	public int get_ordertype(){
		return order_Type;
	}
	
	public void set_orderstatus(int orderStatus) {
		order_Status = orderStatus;
	}
	
	public int get_orderstatus(){
		return order_Status;
	}
		
}
