import java.io.FileInputStream
import java.util.Properties

plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
}

val keystoreProperties = Properties()
val keystorePropertiesFile: File = rootProject.file("keystore.properties")
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
	namespace = "boris.sumishisen"
	compileSdk = 34

	defaultConfig {
		applicationId = "boris.sumishisen"
		minSdk = 26
		targetSdk = 34
		versionCode = 3
		versionName = "1.7.1"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	signingConfigs {
		create("release") {
			storeFile = file(keystoreProperties["storeFile"] as String)
			storePassword = keystoreProperties["storePassword"] as String
			keyAlias = keystoreProperties["keyAlias"] as String
			keyPassword = keystoreProperties["keyPassword"] as String
		}
	}
	buildTypes {
		release {
			isMinifyEnabled = false
			signingConfig = signingConfigs.getByName("release")
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}
	buildFeatures {
		viewBinding = true
	}
	androidResources {
		noCompress += "txt"
	}
}

dependencies {
	implementation("androidx.activity:activity-ktx:1.8.1")
	implementation("androidx.appcompat:appcompat:1.6.1")
	implementation("androidx.constraintlayout:constraintlayout:2.1.4")
	implementation("androidx.core:core-ktx:1.12.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
	implementation("com.google.android.material:material:1.10.0")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}