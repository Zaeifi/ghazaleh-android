package ir.ghazalehriazipsychologist.app

/**
 * هر آیتم منوی خانه یا نوار پایین، به یک صفحه از سایت زنده وصل می‌شود.
 * چون این‌ها فقط عنوان/آیکون/آدرس هستند (نه خودِ محتوا)، وقتی محتوای آن صفحه روی سایت
 * تغییر کند، همین‌جا هیچ تغییری لازم نیست — فقط وقتی خودِ ساختار منو عوض شود این فایل را ویرایش کنید.
 */
data class MenuItemData(
    val title: String,
    val iconRes: Int,
    val path: String // مسیر نسبی از ریشه سایت، مثل "services.php"
)

object AppMenu {
    // آیتم‌های میانبر نوار پایین صفحه
    val bottomNav = listOf(
        MenuItemData("تست‌ها", R.drawable.ic_tests, "tests.php"),
        MenuItemData("مقالات", R.drawable.ic_articles, "articles.php"),
        MenuItemData("رزرو نوبت", R.drawable.ic_booking, "booking.php"),
    )

    // شبکه آیکون‌های صفحه خانه — ترتیب و نام‌گذاری دقیقاً مطابق طرح تأییدشده
    val homeGrid = listOf(
        MenuItemData("رزرو نوبت", R.drawable.ic_booking, "booking.php"),
        MenuItemData("خدمات", R.drawable.ic_services, "services.php"),
        MenuItemData("تست‌های روان‌شناسی", R.drawable.ic_tests, "tests.php"),
        MenuItemData("گالری", R.drawable.ic_gallery, "gallery.php"),
        MenuItemData("کارگاه‌ها", R.drawable.ic_workshops, "workshops.php"),
        MenuItemData("ویدئوها", R.drawable.ic_videos, "videos.php"),
        MenuItemData("مقالات", R.drawable.ic_articles, "articles.php"),
        MenuItemData("تجربه با مراجعین", R.drawable.ic_experiences, "experiences.php"),
        MenuItemData("درباره من", R.drawable.ic_home, "index.php#about"),
        MenuItemData("تماس", R.drawable.ic_contact, "index.php#contact"),
        MenuItemData("سوالات متداول", R.drawable.ic_faq, "faq.php"),
    )
}
