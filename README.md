# Body Recomp Tracker (Android - Kotlin/Compose)

App para recomposição corporal com déficit calórico dinâmico e registro de cargas (memória muscular).

## Stack
- Kotlin + Jetpack Compose (Material 3, Dark Mode)
- Room Database (offline-first)
- WorkManager (lembrete de proteína às 19h)
- App Widget: cronômetro de descanso (60/90s)

## Requisitos
- Android Studio Giraffe/mais recente
- JDK 17
- MinSdk 24, Target 34

## Como abrir e rodar
1. Abra o diretório no Android Studio.
2. Faça Sync do Gradle (o Android Studio gerará o Gradle Wrapper se necessário).
3. Selecione `app` e rode no emulador/dispositivo.

## Estrutura
- `app/src/main/java/com/bodyrecomptracker`
  - `feature/*`: telas Compose (Onboarding, Dashboard, Workout, History, Settings)
  - `data/db/*`: entidades, DAOs e `AppDatabase`
  - `domain/*`: cálculos (TMB/macros) e progressão de carga
  - `notifications/*`: Worker de lembrete de proteína e agendamento
  - `widget/*`: App Widget de descanso

## Testes
- `app/src/test/.../CalculatorsTest.kt` cobre TMB e metas de proteína.

## CI
- GitHub Actions: build e testes com Gradle.

## Permissões
- POST_NOTIFICATIONS (Android 13+) para notificações de lembrete/cronômetro.

## Próximos passos
- Conectar UI aos dados (CRUD refeições/treinos).
- Integração Health Connect (passos/gasto calórico ativo).
- Gráficos de progresso (peso/cargas).

