package nl.demaker.demaker.config;

// ============================================================================
// DATAINITIALIZER.JAVA
// Dit bestand wordt automatisch uitgevoerd als Spring Boot start
// Het vult de database met testdata zodat de beoordelaar kan testen
// ============================================================================

// === IMPORTS ===
// Models - dit zijn de Java classes die je database tabellen voorstellen
import nl.demaker.demaker.model.Car;
import nl.demaker.demaker.model.Customer;
import nl.demaker.demaker.model.Deficiency;
import nl.demaker.demaker.model.Inspection;
import nl.demaker.demaker.model.Inspection.InspectionStatus; // <-- Dit is een INNER ENUM (zit IN Inspection.java)
import nl.demaker.demaker.model.Role;
import nl.demaker.demaker.model.User;

// Repositories - dit zijn de classes waarmee je data opslaat/ophaalt uit de database
import nl.demaker.demaker.repository.CarRepository;
import nl.demaker.demaker.repository.CustomerRepository;
import nl.demaker.demaker.repository.DeficiencyRepository;
import nl.demaker.demaker.repository.InspectionRepository;
import nl.demaker.demaker.repository.RoleRepository;
import nl.demaker.demaker.repository.UserRepository;

// Spring imports
import org.springframework.boot.CommandLineRunner;          // Zorgt dat run() wordt aangeroepen bij opstarten
import org.springframework.security.crypto.password.PasswordEncoder; // Voor het hashen van wachtwoorden
import org.springframework.stereotype.Component;            // Maakt dit een Spring Bean
import org.springframework.transaction.annotation.Transactional; // Zorgt voor database transacties

// Java imports
import java.time.LocalDate;  // Voor datums (bijv. 2024-03-15)
import java.util.HashSet;    // Voor een Set (lijst zonder duplicaten)
import java.util.Set;

/**
 * DataInitializer - Vult de database met testdata bij het opstarten
 *
 * WAT DOET DIT BESTAND?
 * - Maakt rollen aan (ADMIN, MONTEUR)
 * - Maakt test users aan (admin, monteur)
 * - Maakt klanten aan
 * - Maakt auto's aan (gekoppeld aan klanten)
 * - Maakt keuringen aan (gekoppeld aan auto's)
 * - Maakt tekortkomingen aan (gekoppeld aan keuringen)
 *
 * WANNEER WORDT DIT UITGEVOERD?
 * - Automatisch bij elke start van de applicatie
 * - Spring Boot roept de run() method aan
 */
@Component  // <-- Dit zorgt dat Spring deze class automatisch aanmaakt
public class DataInitializer implements CommandLineRunner {

    // ========================================================================
    // STAP 1: REPOSITORIES DECLAREREN
    // Dit zijn de "verbindingen" naar je database tabellen
    // Met een repository kun je data opslaan, ophalen, updaten en verwijderen
    // ========================================================================

    private final RoleRepository roleRepository;           // Voor de 'roles' tabel
    private final UserRepository userRepository;           // Voor de 'users' tabel
    private final PasswordEncoder passwordEncoder;         // Voor het hashen van wachtwoorden
    private final CustomerRepository customerRepository;   // Voor de 'customers' tabel
    private final CarRepository carRepository;             // Voor de 'cars' tabel
    private final InspectionRepository inspectionRepository; // Voor de 'inspections' tabel
    private final DeficiencyRepository deficiencyRepository; // Voor de 'deficiencies' tabel

    // ========================================================================
    // CONSTRUCTOR - Spring injecteert hier automatisch alle repositories
    // Dit heet "Dependency Injection" - Spring maakt de objecten voor je aan
    // ========================================================================
    public DataInitializer(RoleRepository roleRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          CustomerRepository customerRepository,
                          CarRepository carRepository,
                          InspectionRepository inspectionRepository,
                          DeficiencyRepository deficiencyRepository) {
        // Sla alle repositories op zodat we ze later kunnen gebruiken
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
        this.carRepository = carRepository;
        this.inspectionRepository = inspectionRepository;
        this.deficiencyRepository = deficiencyRepository;
    }

