package com.ufkoku.mvp.utils.view_injection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ufkoku.mvp.utils.view_injection.annotation.InjectView
import com.ufkoku.mvp.utils.view_injection.annotation.Layout
import java.lang.reflect.Field
import java.util.*

object ViewInjector {

    private val cache: WeakHashMap<Class<*>, List<Field>> = WeakHashMap()

    fun checkAnnotation(target: Any): Boolean {
        return target.javaClass.isAnnotationPresent(Layout::class.java)
    }

    fun injectViews(context: Context, target: Any, cancelOnSuperClass: Class<*>? = null, parent: ViewGroup?): View {
        val layoutId = target.javaClass.getAnnotation(Layout::class.java).value
        val layout = LayoutInflater.from(context).inflate(layoutId, parent, false)

        injectViewsFrom(target, layout, cancelOnSuperClass)

        return layout
    }

    fun injectViewsFrom(target: Any, source: View, cancelOnSuperClass: Class<*>? = null) {
        target.javaClass.getAllAcceptableFields(cancelOnSuperClass)
                .forEach {
                    it.isAccessible = true
                    val annotation = it.getAnnotation(InjectView::class.java)
                    val id = annotation.value
                    it.set(target, source.findViewById(id))
                }
    }

    /**
     * Get all fields, including inherited.
     *
     * @param typeOfFields - returns fields, which are instance of this param value;
     * @param cancelOnSuperClass - stops checking hierarchy, when reaches this class. This class is not checked.
     *
     * */
    private fun Class<*>.getAllAcceptableFields(cancelOnSuperClass: Class<*>? = null): Collection<Field> {
        var cacheForClass = cache[this]
        if (cacheForClass == null) {

            cacheForClass = this.declaredFields
                    .filter { View::class.java.isAssignableFrom(it.type) && it.isAnnotationPresent(InjectView::class.java) }
                    .toMutableList()

            if (this != cancelOnSuperClass && this.superclass != null && this.superclass != cancelOnSuperClass) {
                cacheForClass.addAll(this.superclass.getAllAcceptableFields(cancelOnSuperClass))
            }

            cache[this] = cacheForClass

            return cacheForClass;
        }

        return cacheForClass
    }

}
