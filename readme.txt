Toy Social Network - Laborator 4 (refactorizare cu interfete functionale si stream-uri)

0. Ce s-a schimbat fata de laboratorul 3
Aplicatia face exact ce facea la laboratorul 3 (aceleasi meniuri, aceleasi
fisiere in data/, acelasi format, aceleasi rezultate). Am refactorizat codul
in stil Java 8: comportamentul se transmite ca parametru (lambda, referinte la
metode), iar buclele clasice pe colectii au fost inlocuite cu stream-uri.
Persistenta in baza de date nu este implementata (ramane pentru laboratorul 5).

1. Cum functioneaza factory-ul si ce trebuie schimbat pentru baza de date
Tipul de persistenta se alege din src/main/resources/config.properties, prin
cheia repository.type (MEMORY, FILE sau DATABASE). Caile fisierelor
(utilizatori.file, prietenii.file) si parametrii bazei de date (db.url,
db.user, db.password) sunt tot acolo, nu in cod.

RepositoryFactory este singurul loc care cunoaste clasele concrete de
repository (Simple Factory). Nu mai foloseste switch: fiecare metoda de creare
are un Map<RepositoryType, Supplier<Repository<...>>> in care fiecarui tip ii
corespunde un Supplier care stie sa creeze repository-ul. Metoda ia Supplier-ul
tipului configurat si apeleaza get(). Map-ul se construieste in interiorul
metodei, pentru ca lambda-urile captureaza validatorul primit ca parametru.
Supplier-ul amana crearea: se creeaza doar repository-ul ales (nu se deschid
fisiere si nu se cer proprietatile bazei de date cand lucrez cu FILE).
Daca un tip din enum nu are Supplier, se arunca RepositoryException cu mesaj
clar. O valoare lipsa sau necunoscuta in config (de exemplu FILEE) da tot un
mesaj clar.

Pentru trecerea la baza de date nu modific Service, UI sau Main. Trebuie doar:
  - repository.type=DATABASE in config.properties (db.url, db.user, db.password
    exista deja in fisier);
  - implementarea metodelor din UtilizatorDbRepository si
    PrietenieDbRepository (acum arunca UnsupportedOperationException cu mesajul
    "Implemented in lab 4"); factory-ul le creeaza deja pentru DATABASE.
La un tip nou de persistenta (de exemplu XML) adaug o constanta in
RepositoryType si o intrare in map.

Simple Factory sau Abstract Factory (OCP)
  Am ales Simple Factory. Avantaje: simplu, o singura clasa; Service, UI si Main
  depind doar de interfata Repository si nu contin new pe repository-uri
  concrete. Dezavantaj: la un tip nou de persistenta trebuie modificata fabrica
  (o intrare noua in cele doua map-uri), deci OCP nu e respectat complet in
  interiorul fabricii (respectat in restul aplicatiei).
  Alternativa este Abstract Factory: o interfata RepositoryFactory cu cate o
  implementare pentru fiecare tip (FileRepositoryFactory, DbRepositoryFactory).
  Avantaje: un tip nou inseamna o clasa noua, fara modificarea codului existent
  (respecta OCP), iar ambele repository-uri vin garantat din aceeasi familie.
  Dezavantaje: mai multe clase; daca se adauga un repository nou in familie, se
  modifica interfata si toate fabricile. Pentru doua repository-uri si trei
  tipuri de persistenta, Simple Factory este suficient.

2. Fisier lipsa si linii corupte
Comportamentul este identic cu cel de la laboratorul 3:
- Fisier inexistent: aplicatia porneste cu repository-ul gol, iar fisierul (si
  folderul data/) se creeaza la prima salvare. Cu Files.lines, un fisier lipsa
  da NoSuchFileException (nu FileNotFoundException), pe care o prind separat,
  inaintea lui IOException.
- Linie goala: este sarita, dar numarata (numarul liniei corespunde cu cel din
  fisier).
- Linie corupta sau invalida: este ignorata si raportata cu numarul liniei si
  motivul (camp lipsa, numar invalid, data invalida, entitate care nu trece de
  validator, id duplicat), apoi incarcarea continua cu linia urmatoare.
