package edu.skku.planner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

//import java.util.Date;
//import java.util.*;
import edu.skku.planner.io.DataReader;

public class PlanBuilder {
	
	double[] a_capa;

	public int plan(String projectID, String idx) {
		
		DataReader dr = new DataReader();
        DataModel dm = new DataModel();
        
        int[] all_part_ID;
        ArrayList part_3d_oid;
        
        
		//connect
        dr.connect();
        
        //read from table
        dr.readforOrder(dm, idx);
        dr.readforPart(dm, projectID);
        dr.readforRel(dm);
        dr.readforWctr(dm);
        
        all_part_ID = dm.getallPartID();
        for (int i = 0; i < all_part_ID.length; i++) {
			dr.readforStd(dm, all_part_ID[i]);
		}
        
        part_3d_oid = dm.get3DPartOID(all_part_ID);
        for (int i = 0; i < part_3d_oid.size(); i++) {
        	dr.readforStd3d(dm, (int) part_3d_oid.get(i));			
		}
                
		//plan&save
        int type = dm.getOrderType(); //ordertype loading
        java.util.Date due = dm.getDueDate();
        int[] p_plan;
        
            if (type==0) { //����ֹ��� �ƴ� ���
            	
            	System.out.println("���� �ֹ� plan ����");
            	CapacityLoading cl = new CapacityLoading(dm);
    			a_capa = cl.capacityload(projectID); // capa���� ����
    			
    			if (due == null) { //duedate�� ���� ��� -> forward planning
    				FIFOPlanner fp = new FIFOPlanner(dm);
    				try{
    					p_plan = fp.fifoplan(a_capa);		
    					dr.save(p_plan, projectID, idx);
            			dr.disconnect();
            			System.out.println("���� �ֹ� forward plan �� ��� ���� �Ϸ�");
            			return 0;
    				} catch (UserException e){
						dr.disconnect();
						System.out.println("���� �ֹ� forward plan ����");
						return e.returnError();
					}
				} else { //duedate�� �ִ� ��� -> backward planning
					 
					BACKWARDPlanner bp = new BACKWARDPlanner(dm);
					try{
						p_plan = bp.backwardplan(a_capa);
						dr.save(p_plan, projectID, idx);
						dr.disconnect();
						System.out.println("���� �ֹ� backward plan �� ��� ���� �Ϸ�");
						return 0;
					} catch (UserException e){
						dr.disconnect();
						System.out.println("���� �ֹ� backward plan ����");
						return e.returnError();
					}
				}	
    		} 
            
            if (type==1){ //����ֹ��� ���
            	
            	System.out.println("����ֹ� plan ����");
    			
    			URGENTPlanner up = new URGENTPlanner(dm, projectID);
    			try{
    				int[] u_plan = up.urgentplan(projectID); 
    				dr.save(u_plan, projectID, idx);
        			dr.disconnect();
        			System.out.println("��� �ֹ� plan �� ��� ���� �Ϸ�");
        			return 0;
    			} catch (UserException e){
    				dr.disconnect();
					System.out.println("��� �ֹ� plan ����");
					return e.returnError();
				}
    		}
        
		return (Integer) null;   
	}
	public long diffCalendar(java.util.Date StartDay, java.util.Date EndDay)
	{
		long diff = 0;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		String start = formatter.format(StartDay);
		String end = formatter.format(EndDay);
		
		try{
			java.util.Date Today = formatter.parse(start);
			java.util.Date DueDate = formatter.parse(end);
			
			diff = DueDate.getTime() - Today.getTime();
			diff /= (24 * 60 * 60 * 1000);
			 
		} catch(ParseException e){
			e.printStackTrace();
		}
		
		return diff; 
	}
	
}
