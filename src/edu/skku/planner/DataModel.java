package edu.skku.planner;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import edu.skku.planner.io.DataReader;
import edu.skku.planner.model.*;

public class DataModel {
	
	private List<Order> orders = new ArrayList<Order>();
	private List<Part> parts = new ArrayList<Part>();
	private List<Rel> rels = new ArrayList<Rel>();
	private List<Wctr> wctrs = new ArrayList<Wctr>();
	private List<Std_step> stds = new ArrayList<Std_step>();
	private List<Std_step_3d> stds3d = new ArrayList<Std_step_3d>();
	private ArrayList part_oid_3D = new ArrayList();
	private double lt_total;
	
	private ArrayList producing_order = new ArrayList();
	private ArrayList producing_pidx = new ArrayList();
	private List<Result> results = new ArrayList<Result>();
	private List producing_parts = new ArrayList();
	private List<Std_step_c> stdsc = new ArrayList<Std_step_c>();
	private List<Result_c> results_c = new ArrayList<Result_c>();
	
//	private List<Result> results = new ArrayList<Result>();
//	private List<Integer> producing_project = new ArrayList<Integer>();
	
	// DB에서 읽어온 produce_order table 관련 객체 생성
	public void loadOrder(ResultSet rs){ 
		try {
			while (rs.next()) {	
				int i_dx = rs.getInt("idx");
				String project_id = rs.getString("projectid");
				String product_id = rs.getString("productid");
				int order_qty = rs.getInt("orderqty");
				Date due_date = rs.getDate("duedate"); // sql에서 util로 변환
				java.util.Date uti_due_date = due_date;				
				int order_type = rs.getInt("ordertype");
				int order_status = rs.getInt("orderstatus");
				Order neworder = new Order(i_dx, project_id, product_id, order_qty, uti_due_date, order_type, order_status);
				orders.add(neworder);
			}
		}
		catch(SQLException cnfe) {
			System.out.println("non-connection" + cnfe.getMessage());		
		}
		System.out.println("# of Orders(table:ordertable) saved:"+orders.size());	
	}
	
	public void loadPart(ResultSet rs){
		try {
			while (rs.next()) {	
				int oid = rs.getInt("oid");
				String part_no = rs.getString("part_no");
				//int project_id = rs.getInt("project");
				//Part newpart = new Part(oid, part_no, project_id);
				Part newpart = new Part(oid, part_no);
				parts.add(newpart);
			}
		}
		catch(SQLException cnfe) {
			System.out.println("non-connection" + cnfe.getMessage());		
		}
		System.out.println("# of Orders(table:part) saved:"+parts.size());	
	}
	
	public void loadRel(ResultSet rs){
		try {
			while (rs.next()) {	
				//int part_relating = rs.getInt("relating");
				int part_related = rs.getInt("related");
				int qty = rs.getInt("qty");
				Rel newrel = new Rel(part_related, qty);
				rels.add(newrel);
			}
		}
		catch(SQLException cnfe) {
			System.out.println("non-connection" + cnfe.getMessage());		
		}
		System.out.println("# of Orders(table:rel) saved:"+rels.size());	
	}
	
	public void loadWctr(ResultSet rs){
		try {
			while (rs.next()) {	
				String wctr_id = rs.getString("wctr_id");
				//String wctr_type = rs.getString("wctr_type");
				double day_capa = rs.getDouble("day_capa");
				//Wctr newwctr = new Wctr(wctr_id, wctr_type, day_capa);
				Wctr newwctr = new Wctr(wctr_id, day_capa);
				wctrs.add(newwctr);
			}
		}
		catch(SQLException cnfe) {
			System.out.println("non-connection" + cnfe.getMessage());		
		}
		System.out.println("# of Orders(table:wctr) saved:"+wctrs.size());	
	}
	
	public void loadStd(ResultSet rs){
		try {
			while (rs.next()) {	
				int oid = rs.getInt("oid");
				String wctr_type = rs.getString("wctr_type");
				Double unit_run_avg = rs.getDouble("unit_run_avg");
				Double setup_avg = rs.getDouble("setup_avg");
				int unit_prod_size = rs.getInt("unit_prod_size");
				Std_step newstd = new Std_step(oid, wctr_type, unit_run_avg, setup_avg, unit_prod_size);
				stds.add(newstd);
			}
		}
		catch(SQLException cnfe) {
			System.out.println("non-connection" + cnfe.getMessage());		
		}
		System.out.println("# of Orders(table:std_step) saved:"+stds.size());	
	}
	
