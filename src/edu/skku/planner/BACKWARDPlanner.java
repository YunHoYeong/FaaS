package edu.skku.planner;

import java.util.Date;
import java.lang.Math;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import edu.skku.planner.io.DataReader;

public class BACKWARDPlanner {
	// Git Test (16.04.07)
	String productID; // �����û ����ǰID
	int order_qty; // ����ǰ �����û ����(�ֹ���)
	int[] part_ID; // 3D������ ���� �ʿ� ��ǰ ID
	Date due_date; // �� �Է� duedate ǥ��
	int num_part; // 3D������ ���� �ʿ� ��ǰ ����(BOM)
	int[] part_qty_1; // 3D������ ���� �ʿ� ��ǰ �� - ����ǰ 1����
	double[] part_unit_time; // 3D�������� ��ǰ�� ����ð�
	double[] a_capa; // ���ں� capa
	int plan_horizon; // �����ȹ ����
	double lead_time; // 3D������ ���� ����Ÿ��
	DataModel dm = new DataModel();
	DataReader dr = new DataReader();
	static double i_unit_time;

	public BACKWARDPlanner(DataModel dm) {

		productID = dm.getProductID(); // �����û ����ǰID
		order_qty = dm.getOrderQty(); // ����ǰ �����û ����(�ֹ���)
		part_ID = dm.getPartID(); // 3D������ ���� �ʿ� ��ǰ ID
		due_date = dm.getDueDate(); // �� �Է� duedate
		num_part = part_ID.length; // 3D������ ���� �ʿ� ��ǰ ����(BOM)
		part_qty_1 = dm.getPartQtyperUnit(); // 3D������ ���� �ʿ� ��ǰ �� - ����ǰ 1����
		part_unit_time = dm.getUnitTime(part_ID); // 3D�������� ��ǰ�� ����ð�
		// a_capa = dm.getCapacity(); //���ں� capa
		lead_time = dm.getLeadTime(); // 3D������ ���� lead time
	}

