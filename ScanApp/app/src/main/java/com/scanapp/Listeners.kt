package com.scanapp

class Listeners {

    interface DialogInteractionListener {
        fun dismissDialog()
        fun addDialog()
        fun addErrorDialog()
        fun addErrorDialog(msg: String?)
    }
    interface DialogAthleteInteractionListener {
        fun dismissDialog()
        fun addDialog()
        fun addLoadingDialog()
        fun dismissLoadingDialog()
        fun addErrorDialog()
        fun addErrorDialog(msg: String?)
    }
    interface NewDialogInteractionListener {
        fun dismissDialog()
        fun addDialog()
        fun addErrorDialog()
        fun addErrorDialog(msg: String?)
        fun makeListEmpty()
    }
}