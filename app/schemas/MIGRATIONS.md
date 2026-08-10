# Room Database — Guide des migrations

## Procédure pour chaque changement de schéma

### 1. Modifier le modèle dans `Models.kt`
Exemple : ajouter une colonne `bio` à la table `users`.

```kotlin
@Entity(tableName = "users")
data class User(
    // ...colonnes existantes...
    val bio: String = ""  // ← nouvelle colonne
)
```

### 2. Incrémenter la version dans `AppDatabase.kt`

```kotlin
@Database(
    entities = [...],
    version = 6,  // ← était 5
    exportSchema = true
)
```

### 3. Écrire la migration dans `Migrations.kt`

```kotlin
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE users ADD COLUMN bio TEXT NOT NULL DEFAULT ''")
    }
}
```

### 4. Ajouter la migration à `ALL_MIGRATIONS`

```kotlin
val ALL_MIGRATIONS = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4,
    MIGRATION_4_5,
    MIGRATION_5_6  // ← ajouter ici
)
```

### 5. Builder → synchroniser automatiquement
`DatabaseModule` passe déjà `*ALL_MIGRATIONS` au builder Room.
Aucune modification nécessaire dans `DatabaseModule`.

### 6. Compiler
Room génère automatiquement `schemas/com.example.data.AppDatabase/6.json`.
**Committer ce fichier** — c'est la preuve permanente du schéma v6.

---

## Historique des versions

| Version | Changements |
|---------|-------------|
| 1 | Schéma initial : accounts, posts, chat_messages, brand_kits, media_items |
| 2 | Ajout colonne `isProfessional` dans `accounts` |
| 3 | Ajout tables `users` et `social_profiles` |
| 4 | Ajout table `authentication_logs` |
| 5 | Ajout colonne `adaptedContent` dans `posts`, `fileSize` dans `media_items` |

---

## Règles importantes

- **Ne jamais modifier une migration existante** une fois publiée en production.
- **Ne jamais utiliser `fallbackToDestructiveMigration`** sur une app en production — ça efface toutes les données utilisateur.
- Chaque fichier JSON dans ce dossier est versionné et doit être committé dans git.
- Si tu rates une migration en dev et que la DB est corrompue localement, désinstalle l'app et réinstalle — les données de dev ne sont pas critiques.
