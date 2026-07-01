# KanjiLens Mobile
## Alunos:
### João Pedro Oliveira Lisboa 562410, Antonio Pedro Cayky do Nascimento Pereira 567267, Victor Emanuel Tomaz das Neves 537516, João Volney Grangeiro dos Santos 553481 

Aplicativo Android para estudo de kanji com autenticacao Firebase, OCR com ML Kit e persistencia no Firestore.

## Visao Geral

O app permite:

- criar conta e fazer login com Firebase Auth
- capturar kanji pela camera com CameraX
- reconhecer texto japones com ML Kit
- confirmar candidatos de OCR antes de salvar
- consultar detalhes de kanji via Firestore e `kanjiapi.dev`
- salvar itens na colecao do usuario
- marcar kanji como visto, comentar e remover
- alterar idioma e tema nas configuracoes

## Stack

- Kotlin
- Jetpack Compose
- Navigation Compose
- Firebase Auth
- Firebase Firestore
- ML Kit Text Recognition Japanese
- CameraX
- Retrofit + Gson
- DataStore Preferences

## Requisitos

- Android Studio recente
- JDK 11
- Android SDK configurado
- arquivo `app/google-services.json`

## Configuracao

1. Clone o repositorio.
2. Abra o projeto no Android Studio.
3. Adicione o arquivo `google-services.json` em `app/`.
4. Sincronize o Gradle.
5. Rode o app em um dispositivo ou emulador Android.

## Como Funciona

### Autenticacao

- O app usa Firebase Auth para login e cadastro.
- Os dados basicos do usuario sao salvos na colecao `usuarios` do Firestore.

### OCR e Captura

- A camera usa CameraX para capturar a imagem.
- O reconhecimento usa ML Kit com modelo japones.
- Depois da foto, o app mostra uma lista de candidatos de kanji lidos.
- O usuario escolhe o kanji correto antes de continuar.

### Dados de Kanji

- O app tenta buscar primeiro no Firestore.
- Se nao encontrar, faz fallback para `https://kanjiapi.dev/`.
- Os kanji salvos pelo usuario ficam em `usuarios/{uid}/colecao`.

### Preferencias

- Tema escuro e idioma sao persistidos localmente com DataStore.

## Estrutura

```text
app/src/main/java/com/example/kanjilens/
|- auth/
|  |- data/
|  |- domain/
|  |- presentation/
|- kanji/
|  |- data/
|  |- model/
|  |- presentation/
|- settings/
|  |- data/
|  |- presentation/
|- ui/
|  |- navigation/
|  |- theme/
|- MainActivity.kt
```

## Navegacao Principal

- `Login`
- `Register`
- `Home`
- `Camera`
- `Discovery`
- `Encyclopedia`
- `Settings`

## Arquivos Importantes

- `app/src/main/java/com/example/kanjilens/MainActivity.kt`
- `app/src/main/java/com/example/kanjilens/auth/`
- `app/src/main/java/com/example/kanjilens/kanji/presentation/ui/CameraScreen.kt`
- `app/src/main/java/com/example/kanjilens/kanji/data/remote/KanjiFirestoreRepository.kt`
- `app/src/main/java/com/example/kanjilens/settings/data/local/AppSettingsStore.kt`

## Permissoes

- `INTERNET`
- `CAMERA`


