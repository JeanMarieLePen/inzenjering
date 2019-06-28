package model;

import java.util.ArrayList;
import java.util.List;

import ucm.gaia.jcolibri.cbrcore.Attribute;
import ucm.gaia.jcolibri.cbrcore.CaseComponent;

public class Pregledi implements CaseComponent {

	private int godine;
	private String pol;
	private List<String> simptomi = new ArrayList();
	public  String dijagnoza;
	private String dodatnaIspitivanja;
	private String metodLecenja;
	private String preventivniPregled;
	public  static String dijagnoze;
	
	
	public String getPreventivniPregled() {
		return preventivniPregled;
	}


	public void setPreventivniPregled(String preventivniPregled) {
		this.preventivniPregled = preventivniPregled;
	}


	public int getGodine() {
		return godine;
	}


	public void setGodine(int godine) {
		this.godine = godine;
	}


	public String getPol() {
		return pol;
	}

	public void setPol(String pol) {
		this.pol = pol;
	}


	public String getDijagnoza() {
		return dijagnoza;
	}

	public void setDijagnoza(String dijagnoza) {
		this.dijagnoza = dijagnoza;
	}


	public String getDodatnaIspitivanja() {
		return dodatnaIspitivanja;
	}


	public void setDodatnaIspitivanja(String dodatnaIspitivanja) {
		this.dodatnaIspitivanja = dodatnaIspitivanja;
	}


	public String getMetodLecenja() {
		return metodLecenja;
	}


	public List<String> getSimptomi() {
		return simptomi;
	}


	public void setSimptomi(List<String> simptomi) {
		this.simptomi = simptomi;
	}
	
	public void setSimptomi(String simptom) {
		this.simptomi.add(simptom);
	}


	public void setMetodLecenja(String metodLecenja) {
		this.metodLecenja = metodLecenja;
	}

	/*
	@Override
	public String toString() {
		dijagnoze = dijagnoza;
		return "Pregled [godine=" + godine + ", pol=" + pol + ", simptomi=" + simptomi + ", dijagnoza=" + dijagnoza
				+ ", dodatna ispitivanja=" + dodatnaIspitivanja + ", metoda lecenja =" + metodLecenja + " preventivni pregled =" + preventivniPregled + "]";
	}
	*/
	@Override
	public String toString() {
		dijagnoze = dijagnoza;
		return "alergija na " + dijagnoza;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id",this.getClass());
	}
}
