package edu.skku.planner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.Date;
import edu.skku.planner.io.DataReader;

public class FIFOPlanner {
	
    String productID;   //생산요청 완제품ID
    int order_qty;   //완제품 생산요청 수량(주문수)
    int[] part_ID;  //3D프린터 생산 필요 부품 ID
    Date due_date;   // 고객 입력 duedate 표시
    int num_part;   //3D프린터 생산 필요 부품 개수(BOM)
    int[] part_qty_1;  //3D프린터 생산 필요 부품 수 - 완제품 1개당
    double[] part_unit_time; //3D프린터의 부품별  생산시간    
    double[] a_capa;   //일자별  capa
    int plan_horizon;     //생산계획 구간
    double lead_time; //3D프린터 이후 리드타임
    DataModel dm = new DataModel();
    DataReader dr = new DataReader();
	static double i_unit_time;
	
	//plan generation
    //1. Deploy
	public FIFOPlanner(DataModel dm) {

        productID = dm.getProductID();   //생산요청 완제품ID        
        order_qty = dm.getOrderQty();   //완제품 생산요청 수량(주문수)
        part_ID = dm.getPartID();  //3D프린터 생산 필요 부품 ID
        due_date = dm.getDueDate();   // 고객 입력 duedate
        num_part = part_ID.length;   //3D프린터 생산 필요 부품 개수(BOM)
        part_qty_1 = dm.getPartQtyperUnit();  //3D프린터 생산 필요 부품 수 - 완제품 1개당
        part_unit_time = dm.getUnitTime(part_ID); //3D프린터의 부품별  생산시간    
        //a_capa = dm.getCapacity();   //일자별  capa
        lead_time = dm.getLeadTime(); //3D프린터 이후 lead time
                
	}	
	
	public void loadcplan(){
        //2 Load Confirmed plan
        //추후에 반영해야함: a_capa에서 확정된 계획문량만큼 capa감소
	}


