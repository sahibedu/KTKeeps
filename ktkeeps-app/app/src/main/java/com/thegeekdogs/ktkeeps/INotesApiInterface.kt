package com.thegeekdogs.ktkeeps

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @author Sahib Singh
 * @since 19/02/21
 **/
interface INotesApiInterface {

    @POST("notes")
    suspend fun addNewNote(
        @Field("note_text") noteText: String,
        @Field("note_created_date") noteCreatedDate: String
    ) : BaseResponseModel<NoteResponseModel>

    @GET("notes")
    suspend fun getAllNotes(): BaseResponseModel<List<NoteResponseModel>>

    @DELETE("notes")
    suspend fun deleteNote(@Field("note_id") noteId: String):BaseResponseModel<Boolean>
}