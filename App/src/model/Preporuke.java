package model;

import java.util.ArrayList;
import java.util.List;

import ucm.gaia.jcolibri.cbrcore.Attribute;
import ucm.gaia.jcolibri.cbrcore.CaseComponent;

public class Preporuke implements CaseComponent{
	private int godine;
	private String pol;
	private String dijagnoza;
	private String dodatnaIspitivanja;
	private String metodLecenja;
	private String preventivniPregled;
	
	public static String preventivniPregledCB;
	public static String dodatnaIspitivanjaCB;
	public static String metodLecenjaCB;
	
	
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
	public void setMetodLecenja(String metodLecenja) {
		this.metodLecenja = metodLecenja;
	}
	public String getPreventivniPregled() {
		return preventivniPregled;
	}
	public void setPreventivniPregled(String preventivniPregled) {
		this.preventivniPregled = preventivniPregled;
	}


/*	@Override
	public String toString() {
		return "Pregled [godine=" + godine + ", pol=" + pol + " dijagnoza=" + dijagnoza
				+ ", dodatna ispitivanja=" + dodatnaIspitivanja + ", metoda lecenja =" + metodLecenja + " preventivni pregled =" + preventivniPregled + "]";
	}*/
	
	@Override
	public String toString() {
		preventivniPregledCB = preventivniPregled;
		dodatnaIspitivanjaCB = dodatnaIspitivanja;
		metodLecenjaCB = metodLecenja;
		
		return "dodatna ispitivanja=" + dodatnaIspitivanja + ", metoda lecenja =" + metodLecenja + " preventivni pregled =" + preventivniPregled;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("id",this.getClass());
	}
}
