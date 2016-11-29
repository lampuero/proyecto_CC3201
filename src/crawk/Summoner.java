package crawk;

public class Summoner {
  int id;
  String nombre;
  public String toString(){
    // Esta funcion devuelve en string la llave correspondiente a esta clase.
    String summoner = "";
    summoner += " "+Integer.toString(this.id);
    return summoner;
  }
}
