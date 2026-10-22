Toy Social Network - Lab 3
==========================

1. Despre proiect
-----------------
Aplicatie de retea sociala cu utilizatori si prietenii, scrisa in Java, cu
arhitectura pe straturi: UI (Consola), Service, Repository, domain si
validators. In acest laborator am adaugat persistenta in fisiere, o fabrica
pentru crearea repository-urilor si exceptii proprii.

2. Cum schimb sursa de date
---------------------------
Sursa de date se alege din fisierul src/main/resources/config.properties,
prin cheia repository.type. Valorile posibile sunt:
  - FILE      : datele sunt salvate in fisiere text (folderul data/)
  - MEMORY    : datele sunt tinute doar in memorie si se pierd la inchidere
  - DATABASE  : pregatit pentru o baza de date, dar inca neimplementat;
                aplicatia afiseaza un mesaj clar ca optiunea nu e disponibila

Clasa RepositoryFactory citeste valoarea si construieste repository-ul
potrivit. Main nu stie ce implementare primeste, deci pentru a schimba sursa
de date modific doar config.properties, fara sa recompilez codul.
Daca cheia lipseste sau are o valoare gresita (de exemplu FILEE), aplicatia
da un mesaj de eroare clar.

3. Fisiere lipsa sau corupte
----------------------------
- Fisier lipsa: aplicatia porneste cu repository-ul gol. Fisierul (si
  folderul data/, daca nu exista) se creeaza la prima salvare.
- Linie corupta: o ignor si afisez un mesaj cu numarul liniei, dupa care
  continui cu restul fisierului. Sunt tratate: campuri lipsa, numar invalid,
  data invalida, id duplicat si entitati care nu trec de validare.
  Liniile goale sunt sarite fara mesaj.
- Justificare: am ales sa ignor si sa raportez, nu sa opresc aplicatia,
  pentru ca o singura linie stricata nu trebuie sa blocheze accesul la
  restul datelor. Utilizatorul vede exact ce linie e problema si o poate
  corecta. Erorile de citire/scriere propriu-zise (IOException) sunt
  transformate in RepositoryException.

4. Pattern-uri folosite
-----------------------
- Factory (Simple Factory): RepositoryFactory creeaza repository-ul in
  functie de configurare.
- Template Method: AbstractFileRepository implementeaza incarcarea si
  scrierea in fisier, iar subclasele (UtilizatorFileRepository,
  PrietenieFileRepository) implementeaza doar extractEntity si
  createEntityAsString.
- Strategy: Validator<T> este primit de repository prin constructor, deci
  regulile de validare pot fi schimbate fara sa modific repository-ul.
- Decorator: BufferedReader/BufferedWriter se pun peste
  FileReader/FileWriter pentru citire si scriere pe linii.

5. Principii SOLID
------------------
- DIP: Service depinde de interfata Repository, nu de o clasa concreta.
- OCP: pot adauga un tip nou de repository (de exemplu pentru baza de date)
  fara sa modific Service sau UI; modific doar fabrica.
- SRP: fiecare clasa are un singur rol (validare, persistenta, logica de
  business, interactiune cu utilizatorul).
- LSP: repository-urile pe fisier respecta contractul interfetei (save si
  update intorc null la succes, delete intoarce entitatea stearsa), deci
  pot inlocui varianta in memorie.

Diferenta fata de Abstract Factory: eu folosesc un Simple Factory, o singura
metoda care creeaza un singur tip de obiect (repository). Abstract Factory
ar crea o familie intreaga de obiecte care merg impreuna (de exemplu
repository-uri + conexiune + alte componente pentru fiecare tip de stocare).

6. Unde se face validarea
-------------------------
Validarea se face in repository, prin validatorul primit in constructor,
atat la adaugarea de la tastatura, cat si la citirea din fisier. Erorile
ajung la utilizator sub forma de exceptii proprii (ValidationException,
ServiceException, RepositoryException), tratate in Consola.
