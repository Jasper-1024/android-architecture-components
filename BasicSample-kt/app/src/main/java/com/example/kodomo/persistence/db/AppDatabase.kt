package com.example.kodomo.persistence.db

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kodomo.persistence.AppExecutors
import com.example.kodomo.persistence.db.converter.DateConverter
import com.example.kodomo.persistence.db.dao.CommentDao
import com.example.kodomo.persistence.db.dao.ProductDao
import com.example.kodomo.persistence.db.entity.CommentEntity
import com.example.kodomo.persistence.db.entity.ProductEntity
import com.example.kodomo.persistence.db.entity.ProductFtsEntity

/**
 * @author linxiaotao
 * 2018/12/27 下午4:28
 */
@Database(entities = [ProductEntity::class, ProductFtsEntity::class, CommentEntity::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "basic-sample-db"

        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `productsFts` USING FTS4("
                        + "`name` TEXT, `description` TEXT, content=`products`)")
                database.execSQL("INSERT INTO productsFts (`rowid`, `name`, `description`) "
                        + "SELECT `id`, `name`, `description` FROM products")
            }
        }

        var instance: AppDatabase? = null

        fun getInstance(context: Context, executors: AppExecutors): AppDatabase {
            val i1 = instance
            i1?.let {
                return i1
            }
            synchronized(AppDatabase::class.java) {
                val i2 = instance
                i2?.let {
                    return i2
                }
                val created = buildDatabase(context, executors)
                created.updateDatabaseCreated(context)
                instance = created
                return created
            }

        }


        fun buildDatabase(appContext: Context, executors: AppExecutors): AppDatabase {
            return Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            executors.diskIO().execute {
                                Thread.sleep(4000)
                                val products = DataGenerator.generateProducts()
                                insertData(
                                        getInstance(appContext, executors),
                                        products,
                                        DataGenerator.generateCommentsForProducts(products)
                                )
                                getInstance(appContext, executors).setDatabaseCreated()
                            }
                        }

                    })
                    .addMigrations(MIGRATION_1_2)
                    .build()
        }

        fun insertData(database: AppDatabase, products: List<ProductEntity>, comments: List<CommentEntity>) {
            database.runInTransaction {
                database.productDao().insertAll(products)
                database.commentDao().insertAll(comments)
            }
        }

    }

    abstract fun productDao(): ProductDao

    abstract fun commentDao(): CommentDao

    fun getDatabaseCreated() = mIsDatabaseCreated

    private val mIsDatabaseCreated = MutableLiveData<Boolean>()

    private fun setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true)
    }

    private fun updateDatabaseCreated(context: Context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated()
        }
    }

}