	public void loadStd3d(ResultSet rs){
		try {
			while (rs.next()) {
				int part_oid = rs.getInt("part_oid");
				String wctr_type = rs.getString("wctr_type");
				Double unit_run_avg = rs.getDouble("unit_run_avg");
				Double setup_avg = rs.getDouble("setup_avg");
				int unit_prod_size = rs.getInt("unit_prod_size");
				Std_step_3d newstd3d = new Std_step_3d(part_oid, wctr_type, unit_run_avg, setup_avg, unit_prod_size);
				stds3d.add(newstd3d);
			}
		}
		catch(SQLException cnfe) {
			System.out.println("non-connection" + cnfe.getMessage());		
		}
		System.out.println("# of Orders(table:std_step_for3dp) saved:"+stds3d.size());	
	}
	
	public String getProjectID(){
		String Project_ID = orders.get(0).get_projectID();
		return Project_ID;
	}
	
	//ordertable.idx 요청
	public int getOrderIdx(){
		int idx = orders.get(0).get_iDX();
		return idx;
	}

	//프로젝트별 요청 제품은 하나라는 가정에서 완제품 ID 요청
	public String getProductID(){
		String P_ID = orders.get(0).get_productID();
		System.out.println("Product Part ID: "+P_ID);
		return P_ID;
	}

	//프로젝트별 요청 제품은 하나라는 가정에서 완제품 생산 수량 요청
	public int getOrderQty(){
		int O_qty = orders.get(0).get_orderQty();
		//O_qty = 14;
		System.out.println("Order qty: "+O_qty);
		return O_qty;
	}
	
	//프로젝트별 due date 요청
	public java.util.Date getDueDate(){
		java.util.Date Due = orders.get(0).get_duedate();
		System.out.println("Date: "+Due);
		return Due;	
	}
	
	//프로젝트별 order type 요청(긴급오더 구분)
	public int getOrderType(){
		int O_type = orders.get(0).get_ordertype();
		System.out.println("Ordertype: "+O_type);
		return O_type;
	}
	
	//프로젝트별 order status 요청(오더 처리상황 조회)
//	public String getOrderStatus(){
//		String O_status = orders.get(0).get_orderstatus();
//		System.out.println("Orderstatus: "+O_status);
//		return O_status;
//	}
	
	//완제품 포함 전체 파트 아이디 조회
	public int[] getallPartID() {
		int[] all_part_ID = new int[parts.size()];
		
		for (int i = 0; i < parts.size(); i++) {
			all_part_ID[i] = parts.get(i).get_oID();
//			System.out.println("All partID: "+all_part_ID[i]);			
		}
		return all_part_ID;
	}
	
	//완제품을 기준으로 Child 부품 ID조회
	public int[] getChildPartID() {
		int[] child_part_ID = new int[rels.size()];
		
		for (int i = 0; i < rels.size(); i++) {
			child_part_ID[i] = rels.get(i).get_partRelated();	
//			System.out.println("PartID: "+part_ID[i]);
		}
		return child_part_ID;
	}
	
	//완제품을 기준으로 3D프린터에서 생산되는 부품 oid조회
	public ArrayList get3DPartOID(int[] all_part_ID){
		String[] part_type = new String[stds.size()];
		
		for (int i = 0; i < stds.size(); i++) {
			part_type[i] = stds.get(i).get_wctrType();
			if (part_type[i].equals("P")) {
				part_oid_3D.add(stds.get(i).get_oID());
				//System.out.println("partOID: "+part_oid_3D);
			}
		}
		return part_oid_3D;
	}
	
	//완제품을 기준으로 3D프린터에서 생산되는 부품 ID 조회
	public int[] getPartID(){		
		int[] part_ID = new int[part_oid_3D.size()];
		
		for (int i = 0; i < part_ID.length; i++) {
			part_ID[i] = stds3d.get(i).get_partOID();
			//System.out.println("Part ID(using 3D printer): "+part_ID[i]);
		}
		
		return part_ID;
	}

