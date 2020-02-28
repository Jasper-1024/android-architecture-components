package com.example.kodomo.persistence

/**
 * @author linxiaotao
 * 2018/12/27 下午4:41
 */
open class SingletonHolder<out T, in A>(creator: (A) -> T) {

    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        i?.let {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            i2?.let {
                return i2
            }
            val created = creator!!(arg)
            instance = created
            creator = null
            created
        }

    }

}