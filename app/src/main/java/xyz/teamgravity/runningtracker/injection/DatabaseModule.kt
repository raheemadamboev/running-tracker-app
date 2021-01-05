package xyz.teamgravity.runningtracker.injection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import xyz.teamgravity.runningtracker.helper.constants.Preferences
import xyz.teamgravity.runningtracker.viewmodel.MyDatabase
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideMyDatabase(@ApplicationContext app: Context)
    = Room.databaseBuilder(app.applicationContext, MyDatabase::class.java, MyDatabase.DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideRunDao(db: MyDatabase) = db.runDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE)
}