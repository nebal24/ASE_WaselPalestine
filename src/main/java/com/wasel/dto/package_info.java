package com.wasel.dto;
public class package_info {
}

/*
DTO (Data Transfer Object) is used to control what data
is received from the client and what data is returned.

We use DTOs to:
- Prevent exposing sensitive data
- Control input validation
- Keep entities separate from API models

Entities represent database tables.
DTOs represent API request and response models.*/

//---------------------------------------------------------------

/*DTO = كائن نستخدمه عشان نتحكم شو يدخل للنظام وشو يطلع منه.
هو زي "فلتر" بين:
المستخدم  ←→  النظام
طيب ليش ما نستخدم الـ Entity وخلاص؟
لأن الـ Entity تمثل جدول قاعدة البيانات
وفيها أشياء داخلية ما لازم تظهر للمستخدم.


مثال بسيط جدًا
عندك Entity اسمها User
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
}

لو رجعناها مباشرة في API
رح يرجع كمان:
password ❌
role ❌
وهذا خطر.

الحل؟
نعمل DTO.
UserResponseDTO
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
}

الآن الـ API يرجع فقط:
id
name
email
وما يرجع password 👍*/
