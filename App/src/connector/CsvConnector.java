package connector;

import java.io.BufferedReader;

import model.Pregledi;

import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.DefaultListModel;

import app.sqliteConnection;
import model.Pregledi;
import ucm.gaia.jcolibri.cbrcore.CBRCase;
import ucm.gaia.jcolibri.cbrcore.CaseBaseFilter;
import ucm.gaia.jcolibri.cbrcore.Connector;
import ucm.gaia.jcolibri.exception.InitializingException;
import ucm.gaia.jcolibri.util.FileIO;

public class CsvConnector implements Connector {
	Connection connection = sqliteConnection.dbConnector2();
	
	@Override
	public Collection<CBRCase> retrieveAllCases() {
		LinkedList<CBRCase> cases = new LinkedList<CBRCase>();
		
		try {
			String query = "Select * from PreglediCB";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();			
			
			while (rs.next()) {	

				CBRCase cbrCase = new CBRCase();

				Pregledi pregledi = new Pregledi();
				// TODO
				pregledi.setGodine(Integer.parseInt(rs.getString("Godine")));
				pregledi.setPol(rs.getString("Pol"));
				 
				String simptomi = rs.getString("Simptomi");
			    String[] values = simptomi.toString().substring(1,simptomi.length()-1).split(",");
				for(int i =0;i<values.length;i++) {
				//	System.out.println("values = " + values[i].toString());	
					pregledi.setSimptomi(values[i].toString());
				}
				pregledi.setDijagnoza(rs.getString("Dijagnoza"));
				pregledi.setDodatnaIspitivanja(rs.getString("Dodatno_ispitivanje"));
				pregledi.setMetodLecenja(rs.getString("Metod_lecenja"));
				pregledi.setPreventivniPregled(rs.getString("Preventivni_pregled"));
				
				System.out.println("pregledi.getSimptomi:" + pregledi.getSimptomi());
				System.out.println("pregledi.getDijagnoza:" + pregledi.getDijagnoza());
				
				cbrCase.setDescription(pregledi);
				cases.add(cbrCase);
			}
			pst.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cases;
	}

	@Override
	public Collection<CBRCase> retrieveSomeCases(CaseBaseFilter arg0) {
		return null;
	}

	@Override
	public void storeCases(Collection<CBRCase> arg0) {
	}
	
	@Override
	public void close() {
	}

	@Override
	public void deleteCases(Collection<CBRCase> arg0) {
	}

	@Override
	public void initFromXMLfile(URL arg0) throws InitializingException {
	}

}