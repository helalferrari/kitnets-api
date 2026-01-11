# SonarQube Command for Kitnet

To run the SonarQube analysis, use:

```bash
./mvnw clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
      -Dsonar.projectKey=kitnet \
      -Dsonar.projectName='kitnet' \
      -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.token=sqp_574d08d359a23505a5954fc002193d6aeb60d50c \
      -Dspring.datasource.url='jdbc:postgresql://localhost:5432/kitnets_db'
```

## Clean Code Practices

### Comments
- **Avoid Redundancy:** Do not add comments that explain what the code already says (e.g., `// Getters and Setters`, `// Constructor`, `// ID Field`).
- **No Noise:** Remove section markers or decorative comments (e.g., `// --- CHANGE HERE ---`, `// Address Fields`).
- **Self-Documenting Code:** Prefer descriptive names for variables and methods over comments.

### Readability & Order
- **Class Structure:** Maintain a logical flow of elements:
    1. Constants / Static fields
    2. Instance fields
    3. Constructors
    4. Lifecycle methods (e.g., `@PrePersist`)
    5. Public methods (Business logic / Actions)
    6. Overridden methods (e.g., `UserDetails` implementation)
    7. Private helper methods
- **Grouping:** Keep fields together and avoid mixing them with methods or internal logic.
- **Helper Methods:** Place helper methods near the code that uses them or at the end of the class.

```