package crawk;


public class Principal {
	static Base BD;
	static MatchInfo MT;
	static HistoryInfo HT;
	static int Id;
	static String SERVER;
	static String key;
	public static void main(String[] arg){
	  SERVER = arg[0].toUpperCase();
	  key = arg[1];
		int iteracion=0;
		while(iteracion<10000){
			System.out.println(iteracion);
			iteracion++;
			try{
				BD= new Base(SERVER);
			}catch (Exception e)
			{e.printStackTrace(System.out);}
			try{
				Id=BD.lastSummoner();
			}catch (Exception e)
			{System.out.println("Error Ultimo Summoner"+e);}
			HT=new HistoryInfo(Id,SERVER);
			HT.setAPIKey(key);
			try{
				long[] idpart = HT.getId();
				for(int i=0;i<idpart.length;i++){
					if ((idpart[i])!=0){
						MT=new MatchInfo(idpart[i],SERVER);
						MT.setAPIKey(key);
						MT.prepare();
						MT.process();
						int[][][] jugadores=MT.jugadores;
						String[][] nombres=MT.nombres;
						String[] valores =MT.valores;
						int creation = MT.creation;
						BD.agregarPartido(idpart[i],jugadores,nombres,valores,creation);
					}
				}
			}catch(Exception e){e.printStackTrace(System.out);}
		}
	}
}