	//3D프린터에서 생산되는 완제품 1개 당 부품 필요량 조회
	public int[] getPartQtyperUnit(){
		int[] part_qty_name = getPartID();
		int[] part_qty = new int[part_qty_name.length];		
		
		for (int i = 0; i < part_qty_name.length; i++) {
			part_qty[i] = rels.get(i).get_partQty();
			System.out.println("Child Part ID:"+part_qty_name[i]+"=>Part Qty:"+part_qty[i]+"개");
		}
		return part_qty;	
	}
	
	//3D프린터에서 생산되는 총 부품 생산량 조회 
	public int[] getPartQty(int order_qty){
		int[] part_qty_1 = getPartQtyperUnit();
		int[] part_qty_t = new int[part_qty_1.length];
		
		for (int i = 0; i < part_qty_1.length; i++) {
			part_qty_t[i]= order_qty*part_qty_1[i];
		}		
		return part_qty_t;
	}
	
	//3D프린터에서 부품 1개 생산에 필요한 시간 조회	
	public double[] getUnitTime(int[] part_ID){
		double[] ut = new double[part_ID.length];
		//String[] ut_type = new String[part_ID.length];
				
		for (int i = 0; i < part_ID.length; i++) {
			ut[i] = (stds3d.get(i).get_unitrunAvg()/stds3d.get(i).get_unitprodSize()) + (stds3d.get(i).get_setupAvg()/stds3d.get(i).get_unitprodSize());
//			ut_type[i] = stds3d.get(i).get_wctrType();
//			if (ut_type[i].equals("01")) {
//				ut[i] = stds3d.get(i).get_unitrunAvg();
//			}
		}
		return ut;
	}
	
	//3D프린터에서 부품 생산후 완제품을 만드는데 필요한 시간 조회
	public double getLeadTime() {
		double[] lt = new double[stds.size()];
		String[] lt_type = new String[stds.size()];
		
		for (int i = 0; i < lt.length; i++) {
			lt_type[i] = stds.get(i).get_wctrType();
			
			if (lt_type[i].equals("P")) { //wctr_type = P 인것은 3DPrinting
				lt[i]=0;				
			} else {
				lt[i] = (stds.get(i).get_unitrunAvg()/stds.get(i).get_unitprodSize()) + (stds.get(i).get_setupAvg()/stds.get(i).get_unitprodSize());
			}
			lt_total = lt_total + lt[i];
		}
		System.out.println("Total Leadtime: "+lt_total);
		return lt_total;
	}
	
	//3D프린터의 일별 Capacity조회 //***day_capa의미 확인***
	public double[] getCapacity(String projectID){
		int horizon = 60; //planning horizon: 2달
		Double utilization = (double) 0.95;
		double[] capa = new double[horizon]; //10일째까지 반영
//		try {
//			String propFile = DataReader.class.getResource("").getPath();
//			File fileInSamePackage = new File(propFile + "DBProperties.properties");
//			
//			Properties props = new Properties();
//			FileInputStream fis = new FileInputStream(fileInSamePackage);
//			props.load(new java.io.BufferedInputStream(fis));
//			
//			horizon = Integer.parseInt(props.getProperty("HORIZON"));
//			utilization = Double.parseDouble(props.getProperty("UTILIZATION"));
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		if (projectID.equals("6")) { //한글시계의 경우
			for (int i = 0; i < capa.length; i++){
				capa[i] = 8*60*60*utilization*3;   // capa단위: 초단위, 8시간 반영 시간*분*초*util*댓수
			}
			
			return capa;
			
		} else {
			for (int i = 0; i < capa.length; i++){
				capa[i] = 8*60*60*utilization*1;   // capa단위: 초단위, 8시간 반영 시간*분*초*util*댓수
			}
			
			return capa;
		}
	}
	
	//////////////////////이후의 코드는 planning의 사전 단계에 필요한 코드임///////////////////////
	
	//생산이 확정된 ordertable.idx 조회
	public List<Integer> loadconfirmOrder(ResultSet rs){ 
		
		try {
			while (rs.next()) {	
				int project_idx = rs.getInt("idx");
				producing_order.add(project_idx);				
			}
		}
		catch(SQLException e) {
			System.out.println("non-connection" + e.getMessage());		
		}
		System.out.println("# of producing project(CL) saved:"+producing_order.size());
		
		return producing_order;	
	}
	