	public int[] fifoplan(double[] a_capa) throws UserException { //return값 생겨야되므로 void 제거, 중복되는 부분은 private 함수로 뺴서 만들기
		
		plan_horizon = a_capa.length;     //생산계획 구간

		
		int[][] bottleneck_plan = new int[plan_horizon][num_part];  //부품별 3D프린터 생산 계획(Output data)
		int[] b_f_plan = new int[plan_horizon];  //완제품 기준의 생산 계획.. 하나의 완제품을 위한 부품이 모두 생산되는 시점 기준 (Output data)
			
		i_unit_time = totaltimeCalperunit(); //완제품 1개를 위한 부품의 총 생산시간
				
		int i_part_qty = 0;  //완제품 1개를 위한 부품의 총 갯수
		for (int i = 0; i < num_part; i++) {
			i_part_qty = i_part_qty + part_qty_1[i];
		}
		
		System.out.println("Number of parts per unit: "+i_part_qty);
		
		int c_days = 0;  //생산계획 진행날자
		
		//3D printer 일별 생산 계획 수립
		for (int i = 0; i <order_qty ; i++) { //i = 생산수량
			if(c_days >= plan_horizon){break;}
			//System.out.println("제품 생산 시작 : "+i);
			for (int j = 0; j < num_part; j++) {//j = (BOM) 필요한 부품의 종류
				if(c_days >= plan_horizon){break;}
				//System.out.print("--> 부품 type : "+j);
				int tmp = part_qty_1[j];    
				for (int k = 0; k < tmp; k++) { //k = (BOM) 필요 부품별 완제품 1개에 요구되는 개수		
					if(c_days >= plan_horizon){break;}
					//System.out.print("--> 부품 개수 : "+k);
					for (int days = c_days; days < plan_horizon ; days++) { //days = 날짜
						if(c_days >= plan_horizon){break;}
						
						if(a_capa[days] >= part_unit_time[j]) {  //capa가 존재하면..
							bottleneck_plan[days][j]++;
							a_capa[days] = a_capa[days] - part_unit_time[j]; 
							//System.out.print("--> 생산완료 : day"+days);
							//for (int aa = 0; aa < plan_horizon; aa++){
							
							//	System.out.print("/"+a_capa[aa]);	
							//}
							//System.out.println();
							break;
						} else {  // capa가 없으면 다음날로 이동
							
							if(c_days + 1< 60)
							{
								c_days++; 
								a_capa[c_days] = a_capa[c_days] + a_capa[days];   //전날 남는 소량 capa 이용할 수 있도록 반영
								a_capa[days] = 0;	
							}
							else c_days++; break;
							
							//System.out.print("다음날로 이동~~~!!!!");
						}
					}
				}
			}
			if(c_days < plan_horizon){	b_f_plan[c_days]++;	}
			
		} 

		//완제품까지 leadtime 반영  (3D프린터 이후는 모든 설비가 매일 유효하다는 가정...)@@@@@@@
		int[] p_plan = new int[plan_horizon];
		double lead_time = dm.getLeadTime(); //3D프린터 이후의 생산 Leadtime
		int l_days = (int)(lead_time/(8*60*60));          //leadtime의 일자 8 시간 반영
		int l_hours = (int)(lead_time%(8*60*60));         //leadtime의 일자를 뺀 나머지
		int l_qty = 0;    //leadtime 시간내 생산량..뒷 날자로 완성이 밀리는 갯수		
		//LT가 하루 이상부분은 일자 이동
		for (int i = 0; i < (plan_horizon-l_days); i++) { 
			p_plan[i+l_days]=b_f_plan[i];
		}		
		
		//LT 하루 이하부분은 일부 물량 이동
		for (int i = (plan_horizon-1); i > 0; i--) { 
			
			int tmp_qty;
			if (p_plan[i-1]>0){
			
				if(a_capa[i-1]>0){  //계획이 있는데 capa가 남으면.. plan 마지막날
					l_qty = (int)(l_hours/i_unit_time);
					if (l_hours < a_capa[i-1]){
						tmp_qty = 0;
					} else {
						l_qty = (int)Math.ceil(((l_hours-a_capa[i-1])/i_unit_time));  //당일완성 불가 개수
						tmp_qty = l_qty;
						if(p_plan[i-1] > l_qty){
							tmp_qty = l_qty;
						} else {
							tmp_qty = p_plan[i-1];
						}
					}
				} else {  //full capa로 생산한날..
					l_qty = (int)Math.ceil((l_hours/i_unit_time));  //l_hour이내에 만들 수 있는 갯수...당일 완성 불가 
					if(p_plan[i-1] > l_qty){
						tmp_qty = l_qty;
					} else {
						tmp_qty = p_plan[i-1];
					}
				}	
				p_plan[i-1]=p_plan[i-1]-tmp_qty;
				p_plan[i]=p_plan[i]+tmp_qty;
			}
		}
		
		System.out.println("Completed:");
		
		System.out.println("===========  3D 프린터 일별 생산량 (부품기준) ======================");
		for (int i = 0; i < plan_horizon; i++){
			System.out.print("일자(" + i +") ==> 부품 기준 생산량: ");
			for (int j = 0; j < num_part; j++){
				System.out.print(bottleneck_plan[i][j]+"/");
			}
			System.out.println();	
		}
		
		System.out.println("===========  3D 프린터 일별 생산량 (완제품/잔여capa정보) ======================");
		for (int i = 0; i < plan_horizon; i++){
			System.out.println("일자(" + i +") ==> 완제품 기준 생산량: "+ b_f_plan[i]+"/"+a_capa[i]);	
		}
		
		System.out.println("===========  일별 완제품 생산량 (최종공정) ======================");
		for (int i = 0; i < plan_horizon; i++){
			System.out.println("일자(" + i +") ==> 완제품 기준 생산량: "+ p_plan[i]);	
		}
		
		int sumManufactured = 0;
		for(int i : p_plan) sumManufactured += i;
		
		// Return 전에 Error 검사.
		if(sumManufactured < order_qty){
			throw new UserException(1); // ErrorCode 1 : Planning Horizon내에 계획 작성 불가
		}
		return p_plan;
        
	}
	
	public double totaltimeCalperunit() { //완제품 1개를 위한 부품의 총 생산시간 
		for (int i = 0; i < num_part; i++) {
			i_unit_time = i_unit_time + part_qty_1[i]*part_unit_time[i];
		}
		System.out.println("Total time required per unit: "+i_unit_time);
		
		return i_unit_time;
	}
}
