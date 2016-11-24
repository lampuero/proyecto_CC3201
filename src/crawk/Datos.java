package crawk;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class Datos {
	private Connection DMC=null;
	private Statement s=null;
	public Datos(){
	try{
		Class.forName("com.mysql.jdbc.Driver");
		DMC = DriverManager.getConnection ("jdbc:mysql://localhost/loldb","root", "ak8cbeh1");
	}catch(SQLException e)
	{
		e.printStackTrace();
		System.out.println("Intento: ERR01");
		
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println("Intento: ERR02");
	} 
}
	public int get_vers_idasdf(String X){
		try{
		s = DMC.createStatement();
		ResultSet rs=s.executeQuery ("SELECT id FROM version WHERE nombre="+X+";");
		if(rs.next()){
			return rs.getInt("id");
		}
		else{
			s= DMC.createStatement();
			s.executeUpdate("INSERT INTO version (nombre) VALUES ('"+X+"')");
			return get_vers_idasdf(X);
		}
		}catch(SQLException se){
			se.printStackTrace();
			return get_vers_idasdf(X);
		}finally{
			try{
		         if(s!=null)
		            DMC.close();
		      }catch(SQLException se){
		      }
		      try{
		         if(DMC!=null)
		            DMC.close();
		      }catch(SQLException e){
		         e.printStackTrace();
		      }
		}
	}
}
