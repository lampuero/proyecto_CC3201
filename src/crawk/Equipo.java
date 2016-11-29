package crawk;

public class Equipo {
  int teamid;
  long partido;
  boolean ganador;
  public String toString(){
    // Esta funcion devuelve en string la llave correspondiente a esta clase.
    String equipo ="";
    equipo += Integer.toString(this.teamid);
    equipo += " "+Long.toString(this.partido);
    return equipo;
  }
}
