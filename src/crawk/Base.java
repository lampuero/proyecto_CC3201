package crawk;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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
    PreparedStatement existEquipo;
    PreparedStatement agregarChampion;
    PreparedStatement agregarKill;
    PreparedStatement existJugador;
    PreparedStatement existChampion;
    //PreparedStatement existKill;
    
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
        getUltimo = DMC.prepareStatement("SELECT id FROM elopick.summoner WHERE server = ? ORDER BY last ASC LIMIT 1");
        updateUltimo = DMC.prepareStatement("UPDATE elopick.summoner SET  last= NOW() WHERE id=? AND server=?");
        agregarPartido = DMC.prepareStatement("INSERT INTO elopick.partido (id,server,queuetype,season,version,creation)  VALUES (?,?,?,?,?,?)" );
        agregarEquipo = DMC.prepareStatement("INSERT INTO elopic.equipo (teamid,partido,server,ganador)  VALUES (?,?,?,?)" );
        agregarJugador = DMC.prepareStatement("INSERT INTO elopic.jugador (id,partido, server,teamid,summoner,champion)  VALUES (?,?,?,?,?,?)" );
        agregarSummoner = DMC.prepareStatement("INSERT INTO elopic.summoner (id, server, nombre, last)  VALUES (?,?,?,to_timestamp(0))" );
        agregarChampion = DMC.prepareStatement("INSERT INTO elopic.champion (id, nombre, key) VALUES(?,?,?)");
        agregarKill = DMC.prepareStatement("INSERT INTO elopick.kill (killer, dead, partido, server, time) VALUES (?,?,?,?,?)");
        existPartido = DMC.prepareStatement("SELECT COUNT (*) AS x FROM elopick.partido WHERE id = ? AND server = ?");
        existEquipo = DMC.prepareStatement("SELECT COUNT (*)AS x FROM elopick.equipo WHERE teamid = ? AND partido = ? AND server = ?");
        existJugador = DMC.prepareStatement("SELECT COUNT(*) AS x FROM elopick.jugador WHERE id = ? AND partido = ? AND server = ?");
        existSummoner = DMC.prepareStatement("SELECT COUNT (*) AS x FROM elopick.summoner WHERE id = ?");
        existChampion = DMC.prepareStatement("SELECT COUNT (*) AS x FROM elopick.champion WHERE id = ?");
        //existKill = DMC.prepareStatement("SELECT COUNT (*) AS x FROM elopick.kill WHERE id = ?");
        getUltimo.setString(1, SERVER);
        updateUltimo.setString(2, SERVER);
        agregarPartido.setString(2, SERVER);
        agregarEquipo.setString(3, SERVER);
        agregarJugador.setString(3, SERVER);
        agregarSummoner.setString(2, SERVER);
        agregarKill.setString(4,SERVER);
        existPartido.setString(2, SERVER);
        existEquipo.setString(3, SERVER);
        existJugador.setString(3, SERVER);
        DMC.setAutoCommit(false);
      } catch (SQLException e) {
        e.printStackTrace();
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
	public void agregarPartido(long id, String queuetype, String season, String version, int creation) throws SQLException{
	  if(existePartido(id)){
	    return;
	  }
      agregarPartido.setLong(1, id);
      agregarPartido.setString(2, SERVER);
      agregarPartido.setString(3, queuetype);
      agregarPartido.setString(4, season);
      agregarPartido.setString(5, version);
      agregarPartido.setInt(6, creation);
      agregarPartido.executeUpdate();
	}
	public boolean existeEquipo(int teamid, long partido) throws SQLException{
	    existEquipo.setInt(1, teamid);
	    existEquipo.setLong(2, partido);
	    ResultSet rs = existEquipo.executeQuery();
        return rs.getInt("x") == 1;
    }
    public void agregarEquipo(int teamid, long partido, boolean ganador) throws SQLException{
	    if (existeEquipo(teamid, partido))
	        return;
	    agregarEquipo.setInt(1, teamid);
	    agregarEquipo.setLong(2, partido);
	    agregarEquipo.setBoolean(4, ganador);
	    agregarEquipo.executeUpdate();
    }
    public boolean existeJugador(int id, long partido) throws SQLException{
        existJugador.setInt(1, id);
        existJugador.setLong(2, partido);
        ResultSet rs = existJugador.executeQuery();
        return rs.getInt("x") == 1;
    }
    public void agregarJugador(int id, long partido,int teamid, int champion, int summoner) throws SQLException{
        if (existeJugador(id, partido))
            return;
        agregarJugador.setInt(1, id);
        agregarJugador.setLong(2, partido);
        agregarJugador.setInt(4, teamid);
        agregarJugador.setInt(5, champion);
        agregarJugador.setInt(6, summoner);
        agregarJugador.executeUpdate();
    }
    public boolean existeSummoner(int id) throws SQLException{
        existSummoner.setInt(1, id);
        ResultSet rs = existSummoner.executeQuery();
        return rs.getInt("x") == 1;
    }
    public void agregarSummoner(int id, String nombre) throws SQLException{
        if (existeSummoner(id))
            return;
	    agregarSummoner.setInt(1, id);
        agregarSummoner.setString(2, nombre);
        agregarSummoner.executeUpdate();
    }
    public boolean existeChampion(int id) throws SQLException{
        existChampion.setInt(1, id);
        ResultSet rs = existChampion.executeQuery();
        return rs.getInt("x") == 1;
    }
    public void agregarChampion(int id, String nombre) throws SQLException{
        if (existeChampion(id))
            return;
        agregarChampion.setInt(1, id);
        agregarChampion.setString(2, nombre);
        agregarChampion.executeUpdate();
    }
    public void agregarKill(int killer, int dead, long partido, int time) throws SQLException{
        agregarKill.setInt(1, killer);
        agregarKill.setInt(2, dead);
        agregarKill.setLong(3, partido);
        agregarKill.setInt(5, time);
        agregarKill.executeUpdate();
    }
    public void agregarDatos(Partido partido, ArrayList<Jugador> jugadores, ArrayList<Summoner> summoners, Equipo[] equipos, ArrayList<Kill> kills) throws SQLException{
	    // esto va sin un loop porque se ingresa un solo partido
	    agregarPartido(partido.id, partido.queuetype, partido.season, partido.version, partido.creation);        
        System.out.println("Agregado partido "+partido);
        for(Jugador jugador : jugadores){
            // si no cachan este es un for each loop en java
            agregarJugador(jugador.id, jugador.partido, jugador.teamid, jugador.champion, jugador.summoner);
            System.out.println("Agregado jugador "+jugador);
        }
        for (Summoner summoner : summoners){
            // si no cachan este es un for each loop en java
            agregarSummoner(summoner.id, summoner.nombre);
            System.out.println("Agregado summoner "+summoner);
        }
        for (Equipo equipo : equipos) {
            // si no cachan este es un for each loop en java
            agregarEquipo(equipo.teamid, equipo.partido, equipo.ganador);
            System.out.println("Agregado equipo "+equipo);
        }
        Iterator<Kill> killIterator = kills.iterator();
        while (killIterator.hasNext()){
            Kill kill = killIterator.next();
            agregarKill(kill.killer, kill.dead, kill.partido, kill.time);
            System.out.println("Agregado kill "+kill);
        }
        DMC.commit();
    }
}
