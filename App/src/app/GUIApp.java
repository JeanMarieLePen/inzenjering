package app;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.*;
import javax.swing.*;
import java.io.*;

import com.sun.org.apache.xalan.internal.xsltc.runtime.ErrorMessages;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;
import com.ugos.jiprolog.engine.JIPVariable;

import net.proteanit.sql.DbUtils;
import ucm.gaia.jcolibri.cbrcore.CaseComponent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.Panel;
import java.awt.TextArea;

import cbr.CbrApplication;
import cbr.CbrPreporuke;
import java.awt.Color;
public class GUIApp {

private JFrame frame; 
private JTable tablePacijenti;
private static String textSimpotmiAn ="";  //preuzima se vrednost iz polja Anamneza
private static Object[] textIzDijagnoze; //preuzima se vrednost iz polja Dijagnoza
private static String textPregled ="";
private static ArrayList rezultatDijagnoza =new ArrayList(); //smesta se rezultat dobijen prosledjivanjem simptoma u prolog(nakon pristika Dijagnoza) i ispisuje se u polje JListDijagnoza;
private static ArrayList rezultatIspitivanja =new ArrayList();//smesta se rezultat dobijen prosledjivanjem simptoma u prolog(nakon pristika Ispitivanje) i ispisuje se u polje JListIspitivanje;
private static ArrayList rezultatMetodLecenja=new ArrayList();//smesta se rezultat dobijen prosledjivanjem simptoma u konsultujPrologLecenje(nakon pristika MetodLecenja) i ispisuje se u polje JListIspitivanje;
private static String rezultatPregled=" ";  //smesta se rezultat dobijen prosledjivanjem dijagnoze u prolog(nakon pristika lecenje,pregled)


private static int godine;
private static String pol;
public  static ArrayList dijagnozaCBGui = new ArrayList(); //lista prvih n dijagnoza koje CB vrati kao rezultat;
public  static ArrayList preventivniPregledCBGui = new ArrayList(); //lista prvih n  preventivniPregled koje CB vrati kao rezultat;
public  static ArrayList dodatnaIspitivanjaCBGui = new ArrayList(); //lista prvih n dodatnaIspitivanja koje CB vrati kao rezultat;
public  static ArrayList metodLecenjaCBGui = new ArrayList(); //lista prvih n metodLecenja koje CB vrati kao rezultat;

public static String dijagnoza;
public static String dijagnozaProlog;
public static String metodLecenja;
private static ArrayList simpt = new ArrayList();

//Kreiranje konekcije; 
Connection connection = null;
private JTable tablePreglediIP;
private JTextField textFieldAnamneza;
private JList JListDijagnozaProlog; //lista u koju se upisuje rezultat prologa;
private JTextField textFieldID;
private JTextField textFieldIme;
private JTextField textFieldPrezime;
private JTextField textFieldGodine;
private JTextField textFieldPol;
private JTextField textFieldKGrupa;
private JTextField textFieldSearch;
private JComboBox comboBoxSelection;
private JComboBox comboBoxSelectIP;
private JList JListDodatnaIspitivanjaProlog;
private JTextField textFieldDijagnozaIP;
private JTextField textFieldLecenjeIP;
private JTextField textFieldDodIspIP;
private JTextField textFieldDatumIP;
private JTextField textFieldPrezimeIP;
private JTextField textFieldImeIP;
private JTextField textFieldIDIP;
private JTextField textFieldSearchIP;
private JTextField textFieldNewSimptom;
private JTextArea textKomentar;

private JList listSimpotomi;
private JList listPacijentiPregled;
private JComboBox comboBoxListaSimptoma;

private ArrayList<String> odabraniSimptomi = new ArrayList<String>(); //lista u koju se smestaju selektovani simptomi iz liste simptoma;
private ArrayList<String>  odabraniPacijenti = new ArrayList<String>(); //lista u koju se smesta selektovani pacijent  iz liste pacijenata;
//private String odabranaDijagnozaCB; //odabrana dijagnoza iz liste DijagnozaCB; i koja ce se proslediti CbrPerporuke kao dijagnoza;
private int odabranaDijagnozaCBIndex; //index odabrane dijagnoza iz liste DijagnozaCB; i koja ce se proslediti CbrPerporuke kao dijagnoza;
private int odabranaDodatnaIspitivanjaCBIndex;
private int odabranMetodLecCBIndex;
private int odabranPrevPregCBIndex;

private int indexDijagnoze;

private JList listSimptomiTest;
private int odabraniPacijentGodine;
private int odabraniPacijentID;
private String odabraniPacijentPol;
private String prosledjeniSimptomi;
private String prosledjenaDijagnoza;
private JList JListLecenjeProlog;

private JList listDijagnozaCB;
private JList listDodatnaIspitivanjaCB;
private JList listMetodLecenjaCB;
private JList listPrevPregCB;

private JRadioButton JRadioButtonProl;
private JRadioButton JRadioButtonCB;
private JTextField textFieldPrevPregProlog;
private JTextField textFieldMetodLecenja;


	/**
	 * Launch the application.
	 */
	//MAIN:
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
			
