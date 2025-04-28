import pandas as pd

# Calea către fișierul inițial CSV
cale_fisier = r'C:\Users\Paul\Desktop\calories.csv'

# Citirea fișierului CSV într-un DataFrame
date_calorii = pd.read_csv(cale_fisier)

# Dicționar de codificare pentru coloana 'Gender'
codificari_personalizate = {
    'Gender': {
        'male': 1,
        'female': 2
    }
}

# Aplicarea codificărilor pe coloanele relevante
for coloana, codificare in codificari_personalizate.items():
    if coloana in date_calorii.columns:
        date_calorii[coloana] = date_calorii[coloana].map(codificare)

# Ștergerea coloanelor nedorite din tabel
coloane_de_sters = ['User_ID']
date_calorii.drop(columns=[col for col in coloane_de_sters if col in date_calorii.columns], inplace=True)

# Verificare pentru coloane nenumerice
coloane_nenumerice = date_calorii.select_dtypes(exclude=['number']).columns

if len(coloane_nenumerice) > 0:
    print("Coloane nenumerice găsite:")
    print(coloane_nenumerice)
else:
    print("Toate valorile sunt deja numerice.")

# Conversia eventualelor valori de tip text în valorile numerice corespunzătoare
for coloana in date_calorii.columns:
    if date_calorii[coloana].dtype == 'object':
        date_calorii[coloana] = pd.to_numeric(date_calorii[coloana], errors='coerce')

# Verificare finală pentru coloane nenumerice după conversie
coloane_nenumerice_dupa = date_calorii.select_dtypes(exclude=['number']).columns

if len(coloane_nenumerice_dupa) > 0:
    print("După conversie, coloane nenumerice rămase:")
    print(coloane_nenumerice_dupa)
else:
    print("Toate valorile sunt acum numerice.")

# Verificare pentru coloane duplicate
coloane_duplicate = date_calorii.columns[date_calorii.columns.duplicated()]

if len(coloane_duplicate) > 0:
    print("Coloane duplicate găsite:")
    print(coloane_duplicate.tolist())
    # Eliminăm duplicatele păstrând prima apariție
    date_calorii = date_calorii.loc[:, ~date_calorii.columns.duplicated()]
    print("Coloanele duplicate au fost eliminate.")
else:
    print("Nu există coloane duplicate.")

# Salvarea tabelului prelucrat într-un nou fișier CSV
cale_salveaza = r'C:\Users\Paul\Desktop\calories_finished.csv'
date_calorii.to_csv(cale_salveaza, index=False)

print(f'Fișierul final cu date codificate personalizat a fost salvat la: {cale_salveaza}')
