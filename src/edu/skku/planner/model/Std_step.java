package edu.skku.planner.model;

public class Std_step {
	
	int o_ID;
	String wctr_Type;
	Double unit_run_Avg;
	Double setup_Avg;
	int unit_prod_Size;
	
	public Std_step(int oID, String wctrType, double setupAvg, double unitrunAvg, int unitprodSize){
		o_ID = oID;
		wctr_Type = wctrType;
		setup_Avg = setupAvg;
		unit_run_Avg = unitrunAvg;
		unit_prod_Size = unitprodSize;
	}
	
	public void set_oID(int oID){
		o_ID = oID;
	}
	
	public int get_oID(){
		return o_ID;
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
	
	public double get_setupAvg(){
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
