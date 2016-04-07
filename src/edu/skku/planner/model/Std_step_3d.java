package edu.skku.planner.model;

public class Std_step_3d {
	
	int part_OID;
	String wctr_Type;
	Double unit_run_Avg;
	Double setup_Avg;
	int unit_prod_Size;
	
	public Std_step_3d(int partOID, String wctrType, Double setupAvg, Double unitrunAvg, int unitprodSize){
		part_OID = partOID;
		wctr_Type = wctrType;
		setup_Avg = setupAvg;
		unit_run_Avg = unitrunAvg;
		unit_prod_Size = unitprodSize;
	}
	
	public void set_partOID(int partOID){
		part_OID = partOID;
	}
	
	public int get_partOID(){
		return part_OID;
	}
	
	public void set_wctrType(String wctrType){
		wctr_Type = wctrType;
	}
	
	public String get_wctrType(){
		return wctr_Type;
	}

	public void set_setupAvg(Double setupAvg){
		setup_Avg = setupAvg;
	}
	
	public Double get_setupAvg(){
		return setup_Avg;
	}
	
	public void set_unitrunAvg(Double unitrunAvg){
		unit_run_Avg = unitrunAvg;
	}
	
	public Double get_unitrunAvg(){
		return unit_run_Avg;
	}
	
	public void set_unitprodSize(int unitprodSize){
		unit_prod_Size = unitprodSize;
	}
	
	public int get_unitprodSize(){
		return unit_prod_Size;
	}

}
