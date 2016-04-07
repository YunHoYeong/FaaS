package edu.skku.planner.io;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import edu.skku.planner.DataModel;

public class DataReader {
	
	static Connection cnn = null;
	static Statement st = null;
	static ArrayList<Integer> order_list = new ArrayList<Integer>();
	private int[] parents_list = null;
	
	//connect DB
	public void connect() {
		
		try {
			
//			String propFile = DataReader.class.getResource("").getPath();			
//			File fileInSamePackage = new File(propFile + "DBProperties.properties");
//			
//			Properties props = new Properties(); //������Ƽ ��ü ����			
//			FileInputStream fis = new FileInputStream(fileInSamePackage); //������Ƽ ���� ��Ʈ���� ���			
//			props.load(new java.io.BufferedInputStream(fis)); //������Ƽ ���� �ε�
//			
//			String url = props.getProperty("URL");
//			String ip = props.getProperty("IP");
//			String port = props.getProperty("PORT");
//			String folder = props.getProperty("FOLDER");
//			String id = props.getProperty("ID");
//			String pw = props.getProperty("PW");
			
			//FaaS Cloud ��������
			String url = "org.mariadb.jdbc.Driver";
			String ip = "jdbc:mariadb://14.63.220.246:";
			String port = "7749";
			String folder = "/faasdb";
			String id = "faas";
			String pw = "fasta";
			
			//Localhost ��������
//			String url = "org.mariadb.jdbc.Driver";
//			String ip = "jdbc:mariadb://localhost:";
//			String port = "7749";
//			String folder = "/faasdb";
//			String id = "root";
//			String pw = "root@faas";
						
			Class.forName(url);
			cnn = DriverManager.getConnection(ip+port+folder, id, pw);
			System.out.println("MariaDB connection");
			
			st = cnn.createStatement();
			
		}
		catch(ClassNotFoundException | SQLException cnfe) {
			System.out.println("non-connection" + cnfe.getMessage());		
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	// read input data (ordertable)
	public void readforOrder(DataModel dm, String idx) {
			
		ResultSet rs = null;
		
		try {
			String sql = "select * from ordertable where idx="+idx;
			rs = st.executeQuery(sql);
			dm.loadOrder(rs);			
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage()); // alarm message
			System.out.println("SQLState: " + e.getSQLState()); // sql alarm message
			}		
	}
	
	// read input data (part)
	public void readforPart(DataModel dm, String projectID) {
		
		ResultSet rs = null;
		
		try {
			String sql = "select * from part where project="+projectID;
			rs = st.executeQuery(sql);
			dm.loadPart(rs);			
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage()); // alarm message
			System.out.println("SQLState: " + e.getSQLState()); // sql alarm message
			}		
	}
	
	// read input data (rel:BOM)
	public void readforRel(DataModel dm) {
		
		ResultSet rs = null;
		parents_list = dm.getallPartID();
		for (int i = 0; i < parents_list.length; i++) {
			try {
				String sql = "select * from rel where relating='"+parents_list[i]+"'";
				rs = st.executeQuery(sql);
				dm.loadRel(rs);
			}
			catch (SQLException e) {
				}
		}		
	}
	
