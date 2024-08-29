package com.siloamhospitals.siloamcaregiver.network

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.siloamhospitals.siloamcaregiver.ext.encryption.getMd5
import com.siloamhospitals.siloamcaregiver.network.dao.CaregiverChatDao
import com.siloamhospitals.siloamcaregiver.network.entity.CaregiverChatEntity
import com.siloamhospitals.siloamcaregiver.network.entity.FailedChatEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [CaregiverChatEntity::class, FailedChatEntity::class], version = 2)
abstract class CaregiverDatabase : RoomDatabase() {

    abstract fun caregiverChatDao(): CaregiverChatDao

    companion object {
        private const val DB_NAME = "chat_database.db"

        fun getInstance(context: Context): CaregiverDatabase {
            val PHRASE = "siloam_c4r3g1v3r"

//            val passphrase = SQLiteDatabase.getBytes(PHRASE.getMd5()?.toCharArray())
//
//            if (SQLCipherUtils.getDatabaseState(
//                    context,
//                    DB_NAME
//                ) == SQLCipherUtils.State.UNENCRYPTED
//            ) {
//                SQLCipherUtils.encrypt(context, DB_NAME, passphrase)
//            }

//            val factory = SupportFactory(passphrase)

            /*
                        if (DEBUG || BUILD_TYPE == STAGING) {
                            Room.databaseBuilder(get(), CaregiverDatabase::class.java, DB_NAME)
                                .addMigrations(migrateDatabase)
                                .fallbackToDestructiveMigration()
                                .build()
                        } else {
                            Room.databaseBuilder(get(), CaregiverDatabase::class.java, DB_NAME)
                                .addMigrations(migrateDatabase)
                                .fallbackToDestructiveMigration()
                                .openHelperFactory(factory)
                                .build()
                        }
            */

//            Room.databaseBuilder(get(), CoreDatabase::class.java, DB_NAME)
//                .addMigrations(migrateDatabase)
//                .fallbackToDestructiveMigration()
//                .build()

            return Room.databaseBuilder(context, CaregiverDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
//                .openHelperFactory(factory)
                .build()
        }
    }
}