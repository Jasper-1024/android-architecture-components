/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.persistence.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import com.example.android.persistence.AppExecutors;
import com.example.android.persistence.db.converter.DateConverter;
import com.example.android.persistence.db.dao.CommentDao;
import com.example.android.persistence.db.dao.ProductDao;
import com.example.android.persistence.db.entity.CommentEntity;
import com.example.android.persistence.db.entity.ProductEntity;

import com.example.android.persistence.db.entity.ProductFtsEntity;
import java.util.List;
//数据库持有者 3个表                                                                      版本2
@Database(entities = {ProductEntity.class, ProductFtsEntity.class, CommentEntity.class}, version = 2)
@TypeConverters(DateConverter.class)//转换类
public abstract class AppDatabase extends RoomDatabase {
    //数据库变量
    private static AppDatabase sInstance;

    @VisibleForTesting
    public static final String DATABASE_NAME = "basic-sample-db";
    //几个 DAO
    public abstract ProductDao productDao();

    public abstract CommentDao commentDao();
    //数据库是否创建
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {//如果数据库还未实例化
            synchronized (AppDatabase.class) {//同步锁保证一个时间内只有一个访问
                if (sInstance == null) {
                    //实例化数据库
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database. {@link Builder#build()} only sets up the database configuration and
     * creates a new instance of the database.
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext,
            final AppExecutors executors) {
        //      创建实例
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            // Add a delay to simulate a long-running operation
                            addDelay();//+4s
                            // Generate the data for pre-population 生成数据
                            AppDatabase database = AppDatabase.getInstance(appContext, executors);
                            List<ProductEntity> products = DataGenerator.generateProducts();
                            List<CommentEntity> comments =
                                    DataGenerator.generateCommentsForProducts(products);
                            //插入数据
                            insertData(database, products, comments);
                            // notify that the database was created and it's ready to be used 标记数据库可用
                            database.setDatabaseCreated();
                        });
                    }
                })
            .addMigrations(MIGRATION_1_2)
            .build();
    }

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }
    //插入数据
    private static void insertData(final AppDatabase database, final List<ProductEntity> products,
            final List<CommentEntity> comments) {
        database.runInTransaction(() -> {
            database.productDao().insertAll(products);
            database.commentDao().insertAll(comments);
        });
    }
    //延迟4s
    private static void addDelay() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ignored) {
        }
    }
    //获取数据库状态
    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
    //版本1 -> 版本2 数据库升级 保留数据.
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `productsFts` USING FTS4("
                + "`name` TEXT, `description` TEXT, content=`products`)");
            database.execSQL("INSERT INTO productsFts (`rowid`, `name`, `description`) "
                + "SELECT `id`, `name`, `description` FROM products");

        }
    };
}
