
# Elektronik İmza Uygulaması

Bu Java uygulaması, dosyaları CAdES imzalama formatında ve BES seviyesinde akıllı kart ile imzalayarak çıktı üretir.

## Başlangıç

Projeyi çalıştırmak için aşağıdaki adımları takip edebilirsiniz:

1. **Gerekli Java Sürümünü Yükleyin**  
   Uygulama, Java 8 veya daha yeni sürümlerinde çalışmaktadır. Java'nın yüklü olup olmadığını aşağıdaki komutla kontrol edebilirsiniz:
   ```bash
   java -version
   ```

2. **Uygulamayı Çalıştırma**  
   Uygulamayı çalıştırmak için terminali açın ve aşağıdaki komutu kullanın:
   ```bash
   java -jar eimza.jar
   ```

3. **İmzalama İşlemi**
    - İlk olarak, imzalanmak istenen dosyanın **tam yolunu** girin.
    - Ardından, **akıllı kartın PIN** kodunu girin.
    - Uygulama, dosyayı CAdES formatında ve BES seviyesinde imzalayarak çıktı üretecektir.

## Örnek Kullanım

Terminalde aşağıdaki gibi bir işlem yapabilirsiniz:
```bash
java -jar eimza.jar
```
Ekranda şu şekilde bir çıkış alırsınız:

```
Lütfen imzalanacak dosyanın tam yolunu giriniz: /path/to/file.txt
Lütfen akıllı kartın PIN kodunu giriniz: ******
İmzalama işlemi tamamlandı. Çıktı: /path/to/signed_file.txt
```

## Bağımlılıklar

- Java 8 veya daha yeni bir sürüm gereklidir.
