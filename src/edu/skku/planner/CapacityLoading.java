package edu.skku.planner;

import java.text.SimpleDateFormat;
import java.util.*;

import edu.skku.planner.io.DataReader;

public class CapacityLoading {
	
	DataModel dm = new DataModel();
	DataReader dr = new DataReader();
	private List<Integer> producing_idx = new ArrayList<Integer>(); //계획이 확정된 오더의 idx
	private List results = new ArrayList(); //계획이 확정된 오더의 생산계획 결과
	private List producing_pid = new ArrayList();
	private List producing_partid = new ArrayList();
	private List producing_3dpart = new ArrayList();
	private List<Double> time_list = new ArrayList();
	private int[] oid_list;
	private java.sql.Date[] date_list; 
	private int[] qty_list;
	private List full_date_list = new ArrayList();
	private List full_time_list = new ArrayList();
	private double[] full_capa;
	private double[] available_capa;
	
	public CapacityLoading(DataModel dm) {
		
	}

	public double[] capacityload(String ProjectID) {
		
		
		producing_idx = dr.readconfirmOrder(dm); //생산이 확정된 order 조회 //개별
		
		full_capa = dm.getCapacity(ProjectID);
		available_capa = new double[full_capa.length];
		
		if (producing_idx.size()==0) {
			System.out.println("확정계획이 없으므로 full capa로 계획 작성");
			
			return full_capa;
		}
		
		else {
			
			System.out.println("확정계획 capa 차감 시작");
			
			for (int i = 0; i < producing_idx.size(); i++) {
			dr.readeachResult(dm, producing_idx.get(i)); // o_idx 기반으로 결과 불러오기 (o_idx 하나씩)
			System.out.println("p_idx:"+producing_idx.get(i));
			date_list = dm.getResultdate(); // o_idx에 해당하는 date 불러오기
			qty_list = dm.getResultqty(); // o_idx에 해당하는 qty 불러오기
			
			//idx를 바탕으로 projectid 조회
			producing_pid = dr.readconfirmPIDX(dm, (int) producing_idx.get(i)); //o_idx 기반으로 projectid 조회

			producing_partid = dr.readconfirmPart(dm, (int) producing_pid.get(i)); //projectid를 통해 partid조회
			dr.readcon3dPart(dm, producing_partid);
			
			time_list.add(dm.getconfirmunitTime()); // o_idx에 해당하는 3d printing time (capa에 고려)

//			System.out.println("time_list:"+time_list.get(i));

			for (int l = 0; l < date_list.length; l++) { //전체 결과 리스트에 
				full_date_list.add(date_list[l]); //결과 중 date만 
				full_time_list.add(qty_list[l]*time_list.get(i)); //결과 중 qty*각제품 걸리는 시간만
			}
			
//			for (int j = 0; j < full_date_list.size(); j++) {
//				System.out.println("full_date_list:"+full_date_list.get(j));
//				System.out.println("full_time_list:"+full_time_list.get(j));
//			}
			producing_partid.clear(); // 불러왔던 partid 초기화
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		for (int i = 0; i < full_capa.length; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, i);
			Date date = cal.getTime();
			String s_date = sdf.format(date);
			
			double[] daily_time_list = new double[full_capa.length];
			
			for (int j = 0; j < full_date_list.size(); j++) {
				String d_date = sdf.format(full_date_list.get(j));
				if (d_date.equals(s_date)) {
					daily_time_list[i] = daily_time_list[i] + ((double) full_time_list.get(j));					
				}
			}
			
			available_capa[i] = full_capa[i] - daily_time_list[i]; //풀캐파에서 사용된 시간 차감
			
			if (available_capa[i]<0) { //capa가 음수인 경우 0으로
				available_capa[i]=0;
			}	
			
//			System.out.println("available capa:"+available_capa[i]);
		}		
		return available_capa;
			
		}
	}
}
	
