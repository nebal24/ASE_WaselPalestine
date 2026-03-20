//هذا الباكج هو "باب الدخول" للمشروع
//كل ما يتعلق بـ تسجيل الدخول وإنشاء الحسابات موجود هنا.
//
//ماذا يحدث داخل الملفات؟
//        1️ RegisterRequest.java
//لما المستخدم يرسل طلب تسجيل جديد، السيرفر بيستقبل:
//
//name
//
//email
//
//password
//
//role (USER, MODERATOR, ADMIN)
//
//2️ AuthenticationController.java
//بيسلم البيانات لـ AuthenticationService
//
//3️AuthenticationService.java
//إيش بيصير هنا بالترتيب؟
//
//في عملية Register:
//يستقبل البيانات من RegisterRequest
//
//يشفر كلمة السر (ما بنحفظها نص عادي)
//
//ينشئ كائن User جديد
//
//يحفظه في قاعدة البيانات باستخدام UserRepository
//
//ينشئ JWT Token للمستخدم
//
//يرجعه في AuthenticationResponse
//
//في عملية Authenticate (تسجيل دخول):
//يستقبل email و password
//
//يتحقق إذا المستخدم موجود
//
//يتأكد إن كلمة السر صحيحة (مع التشفير)
//
//لو صح: ينشئ JWT Token ويرجعه
//
//لو غلط: يرمي خطأ (Unauthorized)
//
//4️AuthenticationResponse.java
//السيرفر بيرجع فقط:
//
//json
//{
//    "token": "eyJhbGciOiJIUzI1NiJ9..."
//}
//ما بيرجع كلمة السر أبداً!
//
//        5️ JwtService.java (موجود في security package)
//هذا اللي بيصنع التوكن:
//
//يحط فيه email المستخدم
//
//يحدد وقت انتهاء الصلاحية (24 ساعة)
//
//يوقعه بـ Secret Key عشان ما يتزور
//
// بأرقام
//         BCrypt بيشفر كلمة السر (ما حد يقدر يفكها)
//
//JWT Token بيشتغل 24 ساعة
//
// UserRepository بيحفظ المستخدم في PostgreSQL