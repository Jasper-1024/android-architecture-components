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

package com.example.android.persistence.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.android.persistence.db.entity.ProductEntity;

import java.util.List;

@Dao
public interface ProductDao {
    //返回 products 所有记录
    @Query("SELECT * FROM products")
    LiveData<List<ProductEntity>> loadAllProducts();
    //插入数据 主键相同,旧数据会替换新数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProductEntity> products);
    //查询 :productId
    @Query("select * from products where id = :productId")
    LiveData<ProductEntity> loadProduct(int productId);
    //查询 :productId
    @Query("select * from products where id = :productId")
    ProductEntity loadProductSync(int productId);
    //全文搜索
    @Query("SELECT products.* FROM products JOIN productsFts ON (products.id = productsFts.rowid) "
        + "WHERE productsFts MATCH :query")
    LiveData<List<ProductEntity>> searchAllProducts(String query);
}
