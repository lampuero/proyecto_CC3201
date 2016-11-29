package crawk;

public class Equipo {
  int teamid;
  long partido;
  boolean ganador;
  public Equipo(int teamid, long partido, boolean ganador){
    this.teamid = teamid;
    this.partido = partido;
    this.ganador = ganador;
  }
  public String toString(){
    // Esta funcion devuelve en string la llave correspondiente a esta clase.
    String equipo ="";
    equipo += Integer.toString(this.teamid);
    equipo += " "+Long.toString(this.partido);
    return equipo;
  }
}
