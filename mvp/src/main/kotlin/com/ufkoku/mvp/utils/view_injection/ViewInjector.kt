package com.ufkoku.mvp.utils.view_injection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ufkoku.mvp.utils.view_injection.annotation.Layout
import java.lang.reflect.Field
import java.util.*

object ViewInjector {

    private val cache: WeakHashMap<Class<*>, MutableList<Field>> = WeakHashMap()

    fun checkAnnotation(target: Any): Boolean {
        return target.javaClass.isAnnotationPresent(Layout::class.java)
    }

    fun injectViews(context: Context, target: Any, parent: ViewGroup?): View {
        val layoutId = target.javaClass.getAnnotation(Layout::class.java).layout
        val layout = LayoutInflater.from(context).inflate(layoutId, parent, false)

        injectViewsFrom(target, layout)

        return layout
    }

    fun injectViewsFrom(target: Any, source: View) {

    }

//    /**
//     * Get all fields, including inherited.
//     *
//     * @param typeOfFields - returns fields, which are instance of this param value;
//     * @param cancelOnSuperClass - stops checking hierarchy, when reaches this class. This class is not checked.
//     *
//     * */
//    private fun Class<*>.getAllAcceptableFields(cancelOnSuperClass: Class<*>? = null): Collection<Field> {
//        var cacheForClass = cache[this]
//        if (cacheForClass == null) {
//            cacheForClass = ArrayList()
//            cache[this] = cacheForClass
//        }
//
//        fields.forEach { it.isAccessible = true }
//
//
//    }

}
