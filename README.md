# Bibliothek Projekt 📚

Eine Bibliothek braucht eine Anwendung für zwei Benutzer: den Bibliothekar, der Bücher an Leser ausleihen oder zurückgeben kann, und den Leser, der Bücher buchen kann, um sie auszuleihen. Jedes Buch hat einen Titel, einen Autor, ein Erscheinungsjahr, eine Liste der Leser, die Bücher ausgeliehen haben, eine anfängliche Anzahl von Exemplaren und eine aktuelle Anzahl von Exemplaren. Autoren, Bibliothekare und Leser sind Personen. Bibliothekare und Leser sind Nutzer der Anwendung. Jede Person hat einen Namen und einen Vornamen. Jeder Autor hat eine ID und eine Liste von Büchern. Jeder Benutzer hat eine ID, einen Benutzernamen und ein Kennwort. Jeder Bibliothekar hat ein Gehalt, ein Anmeldedatum, ein Abmeldedatum und eine Liste der Leser, und jeder Leser hat eine Liste der ausgeliehenen Bücher, eine Gesamtzahl von Büchern, eine Liste der reservierten Bücher und ein Datum für Bücher.
## Die Anwendung ermöglicht:
  - dem Bibliothekar:
    - ein Buch für einen Leser auszuleihen oder zurückzugeben
    - ein Buch/ Autor eines Buches hinzufügen
    - alle Bücher und alle Leser aufsteigend nach Nachnamen sortiert anzuzeigen
    - alle von Lesern ausgeliehenen Bücher anzuzeigen
    - Bücher anzuzeigen, die heute zurückgegeben werden müssen
    - andere Informationen über die Bücher und die Leser zu sehen
  - dem Leser:
    - alle Bücher anzuzeigen und andere Optionen verwenden, um Bücher anders anzuzeigen (sorts and filters)
    - ein Buch zu buchen
    
## Diagramme
  IntelliJ IDEA Diagramm:
  
  ![intdiag](https://user-images.githubusercontent.com/115468048/210518890-2de94966-16f3-47f4-bcaa-f462c06e5652.png)

  
  SQL Server Management Studio Diagramm:
  
  ![sqldiag](https://user-images.githubusercontent.com/115468048/210518943-0790c255-019a-4bd3-a82e-8ae71adc2e63.png)
   
 ## Lessons Learned & Fortschritte während des Semesters
   - wie man die Programmiersprache Java verwendet soll
   - wie Schnittstelle/ Interfaces benutzen werden und wie man CRUD repository implementiert (sowohl In-memory repositories, als auch Database repositories)
   - wie die Beziehungen zwischen Entitäten (Many-to-many, One-to-many etc) funktionieren und wie sie im Code implementiert
   - wie man die Anwendung zu einer SQL Datenbank verbindet
   - wie man Maven und Hibernate benutzt
   - wie man JPA (Java Persistence API) bzw. JPQL (Jakarta Persistence Query Language) verwendet
   - wie Unit tests, custom Exceptions, Javadocs, sorts and filters implementieren werden
   - wie man github vom terminal verwendet (auch wie branches funktionieren)
   
## die ursprüngliche Struktur des Projekts:
![MicrosoftTeams-image](https://user-images.githubusercontent.com/115468048/210528698-ef7f38f5-9874-4c94-87d7-f7ae68213505.png)

