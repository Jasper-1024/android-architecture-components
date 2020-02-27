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

package com.example.android.persistence.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import com.example.android.persistence.BasicApp;
import com.example.android.persistence.DataRepository;
import com.example.android.persistence.db.entity.ProductEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
//产品ViewModel
public class ProductListViewModel extends AndroidViewModel {
    private static final String QUERY_KEY = "QUERY";
    //保存临时数据,即使后台进程关闭数据也会得到保留.这里是保存搜索框值.
    private final SavedStateHandle mSavedStateHandler;
    //数据
    private final DataRepository mRepository;
    //livedata 产品列表
    private final LiveData<List<ProductEntity>> mProducts;

    public ProductListViewModel(@NonNull Application application,
            @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        mSavedStateHandler = savedStateHandle;
        //获取全局单例 Repository
        mRepository = ((BasicApp) application).getRepository();

        // Use the savedStateHandle.getLiveData() as the input to switchMap,
        // allowing us to recalculate what LiveData to get from the DataRepository
        // based on what query the user has entered
        //
        mProducts = Transformations.switchMap(
                //取缓存的搜索框值
                savedStateHandle.getLiveData("QUERY", null),
                (Function<CharSequence, LiveData<List<ProductEntity>>>) query -> {
                    //如果搜索框值为空
                    if (TextUtils.isEmpty(query)) {
                        //返回所有产品list
                        return mRepository.getProducts();
                    }//搜索框值不为空
                    //搜索后返回
                    return mRepository.searchProducts("*" + query + "*");
                });
    }
    //保存搜索框值
    public void setQuery(CharSequence query) {
        // Save the user's query into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandler.set(QUERY_KEY, query);
    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    public LiveData<List<ProductEntity>> getProducts() {
        return mProducts;
    }
}
