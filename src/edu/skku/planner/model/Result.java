package edu.skku.planner.model;

import java.sql.Date;

public class Result {
	
	int project_ID;
	int o_ID;
	Date pro_Date;
	int daily_Qty;
	
	
//	public Result(int projectID, Date proDate, int dailyQty) {
//		project_ID = projectID;
//		pro_Date = proDate;
//		daily_Qty = dailyQty;		
//	}
	
	public Result(int oID, Date proDate, int dailyQty) {
	o_ID = oID;
	pro_Date = proDate;
	daily_Qty = dailyQty;		
	}
	
	public void set_projectID(int projectID) {
		project_ID = projectID;
	}
	
	public int get_projectID() {
		return project_ID;
	}
	
	public void set_oID(int oID) {
		o_ID = oID;
	}
	
	public int get_oID() {
		return o_ID;
	}
	
	public void set_proDate(Date proDate) {
		pro_Date = proDate;
	}
	
	public Date get_proDate() {
		return pro_Date;
	}
	
	public void set_dailyQty(int dailyQty) {
		daily_Qty = dailyQty;
	}
	
	public int get_dailyQty() {
		return daily_Qty;
	}

}
