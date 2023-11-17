# ShiftTracker-WM
Shift Tracker

Genel Bakış

Shift Tracker, kullanıcıların vardiya saatlerini takip etmelerini sağlayan bir Android uygulamasıdır. Uygulama, kullanıcının giriş ve çıkış zamanlarını otomatik olarak kaydederek, çalışma saatlerini kolayca izlemesine olanak tanır.

Özellikler

Vardiya Giriş-Çıkış Takibi: Kullanıcıların giriş ve çıkış saatlerini kaydeder.
Konum Bazlı Kontroller: Kullanıcıların belirlenen konumda olup olmadığını kontrol eder.
Firebase Entegrasyonu: Kullanıcı verilerini saklamak ve yönetmek için Firebase Firestore ve Auth kullanır.
Zaman Senkronizasyonu: NTPUDPClient ile zaman senkronizasyonu sağlar.
Arka Plan Hizmetleri: Periodik olarak konum ve vardiya durumunu kontrol eder.

Kurulum

Bu uygulamayı çalıştırmak için Android Studio'nun en son sürümüne ihtiyacınız vardır.

Projeyi GitHub'dan klonlayın:

bash
Copy code
git clone [GitHub URL]

Android Studio'da açın ve gerekli bağımlılıkları indirin.

local.properties dosyasına Firebase konfigürasyon detaylarını ekleyin.
Uygulamayı bir Android cihazda veya emülatörde çalıştırın.


Kullanılan Teknolojiler

Kotlin
Firebase Firestore, Auth, ve Storage
Android WorkManager
Google Play Services Location
