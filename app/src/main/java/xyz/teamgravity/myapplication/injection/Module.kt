package xyz.teamgravity.myapplication.injection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import xyz.teamgravity.myapplication.viewmodel.MyDatabase
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object Module {

    @Singleton
    @Provides
    fun provideMyDatabase(@ApplicationContext context: Context)
    = Room.databaseBuilder(context.applicationContext, MyDatabase::class.java, MyDatabase.DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideRunDao(db: MyDatabase) = db.runDao()
}