package connector;

import java.io.BufferedReader;

import model.Pregledi;
import model.Preporuke;

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

public class CsvConnector2 implements Connector {
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

				Preporuke preporuke = new Preporuke();
				// TODO
				preporuke.setGodine(Integer.parseInt(rs.getString("Godine")));
				preporuke.setPol(rs.getString("Pol"));
				preporuke.setDijagnoza(rs.getString("Dijagnoza"));
				preporuke.setDodatnaIspitivanja(rs.getString("Dodatno_ispitivanje"));
				preporuke.setMetodLecenja(rs.getString("Metod_lecenja"));
				preporuke.setPreventivniPregled(rs.getString("Preventivni_pregled"));
				
				System.out.println("preporuke.getDijagnoza:" + preporuke.getDijagnoza());
				
				cbrCase.setDescription(preporuke);
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