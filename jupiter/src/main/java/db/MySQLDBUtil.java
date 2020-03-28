package db;

public class MySQLDBUtil {

	//endpoint
	private static final String INSTANCE = "jupiter-job-recom-mysql.ckgcjsfximnc.us-west-1.rds.amazonaws.com";
	private static final String PORT_NUM = "3306";
	public static final String DB_NAME = "jupiter";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "tjupfrcc615";
	
	//assembly of above
	//this is not http://so the password is safe, this is jdbc:mysql://
	public static final String URL = "jdbc:mysql://"
			+ INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";
}
