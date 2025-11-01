graph TD
    subgraph "UI Layer (Fragments & Adapters)"
        A[MainActivity] --> B[LessonsFragment]
        A --> C[PlayerFragment]
        A --> D[ChatFragment]

        B --> BE[LessonAdapter]
        C --> CE[SongAdapter]
        D --> DE[ChatAdapter]
    end

    subgraph "Domain Layer (ViewModels)"
        B --> BV[LessonsViewModel]
        C --> CV[PlayerViewModel]
        D --> DV[ChatViewModel]
    end

    subgraph "Data Layer (Repository & Local)"
        BV --> BR[LessonRepository]
        CV --> CR[PlayerRepository]  %% Можно объединить с SongProvider/PlayerService
        DV --> DR[LessonRepository]
        DR --> DB[(AppDatabase)]
        BR --> DB
        DB --> DAO[LessonDao]
    end

    subgraph "Models"
        M1[Lesson]
        M2[Song]
        M3[ChatMessage]
    end

    %% Связи между слоями
    BR --> M1
    CR --> M2
    DV --> M3
    BE --> M1
    CE --> M2
    DE --> M3

    %% Внешние зависимости
    classDef uiLayer fill:#ffe4b5,stroke:#333;
    classDef domainLayer fill:#e0ffff,stroke:#333;
    classDef dataLayer fill:#fafad2,stroke:#333;
    classDef modelLayer fill:#eeeee0,stroke:#333;

    class A,B,C,D,BE,CE,DE uiLayer
    class BV,CV,DV domainLayer
    class BR,CR,DB,DAO dataLayer
    class M1,M2,M3 modelLayer
