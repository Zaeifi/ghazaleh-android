# قوانین ProGuard — در حالت پیش‌فرض minifyEnabled خاموش است، پس این فایل فعلاً استفاده نمی‌شود.
# اگر بعداً minifyEnabled=true کردید، این خط برای درست‌کار-کردن پل جاوااسکریپت↔WebView لازم است:
-keepclassmembers class ir.ghazalehriazipsychologist.app.* {
   public *;
}
