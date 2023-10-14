package com.example.hikemate.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.hikemate.Database.Model.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert (Message message);

    @Query("SELECT * FROM message")
    List<Message> getAllMessages();

    @Query("SELECT * FROM message WHERE id =:id")
    Message getMessageById (int id);

    @Query("DELETE FROM message")
    void deleteAllMessages();

}