- Justificare: o singura linie stricata nu trebuie sa blocheze accesul la
  restul datelor, iar utilizatorul vede exact ce linie trebuie corectata. La id
  duplicat se pastreaza prima aparitie.
- Erorile reale de citire/scriere (IOException, UncheckedIOException) se
  transforma in RepositoryException, cu cauza originala atasata (exception
  chaining).
Fisierul de test cu linii corupte este data/utilizatori_corupt.txt.

3. Interfete functionale folosite si unde
- Validator<T> (interfata proprie, @FunctionalInterface, cu metoda default
  andThen): in Main am scris regula "numele are cel mult 10 caractere" ca
  lambda si am compus-o cu UtilizatorValidator, fara clasa noua si fara sa
  modific UtilizatorValidator. Validatorul compus este injectat in repository.
  andThen aplica validatorul curent, apoi pe celalalt, deci regulile care
  presupun date corecte se pun dupa validarea de baza.
- Predicate<Utilizator>: Repository.findAll(Predicate<E>) este metoda default;
  CriteriiUtilizator are metode statice care intorc Predicate (prenumeleIncepeCu,
  areNumele), combinate cu and si Predicate.not. Service si meniul (optiunile
  9-12) le folosesc, deci nu exista metode de tip findBy... in repository.
- Supplier: map-ul din RepositoryFactory si lambda din orElseThrow.
- Function: map, toMap, Comparator.comparing (de exemplu Utilizator::getId).
- Consumer: forEach in Consola.
- Comparator: Comparator.comparingInt in comunitateSociabila.

Referinte la metode (toate cele patru tipuri):
- statica: CriteriiUtilizator::areNumeScurt (in Predicate.not);
- metoda de instanta a unui obiect anume: System.out::println,
  this::createEntityAsString, repoUtilizatori::findOne;
- metoda de instanta a unui obiect oarecare: Utilizator::getId,
  Prietenie::getIdUtilizator1;
- constructor: ArrayList::new (Collectors.toCollection in Service.comunitati).

4. Bucle inlocuite cu stream-uri
- Service.genereazaId: inainte for + if pentru maxim; acum map, max, orElse.
- Service.stergeUtilizator: inainte doua for (colectare si stergere); acum
  filter, collect, forEach.
- Service.construiesteAdiacenta: inainte for peste prietenii; acum toMap, filter,
  map, collect.
- Service.comunitateSociabila: inainte for-uri imbricate; acum max, map, collect.
- AbstractFileRepository.loadData: inainte BufferedReader + while; acum
  Files.lines, IntStream.range, forEach.
- AbstractFileRepository.writeToFile: inainte for + BufferedWriter; acum map,
  collect, Files.write.

Pipeline-ul din Service.prieteniiLui (exemplu): sursa este
StreamSupport.stream(repoPrietenii.findAll().spliterator(), false) (findAll()
intoarce Iterable, care nu are stream()); filter si map sunt operatii
intermediare (nu fac nimic singure), iar collect este operatia terminala care
porneste executia. Rezultatele se construiesc cu collect, nu prin adaugare
intr-o lista exterioara din forEach. Stream-ul se consuma o singura data.

Bucla pastrata: while (activ) din Consola.run() (meniul interactiv). Nu
parcurge o colectie, ci repeta citirea optiunilor pana la iesire, deci nu are
echivalent in stream-uri. In dfs si drumMaxim (backtracking) am folosit
stream-uri peste vecini, dar cu o multime comuna (vizitati / peDrum) care este
verificata si modificata in ordine; algoritmul se bazeaza pe evaluarea
secventiala a stream-ului (niciodata parallelStream).

