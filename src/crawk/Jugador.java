package crawk;

public class Jugador {
  int id;
  long partido;
  int teamid;
  int champion;
  int summoner;
  public String toString(){
    // Esta funcion devuelve en string la llave correspondiente a esta clase.
    String jugador = "";
    jugador += Integer.toString(this.id);
    jugador += " "+Long.toString(this.partido);
    return jugador;
  }
}
