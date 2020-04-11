package com.kotlin.basicsample_kotlin.db

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kotlin.basicsample_kotlin.AppExecutors
import com.kotlin.basicsample_kotlin.db.converter.DateConverter
import com.kotlin.basicsample_kotlin.db.dao.CommentDao
import com.kotlin.basicsample_kotlin.db.dao.ProductDao
import com.kotlin.basicsample_kotlin.db.entity.CommentEntity
import com.kotlin.basicsample_kotlin.db.entity.ProductEntity
import com.kotlin.basicsample_kotlin.db.entity.ProductFtsEntity

@Database(
    entities = [ProductEntity::class, ProductFtsEntity::class, CommentEntity::class],
    version = 2
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun commentDao(): CommentDao
    private var mIsDatabaseCreated: MutableLiveData<Boolean> = MutableLiveData()

    companion object {
        val DATABASE_NAME = "basic-sample-db"
        private var sInstance: AppDatabase? = null

        fun getInstance(context: Context, executors: AppExecutors): AppDatabase {
            sInstance?.let {
                return sInstance as AppDatabase
            }
            synchronized(AppDatabase::class.java) {
                var tmp = buildDatabase(context.applicationContext, executors)
                tmp.updateDatabaseCreated(context)
                return tmp
            }
        }

        private fun buildDatabase(
            appContext: Context, executors: AppExecutors
        ): AppDatabase {
            return Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        executors.DiskIO().execute {
                            addDelay()
                            var database = AppDatabase.getInstance(appContext, executors)
                            var products = DataGenerator.generateProducts()
                            var comments = DataGenerator.generateCommentsForProducts(products)
                            inserData(database, products, comments)
                            // notify that the database was created and it's ready to be used 标记数据库可用
                            database.setDatabaseCreated()
                        }
                    }
                }
                ).addMigrations(MIGRATION_1_2)
                .build()
        }


        private fun inserData(
            database: AppDatabase,
            products: List<ProductEntity>,
            comments: List<CommentEntity>
        ) {
            database.productDao().insertAll(products)
            database.commentDao().insertAll(comments)
        }

        private fun addDelay() {
            Thread.sleep(4000)
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE VIRTUAL TABLE IF NOT EXISTS `productsFts` USING FTS4("
                            + "`name` TEXT, `description` TEXT, content=`products`)"
                )
                database.execSQL(
                    "INSERT INTO productsFts (`rowid`, `name`, `description`) "
                            + "SELECT `id`, `name`, `description` FROM products"
                )
            }
        }


    }

    private fun updateDatabaseCreated(context: Context) {
        if (context.getDatabasePath(DATABASE_NAME).exists())
            setDatabaseCreated()
    }

    private fun setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true)
    }

    fun getDatabaseCreated() = mIsDatabaseCreated


}
