	public int[] backwardplan(double[] a_capa) throws UserException {


		plan_horizon = a_capa.length; // �����ȹ ����

		int[] b_f_plan = new int[plan_horizon]; // ����ǰ ������ ���� ��ȹ.. �ϳ��� ����ǰ�� ����  ��ǰ�� ��� ����Ǵ� ���� ���� (Output data)
		int[][] bottleneck_plan = new int[plan_horizon][num_part]; // ��ǰ�� 3D������  ���� ��ȹ(Output data)
		i_unit_time = totaltimeCalperunit(); // ����ǰ 1���� ���� ��ǰ�� �� ����ð�

		int i_part_qty = 0; // ����ǰ 1���� ���� ��ǰ�� �� ����
		for (int i = 0; i < num_part; i++) {
			i_part_qty = i_part_qty + part_qty_1[i];
		}
		System.out.println("Number of parts per unit: " + i_part_qty);

		// 1. Today(��)�� Due date������ ���̸� ����.

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date tempToday = new Date();

		String start = formatter.format(tempToday);
		String end = formatter.format(due_date);
		long diff = 0;
		try {
			Date Today = formatter.parse(start);
			Date DueDate = formatter.parse(end);

			diff = DueDate.getTime() - Today.getTime();
			diff /= (24 * 60 * 60 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if( plan_horizon < diff ){
			// ErrorCode 3 : Due Date�� Plan_horizon�� �ʰ��� ���
			throw new UserException(3);
		}
		
		int l_days = (int) (lead_time / (8 * 60 * 60)); // LT�� ���� 8�ð�(��) �ݿ� ==> 8�ð�(��) ����ȭ �ؾ���.
		int l_hours = (int) (lead_time % (8 * 60 * 60)); // LT�� ���ڸ� �� ������

		// 2. lead Time��ŭ ����. Ž���� ������ ��¥�� ��. ���� �ش� ��¥ ������ capa�� ������Ŵ.
		int c_days = (int) diff - l_days;
		a_capa[c_days] = a_capa[c_days] - l_hours;
		
		if (a_capa[c_days] < 0.0) {
			a_capa[c_days] = 0.0;
		}
		
		for (int i = (order_qty - 1); i >= 0; i--) // ���� ��û�� ����ǰ�� �� ����.
		{
			if(c_days < 0){	break;}
			for (int j = (num_part - 1); j >= 0; j--) // (BOM)�ʿ��� ��ǰ�� ����.
			{
				if(c_days < 0){	break;}
				int tmp = part_qty_1[j];
				for (int k = (tmp - 1); k >= 0; k--) // (BOM)��ǰ�� ����.
				{
					if(c_days < 0){	break;}
					for (int days = c_days; days >= 0; days--) // days = ��¥
					{
						if(c_days < 0){	break;}
						if (a_capa[days] >= part_unit_time[j]) // capa�� �����Ѵٸ�
						{
							bottleneck_plan[days][j]++;
							a_capa[days] -= part_unit_time[j];
							break;
						} else // capa�� ������ ������ �̵�
						{
							if(c_days - 1 >= 0)
							{
								c_days --;
								a_capa[c_days] += a_capa[days]; // ���� ���� �ҷ� capa �̿��Ҽ� �ֵ��� �ݿ�
								a_capa[days] = 0;
							}
							else c_days--; break;
						}
					}
				}
			}
			if(c_days >= 0){  b_f_plan[c_days]++;  }
			
		}

		int[] p_plan = new int[plan_horizon];
		int l_qty = 0; // leadtime �ð��� ���귮..�� ���ڷ� �ϼ��� �и��� ����

		// LT�� �Ϸ� �̻�κ��� ���� �̵�
		for (int i = 0; i < (plan_horizon - l_days); i++) {
			p_plan[i + l_days] = b_f_plan[i];
		}

		// LT �Ϸ� ���Ϻκ��� �Ϻ� ���� �̵�
		for (int i = (plan_horizon - 1); i > 0; i--) {
			int tmp_qty;
			if (p_plan[i - 1] > 0) {
				if (a_capa[i - 1] > 0) { // ��ȹ�� �ִµ� capa�� ������.. plan ��������
					l_qty = (int) (l_hours / i_unit_time);
					if (l_hours < a_capa[i - 1]) {
						tmp_qty = 0;
					} else {
						l_qty = (int) Math.ceil(((l_hours - a_capa[i - 1]) / i_unit_time)); // ���Ͽϼ��Ұ�����
						tmp_qty = l_qty;
						if (p_plan[i - 1] > l_qty) {
							tmp_qty = l_qty;
						} else {
							tmp_qty = p_plan[i - 1];
						}
					}
				} else { // full capa�� �����ѳ�..
					l_qty = (int) Math.ceil((l_hours / i_unit_time)); // l_hour�̳��� ���� ���ִ� ����... ���Ͽϼ��Ұ�
					if (p_plan[i - 1] > l_qty) {
						tmp_qty = l_qty;
					} else {
						tmp_qty = p_plan[i - 1];
					}
				}
				p_plan[i - 1] = p_plan[i - 1] - tmp_qty;
				p_plan[i] = p_plan[i] + tmp_qty;
			}
		}

		System.out.println("Completed:");

		System.out.println("===========  3D ������ �Ϻ� ���귮 (��ǰ����) ======================");
		for (int i = 0; i < plan_horizon; i++) {
			System.out.print("����(" + i + ") ==> ��ǰ ���� ���귮: ");
			for (int j = 0; j < num_part; j++) {
				System.out.print(bottleneck_plan[i][j] + "/");
			}
			System.out.println();
		}

		System.out.println("===========  3D ������ �Ϻ� ���귮 (����ǰ/�ܿ�capa����) ======================");
		for (int i = 0; i < plan_horizon; i++) {
			System.out.println("����(" + i + ") ==> ����ǰ ���� ���귮: " + b_f_plan[i] + "/" + a_capa[i]);
		}

		System.out.println("===========  �Ϻ� ����ǰ ���귮 (��������) ======================");
		for (int i = 0; i < plan_horizon; i++) {
			System.out.println("����(" + i + ") ==> ����ǰ ���� ���귮: " + p_plan[i]);
		}

		System.out.println("backward �÷��� �Ϸ�");

		int sumManufactured = 0;
		for(int i : p_plan) sumManufactured += i;
		
		// Return ���� Error �˻�.
		if(sumManufactured < order_qty){
			// ErrorCode 4 : Due Dates ���� ������ �Ұ��� ���
			throw new UserException(4);
		}
		return p_plan;
	}

	public double totaltimeCalperunit() // ����ǰ 1���� ���� ��ǰ�� �� ����ð�
	{
		for (int i = 0; i < num_part; i++) {
			i_unit_time = i_unit_time + part_qty_1[i] * part_unit_time[i];
		}
		System.out.println("Total time required per unit: " + i_unit_time);

		return i_unit_time;
	}
}
