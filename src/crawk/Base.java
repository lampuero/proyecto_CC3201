package crawk;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.sql.PreparedStatement;

public class Base{
	private Connection DMC;
	static String SERVER;
	private String HOST = "127.0.0.1";
    private String PORT = "5432";
    private String DATABASE = "cc3201";
    private String CONNECTION_URL = "jdbc:postgresql://"+HOST+":"+PORT+"/"+DATABASE;
    private String USERNAME = "cc3201";
    private String PASSWORD = "eewugheph4dia4Vah9ee";
    private String SSL = "false";
    PreparedStatement existPartido;
    PreparedStatement agregarPartido;
    PreparedStatement agregarJugador;
    PreparedStatement existSummoner;
    PreparedStatement agregarSummoner;
    PreparedStatement agregarEquipo;
    PreparedStatement getUltimo;
    PreparedStatement updateUltimo;
    PreparedStatement test;
    
	public Base(String server){
	  SERVER=server;
	  String url = CONNECTION_URL;
      Properties props = new Properties();
      props.setProperty("user",USERNAME);
      props.setProperty("password",PASSWORD);
      props.setProperty("ssl",SSL);
      props.setProperty("sslfactory","org.postgresql.ssl.NonValidatingFactory");
      try {
        DMC = DriverManager.getConnection(url, props);
        getUltimo = DMC.prepareStatement("SELECT id FROM elopic.summoner WHERE server = ? ORDER BY last ASC LIMIT 1");
        updateUltimo = DMC.prepareStatement("UPDATE elopic.summoner SET  last= NOW() WHERE id=? AND server=?");
        existPartido = DMC.prepareStatement("SELECT count(*) AS x FROM elopic.partido WHERE id = ? AND server = ?");
        agregarPartido = DMC.prepareStatement("INSERT INTO elopic.partido (id,server,queuetype,season,version,creation)  VALUES (?,?,?,?,?,?)" );
        agregarEquipo = DMC.prepareStatement("INSERT INTO elopic.equipo (teamid,partido,server,ganador)  VALUES (?,?,?,?)" );
        agregarJugador = DMC.prepareStatement("INSERT INTO elopic.Jugador (id,partido, server,teamid,summoner,champion)  VALUES (?,?,?,?,?,?)" );
        existSummoner = DMC.prepareStatement("SELECT count(*) AS x FROM elopic.summoner WHERE id = ? AND server = ?");
        agregarSummoner = DMC.prepareStatement("INSERT INTO elopic.Summoner (id, server, nombre, last)  VALUES (?,?,?,to_timestamp(0))" );
        test = DMC.prepareStatement("SELECT * FROM elopic.summoner");
        getUltimo.setString(1, SERVER);
        updateUltimo.setString(2, SERVER);
        agregarPartido.setString(2, SERVER);
        existPartido.setString(2, SERVER);
        agregarEquipo.setString(3, SERVER);
        agregarJugador.setString(3, SERVER);
        existSummoner.setString(2, SERVER);
        agregarSummoner.setString(2, SERVER);
        DMC.setAutoCommit(false);
      } catch (SQLException e) {
        e.printStackTrace();
      }
	}
	
	public int get_vers_id(String X){
		try{
		Statement s = DMC.createStatement();
		ResultSet rs=s.executeQuery ("SELECT id FROM elopictemp.version WHERE nombre="+X+";");
		if(rs.next()){
			return rs.getInt("id");
		}
		else{
			s= DMC.createStatement();
			s.executeUpdate("INSERT INTO elopictemp.version (nombre) VALUES ('"+X+"')");
			return get_vers_id(X);
		}
		}catch(SQLException se){
			se.printStackTrace();
			return get_vers_id(X);
		}
	}
	
	public int lastSummoner() throws SQLException{
		ResultSet rs=getUltimo.executeQuery ();
		rs.next();
		int Resultado=rs.getInt(1);
		updateUltimo.setInt(1,Resultado);
		updateUltimo.executeUpdate();
		return Resultado;
	}

