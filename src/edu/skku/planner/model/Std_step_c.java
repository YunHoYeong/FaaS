package edu.skku.planner.model;

public class Std_step_c {
	Double unit_run_Avg;
	Double setup_Avg;
	int unit_prod_Size;
	
	public Std_step_c(Double setupAvg, double unitrunAvg, int unitprodSize) {
		setup_Avg = setupAvg;
		unit_run_Avg = unitrunAvg;
		unit_prod_Size = unitprodSize;
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
	
	public double get_unitrunAvg(){
		return unit_run_Avg;
	}
	
	public void set_unitprodSize(int unitprodSize){
		unit_prod_Size = unitprodSize;
	}
	
	public int get_unitprodSize(){
		return unit_prod_Size;
	}
}


