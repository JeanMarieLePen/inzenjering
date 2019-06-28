package app;
import app.GUIApp;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;
import com.ugos.jiprolog.engine.JIPVariable;

public class Prolog {
	
	//PROLOG METODA: Uvucena preko kopirana u resources i uvucena preko buildpatha:
	public static JIPQuery konsultujPrologDijagnoza(String simptomi,int godine,String pol) {

		System.out.println("Hello World from PROLOG!");
		System.out.println("Prosledjeni text:" + simptomi +" godineProl: "+ godine +" polPrl: " + pol);
		JIPEngine engine = new JIPEngine();
		try{
			if(pol.equals("M")) {
				pol="musko";
			}
			else if(pol.equals("Z")) {
				pol="zensko";
			}
			System.out.println("Pol posle promene: " + pol);
		 	engine.consultFile("Prolog/ispitivanjaIdijagnostika.prol");
		 	JIPTermParser parser = engine.getTermParser();
		 	JIPTerm term = parser.parseTerm("anamneza(["+simptomi+"],Alergija_na,"+godine+","+pol+")"); 
			JIPQuery query = engine.openSynchronousQuery(term);
	
			return query;
		}catch (Exception e){
			e.printStackTrace();
			return null;
	  }
	}	
		
		public static JIPQuery konsultujPrologIspitivanja(String dijagnoza) {
			

			JIPEngine engine = new JIPEngine();
			try{
			 	engine.consultFile("Prolog/ispitivanjaIdijagnostika.prol");
			 	JIPTermParser parser = engine.getTermParser();
			 	JIPTerm term = parser.parseTerm("pregled("+dijagnoza+",Ispitivanje,Procenat)"); 
				JIPQuery query = engine.openSynchronousQuery(term);

				return query;
			}catch (Exception e){
				e.printStackTrace();
				return null;
		  }
	   }
		
		public static JIPQuery konsultujPrologLecenje(String dijagnoza) {
			System.out.println("Hello World from konsultujPrologLecenje!");
			System.out.println("Prosledjeni text:" + dijagnoza);
			JIPEngine engine = new JIPEngine();
			try{
			 	engine.consultFile("Prolog/ispitivanjaIdijagnostika.prol");
			 	JIPTermParser parser = engine.getTermParser();
			 	JIPTerm term = parser.parseTerm("zahvat("+dijagnoza+",Ispitivanje,Procenat)"); //ovde menjamo (npr isti(3,X));
				JIPQuery query = engine.openSynchronousQuery(term);
		

				return query;
			}catch (Exception e){
				e.printStackTrace();
				return null;
		  }
		}
		
		public static JIPQuery konsultujPrologPrevPreg(String alergija,int godine,String pol) {
			System.out.println("Hello World from konsultujPrologPreventivniPregled!");
			System.out.println("Prosledjeni text:" + alergija);
			JIPEngine engine = new JIPEngine();
			try{
					if(pol.equals("M")) {
						pol="musko";
					}
					else if(pol.equals("Z")) {
						pol="zensko";
					}
			 	engine.consultFile("Prolog/ispitivanjaIdijagnostika.prol");
			 	JIPTermParser parser = engine.getTermParser();
			 	JIPTerm term = parser.parseTerm("preventivni_pregled("+alergija+","+godine+","+ pol +", Rezultat)"); //ovde menjamo (npr isti(3,X));
				JIPQuery query = engine.openSynchronousQuery(term);
		

				return query;
			}catch (Exception e){
				e.printStackTrace();
				return null;
		  }
		}
}