	public boolean existePartido(long idpart) throws SQLException{
	  existPartido.setLong(1, idpart);
	  ResultSet rs = existPartido.executeQuery();
	  return rs.getInt("x") == 1;
	}
	public void agregarPartido(long idpart,int[][][]p, String[][] nombres, String[] valores, int creation) throws SQLException{
	  if(existePartido(idpart)){
	    return;
	  }
      agregarPartido.setLong(1, idpart);
      agregarPartido.setString(2, SERVER);
      agregarPartido.setString(3, valores[0]);
      agregarPartido.setString(4, valores[1]);
      agregarPartido.setString(5, valores[2]);
      agregarPartido.setInt(6, creation);
      agregarPartido.executeUpdate();
      System.out.println("Agregado partido "+Long.toString(idpart));
      for(int i = 0; i<2; i++){
        agregarEquipo.setLong(1, idpart);
        agregarEquipo.setBoolean(3,(i==0));
        agregarEquipo.executeUpdate();
        for(int j = 0; j<p[i][1].length;j++){
          existSummoner.setInt(1,  p[i][1][j]);
          ResultSet rs = existSummoner.executeQuery();
          if(rs.next() && rs.getInt("x") == 1){
              System.out.println("Summoner repetido");
          }
          else{
            agregarSummoner.setInt(1, p[i][1][j]);
            agregarSummoner.setString(3, nombres[i][j]);
            agregarSummoner.executeUpdate();
          }
          agregarJugador.setLong(1,idpart);
          agregarJugador.setInt(3,p[i][1][j]);
          agregarJugador.setInt(4,p[i][0][j]);
          agregarJugador.setBoolean(5,(i==0));
          agregarJugador.executeUpdate();
        }
      DMC.commit();
      }
      
	  //for(int i = 0; i<5;i++){
	  
	    
	    
	  //}
	  /*
		try{
			for(int i=0;i<5;i++){
				for(int k=0;k<i;k++){
					int dupla=Math.min(p[0][0][i],p[0][0][k])*1000+Math.max(p[0][0][i],p[0][0][k]);
					Statement s = DMC.createStatement();
					ResultSet rs=s.executeQuery ("SELECT count(*),ganados,perdidos FROM resultados WHERE dupla="+dupla+";");
					rs.next();
					int ganados;
					if(rs.getInt(1)==0){
						ganados=1;
						s= DMC.createStatement();
						s.executeUpdate("INSERT INTO resultados (dupla,ganados,perdidos,asobreb,bsobrea) Values ("+dupla+",1,0,0,0)");
					}
					else{
						ganados=rs.getInt(2)+1;
						Statement kk= DMC.createStatement();
						kk.executeUpdate("UPDATE resultados set ganados="+ganados+" WHERE dupla="+dupla+"");
					}
				}
			}
			for(int i=0;i<5;i++){
				for(int k=0;k<i;k++){
					int dupla=Math.min(p[1][0][i],p[1][0][k])*1000+Math.max(p[1][0][i],p[1][0][k]);
					Statement s = DMC.createStatement();
					ResultSet rs=s.executeQuery ("SELECT count(*),ganados,perdidos FROM resultados WHERE dupla="+dupla+";");
					rs.next();
					int ganados;
					if(rs.getInt(1)==0){
						s= DMC.createStatement();
						s.executeUpdate("INSERT INTO Resultados (dupla,ganados,perdidos,asobreb,bsobrea) Values ("+dupla+",0,1,0,0)");
					}
					else{
						ganados=rs.getInt(3)+1;
						Statement kk = DMC.createStatement();
						kk.executeUpdate("UPDATE resultados set perdidos="+ganados+" WHERE dupla="+dupla+"");		
					}
				}
			}
			for(int i=0;i<5;i++){
				agregarsum(p[0][1][i]);
				agregarsum(p[1][1][i]);
				for(int k=0;k<5;k++){
					int dupla;
					if(p[0][0][i]<p[1][0][k]){
						dupla=p[0][0][i]*1000+p[1][0][k];
						Statement s = DMC.createStatement();
						ResultSet rs=s.executeQuery ("SELECT count(*), asobreb FROM resultados WHERE dupla="+dupla+";");
						rs.next();
						int ganados;
						if(rs.getInt(1)==0){
							s= DMC.createStatement();
							s.executeUpdate("INSERT INTO resultados (dupla,ganados,perdidos,asobreb,bsobrea) Values ("+dupla+",0,0,1,0)");
						}
						else{
							ganados=rs.getInt(2)+1;
							Statement kk = DMC.createStatement();
							kk.executeUpdate("UPDATE resultados set asobreb="+ganados+" WHERE dupla="+dupla+"");		
						}
					}
					else{
						dupla=p[1][0][k]*1000+p[0][0][i];
						Statement s = DMC.createStatement();
						ResultSet rs=s.executeQuery ("SELECT count(*), bsobrea FROM resultados WHERE dupla="+dupla+";");
						rs.next();
						int ganados;
						if(rs.getInt(1)==0){
							s= DMC.createStatement();
							s.executeUpdate("INSERT INTO resultados (dupla,ganados,perdidos,asobreb,bsobrea) Values ("+dupla+",0,0,0,1)");
						}
						else{
							ganados=rs.getInt(2)+1;
							Statement kk = DMC.createStatement();
							kk.executeUpdate("UPDATE resultados set bsobrea="+ganados+" WHERE dupla="+dupla+"");		
						}
					}
				}
			}
		}catch (Exception e){
			System.out.println(e);
	
		}
		*/
	}

}
