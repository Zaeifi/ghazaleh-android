plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ir.ghazalehriazipsychologist.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "ir.ghazalehriazipsychologist.app"
        minSdk = 24
        targetSdk = 34
        // نسخه اپلیکیشن — فقط وقتی تغییر کنید که خودِ کد اپ (نه محتوای سایت) تغییر کرده و
        // می‌خواهید نسخه جدید را در گوگل‌پلی منتشر کنید. محتوای سایت نیازی به افزایش این عدد ندارد.
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables.useSupportLibrary = true
    }

    // کلید امضای نسخه release: اگر متغیرهای محیطی زیر تنظیم شده باشند (مثلاً در GitHub Actions
    // از روی Secrets) از همان‌ها استفاده می‌شود؛ در غیر این‌صورت (مثلاً ساخت محلی روی سیستم خودتان
    // بدون این متغیرها) بیلد release بدون امضا باقی می‌ماند و فقط برای تست لوکال قابل استفاده است.
    val releaseKeystorePath = System.getenv("RELEASE_KEYSTORE_PATH")
    val releaseKeystorePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
    val releaseKeyAlias = System.getenv("RELEASE_KEY_ALIAS")
    val releaseKeyPassword = System.getenv("RELEASE_KEY_PASSWORD")
    val hasReleaseSigning = !releaseKeystorePath.isNullOrBlank() &&
        !releaseKeystorePassword.isNullOrBlank() &&
        !releaseKeyAlias.isNullOrBlank() &&
        !releaseKeyPassword.isNullOrBlank()

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseKeystorePath!!)
                storePassword = releaseKeystorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-ktx:1.9.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.webkit:webkit:1.11.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}
