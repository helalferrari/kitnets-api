# Kitnets API 🏠

API RESTful desenvolvida para o gerenciamento de Kitnets, facilitando a conexão entre proprietários (landlords) e inquilinos (tenants). O sistema gerencia autenticação, cadastro e **edição** de imóveis, upload de fotos e buscas personalizadas, incluindo **busca inteligente por IA**.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.12-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring_AI-1.0.0--M4-green?style=for-the-badge&logo=spring&logoColor=white)
![SonarQube](https://img.shields.io/badge/SonarQube-Integration-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

## 🛠️ Tecnologias Utilizadas

Este projeto foi construído utilizando as melhores práticas do ecossistema Java moderno.

### Linguagem e Frameworks
- **Java 21:** Versão LTS mais recente, utilizando recursos modernos da linguagem.
- **Spring Boot 3.4.12:** Framework base para desenvolvimento ágil.
- **Spring AI:** Integração com modelos de Inteligência Artificial (Groq/OpenAI).
- **Spring Data JPA:** Camada de persistência e ORM (Hibernate).
- **Spring Security:** Gestão de autenticação e autorização robusta.

### Bibliotecas e Ferramentas
- **Groq API:** LLM de ultra-baixa latência para processamento de linguagem natural.
- **Auth0 Java JWT:** Implementação de tokens JWT para segurança stateless.
- **Lombok:** Redução de código boilerplate (Getters, Setters, Builders).
- **MariaDB Driver:** Conector para banco de dados relacional.
- **Maven:** Gerenciamento de dependências e build.

### Testes e Qualidade
- **JUnit 5:** Framework de testes unitários.
- **Mockito:** Framework para criação de mocks em testes.
- **JaCoCo:** Ferramenta para análise de cobertura de código (Code Coverage).
- **SonarQube:** Análise contínua de qualidade de código (Code Smells, Bugs, Vulnerabilidades).
- **H2 Database:** Banco de dados em memória para execução rápida de testes.

---

## 📦 Estrutura de Pacotes

O projeto segue uma arquitetura em camadas bem definida para garantir a manutenibilidade e escalabilidade. O domínio foi refatorado para o **Inglês** para seguir padrões internacionais.

```
src/main/java/com/helalferrari/kitnetsapi
├── config/           # Configurações gerais da aplicação (ex: WebConfig, Cors)
├── controller/       # Camada REST (Endpoints da API)
├── dto/              # Data Transfer Objects (Records para entrada/saída de dados)
│   ├── auth/         # DTOs de autenticação (Login, Registro)
│   └── kitnet/       # DTOs relacionados às Kitnets (KitnetRequestDTO, KitnetSearchCriteriaDTO)
├── infra/            # Infraestrutura e configurações transversais
│   ├── security/     # Filtros, Configurações de Segurança e Token Service
│   └── exception/    # Global Exception Handler (Tratamento centralizado de erros)
├── mapper/           # Classes responsáveis por converter Entity <-> DTO
├── model/            # Entidades JPA (Representação das tabelas do banco)
│   └── enums/        # Enumerações (ex: UserRole, Amenity, BathroomType)
├── repository/       # Interfaces de acesso a dados (Spring Data JPA)
│   └── spec/         # Specifications para buscas dinâmicas
└── service/          # Regras de negócio da aplicação (incluindo GroqService)
```

---

## 🚀 Instalação e Uso

### Pré-requisitos
- **Java JDK 21** instalado.
- **Git** instalado.
- **Banco de Dados MariaDB** (ou Docker para subir um container).
- **Chave de API do Groq** (para busca por IA).

### Configuração da API Key (Groq)

Para utilizar a busca por IA, você precisa de uma chave da Groq. Você pode configurá-la de duas formas:

1.  **Variável de Ambiente (Recomendado):**
    ```bash
    export GROQ_API_KEY=sua_chave_aqui
    ```
2.  **Arquivo Local:**
    Crie um arquivo `application-local.yaml` na raiz do projeto com o conteúdo:
    ```yaml
    GROQ_API_KEY: "sua_chave_aqui"
    ```
    (Este arquivo já está no `.gitignore`).

### Massa de Dados

O projeto inclui um arquivo compactado com 5000 registros de exemplo para teste de carga e busca. As kitnets estão vinculadas a usuários com IDs entre **1 e 5**.

**Importante:** Antes de carregar os dados, você deve criar manualmente pelo menos **5 usuários** com o papel de `LANDLORD` (proprietário) através da API, para garantir que as kitnets tenham donos válidos no banco de dados.

1.  **Crie os usuários:** utilize o endpoint `/auth/register` para cadastrar 5 proprietários. Se o seu banco de dados estiver vazio, os IDs gerados serão de 1 a 5.
2.  **Descompacte o arquivo de dados:**
    ```bash
    tar -xzvf src/main/resources/data.tar.gz -C src/main/resources
    ```
    Isso criará o arquivo `src/main/resources/data.sql`.
3.  **Carregue os dados:** Execute a aplicação com o perfil de carga de dados:
    ```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=load-data
    ```

### Execução Padrão

Para rodar a aplicação normalmente (sem recarregar dados):

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

### Busca por IA

Utilize o endpoint `/api/kitnets/search/ai` para fazer buscas em linguagem natural.

Exemplo:
```bash
curl -G "http://localhost:8080/api/kitnets/search/ai" --data-urlencode "query=Procuro um apartamento mobiliado em Florianópolis com vista para o mar e que aceite pets até 2500 reais"
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
