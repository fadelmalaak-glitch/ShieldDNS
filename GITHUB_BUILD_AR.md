# بناء ShieldDNS APK من GitHub — بدون Android Studio

## أول مرة فقط
1. افتح github.com وسجّل الدخول.
2. أنشئ Repository جديد باسم `ShieldDNS`، واجعله **Private** إن أردت.
3. افتح ملف ZIP الخاص بالمشروع على الكمبيوتر وفك الضغط.
4. ارفع **محتويات مجلد ShieldDNS نفسه** إلى جذر الـRepository، بحيث يظهر `app` و `.github` و `build.gradle` مباشرة في الصفحة الرئيسية.
5. بعد الرفع، افتح تبويب **Actions**.
6. اختر **Build ShieldDNS APK**.
7. اضغط **Run workflow** ثم **Run workflow**.
8. بعد اكتمال البناء وظهور العلامة الخضراء، افتح الـrun.
9. في قسم **Artifacts** اضغط `ShieldDNS-v0.1-test`.
10. سينزل ملف ZIP؛ فكّه وستجد `ShieldDNS-v0.1-test.apk` وملف SHA-256 للتحقق.

## تثبيت النسخة على OPPO
- انقل APK إلى الهاتف وافتحه.
- اسمح بالتثبيت من المصدر المستخدم عند مطالبة ColorOS.
- افتح ShieldDNS واضغط Start Protection.
- وافق على طلب Android الخاص باتصال VPN.

## ملاحظات النسخة 0.1
- هذه نسخة اختبار وليست anti-uninstall.
- تعتمد على Local VPN لتمرير DNS إلى Family Filter.
- بعض التطبيقات التي تستخدم DNS-over-HTTPS خاصًا بها قد تحتاج معالجة إضافية في النسخة الأقوى.
