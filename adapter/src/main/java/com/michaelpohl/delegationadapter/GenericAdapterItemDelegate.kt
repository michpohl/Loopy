package com.michaelpohl.delegationadapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.michaelpohl.delegationadapter.AdapterItemDelegate

import kotlin.reflect.KClass

class GenericAdapterItemDelegate<Model : Any, VH : DelegationAdapterItemHolder<Model>>(
    @LayoutRes val layoutId: Int, private val modelClass: KClass<Model>, private val vhClass: KClass<VH>,
    val clickListener: ((Model) -> Unit)?) :
    AdapterItemDelegate<Model, VH>() {
    override fun createViewHolder(parent: ViewGroup): VH {
        val itemView = inflateLayout(layoutId, parent)
        return vhClass.createEntity(itemView)
    }

    override fun isForItemType(item: Any): Boolean {
        return modelClass.isInstance(item);
    }

    override fun doBinding(item: Any, holder: DelegationAdapterItemHolder<*>) {
        if (this.isForItemType(item)) {
            if (clickListener != null) {
                holder.itemView.setOnClickListener {
                        val itemToSend = holder.item ?: item
                         clickListener.invoke(itemToSend as Model) }
            }
            bindViewHolder(item as Model, holder as VH)
        } else {
            onBindViewHolderFailed(item, holder)
        }
    }

    @Suppress("SpreadOperator")
    inline fun <T : DelegationAdapterItemHolder<*>> KClass<T>.createEntity(vararg args: Any): T {
        val constructor = this.java.getDeclaredConstructor(View::class.java)
        return constructor.newInstance(*args) as T
    }
}

inline fun <reified Model : Any, reified VH : DelegationAdapterItemHolder<Model>> delegate(
    @LayoutRes layoutRes: Int): GenericAdapterItemDelegate<Model, VH> {
    return GenericAdapterItemDelegate(
        layoutRes,
        Model::class,
        VH::class,
        null)
}

inline fun <reified Model : Any, reified VH : DelegationAdapterItemHolder<Model>> clickableDelegate(
    @LayoutRes layoutRes: Int,
    noinline clickListener: ((Model) -> Unit)): GenericAdapterItemDelegate<Model, VH> {
    return GenericAdapterItemDelegate(
        layoutRes,
        Model::class, VH::class, clickListener)
}


