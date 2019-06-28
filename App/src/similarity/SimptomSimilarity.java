package similarity;

import java.util.List;

import ucm.gaia.jcolibri.exception.NoApplicableSimilarityFunctionException;
import ucm.gaia.jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

public class SimptomSimilarity implements LocalSimilarityFunction {
	
	@Override
	public double compute(Object value1, Object value2) throws NoApplicableSimilarityFunctionException {
		System.out.println("Usao u Simptom Similarity");
	
		List<String> val1 = (List<String>) value1;
		List<String> val2 = (List<String>) value2;
		//Moram da u elemente na nultom indeksu stavim razmak da bi ih mogao porediti sa svim ostalim elementima iz liste...
		val1.set(0, " "+val1.get(0));
		
		
		System.out.println("val1: " +val1.toString());
		System.out.println("val2: " +val2.toString());
		double istih = 0;
	

	//	System.out.println("st in val2" + val2);
		for(String st : val1) {
			if(val2.contains(st)) {
				istih += 1;
			} else {
				istih += 0;
			}
		}
		
		for(String st : val2) {
		//	System.out.println("st in val2" + st);
			if(val1.contains(st)) {
				istih += 1;
			} else {
				istih += 0;
			}
		}
		
		istih = istih/(val1.size()+val2.size());
		System.out.println("istih = " + istih );
		
		return istih;
	}

	@Override
	public boolean isApplicable(Object value1, Object value2) {
		return true;
	}
}