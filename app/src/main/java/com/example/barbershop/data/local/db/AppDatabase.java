package com.example.barbershop.data.local.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.barbershop.data.local.dao.AppointmentDao;
import com.example.barbershop.data.local.dao.BarberDao;
import com.example.barbershop.data.local.dao.BranchDao;
import com.example.barbershop.data.local.dao.CityDao;
import com.example.barbershop.data.local.dao.ServiceDao;
import com.example.barbershop.data.local.dao.UserDao;
import com.example.barbershop.data.local.entity.AppointmentEntity;
import com.example.barbershop.data.local.entity.BarberEntity;
import com.example.barbershop.data.local.entity.BranchEntity;
import com.example.barbershop.data.local.entity.CityEntity;
import com.example.barbershop.data.local.entity.ServiceEntity;
import com.example.barbershop.data.local.entity.UserEntity;

@Database(
        entities = {
                UserEntity.class,
                CityEntity.class,
                BranchEntity.class,
                ServiceEntity.class,
                BarberEntity.class,
                AppointmentEntity.class
        },
        version = 4,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract CityDao cityDao();
    public abstract BranchDao branchDao();
    public abstract ServiceDao serviceDao();
    public abstract BarberDao barberDao();
    public abstract AppointmentDao appointmentDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "barbershop_db"
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);

                                    db.execSQL("INSERT INTO cities (name) VALUES ('Москва')");
                                    db.execSQL("INSERT INTO cities (name) VALUES ('Санкт-Петербург')");
                                    db.execSQL("INSERT INTO cities (name) VALUES ('Казань')");
                                    db.execSQL("INSERT INTO cities (name) VALUES ('Новосибирск')");
                                    db.execSQL("INSERT INTO cities (name) VALUES ('Екатеринбург')");
                                    db.execSQL("INSERT INTO cities (name) VALUES ('Энгельс')");

                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (1, 'Barber House Центр', 'г. Москва, ул. Тверская, д. 18')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (1, 'Barber House Арбат', 'г. Москва, ул. Арбат, д. 27')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (1, 'Barber House Сити', 'г. Москва, Пресненская наб., д. 8')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (2, 'Barber House Невский', 'г. Санкт-Петербург, Невский пр-т, д. 56')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (2, 'Barber House Петроградка', 'г. Санкт-Петербург, Каменноостровский пр-т, д. 21')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (2, 'Barber House Московский', 'г. Санкт-Петербург, Московский пр-т, д. 102')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (3, 'Barber House Баумана', 'г. Казань, ул. Баумана, д. 44')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (3, 'Barber House Кремлёвская', 'г. Казань, ул. Кремлёвская, д. 19')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (4, 'Barber House Красный', 'г. Новосибирск, Красный пр-т, д. 77')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (4, 'Barber House Гагаринская', 'г. Новосибирск, ул. Гоголя, д. 15')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (5, 'Barber House Центр', 'г. Екатеринбург, ул. Малышева, д. 36')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (5, 'Barber House Вайнера', 'г. Екатеринбург, ул. Вайнера, д. 12')");
                                    db.execSQL("INSERT INTO branches (cityId, title, address) VALUES (6, 'Barber House Энгельс', 'г. Энгельс, ул. Тельмана, д. 23')");

                                    db.execSQL("INSERT INTO services (name, description, price, durationMinutes) VALUES ('Мужская стрижка', 'Классическая или современная стрижка', 1200, 60)");
                                    db.execSQL("INSERT INTO services (name, description, price, durationMinutes) VALUES ('Стрижка + борода', 'Комплексная услуга', 1800, 90)");
                                    db.execSQL("INSERT INTO services (name, description, price, durationMinutes) VALUES ('Оформление бороды', 'Контур, длина и уход', 900, 40)");
                                    db.execSQL("INSERT INTO services (name, description, price, durationMinutes) VALUES ('Детская стрижка', 'Для мальчиков до 12 лет', 1000, 50)");
                                    db.execSQL("INSERT INTO services (name, description, price, durationMinutes) VALUES ('Королевское бритьё', 'Бритьё опасной бритвой с уходом', 1500, 60)");

                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (1, 'Артём Волков', 'Fade / Crop', 6, 4.9, '+7 (901) 111-10-01')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (1, 'Илья Морозов', 'Классические стрижки', 4, 4.8, '+7 (901) 111-10-02')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (2, 'Никита Лебедев', 'Борода / уход', 5, 4.9, '+7 (901) 111-10-03')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (2, 'Даниил Орлов', 'Современный стиль', 3, 4.7, '+7 (901) 111-10-04')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (3, 'Максим Громов', 'Fade / beard', 7, 5.0, '+7 (901) 111-10-05')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (3, 'Егор Павлов', 'Классика', 4, 4.8, '+7 (901) 111-10-06')");

                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (4, 'Матвей Соколов', 'Стрижка + борода', 6, 4.9, '+7 (902) 222-10-01')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (4, 'Кирилл Фёдоров', 'Текстурные стрижки', 4, 4.8, '+7 (902) 222-10-02')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (5, 'Владислав Беляев', 'Классика / деловой стиль', 5, 4.9, '+7 (902) 222-10-03')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (5, 'Роман Титов', 'Борода / уход', 3, 4.7, '+7 (902) 222-10-04')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (6, 'Глеб Козлов', 'Fade', 5, 4.8, '+7 (902) 222-10-05')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (6, 'Тимур Иванов', 'Креативные стрижки', 4, 4.7, '+7 (902) 222-10-06')");

                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (7, 'Айдар Хасанов', 'Классика / Fade', 6, 4.9, '+7 (903) 333-10-01')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (7, 'Руслан Нургалиев', 'Борода', 4, 4.8, '+7 (903) 333-10-02')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (8, 'Эмиль Шарипов', 'Современный стиль', 5, 4.8, '+7 (903) 333-10-03')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (8, 'Булат Ахметов', 'Стрижка + борода', 7, 4.9, '+7 (903) 333-10-04')");

                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (9, 'Дмитрий Блинов', 'Fade', 5, 4.8, '+7 (904) 444-10-01')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (9, 'Алексей Воронов', 'Классика', 4, 4.7, '+7 (904) 444-10-02')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (10, 'Олег Седов', 'Борода / контур', 6, 4.9, '+7 (904) 444-10-03')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (10, 'Павел Демин', 'Текстурные стрижки', 3, 4.7, '+7 (904) 444-10-04')");

                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (11, 'Антон Захаров', 'Классика / бизнес-стиль', 7, 4.9, '+7 (905) 555-10-01')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (11, 'Сергей Комаров', 'Fade / Crop', 4, 4.8, '+7 (905) 555-10-02')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (12, 'Николай Чернов', 'Борода / бритьё', 5, 4.8, '+7 (905) 555-10-03')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (12, 'Игорь Смирнов', 'Современный стиль', 4, 4.7, '+7 (905) 555-10-04')");

                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (13, 'Андрей Климов', 'Классическая стрижка', 5, 4.8, '+7 (906) 666-10-01')");
                                    db.execSQL("INSERT INTO barbers (branchId, name, specialization, experienceYears, rating, phone) VALUES (13, 'Михаил Ершов', 'Борода / уход', 4, 4.7, '+7 (906) 666-10-02')");
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}