					GUIApp window = new GUIApp();	
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}	
	
	/**
	 * Create the application.
	 */
	public GUIApp() {
		connection = sqliteConnection.dbConnector();
		initialize();	
	}
	
	/**
	 * Metoda koja radi automatsko osvezavanje tabele nakon bilo kavih promena u njoj;
	 */
	public void refreshTable() {
		try {
			String query = "Select * from Pacijenti";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			tablePacijenti.setModel(DbUtils.resultSetToTableModel(rs));
		 	
			pst.close();
			rs.close();
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda koja radi automatsko povezivanje i osvezavanje tabele PreglediCB sa tabelom Pregeledi nakon bilo kavih promena u njoj;
	 */
	public void loadPreglediCB() {
		try {
			
			String query3 = "Delete from PreglediCB";
			PreparedStatement pst3 = connection.prepareStatement(query3);
			//JOptionPane.showMessageDialog(null,"Obrisana tabela PreglediCB");
			pst3.execute();
			pst3.close();
			
			//Dobavim sve podatke iz tabele Pregledi: 
			String query1 = "Select * from (Pacijenti inner join Pregledi on Pacijenti.ID_Pacijenta = Pregledi.ID_Pacijenta)";
			PreparedStatement pst1 = connection.prepareStatement(query1);
			ResultSet rs1 = pst1.executeQuery();
			
			/**
			 * Ovde ide sinhronizovanje tabele Pregledi iz prologa sa tabelom pregledi u CB kako bi na osnovu nje generisali podatke;
			 * */
			while(rs1.next()){
				String query2 = "INSERT INTO PreglediCB(Godine,Pol,Simptomi,Dijagnoza,Dodatno_ispitivanje,Metod_lecenja,Preventivni_pregled) VALUES(?,?,?,?,?,?,?)";
				PreparedStatement pst2 = connection.prepareStatement(query2);
				pst2.setString(1, rs1.getString("Godine"));//treba da preuzmeme na osnovu id_pacijetna godine i pol iz tebele pacijenti
				pst2.setString(2, rs1.getString("Pol"));
				pst2.setString(3, rs1.getString("Simptomi")); //ova polja direktno preuzmemem iz tabele pregledi;
				pst2.setString(4, rs1.getString("Dijagnoza"));
				pst2.setString(5, rs1.getString("Dodatno_ispitivanje"));
				pst2.setString(6, rs1.getString("Metod_lecenja"));
				pst2.setString(7, rs1.getString("Preventivni_pregled"));
				
				pst2.execute();	
				pst2.close();
			}
					
			pst1.close();
			rs1.close();

		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			e.printStackTrace();
		}		
	}

	/**
	 * Metoda koja automatski pri pokretanju ucitava ID,Ime i Prezime paicijenta u lisu pacijenti na.
	 */
	public void loadList() {
		try {
			String query = "Select * from Pacijenti";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery(); 

			DefaultListModel DML = new DefaultListModel();
			while(rs.next()) {
				String pacijent = rs.getString("ID_Pacijenta")+" "+rs.getString("Ime")+" "+rs.getString("Prezime");
				DML.addElement(pacijent);
			}
			listPacijentiPregled.setModel(DML);
			pst.close();
			rs.close();
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda koja automatski pri dodavanju novog pregleda azurira prikaz tabele Pregledi.
	 */
	public void refreshTablePregledi() {
		try {
			String query = "Select Pacijenti.ID_Pacijenta, Pacijenti.Ime, Pacijenti.Prezime,Pregledi.Datum_pregleda, Pregledi.Simptomi,Pregledi.Dodatno_ispitivanje,Pregledi.Dijagnoza,Pregledi.Metod_lecenja,Pregledi.Preventivni_pregled,Pregledi.ID_Pregleda from (Pacijenti inner join Pregledi on Pacijenti.ID_Pacijenta = Pregledi.ID_Pacijenta)";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			tablePreglediIP.setModel(DbUtils.resultSetToTableModel(rs));
			//loadPreglediCB();
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null,e);
			e.getStackTrace();
		}
	}
	
	/**
	 * Metoda koja automatski pri pokretanju ucitava Simptome u combobox simptoma.
	 */
	public void loadSymptoms() {
	
		try { //odavde mogu i brisati bukvalno samo prosledim komandu za brisanje!
			String query = "Select * from Simptomi";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			DefaultListModel DML = new DefaultListModel();
			while(rs.next()) {
				DML.addElement(rs.getString("Naziv"));
				
			}
			
			listSimptomiTest.setModel(DML);
			pst.close();
			rs.close();
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			e.printStackTrace();
		}		
	}
	
	/*
	 * Ispis dijagnoze sa procentima pogotka u listu CB Dijagnoza: sluza samo kao prikaz*
	 */
	public void loadDijagnozaCB() {
		
		try { //odavde mogu i brisati bukvalno samo prosledim komandu za brisanje!
			
			DefaultListModel DML = new DefaultListModel();
		
			for (Object object : CbrApplication.stringIspis) {
				DML.addElement(object.toString());
			}	
	
			listDijagnozaCB.setModel(DML);

		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			e.printStackTrace();
		}		
	}
	
	
	/*
	 * Ispis dijagnoze sa procentima pogotka u listu CB Dijagnoza: sluza samo kao prikaz*
	 */
	public void loadDodatnaIspitivanjaCB() {
		
		try { //odavde mogu i brisati bukvalno samo prosledim komandu za brisanje!
			
			DefaultListModel DML = new DefaultListModel();
			//listDijagnozaCB.setText(CbrApplication.stringIspis.toString().substring(1,CbrApplication.stringIspis.toString().length()-1));
			for (Object object : dodatnaIspitivanjaCBGui) {
				DML.addElement(object.toString());
			}					
	
			listDodatnaIspitivanjaCB.setModel(DML);

		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			e.printStackTrace();
		}		
	}
	
	
	public void loadMetodeLecenjaCB() {
		
		try { //odavde mogu i brisati bukvalno samo prosledim komandu za brisanje!
			
			DefaultListModel DML = new DefaultListModel();
			//listDijagnozaCB.setText(CbrApplication.stringIspis.toString().substring(1,CbrApplication.stringIspis.toString().length()-1));
			for (Object object : metodLecenjaCBGui) {
				DML.addElement(object.toString());
			}			
	
			listMetodLecenjaCB.setModel(DML);

		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			e.printStackTrace();
		}		
	}
	
	public void loadPreventivniPregledCB() {
		
		try { //odavde mogu i brisati bukvalno samo prosledim komandu za brisanje!
			
			DefaultListModel DML = new DefaultListModel();
			
			for (Object object : preventivniPregledCBGui) {
				if(object == null) {
					object = "nema";
				}
				DML.addElement(object.toString());
			}			
	
			listPrevPregCB.setModel(DML);

		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			e.printStackTrace();
		}		
	}
		
	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 945, 570);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//za pomeranje dimenzija ekrana prilikom uvecanja.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width;
		int height = screenSize.height;
		
		JTabbedPane tabbedPaneMain = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneMain.setBounds(0, 0, 434, 262);
		tabbedPaneMain.setSize(width,height); //dodato za size
		frame.getContentPane().add(tabbedPaneMain);
										
		ButtonGroup odabirPregleda = new ButtonGroup();
		odabirPregleda.clearSelection(); // da su neselektovani po defaultu;

		
		//PANEL PACIJENT****************************************************************************
		
		JPanel panelPacijent = new JPanel();
		tabbedPaneMain.addTab("Pacijenti", null, panelPacijent, null);
		panelPacijent.setSize(width,height);
		panelPacijent.setLayout(null);
		
		//TABELA PACIJENTI:
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(255, 68, 659, 350);
		panelPacijent.add(scrollPane);
		
		tablePacijenti = new JTable();
		tablePacijenti.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				try {
					int row = tablePacijenti.getSelectedRow(); //vrati vrednost reda na koji sam kliknuo;
					String ID_ = (tablePacijenti.getModel().getValueAt(row , 0)).toString();//sadrzaj preuzet iz tog reda;
					
					String query = "select * from Pacijenti where ID_Pacijenta='"+ID_+"' ";
					PreparedStatement pst = connection.prepareStatement(query);
					
					ResultSet rs=pst.executeQuery();
					
					
					while(rs.next()) {
						textFieldID.setText(rs.getString("ID_Pacijenta"));
						textFieldIme.setText(rs.getString("Ime"));
						textFieldPrezime.setText(rs.getString("Prezime"));
						textFieldGodine.setText(rs.getString("Godine"));
						textFieldPol.setText(rs.getString("Pol"));
						textFieldKGrupa.setText(rs.getString("Krvna_grupa"));
					}
					
					pst.close();
					rs.close();
					
				}catch(Exception e) {
					JOptionPane.showMessageDialog(null,e);
					e.printStackTrace();
				}
			}
				
		});
		scrollPane.setViewportView(tablePacijenti);
		
		
		//JTEXTFILED,JLABEL za ID,IME... i dugme btnUcitajPacijente:
		
		JButton btnUcitajPacijente = new JButton("Ucitaj pacijente");
		btnUcitajPacijente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String query = "Select * from Pacijenti";
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery(); //Izvrsi query i smesti rezulat u rs;
					
					/*rs2xml je jar koji ima klasu DbUtils koja nam pomaze da rezultate 
					za ove upite prevedemo u podatke koje mozemo smestiti u tabelu;*/
					tablePacijenti.setModel(DbUtils.resultSetToTableModel(rs));
				 	
					pst.close();
					rs.close();
				}catch(Exception e) {
					JOptionPane.showMessageDialog(null, e);
					e.printStackTrace();
				}
			}
		});
		btnUcitajPacijente.setBounds(255, 429, 132, 36);
		panelPacijent.add(btnUcitajPacijente);
		
		JLabel lblID = new JLabel("ID");
		lblID.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblID.setBounds(30, 80, 46, 14);
		panelPacijent.add(lblID);
		
		JLabel lblIme = new JLabel("IME");
		lblIme.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblIme.setBounds(30, 120, 46, 14);
		panelPacijent.add(lblIme);
		
		JLabel lblPrezime = new JLabel("PREZIME");
		lblPrezime.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblPrezime.setBounds(30, 160, 65, 14);
		panelPacijent.add(lblPrezime);
		
		JLabel lblGodine = new JLabel("GODINE");
		lblGodine.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblGodine.setBounds(30, 200, 65, 14);
		panelPacijent.add(lblGodine);
		
		JLabel lblPol = new JLabel("POL");
		lblPol.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblPol.setBounds(30, 240, 46, 14);
		panelPacijent.add(lblPol);
		
		JLabel lblKrvGrupa = new JLabel("KRVNA GRUPA");
		lblKrvGrupa.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblKrvGrupa.setBounds(30, 280, 115, 14);
		panelPacijent.add(lblKrvGrupa);
		
		textFieldID = new JTextField();
		textFieldID.setBounds(102, 80, 143, 20);
		panelPacijent.add(textFieldID);
		textFieldID.setColumns(10);
		
		textFieldIme = new JTextField();
		textFieldIme.setColumns(10);
		textFieldIme.setBounds(102, 120, 143, 20);
		panelPacijent.add(textFieldIme);
		
		textFieldPrezime = new JTextField();
		textFieldPrezime.setColumns(10);
		textFieldPrezime.setBounds(102, 160, 143, 20);
		panelPacijent.add(textFieldPrezime);
		
		textFieldGodine = new JTextField();
		textFieldGodine.setColumns(10);
		textFieldGodine.setBounds(102, 200, 143, 20);
		panelPacijent.add(textFieldGodine);
		
		textFieldPol = new JTextField();
		textFieldPol.setColumns(10);
		textFieldPol.setBounds(102, 240, 143, 20);
		panelPacijent.add(textFieldPol);
		
		textFieldKGrupa = new JTextField();
		textFieldKGrupa.setBounds(142, 280, 103, 20);
		panelPacijent.add(textFieldKGrupa);
		textFieldKGrupa.setColumns(10);
		
				
		//IMPLEMENTIRANE FUNKCIONALNOSTI DUGMADI SAVE,UPDATE,DELETE:
		
		//SAVE BUTTON:
		
		JButton btnSave = new JButton("Sacuvaj");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String query = "INSERT INTO Pacijenti(ID_Pacijenta,Ime,Prezime,Godine,Pol,Krvna_grupa) VALUES(?,?,?,?,?,?)";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.setString(1, textFieldID.getText());
					pst.setString(2, textFieldIme.getText());
					pst.setString(3, textFieldPrezime.getText());
					pst.setString(4, textFieldGodine.getText());
					pst.setString(5, textFieldPol.getText());
					pst.setString(6, textFieldKGrupa.getText());
					
					//ResultSet rs = pst.executeQuery(); Jer query ne vraca rezultat;
					pst.execute();
					
					JOptionPane.showMessageDialog(null,"Data Saved");
					
					pst.close();
					//rs.close();
					
				}catch(Exception e) {
					JOptionPane.showMessageDialog(null,"ID pacijenta mora biti jedinstven!");
					e.printStackTrace();
				}
				refreshTable();
				loadList();//osvezava listu pacijenata;
			}
		});
		btnSave.setBounds(30, 328, 89, 23);
		panelPacijent.add(btnSave);
		
		//UPDATE BUTTON:
		
		JButton btnEdit = new JButton("Azuriraj");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String query = " Update Pacijenti set ID_Pacijenta='"+textFieldID.getText()+"', Ime='"+textFieldIme.getText()+"',Prezime='"+textFieldPrezime.getText()+"',Godine='"+textFieldGodine.getText()+"',Pol='"+textFieldPol.getText()+"',Krvna_grupa='"+textFieldKGrupa.getText()+"' "
				+ " where ID_Pacijenta='"+textFieldID.getText()+"'  ";
					PreparedStatement pst = connection.prepareStatement(query);					
					
					pst.execute();
					
					JOptionPane.showMessageDialog(null,"Date updated");
					
					pst.close();

					
				}catch(Exception e) {
					JOptionPane.showMessageDialog(null,e);
					e.printStackTrace();
				}
				refreshTable();
				loadList();//osvezava listu pacijenata;
			}
		});
		btnEdit.setBounds(30, 362, 89, 23);
		panelPacijent.add(btnEdit);
		
		//DELETE BUTTON:
		
		JButton btnDelete = new JButton("Obrisi");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int action = JOptionPane.showConfirmDialog(null,"Do You really Whant To Delete","Delete",JOptionPane.YES_NO_OPTION);
				if(action == 0) {
					try {
						String query = "delete from Pacijenti where ID_Pacijenta = '"+textFieldID.getText() +"' ";
						PreparedStatement pst = connection.prepareStatement(query);		

						pst.execute();
											
						JOptionPane.showMessageDialog(null,"Date deleted");
						
						pst.close();
						
					}catch(Exception e) {
						JOptionPane.showMessageDialog(null,e);
						e.printStackTrace();
					}
				}
				refreshTable();
				loadList();//osvezava listu pacijenata;
			}
		});
		btnDelete.setBounds(30, 407, 89, 23);
		panelPacijent.add(btnDelete);
		
		textFieldSearch = new JTextField();
		textFieldSearch.setToolTipText("Vrednost po kojoj se pretrazuje.");
		textFieldSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {

				try {
					String selection = (String)comboBoxSelection.getSelectedItem();
					String query = "select * from Pacijenti where "+selection+" = ?";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.setString(1, textFieldSearch.getText() );
					ResultSet rs=pst.executeQuery(); //#20 tutorial objasnjava sve 
					
					tablePacijenti.setModel(DbUtils.resultSetToTableModel(rs));
					
					pst.close();
					rs.close();
					
				}catch(Exception e) {
					JOptionPane.showMessageDialog(null,e);
					e.printStackTrace();
				}
			}
		});
		textFieldSearch.setBounds(659, 21, 175, 29);
		panelPacijent.add(textFieldSearch);
		textFieldSearch.setColumns(10);
		
		comboBoxSelection = new JComboBox();
		comboBoxSelection.setModel(new DefaultComboBoxModel(new String[] {"ID_Pacijenta", "Ime", "Prezime", "Godine", "Pol", "Krvna_grupa"}));
		comboBoxSelection.setBounds(452, 21, 150, 29);
		panelPacijent.add(comboBoxSelection);						
		
		JLabel lblNewLabelKritFilt = new JLabel("Kriterijum filtracije:");
		lblNewLabelKritFilt.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabelKritFilt.setBounds(317, 21, 117, 29);
		panelPacijent.add(lblNewLabelKritFilt);
										
													
					//PANEL PREGLED ****************************************************************************
							
					JPanel panelPregled = new JPanel();
					tabbedPaneMain.addTab("Pregledi", null, panelPregled, null);
					panelPregled.setSize(width,height);
					panelPregled.setLayout(null);
					
					JLabel lblUnosSimpotma = new JLabel("Anamneza\\Nalaz");
					lblUnosSimpotma.setFont(new Font("Tahoma", Font.BOLD, 14));
					lblUnosSimpotma.setBounds(530, 12, 141, 28);
					panelPregled.add(lblUnosSimpotma);
					
					JButton btnNewButton = new JButton("Dijagnoza");
					btnNewButton.addActionListener(new ActionListener() {
						@SuppressWarnings("unchecked")
						public void actionPerformed(ActionEvent arg0) {
							try {
								rezultatDijagnoza.clear();
								//Prolog deo:						
								GUIApp.textSimpotmiAn = textFieldAnamneza.getText();  //za test samo jedan simptom prosledjujem;
								JIPQuery query = Prolog.konsultujPrologDijagnoza(GUIApp.textSimpotmiAn,godine,pol);
								JIPTerm solution;
								while((solution=query.nextSolution()) != null){
									//TO DO 
									//Ovde se popuni lista dijagnoza koje prolog vrati kao rezulat:
									for(JIPVariable var: solution.getVariables())
									//rezultatDijagnoza.add(var.getName() + "=" + var.getValue() + "\n");
									rezultatDijagnoza.add(var.getValue());
								}
							//	System.out.println("**************rezultatDijagnoza list: " +  rezultatDijagnoza);
								DefaultListModel DML = new DefaultListModel();
								for (Object dijagnoza : rezultatDijagnoza) {
									System.out.println("values rezultatDijagnoza= " + dijagnoza.toString());	
									DML.addElement(dijagnoza.toString());
									
								}
			
								
								if (pol.equals("Z")) {
									textKomentar.setText("Zbog unetih simptoma kao i sto je pacijent zena i ima "+ godine + " godine. Dijagnoza je sledeca");
									JListDijagnozaProlog.setModel(DML);
								}
								else if(pol.equals("M")) {
									textKomentar.setText("Zbog unetih simptoma kao i sto je pacijent muskarac i ima "+ godine + " godine. Dijagnoza je sledeca");
									JListDijagnozaProlog.setModel(DML);
								}
								

								///CASE-BASED deo:
								simpt.clear();
								//dijagnozaCBGui.clear();
								String simptomi = textFieldAnamneza.getText();
								System.out.println("simptomi CASE BASED: " + simptomi);
							    String[] values = simptomi.toString().split(",");
								for(int i =0;i<values.length;i++) {
									System.out.println("values CB= " + values[i].toString());	
									simpt.add(values[i].toString());
								}
								
								CbrApplication.RunCaseBased(godine,pol,simpt);
								
								@SuppressWarnings("unused")
								java.util.List<Object> pomocna = new ArrayList();
								pomocna = CbrApplication.stringIspis;
								System.out.println("DIjagnoza.GUI: " + dijagnozaCBGui);
						
								/**
								 * TO DO ODRADITI DRUGACIJI ISPIS DIJAGNOZE:
								 */
								loadDijagnozaCB();																																						
								
								
							}catch(Exception e) {
								JOptionPane.showMessageDialog(null,"Morate prvo odabrati i potvrditi simptome!");
								e.getStackTrace();
							}
						}
					});
					btnNewButton.setBounds(801, 99, 102, 25);
					panelPregled.add(btnNewButton);
					
					JScrollPane scrollPaneDijagnoza = new JScrollPane();
					scrollPaneDijagnoza.setBounds(280, 150, 313, 70);
					panelPregled.add(scrollPaneDijagnoza);
					
					
					JListDijagnozaProlog = new JList();
					JListDijagnozaProlog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPaneDijagnoza.setViewportView(JListDijagnozaProlog);
					JListDijagnozaProlog.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent arg0) {
							System.out.println("mouse clicked value : " + JListDijagnozaProlog.getSelectedValue());
							System.out.println("mouse clicked index: " + JListDijagnozaProlog.getSelectedIndex());
							
						}
					});
					JListDijagnozaProlog.setToolTipText("Dijagnoza data na osnovu Prolog metode.");
					
					textKomentar = new JTextArea();
					scrollPaneDijagnoza.setRowHeaderView(textKomentar);
					textKomentar.setWrapStyleWord(true);
					textKomentar.setRows(1);
					textKomentar.setFont(new Font("Monospaced", Font.BOLD, 13));
					textKomentar.setLineWrap(true);
					textKomentar.setEditable(false);
					textKomentar.setColumns(16);
					textKomentar.setBackground(Color.LIGHT_GRAY);
					textKomentar.setForeground(Color.BLACK);
					
					
					//Snimanje pregleda u okviru 
					JButton btnSaveIn = new JButton("Sacuvaj pregled");
					btnSaveIn.addActionListener(new ActionListener() {
						@SuppressWarnings("unused")
						public void actionPerformed(ActionEvent arg0) {
							try {
								if(JRadioButtonProl.isSelected()) {
									
									if (JListDijagnozaProlog.getSelectedValue().equals(null)) {
										JOptionPane.showMessageDialog(null, "Niste odabrali prolog dijagnozu!");
									}
									else if(JListDodatnaIspitivanjaProlog.getSelectedValue().equals(null)) {
										JOptionPane.showMessageDialog(null, "Niste odabrali prolog dodatno ispitivanje!");
									}
									else if(JListLecenjeProlog.getSelectedValue().equals(null)){
										JOptionPane.showMessageDialog(null, "Niste odabrali prolog metod lecenja!");
									}
									else {
										String rezultat;
										String id= String.valueOf(odabraniPacijentID);
								
										
										String query = "INSERT INTO Pregledi(ID_Pacijenta,Datum_pregleda,Simptomi,Dodatno_ispitivanje,Dijagnoza,Metod_lecenja,Preventivni_pregled,ID_Pregleda) VALUES(?,?,?,?,?,?,?,?)";
										PreparedStatement pst2 = connection.prepareStatement(query);
										
										
										//ZA GENERISANJE ID_PREGLEDA:
										String query1 = "Select * from Pregledi";
										PreparedStatement pst3 = connection.prepareStatement(query1);
										ResultSet brojRedova = pst3.executeQuery();
										
										int generatorBroja = 1;
										while(brojRedova.next()) {
											generatorBroja += 1; 
										}

										System.out.println(generatorBroja);
									
										//AUTOMATSKO GENERISANJE DATUMA PREGLEDA:
										DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
										LocalDateTime now = LocalDateTime.now();
										String[] indexIspitivanja = JListDodatnaIspitivanjaProlog.getSelectedValue().toString().split("=");
										System.out.println("INDEX ISPITIVANJA:" + indexIspitivanja[1] );
										System.out.println(dateFormat.format(now));
										
										int odabranoLecenjeIndex = JListLecenjeProlog.getSelectedIndex();
										
										if(JListDodatnaIspitivanjaProlog.getSelectedIndex()%2==0) {
											if(JListLecenjeProlog.getSelectedIndex()%2==0) {
												pst2.setString(1,id);
												pst2.setString(2, dateFormat.format(now));
												pst2.setString(3, odabraniSimptomi.toString()); //da odstrani [ ] iz nize;
												pst2.setString(4, indexIspitivanja[1]);	//Polje za dodatna ispitivanja;
												pst2.setString(5, dijagnozaProlog);
												pst2.setString(6, JListLecenjeProlog.getSelectedValue().toString()); //Polje za preglede;
												pst2.setString(7, textFieldPrevPregProlog.getText()); //Polje za preglede;
												pst2.setString(8, String.valueOf(generatorBroja));
												
												pst2.execute();
												
												JOptionPane.showMessageDialog(null,"Prolog pregled je snimljen");
												
												pst2.close();
											}
											else {
												JOptionPane.showMessageDialog(null,"Ne mozete uneti procente kao metod lecenja!");			
											}
										}
										else {
											JOptionPane.showMessageDialog(null,"Ne mozete uneti procente kao dodatni pregled!");														
											}
									}
								}
								else if(JRadioButtonCB.isSelected()){
									if (listDijagnozaCB.getSelectedValue().equals(null)) {
										JOptionPane.showMessageDialog(null, "Niste odabrali dijagnozu!");
									}
									else if(listDodatnaIspitivanjaCB.getSelectedValue().equals(null)) {
										JOptionPane.showMessageDialog(null, "Niste odabrali dodatno ispitivanje!");
									}
									else if(listMetodLecenjaCB.getSelectedValue().equals(null)){
										JOptionPane.showMessageDialog(null, "Niste odabrali metod lecenja!");
									}
									else if(listPrevPregCB.getSelectedValue().equals(null)){
										JOptionPane.showMessageDialog(null, "Niste odabrali preventivan pregled!");
									}
									else {
											
										
										String rezultat;
										String id= String.valueOf(odabraniPacijentID);
									
										
										String query = "INSERT INTO Pregledi(ID_Pacijenta,Datum_pregleda,Simptomi,Dodatno_ispitivanje,Dijagnoza,Metod_lecenja,Preventivni_pregled,ID_Pregleda) VALUES(?,?,?,?,?,?,?,?)";
										PreparedStatement pst2 = connection.prepareStatement(query);
		   								
										
										//ZA GENERISANJE ID_PREGLEDA:
										String query1 = "Select * from Pregledi";
										PreparedStatement pst3 = connection.prepareStatement(query1);
										ResultSet brojRedova = pst3.executeQuery();
										
										int generatorBroja = 1;
										while(brojRedova.next()) {
											generatorBroja += 1; 
										}

										System.out.println(generatorBroja);
									
										//AUTOMATSKO GENERISANJE DATUMA PREGLEDA:
										DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
										LocalDateTime now = LocalDateTime.now();
											
										System.out.println(dateFormat.format(now));
										
										odabranPrevPregCBIndex = listPrevPregCB.getSelectedIndex();	
										odabranaDodatnaIspitivanjaCBIndex = listPrevPregCB.getSelectedIndex();
										odabranMetodLecCBIndex =  listMetodLecenjaCB.getSelectedIndex();
										pst2.setString(1, id);
										pst2.setString(2, dateFormat.format(now));
										pst2.setString(3, odabraniSimptomi.toString()); //da odstrani [ ] iz nize;
										pst2.setString(4, (String) dodatnaIspitivanjaCBGui.get(odabranaDodatnaIspitivanjaCBIndex));	//Polje za dodatna ispitivanja;
										pst2.setString(5, dijagnoza);
										pst2.setString(6,  (String) metodLecenjaCBGui.get(odabranMetodLecCBIndex)); //Polje za preglede;
										pst2.setString(7, (String) preventivniPregledCBGui.get(odabranPrevPregCBIndex)); //Polje za preglede;
										pst2.setString(8, String.valueOf(generatorBroja));																			
				
										
										pst2.execute();
										
										JOptionPane.showMessageDialog(null,"Case-Based pregled je snimljen");
										
										pst2.close();
									}
								}
								else {
									JOptionPane.showMessageDialog(null,"Nije odabran tip pregleda za snimanje!");
								}
								
								//sinhronizuje se pregledi PreglediCB;
								loadPreglediCB();									
							}catch(Exception e) {
								JOptionPane.showMessageDialog(null,e);
								e.printStackTrace();
							}
							refreshTablePregledi();
						}
					});
					btnSaveIn.setBounds(716, 470, 187, 23);
					panelPregled.add(btnSaveIn);
					
					JScrollPane scrollPaneAnamneza = new JScrollPane();
					scrollPaneAnamneza.setBounds(280, 39, 633, 49);
					panelPregled.add(scrollPaneAnamneza);
					
					textFieldAnamneza = new JTextField();
					scrollPaneAnamneza.setViewportView(textFieldAnamneza);
					textFieldAnamneza.setColumns(10);
					
					JLabel lblDodatnaIspitivanja = new JLabel("Dodatna ispitivanja");
					lblDodatnaIspitivanja.setFont(new Font("Tahoma", Font.BOLD, 12));
					lblDodatnaIspitivanja.setBounds(536, 231, 135, 14);
					panelPregled.add(lblDodatnaIspitivanja);
					
					JScrollPane scrollPaneDodatnaIspitivanjaProlog = new JScrollPane();
					scrollPaneDodatnaIspitivanjaProlog.setBounds(280, 260, 310, 50);
					panelPregled.add(scrollPaneDodatnaIspitivanjaProlog);
					
					JListDodatnaIspitivanjaProlog = new JList();
					scrollPaneDodatnaIspitivanjaProlog.setViewportView(JListDodatnaIspitivanjaProlog);
					JListDodatnaIspitivanjaProlog.setToolTipText("Dodatna ispitivanja predlozena na osnovu Prolog metode.");
				
					
					JLabel lblPacijentZaPregled = new JLabel("Pacijent za pregled");
					lblPacijentZaPregled.setFont(new Font("Tahoma", Font.BOLD, 12));
					lblPacijentZaPregled.setBounds(20, 20, 146, 14);
					panelPregled.add(lblPacijentZaPregled);
					
					JLabel lblListaSimptoma = new JLabel("Lista simptoma");
					lblListaSimptoma.setFont(new Font("Tahoma", Font.BOLD, 12));
					lblListaSimptoma.setBounds(20, 113, 146, 14);
					panelPregled.add(lblListaSimptoma);
					
					textFieldNewSimptom = new JTextField();
					textFieldNewSimptom.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent arg0) {
							textFieldNewSimptom.setText("");
						}
					});
					textFieldNewSimptom.setText("Unesite simptom");
					textFieldNewSimptom.setBounds(20, 369, 146, 28);
					panelPregled.add(textFieldNewSimptom);
					textFieldNewSimptom.setColumns(10);
					
					JLabel lblDodajSimptom = new JLabel("Dodaj simptom u listu");
					lblDodajSimptom.setFont(new Font("Tahoma", Font.BOLD, 12));
					lblDodajSimptom.setBounds(20, 344, 158, 14);
					panelPregled.add(lblDodajSimptom);
					
					JScrollPane scrollPanePacijentiLista = new JScrollPane();
					scrollPanePacijentiLista.setBounds(20, 39, 146, 23);
					panelPregled.add(scrollPanePacijentiLista);
					
					listPacijentiPregled = new JList();
					scrollPanePacijentiLista.setViewportView(listPacijentiPregled);
					
					JButton btnDodajSimptom = new JButton("Dodaj simptom");
					btnDodajSimptom.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							try {
								String query = "INSERT INTO Simptomi(Naziv) VALUES(?)";
								PreparedStatement pst = connection.prepareStatement(query);
							
									pst.setString(1, textFieldNewSimptom.getText());
									
									pst.execute();
									
									JOptionPane.showMessageDialog(null,"Simptom dodat");
									
									pst.close();
								
							}catch(Exception e) {
								JOptionPane.showMessageDialog(null,"Vec postoji takav simptom!");
								e.printStackTrace();
							}
							loadSymptoms();
						}
					});
					btnDodajSimptom.setBounds(20, 414, 135, 23);
					panelPregled.add(btnDodajSimptom);
					
					JScrollPane scrollPaneListaSimptoma = new JScrollPane();
					scrollPaneListaSimptoma.setBounds(20, 133, 146, 146);
					panelPregled.add(scrollPaneListaSimptoma);
					
					listSimptomiTest = new JList();
					scrollPaneListaSimptoma.setViewportView(listSimptomiTest);
					
					JButton btnPotvrdiSimptome = new JButton("Potvrdi simptome");
					btnPotvrdiSimptome.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							
								try {															
									@SuppressWarnings("deprecation")
									Object[] selektovaniSimptomi =  listSimptomiTest.getSelectedValues();
																					
									for(int i=0; i<selektovaniSimptomi.length;i++) {
										System.out.println(selektovaniSimptomi[i]);
										odabraniSimptomi.add(selektovaniSimptomi[i].toString());
									}
									
									textFieldAnamneza.setText(odabraniSimptomi.toString().substring(1, odabraniSimptomi.toString().length()-1));
									
								}catch(Exception e) {
									JOptionPane.showMessageDialog(null,e);
									e.printStackTrace();
								}
						}
					});
					btnPotvrdiSimptome.setBounds(20, 290, 135, 23);
					panelPregled.add(btnPotvrdiSimptome);
					
					JButton btnObrisiSimptome = new JButton("Obrisi anamnezu");
					btnObrisiSimptome.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							odabraniSimptomi.clear();	
							textFieldAnamneza.setText(odabraniSimptomi.toString());
						}
					});
					btnObrisiSimptome.setBounds(280, 99, 146, 25);
					panelPregled.add(btnObrisiSimptome);
					
					JButton btnPotvrdiPacijenta = new JButton("Potvrdi pacijenta");
					btnPotvrdiPacijenta.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							try {
								
								Object []selektovaniPacijent =  listPacijentiPregled.getSelectedValues();

								for(int i=0; i<selektovaniPacijent.length;i++) {
									System.out.println("Selektovani_pacijent: " +selektovaniPacijent[i]);
									odabraniPacijenti.add(selektovaniPacijent[i].toString());
									
								}	
							
									System.out.println(odabraniPacijenti.toString());
						
								String []pomocniNiz = odabraniPacijenti.toString().split(" "); 
								odabraniPacijenti.remove(0);
								
								//pomocniNiz[0] == ID;
								String id_= pomocniNiz[0].substring(1);
								
								//Preuzimamo ID odabranog pacijenta za pregled kako bi ga prosledili u tabelu Pregledi ako se sacuva pregled;
								odabraniPacijentID = Integer.parseInt(id_);
								
								System.out.println("Id :"+id_);
								
								String query = "select * from Pacijenti where ID_Pacijenta = ?";
								PreparedStatement pst = connection.prepareStatement(query);
								pst.setString(1, id_ );
								ResultSet rs=pst.executeQuery();
								
								odabraniPacijentGodine = Integer.parseInt(rs.getString("Godine"));
								godine = odabraniPacijentGodine;
								odabraniPacijentPol =  rs.getString("Pol");
								pol=odabraniPacijentPol ;
								System.out.println("Godine pacijena: " + odabraniPacijentGodine );
								System.out.println("Pol pacijena: " + odabraniPacijentPol );
								
								pst.close();
								rs.close();												
							
								//posle snimanja resetuju se sva polja za nove unose:
								odabraniSimptomi.clear();
								
								System.out.println("Odabrani simtomo: " + odabraniSimptomi.toString());
							}catch(Exception e) {
								JOptionPane.showMessageDialog(null,"Morate selektovati pacijenta");
							
								e.printStackTrace();
							}
							
						}
					});
					btnPotvrdiPacijenta.setBounds(20, 73, 135, 23);
					panelPregled.add(btnPotvrdiPacijenta);
					
					JScrollPane scrollPaneMetodLecenjaProlog = new JScrollPane();
					scrollPaneMetodLecenjaProlog.setBounds(280, 344, 310, 50);
					panelPregled.add(scrollPaneMetodLecenjaProlog);
					
					JListLecenjeProlog = new JList();
					scrollPaneMetodLecenjaProlog.setViewportView(JListLecenjeProlog);
					JListLecenjeProlog.setToolTipText("Preporucen metod lecenja predlozena na osnovu Prolog metode.");
					
					JLabel lblPreporucenMetodLecenja = new JLabel("Preporucen metod lecenja");
					lblPreporucenMetodLecenja.setFont(new Font("Tahoma", Font.BOLD, 12));
					lblPreporucenMetodLecenja.setBounds(530, 320, 174, 14);
					panelPregled.add(lblPreporucenMetodLecenja);
					
					JScrollPane scrollPaneListDijagnozaCB = new JScrollPane();
					scrollPaneListDijagnozaCB.setBounds(603, 150, 311, 70);
					panelPregled.add(scrollPaneListDijagnozaCB);
					
					listDijagnozaCB = new JList();
					scrollPaneListDijagnozaCB.setViewportView(listDijagnozaCB);
					listDijagnozaCB.setToolTipText("Dijagnoza data na osnovu Case-Based metode.");
					
					JScrollPane scrollPanelistDodatnaIspitivanjaCB = new JScrollPane();
					scrollPanelistDodatnaIspitivanjaCB.setBounds(603, 260, 310, 50);
					panelPregled.add(scrollPanelistDodatnaIspitivanjaCB);
					
						
						listDodatnaIspitivanjaCB = new JList();
						scrollPanelistDodatnaIspitivanjaCB.setViewportView(listDodatnaIspitivanjaCB);
						listDodatnaIspitivanjaCB.setToolTipText("Dodatna ispitivanja predlozena na osnovu Case-Based metode.");
					
					JScrollPane scrollPanelistMetodLecenjaCB = new JScrollPane();
					scrollPanelistMetodLecenjaCB.setBounds(603, 344, 311, 50);
					panelPregled.add(scrollPanelistMetodLecenjaCB);
					
					listMetodLecenjaCB = new JList();
					scrollPanelistMetodLecenjaCB.setViewportView(listMetodLecenjaCB);
					listMetodLecenjaCB.setToolTipText("Preporucen metod lecenja predlozena na osnovu Case-Based metode.");
					
					JRadioButtonProl = new JRadioButton("Prolog pregled");
					JRadioButtonProl.setToolTipText("Izaberite rezultat prolog pregleda.");
					JRadioButtonProl.setBounds(420, 474, 109, 23);
					panelPregled.add(JRadioButtonProl);
					
					JRadioButtonCB = new JRadioButton("Case-Based pregled");
					JRadioButtonCB.setToolTipText("Izaberite rezultat Case-Based pregleda.");
					JRadioButtonCB.setBounds(530, 474, 145, 23);
					panelPregled.add(JRadioButtonCB);
					
					//Povezivanje radioButtons;
					odabirPregleda.add(JRadioButtonProl);
					odabirPregleda.add(JRadioButtonCB); 
					
					JLabel lblDijagnoza = new JLabel("Dijagnoza");
					lblDijagnoza.setBounds(530, 113, 109, 17);
					panelPregled.add(lblDijagnoza);
					lblDijagnoza.setFont(new Font("Tahoma", Font.BOLD, 14));
					
					JLabel lblPrologStrana = new JLabel("Prolog");
					lblPrologStrana.setFont(new Font("Tahoma", Font.BOLD, 12));
					lblPrologStrana.setBounds(280, 135, 46, 14);
					panelPregled.add(lblPrologStrana);
					
					JLabel lblCasebasedStrana = new JLabel("Case-Based");
					lblCasebasedStrana.setFont(new Font("Tahoma", Font.BOLD, 12));
					lblCasebasedStrana.setBounds(839, 134, 75, 14);
					panelPregled.add(lblCasebasedStrana);
					
					
					//Kada se klikne na dugem Ispitivanje prosledi se prologu Dijagnoza nakon cega on vrati ispitivanje.
					JButton btnIspitivanja = new JButton("Ispitivanja");
					btnIspitivanja.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							//Prolog deo:

							try {		
								rezultatIspitivanja.clear();
								textIzDijagnoze = JListDijagnozaProlog.getSelectedValues();
								indexDijagnoze = JListDijagnozaProlog.getSelectedIndex();
								
								for(int i=0; i<textIzDijagnoze.length;i++) {
									System.out.println(textIzDijagnoze[i]);
									System.out.println("Index odabrane dijagnoze " + indexDijagnoze);
								
								}

								dijagnozaProlog = rezultatDijagnoza.get(indexDijagnoze).toString();
								
								JIPQuery query = Prolog.konsultujPrologIspitivanja(dijagnozaProlog);
								JIPTerm solution;
								
								
								while((solution=query.nextSolution()) != null){
									//TO DO 
									//Ovde se popuni lista i koje prolog vrati kao rezulat:
									for(JIPVariable var: solution.getVariables())
										rezultatIspitivanja.add(var.getName() + "=" + var.getValue() + "\n");
										//rezultatIspitivanja.add(var.getValue());
								}
								
								System.out.println("rezultatIspitivanja list: " + rezultatIspitivanja);
								DefaultListModel DML = new DefaultListModel();
								for (Object ispitivanje : rezultatIspitivanja) {
									System.out.println("values rezultatIspitivanja= " + ispitivanje.toString());	
									DML.addElement(ispitivanje.toString());
									
								}
								

								JListDodatnaIspitivanjaProlog.setModel(DML);
								
								
							//CASE - BASED kod: preuzima odabranu dijagnozu i prosledjuje je u CbrPreporuke;
								
								Object[] selektovanaDijagnoza =  listDijagnozaCB.getSelectedValues();
								odabranaDijagnozaCBIndex = listDijagnozaCB.getSelectedIndex();									
								for(int i=0; i<selektovanaDijagnoza.length;i++) {
									System.out.println(selektovanaDijagnoza[i]);
									System.out.println("Index odabrane dijagnoze " + odabranaDijagnozaCBIndex);
									//odabranaDijagnozaCB=(String) selektovanaDijagnoza[i];
								}
								dijagnoza =  dijagnozaCBGui.get(odabranaDijagnozaCBIndex).toString();

							
								CbrPreporuke.RunCaseBased(godine, pol, dijagnoza);
		
								loadDodatnaIspitivanjaCB();
			
							}catch(Exception e) {
								JOptionPane.showMessageDialog(null,"Niste selektovali dijagnozu iz Prologa ili Case-Based!");
								
								e.getStackTrace();
							}
						}
					});
					btnIspitivanja.setBounds(801, 229, 102, 20);
					panelPregled.add(btnIspitivanja);
					
					JButton btnLecenje = new JButton("Lecenje");
					btnLecenje.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							//Prolog deo:
							try {							
								rezultatMetodLecenja.clear();
								JIPQuery query = Prolog.konsultujPrologLecenje(dijagnozaProlog);
								JIPTerm solution;
								System.out.println("DIJAGNOZAPROLOGLECENJE: " + dijagnozaProlog);
								
								while((solution=query.nextSolution()) != null){
									 
									//Ovde se popuni lista i koje prolog vrati kao rezulat:
									for(JIPVariable var: solution.getVariables())
										rezultatMetodLecenja.add(var.getValue());
								}
								
								
								
								System.out.println("rezultatMetodLecenja list: " +rezultatMetodLecenja);
								DefaultListModel DML = new DefaultListModel();
								for (Object MetLecenja : rezultatMetodLecenja) {
								//	System.out.println("values rezultatIspitivanja= " + MetLecenja.toString());	
									DML.addElement(MetLecenja.toString());
			
								}
								

								JListLecenjeProlog.setModel(DML);
								
								
								System.out.println("METOD LECENJA: " + metodLecenja);
							
	
								//CASE BASE kod:
								
								Object[] selektovanMetodLecenja =  listMetodLecenjaCB.getSelectedValues();
								odabranMetodLecCBIndex = listMetodLecenjaCB.getSelectedIndex();									
								for(int i=0; i<selektovanMetodLecenja.length;i++) {
									System.out.println(selektovanMetodLecenja[i]);
									System.out.println("Index odabrane metode lecenja: " + odabranMetodLecCBIndex);
									//odabranaDijagnozaCB=(String) selektovanMetodLecenja[i];
								}
								loadMetodeLecenjaCB();
							
								

							}catch(Exception e) {
								JOptionPane.showMessageDialog(null,"Niste selektovali jedan metod lecenja iz Prologa ili Case-Based!");
								//JOptionPane.showMessageDialog(null,e);
								e.getStackTrace();
							}
						}
					});
					btnLecenje.setBounds(801, 315, 102, 23);
					panelPregled.add(btnLecenje);
					
					textFieldPrevPregProlog = new JTextField();
					textFieldPrevPregProlog.setEditable(false);
					textFieldPrevPregProlog.setToolTipText("Preporucen metod lecenja predlozena na osnovu Prolog metode.");
					textFieldPrevPregProlog.setColumns(10);
					textFieldPrevPregProlog.setBounds(280, 425, 310, 40);
					panelPregled.add(textFieldPrevPregProlog);
					
					JScrollPane scrollPanelistPrevPregCB = new JScrollPane();
					scrollPanelistPrevPregCB.setBounds(603, 425, 311, 40);
					panelPregled.add(scrollPanelistPrevPregCB);
					
					listPrevPregCB = new JList();
					scrollPanelistPrevPregCB.setViewportView(listPrevPregCB);
					listPrevPregCB.setToolTipText("Preporucen metod lecenja predlozena na osnovu Prolog metode.");
					
					JLabel lblPreporucenPreventivniPregled = new JLabel("Preventivni pregled");
					lblPreporucenPreventivniPregled.setFont(new Font("Tahoma", Font.BOLD, 12));
					lblPreporucenPreventivniPregled.setBounds(530, 405, 135, 14);
					panelPregled.add(lblPreporucenPreventivniPregled);
					
					JButton btnPrevPregled = new JButton("Pregled");
					btnPrevPregled.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							//Prolog deo:
							try {
								//na osnovu rezultata dijagnoze izbacice preventivni pregled;
								rezultatPregled = "";
					
								JIPQuery query = Prolog.konsultujPrologPrevPreg(dijagnozaProlog,godine,pol);
								JIPTerm solution;
								
								//TO DO: Promeniti rezultat ispisa (verovatno ce se morati menjati)
								while((solution=query.nextSolution()) != null){
									for(JIPVariable var: solution.getVariables())
									rezultatPregled+=var.getValue() +"\n";
									
								}
								
								textFieldPrevPregProlog.setText(rezultatPregled);
						
							 //Case-Based kod:
								
								Object[] selektovanPrevPregled =  listPrevPregCB.getSelectedValues();
								odabranPrevPregCBIndex = listPrevPregCB.getSelectedIndex();									
								for(int i=0; i<selektovanPrevPregled.length;i++) {
									System.out.println(selektovanPrevPregled[i]);
									System.out.println("Index odabranog prev preg: " + odabranPrevPregCBIndex);
								}

								System.out.println("preventivniPregledCBGui " + preventivniPregledCBGui);
								System.out.println(" dodatnaIspitivanjaCBGui " + dodatnaIspitivanjaCBGui);
								System.out.println("metodLecenjaPregledCBGui" + metodLecenjaCBGui);
								
								loadPreventivniPregledCB();
								 
							}catch(Exception e) {
								JOptionPane.showMessageDialog(null,"Morte izabrati jedan preventivan pregled iz Case-Based!");
								e.getStackTrace();
							}
						}
					});
					btnPrevPregled.setBounds(801, 397, 102, 23);
					panelPregled.add(btnPrevPregled);
					
					JButton buttonObrisiSimptom = new JButton("Obrisi simptom");
					buttonObrisiSimptom.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							int action = JOptionPane.showConfirmDialog(null,"Do You really Whant To Delete","Delete",JOptionPane.YES_NO_OPTION);
							if(action == 0) {													
								try {
								
									int odabraniIndex = listSimptomiTest.getSelectedIndex();
									System.out.println("Odabrani indeks:" + odabraniIndex);
									String query = "delete from Simptomi where Naziv = '"+  listSimptomiTest.getSelectedValue()+"' ";
									PreparedStatement pst = connection.prepareStatement(query);		

									pst.execute();
														
									JOptionPane.showMessageDialog(null,"Date deleted");
									
									pst.close();
									
								}catch(Exception e) {
									JOptionPane.showMessageDialog(null,e);
									e.printStackTrace();
								}
							}
							loadSymptoms();

							}
					});
					buttonObrisiSimptom.setBounds(20, 458, 135, 23);
					panelPregled.add(buttonObrisiSimptom);


			//PANEL ISOTRIJA PREGLEDA****************************************************************************
			
			JPanel panelIstorijaPregleda = new JPanel();
			tabbedPaneMain.addTab("Istorija pregleda", null, panelIstorijaPregleda, null);
			panelIstorijaPregleda.setLayout(null);
			
			JScrollPane scrollPaneTableIP = new JScrollPane();
			scrollPaneTableIP.setBounds(257, 61, 657, 324);
			panelIstorijaPregleda.add(scrollPaneTableIP);
			
			JLabel lblIDIp = new JLabel("ID");
			lblIDIp .setFont(new Font("Tahoma", Font.BOLD, 14));
			lblIDIp .setBounds(30, 60, 50, 15);
			panelIstorijaPregleda.add(lblIDIp );
			
			JLabel lblImeIp = new JLabel("IME");
			lblImeIp .setFont(new Font("Tahoma", Font.BOLD, 14));
			lblImeIp .setBounds(30, 100, 50, 15);
			panelIstorijaPregleda.add(lblImeIp );
			
			JLabel lblPrezimeIp = new JLabel("PREZIME");
			lblPrezimeIp .setFont(new Font("Tahoma", Font.BOLD, 14));
			lblPrezimeIp .setBounds(30, 140, 65, 15);
			panelIstorijaPregleda.add(lblPrezimeIp );
			
			
			JLabel lblSimptomi = new JLabel("SIMPTOMI");
			lblSimptomi.setFont(new Font("Tahoma", Font.BOLD, 14));
			lblSimptomi.setBounds(30, 220, 80, 15);
			panelIstorijaPregleda.add(lblSimptomi);

			
			JLabel lblDijagnoza_1 = new JLabel("DIJAGNOZA");
			lblDijagnoza_1.setFont(new Font("Tahoma", Font.BOLD, 14));
			lblDijagnoza_1.setBounds(30, 180, 100, 15);
			panelIstorijaPregleda.add(lblDijagnoza_1);
													

			textFieldDijagnozaIP = new JTextField();
			textFieldDijagnozaIP.setBounds(121, 180, 126, 20);
			panelIstorijaPregleda.add(textFieldDijagnozaIP);
			textFieldDijagnozaIP.setColumns(10);
													
			textFieldIDIP = new JTextField();
			textFieldIDIP.setColumns(10);
			textFieldIDIP.setBounds(121, 60, 126, 20);
			panelIstorijaPregleda.add(textFieldIDIP);
													
			textFieldImeIP = new JTextField();
			textFieldImeIP.setColumns(10);
			textFieldImeIP.setBounds(121, 100, 126, 20);
			panelIstorijaPregleda.add(textFieldImeIP);
			
			textFieldPrezimeIP = new JTextField();
			textFieldPrezimeIP.setColumns(10);
			textFieldPrezimeIP.setBounds(121, 140, 126, 20);
			panelIstorijaPregleda.add(textFieldPrezimeIP);
													
 
			tablePreglediIP = new JTable();
			tablePreglediIP.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					try {
						int row = tablePreglediIP.getSelectedRow(); //vrati vrednost reda na koji sam kliknuo;
						String ID_ = (tablePreglediIP.getModel().getValueAt(row , 0)).toString();//sadrzaj preuzet iz tog reda i 0 kolone (ID_Pacijenta);
						String ID_pregleda = (tablePreglediIP.getModel().getValueAt(row , 9)).toString();//sadrzaj preuzet iz tog reda i 8 kolone (ID_Pregleda);
						
						
						String query = "Select Pacijenti.ID_Pacijenta, Pacijenti.Ime, Pacijenti.Prezime,Pregledi.Datum_pregleda,Pregledi.Simptomi,Pregledi.Dodatno_ispitivanje,Pregledi.Dijagnoza,Pregledi.Metod_lecenja,Pregledi.Preventivni_pregled from (Pacijenti inner join Pregledi on Pacijenti.ID_Pacijenta = Pregledi.ID_Pacijenta) where Pregledi.ID_Pacijenta ='"+ID_+"' AND Pregledi.ID_Pregleda ='"+ID_pregleda+"' ";							
						PreparedStatement pst = connection.prepareStatement(query);
						
						
						ResultSet rs = pst.executeQuery();
						
						while(rs.next()) {
							textFieldIDIP.setText(rs.getString("ID_Pacijenta"));
							textFieldImeIP.setText(rs.getString("Ime"));
							textFieldPrezimeIP.setText(rs.getString("Prezime"));
							textFieldDijagnozaIP.setText(rs.getString("Dijagnoza"));
							textFieldMetodLecenja.setText(rs.getString("Metod_lecenja"));
							//Za listu:
							
							DefaultListModel DLM = new DefaultListModel();
							String bezZagrada =   rs.getString("Simptomi").substring(1,  rs.getString("Simptomi").length()-1);
					        for (String retval: bezZagrada.split(",")) {
					        	DLM.addElement(retval);;
					        }	

					    	listSimpotomi.setModel(DLM);

						}
						
						pst.close();
						rs.close();
						
					}catch(Exception e) {
						JOptionPane.showMessageDialog(null,e);
						e.printStackTrace();
					}
					refreshTable();
				}
			});
			scrollPaneTableIP.setViewportView(tablePreglediIP);
			
			JButton btnUcitajpreglede = new JButton("Ucitaj preglede");
			btnUcitajpreglede.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					 refreshTablePregledi();
				}
			});
			btnUcitajpreglede.setBounds(267, 411, 135, 40);
			panelIstorijaPregleda.add(btnUcitajpreglede);
			
			textFieldSearchIP = new JTextField();
			textFieldSearchIP.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent arg0) {

					try {
						String selection = (String) comboBoxSelectIP.getSelectedItem();
						String query = "Select Pacijenti.ID_Pacijenta, Pacijenti.Ime, Pacijenti.Prezime,Pregledi.Datum_pregleda, Pregledi.Simptomi,Pregledi.Dodatno_ispitivanje,Pregledi.Dijagnoza,Pregledi.Metod_lecenja,Pregledi.Preventivni_pregled,Pregledi.ID_Pregleda from (Pacijenti inner join Pregledi on Pacijenti.ID_Pacijenta = Pregledi.ID_Pacijenta) where Pregledi.'"+selection+"' = ?";
						PreparedStatement pst = connection.prepareStatement(query);
						pst.setString(1, textFieldSearchIP.getText() );
						ResultSet rs=pst.executeQuery(); //#20 tutorial objasnjava sve 
						
						tablePreglediIP.setModel(DbUtils.resultSetToTableModel(rs));
						
						pst.close();
						rs.close();
						
					}catch(Exception e) {
						JOptionPane.showMessageDialog(null,e);
						e.printStackTrace();
					}
					
				}
			});
			textFieldSearchIP.setBounds(695, 31, 161, 20);
			panelIstorijaPregleda.add(textFieldSearchIP);
			textFieldSearchIP.setColumns(10);
													
		    comboBoxSelectIP = new JComboBox();
		    comboBoxSelectIP.setModel(new DefaultComboBoxModel(new String[] {"ID_Pacijenta", "Datum_pregleda"}));
		    comboBoxSelectIP.setBounds(509, 29, 140, 20);
		    panelIstorijaPregleda.add(comboBoxSelectIP);
		    
		    JScrollPane scrollPane_1 = new JScrollPane();
		    scrollPane_1.setBounds(123, 221, 124, 120);
		    panelIstorijaPregleda.add(scrollPane_1);
		    
		    listSimpotomi = new JList();
		    scrollPane_1.setViewportView(listSimpotomi);
		    
		    JLabel lblNewLabelKritFilt2 = new JLabel("Kriterijum filtracije:");
		    lblNewLabelKritFilt2.setFont(new Font("Tahoma", Font.BOLD, 12));
		    lblNewLabelKritFilt2.setBounds(379, 33, 135, 14);
		    panelIstorijaPregleda.add(lblNewLabelKritFilt2);
		    
		    JButton btnObrisiPregled = new JButton("Obrisi pregled");
		    btnObrisiPregled.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent arg0) {
					int action = JOptionPane.showConfirmDialog(null,"Do You really Whant To Delete","Delete",JOptionPane.YES_NO_OPTION);
					if(action == 0) {
						try {
							int row = tablePreglediIP.getSelectedRow(); //vrati vrednost reda na koji sam kliknuo;
						
							String ID_pregleda = (tablePreglediIP.getModel().getValueAt(row , 9)).toString();//sadrzaj
						
							String query = "delete from Pregledi where ID_Pregleda = '"+ ID_pregleda +"' ";
							PreparedStatement pst = connection.prepareStatement(query);		

							pst.execute();
												
							JOptionPane.showMessageDialog(null,"Date deleted");
							
							pst.close();
							
						}catch(Exception e) {
							JOptionPane.showMessageDialog(null,e);
							e.printStackTrace();
						}
					}
					refreshTablePregledi();
					loadPreglediCB();
				//osvezava listu pacijenata;
		    	}
		    });
		    btnObrisiPregled.setBounds(23, 428, 135, 23);
		    panelIstorijaPregleda.add(btnObrisiPregled);
		    
		    JLabel lblMetodLecenja = new JLabel("LECENJE");
		    lblMetodLecenja.setFont(new Font("Tahoma", Font.BOLD, 14));
		    lblMetodLecenja.setBounds(22, 353, 73, 15);
		    panelIstorijaPregleda.add(lblMetodLecenja);
		    
		    textFieldMetodLecenja = new JTextField();
		    textFieldMetodLecenja.setColumns(10);
		    textFieldMetodLecenja.setBounds(121, 352, 126, 20);
		    panelIstorijaPregleda.add(textFieldMetodLecenja);
			
			//ucitavanje liste pacijenata iz tabele;
			loadList();
			loadSymptoms();
	}	
}
