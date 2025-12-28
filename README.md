# Kitnets API 🏠

API RESTful desenvolvida para o gerenciamento de Kitnets, facilitando a conexão entre proprietários (landlords) e inquilinos (tenants). O sistema gerencia autenticação, cadastro de imóveis, upload de fotos e buscas personalizadas.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.12-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

## 🛠️ Tecnologias Utilizadas

Este projeto foi construído utilizando as melhores práticas do ecossistema Java moderno.

### Linguagem e Frameworks
- **Java 21:** Versão LTS mais recente, utilizando recursos modernos da linguagem.
- **Spring Boot 3.4.12:** Framework base para desenvolvimento ágil.
- **Spring Data JPA:** Camada de persistência e ORM (Hibernate).
- **Spring Security:** Gestão de autenticação e autorização robusta.

### Bibliotecas e Ferramentas
- **Auth0 Java JWT:** Implementação de tokens JWT para segurança stateless.
- **Lombok:** Redução de código boilerplate (Getters, Setters, Builders).
- **MariaDB Driver:** Conector para banco de dados relacional.
- **Maven:** Gerenciamento de dependências e build.

### Testes e Qualidade
- **JUnit 5:** Framework de testes unitários.
- **Mockito:** Framework para criação de mocks em testes.
- **JaCoCo:** Ferramenta para análise de cobertura de código (Code Coverage).
- **H2 Database:** Banco de dados em memória para execução rápida de testes.

---

## 📦 Estrutura de Pacotes

O projeto segue uma arquitetura em camadas bem definida para garantir a manutenibilidade e escalabilidade.

```
src/main/java/com/helalferrari/kitnetsapi
├── config/           # Configurações gerais da aplicação (ex: WebConfig, Cors)
├── controller/       # Camada REST (Endpoints da API)
├── dto/              # Data Transfer Objects (Records para entrada/saída de dados)
│   ├── auth/         # DTOs de autenticação (Login, Registro)
│   └── kitnet/       # DTOs relacionados às Kitnets
├── infra/            # Infraestrutura e configurações transversais
│   ├── security/     # Filtros, Configurações de Segurança e Token Service
│   └── exception/    # Global Exception Handler (Tratamento centralizado de erros)
├── mapper/           # Classes responsáveis por converter Entity <-> DTO
├── model/            # Entidades JPA (Representação das tabelas do banco)
│   └── enums/        # Enumerações (ex: UserRole)
├── repository/       # Interfaces de acesso a dados (Spring Data JPA)
└── service/          # Regras de negócio da aplicação
```

---

## 🚀 Instalação e Uso

### Pré-requisitos
- **Java JDK 21** instalado.
- **Git** instalado.
- **Banco de Dados MariaDB** (ou Docker para subir um container).

### Passos para execução

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/helalferrari/kitnets-api.git
   cd kitnets-api
   ```

2. **Configuração do Banco de Dados:**
   Certifique-se de configurar as variáveis de ambiente ou editar o `application.properties` (para dev local) com suas credenciais do banco.
   
   Exemplo de variáveis esperadas:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `API_SECURITY_TOKEN_SECRET` (Segredo para geração do JWT)

3. **Executar a aplicação:**
   Utilize o wrapper do Maven incluído no projeto:
   
   **Linux/Mac:**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   **Windows:**
   ```cmd
   mvnw.cmd spring-boot:run
   ```

4. **Executar Testes:**
   Para rodar a suíte de testes e verificar a cobertura:
   ```bash
   ./mvnw test
   ```

---

## 🤝 Contribuição

Contribuições são bem-vindas! Siga os passos abaixo:

1. Faça um **Fork** do projeto.
2. Crie uma **Branch** para sua feature (`git checkout -b feature/nova-feature`).
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`).
4. Garanta que os testes estão passando (`./mvnw test`).
5. Faça o **Push** para a branch (`git push origin feat/nova-feature`).
6. Abra um **Pull Request**.

---

## 📝 Licença

Este projeto está licenciado sob a licença **MIT**. Consulte o arquivo `LICENSE` para mais detalhes.

A licença MIT permite que você use, copie, modifique, mescle, publique, distribua, sublicencie e/ou venda cópias do Software, desde que o aviso de direitos autorais e o aviso de permissão sejam incluídos em todas as cópias ou partes substanciais do Software.
1. [ ] ](https://github.com/helalferrari/kitnets-api/blob/main/README.md)