5. Template Method sau Function pentru conversia linie <-> entitate
Am pastrat Template Method: AbstractFileRepository face citirea si scrierea in
fisier, iar UtilizatorFileRepository si PrietenieFileRepository implementeaza
doar extractEntity si createEntityAsString. Motive: conversia are un nume, un
loc propriu si Javadoc, deci poate fi documentata si testata separat; codul de
lucru cu fisierul nu se duplica. Alternativa (un singur FileRepository<ID, E>
cu Function<String, E> si Function<E, String> primite in constructor) ar
inlocui mostenirea cu compozitie si ar elimina warning-ul "this-escape" (se
apeleaza loadData() din constructor, care foloseste metode abstracte), dar
conversia ar deveni o lambda anonima in factory.

6. Optional
Repository.findOne ramane cu null (documentat in contract), iar Service il
imprumuta intr-un Optional: gasesteUtilizator(id) foloseste
Optional.ofNullable(repoUtilizatori.findOne(id)).orElseThrow(...) si inlocuieste
verificarile "== null" din adaugaPrietenie si stergeUtilizator. max() din
stream-uri intoarce tot Optional, pe care il desfac cu orElse. Nu apelez get()
fara verificare.
Motiv: trecerea lui findOne la Optional<E> ar modifica interfata si toate
implementarile (memorie, fisier, bloc DB cu schelet), Javadoc-ul si contractul,
fara castig real pentru acest proiect; astfel Optional este folosit unde
rezultatul poate lipsi, fara a schimba contractul repository-ului.

7. Principii de proiectare si sabloane
- Factory (Simple Factory): RepositoryFactory, cu Map de Supplier.
- Template Method: AbstractFileRepository (vezi punctul 5).
- Strategy: Validator<T> este strategia, injectata in repository prin
  constructor; UtilizatorValidator si regula din Main sunt strategii concrete
  (lambda); InMemoryRepository este contextul.
- Decorator: la laboratorul 3 se vedea in BufferedReader/BufferedWriter peste
  FileReader/FileWriter. Dupa refactorizare, citirea si scrierea se fac cu
  Files.lines si Files.write, care ascund bufferizarea, deci nu mai apare
  explicit in cod. Validator.andThen inlantuie doua validatoare intr-unul
  (validatorul rezultat le inconjoara pe ambele), o forma de compunere
  asemanatoare.
- DIP: Service, UI si Main depind de interfata Repository, nu de clase
  concrete; Main nu contine new pe repository-uri concrete.
- OCP: regulile de validare si criteriile de cautare noi se adauga fara sa
  modific codul existent (lambda, andThen, Predicate); tipul nou de persistenta
  cere doar o intrare in map.
- SRP: validare, persistenta, logica de business si interactiunea cu utilizatorul
  sunt in clase diferite.
- LSP: repository-urile pe fisier respecta contractul interfetei Repository (save
  si update intorc null la succes, delete intoarce entitatea stearsa, null ca
  argument da IllegalArgumentException), deci pot inlocui varianta in memorie.
- ISP: findAll(Predicate) este metoda default, deci interfata ramane mica si nu
  creste cu o metoda pentru fiecare criteriu.

8. Unde se face validarea
Validarea se face in repository (InMemoryRepository.save/update, mostenit de
varianta pe fisier), prin validatorul primit in constructor. Motiv: orice cale
prin care intra date (introduse de utilizator sau citite din fisier) trece prin
acelasi punct, deci nu pot ajunge date invalide in fisier. Service face doar
verificarile de business (utilizator existent, prietenie duplicata). Dezavantaj:
validarea este legata de repository; daca ar fi in Service, datele citite din
fisier ar putea ocoli validarea.

9. Demonstratie la predare
1. Adaugari si stergeri de utilizatori si prietenii, inchid aplicatia si o
   repornesc: datele raman in data/.
2. Fisier lipsa (repository gol) si linie corupta introdusa manual
   (data/utilizatori_corupt.txt): linia este raportata cu numarul ei, restul se
   incarca.
3. repository.type=MEMORY (date doar in memorie) si DATABASE
   (UnsupportedOperationException cu mesaj clar).
4. Trei locuri din cod inainte/dupa refactorizare (tabelul de la punctul 4),
   cu explicatia pipeline-ului (operatii intermediare / terminale).
5. O regula noua de validare doar cu lambda (in Main) si un criteriu nou de
   cautare doar cu lambda, fara clase noi.
