package crawk;

public class Partido {
  long id;
  String queuetype;
  String season;
  String version;
  int creation;
  public String toString(){
    // Esta funcion devuelve en string la llave correspondiente a esta clase.
    String partido = "";
    partido += " "+Long.toString(this.id);
    return partido;
  }
}