	//생산계획 결과 조회
	public List loadResult(ResultSet rs){ 
		
		try {
			while (rs.next()) {
				int id = rs.getInt("o_id");
				Date date = rs.getDate("date");
				int qty = rs.getInt("qty");
				Result newresult = new Result(id, date, qty);
				results.add(newresult);
			}
		}
		catch(SQLException cnfe) {
			System.out.println("non-connection" + cnfe.getMessage());		
		}
		
		System.out.println("# of Results saved:"+results.size());
		
		return results;
	}
	
	public List loadeachResult(ResultSet rs){ 
		
		try {
			while (rs.next()) {
				int id = rs.getInt("o_id");
				Date date = rs.getDate("date");
				int qty = rs.getInt("qty");
				Result_c newresult_c = new Result_c(date, qty);
//				results_c.clear();
				results_c.add(newresult_c);
			}
		}
		catch(SQLException e) {
			System.out.println("non-connection" + e.getMessage());		
		}
		
		System.out.println("# of each Results(CL) saved:"+results_c.size());
		
		return results_c;
	}
	
	// 생산이 확정된 ordertable.idx를 통해 ordertable.projectid 조회
	public List loadconfirmPIDX(ResultSet rs){ 
		try {
			while (rs.next()) {	
				int project_id = rs.getInt("projectid");
				producing_pidx.add(project_id);
			}
		}
		catch(SQLException e) {
			System.out.println("non-connection" + e.getMessage());		
		}
//		System.out.println("# of producing projectid(CL) saved:"+producing_pidx.size());
		return producing_pidx;
	}
	
	public List loadconfirmPart(ResultSet rs){
		try {
			while (rs.next()) {	
				int oid = rs.getInt("oid");
				producing_parts.add(oid);
			}
		}
		catch(SQLException cnfe) {
			System.out.println("non-connection" + cnfe.getMessage());		
		}
//		System.out.println("# of Parts(CL) saved:"+producing_parts.size());
		return producing_parts;
	}
	
	public void loadconfirmStd(ResultSet rs){
		try {
			while (rs.next()) {
				double setup_avg = rs.getDouble("setup_avg");
				double unit_run_avg = rs.getDouble("unit_run_avg");
				int unit_prod_size = rs.getInt("unit_prod_size");
				Std_step_c newstds = new Std_step_c(setup_avg, unit_run_avg, unit_prod_size);
				stdsc.add(newstds);
			}
		}
		catch(SQLException cnfe) {
			System.out.println("non-connection" + cnfe.getMessage());		
		}
		
		System.out.println("# of stds(CL) saved:"+stdsc.size());
	}
	
	public double getconfirmunitTime() {
		double[] setup_list = new double[stdsc.size()];
		double[] unit_run_list = new double[stdsc.size()];
		double total_setup_time = 0;
		double total_run_time = 0;
		double total_time = 0;
//		setup_list[i] = stdsc.get(i).get_setupAvg();
//		unit_run_list[i] = stdsc.get(i).get_unitrunAvg();
//		total_setup_time = total_setup_time + setup_list[0];
//		total_run_time = total_run_time + unit_run_list[0];
//		total_time = total_setup_time + total_run_time;
		for (int i = 0; i < setup_list.length; i++) {
			setup_list[i] = 0;
			unit_run_list[i]=0;
			setup_list[i] = (stdsc.get(i).get_setupAvg()/stdsc.get(i).get_unitprodSize());
			unit_run_list[i] = (stdsc.get(i).get_unitrunAvg()/stdsc.get(i).get_unitprodSize());
			total_setup_time = total_setup_time + setup_list[i];
			total_run_time = total_run_time + unit_run_list[i];
			total_time = total_setup_time + total_run_time;
		}
		System.out.println("total time: "+total_time);
		stdsc.clear();
		return total_time;
	}
	
	public int[] getResultoid() {
		int[] oid_list =new int[results.size()];
		for (int i = 0; i < results.size(); i++) {
			oid_list[i] = results.get(i).get_oID();
		}
		return oid_list;
	}
	
	public Date[] getResultdate() {
		Date[] date_list = new Date[results_c.size()];
		for (int i = 0; i < results_c.size(); i++) {
			date_list[i] = results_c.get(i).get_proDate();
//			System.out.println("date list:"+date_list[i]);
		}
		return date_list;
	}
	
	public int[] getResultqty() {
		int[] qty_list = new int[results_c.size()];
		for (int i = 0; i < results_c.size(); i++) {
			qty_list[i] = results_c.get(i).get_dailyQty();
		}
		results_c.clear();
		return qty_list;		
	}
	
}
