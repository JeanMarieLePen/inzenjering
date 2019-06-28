package cbr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import app.GUIApp;
import connector.CsvConnector;
import model.Pregledi;
import similarity.SimptomSimilarity;
import similarity.TableSimilarity;
import ucm.gaia.jcolibri.casebase.LinealCaseBase;
import ucm.gaia.jcolibri.cbraplications.StandardCBRApplication;
import ucm.gaia.jcolibri.cbrcore.Attribute;
import ucm.gaia.jcolibri.cbrcore.CBRCase;
import ucm.gaia.jcolibri.cbrcore.CBRCaseBase;
import ucm.gaia.jcolibri.cbrcore.CBRQuery;
import ucm.gaia.jcolibri.cbrcore.CaseBaseFilter;
import ucm.gaia.jcolibri.cbrcore.CaseComponent;
import ucm.gaia.jcolibri.cbrcore.Connector;
import ucm.gaia.jcolibri.connector.PlainTextConnector;
import ucm.gaia.jcolibri.exception.ExecutionException;
import ucm.gaia.jcolibri.exception.InitializingException;
import ucm.gaia.jcolibri.extensions.recommendation.casesDisplay.UserChoice;
import ucm.gaia.jcolibri.extensions.recommendation.conditionals.BuyOrQuit;
import ucm.gaia.jcolibri.extensions.recommendation.conditionals.ContinueOrFinish;
import ucm.gaia.jcolibri.extensions.recommendation.navigationByProposing.CriticalUserChoice;
import ucm.gaia.jcolibri.extensions.recommendation.navigationByProposing.CritiqueOption;
import ucm.gaia.jcolibri.extensions.recommendation.navigationByProposing.DisplayCasesTableWithCritiquesMethod;
import ucm.gaia.jcolibri.extensions.recommendation.navigationByProposing.queryElicitation.MoreLikeThis;
import ucm.gaia.jcolibri.method.gui.formFilling.ObtainQueryWithFormMethod;
import ucm.gaia.jcolibri.method.retrieve.RetrievalResult;
import ucm.gaia.jcolibri.method.retrieve.FilterBasedRetrieval.FilterBasedRetrievalMethod;
import ucm.gaia.jcolibri.method.retrieve.FilterBasedRetrieval.FilterConfig;
import ucm.gaia.jcolibri.method.retrieve.FilterBasedRetrieval.predicates.NotEqual;
import ucm.gaia.jcolibri.method.retrieve.FilterBasedRetrieval.predicates.QueryLess;
import ucm.gaia.jcolibri.method.retrieve.FilterBasedRetrieval.predicates.QueryMore;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.NNConfig;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Table;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.Threshold;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.recommenders.InrecaLessIsBetter;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.local.recommenders.InrecaMoreIsBetter;
import ucm.gaia.jcolibri.method.retrieve.selection.SelectCases;
import ucm.gaia.jcolibri.util.FileIO;

public class CbrApplication implements StandardCBRApplication {
	
	Connector _connector;  /** Connector object */
	CBRCaseBase _caseBase;  /** CaseBase object */

	NNConfig simConfig;  /** KNN configuration */
	
	public static List<Object> stringIspis = new ArrayList();
	
