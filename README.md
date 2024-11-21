
# **Document Management System (DMS)**

Este projeto é um **sistema de gerenciamento de documentos criptografados** que permite o upload e o download seguro de arquivos. O sistema criptografa os arquivos durante o upload e permite o download usando uma chave de criptografia, garantindo que apenas usuários autorizados possam acessar o conteúdo.

## **Tecnologias Utilizadas**

- **Spring Boot**: Framework para construir aplicações Java baseadas em microserviços.
- **JPA/Hibernate**: Para persistência de dados.
- **AES (Advanced Encryption Standard)**: Para criptografar os arquivos de forma segura.
- **Base64**: Codificação de chave e arquivos.
- **MySQL**: Banco de dados relacional para armazenar os metadados dos arquivos.
- **Maven**: Gerenciamento de dependências e construção do projeto.

## **Funcionalidades**

- **Upload de arquivos criptografados**: O usuário pode enviar arquivos para o sistema, que serão criptografados com uma chave gerada de forma aleatória e segura.
- **Download de arquivos criptografados**: O usuário pode baixar arquivos utilizando uma chave de criptografia válida.
- **Validação de chave**: O sistema valida a chave fornecida durante o download para garantir que o usuário tem permissão para acessar o arquivo.
- **Expiração de chave**: Cada chave de criptografia possui um tempo de validade, após o qual o arquivo não pode mais ser acessado.
  
## **Estrutura do Projeto**

### **Controller**
A camada de Controller é responsável por fornecer os endpoints da API para a comunicação com o cliente.

#### `FileController.java`

- **GET `/upload`**: Endpoint que permite o upload de arquivos criptografados.
- **GET `/{randomIdentification}`**: Endpoint para o download de arquivos criptografados, onde a chave de criptografia deve ser fornecida.

### **Service**
A camada de Service implementa a lógica de negócios, incluindo a criptografia e a validação de arquivos.

#### `FileService.java`

- **processUpload**: Método que recebe um arquivo e o nome, valida as informações e criptografa o conteúdo antes de salvar.
- **processDownloadAndGenerateHeaders**: Método que recupera um arquivo, valida a chave e descriptografa o conteúdo.
- **generateKey**: Método que gera uma chave AES aleatória para criptografia.
- **encrypt**: Método para criptografar os dados do arquivo.
- **decrypt**: Método para descriptografar os dados do arquivo.

### **Model**
A camada Model define as entidades que são persistidas no banco de dados.

#### `EncryptedFile.java`

Representa um arquivo criptografado armazenado no banco de dados. Contém informações como:

- **randomIdentification**: Identificador único do arquivo.
- **fileName**: Nome do arquivo.
- **encryptedContent**: Conteúdo criptografado do arquivo.
- **encryptionKey**: Chave usada para criptografar o arquivo.
- **keyExpiration**: Expiração da chave de criptografia.

### **Repository**
A camada Repository interage com o banco de dados e fornece métodos para acessar as entidades de forma persistente.

#### `FileRepository.java`

- Utiliza a interface `JpaRepository` para realizar operações CRUD na tabela de arquivos criptografados.

### **Exception Handling**
A camada de exceções lida com erros e fornece respostas personalizadas para o cliente.

#### `ControllerAdvice.java`

- Tratamento de exceções global para erros de API.
- Exceções específicas como **GeneralApiError** e **MultipartException** são tratadas e retornadas com status apropriados.

---

## **Como Rodar o Projeto**

### **Pré-requisitos**

Antes de rodar o projeto, é necessário:

- **Java 17** ou superior.
- **Maven** instalado.
- **Banco de dados MySQL** configurado (ou outro banco relacional com ajustes de configuração).

### **Passos para Rodar**

1. Clone o repositório para sua máquina local:
    ```bash
    git clone https://github.com/seu-usuario/seu-repositorio.git
    cd seu-repositorio
    ```

2. Configure seu banco de dados no arquivo `application.properties`:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/seu_banco
    spring.datasource.username=seu_usuario
    spring.datasource.password=sua_senha
    spring.jpa.hibernate.ddl-auto=update
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    ```

3. Compile e inicie o projeto com o Maven:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

4. A API estará disponível em `http://localhost:8080`.

---

## **Endpoints da API**

### **1. Upload de Arquivo**

- **URL**: `/upload`
- **Método**: `POST`
- **Parâmetros**:
    - `file`: Arquivo a ser enviado.
    - `fileName`: Nome do arquivo.
  
**Exemplo de Requisição**:

```bash
curl -X POST "http://localhost:8080/upload" -F "file=@/caminho/do/arquivo" -F "fileName=nome_do_arquivo"
```

**Resposta**:

```json
{
  "randomIdentification": 12345,
  "encryptionKey": "chave-de-criptografia-gerada"
}
```

### **2. Download de Arquivo**

- **URL**: `/download/{randomIdentification}`
- **Método**: `GET`
- **Parâmetros**:
    - `randomIdentification`: Identificador único do arquivo.
    - `key`: Chave de criptografia.

**Exemplo de Requisição**:

```bash
curl -X GET "http://localhost:8080/download/12345?key=chave-de-criptografia"
```

**Resposta**: O conteúdo do arquivo será retornado para o cliente.

---

## **Exceções Comuns**

- **400 BAD REQUEST**: Se um parâmetro necessário (como `file` ou `fileName`) estiver ausente ou inválido.
- **422 UNPROCESSABLE ENTITY**: Se ocorrer um erro geral na API.
- **500 INTERNAL SERVER ERROR**: Para falhas no servidor.

---

## **Contribuições**

Contribuições são bem-vindas! Se você encontrar algum problema ou tiver sugestões de melhorias, fique à vontade para abrir uma *issue* ou enviar um *pull request*.
