package com.wasel.wasel.exception;
public class package_info {
}

/*باكيج exception مسؤولة عن معالجة الأخطاء التي تحدث داخل النظام بشكل منظم وموحد.
بدل ما كل Controller يرجع خطأ بطريقة مختلفة،
نقوم بتجميع إدارة الأخطاء في مكان واحد.

لماذا نحتاجها؟
في مشروع Wasel ممكن يحدث:
Incident غير موجود
مستخدم غير مصرح له
تقرير مكرر
Token غير صالح
فشل API خارجي

هذه الطبقة تضمن:
إرجاع HTTP Status Code صحيح (مثل 404, 401, 400)
إرجاع رسالة خطأ واضحة
توحيد شكل الـ error response
تحسين تصميم الـ API

ماذا نضع داخلها؟
1️⃣ Custom Exceptions
مثل:
ResourceNotFoundException
UnauthorizedException
DuplicateReportException

2️⃣ GlobalExceptionHandler
وهو كلاس يعالج جميع الأخطاء ويرجع Response منظم.*/
