package cbr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import app.GUIApp;
import connector.CsvConnector;
import connector.CsvConnector2;
import model.Pregledi;
import model.Preporuke;
import similarity.TableSimilarity;
import ucm.gaia.jcolibri.casebase.LinealCaseBase;
import ucm.gaia.jcolibri.cbraplications.StandardCBRApplication;
import ucm.gaia.jcolibri.cbrcore.Attribute;
import ucm.gaia.jcolibri.cbrcore.CBRCase;
import ucm.gaia.jcolibri.cbrcore.CBRCaseBase;
import ucm.gaia.jcolibri.cbrcore.CBRQuery;
import ucm.gaia.jcolibri.cbrcore.Connector;
import ucm.gaia.jcolibri.exception.ExecutionException;
import ucm.gaia.jcolibri.method.retrieve.RetrievalResult;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
import ucm.gaia.jcolibri.method.retrieve.selection.SelectCases;

public class CbrPreporuke implements StandardCBRApplication{
	Connector _connector;  /** Connector object */
	CBRCaseBase _caseBase;  /** CaseBase object */

	NNConfig simConfig;  /** KNN configuration */
	
	public static List<Object> stringIspis = new ArrayList();
	public void configure() throws ExecutionException {
		_connector =  new CsvConnector2();
		
		_caseBase = new LinealCaseBase();  // Create a Lineal case base for in-memory organization
		
		simConfig = new NNConfig(); // KNN configuration
		simConfig.setDescriptionSimFunction(new Average());  // global similarity function = average
		
		// simConfig.addMapping(new Attribute("attribute", CaseDescription.class), new Interval(5));
		// TODO
		
	
		
//		Za dodatna
		ArrayList<String> pol = new ArrayList<String>();
		pol.add("M");
		pol.add("Z");

		TableSimilarity tablesPol = new TableSimilarity(pol);
		tablesPol.setSimilarity("M","Z", .6);
		
		ArrayList<String> dijagnoze = new ArrayList<String>();
		dijagnoze.add("hladnocu"); 
		dijagnoze.add("hronicni_sinusitis");
		dijagnoze.add("alergija_na_zivotinje");
		dijagnoze.add("alergijski_izazvan_konjuktivitis");
		dijagnoze.add("alergija_na_hranu");
		dijagnoze.add("sunce");
		dijagnoze.add("polen");
		dijagnoze.add("alergijski_rinitis"); //polenski tip
		dijagnoze.add("ekcem");
		dijagnoze.add("urtikarija"); 
		dijagnoze.add("seboreicni_dermatitis");
		dijagnoze.add("hipersenzitivnost");
	
	    TableSimilarity tablesDijagnoza = new TableSimilarity(dijagnoze);
		tablesDijagnoza.setSimilarity("hladnocu", "sunce", .4,.6);
		tablesDijagnoza.setSimilarity("ekcem", "sunce", .6);
		tablesDijagnoza.setSimilarity("hladnocu", "sunce", .4);
		tablesDijagnoza.setSimilarity("urtikarija", "sunce", .6);
		tablesDijagnoza.setSimilarity("urtikarija", "hladnocu", .4);
		tablesDijagnoza.setSimilarity("hipersenzitivnost","sunce",.5, .4);
		tablesDijagnoza.setSimilarity("hipersenzitivnost","hladnocu",.7, .6);
		tablesDijagnoza.setSimilarity("alergijski_izazvan_konjuktivitis","polen",.6);
		tablesDijagnoza.setSimilarity("alergijski_izazvan_konjuktivitis","alergija_na_zivotinje",.7, .6);
		tablesDijagnoza.setSimilarity("hronicni_sinusitis","hladnocu",.5);
		tablesDijagnoza.setSimilarity("seboreicni_dermatitis","ekcem",.4);
		tablesDijagnoza.setSimilarity("alergijski_rinitis","polen",.8);
		//Implementirani intervali pretrage kao i similarity tabela:
		Attribute atributGodine =new Attribute("godine", Preporuke.class); 
		simConfig.addMapping(atributGodine, new Interval(5)); 
		simConfig.setWeight(atributGodine, .5);
		
		Attribute atributPol = new Attribute("pol", Preporuke.class);
		simConfig.addMapping(atributPol, tablesPol);
		simConfig.setWeight(atributPol, .5);
		
		Attribute atributDijagnoza = new Attribute("dijagnoza", Preporuke.class);
		simConfig.addMapping(atributDijagnoza, tablesDijagnoza);
		simConfig.setWeight(atributDijagnoza, 1.0);
	
		
		// Equal - returns 1 if both individuals are equal, otherwise returns 0
		// Interval - returns the similarity of two number inside an interval: sim(x,y) = 1-(|x-y|/interval)
		// Threshold - returns 1 if the difference between two numbers is less than a threshold, 0 in the other case
		// EqualsStringIgnoreCase - returns 1 if both String are the same despite case letters, 0 in the other case
		// MaxString - returns a similarity value depending of the biggest substring that belong to both strings
		// EnumDistance - returns the similarity of two enum values as the their distance: sim(x,y) = |ord(x) - ord(y)|
		// EnumCyclicDistance - computes the similarity between two enum values as their cyclic distance
		// Table - uses a table to obtain the similarity between two values. Allowed values are Strings or Enums. The table is read from a text file.
		// TableSimilarity(List<String> values).setSimilarity(value1,value2,similarity) 
	}

	public void cycle(CBRQuery query) throws ExecutionException {
		Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(_caseBase.getCases(), query, simConfig);
		eval = SelectCases.selectTopKRR(eval, 3);
		System.out.println("Ciklus");
		System.out.println("Retrieved cases:");
		for (RetrievalResult nse : eval) {
			System.out.println(nse.get_case().getDescription() + " -> " + nse.getEval());
			//TO DO ispis rezultata u JList, a ne u konzolu:
			stringIspis.add(nse.get_case().getDescription()+ " -> " + nse.getEval());
			GUIApp.dodatnaIspitivanjaCBGui.add(Preporuke.dodatnaIspitivanjaCB);
			GUIApp.metodLecenjaCBGui.add(Preporuke.metodLecenjaCB);
			GUIApp.preventivniPregledCBGui.add(Preporuke.preventivniPregledCB);
		}
	}

	public void postCycle() throws ExecutionException {
		
	}

	public CBRCaseBase preCycle() throws ExecutionException {
		System.out.println("Preciklus");
		_caseBase.init(_connector);
		java.util.Collection<CBRCase> cases = _caseBase.getCases();
		for (CBRCase c: cases)
			System.out.println(c.getDescription());
		return _caseBase;
	}

	public static void RunCaseBased(int godine,String pol,String dijag) {
		StandardCBRApplication recommender = new CbrPreporuke();
		System.out.println("USAO U CbrPreporuke");
		System.out.println("Proslednjeo u RunCaseBased:");
		System.out.println("Godine: " + godine);
		System.out.println("Pol: "+ pol);
		System.out.println("Dijagnoza: " + dijag);
		
		try {
			recommender.configure();

			recommender.preCycle();

			CBRQuery query = new CBRQuery();
			Preporuke opisPreporuke = new Preporuke();
			opisPreporuke.setGodine(godine);
			opisPreporuke.setPol(pol);
			String dijagnoza= dijag;
			opisPreporuke.setDijagnoza(dijagnoza);
			
			System.out.println("opisPreporuke.getDijagnoza():" + opisPreporuke.getDijagnoza());
			query.setDescription(opisPreporuke);

			recommender.cycle(query);

			recommender.postCycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
