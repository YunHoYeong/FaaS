package edu.skku.planner.model;

public class Part {
	
	int o_ID;
	String part_No;
	int project_ID;
	
//	public Part(String oID, String partNo, int projectID) {
//		o_ID = oID;
//		part_No = partNo;
//		project_ID = projectID;		
//	}
	
	public Part(int oID, String partNo) {
		o_ID = oID;
		part_No = partNo;	
	}
	
	public void set_oID(int oID) {
		o_ID = oID;
	}
	
	public int get_oID() {
		return o_ID;
	}
	
	public void set_partNo(String partNo) {
		part_No = partNo;
	}
	
	public String get_partNo() {
		return part_No;
	}
	
	public void set_projectID(int projectID) {
		project_ID = projectID;
	}
	
	public int get_projectID() {
		return project_ID;
	}

}
