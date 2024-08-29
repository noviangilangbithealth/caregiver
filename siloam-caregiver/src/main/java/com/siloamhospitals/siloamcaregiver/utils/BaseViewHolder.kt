package com.siloamhospitals.siloamcaregiver.utils

import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

@Suppress("UNCHECKED_CAST")
class BaseViewHolder<VB>(@NonNull itemBinding: ViewBinding) : RecyclerView.ViewHolder(itemBinding.root) {
    val mViewDataBinding: VB = itemBinding as VB
}