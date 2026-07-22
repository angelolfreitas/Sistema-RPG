# IEJI RPG — Backend

Backend de uma aplicação web para mestrar e jogar RPGs de investigação/terror (estilo *Ordem Paranormal*), com fichas de personagem, casos de investigação, batalhas contra monstros, inventário, chat e presença em tempo real via WebSocket.

## ✨ Funcionalidades

- **Autenticação e autorização** via JWT, com papéis hierárquicos (`ADMIN` > `MANAGER` > `USER`) e permissões granulares por recurso (`read`/`write`/`delete`).
- **Personagens (fichas)**: criação, edição, patch parcial e upload de imagem (Cloudinary).
- **Casos de investigação**: criação pelo mestre, agendamento de sessões, entrada de jogadores, listagem de participantes.
- **Combate**: monstros com aplicação de dano, controle de batalha e sincronização em tempo real via WebSocket.
- **Inventário**: por personagem, com regras de posse (dono ou mestre) delegadas ao serviço de personagens.
- **Chat em tempo real e presença online** por caso, via STOMP/WebSocket autenticado por JWT.
- **Cache com Redis** para reduzir carga no banco.
- **Recuperação de senha** por e-mail (integração com Gmail API).
- **Documentação da API** via springdoc-openapi (Swagger UI).

## 🛠️ Tecnologias

| Categoria | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Build | Gradle |
| Persistência | Spring Data JPA + PostgreSQL |
| Segurança | Spring Security + JWT (`java-jwt`) |
| Tempo real | Spring WebSocket (STOMP) |
| Cache | Spring Cache + Redis |
| Upload de imagens | Cloudinary |
| Documentação de API | springdoc-openapi (Swagger UI) |
| Validação | Spring Validation |
| Outros | Lombok, DevTools |

## 📁 Arquitetura (visão geral)

```
com.ieji.rpg
├── controller/            # Endpoints REST (extends AbstractController)
│   └── websocket/         # Controllers STOMP (chat, batalha, presença)
├── service/                # Regras de negócio (extends AbstractService)
├── domain/
│   ├── entity/              # Entidades JPA
│   └── dto/                 # Requests e Responses
└── infra/
    ├── security/            # JWT, filtros, SecurityConfig
    ├── websocket/           # Configuração STOMP + handshake JWT
    ├── redis/               # Configuração de cache
    ├── messaging/           # Executor assíncrono
    └── port/                # Integração com serviços externos (Cloudinary)
```

O projeto usa um padrão de **CRUD genérico**: `AbstractController` e `AbstractService` concentram as operações básicas (`create`, `findAll`, `getById`, `update`, `delete`, `patch`), e cada controller específico sobrescreve o que precisa (regras de autorização, validações extras, endpoints adicionais).

## 🔐 Autenticação e papéis

A API usa **JWT Bearer Token**. O token é gerado no login/registro e deve ser enviado no header:

```
Authorization: Bearer <token>
```

Papéis disponíveis (cumulativos): **ADMIN** → **MANAGER** → **USER**, com autoridades no formato `papel::ação` (ex.: `admin::write`, `user::read`).

Endpoints públicos (sem autenticação): registro, login, esqueci minha senha, reset de senha, Swagger UI e o handshake do WebSocket (`/ws/**`).

## 🚀 Como rodar o projeto

### Pré-requisitos
- Java 21
- PostgreSQL
- Redis
- Conta Cloudinary (para upload de imagens)
- Credenciais OAuth2 do Gmail (para envio de e-mails de recuperação de senha)

### Variáveis de ambiente

Crie um arquivo `.env` (ou configure as variáveis no seu ambiente/host) com:

```env
# Banco de dados
DB_URL=jdbc:postgresql://localhost:5432/ieji_rpg
DB_USERNAME=postgres
DB_PASSWORD=senha

# Segurança
SECURITY_SECRET=uma_chave_secreta_forte
ADMIN_NAME=admin@example.com
ADMIN_PASSWORD=senha_do_admin_inicial

# Cloudinary
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=

# Front-end (CORS)
FRONTEND_URL=http://localhost:5173

# E-mail (recuperação de senha via Gmail API)
GMAIL_CLIENT_ID=
GMAIL_CLIENT_SECRET=
GMAIL_REFRESH_TOKEN=
SENDER_EMAIL=

# Redis
REDIS_URL=redis://localhost:6379
```

> Um usuário **admin** é criado automaticamente na inicialização, caso ainda não exista um usuário com o e-mail definido em `ADMIN_NAME`.

### Rodando localmente

```bash
# Clonar o repositório
git clone <url-do-repositorio>
cd ieji-rpg

# Rodar com Gradle
./gradlew bootRun
```

A aplicação sobe por padrão em `http://localhost:8080`.

### Rodando com Docker

```bash
docker build -t ieji-rpg .
docker run -p 8080:8080 --env-file .env ieji-rpg
```

### Documentação interativa (Swagger)

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

## 📡 Endpoints da API

Todos os endpoints (exceto os marcados como **público**) exigem token JWT. As permissões indicadas são as mínimas necessárias.

### Autenticação — `/auth`
| Método | Rota | Permissão | Descrição |
|---|---|---|---|
| POST | `/auth/login` | público | Autentica e retorna o token |
| POST | `/auth/register` | público | Registra um novo usuário |
| POST | `/auth/admin/register` | `admin::write` | Registra usuário com papel específico |
| PATCH | `/auth/{id}/role` | `admin::write` | Altera o papel de um usuário |
| POST | `/auth/forgot-password` | público | Solicita reset de senha por e-mail |
| POST | `/auth/reset-password` | público | Efetiva a nova senha a partir do token recebido |
| PUT | `/auth` | `admin::write` | Atualiza dados de um usuário |
| DELETE | `/auth/{id}` | `manager::write` / `admin::write` | Remove um usuário |

