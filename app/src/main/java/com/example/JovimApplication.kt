package com.example

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with @HiltAndroidApp.
 * This triggers Hilt's code generation and is the root component
 * that provides dependencies across the entire app.
 */
@HiltAndroidApp
class JovimApplication : Application()
