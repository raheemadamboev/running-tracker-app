package xyz.teamgravity.runningtracker.injection

import android.content.Context
import android.content.SharedPreferences
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
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(Preferences.NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideUserName(shp: SharedPreferences) = shp.getString(Preferences.USER_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideUserWeight(shp: SharedPreferences) = shp.getFloat(Preferences.USER_WEIGHT, 75F)

    @Singleton
    @Provides
    fun provideIsSetUp(shp: SharedPreferences) = shp.getBoolean(Preferences.IS_SET_UP, false)
}