### Personagens — `/personagem`
| Método | Rota | Permissão | Descrição |
|---|---|---|---|
| POST | `/personagem` | `user::write` | Cria personagem (dono = usuário logado) |
| GET | `/personagem` | `user::write` | Lista personagens visíveis ao usuário |
| GET | `/personagem/meu` | `user::write` | Lista os personagens do próprio usuário |
| GET | `/personagem/{id}` | `user::write` | Detalha um personagem (checa posse/acesso) |
| PUT | `/personagem` | `user::write` | Atualiza personagem (checa posse/acesso) |
| PATCH | `/personagem/{id}` | `user::write` | Atualização parcial (checa posse/acesso) |
| DELETE | `/personagem/{id}` | `user::write` | Remove personagem (checa posse/acesso) |

### Casos de investigação — `/casos`
| Método | Rota | Permissão | Descrição |
|---|---|---|---|
| POST | `/casos` | `manager::write` / `admin::write` | Cria um caso |
| PUT | `/casos` | `manager::write` / `admin::write` | Atualiza um caso |
| DELETE | `/casos/{id}` | `admin::write` | Remove um caso |
| POST | `/casos/{id}/sessoes` | `manager::write` / `admin::write` | Agenda uma sessão |
| GET | `/casos/{id}/sessoes` | `user::read` | Lista sessões agendadas |
| DELETE | `/casos/{id}/sessoes/{idSessao}` | `manager::write` / `admin::write` | Cancela uma sessão |
| POST | `/casos/{id}/entrar` | `user::write` | Usuário logado entra na sessão do caso |
| GET | `/casos/{id}/jogadores` | `user::read` | Lista jogadores do caso |
| GET | `/casos/{id}/usuarios` | `manager::write` / `admin::write` | Lista jogadores com dados completos |

### Monstros — `/monstro`
| Método | Rota | Permissão | Descrição |
|---|---|---|---|
| GET | `/monstro` | `user::write` | Lista monstros conforme regras do papel do usuário |
| GET | `/monstro/{id}` | `admin::read` | Detalha um monstro |
| POST | `/monstro` | `manager::write` | Cria um monstro |
| PUT | `/monstro` | `admin::write` | Atualiza um monstro |
| PATCH | `/monstro/{id}` | `admin::write` | Atualização parcial |
| DELETE | `/monstro/{id}` | `admin::write` | Remove um monstro |
| POST | `/monstro/{id}/dano` | `admin::write` | Aplica dano (reduz PV) |

### Inventário — `/inventario`
| Método | Rota | Permissão | Descrição |
|---|---|---|---|
| GET | `/inventario/meu` | `user::write` | Inventário do personagem logado |
| GET | `/inventario` | `manager::write` / `admin::write` | Lista todos os inventários |
| POST | `/inventario` | `user::write` | Adiciona item (checa posse do personagem) |
| PATCH | `/inventario/{idPersonagem}/{idItem}` | `user::write` | Altera quantidade de um item |
| DELETE | `/inventario/{idPersonagem}/{idItem}` | `user::write` | Remove item do inventário |

### Itens — `/item`
CRUD completo restrito a `manager::write` para escrita; leitura conforme `user::write`.

### Aetherys — `/aetherys`
CRUD com criação/edição/patch para `manager::write`/`admin::write` e exclusão restrita a `admin::write`.

### Chat — `/chat`
| Método | Rota | Permissão | Descrição |
|---|---|---|---|
| GET | `/chat/caso/{idCaso}` | `user::read` | Histórico de mensagens do caso |

### Upload — `/uploads`
| Método | Rota | Permissão | Descrição |
|---|---|---|---|
| POST | `/uploads/imagem` | `user::write` | Upload de imagem (multipart) para o Cloudinary |

### Perícia / Pergunta
Endpoints CRUD básicos existentes na API, mas ainda **não utilizados** pelas regras de negócio atuais.

## 🔌 WebSocket (STOMP)

Endpoint de conexão: `ws://localhost:8080/ws` (com fallback SockJS).

A autenticação acontece no `CONNECT`, via header `Authorization: Bearer <token>` — conexões sem token válido são recusadas.

| Destino (envio) | Descrição |
|---|---|
| `/app/caso/{casoId}/chat` | Envia mensagem de chat |
| `/app/caso/{casoId}/monstro/update` | Atualiza PV de um monstro (`admin::write`) |
| `/app/caso/{casoId}/monstro/{monstroId}/batalha` | Inicia batalha (`admin::write`) |
| `/app/caso/{casoId}/monstro/{monstroId}/encerrar-batalha` | Encerra batalha (`admin::write`) |

| Tópico (recebimento) | Descrição |
|---|---|
| `/topic/caso/{casoId}/chat` | Mensagens do chat do caso |
| `/topic/caso/{casoId}/monstros` | Atualizações de monstros |
| `/topic/caso/{casoId}/batalha` | Eventos de batalha |
| `/topic/caso/{casoId}/presenca` | Usuários online no caso |

> ⚠️ O controle de presença é mantido em memória — funciona corretamente apenas com uma única instância da aplicação. Para múltiplas réplicas, seria necessário migrar para um estado compartilhado (ex.: Redis).

## 🧪 Testes

```bash
./gradlew test
```

## 📄 Licença

Defina aqui a licença do projeto (ex.: MIT).