	// read input data (wctr)
	public void readforWctr(DataModel dm) { //3D Printer Capa�� ***�����ʿ�***
		
		ResultSet rs = null;
		
		try {
			String sql = "select * from wctr where wctr_type=01";
			rs = st.executeQuery(sql);
			dm.loadWctr(rs);			
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage()); // alarm message
			System.out.println("SQLState: " + e.getSQLState()); // sql alarm message
			}		
	}
	
	// read input data (std_step)
	public void readforStd(DataModel dm, int partoid) {
		
		ResultSet rs = null;
		
		try {
			String sql = "select * from std_step where part_oid="+partoid;
			rs = st.executeQuery(sql);
			dm.loadStd(rs);			
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage()); // alarm message
			System.out.println("SQLState: " + e.getSQLState()); // sql alarm message
			}		
	}
	
	// read input data (3D ������ ����Ǵ� ��ǰ�� ��ȸ)
	public void readforStd3d(DataModel dm, int oid) {
		
		ResultSet rs = null;
		
		try {
			String sql = "select * from std_step where oid="+oid;
			rs = st.executeQuery(sql);
			dm.loadStd3d(rs);	//		
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage()); // alarm message
			System.out.println("SQLState: " + e.getSQLState()); // sql alarm message
			}		
	}
	
	// ������ Ȯ���� order �б�
	public List<Integer> readconfirmOrder(DataModel dm) {
		ResultSet rs = null;		
		
		try {
			String sql = "select * from ordertable where orderstatus='1'";
			rs = st.executeQuery(sql);
			List<Integer> p_idx = dm.loadconfirmOrder(rs);
			return p_idx;
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage()); // alarm message
			System.out.println("SQLState: " + e.getSQLState()); // sql alarm message
			}
		
		return null;
	}
	
	// ������ Ȯ���� ordertable.idx�κ��� projectid ��ȸ
	public List readconfirmPIDX(DataModel dm, int idx) {
		ResultSet rs = null;		
		
		try {
			String sql = "select * from ordertable where idx='"+idx+"'";
			rs = st.executeQuery(sql);
			List p_project = dm.loadconfirmPIDX(rs);
			return p_project;
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage()); // alarm message
			System.out.println("SQLState: " + e.getSQLState()); // sql alarm message
			}
		
		return null;
	}
	
	public List readconfirmPart(DataModel dm, int projectID) {
		
		ResultSet rs = null;
		
		try {
			String sql = "select * from part where project='"+projectID+"'";
			rs = st.executeQuery(sql);
			List p_parts = dm.loadconfirmPart(rs);
			return p_parts;
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage()); // alarm message
			System.out.println("SQLState: " + e.getSQLState()); // sql alarm message
			}
		return null;
	}
	
	public void readcon3dPart(DataModel dm, List part_list) {
		
		ResultSet rs = null;
		
		for (int i = 0; i < part_list.size(); i++) {
			try {
				String sql = "select * from std_step where wctr_type='P' and part_oid='"+part_list.get(i)+"'";
				rs = st.executeQuery(sql);
				dm.loadconfirmStd(rs);
			}
			catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage()); // alarm message
				System.out.println("SQLState: " + e.getSQLState()); // sql alarm message
				}
		}	
	}
			
	// ������ Ȯ���� order�� ��ȹ �б�
	public List readResult(DataModel dm, List producing_order) {
		
		ResultSet rs = null;
		
		for (int i = 0; i < producing_order.size(); i++) {
			try {
				String sql = "select * from planresult where o_id='"+producing_order.get(i)+"'";
				rs = st.executeQuery(sql);
				dm.loadResult(rs);	//		
			}
			catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage()); // alarm message
				System.out.println("SQLState: " + e.getSQLState()); // sql alarm message
				}	
		}
		
		return null;
	}
	
	public void readeachResult(DataModel dm, int o_id) {
		
		ResultSet rs = null;
		
		try {
			String sql = "select * from planresult where o_id='"+o_id+"'";
			rs = st.executeQuery(sql);
			dm.loadeachResult(rs);	//		
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage()); // alarm message
			System.out.println("SQLState: " + e.getSQLState()); // sql alarm message
		}
	}
	
	// save output data
	public void save(int[] plan, String projectID, String idx){ // DB�� plan ����� �����ϴ� �ڵ� �ۼ�

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		for (int i = 0; i < plan.length; i++) {
			
			try {
				
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, i);
				java.util.Date date = cal.getTime();
				
				String strDate = sdf.format(date);
				System.out.println(strDate);
				//String sql = "insert into planresult (projectid, date, qty) values"+"("+projectID+","+i+","+plan[i]+")";
				String sql = "insert into planresult (o_id, projectid, date, qty) values"+"("+idx+","+projectID+",STR_TO_DATE('"+strDate+"','%Y%m%d'),"+plan[i]+")";
				System.out.println("SQL:"+sql);
				st.executeUpdate(sql);
			}
			
			catch (SQLException se) {
				System.out.println("SQLException: " + se.getMessage()); // alarm message
				System.out.println("SQLState: " + se.getSQLState()); // sql alarm message
			}
			
			
		}
		
	}
	
	public void disconnect() {
		try {
			if (st != null) {
				st.close();
				st = null;
			}
		}
		catch (Exception e) {
			
		}
		try {
			if (cnn != null) {
				cnn.commit();
				cnn.close();
				cnn = null;
			}
		}
		catch (Exception e) {
			
		}
		
		
	}
	
}
