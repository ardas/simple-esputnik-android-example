# simple-esputnik-android-example


https://github.com/ardas/simple-esputnik-android-example

https://github.com/ardas/simple-esputnik-android-example/blob/master/app/src/main/AndroidManifest.xml

    <uses-permission android:name="android.permission.WAKE_LOCK" />
    
            <service
            android:name=".service.ESFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
        
        
https://github.com/ardas/simple-esputnik-android-example/blob/master/app/src/main/java/ua/esputnik/support/service/ESFirebaseMessagingService.java

перекидываем обработку в Jobs пуш с кастомными данными.

    OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(EsPushJob.class)
                .setInputData(new Data.Builder().putAll(dd).build())
                .build();
        WorkManager.getInstance()
                .beginWith(work)
                .enqueue();
    
Тут показываем пуш и отправляем статус DELIVERED
https://github.com/ardas/simple-esputnik-android-example/blob/master/app/src/main/java/ua/esputnik/support/job/EsPushJob.java