	public void configure() throws ExecutionException {
		_connector =  new CsvConnector();
		
		_caseBase = new LinealCaseBase();  // Create a Lineal case base for in-memory organization
		
		simConfig = new NNConfig(); // KNN configuration
		simConfig.setDescriptionSimFunction(new Average());  // global similarity function = average
		
		// simConfig.addMapping(new Attribute("attribute", CaseDescription.class), new Interval(5));
		// TODO
		
	
		
//	public static void Dijagnozaa() {	
		ArrayList<String> pol = new ArrayList<String>();
		pol.add("M");
		pol.add("Z");

		TableSimilarity tablesPol = new TableSimilarity(pol);
		tablesPol.setSimilarity("M","Z",.75);
		
		//Umesto tableSimilarity za simptome je koriscena symptomSimilarity:
		/*	ArrayList<String> simptomi = new ArrayList<String>();
		simptomi.add("crvenilo");
		simptomi.add("svrab");
		simptomi.add("kozni_osip");
		simptomi.add("oticanje");
		simptomi.add("mucnina");
		simptomi.add("povracanje");
		simptomi.add("glavobolja");
		simptomi.add("anafilakticki_sok");
		simptomi.add("koprivnjaca");
		simptomi.add("povisena_temperatura");
		simptomi.add("gusenje");
		simptomi.add("svrab_ociju");
		simptomi.add("suzenje_ociju");
		simptomi.add("crvenilo_ociju");
		simptomi.add("kijanje");
		simptomi.add("vodena_sekrecija_iz_nosa");
		simptomi.add("svrab_nosne_sluznice");
		simptomi.add("crvenilo_nosne_sluznice");
		simptomi.add("suvi_kasalj");
		simptomi.add("otezano_disanje");
		simptomi.add("dermatitis");
		simptomi.add("zapusen_nos");
		simptomi.add("kasalj");
		simptomi.add("grebanje_u_grlu");
		simptomi.add("proliv");
		simptomi.add("sviranje_u_grudima");
		simptomi.add("smetnje_sa_gutanjem");
		simptomi.add("plikovi");
		simptomi.add("krastice_na_kozi");
		simptomi.add("perutanje_koze");
		simptomi.add("pojava_urtika");

	
		TableSimilarity tablesSimptomi = new TableSimilarity(simptomi);
		//TO DO Table of similarity za simptome; tipa svrab ociju i crvenilo ociju je slicno, perutanje koze i krastice na kozi ali ne perutanje i povracanje
		//crvenilo
		tablesSimptomi.setSimilarity("crvenilo", "svrab", .2);
		tablesSimptomi.setSimilarity("crvenilo", "kozni_osip", .75);
		tablesSimptomi.setSimilarity("crvenilo", "koprivnjaca", .6);
		tablesSimptomi.setSimilarity("crvenilo", "dermatitis", .6);
		tablesSimptomi.setSimilarity("crvenilo", "plikovi", .3);
		tablesSimptomi.setSimilarity("crvenilo", "krastice_na_kozi",.2);
		tablesSimptomi.setSimilarity("crvenilo", "perutanje_koze",.25);
		tablesSimptomi.setSimilarity("crvenilo", "pojava_urtika",.3);
		tablesSimptomi.setSimilarity("crvenilo", "povisena_temperatura",.1);

		
		//svrab
		tablesSimptomi.setSimilarity("svrab", "kozni_osip", .75);
		tablesSimptomi.setSimilarity("svrab", "koprivnjaca", .6);
		tablesSimptomi.setSimilarity("svrab", "dermatitis", .6);
		tablesSimptomi.setSimilarity("svrab", "plikovi", .3);
		tablesSimptomi.setSimilarity("svrab", "krastice_na_kozi",.2);
		tablesSimptomi.setSimilarity("svrab", "perutanje_koze",.25);
		
		//kozni_osip
		tablesSimptomi.setSimilarity("kozni_osip", "koprivnjaca", .6);
		tablesSimptomi.setSimilarity("kozni_osip", "dermatitis", .6);
		tablesSimptomi.setSimilarity("kozni_osip", "plikovi", .3);
		tablesSimptomi.setSimilarity("kozni_osip", "krastice_na_kozi",.2);
		tablesSimptomi.setSimilarity("kozni_osip", "perutanje_koze",.25);

		//koprivnjaca
		tablesSimptomi.setSimilarity("koprivnjaca", "dermatitis", .6);
		tablesSimptomi.setSimilarity("koprivnjaca", "plikovi", .3);
		tablesSimptomi.setSimilarity("koprivnjaca", "krastice_na_kozi",.2);
		tablesSimptomi.setSimilarity("koprivnjaca", "perutanje_koze",.25);
		
		
		//dermatitis
		tablesSimptomi.setSimilarity("dermatitis", "plikovi", .3);
		tablesSimptomi.setSimilarity("dermatitis", "krastice_na_kozi",.2);
		tablesSimptomi.setSimilarity("dermatitis", "perutanje_koze",.2);
		
		//plikovi
		tablesSimptomi.setSimilarity("plikovi", "krastice_na_kozi",.2);
		tablesSimptomi.setSimilarity("plikovi", "perutanje_koze",.2);
		
		//krastice na kozi:
		tablesSimptomi.setSimilarity("krastice_na_kozi", "perutanje_koze",.25);
		
		//mucnina , povracanje , glavobolja ipovisena_temperatura:
		tablesSimptomi.setSimilarity("mucnina", "povracanje", .9);
		tablesSimptomi.setSimilarity("mucnina","povisena_temperatura",.2);
		tablesSimptomi.setSimilarity("mucnina","glavobolja",.2);
		
		tablesSimptomi.setSimilarity("povracanje","povisena_temperatura",.2);
		tablesSimptomi.setSimilarity("povracanje","glavobolja",.2);
		
		//povisena temperatura:
		tablesSimptomi.setSimilarity("povisena_temperatura","glavobolja",.3);
	
		//glavobolja
		tablesSimptomi.setSimilarity("glavobolja","kijanje",.1);
		tablesSimptomi.setSimilarity("glavobolja","kasalj",.1);
		tablesSimptomi.setSimilarity("glavobolja","otezano_disanje",.1);

		
		//suvi kasalj, kasalj,grebanje u grlu, otezani disanje, smetnje sa disanjem,gusenje:
		tablesSimptomi.setSimilarity("suvi_kasalj", "kasalj", .9);
		tablesSimptomi.setSimilarity("suvi_kasalj", "grebanje_u_grlu", .2);
		tablesSimptomi.setSimilarity("suvi_kasalj", "otezano_disanje", .2);
		tablesSimptomi.setSimilarity("suvi_kasalj", "gusenje", .2);
		tablesSimptomi.setSimilarity("suvi_kasalj", "sviranje_u_grudima",.2);
		tablesSimptomi.setSimilarity("suvi_kasalj", "smetnje_sa_gutanjem",.2);
		
		//kasalj 
		tablesSimptomi.setSimilarity("kasalj", "grebanje_u_grlu", .4);
		tablesSimptomi.setSimilarity("kasalj", "otezano_disanje", .2);
		tablesSimptomi.setSimilarity("kasalj", "gusenje", .2);
		tablesSimptomi.setSimilarity("kasalj", "sviranje_u_grudima",.2);
		tablesSimptomi.setSimilarity("kasalj", "smetnje_sa_gutanjem",.3);
		
		//grebanje u grlu
		tablesSimptomi.setSimilarity("grebanje_u_grlu", "otezano_disanje", .6);
		tablesSimptomi.setSimilarity("grebanje_u_grlu", "gusenje", .2);
		tablesSimptomi.setSimilarity("grebanje_u_grlu", "sviranje_u_grudima",.2);
		tablesSimptomi.setSimilarity("grebanje_u_grlu", "smetnje_sa_gutanjem",.1);
		
		//otezano disanje
		tablesSimptomi.setSimilarity("otezano_disanje", "gusenje", .3);
		tablesSimptomi.setSimilarity("otezano_disanje", "sviranje_u_grudima",.2);
		tablesSimptomi.setSimilarity("otezano_disanje", "smetnje_sa_gutanjem",.6);
		
		//gusenje
		tablesSimptomi.setSimilarity("suvi_kasalj", "sviranje_u_grudima",.5);
		tablesSimptomi.setSimilarity("suvi_kasalj", "smetnje_sa_gutanjem",.2);
		
		//sviranje u grudima
		tablesSimptomi.setSimilarity("sviranje_u_grudima", "smetnje_sa_gutanjem",.2);
		
		//smetnje sa gutanjem:
		
		
		//NOS:
		
		tablesSimptomi.setSimilarity("svrab_ociju","suzenje_ociju",.0);
		tablesSimptomi.setSimilarity("svrab_ociju","crvenilo_ociju",.0);
		tablesSimptomi.setSimilarity("suzenje_ociju","crvenilo_ociju",.0);
		
		//tablesSimptomi.setSimilarity("kijanje","kijanje", .0, 0.2);//?
		tablesSimptomi.setSimilarity("kijanje","vodena_sekrecija_iz_nosa",.0);
		tablesSimptomi.setSimilarity("kijanje","svrab_nosne_sluznice",.0);
		tablesSimptomi.setSimilarity("kijanje","crvenilo_nosne_sluznice",.0);
		tablesSimptomi.setSimilarity("kijanje","zapusen_nos",.0);
		
		tablesSimptomi.setSimilarity("vodena_sekrecija_iz_nosa","svrab_nosne_sluznice",.0);
		tablesSimptomi.setSimilarity("vodena_sekrecija_iz_nosa","crvenilo_nosne_sluznice",.0);
		tablesSimptomi.setSimilarity("vodena_sekrecija_iz_nosa","zapusen_nos",.0);
		
		
		tablesSimptomi.setSimilarity("svrab_nosne_sluznic","crvenilo_nosne_sluznice",.0);
		tablesSimptomi.setSimilarity("svrab_nosne_sluznic","zapusen_nos",.0);
		
		tablesSimptomi.setSimilarity("crvenilo_nosne_sluznice","zapusen_nos",.0);
		
		
		//iste bolesti:
		
		//proliv:
		tablesSimptomi.setSimilarity("proliv", "temperatura", .2);
		
		//anafilaktici_sok:
		
		tablesSimptomi.setSimilarity("anafilakticki_sok", "crvenilo", .2);
		tablesSimptomi.setSimilarity("anafilakticki_sok", "kozni_osip", .2);
		tablesSimptomi.setSimilarity("anafilakticki_sok", "koprivnjaca", .2);
		tablesSimptomi.setSimilarity("anafilakticki_sok", "otok", .6);
		tablesSimptomi.setSimilarity("anafilakticki_sok", "sviranje_u_grudima", .6);
		tablesSimptomi.setSimilarity("anafilakticki_sok", "otežano_disanje", .6);
		tablesSimptomi.setSimilarity("anafilakticki_sok", "kasalj", .6);
		tablesSimptomi.setSimilarity("anafilakticki_sok", "mucnina", .6);
		tablesSimptomi.setSimilarity("anafilakticki_sok", "povracanje", .6);
		tablesSimptomi.setSimilarity("anafilakticki_sok", "kasalj", .6);
		tablesSimptomi.setSimilarity("anafilakticki_sok", "otežano_gutanje", .6);
		*/
		
		//Implementirani intervali pretrage kao i similarity tabela:
	
		Attribute atributGodine = new Attribute("godine", Pregledi.class);
		simConfig.addMapping(atributGodine, new Interval(5));
		simConfig.setWeight(atributGodine, 0.5);		
		
		Attribute atributPol = new Attribute("pol", Pregledi.class);
		simConfig.addMapping(atributPol, tablesPol);
		simConfig.setWeight(atributPol, 0.5);
		
		Attribute atributSimptomi = new Attribute("simptomi", Pregledi.class);
		simConfig.addMapping(atributSimptomi, new SimptomSimilarity()); 
		simConfig.setWeight(atributSimptomi,1.0); 
		
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
		System.out.println("CIKLUS");
		Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(_caseBase.getCases(), query, simConfig);
		eval = SelectCases.selectTopKRR(eval, 3);
		System.out.println("Retrieved cases:");
		for (RetrievalResult nse : eval) {
			System.out.println(nse.get_case().getDescription() + " -> " + nse.getEval());
			//TO DO ispis rezultata u JTextField, a ne u konzolu:
			stringIspis.add(nse.get_case().getDescription()+ " -> " + nse.getEval());
			GUIApp.dijagnozaCBGui.add(Pregledi.dijagnoze);
		}

	}

