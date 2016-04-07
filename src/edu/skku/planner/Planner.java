package edu.skku.planner;

public class Planner {

	public static void main(String[] args) {
		String projectID = null;
		String idx = null;
		int result;
		
		if (args.length == 0) {
			System.out.println("Argument is not given. set projectID=6 and idx =175");
			projectID = "6";
			idx = "175";
		}
		else {
			projectID = args[0];
			idx = args[1];
		}
		
		PlanBuilder pb = new PlanBuilder();
		result = pb.plan(projectID, idx);
		System.out.println(result);
	}

}
