package de.michaelpohl.loopy.common

import de.michaelpohl.loopy.ui.main.base.BaseFragment

//TODO remove this class and the reason for its existence
/**
 * This whole thing is a workaround so MainActivity doesn't lose it's
 * one important state while the screen rotates.
 * It's pretty stupid and can easily be solved differently but I needed
 * to get this out of the way quick. I'll come back to this
 */
object StateHelper{

    var currentFragment: BaseFragment? = null
}
