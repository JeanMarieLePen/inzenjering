
diagnosis([steroids,antihistamine,petroleum_jelly,moisturizers,crisaborole,dupilumab],[eczema]).
diagnosis([epinephrine,antihistamnines,corticosteroids],[egg_allergy,fish_allergy,wheat_allergy,soy_allergy,shellfish_allergy]).
diagnosis([epinephrine,antihistamine],[milk_allergy,peanut_allergy,treenut_allergy]).
diagnosis([antihistamine_eyedrops, mast_cell_stabilizer_eyedrops, nonsteroidal_anti_inflammatory_drugs, corticosteroid_eyedrops, nonsedating_oral_antihistamines, immunotherapy],[eye_allergy]).
diagnosis([antihistamine, ibuprofen, aspirin, corticosteroids], [drug_allergy]).
diagnosis([nasal_decongestant_sprays, antihistamines, topical_nasal_corticosteroids, nasal_saline_washes, surgery], [sinus_infection]).
diagnosis([intranasal_corticosteroids,antihistamines,decongestants,nasal_sprays,leukatriene_pathway_inhibitors,immunotherapy],[allergic_rhinitis]).
diagnosis([epinephrine],[latex_allergy]). 


sadrzi(S,[]).
sadrzi(S,[H|T]) :- member(H,S), sadrzi(S,T).

%odredjivanje terapije na osnovu postavljenih dijagnoza(lista)

recommended_therapy(S,T) :- diagnosis(T,S2), sadrzi(S2,S).

