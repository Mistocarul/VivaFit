# Importuri
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import mean_squared_error, r2_score
from tensorflow.keras.models import Sequential, load_model
from tensorflow.keras.layers import Dense, Dropout
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.callbacks import EarlyStopping
import joblib

# Citirea datelor
cale_fisier = r'C:\Users\Paul\Desktop\calories_finished.csv'
tabel_date = pd.read_csv(cale_fisier)

# Separare caracteristici și țintă
caracteristici = tabel_date.drop(columns=['Calories'])
calorii_tinta = tabel_date['Calories']

# Împărțire 90% antrenare, 10% test
caracteristici_antrenare, caracteristici_test, calorii_antrenare, calorii_test = train_test_split(
    caracteristici, calorii_tinta, test_size=0.1, random_state=42
)

# Standardizare
scaler_calorii = StandardScaler()
caracteristici_antrenare_std = scaler_calorii.fit_transform(caracteristici_antrenare)
caracteristici_test_std = scaler_calorii.transform(caracteristici_test)

# Salvare medii și deviații standard
medii = scaler_calorii.mean_
deviatii_std = scaler_calorii.scale_

scaler_date = pd.DataFrame({
    'medie': medii,
    'deviatie_standard': deviatii_std
}, index=caracteristici.columns)

scaler_date.to_csv('scaler_calorii.csv', index=True)

# Definire model rețea neuronală
model_calorii = Sequential()
model_calorii.add(Dense(512, input_dim=caracteristici_antrenare_std.shape[1], activation='relu'))
model_calorii.add(Dropout(0.1))
model_calorii.add(Dense(256, activation='relu'))
model_calorii.add(Dropout(0.1))
model_calorii.add(Dense(128, activation='relu'))
model_calorii.add(Dense(1))

model_calorii.compile(optimizer=Adam(learning_rate=0.001), loss='mean_squared_error')

# Early stopping pe set validare
oprire_devreme = EarlyStopping(monitor='val_loss', patience=20, restore_best_weights=True)

# Antrenare model pe 90%
istoric_antrenare = model_calorii.fit(
    caracteristici_antrenare_std, calorii_antrenare,
    epochs=200, batch_size=64,
    validation_data=(caracteristici_test_std, calorii_test),
    verbose=1,
    callbacks=[oprire_devreme]
)

# Evaluare pe setul de test
predictii_test = model_calorii.predict(caracteristici_test_std)

mse_test = mean_squared_error(calorii_test, predictii_test)
r2_test = r2_score(calorii_test, predictii_test)

print(f"MSE pe setul de test (90/10): {mse_test:.2f}")
print(f"R² pe setul de test (90/10): {r2_test:.4f}")

# Salvare model și scaler
model_calorii.save('model_calorii.h5')
joblib.dump(scaler_calorii, 'scaler_calorii.pkl')


# Funcție de predicție
def prezicere_calorii(input_utilizator):
    try:
        model_incarcat = load_model('model_calorii.h5')
        scaler_incarcat = joblib.load('scaler_calorii.pkl')
        input_std = scaler_incarcat.transform([input_utilizator])
        predictie = model_incarcat.predict(input_std)
        return predictie[0][0]
    except Exception as eroare:
        print(f"Eroare la predicție: {eroare}")
        return None

# Exemplu de predicție
input_exemplu = [
    1,    # Sex: 1=bărbat, 2=femeie
    22,   # Vârstă
    178,  # Înălțime (cm)
    120,  # Greutate (kg)
    45,   # Durată activitate (min)
    125,  # Puls mediu
    39    # Temperatură corp
]

calorii_previzionate = prezicere_calorii(input_exemplu)
if calorii_previzionate is not None:
    print(f"Predicție calorii pentru exemplu: {calorii_previzionate:.2f}")
