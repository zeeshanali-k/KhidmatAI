# Rules — Local Database (Room)

## Setup

- Use **Room** (`androidx.room`) for local persistence. Use **KSP** for annotation processing.
- Entities and DAOs live in `:core:database`.
- The `RoomDatabase` instance is a **Koin `@Single`**.
- Export Room schemas to `$projectDir/schemas` and commit them to version control.

```kotlin
// build.gradle.kts (:core:database)
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true") // generate Kotlin sources, not Java
}
```

---

## Entities

```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarUrl: String?,
    val updatedAt: Long = System.currentTimeMillis(),
)
```

- One entity per table. No business logic in entity classes.
- Use `@TypeConverters` for non-primitive fields (e.g., `List<String>`, `Instant`). Never store serialized JSON blobs as raw strings.
- Use `@Embedded` for nested value objects that belong to the same table.
- Use `@Relation` with `@Transaction` for one-to-many queries.

---

## DAOs

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun observeUser(id: String): Flow<UserEntity?>  // ← reactive

    @Query("SELECT * FROM users")
    fun observeAll(): Flow<List<UserEntity>>

    @Upsert
    suspend fun upsert(entity: UserEntity)

    @Upsert
    suspend fun upsertAll(entities: List<UserEntity>)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}
```

- Methods that **return data** use `Flow<T>` for reactivity.
- Methods that **write data** are `suspend` functions.
- Use `@Upsert` (Room 2.5+) instead of `@Insert(onConflict = REPLACE)`.

---

## Database Class

```kotlin
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
```

---

## Koin Provision

```kotlin
@Module
class DatabaseModule {
    @Single
    fun provideDatabase(@AndroidContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
            .addMigrations(MIGRATION_1_2)  // always explicit migrations
            .build()

    @Single
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
}
```

---

## Migrations

- **Always** write explicit `Migration` objects — never use `fallbackToDestructiveMigration()` in production builds.
- Store migration scripts in `:core:database/src/main/kotlin/.../migrations/`.
- After each schema change, verify migration using `MigrationTestHelper` in a test.

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE users ADD COLUMN avatar_url TEXT")
    }
}
```

---

## Local Data Source Pattern

Repositories must not call DAOs directly — use a **local data source** as an intermediary:

```kotlin
@Single
class UserLocalDataSource(private val dao: UserDao) {
    fun observeUser(id: String): Flow<UserProfile?> =
        dao.observeUser(id).map { it?.toDomain() }

    suspend fun save(profile: UserProfile) = dao.upsert(profile.toEntity())
}
```

---

## Rules Summary

| Rule | Detail |
|---|---|
| KSP only | Never use `kapt` for Room |
| Schema export | Export and commit schemas for migration auditing |
| Explicit migrations | No `fallbackToDestructiveMigration` in production |
| `@Upsert` | Prefer over `@Insert(onConflict = REPLACE)` |
| Reactive reads | All read methods return `Flow<T>` |
| Suspend writes | All write/delete methods are `suspend` |
| Local data source | DAOs are not accessed directly from repositories |
