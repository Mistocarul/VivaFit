<!DOCTYPE html>
<html lang="ro">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VivaFit Backend</title>
</head>
<body>

<h1>VivaFit Backend</h1>

<h2>Descriere</h2>
<p>
    VivaFit este un backend API dezvoltat pentru o aplicație de gestionare a autentificării și înregistrării utilizatorilor.
    Sistemul de autentificare suportă mai multe metode de înregistrare și login, incluzând autentificare clasică
    (username + password) și autentificare prin intermediul conturilor <strong>Google</strong> și <strong>Facebook</strong>.
    API-ul implementează funcții esențiale pentru securizarea și gestionarea conturilor utilizatorilor,
    oferind o experiență sigură și personalizată pentru fiecare utilizator.
</p>

<h2>Funcționalități cheie</h2>
<ul>
    <li><strong>Autentificare și înregistrare:</strong>
        <ul>
            <li>Înregistrare clasică cu nume de utilizator și parolă, cu verificarea adresei de email printr-un cod de confirmare trimis pe email.</li>
            <li>Posibilitatea de înregistrare și autentificare folosind Google și Facebook OAuth.</li>
            <li>Sistem de autentificare în doi pași (2FA) pentru conturile create prin metoda clasică.</li>
        </ul>
    </li>
    <li><strong>Resetare parolă:</strong>
        <p>Dacă un utilizator își uită parola, API-ul trimite un email cu un link de resetare a parolei.</p>
    </li>
    <li><strong>Gestionarea sesiunilor multiple:</strong>
        <p>Implementare de WebSocket-uri pentru a gestiona sesiunile multiple ale utilizatorilor.</p>
    </li>
    <li><strong>Sistem de actualizare a datelor de utilizator:</strong>
        <p>Utilizatorii pot schimba informațiile personale direct din aplicație.</p>
    </li>
    <li><strong>Notificări prin email:</strong>
        <p>Integrare cu SendGrid pentru trimiterea de email-uri de confirmare și resetare a parolei.</p>
    </li>
</ul>

<h2>Tehnologii utilizate</h2>
<ul>
    <li>Java, Spring Boot</li>
    <li>OAuth 2.0 (Google, Facebook), JWT (JSON Web Tokens)</li>
    <li>Parolă hash-ată (BCrypt)</li>
    <li>WebSocket-uri</li>
    <li>SendGrid pentru email-uri</li>
    <li>MySQL</li>
</ul>

<h2>Contribuții și realizări</h2>
<ul>
    <li>Dezvoltat un sistem de autentificare multi-platformă, asigurând securitatea sporită prin verificarea adreselor IP.</li>
    <li>Gestionarea eficientă a sesiunilor multiple cu ajutorul WebSocket-urilor.</li>
    <li>Crearea de template-uri dinamice pentru email-urile de notificare folosind SendGrid.</li>
</ul>

<h2>Rezultate</h2>
<p>Asigurat un sistem securizat și flexibil de autentificare, oferind utilizatorilor o experiență sigură și simplificată de înregistrare și login.</p>

</body>
</html>
