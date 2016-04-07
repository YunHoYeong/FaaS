package edu.skku.planner;

import edu.skku.planner.io.DataReader;

public class URGENTPlanner {
	
    String productID;   //생산요청 완제품ID
    int order_qty;   //완제품 생산요청 수량(주문수)
    double[] a_capa;   //일자별  capa
    int plan_horizon;     //생산계획 구간
    double lead_time; //3D프린터 이후 리드타임
    DataModel dm = new DataModel();
    DataReader dr = new DataReader();
	
	//plan generation
    //1. Deploy
    
	public URGENTPlanner(DataModel dm, String projectid) {

        productID = dm.getProductID();   //생산요청 완제품ID        
        order_qty = dm.getOrderQty();   //완제품 생산요청 수량(주문수)
        a_capa = dm.getCapacity(projectid);
        
	}
	
	public int[] urgentplan(String projectid) throws UserException {
		plan_horizon = a_capa.length;     //생산계획 구간
		
		if (projectid.equals("6")) { //한글시계의 경우 -- 조립공정:bottleneck
			
			if (order_qty<=20) { //한글시계 오더 20개 이하인 경우만 플랜 작성
				
				for (int i = 0; i < a_capa.length; i++) {
					a_capa[i] = a_capa[i]/3;
				}
				
				int lead_time = 120+20+10;
				int num_part = 1;
				
				int[][] bottleneck_plan = new int[plan_horizon][num_part];  //부품별 조립 공정 계획(Output data)
				int[] b_f_plan = new int[plan_horizon];  //완제품 기준의 생산 계획.. 하나의 완제품을 위한 부품이 모두 생산되는 시점 기준 (Output data)
					
				int i_unit_time = 198; //완제품 1개를 위한 부품의 총 생산시간
						
				int i_part_qty = 1;
						
				System.out.println("Number of parts per unit: "+i_part_qty);
				
				int c_days = 0;  //생산계획 진행날자
				int[] part_qty_1 = new int[1]; //완제품 1개당 필요 부품 갯수
				part_qty_1[0] = 1;
				
				int[] part_unit_time = new int[1];
				part_unit_time[0] = 198;		
				
				//3D printer 일별 생산 계획 수립
				for (int i = 0; i <order_qty ; i++) { //i = 생산수량
					//System.out.println("제품 생산 시작 : "+i);
					for (int j = 0; j < num_part; j++) {//j = (BOM) 필요한 부품의 종류
						//System.out.print("--> 부품 type : "+j);
						int tmp = part_qty_1[j];    
						for (int k = 0; k < tmp; k++) { //k = (BOM) 필요 부품별 완제품 1개에 요구되는 개수					
							//System.out.print("--> 부품 개수 : "+k);
							for (int days = c_days; days < plan_horizon; days++) { //days = 날짜
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
									c_days++; 
									a_capa[c_days] = a_capa[c_days] + a_capa[days];   //전날 남는 소량 capa 이용할 수 있도록 반영
									a_capa[days] = 0;
									//System.out.print("다음날로 이동~~~!!!!");
								}
							}
						}
					}
					b_f_plan[c_days]++;
					//date = 
				}

				//완제품까지 leadtime 반영  (3D프린터 이후는 모든 설비가 매일 유효하다는 가정...)@@@@@@@
				int[] p_plan = new int[plan_horizon];
				//double lead_time = dm.getLeadTime(); //3D프린터 이후의 생산 Leadtime
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
				
				System.out.println("===========  조립 공정 일별 생산량 (부품기준) ======================");
				for (int i = 0; i < plan_horizon; i++){
					System.out.print("일자(" + i +") ==> 부품 기준 생산량: ");
					for (int j = 0; j < num_part; j++){
						System.out.print(bottleneck_plan[i][j]+"/");
					}
					System.out.println();	
				}
				
				System.out.println("===========  조립 공정 일별 생산량 (완제품/잔여capa정보) ======================");
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
				
			} else { // 예외처리
				throw new UserException(2); // ErrorCode 2 : 준비된 자재를 초과하는 주문량입니다.
			}		
			
		} else { //라바의 경우 -- 훈증(Vapor: Bottleneck)
			
			if (order_qty<=100) { //orderqty가 100개 이하인 경우
				
				double lead_time = (200+20+10)/3;
				int num_part = 2;
				
				int[][] bottleneck_plan = new int[plan_horizon][num_part];  //부품별 훈증 공정 계획(Output data)
				int[] b_f_plan = new int[plan_horizon];  //완제품 기준의 생산 계획.. 하나의 완제품을 위한 부품이 모두 생산되는 시점 기준 (Output data)
					
				double i_unit_time = 400/3 ; //완제품 1개를 위한 부품의 총 생산시간
						
				int i_part_qty = 2;
						
				System.out.println("Number of parts per unit: "+i_part_qty);
				
				int c_days = 0;  //생산계획 진행날자
				int[] part_qty_1 = new int[2]; //완제품 1개당 필요 부품 갯수
				part_qty_1[0] = 1;
				part_qty_1[1] = 1;
				
				double[] part_unit_time = new double[2];
				part_unit_time[0] = 400/3;
				part_unit_time[1] = 400/3;
				
				//3D printer 일별 생산 계획 수립
				for (int i = 0; i <order_qty ; i++) { //i = 생산수량
					//System.out.println("제품 생산 시작 : "+i);
					for (int j = 0; j < num_part; j++) {//j = (BOM) 필요한 부품의 종류
						//System.out.print("--> 부품 type : "+j);
						int tmp = part_qty_1[j];    
						for (int k = 0; k < tmp; k++) { //k = (BOM) 필요 부품별 완제품 1개에 요구되는 개수					
							//System.out.print("--> 부품 개수 : "+k);
							for (int days = c_days; days < plan_horizon; days++) { //days = 날짜
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
									c_days++; 
									a_capa[c_days] = a_capa[c_days] + a_capa[days];   //전날 남는 소량 capa 이용할 수 있도록 반영
									a_capa[days] = 0;
									//System.out.print("다음날로 이동~~~!!!!");
								}
							}
						}
					}
					b_f_plan[c_days]++;
					//date = 
				}

				//완제품까지 leadtime 반영  (3D프린터 이후는 모든 설비가 매일 유효하다는 가정...)@@@@@@@
				int[] p_plan = new int[plan_horizon];
				//double lead_time = dm.getLeadTime(); //3D프린터 이후의 생산 Leadtime
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
				
				System.out.println("===========  조립 공정 일별 생산량 (부품기준) ======================");
				for (int i = 0; i < plan_horizon; i++){
					System.out.print("일자(" + i +") ==> 부품 기준 생산량: ");
					for (int j = 0; j < num_part; j++){
						System.out.print(bottleneck_plan[i][j]+"/");
					}
					System.out.println();	
				}
				
				System.out.println("===========  조립 공정 일별 생산량 (완제품/잔여capa정보) ======================");
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
				
			} else { //예외 처리용
				throw new UserException(2); // ErrorCode 2 : 준비된 자재를 초과하는 주문량입니다.
			}
		}
	}
}