    // ========================================================================
    // RUN METHOD - Dit wordt automatisch aangeroepen bij het opstarten
    // @Transactional = als er iets fout gaat, wordt alles teruggedraaid
    // ========================================================================
    @Override
    @Transactional
    public void run(String... args) {
        try {
            System.out.println("\n========================================");
            System.out.println("   DATABASE INITIALISATIE GESTART");
            System.out.println("========================================\n");

            // === STAP 1: Rollen aanmaken ===
            // Rollen bepalen wat een user mag doen (autorisatie)
            Role adminRole = initializeRole("ROLE_ADMIN");
            Role monteurRole = initializeRole("ROLE_MONTEUR");

            // === STAP 2: Test users aanmaken ===
            // Admin krijgt BEIDE rollen, monteur alleen MONTEUR rol
            initializeUser("admin", "admin@demaker.nl", "admin123", Set.of(adminRole, monteurRole));
            initializeUser("monteur", "monteur@demaker.nl", "monteur123", Set.of(monteurRole));

            // === STAP 3: Klanten aanmaken ===
            initializeCustomers();

            // === STAP 4: Auto's aanmaken (gekoppeld aan klanten) ===
            initializeCars();

            // === STAP 5: Keuringen aanmaken (gekoppeld aan auto's) ===
            initializeInspections();

            // === STAP 6: Tekortkomingen aanmaken (gekoppeld aan keuringen) ===
            initializeDeficiencies();

            // Print login gegevens naar console
            System.out.println("\n========================================");
            System.out.println("   DATABASE INITIALISATIE VOLTOOID!");
            System.out.println("========================================");
            System.out.println("\n=== LOGIN GEGEVENS ===");
            System.out.println("Admin:   username=admin    password=admin123");
            System.out.println("Monteur: username=monteur  password=monteur123");
            System.out.println("======================\n");

        } catch (Exception e) {
            // Als er iets fout gaat, print de error
            System.err.println("ERROR bij initialiseren data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========================================================================
    // HELPER METHOD: Rol aanmaken
    // Checkt eerst of de rol al bestaat, zo niet -> maak aan
    // ========================================================================
    private Role initializeRole(String roleName) {
        // findByName() zoekt in de database naar een rol met deze naam
        // orElseGet() = als niet gevonden, voer dan de code in de lambda uit
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    // Rol bestaat niet, dus we maken een nieuwe aan
                    Role role = new Role();
                    role.setName(roleName);
                    roleRepository.save(role);  // <-- Slaat op in database (INSERT INTO roles...)
                    System.out.println("+ Rol '" + roleName + "' aangemaakt");
                    return role;
                });
    }

    // ========================================================================
    // HELPER METHOD: User aanmaken
    // Checkt eerst of de user al bestaat, zo niet -> maak aan met gehashed wachtwoord
    // ========================================================================
    private void initializeUser(String username, String email, String password, Set<Role> roles) {
        // Check of user al bestaat
        if (userRepository.findByUsername(username).isEmpty()) {
            // User bestaat niet, maak nieuwe aan
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            // BELANGRIJK: Wachtwoord wordt gehashed! Nooit plaintext opslaan!
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(new HashSet<>(roles));
            userRepository.save(user);  // <-- INSERT INTO users...
            System.out.println("+ User '" + username + "' aangemaakt");
        } else {
            System.out.println("= User '" + username + "' bestaat al");
        }
    }

    // ========================================================================
    // METHOD: Klanten aanmaken
    // Maakt 3 test klanten aan met Nederlandse namen
    // ========================================================================
    private void initializeCustomers() {
        // count() telt hoeveel records er in de tabel zitten
        // Als er 0 zijn, maken we nieuwe aan
        if (customerRepository.count() == 0) {

            // --- Klant 1: Jan de Vries ---
            Customer customer1 = new Customer();
            customer1.setFirstName("Jan");
            customer1.setLastName("de Vries");
            customer1.setEmail("jan.devries@email.nl");
            customer1.setPhoneNumber("0612345678");
            customerRepository.save(customer1);  // <-- INSERT INTO customers...

            // --- Klant 2: Maria Jansen ---
            Customer customer2 = new Customer();
            customer2.setFirstName("Maria");
            customer2.setLastName("Jansen");
            customer2.setEmail("maria.jansen@email.nl");
            customer2.setPhoneNumber("0687654321");
            customerRepository.save(customer2);

            // --- Klant 3: Pieter Bakker ---
            Customer customer3 = new Customer();
            customer3.setFirstName("Pieter");
            customer3.setLastName("Bakker");
            customer3.setEmail("pieter.bakker@email.nl");
            customer3.setPhoneNumber("0611223344");
            customerRepository.save(customer3);

            System.out.println("+ 3 klanten aangemaakt");
        } else {
            System.out.println("= Klanten bestaan al (" + customerRepository.count() + " gevonden)");
        }
    }

    // ========================================================================
    // METHOD: Auto's aanmaken
    // Maakt 4 auto's aan en koppelt ze aan klanten
    // BELANGRIJK: car.setCustomer(jan) maakt de FOREIGN KEY relatie!
    // ========================================================================
    private void initializeCars() {
        if (carRepository.count() == 0) {

            // Eerst moeten we de klanten ophalen uit de database
            // findByEmail() zoekt op email, orElseThrow() geeft error als niet gevonden
            Customer jan = customerRepository.findByEmail("jan.devries@email.nl")
                    .orElseThrow(() -> new RuntimeException("Klant Jan niet gevonden!"));
            Customer maria = customerRepository.findByEmail("maria.jansen@email.nl")
                    .orElseThrow(() -> new RuntimeException("Klant Maria niet gevonden!"));
            Customer pieter = customerRepository.findByEmail("pieter.bakker@email.nl")
                    .orElseThrow(() -> new RuntimeException("Klant Pieter niet gevonden!"));

            // --- Auto 1: Volkswagen Golf van Jan ---
            Car car1 = new Car();
            car1.setLicensePlate("AB-123-CD");
            car1.setBrand("Volkswagen");
            car1.setModel("Golf");
            car1.setYear(2020);
            car1.setCustomer(jan);  // <-- Dit maakt de FOREIGN KEY naar customers tabel!
            carRepository.save(car1);

            // --- Auto 2: Toyota Corolla van Maria ---
            Car car2 = new Car();
            car2.setLicensePlate("EF-456-GH");
            car2.setBrand("Toyota");
            car2.setModel("Corolla");
            car2.setYear(2019);
            car2.setCustomer(maria);
            carRepository.save(car2);

            // --- Auto 3: BMW 3 Serie van Pieter ---
            Car car3 = new Car();
            car3.setLicensePlate("IJ-789-KL");
            car3.setBrand("BMW");
            car3.setModel("3 Serie");
            car3.setYear(2021);
            car3.setCustomer(pieter);
            carRepository.save(car3);

            // --- Auto 4: Audi A4 van Jan (zijn tweede auto) ---
            Car car4 = new Car();
            car4.setLicensePlate("MN-012-OP");
            car4.setBrand("Audi");
            car4.setModel("A4");
            car4.setYear(2018);
            car4.setCustomer(jan);  // <-- Jan heeft nu 2 auto's!
            carRepository.save(car4);

            System.out.println("+ 4 auto's aangemaakt");
        } else {
            System.out.println("= Auto's bestaan al (" + carRepository.count() + " gevonden)");
        }
    }

    // ========================================================================
    // METHOD: Keuringen aanmaken
    // Maakt 3 keuringen aan met verschillende statussen
    // InspectionStatus is een ENUM (vaste waardes: PLANNED, IN_PROGRESS, COMPLETED, CANCELLED)
    // ========================================================================
    private void initializeInspections() {
        if (inspectionRepository.count() == 0) {

            // Haal auto's op via kenteken
            Car golf = carRepository.findByLicensePlate("AB-123-CD")
                    .orElseThrow(() -> new RuntimeException("Auto Golf niet gevonden!"));
            Car corolla = carRepository.findByLicensePlate("EF-456-GH")
                    .orElseThrow(() -> new RuntimeException("Auto Corolla niet gevonden!"));
            Car bmw = carRepository.findByLicensePlate("IJ-789-KL")
                    .orElseThrow(() -> new RuntimeException("Auto BMW niet gevonden!"));

            // --- Keuring 1: Afgeronde keuring voor Golf ---
            Inspection inspection1 = new Inspection();
            inspection1.setPlannedDate(LocalDate.of(2024, 3, 15));  // 15 maart 2024
            inspection1.setStatus(InspectionStatus.COMPLETED);      // Status = afgerond
            inspection1.setCar(golf);                               // Gekoppeld aan Golf
            inspectionRepository.save(inspection1);

            // --- Keuring 2: Geplande keuring voor Corolla ---
            Inspection inspection2 = new Inspection();
            inspection2.setPlannedDate(LocalDate.of(2024, 3, 20));  // 20 maart 2024
            inspection2.setStatus(InspectionStatus.PLANNED);        // Status = gepland
            inspection2.setCar(corolla);
            inspectionRepository.save(inspection2);

            // --- Keuring 3: Keuring in uitvoering voor BMW ---
            Inspection inspection3 = new Inspection();
            inspection3.setPlannedDate(LocalDate.of(2024, 4, 1));   // 1 april 2024
            inspection3.setStatus(InspectionStatus.IN_PROGRESS);    // Status = bezig
            inspection3.setCar(bmw);
            inspectionRepository.save(inspection3);

            System.out.println("+ 3 keuringen aangemaakt");
        } else {
            System.out.println("= Keuringen bestaan al (" + inspectionRepository.count() + " gevonden)");
        }
    }

    // ========================================================================
    // METHOD: Tekortkomingen aanmaken
    // Maakt 4 tekortkomingen aan bij de keuringen
    // Elke tekortkoming heeft: beschrijving, kosten, en of het een veiligheidsrisico is
    // ========================================================================
    private void initializeDeficiencies() {
        if (deficiencyRepository.count() == 0) {

            // Haal auto's op
            Car golf = carRepository.findByLicensePlate("AB-123-CD").orElseThrow();
            Car bmw = carRepository.findByLicensePlate("IJ-789-KL").orElseThrow();

            // Zoek de keuring van de Golf (we gebruiken stream/filter om de juiste te vinden)
            // Dit is zoals een WHERE clause in SQL
            Inspection golfInspection = inspectionRepository.findAll().stream()
                    .filter(i -> i.getCar().equals(golf) &&
                                i.getPlannedDate().equals(LocalDate.of(2024, 3, 15)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Golf keuring niet gevonden!"));

            // Zoek de keuring van de BMW
            Inspection bmwInspection = inspectionRepository.findAll().stream()
                    .filter(i -> i.getCar().equals(bmw) &&
                                i.getPlannedDate().equals(LocalDate.of(2024, 4, 1)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("BMW keuring niet gevonden!"));

            // --- Tekortkoming 1: Remblokken versleten (GEVAARLIJK!) ---
            Deficiency def1 = new Deficiency();
            def1.setDescription("Remblokken versleten");
            def1.setEstimatedCost(150.00);    // Geschatte kosten: 150 euro
            def1.setSafetyRisk(true);         // Dit is een veiligheidsrisico!
            def1.setInspection(golfInspection);
            deficiencyRepository.save(def1);

            // --- Tekortkoming 2: Olielekkage (niet direct gevaarlijk) ---
            Deficiency def2 = new Deficiency();
            def2.setDescription("Olielekkage motor");
            def2.setEstimatedCost(250.00);
            def2.setSafetyRisk(false);        // Geen direct veiligheidsrisico
            def2.setInspection(golfInspection);
            deficiencyRepository.save(def2);

            // --- Tekortkoming 3: Band profiel te laag (GEVAARLIJK!) ---
            Deficiency def3 = new Deficiency();
            def3.setDescription("Band profiel te laag");
            def3.setEstimatedCost(400.00);
            def3.setSafetyRisk(true);
            def3.setInspection(golfInspection);
            deficiencyRepository.save(def3);

            // --- Tekortkoming 4: Uitlaatsysteem beschadigd (BMW) ---
            Deficiency def4 = new Deficiency();
            def4.setDescription("Uitlaatsysteem beschadigd");
            def4.setEstimatedCost(300.00);
            def4.setSafetyRisk(false);
            def4.setInspection(bmwInspection);  // <-- Bij de BMW keuring!
            deficiencyRepository.save(def4);

            System.out.println("+ 4 tekortkomingen aangemaakt");
        } else {
            System.out.println("= Tekortkomingen bestaan al (" + deficiencyRepository.count() + " gevonden)");
        }
    }
}