	public void postCycle() throws ExecutionException {
		
	}

	public CBRCaseBase preCycle() throws ExecutionException {
		GUIApp.dijagnozaCBGui.clear();
		stringIspis.clear();
		_caseBase.init(_connector);
		java.util.Collection<CBRCase> cases = _caseBase.getCases();
		for (CBRCase c: cases)
			System.out.println(c.getDescription());
		    System.out.println("PRECIKLUS");
		return _caseBase;
	}

	public static void RunCaseBased(int godine,String pol,List<String> simpt) {
		simpt.set(0," "+simpt.get(0));
		StandardCBRApplication recommender = new CbrApplication();
		System.out.println("USAO U CBRAPLICATION");
		System.out.println("Proslednjeo u RunCaseBased:");
		System.out.println("Godine:"+godine);
		System.out.println("Pol"+ pol);
		System.out.println("simpt:");
		for (String string : simpt) {
			System.out.println(string);
		}
			
		try {
			recommender.configure();

			recommender.preCycle();

			CBRQuery query = new CBRQuery();
			Pregledi opisPregleda = new Pregledi();

			opisPregleda.setGodine(godine);
			opisPregleda.setPol(pol);
			opisPregleda.setSimptomi(simpt);
			
			query.setDescription(opisPregleda);

			recommender.cycle(query);

			recommender.postCycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}