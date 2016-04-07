package edu.skku.planner.model;

public class Rel {
	
	int part_Relating;
	int part_Related;
	int part_Qty;
	
//	public Rel(int partRelating, int partRelated, int partQty) {
//		part_Relating = partRelating;
//		part_Related = partRelated;
//		part_Qty = partQty;
//	}
	
	public Rel(int partRelated, int partQty) {
		part_Related = partRelated;
		part_Qty = partQty;
	}
	
//	public void set_partRelating(int partRelating){
//		part_Relating = partRelating;
//	}
//	
//	public int get_partRelating(){
//		return part_Relating;
//	}
	
	public void set_partRelated(int partRelated){
		part_Related = partRelated;
	}
	
	public int get_partRelated(){
		return part_Related;
	}
	
	public void set_partQty(int partQty){
		part_Qty = partQty;
	}
	
	public int get_partQty(){
		return part_Qty;
	}

}
