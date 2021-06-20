package com.michaelpohl.delegationadapter

/**
 * Handle arranging or manipulation a list of items before a [DelegationAdapter] is updated with them. All subtypes of
 * [Sorting] have a [sort] method which is used by a [DelegationAdapter] automatically if a [Sorting] instance is present.
 *
 * For standard [DelegationAdapter] use [Sorting.Basic], which just takes a list of your desired [ItemType] as input,
 * and puts out a list of the same type.
 *
 * If you need to build a more complex solution, you can use [Sorting.Custom], which takes a specified [UpdateType] as input
 * and must contain the necessary logic to put out a list of your desired [ItemType]
 */
sealed class Sorting<ItemType : Any, UpdateType : Any> {

    abstract class Basic<ItemType : Any> : Sorting<ItemType, ItemType>() {
        abstract fun sort(input: List<ItemType>): List<ItemType>
    }

    abstract class Custom<ItemType : Any, UpdateType : Any> : Sorting<ItemType, UpdateType>() {
        abstract fun sort(input: UpdateType): List<ItemType>
    }
}


