package com.siloamhospitals.siloamcaregiver.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.siloamhospitals.siloamcaregiver.R

typealias Inflate<E> = (LayoutInflater, ViewGroup?, Boolean) -> E

@Suppress("unused")
abstract class BaseSingleAdapter<T, VDB : ViewBinding>(
    private val inflate: Inflate<VDB>,
    protected val mList: MutableList<T> = ArrayList()
) : RecyclerView.Adapter<BaseViewHolder<VDB>>() {

    lateinit var adapterContext: Context

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    var activity: AppCompatActivity? = null
    private var lastAnimatedPosition = -1

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VDB> {
        return BaseViewHolder(inflate.invoke(getLayoutInflater(parent), parent, false).apply {
                adapterContext = parent.context
            })
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VDB>, position: Int) {
        if (mList.isEmpty()) return

        holder.apply {
            try {
                val data = getData(position)

                onBindViewHolder(mViewDataBinding, data, position)
                if (animated()) setupAnimation(mViewDataBinding, position, animResources())

                launchDelayedFunction { setupListener(mViewDataBinding, data, position) }
                setupSecondaryAdapter(mViewDataBinding, data, position)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    override fun getItemCount(): Int {
        return mList.size
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    fun initialize(contents: List<T>) {
        val size = itemCount

        mList.clear()
        notifyItemRangeRemoved(0, size)

        mList.addAll(contents)
        notifyItemRangeInserted(0, itemCount)
    }

    fun setup(activity: Activity?) {
        this.activity = activity as AppCompatActivity
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    fun add(content: T, action: ((List<T>) -> Unit)? = null) {
        mList.add(content)
//        notifyItemInserted(itemCount)
        action?.invoke(mList)
    }

    fun add(index: Int, content: T) {
        mList.add(index, content)
//        notifyItemInserted(index)
    }

    fun addAll(contents: List<T>) {
        val size = itemCount
        mList.addAll(contents)
        notifyItemRangeInserted(size, itemCount)
    }

    fun remove(position: Int, action: ((List<T>) -> Unit)? = null) {
        mList.removeAt(position)
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, itemCount)

        action?.invoke(mList)
    }

    fun update(position: Int, content: T, action: ((MutableList<T>) -> Unit)? = null) {
        mList[position] = content
//        notifyItemChanged(position)
        action?.invoke(mList)
    }

    fun clear() {
        val size = itemCount
        mList.clear()
        notifyItemRangeRemoved(0, size)
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    fun getIndexOf(item: T?): Int {
        if (item == null) return 0
        return mList.indexOf(item)
    }

    private fun getData(position: Int = 0): T {
        return try {
            mList[position]
        } catch (e: Exception) {
            mList.last()
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    fun firstItem() = mList.firstOrNull()
    fun lastItem() = mList.lastOrNull()

    fun lastIndex() = mList.lastIndex
    fun isEmpty() = mList.isEmpty()
    fun isNotEmpty() = mList.isNotEmpty()

    fun getList() = mList.toList()
    fun getSize() = mList.size

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    protected abstract fun onBindViewHolder(binding: VDB, data: T, adapterPosition: Int)

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    protected open fun setupListener(binding: VDB, data: T, adapterPosition: Int) {}
    protected open fun setupSecondaryAdapter(binding: VDB, data: T, adapterPosition: Int) {}

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    private fun setupAnimation(binding: VDB, adapterPosition: Int, @AnimRes resId: Int) {
        binding.root.run {
            if (adapterPosition > lastAnimatedPosition) {
                animation = AnimationUtils.loadAnimation(adapterContext, resId)
                also { it.animation.start() }
                lastAnimatedPosition = adapterPosition
            } else {
                animation?.cancel()
                clearAnimation()
            }
        }
    }

    protected open fun animResources(): Int = androidx.transition.R.anim.fragment_open_enter
    protected open fun animated(): Boolean = false

    protected open fun pageWidth(): Double = 0.8

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    protected fun <T> startActivity(activityClass: Class<T>, bundle: Bundle? = null) {
        adapterContext.run {
            startActivity(Intent(this, activityClass).apply { bundle?.run { putExtras(this) } })
        }
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
        return LayoutInflater.from(parent.context)
    }

    fun launchDelayedFunction(timeMillis: Long = DateUtils.SECOND_IN_MILLIS, action: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({ action() }, timeMillis)
    }

}
