package edu.skku.planner.model;

public class Wctr {
	
	String wctr_ID;
	String wctr_Type;
	double day_Capa;

//	public Wctr(String wctrID, String wctrType, double dayCapa){
//		wctr_ID = wctrID;
//		wctr_Type = wctrType;
//		day_Capa = dayCapa;
//	}
	
	public Wctr(String wctrID, double dayCapa){
		wctr_ID = wctrID;
		day_Capa = dayCapa;
	}
	
	public void set_wctrID(String wctrID){
		wctr_ID = wctrID;
	}
	
	public String get_wctrID(){
		return wctr_ID;
	}
	
	public void set_wctrType(String wctrType){
		wctr_Type = wctrType;
	}
	
	public String get_wctrType(){
		return wctr_Type;
	}
	
	public void set_dayCapa(double dayCapa){
		day_Capa = dayCapa;
	}
	
	public double get_dayCapa(){
		return day_Capa;
	}
	
}