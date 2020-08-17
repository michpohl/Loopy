package com.example.adapter.adapter

abstract class Sorting<T: Any>  {
    abstract fun sort(input: List<T>) : List<T>
}
