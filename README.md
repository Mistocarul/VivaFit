VivaFit 
Backend-ul pentru o aplicatie API care momentan implementeaza sistemul de autentificare si inregistrare, dar si schimbarea informatiilor unui user.
Inregistrarea se poate realiza prin 3 metode: cea clasica(Username + password) sau prin Google sau prin Facebook.
Daca ai contul creat cu metoda clasica de fiecare data cand te conectezi de pe un dispozitiv/adresa IP noua vei primi un cod de confirmare.
La crearea unui cont prin metoda clasica, vom verifica corectitudinea adresei de email prin trimitrea unui cod de confirmare.
Daca ti-ai uitat parola vei primi prin email un link de unde iti poti reseta parola.
In plus, prin implementarea websocket-urilor, daca te conectezi de mai multe ori pe acelasi cont, vei fi delogat de pe celelate file.
Pentru email-uri folosesc SendGrid in care am implementate mai multe template-uri dinamice.
