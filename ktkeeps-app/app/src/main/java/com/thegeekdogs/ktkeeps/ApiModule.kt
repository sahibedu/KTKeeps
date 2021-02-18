package com.thegeekdogs.ktkeeps

object ApiModule {

    val userApiInterface = NetworkModule.retrofitInstance.create(IUserApiInterface::class.java)

    val notesApiInterface = NetworkModule.retrofitInstance.create(INotesApiInterface::class.java)
}