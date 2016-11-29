package crawk;

public class Kill {
  int killer;
  int dead;
  long partido;
  int time;
  public Kill(int killer, int dead, long partido, int time){
    this.killer = killer;
    this.dead = dead;
    this.partido = partido;
    this.time = time;
  }
  public String toString(){
    // Esta funcion devuelve en string la llave correspondiente a esta clase.
    String kill = "";
    kill += Integer.toString(this.killer);
    kill += " "+Integer.toString(this.dead);
    kill += " "+Long.toString(this.partido);
    kill += " "+Integer.toString(this.time);
    return kill;
  }
}
