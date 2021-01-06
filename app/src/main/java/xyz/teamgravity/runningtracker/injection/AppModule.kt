package xyz.teamgravity.runningtracker.injection

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import xyz.teamgravity.runningtracker.helper.adapter.RunAdapter
import xyz.teamgravity.runningtracker.helper.constants.Preferences
import xyz.teamgravity.runningtracker.viewmodel.MyDatabase
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

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
    fun provideDateFormat() = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    @Provides
    fun provideRunAdapter(@ApplicationContext app: Context, dateFormat: SimpleDateFormat) = RunAdapter(dateFormat, app.resources)
}