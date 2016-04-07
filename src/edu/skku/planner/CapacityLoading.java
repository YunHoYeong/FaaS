package edu.skku.planner;

import java.text.SimpleDateFormat;
import java.util.*;

import edu.skku.planner.io.DataReader;

public class CapacityLoading {
	
	DataModel dm = new DataModel();
	DataReader dr = new DataReader();
	private List<Integer> producing_idx = new ArrayList<Integer>(); //��ȹ�� Ȯ���� ������ idx
	private List results = new ArrayList(); //��ȹ�� Ȯ���� ������ �����ȹ ���
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
		
		
		producing_idx = dr.readconfirmOrder(dm); //������ Ȯ���� order ��ȸ //����
		
		full_capa = dm.getCapacity(ProjectID);
		available_capa = new double[full_capa.length];
		
		if (producing_idx.size()==0) {
			System.out.println("Ȯ����ȹ�� �����Ƿ� full capa�� ��ȹ �ۼ�");
			
			return full_capa;
		}
		
		else {
			
			System.out.println("Ȯ����ȹ capa ���� ����");
			
			for (int i = 0; i < producing_idx.size(); i++) {
			dr.readeachResult(dm, producing_idx.get(i)); // o_idx ������� ��� �ҷ����� (o_idx �ϳ���)
			System.out.println("p_idx:"+producing_idx.get(i));
			date_list = dm.getResultdate(); // o_idx�� �ش��ϴ� date �ҷ�����
			qty_list = dm.getResultqty(); // o_idx�� �ش��ϴ� qty �ҷ�����
			
			//idx�� �������� projectid ��ȸ
			producing_pid = dr.readconfirmPIDX(dm, (int) producing_idx.get(i)); //o_idx ������� projectid ��ȸ

			producing_partid = dr.readconfirmPart(dm, (int) producing_pid.get(i)); //projectid�� ���� partid��ȸ
			dr.readcon3dPart(dm, producing_partid);
			
			time_list.add(dm.getconfirmunitTime()); // o_idx�� �ش��ϴ� 3d printing time (capa�� ���)

//			System.out.println("time_list:"+time_list.get(i));

			for (int l = 0; l < date_list.length; l++) { //��ü ��� ����Ʈ�� 
				full_date_list.add(date_list[l]); //��� �� date�� 
				full_time_list.add(qty_list[l]*time_list.get(i)); //��� �� qty*����ǰ �ɸ��� �ð���
			}
			
//			for (int j = 0; j < full_date_list.size(); j++) {
//				System.out.println("full_date_list:"+full_date_list.get(j));
//				System.out.println("full_time_list:"+full_time_list.get(j));
//			}
			producing_partid.clear(); // �ҷ��Դ� partid �ʱ�ȭ
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
			
			available_capa[i] = full_capa[i] - daily_time_list[i]; //Ǯĳ�Ŀ��� ���� �ð� ����
			
			if (available_capa[i]<0) { //capa�� ������ ��� 0����
				available_capa[i]=0;
			}	
			
//			System.out.println("available capa:"+available_capa[i]);
		}		
		return available_capa;
			
		}
	}
}
	
