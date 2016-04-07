package edu.skku.planner;

import edu.skku.planner.io.DataReader;

public class URGENTPlanner {
	
    String productID;   //�����û ����ǰID
    int order_qty;   //����ǰ �����û ����(�ֹ���)
    double[] a_capa;   //���ں�  capa
    int plan_horizon;     //�����ȹ ����
    double lead_time; //3D������ ���� ����Ÿ��
    DataModel dm = new DataModel();
    DataReader dr = new DataReader();
	
	//plan generation
    //1. Deploy
    
	public URGENTPlanner(DataModel dm, String projectid) {

        productID = dm.getProductID();   //�����û ����ǰID        
        order_qty = dm.getOrderQty();   //����ǰ �����û ����(�ֹ���)
        a_capa = dm.getCapacity(projectid);
        
	}
	
	public int[] urgentplan(String projectid) throws UserException {
		plan_horizon = a_capa.length;     //�����ȹ ����
		
		if (projectid.equals("6")) { //�ѱ۽ð��� ��� -- ��������:bottleneck
			
			if (order_qty<=20) { //�ѱ۽ð� ���� 20�� ������ ��츸 �÷� �ۼ�
				
				for (int i = 0; i < a_capa.length; i++) {
					a_capa[i] = a_capa[i]/3;
				}
				
				int lead_time = 120+20+10;
				int num_part = 1;
				
				int[][] bottleneck_plan = new int[plan_horizon][num_part];  //��ǰ�� ���� ���� ��ȹ(Output data)
				int[] b_f_plan = new int[plan_horizon];  //����ǰ ������ ���� ��ȹ.. �ϳ��� ����ǰ�� ���� ��ǰ�� ��� ����Ǵ� ���� ���� (Output data)
					
				int i_unit_time = 198; //����ǰ 1���� ���� ��ǰ�� �� ����ð�
						
				int i_part_qty = 1;
						
				System.out.println("Number of parts per unit: "+i_part_qty);
				
				int c_days = 0;  //�����ȹ ���೯��
				int[] part_qty_1 = new int[1]; //����ǰ 1���� �ʿ� ��ǰ ����
				part_qty_1[0] = 1;
				
				int[] part_unit_time = new int[1];
				part_unit_time[0] = 198;		
				
				//3D printer �Ϻ� ���� ��ȹ ����
				for (int i = 0; i <order_qty ; i++) { //i = �������
					//System.out.println("��ǰ ���� ���� : "+i);
					for (int j = 0; j < num_part; j++) {//j = (BOM) �ʿ��� ��ǰ�� ����
						//System.out.print("--> ��ǰ type : "+j);
						int tmp = part_qty_1[j];    
						for (int k = 0; k < tmp; k++) { //k = (BOM) �ʿ� ��ǰ�� ����ǰ 1���� �䱸�Ǵ� ����					
							//System.out.print("--> ��ǰ ���� : "+k);
							for (int days = c_days; days < plan_horizon; days++) { //days = ��¥
								if(a_capa[days] >= part_unit_time[j]) {  //capa�� �����ϸ�..
									bottleneck_plan[days][j]++;
									a_capa[days] = a_capa[days] - part_unit_time[j]; 
									//System.out.print("--> ����Ϸ� : day"+days);
									//for (int aa = 0; aa < plan_horizon; aa++){
									//	System.out.print("/"+a_capa[aa]);	
									//}
									//System.out.println();
									break;
								} else {  // capa�� ������ �������� �̵�
									c_days++; 
									a_capa[c_days] = a_capa[c_days] + a_capa[days];   //���� ���� �ҷ� capa �̿��� �� �ֵ��� �ݿ�
									a_capa[days] = 0;
									//System.out.print("�������� �̵�~~~!!!!");
								}
							}
						}
					}
					b_f_plan[c_days]++;
					//date = 
				}

				//����ǰ���� leadtime �ݿ�  (3D������ ���Ĵ� ��� ���� ���� ��ȿ�ϴٴ� ����...)@@@@@@@
				int[] p_plan = new int[plan_horizon];
				//double lead_time = dm.getLeadTime(); //3D������ ������ ���� Leadtime
				int l_days = (int)(lead_time/(8*60*60));          //leadtime�� ���� 8 �ð� �ݿ�
				int l_hours = (int)(lead_time%(8*60*60));         //leadtime�� ���ڸ� �� ������
				int l_qty = 0;    //leadtime �ð��� ���귮..�� ���ڷ� �ϼ��� �и��� ����		
				//LT�� �Ϸ� �̻�κ��� ���� �̵�
				for (int i = 0; i < (plan_horizon-l_days); i++) { 
					p_plan[i+l_days]=b_f_plan[i];
				}		
				
				//LT �Ϸ� ���Ϻκ��� �Ϻ� ���� �̵�
				for (int i = (plan_horizon-1); i > 0; i--) { 
					
					int tmp_qty;
					if (p_plan[i-1]>0){
					
						if(a_capa[i-1]>0){  //��ȹ�� �ִµ� capa�� ������.. plan ��������
							l_qty = (int)(l_hours/i_unit_time);
							if (l_hours < a_capa[i-1]){
								tmp_qty = 0;
							} else {
								l_qty = (int)Math.ceil(((l_hours-a_capa[i-1])/i_unit_time));  //���Ͽϼ� �Ұ� ����
								tmp_qty = l_qty;
								if(p_plan[i-1] > l_qty){
									tmp_qty = l_qty;
								} else {
									tmp_qty = p_plan[i-1];
								}
							}
						} else {  //full capa�� �����ѳ�..
							l_qty = (int)Math.ceil((l_hours/i_unit_time));  //l_hour�̳��� ���� �� �ִ� ����...���� �ϼ� �Ұ� 
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
				
				System.out.println("===========  ���� ���� �Ϻ� ���귮 (��ǰ����) ======================");
				for (int i = 0; i < plan_horizon; i++){
					System.out.print("����(" + i +") ==> ��ǰ ���� ���귮: ");
					for (int j = 0; j < num_part; j++){
						System.out.print(bottleneck_plan[i][j]+"/");
					}
					System.out.println();	
				}
				
				System.out.println("===========  ���� ���� �Ϻ� ���귮 (����ǰ/�ܿ�capa����) ======================");
				for (int i = 0; i < plan_horizon; i++){
					System.out.println("����(" + i +") ==> ����ǰ ���� ���귮: "+ b_f_plan[i]+"/"+a_capa[i]);	
				}
				
				System.out.println("===========  �Ϻ� ����ǰ ���귮 (��������) ======================");
				for (int i = 0; i < plan_horizon; i++){
					System.out.println("����(" + i +") ==> ����ǰ ���� ���귮: "+ p_plan[i]);	
				}
				
				int sumManufactured = 0;
				for(int i : p_plan) sumManufactured += i;
				
				// Return ���� Error �˻�.
				if(sumManufactured < order_qty){
					throw new UserException(1); // ErrorCode 1 : Planning Horizon���� ��ȹ �ۼ� �Ұ�
				}
				return p_plan;
				
			} else { // ����ó��
				throw new UserException(2); // ErrorCode 2 : �غ�� ���縦 �ʰ��ϴ� �ֹ����Դϴ�.
			}		
			
		} else { //����� ��� -- ����(Vapor: Bottleneck)
			
			if (order_qty<=100) { //orderqty�� 100�� ������ ���
				
				double lead_time = (200+20+10)/3;
				int num_part = 2;
				
				int[][] bottleneck_plan = new int[plan_horizon][num_part];  //��ǰ�� ���� ���� ��ȹ(Output data)
				int[] b_f_plan = new int[plan_horizon];  //����ǰ ������ ���� ��ȹ.. �ϳ��� ����ǰ�� ���� ��ǰ�� ��� ����Ǵ� ���� ���� (Output data)
					
				double i_unit_time = 400/3 ; //����ǰ 1���� ���� ��ǰ�� �� ����ð�
						
				int i_part_qty = 2;
						
				System.out.println("Number of parts per unit: "+i_part_qty);
				
				int c_days = 0;  //�����ȹ ���೯��
				int[] part_qty_1 = new int[2]; //����ǰ 1���� �ʿ� ��ǰ ����
				part_qty_1[0] = 1;
				part_qty_1[1] = 1;
				
				double[] part_unit_time = new double[2];
				part_unit_time[0] = 400/3;
				part_unit_time[1] = 400/3;
				
				//3D printer �Ϻ� ���� ��ȹ ����
				for (int i = 0; i <order_qty ; i++) { //i = �������
					//System.out.println("��ǰ ���� ���� : "+i);
					for (int j = 0; j < num_part; j++) {//j = (BOM) �ʿ��� ��ǰ�� ����
						//System.out.print("--> ��ǰ type : "+j);
						int tmp = part_qty_1[j];    
						for (int k = 0; k < tmp; k++) { //k = (BOM) �ʿ� ��ǰ�� ����ǰ 1���� �䱸�Ǵ� ����					
							//System.out.print("--> ��ǰ ���� : "+k);
							for (int days = c_days; days < plan_horizon; days++) { //days = ��¥
								if(a_capa[days] >= part_unit_time[j]) {  //capa�� �����ϸ�..
									bottleneck_plan[days][j]++;
									a_capa[days] = a_capa[days] - part_unit_time[j]; 
									//System.out.print("--> ����Ϸ� : day"+days);
									//for (int aa = 0; aa < plan_horizon; aa++){
									//	System.out.print("/"+a_capa[aa]);	
									//}
									//System.out.println();
									break;
								} else {  // capa�� ������ �������� �̵�
									c_days++; 
									a_capa[c_days] = a_capa[c_days] + a_capa[days];   //���� ���� �ҷ� capa �̿��� �� �ֵ��� �ݿ�
									a_capa[days] = 0;
									//System.out.print("�������� �̵�~~~!!!!");
								}
							}
						}
					}
					b_f_plan[c_days]++;
					//date = 
				}

				//����ǰ���� leadtime �ݿ�  (3D������ ���Ĵ� ��� ���� ���� ��ȿ�ϴٴ� ����...)@@@@@@@
				int[] p_plan = new int[plan_horizon];
				//double lead_time = dm.getLeadTime(); //3D������ ������ ���� Leadtime
				int l_days = (int)(lead_time/(8*60*60));          //leadtime�� ���� 8 �ð� �ݿ�
				int l_hours = (int)(lead_time%(8*60*60));         //leadtime�� ���ڸ� �� ������
				int l_qty = 0;    //leadtime �ð��� ���귮..�� ���ڷ� �ϼ��� �и��� ����		
				//LT�� �Ϸ� �̻�κ��� ���� �̵�
				for (int i = 0; i < (plan_horizon-l_days); i++) { 
					p_plan[i+l_days]=b_f_plan[i];
				}		
				
				//LT �Ϸ� ���Ϻκ��� �Ϻ� ���� �̵�
				for (int i = (plan_horizon-1); i > 0; i--) { 
					
					int tmp_qty;
					if (p_plan[i-1]>0){
					
						if(a_capa[i-1]>0){  //��ȹ�� �ִµ� capa�� ������.. plan ��������
							l_qty = (int)(l_hours/i_unit_time);
							if (l_hours < a_capa[i-1]){
								tmp_qty = 0;
							} else {
								l_qty = (int)Math.ceil(((l_hours-a_capa[i-1])/i_unit_time));  //���Ͽϼ� �Ұ� ����
								tmp_qty = l_qty;
								if(p_plan[i-1] > l_qty){
									tmp_qty = l_qty;
								} else {
									tmp_qty = p_plan[i-1];
								}
							}
						} else {  //full capa�� �����ѳ�..
							l_qty = (int)Math.ceil((l_hours/i_unit_time));  //l_hour�̳��� ���� �� �ִ� ����...���� �ϼ� �Ұ� 
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
				
				System.out.println("===========  ���� ���� �Ϻ� ���귮 (��ǰ����) ======================");
				for (int i = 0; i < plan_horizon; i++){
					System.out.print("����(" + i +") ==> ��ǰ ���� ���귮: ");
					for (int j = 0; j < num_part; j++){
						System.out.print(bottleneck_plan[i][j]+"/");
					}
					System.out.println();	
				}
				
				System.out.println("===========  ���� ���� �Ϻ� ���귮 (����ǰ/�ܿ�capa����) ======================");
				for (int i = 0; i < plan_horizon; i++){
					System.out.println("����(" + i +") ==> ����ǰ ���� ���귮: "+ b_f_plan[i]+"/"+a_capa[i]);	
				}
				
				System.out.println("===========  �Ϻ� ����ǰ ���귮 (��������) ======================");
				for (int i = 0; i < plan_horizon; i++){
					System.out.println("����(" + i +") ==> ����ǰ ���� ���귮: "+ p_plan[i]);	
				}
				int sumManufactured = 0;
				for(int i : p_plan) sumManufactured += i;
				
				// Return ���� Error �˻�.
				if(sumManufactured < order_qty){
					throw new UserException(1); // ErrorCode 1 : Planning Horizon���� ��ȹ �ۼ� �Ұ�
				}
				return p_plan;
				
			} else { //���� ó����
				throw new UserException(2); // ErrorCode 2 : �غ�� ���縦 �ʰ��ϴ� �ֹ����Դϴ�.
			}
		}
	}
}
