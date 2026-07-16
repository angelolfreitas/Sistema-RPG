# Codex Eleonora — Frontend

Frontend do sistema de RPG (Instituto Eleonora), feito em React + Vite + Tailwind.
Contém 5 telas conectadas à sua API Java: Login, Cadastro, Ficha de Personagem, Inventário e Aetherys.

## O que você precisa instalar antes (uma vez só, no seu computador)

1. **Node.js** (versão 18 ou mais nova): baixe em https://nodejs.org — instale a versão "LTS".
   Depois de instalar, abra o terminal e confira se funcionou:
   ```
   node -v
   npm -v
   ```
   Se aparecer um número de versão em cada comando, está tudo certo.

## Passo a passo para rodar o projeto

1. Extraia o .zip deste projeto em uma pasta no seu computador.

2. Abra o terminal **dentro dessa pasta** (no Windows: clique com o botão direito na pasta > "Abrir no Terminal";
   no Mac: abra o Terminal e digite `cd ` seguido do caminho da pasta).

3. Instale as dependências do projeto (só precisa fazer isso uma vez, ou toda vez que este README mudar):
   ```
   npm install
   ```
   Isso vai baixar tudo que o projeto precisa (React, Tailwind, etc). Pode demorar um minuto.

4. Configure o endereço do seu back-end Java:
   ```
   cp .env.example .env
   ```
   Depois abra o arquivo `.env` num editor de texto e confira se a linha está assim
   (troque a porta se seu Spring Boot rodar em outra):
   ```
   VITE_API_URL=http://localhost:8080
   ```

5. Rode o projeto:
   ```
   npm run dev
   ```
   O terminal vai mostrar um endereço, algo como `http://localhost:5173`. Abra esse endereço no navegador.

6. Para o site funcionar de verdade, **seu back-end Java precisa estar rodando ao mesmo tempo** (na porta
   configurada no passo 4).

## Se der erro de CORS no navegador

Isso quer dizer que o Spring Boot está bloqueando as requisições vindas do `http://localhost:5173`.
No seu back-end, adicione uma configuração de CORS liberando essa origem, por exemplo:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*");
    }
}
```

## Estrutura do projeto

```
src/
  pages/              -> as 5 telas (Login, Register, FichaPersonagem, Inventario, Aetherys)
  components/ui/      -> botão e label reutilizados nas telas
  components/ProtectedRoute.jsx -> bloqueia acesso a páginas internas sem login
  services/api.js     -> configuração central de conexão com sua API (envia o token automaticamente)
  App.jsx             -> define as rotas (endereços) do site
  main.jsx            -> ponto de entrada do React
```

## Rotas do site

| Endereço        | Tela                          | Precisa estar logado? |
|------------------|-------------------------------|------------------------|
| `/login`         | Login                         | Não                     |
| `/register`      | Cadastro                      | Não                     |
| `/personagem`    | Ficha de Personagem            | Sim                     |
| `/inventario`    | Inventário                     | Sim                     |
| `/aetherys`      | Codex de Aetherys              | Sim                     |

## Pontos de atenção sobre a API (ajustar se necessário)

- **Login/Registro**: enviam `{ username, login, password }` para `/auth/user/login` e `/auth/user/register`,
  conforme o `LoginRequest` do seu back-end.
- **Ficha de Personagem**: busca em `GET /personagem` e usa o primeiro item da lista retornada. Se cada usuário
  puder ter mais de um personagem, essa tela precisa de um endpoint que filtre por usuário logado, ou um
  seletor de personagem.
- **Inventário**: assume que `InventarioId` tem o formato `{ personagemId, itemId }`. Se a chave composta do seu
  back-end for diferente, ajuste as chamadas em `src/pages/Inventario.jsx` (funções `handleAddToInventory` e
  `handleRemove`).
- **Aetherys**: como a API não vincula um Aetherys a um personagem, a escolha do jogador fica salva no
  navegador (`localStorage`), não no banco de dados.

## Personalizando

Todo o visual (cores, fontes, ícones) está escrito diretamente nas classes Tailwind dentro de cada arquivo em
`src/pages/`. Não existe um arquivo de "tema" separado — para mudar uma cor, procure o código hexadecimal
(ex: `#7A1230`) no arquivo da tela e troque.
